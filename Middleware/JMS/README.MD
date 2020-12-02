#### Important

Please make sure that `activemq` is running before running these examples

To monitor `activemq`, you can open `http://127.0.0.1:8161/admin/` in your browser. Use the credentials `admim/admin`.
Please note that this is for a default installation, for custom installations this may differ.

#### Running the examples #

This is a multi project gradle setup.

Meaning each project has its own gradle file but there is one setting file and overall grading file so you can run things from the example folders or from the top level folder. 

In the top level folder (`ActiveMQ`):
 - gradle projects
 	- gives you a list of projects

You can run a project through:
 - gradle projectname:taskname
    - gradle Producer_Consumer:runProducerJNDI -Pmessage=anketJNDI

#####  Pre-requisite for each project

  1. Please make sure that `activemq` is running and you're able to access the monitor at `http://127.0.0.1:8161/admin/`
  2. Open a browser window with `http://127.0.0.1:8161/admin/`

##### HelloWorld - ActiveMQ

  A JMS application with multiple, concurrent, consumers and producers running on the same machine.
  1. Open a new tab in your terminal.
  2. Run the **HelloWorld** example
    * `gradle HelloWorld:runHelloWorld -Phost=localhost -PqueueName=MyQueue -PsleepDelay=1000`

##### Producer_Consumer - ActiveMQ

  1. Open two tabs in your terminal
  2. In one tab run the **Producer**. Please run one **Producer** version at one time. They both achieve the same result using different approaches.
  * CLI args version.   
    * Uses CLI arguments to get information regarding the connection, i.e, host, port, queueNmae  
    * `gradle Producer_Consumer:runProducer -Phost=localhost -Pport=61616 -PqueueName=MyQueue -Pmessage=Blanket. `
  * JNDI version  
    *  Uses JNDI properties file to get information regarding the connection, i.e, host, port, queueNmae
     * `gradle Producer_Consumer:runProducerJNDI -Pmessage=BlanketJNDI`
  3. In the other tab run the **Consumer**
      * `gradle Producer_Consumer:runConsumer -Phost=localhost -Pport=61616 -PqueuName=MyQueue -Ptimeout=10000`
  4. You can view the activity inside `activemq` from your browser window running the monitor.e

##### Publisher_Subscriber - ActiveMQ

  1. Open two tabs in your terminal
  2. In one tab run the **Publisher**.
    * gradle Publisher_Subscriber:runDemoPublisherModel -PtopicName=MyTopic -PuserName=default -Ppassword=default
  3. In the other tab run the **Subscriber**. Please run one **Subscriber** version at one time. They both achieve the same result using different approaches.
  * Without Listener
      * User a loop counter to get a fixed number of messages.
      * `gradle Publisher_Subscriber:runDemoSubscriberModel -PtopicName=MyTopic -PclientName=MyClient -PuserName=default -Ppassword=default -PlistenFor=10`
   * With Listener
      * User a **Listener** instead of a loop counter to get messages.
      * `gradle Publisher_Subscriber:runDemoSubscriberModelWithListener -PtopicName=MyTopic -PclientName=MyClient -PuserName=default -Ppassword=default`
  4. You can view the activity inside `activemq` from your browser window running the monitor.S

##### Topic

  NOTE: A **Topic** should already have been created/registered for this example to run. You can use the `jndi.properties` file for registering a new **Topic**.
  Adding a new mapping (`topic.MyTopicJMS = example.MyTopicJMS`) would register a new **Topic** - `example.MyTopicJMS`.
  1. Open a new tab in your terminal.
  2. Run the JMS **Topic** example. This example creates both a **Topic-Publisher** and a **Topic-Subscriber**. Both the **Topic-Publisher** and the **Topic-Subscriber** are attached to the same **Topic**.
The **Publisher** would publish a message to the **Topic** and the **Subscriber** would get it and display it.
     * `gradle Topic:runTopicExample -PtopicName=MyTopic -PuserName=default -Ppassword=default`
  3. You can viewthe activity inside `activemq` from your browser window running the monitor. 

