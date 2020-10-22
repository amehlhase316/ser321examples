#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#define RCVBUFSIZE 32
static const int MAXPENDING = 5;

void exit_client(char* msg) {
	perror(msg);
	exit(1);
}

void handle_client_socket(int clntSocket);

int main(int argc, char *argv[]) {

    // 1) Fetch server parameters (server port).
	if (argc != 2) {
		exit_client("Parameter(s): <Server Port>");
    }

	in_port_t server_port = atoi(argv[1]);

	// 2) Create a socket using TCP.
	int server_sock;
	if ((server_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
		exit_client("socket(): failed creation");
    }

	//3) Construct the server address struct
	struct sockaddr_in server_addr;
	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	server_addr.sin_port = htons(server_port);

    // 4) Bind to the local address
	if (bind(server_sock, (struct sockaddr*) &server_addr, sizeof(server_addr)) < 0) {
       exit_client("bind() failed");
    }

	// 5) Mark the socket so it will listen for incoming connections
	if (listen(server_sock, MAXPENDING) < 0) {
	    exit_client("listen() failed");
    }


	for (;;) {
      struct sockaddr_in client_addr;
      socklen_t client_addr_len = sizeof(client_addr);

      // 6) Wait for a client to connect
      int client_sock = accept(server_sock, (struct sockaddr *)&client_addr, &client_addr_len);
      if (client_sock < 0) {
          exit_client("accept() failed");
      }

      // 7) Handle client connection.
      char client_name[INET_ADDRSTRLEN];
      if (inet_ntop(AF_INET,
            &client_addr.sin_addr.s_addr,
            client_name, sizeof(client_name)) != NULL) {
          printf("Handling client %s/%d\n", client_name, ntohs(client_addr.sin_port));
      } else {
          puts("Unable to get client address");
      }

      handle_client_socket(client_sock);

	}


}


void handle_client_socket(int client_sock) {
    char buffer[RCVBUFSIZE];

    //1) Receive message.
    ssize_t numBytesRcvd = recv(client_sock, buffer, RCVBUFSIZE, 0);
     if (numBytesRcvd < 0) {
        exit_client("recv() failed");
     }

    while (numBytesRcvd > 0) {
        //2) Reply by sending back (part of) the message.
        ssize_t numBytesSent = send(client_sock, buffer, numBytesRcvd, 0);
        if (numBytesSent < 0) {
            exit_client("send() failed");
        }
        else if (numBytesSent != numBytesRcvd) {
            exit_client("send(): sent unexpected number of bytes");
        }

        //3) Receive message (if exists).
        numBytesRcvd = recv(client_sock, buffer, RCVBUFSIZE, 0);
        if (numBytesRcvd < 0) {
            exit_client("recv() failed");
        }
    }

    //4) Close the socket.
    close(client_sock);
}

