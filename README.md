# Twitter
Social network server and client performed using a binary
communication protocol. 

A registered user will be able to follow other users and post
messages.

The implementation of the server is based on the Thread-Per-Client (TPC).

A new client will issue a Register command with the requested user name and password. A registered client can
then login using the Login command. Once the command is sent, the server will reply on
the validity of the username and password. Once a user is logged in successfully, he can
submit other commands. The register and login commands are stated in the following
section: 

Opcode Operation
1 Register request (REGISTER)
2 Login request (LOGIN)
3 Logout request (LOGOUT)
4 Follow / Unfollow request (FOLLOW)
5 Post request (POST)
6 PM request (PM)
7 User list request (USERLIST)
8 Stats request (STAT)
9 Notification (NOTIFICATION)
10 Ack (ACK)
11 Error (ERROR)
