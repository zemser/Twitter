package bgu.spl.net.srv;
import bgu.spl.net.api.Messsages.Message;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {
    //fields
    private ConcurrentHashMap<User, LinkedList<Message>>  waitingMessageListByUser;
    private ConcurrentHashMap<String,User >  userByUserName;
    private ConcurrentLinkedQueue<User> registrationOrderList;
    private Object lockerRegister; //for synchronization of register process
    private Object lockerLogin;   //for synchronization of login process

    public DataBase() {
        this.waitingMessageListByUser=new ConcurrentHashMap<>();
        this.userByUserName=new ConcurrentHashMap<>();
        this.registrationOrderList=new ConcurrentLinkedQueue<>();
        this.lockerRegister=new Object();
        this.lockerLogin=new Object();
    }

    public void addWaitingMessage(User user, Message msg){
        waitingMessageListByUser.get(user).add(msg);
    }

    public boolean isRegisterd(String username) {
            return userByUserName.containsKey(username);
    }

    /**
     * create a user with the given username and password and add him into the database
     * @param username
     * @param password
     */
    public void registerUser(String username, String password){
        User user=new User(username, password);
        waitingMessageListByUser.put(user, new LinkedList<>());
        userByUserName.put(username,user);
        registrationOrderList.add(user);
    }

    /**
     * if a user is registered with the given username and the password matches the correct password
     * check if the the user is not already logged in and if so return the user from the userByUserName hash map
     * else return null
     * @param username
     * @param password
     * @return
     */
    public User tryLogIn(String username,String password) {
        if (userByUserName.containsKey(username)) {
            if (userByUserName.get(username).getPassword().equals(password) && userByUserName.get(username).isLogedIn()==false)
                return userByUserName.get(username);
        }
        return null;
    }

    //returns a user by the username, if no such user exists returns null
    public User getUserByUsername(String username){
        return userByUserName.get(username);
    }


    /**
     * translates the list of users to a string
     * @return
     * a string which begins with the number of users ans then all the users seperated by 0
     */
    public String getRegistrationOrderList(){
        String list="";
        for (User u:registrationOrderList) {
            list+=u.getUsername()+"\0";
        }
        return list;
    }

    public short getRegistrationListSize(){
        return(short)registrationOrderList.size();
    }

    public LinkedList<Message>getWaitingMessages(User user) {
        return waitingMessageListByUser.get(user);
    }

    public Object getLockerRegister() {
        return lockerRegister;
    }

    public Object getLockerLogin() {
        return lockerLogin;
    }
}
