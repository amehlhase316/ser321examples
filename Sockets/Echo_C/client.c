#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define BUFFER_SIZE 1024
#define SERV_IP "127.0.0.1" /*IP Address*/

void exit_client(const char* msg) {
	perror(msg);
	exit(1);
}

int main(int argc, char *argv[]) {

    //1) Fetch client parameters (server IP, server port, text message)
	if (argc != 4) {
	    exit_client("Parameter(s): <Server Address>, <Server Port>, <Message>]");
    }

    char* server_ip_str = argv[1];
    in_port_t server_port = atoi(argv[2]);
    char* echo_text = argv[3];

	// 2) Create a socket using TCP.
	int client_sock;
	if ((client_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
	  exit_client("socket(): failed creation");
    }

    // 3) Construct the server address struct
	struct sockaddr_in server_addr;
	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	int rtnVal = inet_pton(AF_INET, server_ip_str, &server_addr.sin_addr.s_addr);
	if (rtnVal == 0) {
	    exit_client("inet_pton(): invalid ip address");
    } else if (rtnVal < 0) {
	    exit_client("inet_pton(): error when parsing");
    }
	server_addr.sin_port = htons(server_port);

    // 4) Establish the connection to the server.
	if (connect(client_sock, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0) {
	  exit_client("connect(): failed");
    }

	// 5) Send message.
	size_t echo_text_len = strlen(echo_text);
	ssize_t numBytes = send(client_sock, echo_text, echo_text_len, 0);
	if (numBytes < 0) {
	  exit_client("send(): failed to send message");
    } else if (numBytes != echo_text_len) {
	  exit_client("send(): sent unexpected number of bytes");
    }

    // 6) Receive message.
	unsigned int bytes_recieved = 0;
	printf("Received: ");
	while (bytes_recieved < echo_text_len) {
	    char buffer[BUFFER_SIZE];
	    numBytes = recv(client_sock, buffer, BUFFER_SIZE - 1, 0);
	    if (numBytes < 0) {
	        exit_client("recv(): failed");
        } else if (numBytes == 0) {
	        exit_client("recv(): connection closed");
        }
	    bytes_recieved += numBytes;
	    buffer[numBytes] = '\0';
	    fputs(buffer, stdout);
	}
	printf("\n");

	//7) Close the socket.
	close(client_sock);
	exit(0);

}

