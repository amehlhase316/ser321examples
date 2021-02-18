##### Author: Instructor team SE, ASU Polytechnic, CIDSE, SE
* Version: February 2021


##### Purpose
Each program has a short description as well as the Gradle file
* Please run `SockServer` and `SockClient` together.

SockServer initiates a simple socket server. 

The client then asks the user for input (1, 2, 0 -- no error handling) and creates a simple JSON protocol so the server knows which request the client wants

This is just a very simple example. 


##### IDEAS

1 - Add another request and make sure the client and server understands them
2 - Think about the design right now, do you like it better that the requests are handled together (in the switch) and then tehre is a response handling block (after line 59)? 
Possible alternatives:
- Have methods for each reqest/response pair and call it in the switch case and then handle creating the request and receiving of the response
- Put the response into the switch statements
3 - Think about 2 and if in these cases the response still needs a "type"

