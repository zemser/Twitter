package bgu.spl.net.api;
import bgu.spl.net.api.Messsages.*;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    //fields
    List<Byte> bytes=new LinkedList<>();
    private int counter=0;
    private Short Opcode=null;
    private boolean followMessage=false;
    private Short numOfUsers=-1;
    private int sepreatingIndex=-1;  //signals where to separate the array for the register and login

    @Override
    public Message decodeNextByte(byte nextByte) {
        if(Opcode==null && counter>0 && bytes.size()==1){ //returns true when the second byte after '\0' is received
            bytes.add(nextByte);
            byte[] arr=listToArray(bytes);
            Opcode=bytesToShort(arr);
            bytes.remove(1);
        }
        if(nextByte=='\0') //counter for the '\0' bytes
            counter++;

        if(Opcode!=null) {

            switch (Opcode.intValue()) {
                // register message
                case 1:
                    if(counter==2 && sepreatingIndex==-1) //true after the second '\0' byte is received which is the end of username
                        sepreatingIndex=bytes.size();
                    if (counter == 3) {         //true after the third '\0' byte is received - if true create the register message
                        byte[] arr = listToArray(bytes);
                        closeFields();
                        int tmpSepreatingIndex=sepreatingIndex;
                        this.sepreatingIndex=-1;
                        return new RegisterMessage(arr,tmpSepreatingIndex);
                    }
                    break;
                    //login message
                case 2:
                    if(counter==2 && sepreatingIndex==-1) //true after the second '\0' byte is received which is the end of username
                        sepreatingIndex=bytes.size();
                    if (counter == 3) {          //true after the third '\0' byte is received - if true create the login message
                        byte[] arr = listToArray(bytes);
                        closeFields();
                        int tmpSepreatingIndex=sepreatingIndex;
                        this.sepreatingIndex=-1;
                        return new LoginMessage(arr,tmpSepreatingIndex);
                    }
                    break;
                    //logout message
                case 3:
                    closeFields();
                    return new LogoutMessage();

                //follow and unfollow messgae
                case 4:
                    if (bytes.size() == 4) { //checks the number of users
                        byte[] tmpArr={bytes.get(3),nextByte};
                        numOfUsers=bytesToShort(tmpArr);
                        counter=0; //changes the counter to 0 so now it will count the number of 0 between each user
                    }
                    if (numOfUsers!=-1 && counter==numOfUsers.intValue()){ //if we read the numOfUsers /0 we are finished
                        bytes.add(nextByte);
                        byte[] arr = listToArray(bytes);
                        closeFields();
                        followMessage=false;
                        int tempNumOfUsers=numOfUsers.intValue();
                        numOfUsers=-1;
                        return new FollowUnfollowMessage(arr,tempNumOfUsers);
                    }
                    break;
                    //post message
                case 5:
                    if (counter == 2) {    //true after the second '\0' byte is received - if true create the post message
                        byte[] arr = listToArray(bytes);
                        closeFields();
                        return new PostMessage(arr);
                    }
                    break;
                    //pm message
                case 6:
                    if(counter==2 && sepreatingIndex==-1)  //true after the second '\0' byte is received which is the end of username
                        sepreatingIndex=bytes.size();
                    if (counter == 3) {  //true after the third '\0' byte is received - if true create the pm message
                        byte[] arr = listToArray(bytes);
                        closeFields();
                        int tmpSepreatingIndex=sepreatingIndex;
                        this.sepreatingIndex=-1;
                        return new PrivateMessage(arr, tmpSepreatingIndex );
                    }
                    break;
                    //user list message
                case 7:
                    closeFields();
                    return new UserlistMessage();
                    //stat message
                case 8:
                    if (counter == 2) {
                        byte[] arr = listToArray(bytes);
                        closeFields();
                        return new StatMessage(arr);
                    }
                    break;
            }
        }
        bytes.add(nextByte);
        return null;


    }

    @Override
    public byte[] encode(Message message) {
        return message.messageToEncode();
    }

    private short bytesToShort(byte[] byteArr){
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

    private byte[] listToArray(List<Byte> list){
        byte[] byteArray=new byte[list.size()];
        int i=0;
        for (Byte b:list) {
          byteArray[i]=b;
          i++;
        }
        return  byteArray;
    }

    //for the decoder functions, after finishing reading a message restore the fields to default
    private void closeFields(){
        bytes.clear();
        counter = 0;
        Opcode=null;
    }
}
