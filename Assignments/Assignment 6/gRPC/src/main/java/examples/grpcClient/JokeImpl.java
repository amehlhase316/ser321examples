package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;


// Implement the joke service. It has two sevices getJokes and setJoke
class JokeImpl extends JokeGrpc.JokeImplBase {
    
    // having a global set of jokes
    Stack<String> jokes = new Stack<String>();
    
    public JokeImpl(){
        super();
        // copying some dad jokes
        jokes.add("How do you get a squirrel to like you? Act like a nut.");
        jokes.add("I don't trust stairs. They're always up to something.");
        jokes.add("What do you call someone with no body and no nose? Nobody knows.");
        jokes.add("Did you hear the rumor about butter? Well, I'm not going to spread it!");
        
    }
    
    // We are reading how many jokes the clients wants and put them in a list to send back to client
    @Override
    public void getJoke(JokeReq req, StreamObserver<JokeRes> responseObserver) {
        
        System.out.println("Received from client: " + req.getNumber());
        JokeRes.Builder response = JokeRes.newBuilder();
        for (int i=0; i < req.getNumber(); i++){
            if(!jokes.empty()) {
                response.addJoke(jokes.pop()); // yes, I take the joke out when it was used already, should probably be done differently since this way a joke cannot be told twice even to different clients
            }
            else {
                response.addJoke("I am out of jokes..."); // this is more of a hack, better would be to either check the number at the beginnig and say right away if you do not have enough. Or send an error code or similar as well. 
                break;
            }
        }
        JokeRes resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    
    // We take the joke the user wants to set and put it in our set of jokes
    @Override
    public void setJoke(JokeSetReq req, StreamObserver<JokeSetRes> responseObserver) {
        
        System.out.println("Received from client: " + req.getJoke());
        jokes.add(req.getJoke());
        JokeSetRes.Builder response = JokeSetRes.newBuilder();
        response.setOk(true);
        
        JokeSetRes resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}