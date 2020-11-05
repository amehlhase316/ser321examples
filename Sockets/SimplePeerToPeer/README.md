#### Purpose:
Very basic peer-2-peer for a chat. All peers can communicate with each other. 

Each peer is client and server at the same time. 
When started the peer has a serverthread in which the peer listens for potential other peers to connect.

The peer can choose to listen to other peers by setting the host:port for the peers they want to be able to send messages to them. For every one of these peers that this peer wants to listen to a thread is created and a connection established to the server (which is another peer).

Then chatting can start if everyone did this. 

Client Thread constantly listens.

ServerThread writes every registered listener (the other peers). 

### How to run it

Arguments are name and port. Start 2 to many peers each having a unique port number. 

gradle runPeer --args "Name 7000" --console=plain -q

When asked who "> Who do you want to listen to? Enter host:port"
enter in one line all the host:port combination you want to listen to, e.g.
localhost:8000 localhost:8001

You will then be listening to these two peers only. You cannot change who you listen to, you would need to start again. If you enter wrong info the program quits. I know userfriendly, feel free to change that if you like :-)