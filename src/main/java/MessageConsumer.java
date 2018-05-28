import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 *
 * Created by Igor Chirkov on 23.04.2015.
 */
public class MessageConsumer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        Address[] addresses = {new Address("10.233.6.229", 5672)
//            , new
//            Address("10.233.6.227", 5672)
        };
        factory.setUsername("hms");
        factory.setPassword("password");
        factory.setVirtualHost("/");
//        factory.setHost("10.2.53.166");
        factory.setAutomaticRecoveryEnabled(true);
        Connection conn = null;
        try {
            conn = factory.newConnection(addresses);
        } catch (Exception e) {
            System.out.println("connection failed");

        }
        final Channel channel = conn.createChannel();
        String exchangeName = "EventBus";
        String queueName = "myQueue";
        String routingKey = "testRoute";
        boolean durable = true;
        channel.exchangeDeclare(exchangeName, "fanout", durable);
        channel.queueDeclare(queueName, false, true, true, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        boolean autoAck = false;
        channel.basicConsume(queueName, autoAck, "myConsumerTag",
            new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                    throws IOException
                {
//                    String routingKey = envelope.getRoutingKey();
//                    String contentType = properties.getContentType();
                    long deliveryTag = envelope.getDeliveryTag();
                    MessageProducer.TestCl cl = new ObjectMapper().readValue(body, MessageProducer.TestCl.class);

                    System.out.println("Message received " + cl.second);
//                    System.out.println(routingKey);
//                    System.out.println(contentType);
                    channel.basicAck(deliveryTag, false);
                }
            });
//        String exchangeName = "myExchange";
//        String queueName = "myQueue";
//        String routingKey = "testRoute";
//        boolean durable = true;
//        channel.exchangeDeclare(exchangeName, "direct", durable);
//        channel.queueDeclare(queueName, durable, false, false, null);
//        channel.queueBind(queueName, exchangeName, routingKey);
//        QueueingConsumer consumer = new QueueingConsumer(channel);
//        channel.basicConsume(queueName, false, consumer);
//        boolean run = true;
//        while (run) {
//            QueueingConsumer.Delivery delivery;
//            try {
//                delivery = consumer.nextDelivery();
//                String message = new String(delivery.getBody());
//                long tag = delivery.getEnvelope().getDeliveryTag();
//                System.out.println("Message received " + message);
//                channel.basicAck(tag, false);
//                System.out.println("Message deleted " + message);
//            } catch (InterruptedException ie) {
//                continue;
//            }
//        }
//        channel.close();
//        conn.close();
    }
}
