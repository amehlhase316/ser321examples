#### Purpose:
Demonstrate simple Client and Server communication using `SocketServer` and `Socket`classes.

Here a simple protocol is defined which is in json form. You can see 4 different input files already given with this code. 

We define a header where the operation, base and response format is defined.
The payload will define the numbers for the operations. As response you can choose json or string. 

{"header":
  {
    "operation": "add",
    "base": "2",
    "response": "json"
  },
  "payload": {
    "num1": "10",
    "num2": "11"
  }
}

### How to run it

#### Default 
Per default on localhost, sleep delay of 1000
runServer

Per default on localhost, port 8888 and data.org -- all these can be changed
runClient

#### With parameters:

gradle runServer --args 9000 2000
gradle runServer -Pport=9000
gradle runClient --args 'localhost 9000 data10.json'
gradle runClient -Phost=localhost -Pport=9000 -Pfile=data10.json
