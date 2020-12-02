import javax.jms.*;

/**
   This simple producer pushes a single text message to a Queue. The Queue
   is command-line argument 0 and the text message is command-line argument 1.
   Run the corresponding QConsumer to consume the message.
 */
public class QProducer {

    public static void main(String args[]) throws Exception {
      if (args.length != 4) {
        System.out.println("Expected arguments: <jms-host(String)> <jms-port(int)> <queue-name(String)> <message(String)>");
        System.exit(1);
      }
      int jmsPort = 61616;
      String jmsHost = args[0];
      String queueName = args[2];
      String message = args[3];
      try {
        jmsPort = Integer.parseInt(args[1]);
      } catch (NumberFormatException nfe) {
        System.out.println("[JMSPort] must be an integer");
        System.exit(2);
      }

    // URL if running in same VM
    // String jmsURL = "vm://localhost";
    // URL if running in different VMs
    String jmsURL = String.format("tcp://%s:%s", jmsHost, jmsPort);
	try {
	    Connection connection = JMSHelperActiveMQ.getJMSConnection(jmsURL);
	    connection.start();
	    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    Destination destination = session.createQueue(queueName);
 
	    // Create a MessageProducer from the Session to the Topic or Queue
	    MessageProducer producer = session.createProducer(destination);
	    //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

	    TextMessage msg = session.createTextMessage(message);
	    System.out.println("Sent message '" + msg.getText() + "' to queue: " + queueName);
	    producer.send(msg);
	
	    session.close();
	    connection.close();
	} catch (Throwable tw) {
	    tw.printStackTrace();
	}
	//}
    }
}
