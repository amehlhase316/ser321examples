##### Purpose
This example shows how you can use gRPC to have a client and server communiate using Protobuf as a protocol. 
You have a given Python server/client and Java server/client. All of them can communicate with each other. 

#### Java
You do not need to install anything things will run through the gradle file.

- gradle runServerJava
- gradle runClientJava

- `host`, `port` and `message` are optional arguments for the program.

#### PYTHON Install Dependencies
These need to be installed
###### (use of virtualenv recommended for `pip` installs)
  1. install protoc
  2. pip install protobuf
  3. pip install grpcio
  4. pip install grpcio-tools

##### To compile grpc and protocol buffers for Gradle [from the `Sockets` directory]:
- gradle generatProto
- gradle pythonProto

This will generate the py files for proto and grpc, sorry for the two separate calls.

#### Python (Install the dependencies before running these)
- gradle runServerPython
- gradle runClientPython

- `host`, `port` and `message` are optional arguments for the program.e
