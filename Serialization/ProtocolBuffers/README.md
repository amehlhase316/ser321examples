#### Purpose:
Demonstrate simple Client and Server communication using `SocketServer` and `Socket`classes.

Here a simple protocol is defined which uses protobuf. The client reads in a json file and then creates a protobuf object from it to send it to the server. The server reads it and sends back the calculated result. 

The response is also a protobuf but only with a result string. 

To see the proto file see: src/main/proto which is the default location for proto files. 

Gradle is already setup to compile the proto files. 

### How to run it (optional)
The proto file can be compiled using

gradle generateProto

This will also be done when building the project. 

You should see the compiled proto file in Java under build/generated/source/proto/main/java/buffers

You should see the compiled proto file in Python under build/generated/source/proto/main/python/buffers

Now you can run the client and server 

#### Default 
Server is Java
Per default on 9099
runServer

You have two Clients one Python and one Java both using the Protobuf protocol and both can communicate with the server

Both clients run per default on 
host localhost, port 9099 and fil edata.json -- all these can be changed
Run Java:
	runClient
Run Python:
	runClient

#### With parameters:
Java
gradle runClient -Pport=9099 -Phost='localhost' -Pfile='data.json'

Python
gradle runClientPython -Pport=9099 -Phost='localhost' -Pfile='data.json'
