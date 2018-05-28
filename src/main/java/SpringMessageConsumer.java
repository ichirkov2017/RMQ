import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import java.io.IOException;

/**
 * @author Igor Chirkov
 * @version 1.0 27.10.2016.
 */
public class SpringMessageConsumer {
    public static void main(final String... args) throws Exception {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setUsername("hms");
        factory.setPassword("password");
        factory.setVirtualHost("/");
        factory.setHost("10.2.53.166");
        factory.setPort(5672);
        factory.setAutomaticRecoveryEnabled(false);
        factory.setTopologyRecoveryEnabled(true);
        ConnectionFactory connectionFactory = new CachingConnectionFactory(factory);
//        AmqpTemplate template = new RabbitTemplate(connectionFactory);
        AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);
        FanoutExchange exchange = new FanoutExchange("EventBus");
        amqpAdmin.declareExchange(exchange);
        Queue queue = new AnonymousQueue();
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange));
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queue);
        container.setMessageListener(new Listener());
        container.start();
//        container.stop();
//        container.start();
    }

    private static class Listener implements MessageListener {

        public void onMessage(Message message) {
            MessageProducer.TestCl cl = null;
            try {
                cl = new ObjectMapper().readValue(message.getBody(), MessageProducer.TestCl.class);
                System.out.println(cl.first);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
