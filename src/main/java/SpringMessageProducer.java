import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.MessageProperties;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Igor Chirkov
 * @version 1.0 27.10.2016.
 */
public class SpringMessageProducer {
    private static com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();


    public static void main(String[] args) throws Exception {
        factory.setUsername("hms");
        factory.setPassword("password");
        factory.setVirtualHost("/");
        factory.setHost("10.2.53.166");
        factory.setPort(5672);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        ConnectionFactory connectionFactory = new CachingConnectionFactory(factory);
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);
        FanoutExchange exchange = new FanoutExchange("EventBus");
        amqpAdmin.declareExchange(exchange);
        template.setExchange("EventBus");
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Object o = new MessageProducer.TestCl();
        mapper.writeValue(stream, o);
        byte[] messageBodyBytes = stream.toByteArray();
        for (int i = 1; i < 21; i++) {
            template.convertAndSend(messageBodyBytes);
        }
        stream.close();
    }
}
