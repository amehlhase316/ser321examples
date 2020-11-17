#### Purpose:
Demonstrate simple Client and Server communication using `SocketServer` and `Socket` classes.
The server maintains a state of each client using a `HashMap`.

There are not threads in this server, thus evertime the client wants something it will connect again. The client will thus break of the connection when sendint the data. 

The client connects to the server, the client needs to send their unique client id. When the clientID is not known yet the new client id will be saved in a hashmap. If the clientID is known then the server will pull the data from that client. In this case the data is just an int. 

If 't' is chosen as input then the value given by the client will be added to the old total that the server has from that client. 'r' will reset this total. 

### The inputs for the gradle file 

The client accepts 4 inputs:
1) the host
2) the port
3) 'r' or 't': 'r' is for resetting the counter on the Server that is kept for that client, 't' is for adding the 4th input to the already counted totals
4) client id
5) number which will be added to the total

The server 
1) the port
2) sleep for thread

### A task to practice 
Keep the connection open on the client side and allow use input when started. EG. the r,t and a value. The client can keep adding or resetting as much as they want without you having to restart the program. 

Other clients can connect as long as another one does not keep it busy though. If it is busy the client will get an error message telling them to come back later. 

#### Suggestions:
- Have a client run in a while loop with asking for user input at the start
- When a user inputs something then connect to the server send data, receive data, close the connection -- now another client can theoretically send their data and will also open, send, receive and close 


This approach on a single thread is not very robust, since it means that when another client is occupying the server the server will not be able to react and the client will throw an error which you should catch. Thus multi threaded is much better in a multi client environment. 