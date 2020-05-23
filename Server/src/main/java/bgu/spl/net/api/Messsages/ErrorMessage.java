package bgu.spl.net.api.Messsages;

public class ErrorMessage extends Message {
    //fields
    private short messageOpcode;

    public ErrorMessage(short MessageOpcode) {
        super(new Integer(11).shortValue());
        this.messageOpcode=MessageOpcode;
    }

    /**
     * turn the opcode to byte array, the message opcode and then join them to one byte array
     * @return byte array which represents the message
     */
    public byte[] messageToEncode() {
        byte[]tmpOpcode=shortToBytes(getOpcode());
        byte[]tmpMessageOpcode=shortToBytes(messageOpcode);
        byte[] toSend={tmpOpcode[0], tmpOpcode[1], tmpMessageOpcode[0], tmpMessageOpcode[1]};
        return toSend;
    }

   // public String toString(){return  getOpcode()+""+messageOpcode;}

}
