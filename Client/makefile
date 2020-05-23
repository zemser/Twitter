CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_filesystem -lboost_thread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/inChannel.o bin/socketChannel.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/echoClient.o  bin/inChannel.o bin/socketChannel.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/inChannel.o: src/inChannel.cpp
	g++ $(CFLAGS) -o bin/inChannel.o src/inChannel.cpp

bin/socketChannel.o: src/socketChannel.cpp
	g++ $(CFLAGS) -o bin/socketChannel.o src/socketChannel.cpp

.PHONY: clean
clean:
	rm -f bin/*
