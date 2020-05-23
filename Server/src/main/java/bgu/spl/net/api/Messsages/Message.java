package bgu.spl.net.api.Messsages;

public abstract class Message {
    //fields
    private final short Opcode;

    public Message(byte[] byteArr)
    {

      this.Opcode=bytesToShort(byteArr);
    }

    public Message(short opcode)
    {
        this.Opcode=opcode;
    }

    public byte[] messageToEncode() {
        return null;
    }


    protected short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public short getOpcode() {
        return Opcode;
    }

}
