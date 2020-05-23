package bgu.spl.net.api.Messsages;

import java.nio.charset.StandardCharsets;

public class PrivateMessage extends Message {
    //fields
    private String username;
    private String content;
    private String sendingUser;

    public PrivateMessage(byte[] byteArr, int index) {
        super(new Integer(6).shortValue());
        username = new String(byteArr, 2, index-2, StandardCharsets.UTF_8);
        content = new String(byteArr, index+1, byteArr.length-index-1, StandardCharsets.UTF_8);

    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(String sendingUser) {
        this.sendingUser = sendingUser;
    }
}
