#### Purpose:

##### GroupServer & GroupClient

A threaded server providing download service for the Serialized Group
The server waits for clients to connect and request to download the
file admin.ser (must execute: ant execute, prior to running server).
The clients requests the file by sending a "filetoclient^" string to
the server. The server responds by reading admin.ser and then sending the
client a message indicating how many bytes to expect in the transmission of
the file. The client responds OK, and the server sends the file's bytes.
after the client receives the proper number of bytes, it responds with a 
message OK. For longer binary files, this same interaction/protocol could
be used where the client and server agree on a pre-specified buffer size
for sending successive parts of the file.


##### GroupFileSerializer
Exports(serializes) multiple Java objects to a file.

##### socket.IO.read
The socket read for an unknown expected size and will block until 
the first packet is received.
