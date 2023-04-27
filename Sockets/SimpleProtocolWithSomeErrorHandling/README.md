##### Author: Instructor team SE, ASU Polytechnic, CIDSE, SE
* Version: September 2020


##### Purpose
This program shows a very simple client server implementation. The server
has 3 services, echo, add, addmany. Basic error handling on the server side
is implemented. Client does not have error handling and only has hard coded
calls to the server.

* Please run `gradle Socket` and `gradle Client` together.
* Program runs on localhost
* Port is hard coded

## Protocol: ##

### Echo: ###

Request: 
    {
        "type" : "echo", -- type of request
        "data" : <String>  -- String to be echoed 
    }

    General response:
    {
        "type" : "echo", -- echoes the initial response
        "ok" : <bool> -- true or false depending
        "message" : <String>  -- error message if ok false
        "result" : <String>  -- Echoed String if ok true
    }

    Success response:
    {
        "type" : "echo",
        "ok" : true
        "result" : <String> -- the echoed string
    }

### Add: ### 
    Request:
    {
        "type" : "add",
        "num1" : <int>, -- first number
        "num1" : <int> -- second number
    }

    General response
    {
        "type" : "add", -- echoes the initial request
        "ok" : <bool> -- true of false
        "message" : <String>  -- error message if ok false
        "result" : <int>  -- result if ok true
    }

    Success response:
    {
        "type" : "add",
        "ok" : true
        "result" : <int> -- the result of add
    }

### AddMany: ###
Another request, this one does not just get two numbers but gets an array of numbers.

Request:

    {
    "type" : "addmany",
    "nums" : [<int>], -- json array of ints
    }

    General response
    {
        "type" : "addmany", -- echoes the initial request
        "ok" : <bool> -- true of false
        "message" : <String>  -- error message if ok false
        "result" : <int>  -- result if ok true
    }

    Success response:
    {
        "type" : "addmany",
        "ok" : true
        "result" : <int> -- the result of add
    }

    Error response:
    {
        "type" : "addmany",
        "ok" : false
        "message" : "Values in array need to be ints"
    }


### General error responses: ###
These are used for both requests: 

    Error response: When a needed field is not in request
    {
        "ok" : false
        "message" : "Field  + key +  does not exist in request"
    }

    Error response: When a required field is not of correct type
    {
        "ok" : false
        "message" : "Field  + key +  needs to be of type:  + type"
    }

    Error response: When the "type" is not supported, so an unsupported request
    {
        "ok" : false
        "message" : "Type  + req.getString("type") +  not supported."
    }