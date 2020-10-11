Author: Tim Lindquist (Tim.Lindquist@asu.edu), ASU Polytechnic, CIDSE, SE
Version: April 2020

See http://pooh.poly.asu.edu/Ser321

Purpose: This program is to demonstrate the use of Java sockets using Object Streams
and threading on the server side.

Building and running the program can be done with:
ant build

run the server with:
java -cp classes ser321.sockets.ThreadedSockServer 9999

then run clients in separate terminals with:
java -cp classes ser321.sockets.ThreadedSockClient localhost 9999 -q --console=plain
-q => run in quiet mode
--console=plain => does not show execution mode

The client is command line. At the prompt, enter a number 0 to 4 or the word end.
The input is communicated to the server. For a number 0-4, the server will return
a message. When the client enters end, the connection is closed and the client completes.

The server's thread continues as long as the client sends it a number. A separate thread
is created for each client connection, each responsible for server their own client.
Note that the array of strings is a shared object among the clients and we do nothing
special to synchronize access to that shared data (no thread modifies it).
