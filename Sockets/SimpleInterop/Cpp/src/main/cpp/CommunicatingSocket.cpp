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
#include "CommunicatingSocket.hpp"

using namespace std;

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

// CommunicatingSocket Code

CommunicatingSocket::CommunicatingSocket(int type, int protocol) : Socket(type, protocol) {
}

CommunicatingSocket::CommunicatingSocket(int newConnSD) : Socket(newConnSD) {
}

void CommunicatingSocket::connect(const string &foreignAddress,
    unsigned short foreignPort) {
   // Get the address of the requested host
   sockaddr_in destAddr;
   try{
      fillAddr(foreignAddress, foreignPort, destAddr);

      // Try to connect to the given port
      if (::connect(sockDesc, (sockaddr *) &destAddr, sizeof(destAddr)) < 0) {
         throw SocketException("Connect failed (connect())", true);
      }
   }catch(SocketException&){
      throw;
   }
}

void CommunicatingSocket::send(const void *buffer, int bufferLen){
   try{
      if (::send(sockDesc, (raw_type *) buffer, bufferLen, 0) < 0) {
         throw SocketException("Send failed (send())", true);
      }
   }catch(SocketException&){
      throw;
   }
}

int CommunicatingSocket::recv(void *buffer, int bufferLen) {
  int rtn;
  try{
     if ((rtn = ::recv(sockDesc, (raw_type *) buffer, bufferLen, 0)) < 0) {
        throw SocketException("Received failed (recv())", true);
     }
  }catch(SocketException&){
     throw;
  }
  return rtn;
}

string CommunicatingSocket::getForeignAddress() {
  sockaddr_in addr;
  unsigned int addr_len = sizeof(addr);
  try{
     if (getpeername(sockDesc, (sockaddr *) &addr,(socklen_t *) &addr_len) < 0) {
        throw SocketException("Fetch of foreign address failed (getpeername())", true);
     }
  }catch(SocketException&){
     throw;
  }
  return inet_ntoa(addr.sin_addr);
}

unsigned short CommunicatingSocket::getForeignPort() {
   sockaddr_in addr;
   unsigned int addr_len = sizeof(addr);
   try{

      if (getpeername(sockDesc, (sockaddr *) &addr, (socklen_t *) &addr_len) < 0) {
         throw SocketException("Fetch of foreign port failed (getpeername())", true);
      }
   }catch(const SocketException&){
      throw;
   }
   return ntohs(addr.sin_port);
}
