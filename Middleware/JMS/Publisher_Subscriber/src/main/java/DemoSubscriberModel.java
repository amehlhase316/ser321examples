import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DemoSubscriberModel {
    private TopicSession pubSession;
    private TopicConnection connection;

    /* Establish JMS subscriber */
    public DemoSubscriberModel(String topicName, String clientName, String username, String password, int listenFor)
	throws Exception {
	// Obtain a JNDI connection
	InitialContext jndi = new InitialContext();
	// Look up a JMS connection factory
	TopicConnectionFactory conFactory = (TopicConnectionFactory)jndi.lookup("topicConnectionFactry");
	// Create a JMS connection
	connection = conFactory.createTopicConnection();
	connection.setClientID(clientName);  // this is normally done by configuration not programmatically
	// Look up a JMS topic - see jndi.properties in the classes directory
	Topic chatTopic = (Topic) jndi.lookup(topicName);
	TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	TopicSubscriber subscriber = subSession.createDurableSubscriber(chatTopic, "DemoSubscriberModel");
	// Start the JMS connection; allows messages to be delivered
	connection.start();
	// Listen for 10 messages
	int count=0;
    System.out.println("Will wait for " + listenFor + " messages.");
    while (count < listenFor) {
      System.out.println("waiting for messages...");
      TextMessage msg = (TextMessage) subscriber.receive();
      System.out.println("Message #" + ++count + " Received message: " + msg.getText());
	}
	connection.close();
    }
    
    public static void main(String[] args) {
	// uncomment this line for verbose logging to the screen
	// BasicConfigurator.configure();
      if (args.length != 5) {
        System.out.println("Expected arguments: <topic-name(String)> <client-name(String)> <username(String)> <password(String)> <#messages-to-listen-for(int)>");
        System.exit(1);
      }
      String topicName = args[0];
      String clientName = args[1];
      String userName = args[2];
      String password = args[3];
      int listenFor = 10;  // default value
      try {
            listenFor = Integer.parseInt(args[4]);
        } catch (NumberFormatException nfe) {
            System.out.println("[#messages] must be integer");
            System.exit(2);
        }
	try {
	    DemoSubscriberModel demo = new DemoSubscriberModel(topicName, clientName, userName, password, listenFor);
	    BufferedReader commandLine = new java.io.BufferedReader(new InputStreamReader(System.in));
	    
	    // closes the connection and exit the system when 'exit' enters in
	    // the command line
        System.out.println("Enter 'exit' to close the program.");
        while (true) {
        String s = commandLine.readLine();
        if (s.equalsIgnoreCase("exit")) {
            demo.connection.close();
            System.exit(0);
        }
        }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
