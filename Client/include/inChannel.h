//
// Created by zemseris@wincs.cs.bgu.ac.il on 12/29/18.
//

#ifndef CLIENT_INCHANNEL_H
#define CLIENT_INCHANNEL_H

#include <thread>
#include "../include/connectionHandler.h"
using namespace std;


class inChannel {
private:
        ConnectionHandler &handler;
        bool *loggedIn;
        thread *sockThread;

public:
        inChannel(ConnectionHandler &ch, bool *loggedIn,thread *sockThread);
        void run();
        void shortToBytes(short num, char *bytesArr);
        vector<string> splitBySpace(string input);
        bool isLoggedIn();
};


#endif //CLIENT_INCHANNEL_H
