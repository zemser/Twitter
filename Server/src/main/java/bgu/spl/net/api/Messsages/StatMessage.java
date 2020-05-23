package bgu.spl.net.api.Messsages;

import java.nio.charset.StandardCharsets;

public class StatMessage extends Message {
    //fields
    private String username;

    public StatMessage(byte[] byteArr) {
        super(new Integer(8).shortValue());
        username = new String(byteArr, 2, byteArr.length-2, StandardCharsets.UTF_8);
    }

    public String getUsername() {
        return username;
    }
}
