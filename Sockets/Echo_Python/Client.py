#!/usr/bin/env python3

import socket
import sys

HOST = 'localhost'  # The server's hostname or IP address
PORT = 9099        # The port used by the server


if __name__ == "__main__":
	if len(sys.argv) != 3:
	        raise ValueError("Expected arguments: <host(String)> <port(int)>")
	print(sys.argv)
	_, host, port = sys.argv  # host, port

	s = socket.socket()
	s.connect((host, int(port)))
	val = input("Your input: ")
	s.sendall(bytes(val, 'utf-8'))
	data = s.recv(1024)

	print('Received', repr(data))
