package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messsages.*;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.User;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    //fields
    private Connections connection;
    private int connectionId;
    private DataBase dataBase;
    private User user;

    public BidiMessagingProtocolImpl(DataBase dataBase) {
        this.dataBase=dataBase;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connection=connections;
        this.connectionId=connectionId;
    }

    /**
     * Recieves a message and according to the opcode of the message handles the process
     * @param message
     */
    @Override
    public void process(Message message) {
        Short opcode=message.getOpcode();
        switch (opcode.intValue()){
            case 1: registerProcess((RegisterMessage)message);
                    break;
            case 2: loginProcess((LoginMessage) message);
                    break;
            case 3: logoutProcess((LogoutMessage)message);
                    break;
            case 4: followUnFollowMessageProcess((FollowUnfollowMessage)message);
                    break;
            case 5: postProcess((PostMessage)message);
                    break;
            case 6: pmProcess((PrivateMessage)message);
                    break;
            case 7: userListProcess((UserlistMessage)message);
                    break;
            case 8: statProcess((StatMessage)message);
                    break;
        }

    }

    /**
     * check if a given user is already registered in the database, if so sends an error message.
     * else adds the user to the database and returns an Ack Message
     * @param msg
     */
    private void registerProcess(RegisterMessage msg) {
        if(user==null) {
            synchronized (dataBase.getLockerRegister()) {
                if (!dataBase.isRegisterd(msg.getUsername())) { //checks if registered
                    dataBase.registerUser(msg.getUsername(), msg.getPassword());
                    ConcurrentLinkedQueue<Short> shortQueue = new ConcurrentLinkedQueue<>();
                    shortQueue.add(msg.getOpcode());
                    connection.send(connectionId, new AckMessage(shortQueue, null));
                } else {
                    connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
                }
            }
        }
        else
            connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
    }

    /**
     * check if the given user is not registered to the database or is already logged in, if so sends an error.
     * else connects the user to the data base by changin the user status in the data base to logged in and also saving a refernce to the user in the protocol
     * @param msg
     */
    private void loginProcess(LoginMessage msg){
        User tmpUser=dataBase.tryLogIn(msg.getUsername(),msg.getPassword());
        boolean hasWaitingMessages=false;
        synchronized (dataBase.getLockerLogin()) {
            if (tmpUser == null || this.user != null)
                connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
            else {
                this.user = tmpUser;
                user.setConncetionID(new AtomicInteger(connectionId));
                user.setLogedIn(true);
                ConcurrentLinkedQueue<Short> shortQueue = new ConcurrentLinkedQueue<>();
                shortQueue.add(msg.getOpcode());
                connection.send(connectionId, new AckMessage(shortQueue, null));
                if(dataBase.getWaitingMessages(this.user).size()>0)
                    hasWaitingMessages=true;

            }
        }
        //if there are waiting messages send notifications
        if(hasWaitingMessages) {
            LinkedList<Message> waitingMessages = dataBase.getWaitingMessages(this.user);
            for (Message m : waitingMessages) {
                if(m instanceof PostMessage) {
                    PostMessage postMessage = (PostMessage) m;
                    connection.send(connectionId, new NotificationMessage('1', postMessage.getSendingUser(), postMessage.getContent()));
                }
                else
                if(m instanceof PrivateMessage) {
                    PrivateMessage privateMessage = (PrivateMessage) m;
                    connection.send(connectionId, new NotificationMessage('0', privateMessage.getSendingUser(), privateMessage.getContent()));
                }
            }
            waitingMessages.clear();
        }
    }

    /**
     * check if the user is connected, if so change the status of the user in the data base to logged of and also the user in the protocol to null
     * sends an Ack message to the client to log off and also disconnects the client from the connections.
     * @param msg
     */
    private void logoutProcess(LogoutMessage msg){
        if (user != null) {
            synchronized (user) {
                if (user != null) {
                    user.setConncetionID(null);
                    user.setLogedIn(false);
                    user = null;
                }
            }
            ConcurrentLinkedQueue<Short> shortQueue=new ConcurrentLinkedQueue<>();
            shortQueue.add(msg.getOpcode());
            connection.send(connectionId, new AckMessage(shortQueue, null));
            connection.disconnect(connectionId);
        }
        else
            connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
    }

    /**
     * checks if the user is logged in, if so checks if it is an follow or unfollow and according to that add/removes users
     * from the user's followers and following list
     * if no users were followed/unfolloed returns an error message else return a ack message
     * @param msg
     */
    private void followUnFollowMessageProcess(FollowUnfollowMessage msg){
        List<User> tmpUserLIst=new LinkedList<>();
        if(user!=null) { //true if the user is logged in
           if (msg.isFollow()) {
                for (String s : msg.getUserNameList()) {
                    User tmpUser = dataBase.getUserByUsername(s);
                    if (tmpUser != null) {  //check if the user is registered in the data base
                        if (user.follow(tmpUser))
                            tmpUserLIst.add(tmpUser);
                    }
                }
            }
            else{
                for(String s:msg.getUserNameList()) {
                    User tmpUser = dataBase.getUserByUsername(s);
                    if (tmpUser != null) {  //check if the user is registered in the data base
                        if (user.unfollow(tmpUser) && !tmpUserLIst.contains(tmpUser))
                            tmpUserLIst.add(tmpUser);
                    }
                }
            }
        }
        //create a list of shorts of mgs opcode and num of users
        ConcurrentLinkedQueue<Short> shortQueue=new ConcurrentLinkedQueue<>();
        shortQueue.add(msg.getOpcode());
        shortQueue.add((short)tmpUserLIst.size());
        if(tmpUserLIst.size()>0)
            connection.send(connectionId, new AckMessage(shortQueue, ToStringtmpUserLIst(tmpUserLIst)));
        else
          connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
    }


    /**
     * sends a noitfication to the followers of the users or the users that were tagged in the post
     * if the user is logged out the notification will be added to his waiting messages list
     * @param msg
     */
    private void postProcess(PostMessage msg){
        if(user!=null){
            user.increaseNumOfPosts();
            List<User> followingUsers=user.getFollowers();
            for (User followingUser:followingUsers) {
                synchronized (followingUser) {//sync so the followingUser won't logout while he is receiving a message
                    if (followingUser.isLogedIn())
                        connection.send(followingUser.getConncetionID(), new NotificationMessage('1', user.getUsername(), msg.getContent()));

                    else {
                        msg.setSendingUser(user.getUsername());
                        dataBase.addWaitingMessage(followingUser, msg);
                    }
                }

            }
            LinkedList<String> listOfHashTags=SetList(msg.usersFromContent()); //returns a list without repetitions of the tagged users
            for (String userName:listOfHashTags) {
                User hashtagUser=dataBase.getUserByUsername(userName);
                if(hashtagUser!=null &&!followingUsers.contains(hashtagUser)){
                   synchronized (hashtagUser) {//sync so the hashtagUser won't logout while he is receiving a message
                       if (hashtagUser.isLogedIn())
                           connection.send(hashtagUser.getConncetionID(), new NotificationMessage('1', user.getUsername(), msg.getContent()));
                       else {
                           msg.setSendingUser(user.getUsername());
                           dataBase.addWaitingMessage(hashtagUser, msg);
                       }
                   }
                }
            }
            ConcurrentLinkedQueue<Short> shortQueue=new ConcurrentLinkedQueue<>();
            shortQueue.add(msg.getOpcode());
            connection.send(connectionId, new AckMessage(shortQueue, null));
        }
        else
            connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
    }

    /**
     * checks if both the user that should receive the private message and the sending user are logged in
     * if so sends the receiving user a notification
     * else send back an error
     * @param msg
     */
    private void pmProcess(PrivateMessage msg){
        if(user!=null){
            User recipientUser=dataBase.getUserByUsername(msg.getUsername());
            synchronized (recipientUser) { //sync so the recipientUser won't logout while he is receiving a message
                if (recipientUser == null)
                    connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
                else {
                    if(!recipientUser.isLogedIn()){
                        msg.setSendingUser(user.getUsername());
                        dataBase.addWaitingMessage(recipientUser, msg);
                    }
                    else
                        connection.send(recipientUser.getConncetionID(), new NotificationMessage('0', user.getUsername(), msg.getContent()));
                }
                ConcurrentLinkedQueue<Short> shortQueue = new ConcurrentLinkedQueue<>();
                shortQueue.add(msg.getOpcode());
                connection.send(connectionId, new AckMessage(shortQueue, null));
            }
        }
        else
            connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
    }

    /**
     * checks if the client is logged in, if so sends the user list, else sends an error
     * @param msg
     */
    private void  userListProcess(UserlistMessage msg){
        if(user!=null) {
            ConcurrentLinkedQueue<Short> shortQueue=new ConcurrentLinkedQueue<>();
            shortQueue.add(msg.getOpcode());
            shortQueue.add(dataBase.getRegistrationListSize());
            connection.send(connectionId, new AckMessage(shortQueue, dataBase.getRegistrationOrderList()));
        }
        else
            connection.send(connectionId, new ErrorMessage(msg.getOpcode()));

    }

    /**
     * check if the client is logged in and if the user that requested is registered,\
     * if so return a message with the number of posts, number of following and number of followers
     * else return an error
     * @param msg
     */
    private void statProcess(StatMessage msg){
        if(user!=null){
            User statUser=dataBase.getUserByUsername(msg.getUsername());
            if(statUser==null)
                connection.send(connectionId, new ErrorMessage(msg.getOpcode()));
            else {
                connection.send(connectionId, new AckMessage(ToShortStatMessage(statUser,msg.getOpcode()),null));
            }
        }
        else
            connection.send(connectionId, new ErrorMessage(msg.getOpcode()));

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }


    /**
     * for the followUnfollow process message:
     * receives a list of users and returns a string of their usernames seperated by '\0'
     * @param list
     * @return
     */
    private String ToStringtmpUserLIst(List<User> list){
        String s="";
        for (User u:list) {
            s+=u.getUsername()+"\0";
        }
        return s;
    }

    /**
     * for the stat process message:
     * given a user, creates a list of opcodes for the creation of the ack message in the stat process
     * @param statUser
     * @param opcode
     * @return
     */
    private ConcurrentLinkedQueue ToShortStatMessage(User statUser,short opcode){
        ConcurrentLinkedQueue<Short> tmp=new ConcurrentLinkedQueue();
        tmp.add(opcode);
        tmp.add(statUser.getNumOfPosts().shortValue());
        tmp.add((short)statUser.getFollowers().size());
        tmp.add((short)statUser.getFollowing().size());
        return tmp;
    }



    /**
     * given a list, creates a list without repetitions
     * @param list
     * @return
     */
    public LinkedList<String> SetList(LinkedList<String> list){
        LinkedList<String> setList=new LinkedList<>();
        for (String s:list) {
            if(!setList.contains(s))
                setList.add(s);
        }
    return setList;
    }

}
