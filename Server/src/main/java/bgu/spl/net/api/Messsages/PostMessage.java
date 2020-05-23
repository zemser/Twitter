package bgu.spl.net.api.Messsages;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class PostMessage extends Message {
    //fields
    private String sendingUser;
    private String content;
    public PostMessage(byte[] byteArr) {
        super(new Integer(5).shortValue());
        this.content= new String(byteArr, 2, byteArr.length-2, StandardCharsets.UTF_8);
    }

    public String getContent() {
        return content;
    }

    public LinkedList<String> usersFromContent(){
        LinkedList<String> userNames=new LinkedList<>();
        String[] splited = content.split(" ");
        for (String s:splited) {
            if(s.charAt(0)=='@' && s.length()>1)
                userNames.add(s.substring(1));
        }
        return userNames;
    }

    public String getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(String sendingUser) {
        this.sendingUser = sendingUser;
    }


}
