package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    //fields
    private String username;
    private String password;
    private boolean logedIn;
    private AtomicInteger numOfPosts;
    private AtomicInteger conncetionID;
    private List<User> following;
    private List<User> followers;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.logedIn=false;
        this.numOfPosts=new AtomicInteger(0);
        following=new LinkedList<>();
        followers=new LinkedList<>();

    }

    public List<User> getFollowing() {
        return following;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void addToFollowers(User user) {
        followers.add(user);
    }

    public void removeFromFollwers(User user){
        followers.remove(user);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogedIn() {
        return logedIn;
    }

    public void setLogedIn(boolean logedIn) {
        this.logedIn = logedIn;
    }

    public AtomicInteger getNumOfPosts() {
        return numOfPosts;
    }

    /**
     * checks if the given user is already following userToAdd, if so return false
     * else adds him to the following list, and adds the user to the userToAdd followers,return true
     * @param userToAdd
     * @return
     */
    public boolean follow(User userToAdd){
        if(!following.contains(userToAdd)){
            following.add(userToAdd);
            userToAdd.addToFollowers(this);// add followers
            return true;
        }
        else
            return false;
    }

    /**
     * checks if the user is following userToAdd, if so removes him from the following list and from the followers of userToAdd,returns true
     * else returns false
     * @param userToAdd
     * @return
     */
    public boolean unfollow(User userToAdd){
        if(following.contains(userToAdd)){
            following.remove(userToAdd);
            userToAdd.removeFromFollwers(this);
            return true;
        }
        return false;
    }

    public void setConncetionID(AtomicInteger conncetionID) {
        this.conncetionID=conncetionID;
    }

    public int getConncetionID() {
        return conncetionID.intValue();
    }

    public void increaseNumOfPosts(){
        this.numOfPosts.incrementAndGet();
    }
}
