package bgu.spl.net.api.Messsages;

public class NotificationMessage extends Message {
    //fields
    private char pmOrPublic;
    private String postingUser;
    private String content;
    public NotificationMessage(char c, String postingUser, String content){
        super(new Integer(9).shortValue());
        this.pmOrPublic=c;
        this.postingUser=postingUser;
        this.content=content;
    }

    /**
     * turn the short of the opcode to a byte array, turn the char of pmOrPublic to a byte array
     * turn the content and posting user strings to a byte array
     * joins all the array to create a joined array
     * @return byte array that represents the message
     */
    public byte[] messageToEncode(){
        byte[]tmpOpcode=shortToBytes(getOpcode());
        byte[] tmpPmOrPublic=new byte[1];
        if(pmOrPublic=='1')
            tmpPmOrPublic[0]=(byte)1;
        else
            tmpPmOrPublic[0]=(byte)0;
        String stringOfPostInfo=""+postingUser+"\0"+content+"\0";
        byte[]postInfo=stringOfPostInfo.getBytes();
        int sizeOfEncodedArray=3+postInfo.length;
        byte[]encodedArray=new byte[sizeOfEncodedArray];
        encodedArray[0]=tmpOpcode[0];
        encodedArray[1]=tmpOpcode[1];
        encodedArray[2]=tmpPmOrPublic[0];
        for(int i=3; i<encodedArray.length; i++)
            encodedArray[i]=postInfo[i-3];
        return encodedArray;

    }

    /*
    public byte[] messageToEncode(){
        byte[]tmpOpcode=shortToBytes(getOpcode());
        byte[] tmpPmOrPublic;
        if(pmOrPublic=='1')
           tmpPmOrPublic = shortToBytes((short) 1);
        else
            tmpPmOrPublic = shortToBytes((short) 0);
        String stringOfPostInfo=""+postingUser+"\0"+content+"\0";
        byte[]postInfo=stringOfPostInfo.getBytes();
        int sizeOfEncodedArray=4+postInfo.length;
        byte[]encodedArray=new byte[sizeOfEncodedArray];
        encodedArray[0]=tmpOpcode[0];
        encodedArray[1]=tmpOpcode[1];
        encodedArray[2]=tmpPmOrPublic[0];
        encodedArray[3]=tmpPmOrPublic[1];
        for(int i=4; i<encodedArray.length; i++)
            encodedArray[i]=postInfo[i-4];

        return encodedArray;
    }
*/
}