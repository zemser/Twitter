//
// Created by zemseris@wincs.cs.bgu.ac.il on 12/29/18.
//

#include "../include/inChannel.h"

inChannel::inChannel(ConnectionHandler &ch, bool *loggedIn, thread *sockThread):handler(ch),loggedIn(loggedIn),sockThread(sockThread) {}
/**
 * gets input from the keyboard:
 * first checks the first word, and gives it the right opcode
 * then according to it sends the encoded message
 */
void inChannel::run() {
    vector<string> substrings;
    //while (!handler.getFinish()) {
    while (1){
        char opcodeArray[2];
        std::string line = "";
        std::istringstream iss(line);
        getline(std::cin, line);
        substrings = splitBySpace(line);
        if (substrings[0] == "REGISTER") {
            short opcode = 1;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
            handler.sendFrameAscii(substrings[1], '\0');
            handler.sendFrameAscii(substrings[2], '\0');
        }

        if (substrings[0] == "LOGIN") {
            short opcode = 2;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
            handler.sendFrameAscii(substrings[1], '\0');
            handler.sendFrameAscii(substrings[2], '\0');
        }

        if (substrings[0] == "LOGOUT") {
            short opcode = 3;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);

            if(isLoggedIn()){
                sockThread->join();//wait for the socket thread to receive "ack 3"
                break;
            }
        }

        if (substrings[0] == "FOLLOW") {
            short opcode = 4;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
            char follow[]={1};
            if(substrings[1]=="0"){ //if it is a follow message
                 follow[0]={0};
            }

            handler.sendBytes(follow ,1);
            char numOfUsersArray[2];
            shortToBytes(stoi(substrings[2]),numOfUsersArray);
            handler.sendBytes(numOfUsersArray, 2);
            for (unsigned int i = 3; i < substrings.size(); ++i) {
                handler.sendFrameAscii(substrings[i], '\0');
            }
        }

        if (substrings[0] == "POST") {
            short opcode = 5;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
            string join;//for all the content
            for (unsigned int i = 1; i < substrings.size() - 1; ++i) {
                join += substrings[i] + " ";
            }
            join += substrings[substrings.size() - 1];
            handler.sendFrameAscii(join.c_str(), '\0');
        }

        if (substrings[0] == "PM") {
            short opcode = 6;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
            handler.sendFrameAscii(substrings[1], '\0');
            string join; //for all the content
            for (unsigned int i = 1; i < substrings.size() - 1; ++i)  //add all the spaces back to the content to send them together
                join += substrings[i] + " ";
            join += substrings[substrings.size() - 1];
            handler.sendFrameAscii(join.c_str(), '\0');
        }

        if (substrings[0] == "USERLIST") {
            short opcode = 7;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
        }

        if (substrings[0] == "STAT") {
            short opcode = 8;
            shortToBytes(opcode, opcodeArray);
            handler.sendBytes(opcodeArray, 2);
            handler.sendFrameAscii(substrings[1], '\0');
        }
        substrings.clear();
    }
}




void inChannel::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

/**
 * splits a given string by spaces and returns a vector which contains all the separated words
 * @param input
 * @return
 */
vector<string> inChannel::splitBySpace(string input) {
    istringstream iss(input);
    vector<string> substrings;
    string tmp;
    while ( getline( iss, tmp , ' ' ) ) {
        substrings.push_back(tmp);
    }
    return substrings;
}

bool inChannel::isLoggedIn() {
    return *loggedIn;
}