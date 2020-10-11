# Running the examples #
This is a multi project gradle setup. 

Meaning each project has its own gradle file but there is one setting file and overall grading file so you can run things from the example folders or from the top level folder. 

In the top level folder: 
 - gradle projects
 ==> gives you a list of projects

You can run a project through:
 - gradle projectname:taskname
 - e.g. gradle Deadlock:run

SimpleWebServer
  - gradle SimpleWebServer:run
    - gradle SimpleWebServer:run --args '9099'

Socket
  - gradle Socket:runServer
    - gradle SocketState:runServer --args '9099 10000'
  - gradle Socket:runClient
    - gradle Socket:runClient --args 'localhost 9099 10 20'

SocketState
  - gradle SocketState:runServer
    - gradle SocketState:runServer --args '9099 5'
  - gradle SocketState:runClient
    - gradle SocketState:runClient --args 'localhost 9099 r 10 20'

MulticastSocket
  - gradle MulticastSocket:runListener
    - gradle MulticastSocket:runListener --args '225.6.7.8 2222'
  - gradle MulticastSocket:runSender
    - gradle MulticastSocket:runSender --args "'my message' 225.6.7.8 2222"
  - gradle MulticastSocket:runMulticastPeer
    - gradle MulticastSocket:runMulticastPeer --args "'my message' 225.6.7.8 2222"

JavaThreadSock
  - gradle JavaThreadSock:runServer
    - gradle JavaThreadSock:runServer --args '9099'
  - gradle JavaThreadSock:runClient
    - gradle JavaThreadSock:runClient --args 'localhost 9099'

JavaSimpleSock
  - gradle JavaSimpleSock:SockClient -Phost=localhost -Pmessage=hello -Pnumber=3
  - OR
  - gradle JavaSimpleSock:runClient
    - gradle JavaSimpleSock:runClient --args 'localhost message 10'
  - gradle JavaSimpleSock:runServer
  -
  - gradle JavaSimpleSock:runGetIP
    - gradle JavaSimpleSock:runGetIP --args 'https://devhints.io/bash'
  -
  - gradle JavaSimpleSock:runGetNameForIP --args '8.8.4.4'
  -
  - gradle JavaSimpleSock:runBrowser
    - gradle JavaSimpleSock:runBrowser --args 'https://devhints.io/bash'
  -
  - gradle JavaSimpleSock:runDatagramReceive
    - gradle JavaSimpleSock:runDatagramReceive --args '9099'
  - gradle JavaSimpleSock:runDatagramSend
    - gradle JavaSimpleSock:runDatagramSend --args '9099 message'

GroupSerializeSocket (Maintain this order)
  - gradle GroupSerializeSocket:runGroupFileSerialize
  - 
  - gradle GroupSerializeSocket:runServer
    - gradle GroupSerializeSocket:runServer --args '9099'
  - gradle GroupSerializeSocket:runClient
    - gradle GroupSerializeSocket:runClient --args 'localhost 9099'

SimpleInterop
  - Please check the readme inside the project

WebServer
  - gradle WebServer:SimpleWebServer
  - gradle WebServer:FunWebServer
