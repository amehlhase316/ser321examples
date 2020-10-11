##### Author: Tim Lindquist (Tim.Lindquist@asu.edu), ASU Polytechnic, CIDSE, SE
Version: April 2020

##### Purpose
Demonstrate Json-RPC with TCP/IP Java server and manually created server
skeleton and client proxy.
The server and terminal client are executable on both Mac OS X and Debian Linux.

Communication between the service is done using JSON-RPC. Communication between
the client and server is accomplished using TCP/IP sockets in which the protocol
is for the client to send a valid jsonrpc request for one of the methods implemented
by the server. The server reads from the sockets input stream, unmarshals the request,
calls the appropriate method, marshals the result (boolean, Student, String, or String[])
and sends the jsonrpc response back to the client via it output stream.
The purpose of the example is to demonstrate JSON and JSON-RPC via direct TCP/IP sockets.
Other examples in the course demonstrate using frameworks to implement jsonrpc clients and
servers where communication occurs via http.
Use the following sources for background on these technologies:

JSON (JavaScript Object Notation):
 http://en.wikipedia.org/wiki/JSON
 The JSON web site: http://json.org/

JSON-RPC (JSON Remote Procedure Call):
 http://www.jsonrpc.org
 http://en.wikipedia.org/wiki/JSON-RPC

Any text on socket programming with Java.

Building and running the server and terminal clients is done with Gradle.

run server with:
gradle JsonRPCviaTCPStudent:runServer --args '9099'

run client with:
gradle JsonRPCviaTCPStudent:runClient --args 'localhost 9099'
