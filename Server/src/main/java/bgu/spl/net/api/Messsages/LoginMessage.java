package bgu.spl.net.api.Messsages;

import java.nio.charset.StandardCharsets;

public class LoginMessage extends Message {
    //fields
    private String username;
    private String password;

    public LoginMessage(byte[]byteArr, int index) {
        super(new Integer(2).shortValue());
        username = new String(byteArr, 2, index-2, StandardCharsets.UTF_8);
        password = new String(byteArr, index+1, byteArr.length-index-1, StandardCharsets.UTF_8);

    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
