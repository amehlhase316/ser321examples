# Running the examples #
This is a multi project gradle setup. 

Meaning each project has its own gradle file but there is one setting file and overall grading file so you can run things from the example folders or from the top level folder. 

In the top level folder: 
 - gradle projects
 ==> gives you a list of projects

You can run a project through:
 - gradle projectname:taskname
 - e.g. gradle Deadlock:run

TCP
  -gradle TCP:runServer
    -gradle TCP:runServer --args '9099 5'
  -gradle TCP:runClient
    -gradle TCP:runClient  --args 'localhost 9099 message '

UDP
  -gradle UDP:runServer
    -gradle UDP:runServer --args '9099 5'
  -gradle UDP:runClient
    -gradle UDP:runServer --args 'localhost 9099'
  - 
  -gradle UDP:runServer2
    -gradle UDP:runServer2 --args '9099'
  -gradle UDP:runClient2
    -gradle UDP:runClient2  --args 'localhost 9099 message '

SimpleGrabURL
  -gradle SimpleGrabURL:run
    -gradle SimpleGrabURL:run --args 'https://devhints.io/bash'

SimpleGrabHttpURL
  -gradle SimpleGrabHttpURL:run
    -gradle SimpleGrabHttpURL:run --args 'https://devhints.io/bash'

JsonRPCviaTCPStudent
  -gradle JsonRPCviaTCPStudent:runServer
    -gradle JsonRPCviaTCPStudent:runServer --args '9099'
  -gradle JsonRPCviaTCPStudent:runClient
    -gradle JsonRPCviaTCPStudent:runClient --args 'localhost 9099'
