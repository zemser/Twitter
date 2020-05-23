package bgu.spl.net.api.Messsages;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class FollowUnfollowMessage extends Message {
    //fields
    private boolean follow;
    private int numOfUsers;
    private List<String> userNameList;

    //constructor
    public FollowUnfollowMessage(byte[] byteArr, int numOfUsers) {
        super(byteArr);
        userNameList=new LinkedList<>();
        Byte b=new Byte(byteArr[2]);
        if(b.intValue()==0){
            follow=true;
        }
        else
            follow=false;
        this.numOfUsers=numOfUsers;
        int curr0=5;
        int next0=-1;
        for(int i=5;i<byteArr.length;i++){
            b=new Byte(byteArr[i]);
            if(b.intValue()==0){
                next0=i;
                String username = new String(byteArr, curr0, next0-curr0, StandardCharsets.UTF_8);
                userNameList.add(username);
                curr0=next0+1;
            }
        }
    }

    public boolean isFollow() {
        return follow;
    }

    public List<String> getUserNameList() {
        return userNameList;
    }
}
