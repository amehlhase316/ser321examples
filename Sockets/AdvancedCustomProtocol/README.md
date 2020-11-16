# Example Code for client asking for different data
These examples show a TCP and the same code with UDP. 

Created by David Clements so all kudos go to him!

## Description

In the example we are actually converting all the data to a byte[] and not just sending over the String and letting Java do the rest

Client connects to server. Client can send num 1-5 over to client
1 - server will send a joke
2 - server will send a quote
3 - server will send an image
4 - server will send either of the above

For more details see code and/or video

# TCP

## Running the example

`gradle TCPServer`

`gradle TCPClient`


### Simple protocol

Client only sends what they want, could consider adding more information like a clientID or optional data. 

```
{ 
	"selected": <int: 1=joke, 2=quote, 3=image, 4=random>
}
```
   
Server sends the data type of "data" and if it is a joke, quote or image and also of course the data response: 
   
```
{
   "datatype": <int: 1-string, 2-byte array>, 
   "type": <"joke", "quote", "image">,
   "data": <thing to return> 
}
```
   
Server sends error if something goes wrong

```
{
	"error": <error string> 
}
```
   
   
## Issues in the code that were not included on purpose
The code is basically to show you how you can use a TCP connection to send over different data and interpret it on either side. It focuses on this alone and not on error handling and some nicer features.
It is suggested that you play with this and try to include some of the below for your own practice. 

- Not very robust, e.g. user enters String
- Second client can connect to socket but will not be informed that there is already a connection from other client thus the server will not response
	- More than one thread can solve this
	- can consider that client always connects with each new request
		- drawback if server is working with client A then client B still cannot connect, not very robust
- Protocol is very simple no header and payload, here we just used data and type to simplify things
- Error handling is very basic and not complete
- Always send the same joke, quote and picture. Having more of each and randomly selecting with also making sure to not duplicate things would improve things



# UDP

The main differences can be seen in NetworkUtils.java. In there the sending and reading of messages happen. For UDP the max buffer length is assumed to be 1024 bytes. So if the package is bigger it is split up into multiple packages. Ever package holds the information about the following data
     *   totalPackets(4-byte int),  -- number of total packages
     *   currentPacket#(4-byte int),  -- number of current package
     *   payloadLength(4-byte int), -- length of the payload for this package
     *   payload(byte[]) -- payload

Client and server are very similar to the TCP example just the connection of course is UDP instead of TCP. The UDP version has the same issues as the TCP example and that is again on purpose. 

