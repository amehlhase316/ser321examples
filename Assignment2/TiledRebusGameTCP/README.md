# TCP GAME SERVER

This project is about creating a custom communication protocol and using it to transmit pertinent data between a gamer server <br />
and a game client which may or may not be co-located.

To run this program, navigate to the root directory (same directory as this README) and enter two commands from different terminal windows:

> 1. In the first window: **gradle runServer -Pport=\<some arbitrary int\>**
>2. In the second window: **gradle runClient -Pport=\<same port as the first window\> -Phost=\<Server IP\>**

SEE SEPARATE FILE TITLED "TCP_UML"

The Custom Protocol I have built here has some standard metadata inside the header. For example, there is a format field - "json", <br />
a base field - "16" as well as an operation field which lets the client and server know what is happening. Lastly, there is a payload <br />
which contains a serialized image as well as other game state information.

My program is designed to "fail early". That is, it will fail immediately if given improperly formatted startup arguments <br />
but once it is running, it handles what it is capable of handling (i.e. IllegalArgumentExceptions from the user). Other exceptions <br />
external to the application are simply thrown higher until a shutdown sequence is reached.

a) A description of your project and a detailed description of what it does <br />
b) An explanation of how we can run the program <br />
c) A UML diagram showing the back and forth between client and server <br />
d) A description of your protocol header and payload similar to what you usually see <br />
when a protocol is described. e) Explain how you designed your program to be robust (see later under constraints)

[Github Link](https://github.com/cekraus1/ser321examples/tree/master)

