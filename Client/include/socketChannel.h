//
// Created by zemseris@wincs.cs.bgu.ac.il on 12/29/18.
//

#ifndef CLIENT_SOCKETCHANNEL_H
#define CLIENT_SOCKETCHANNEL_H

#include "../include/connectionHandler.h"
using namespace std;

class socketChannel {
private:
    ConnectionHandler &handler;
    bool *loggedIn;

public:
    socketChannel(ConnectionHandler &ch, bool *loggedIn);
    void run();
    short bytesToShort(char *bytesArr);
    string notification();
    string ack();
    string twoBytesToString();
    void login();
};


#endif //CLIENT_SOCKETCHANNEL_H
