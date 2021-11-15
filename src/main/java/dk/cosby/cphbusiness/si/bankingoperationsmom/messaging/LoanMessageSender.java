package dk.cosby.cphbusiness.si.bankingoperationsmom.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import dk.cosby.cphbusiness.si.bankingoperationsmom.models.LoanRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class LoanMessageSender {

    private final static String QUEUE_NAME = "LoanRequest";
    private final static String EXCHANGE_NAME = "loan_request_exchange";

    // Send message via pubsub rabbitmq
    public static void publishLoanRequest(LoanRequest loanRequest) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(loanRequest);

            channel.basicPublish(EXCHANGE_NAME, "", null, json.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + json + "'");
        }
    }


    // send point to point message rabbitmq
    public static void sendLoanRequest(LoanRequest loanRequest) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(loanRequest);
            System.out.println("Resulting JSON string = \n" + json);
            //System.out.println(json);

            channel.basicPublish("", QUEUE_NAME, null, json.getBytes(StandardCharsets.UTF_8));

            System.out.println(" [x] Sent '" + json + "'");

        }

    }


}
