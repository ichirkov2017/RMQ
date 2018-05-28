/**
 * Created by Igor Chirkov on 23.04.2015.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
;

class MessageProducer {


    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.print(new Boolean(true) == Boolean.TRUE);
        int c = 7;
       int result = 4;
        result += c;
        System.out.print(result);
        ConnectionFactory factory = new ConnectionFactory();
        Address[] addresses = {new Address("10.233.6.229", 5672)
//            , new
//            Address("10.233.6.227", 5672)
        };
        factory.setUsername("hms");
        factory.setPassword("password");
        factory.setVirtualHost("/");
        factory.setTopologyRecoveryEnabled(true);
        factory.setAutomaticRecoveryEnabled(true);
        Connection conn = factory.newConnection(addresses);
        Channel channel = conn.createChannel();
        String exchangeName = "EventBus";
        String routingKey = "testRoute";

        for (int i = 1; i < 2100000; i++) {
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Object o = new TestCl();
            mapper.writeValue(stream, o);
            byte[] messageBodyBytes = stream.toByteArray();
            channel.basicPublish(exchangeName, routingKey, MessageProperties.BASIC, messageBodyBytes);
            Thread.sleep(3000);
        }
        channel.close();
        conn.close();
    }

    public static class TestCl {
        public String first = "igor";
        public String second = "Chirkov";
    }
}
