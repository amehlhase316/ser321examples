# UDP GAME SERVER

This project is about creating a custom communication protocol and using it to transmit pertinent data between a gamer server <br />
and a game client which may or may not be co-located. There are a few structural differences between this and the TCP module. <br />
> 1. The concept of UDP is more of a "Chat Window" concept where no specific state is known by the server. The server simply <br />
     waits for a message to come in, then it processes that message accordingly and responds. The TCP connection, on the other <br />
     hand continually passes messages back and forth which assists in maintaining a clear system state picture at all times. <br />
     There are also no guarantees with data integrity with UDP whereas TCP ensures that the data was transmitted intact via a <br />
     checksum.

To run this program, navigate to the root directory (same directory as this README) and enter two commands from different terminal windows:

> 1. In the first window: **gradle runServer -Pport=\<some arbitrary int\>**
>2. In the second window: **gradle runClient -Pport=\<same port as the first window\> -Phost=\<Server IP\>**

SEE SEPARATE FILE TITLED "UDP_UML"

The Custom Protocol I have built here has some standard metadata inside the header. For example, there is a format field - "json", <br />
a base field - "16" as well as an operation field which lets the client and server know what is happening. Lastly, there is a payload <br />
which contains a serialized image as well as other game state information.

My program is designed to "fail early". That is, it will fail immediately if given improperly formatted startup arguments <br />
but once it is running, it handles what it is capable of handling (i.e. IllegalArgumentExceptions from the user). Other exceptions <br />
external to the application are simply thrown higher until a shutdown sequence is reached.

[Github Link](https://github.com/cekraus1/ser321examples/tree/master)
