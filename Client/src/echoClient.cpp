#include <stdlib.h>
#include <iostream>
#include <mutex>
#include <thread>
#include "../include/connectionHandler.h"
#include "../include/inChannel.h"
#include "../include/socketChannel.h"

using namespace std;
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/


int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }



    bool *login=new bool(false);
    socketChannel socketchannel(connectionHandler,login);
    thread thread2(&socketChannel::run, &socketchannel);

    inChannel inchannel(connectionHandler,login,&thread2);

    thread thread1(&inChannel::run, &inchannel);
    //thread thread2(&socketChannel::run, &socketchannel);

    //thread1.join();
    thread1.join();
    delete (login);
    return 0;
}

