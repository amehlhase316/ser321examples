## Purpose:
Very basic peer-2-peer for a chat. All peers can communicate with each other. 

Each peer is client and server at the same time. 
When started the peer has a ServerThread in which the peer listens for potential other peers to connect.

You want to first start the leader who is the one in charge of the network

### Running the leader
This will start the leader on a default port and use localhost
	gradle runPeer -PisLeader=true -q --console=plain

If you want to change the leader settings
	gradle runPeer -PpeerName=Hans -Ppeer="localhost:8080" -Pleader="localhost:8080" -PisLeader=true -q --console=plain

You can of course replace localhost with the IP of your AWS, Pi etc. 

### Running a Pawn

So just a peer who is not the leader, minimal with the "default" leader from above
	gradle runPeer -PpeerName=Anna -Ppeer="localhost:9000" -q --console=plain

If you want to change settings
	gradle runPeer -PpeerName=Elsa -Ppeer="localhost:9000" -Pleader="localhost:8080" -q --console=plain

- isLeader is default false so you do not need to set it
- leader: needs to be the same in ALL started peers no matter if leader or pawn

You can start as many pawns (non leaders) as you like they should all connect. 

Watch the video for some more details about the code. 
This code is a basic code that does not include a lot of error handling yet and might need adjustments depending on how you implement your leader election. You can change this code any way you like. 
Some things that it does not do:
- check inputs from Gradle (would be good to include that)
- most requests to the server are not acknowledged, e.g. when a message is send we just send it and the server will never respond to us that they actually go it

