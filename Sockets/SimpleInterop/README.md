##### Author: Tim Lindquist (Tim.Lindquist@asu.edu), ASU Polytechnic, CIDSE, SE
 * Version: March 2020

##### Purpose
This project shows how to communicate between programs of different
languages using Stream-Based Socket connections. Byte arrays are written/read
on each side of the protocol with the application usually handling what must be
done to provide conversion of the data (object) to/from a byte array. A very
common approach in stream-based socket programming. The application is a simple
echo server with clients. The Echo Server is constructed so that a server thread
is created and allocated to each client. This example has clients written in C++,
and Java. The server and a third client are written in Java.

Running the Example

Building and running the server and terminal clients is done with Gradle.

1. To run the Java Server
  1.1 From the 'Examples/Sockets', run: gradle SimpleInterop:Java:runServer

2. To run the Java Client
  2.1 From the 'Examples/Sockets', run: gradle SimpleInterop:Java:runClient

3. To run the Cpp Client
  3.1 From the 'Examples/Sockets', run: gradle SimpleInterop:Cpp:build
  3.2 Go into the 'SimpleInterop/Cpp/build/install/main/debug', then run, ./Cpp localhost "hello from C++" 9088
