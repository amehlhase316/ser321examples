import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DemoPublisherModel {
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;

    /* Establish JMS publisher */
    public DemoPublisherModel(String topicName, String userName, String password) throws Exception {
	// Obtain a JNDI connection - see jndi.properties
	InitialContext jndi = new InitialContext();
	// Look up a JMS connection factory
	TopicConnectionFactory conFactory = (TopicConnectionFactory)jndi.lookup("topicConnectionFactry");
	// Create a JMS connection
	connection = conFactory.createTopicConnection(userName, password);
	// Create JMS session objects for publisher
	pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	// Look up a JMS topic
	Topic chatTopic = (Topic) jndi.lookup(topicName);
	// Create a JMS publisher
	publisher = pubSession.createPublisher(chatTopic);
	publisher.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    private boolean goPublish(String s) {
	boolean rval = true;
	try {
	    // Create and send message using topic publisher
	    TextMessage message = pubSession.createTextMessage();
	    message.setText(s);
	    publisher.publish(message);
	} catch (Throwable thw1) {
	    thw1.printStackTrace();
	    rval = false;
	}
	return rval;
    }

    public static void main(String[] args) {
	// uncomment this line for verbose logging to the screen
	//BasicConfigurator.configure();
      if (args.length != 3) {
        System.out.println("Expected arguments: <topic-name(String)> <username(String)> <password(String)>");
        System.exit(1);
      }
      String topicName = args[0];
      String userName = args[1];
      String password = args[2];
      try {
		DemoPublisherModel demo = new DemoPublisherModel(topicName, userName, password);
		BufferedReader commandLine = new java.io.BufferedReader(new InputStreamReader(System.in));

		// closes the connection and exit the system when 'exit' entered in the command line
		while (true) {
          System.out.println("Enter text to publish. Enter 'exit' to close the program.");
          String s = commandLine.readLine();
          if (s.equalsIgnoreCase("exit")) {
			demo.connection.close();
			System.exit(0);
          }
          if (demo.goPublish(s)) {
			System.out.println("Published: " + s);
          } else {
			System.out.println("Unable to publish: " + s);
          }
		}
	} catch (Exception e) {
	    e.printStackTrace();
	}
  }
}
