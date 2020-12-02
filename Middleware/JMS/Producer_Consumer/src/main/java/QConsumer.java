import javax.jms.*;

/**
   This simple consumer consumes a single text message on a Queue. The Queue
   is command-line argument 0. Run the corresponding QProducer to send the message.
 */
public class QConsumer {
    public static void main(String args[]) throws Exception {
      if (args.length != 4) {
          System.out.println("Expected arguments: <jms-host(String)> <jms-port(int)> <queue-name(String)> <timeout(int)>");
          System.exit(1);
        }
      int timeout = 5000;
      int jmsPort = 61616;
      String jmsHost = args[0];
      String queueName = args[2];
      try {
        jmsPort = Integer.parseInt(args[1]);
        timeout = Integer.parseInt(args[3]);
      } catch (NumberFormatException nfe) {
        System.out.println("[JMSPort|Timeout] must be an integer");
        System.exit(2);
      }
	try {
	    // URL if running in same VM
        // String jmsURL = "vm://localhost";
        // URL if running in different VMs
        String jmsURL = String.format("tcp://%s:%s", jmsHost, jmsPort);
	    Connection connection = JMSHelperActiveMQ.getJMSConnection(jmsURL);
	    connection.start();
	    //connection.setExceptionListener(this);
	
	    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	
	    Destination destination = session.createQueue(queueName);
	    MessageConsumer consumer = session.createConsumer(destination);
	    // Wait for a message
	    Message message = consumer.receive(timeout);
 
	    if (message instanceof TextMessage) {
          TextMessage textMessage = (TextMessage) message;
          String text = textMessage.getText();
          System.out.println("Received: " + text);
	    } else {
          System.out.println("Received: " + message);
	    }
	
	    consumer.close();
	    session.close();
	    connection.close();
	} catch (Exception e) {
	    System.out.println("Caught: " + e);
	    e.printStackTrace();
	}
    }
}

