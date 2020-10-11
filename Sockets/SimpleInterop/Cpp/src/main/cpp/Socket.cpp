/*
 *   C++ sockets on Unix and Windows
 *   Copyright (C) 2002
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

  #include <sys/types.h>       // For data types
  #include <sys/socket.h>      // For socket(), connect(), send(), and recv()
  #include <netdb.h>           // For gethostbyname()
  #include <arpa/inet.h>       // For inet_addr()
  #include <unistd.h>          // For close()
  #include <netinet/in.h>      // For sockaddr_in
  typedef void raw_type;       // Type used for raw data on this platform

#include <errno.h>             // For errno
#include <string.h>
#include "Socket.hpp"

using namespace std;

// SocketException Code

// Function to fill in address structure given an address and port
static void fillAddr(const string &address, unsigned short port, 
                     sockaddr_in &addr) {
  memset(&addr, 0, sizeof(addr));  // Zero out address structure
  addr.sin_family = AF_INET;       // Internet address

  hostent *host;  // Resolve name
  if ((host = gethostbyname(address.c_str())) == NULL) {
    // strerror() will not work for gethostbyname() and hstrerror() 
    // is supposedly obsolete
    throw SocketException("Failed to resolve name (gethostbyname())");
  }
  addr.sin_addr.s_addr = *((unsigned long *) host->h_addr_list[0]);

  addr.sin_port = htons(port);     // Assign port in network byte order
}

// Socket Code

Socket::Socket(int type, int protocol) {
   try{
      // Make a new socket
      if ((sockDesc = socket(PF_INET, type, protocol)) < 0) {
         throw SocketException("Socket creation failed (socket())", true);
      }
   }catch(SocketException&){
      throw;
   }
}

Socket::Socket(int sockDesc) {
  this->sockDesc = sockDesc;
}

Socket::~Socket() {
    ::close(sockDesc);
  sockDesc = -1;
}

string Socket::getLocalAddress() {
  sockaddr_in addr;
  unsigned int addr_len = sizeof(addr);
  try{
     if (getsockname(sockDesc, (sockaddr *) &addr, (socklen_t *) &addr_len) < 0) {
        throw SocketException("Fetch of local address failed (getsockname())", true);
     }
  }catch(SocketException&){
     throw;
  }
  return inet_ntoa(addr.sin_addr);
}

unsigned short Socket::getLocalPort() {
  sockaddr_in addr;
  unsigned int addr_len = sizeof(addr);
  try{
     if (getsockname(sockDesc, (sockaddr *) &addr, (socklen_t *) &addr_len) < 0) {
        throw SocketException("Fetch of local port failed (getsockname())", true);
     }
  }catch(SocketException&){
     throw;
  }
  return ntohs(addr.sin_port);
}

void Socket::setLocalPort(unsigned short localPort) {
  // Bind the socket to its port
  sockaddr_in localAddr;
  try{
     memset(&localAddr, 0, sizeof(localAddr));
     localAddr.sin_family = AF_INET;
     localAddr.sin_addr.s_addr = htonl(INADDR_ANY);
     localAddr.sin_port = htons(localPort);

     int i = ::bind(sockDesc, (sockaddr *) &localAddr, sizeof(sockaddr_in));
     if ( i < 0) {
        throw SocketException("Set of local port failed (bind())", true);
     }
  }catch(SocketException&){
     throw;
  }
}

void Socket::setLocalAddressAndPort(const string &localAddress,
    unsigned short localPort){
  // Get the address of the requested host
  sockaddr_in localAddr;
  try{
     fillAddr(localAddress, localPort, localAddr);

     if (::bind(sockDesc, (sockaddr *) &localAddr, sizeof(sockaddr_in)) < 0) {
        throw SocketException("Set of local address and port failed (bind())", true);
     }
  }catch(SocketException&){
     throw;
  }
}

void Socket::cleanUp() {
}

unsigned short Socket::resolveService(const string &service,
                                      const string &protocol) {
  struct servent *serv;        /* Structure containing service information */

  if ((serv = getservbyname(service.c_str(), protocol.c_str())) == NULL)
    return stoi(service.c_str());  /* Service is port number */
  else 
    return ntohs(serv->s_port);    /* Found port (network byte order) by name */
}
