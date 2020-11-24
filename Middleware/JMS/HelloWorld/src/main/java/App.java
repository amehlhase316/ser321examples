import org.apache.activemq.ActiveMQConnectionFactory;
 
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
 
/**
 * Hello world!
 */
public class App {
 
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Expected arguments: <jms-host(String)>  <queue-name(String)> <sleep-delay(int)>");
            System.exit(1);
          }
        int sleepDelay = 1000;  // default value
        String jmsHost = args[0];
        String queueName = args[1];
        try {
          sleepDelay = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
          System.out.println("[sleep-delay] must be an integer");
          System.exit(2);
        }
        //String jmsURL = String.format("tcp://%s:%s", jmsHost, jmsPort);
        String jmsURL = String.format("vm://%s", jmsHost);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        Thread.sleep(sleepDelay);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        Thread.sleep(sleepDelay);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        Thread.sleep(sleepDelay);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldConsumer(jmsURL, queueName, sleepDelay), false);
        thread(new HelloWorldProducer(jmsURL, queueName), false);
    }
 
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
 
    public static class HelloWorldProducer implements Runnable {
        String jmsURL = "vm://localhost";
        String queueName = "FOO.TEST";
        public HelloWorldProducer(String jmsURL, String queueName) {
          this.jmsURL = jmsURL;
          this.queueName = queueName;
        }
        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsURL);
 
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();
 
                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
 
                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue(queueName);
 
                // Create a MessageProducer from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
 
                // Create a messages
                String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);
 
                // Tell the producer to send the message
                System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);
 
                // Clean up
                session.close();
                connection.close();
            }
            catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }
 
    public static class HelloWorldConsumer implements Runnable, ExceptionListener {
        String jmsURL = "vm://localhost";
        String queueName = "FOO.TEST";
        int sleepDelay = 1000;
        public HelloWorldConsumer(String jmsURL, String queueName, int sleepDelay) {
          this.jmsURL = jmsURL;
          this.queueName = queueName;
          this.sleepDelay = sleepDelay;
        }
        public void run() {
            try {
 
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
 
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();
 
                connection.setExceptionListener(this);
 
                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
 
                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue(queueName);
 
                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);
 
                // Wait for a message
                Message message = consumer.receive(sleepDelay);
 
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
 
        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }
}
