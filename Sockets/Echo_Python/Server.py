#!/usr/bin/env python3

import socket
import sys

HOST = 'localhost'  # Standard loopback interface address (localhost)
PORT = 65432        # Port to listen on (non-privileged ports are > 1023)

if __name__ == "__main__":
    if len(sys.argv) != 3:
            raise ValueError("Expected arguments: <host(String)> <port(int)>")
    print(sys.argv)
    _, host, port = sys.argv  # host, port

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((host, int(port)))
    s.listen()
    while True: # can accept a new connection
        conn, addr = s.accept() # blocking wait
        with conn:
            print('Connected by', addr)
            while True:
                data = conn.recv(1024)
                if not data:
                    break
                print('Received from client: ', data)
                conn.sendall(data)
