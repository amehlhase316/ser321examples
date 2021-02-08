##### Author: Instructor team SE, ASU Polytechnic, CIDSE, SE
* Version: Februray 2021

##### Purpose
Each program has a short description as well as the Gradle file
* Please run `DatagramReceive` and `DatagramSend` together.
* Please run `SockServer` and `SockClient` together.

##### SockServer and SockClient

SockServer initiates a simple socket server that awaits a String and Integer object from the client. The server will loop infinitely until the exit command is received, at which point it will drop the current client connection and await a new connection.
