//
// Created by zemseris@wincs.cs.bgu.ac.il on 12/29/18.
//

#include "../include/socketChannel.h"
//socketChannel::socketChannel(ConnectionHandler &ch, mutex& mutex):handler(ch), _mutex(mutex) {}

socketChannel::socketChannel(ConnectionHandler &ch, bool *loggedIn): handler(ch), loggedIn(loggedIn){}

void socketChannel::run() {
    //while(!handler.getFinish()) {
    while (1){
        string output;
        char opcodeArray[2];
        handler.getBytes(opcodeArray, 2);
        short opcode = bytesToShort(opcodeArray);
        if (opcode == 9) {
            output = notification();
        }
        if (opcode == 10) {
            output = ack();
        }
        if (opcode == 11) {
            output += "ERROR ";
            output += twoBytesToString();
        }

        cout<<output<<endl;

        if(output=="ACK 3") {
            handler.close();
            break;
        }

        if(output=="ACK 2") {
            login();
        }
    }

}
string socketChannel::notification() {

    string output;
    output+="NOTIFICATION ";
    char privateOrPublic[1];
    handler.getBytes(privateOrPublic,1); //public or private check
    char checkPmOrPublic[2];
    checkPmOrPublic[0]=0;
    checkPmOrPublic[1]=privateOrPublic[0];
    short pmOrPublic=bytesToShort(checkPmOrPublic);
    if(pmOrPublic==1)
        output+="Public ";
    else
        output+="PM ";
    string username;
    handler.getFrameAscii(username,'\0'); //username
    output+=username.substr(0,username.length()-1)+" ";
    string content;
    handler.getFrameAscii(content,'\0'); // content
    output+=content.substr(0,content.length()-1);;
    return output;

}

string socketChannel::ack() {
    string output;
    output=output+"ACK ";
    char messageOpcodeArray[2];
    handler.getBytes(messageOpcodeArray,2);
    short messageOpcode=bytesToShort(messageOpcodeArray);
    int tmp=messageOpcode;
    if(tmp==1){
        output+="1";
        return output;
    }

    if(tmp==2){
        output+="2";
        return output;
    }

    if(tmp==3){
        output+="3";
        return output;
    }

    if(tmp==4){
        output+="4 ";
        char numOfUsersArray[2];
        handler.getBytes(numOfUsersArray,2);
        short numOfUsers=bytesToShort(numOfUsersArray);
        int intNumOfUsers=numOfUsers;
        output+=to_string(intNumOfUsers)+" ";
        for (int i = 0; i < intNumOfUsers; ++i) {
            string content="";
            handler.getFrameAscii(content,'\0');
            output+=content.substr(0,content.length()-1)+" ";
        }
        return output;
    }

    if(tmp==5){
        output+="5";
        return output;
    }

    if(tmp==6){
        output+="6";
        return output;
    }

    if(tmp==7){
        output+="7 ";
        char numOfUsersArray[2];
        handler.getBytes(numOfUsersArray,2);
        short numOfUsers=bytesToShort(numOfUsersArray);
        int intNumOfUsers=numOfUsers;
        output+=to_string(intNumOfUsers)+" ";
        for (int i = 0; i < intNumOfUsers; ++i) {
            string content;
            handler.getFrameAscii(content,'\0');
            output+=content.substr(0,content.length()-1)+" ";
        }
        return output;
    }

    if(tmp==8){
        output+="8 ";
        for (int i = 0; i < 3; ++i)
            output+=twoBytesToString();
        return output;
    }
    return output;
}

//reads two bytes and transforms them to a string,returns it
string socketChannel::twoBytesToString() {
    string output;
    char byteArray[2];
    handler.getBytes(byteArray,2);
    short num=bytesToShort(byteArray);
    int intNum=num;
    output+=to_string(intNum)+" ";
    return output;
}

short socketChannel::bytesToShort(char *bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}



void socketChannel::login() {
    *loggedIn=true;
}

