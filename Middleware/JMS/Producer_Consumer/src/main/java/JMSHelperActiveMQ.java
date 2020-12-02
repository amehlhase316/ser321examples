import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

// This is the ActiveMQ version
public class JMSHelperActiveMQ {

    public static Connection getJMSConnection(String jmsURL) throws Exception {
	// Create a ConnectionFactory
	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsURL);
	return connectionFactory.createConnection();
    }
}

