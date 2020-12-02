import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

// from https://dzone.com/articles/jms-activemq
public class TopicExample implements javax.jms.MessageListener {
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    /* Establish JMS publisher and subscriber */
    public TopicExample(String topicName, String username,
					String password) throws Exception {
	// Obtain a JNDI connection
	InitialContext jndi = new InitialContext();
	// Look up a JMS connection factory
	TopicConnectionFactory conFactory = (TopicConnectionFactory) jndi
	    .lookup("topicConnectionFactry");
	// Create a JMS connection
	connection = conFactory.createTopicConnection(username, password);
	// Create JMS session objects for publisher and subscriber
	pubSession = connection.createTopicSession(false,
						   Session.AUTO_ACKNOWLEDGE);
	TopicSession subSession = connection.createTopicSession(false,
								Session.AUTO_ACKNOWLEDGE);
	// Look up a JMS topic
	Topic chatTopic = (Topic) jndi.lookup(topicName);
	// Create a JMS publisher and subscriber
	publisher = pubSession.createPublisher(chatTopic);
	TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);
	// Set a JMS message listener
	subscriber.setMessageListener(this);
	// Start the JMS connection; allows messages to be delivered
	connection.start();
	// Create and send message using topic publisher
	TextMessage message = pubSession.createTextMessage();
    String messageStr = String.format("'Howdy Friends!' from user: %s", username);
	message.setText(messageStr);
    System.out.println("Publishing message: " + messageStr);
	publisher.publish(message);
    }
    /*
     * A client can register a message listener with a consumer. A message
     * listener is similar to an event listener. Whenever a message arrives at
     * the destination, the JMS provider delivers the message by calling the
     * listener's onMessage method, which acts on the contents of the message.
     */
    public void onMessage(Message message) {
	try {
	    TextMessage textMessage = (TextMessage) message;
	    String text = textMessage.getText();
	    System.out.println("Received Message: " + text);
	} catch (JMSException jmse) {
	    jmse.printStackTrace();
	}
    }
    public static void main(String[] args) {
	// uncomment the following line if you want verbose logging
	//BasicConfigurator.configure();
    if (args.length != 3) {
        System.out.println("Expected arguments: <topic-name(String)> <username(String)> <password(String)>");
        System.exit(1);
      }
      String topicName = args[0];
      String userName = args[1];
      String password = args[2];
	try {
	    TopicExample demo = new TopicExample(topicName, userName, password);
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
