package dk.cosby.cphbusiness.si.bankingoperationsmom.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dk.cosby.cphbusiness.si.bankingoperationsmom.messaging.LoanMessageSender;
import dk.cosby.cphbusiness.si.bankingoperationsmom.models.LoanApplication;
import dk.cosby.cphbusiness.si.bankingoperationsmom.models.LoanRequest;
import dk.cosby.cphbusiness.si.bankingoperationsmom.models.LoanResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * Controller for all queries starting with "/loan"
 */
@Controller
@RequestMapping(path = "/loan")
public class LoanController {

    /**
     * Mapping for /loan root, return the loan page (loan.html)
     * @param model
     * @return loan.html page
     */
    @GetMapping(value = {"", "/"})
    public String loanApplication(Model model) {
        // Add object to model, the object contains the inputs from the html
        LoanRequest loanRequest = new LoanRequest();
        model.addAttribute("loanRequest", loanRequest);
        return "loan";
    }

    /**
     * Controller for /loan/request query
     * @param loanRequest Object that contains the data for the bankservers
     * @param model
     * @return loan-response.html page with the data connected
     * @throws InterruptedException not handled
     * @throws IOException not handled
     * @throws TimeoutException not handled
     */
    @Async
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public CompletableFuture<String> applyForLoan(@ModelAttribute(value="loanRequest") LoanRequest loanRequest, Model model) throws InterruptedException, IOException, TimeoutException {

        List<LoanResponse> responses = new ArrayList<>();

        // start the subscriber RabbitMQ the responses is added to the responses array
        Connection conn = listenForResponses("loan_response_exchange", responses);

        // Send request through rabbitMQ
        LoanMessageSender.publishLoanRequest(loanRequest);

        // wait for answers for one second
        Thread.sleep(1000L);

        // close the connection
        conn.close();

        /*
        for (LoanResponse response : responses) {
            System.out.println("response: " + response.getBankName());
        }
        */

        // add the data to the model
        model.addAttribute("loanResponses", responses);

        // Give empty object to contain data to the model
        model.addAttribute("loanResponse", new LoanResponse());


        return CompletableFuture.completedFuture("loan-response");

    }

    /**
     * Handles queries on /loan/application
     * @param loanResponse data from previous query
     * @param request the incoming request used to access the session
     * @param model
     * @return loan-application.html page with data from model
     */
    @RequestMapping(path = "/application", method = RequestMethod.POST)
    public String applyForLoan(@ModelAttribute(value="loanResponse") LoanResponse loanResponse, HttpServletRequest request, Model model) {

        /*
        System.out.println("BankName: " + loanResponse.getBankName());
        System.out.println(loanResponse.getCostPerMonth());
        System.out.println(loanResponse.getAnnualPercentageRate());
        System.out.println(loanResponse.getTotalCost());
        System.out.println(loanResponse.getPaybackPeriod());
        */

        // add loan data to session
        request.getSession().setAttribute("loanResponse", loanResponse);

        // empty object to contain personal data
        model.addAttribute("loanApplication", new LoanApplication());

        return "loan-application";
    }


    /**
     * handles queries on /loan/application/apply
     * @param loanApplication object to contain personal data
     * @param request incomming request object to access session
     * @return loan-application-approval.html page
     * @throws IOException not handled
     */
    @RequestMapping(path = "/application/apply", method = RequestMethod.POST)
    public String applyForLoan(@ModelAttribute(value="loanApplication") LoanApplication loanApplication, HttpServletRequest request) throws IOException {

        // Get the loan data from the session
        LoanResponse loanResponse = (LoanResponse) request.getSession().getAttribute("loanResponse");

        // generate random filename
        UUID filename = UUID.randomUUID();

        // Create a new file reference
        File file = new File("src/main/java/dk/cosby/cphbusiness/si/bankingoperationsmom/confirmationLetters/" + filename + ".txt");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        // write the confirmation letter to the file
        writer.write("Dear " + findPronounce(loanApplication.getFirstname()) + " " + loanApplication.getFirstname() + "\n");
        writer.write("\n");
        writer.write("Your loan has been approved by " + loanResponse.getBankName() + ".\n");

        writer.flush();
        writer.close(); // close writer when finished

        // add filename to session variable
        request.getSession().setAttribute("confirmationLetter", filename.toString());

        /*
        System.out.println(loanApplication.getFirstname());
        System.out.println(loanApplication.getLastname());
        System.out.println(loanApplication.getEmail());
        System.out.println(loanApplication.getAccountNumber());
        System.out.println(loanApplication.getRegistrationNumber());
        */

        /*
        System.out.println(loanResponse.getBankName());
        System.out.println(loanResponse.getPaybackPeriod());
        */

        return "loan-application-approval";
    }

    /**
     * Handles queries on /loan/application/apply/confirmationletter
     * This makes it possible to download the file
     * @param request to access the session
     * @return the confirmation letter at a text file (.txt)
     * @throws IOException write error
     */
    @GetMapping(
            path = "/application/apply/confirmationletter"
    )
    public ResponseEntity<InputStreamResource> createConfirmationLetter(HttpServletRequest request) throws IOException {

        // Get filename from session
        String filename = (String) request.getSession().getAttribute("confirmationLetter");

        // Create file reference
        File file = new File("src/main/java/dk/cosby/cphbusiness/si/bankingoperationsmom/confirmationLetters/" + filename + ".txt");

        // read the file (be ready to read the file)
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);

        // return the file (creates a download dialog at client)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(file.length())
                .body(inputStreamResource);

    }

    /**
     * Subscribe to exchange rabbitMQ
     * @param exchangeName name of the exchange
     * @param responses list of responses from the bank
     * @return the connection, so it can be closed when the reading is done
     * @throws IOException not handled
     * @throws TimeoutException not handled
     */
    private Connection listenForResponses(String exchangeName, List<LoanResponse> responses) throws IOException, TimeoutException {
        // Create connection to rabbitmq
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // connect to the exchange
        channel.exchangeDeclare(exchangeName, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // Handle responses
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            // Map json string to object

            LoanResponse loanResponse = mapper.readValue(message, LoanResponse.class);

            responses.add(loanResponse);

            System.out.println(" [x] Received '" + message + "'");
        };

        // listen for responses
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        return connection;
    }

    /**
     * returns the pronounce given a name e.g. "Anders" -> "Mr."
     * @param firstname name to find pronounce from
     * @return pronounce as String
     */
    private String findPronounce(String firstname) {

        // API URI
        final String uri = "http://www.thomas-bayer.com/restnames/name.groovy?name=" + firstname;

        // Consume API
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        boolean[] maleFemale = maleFemale(result);

        // Pronounce check from API response
        if(maleFemale[0] && !maleFemale[1]) return "Mr.";
        if(!maleFemale[0] && maleFemale[1]) return "Ms.";
        return "Mx.";

    }

    /**
     * Create a "truth table" from the API response
     * @param xml API response
     * @return truth table (male female)
     */
    private boolean[] maleFemale(String xml) {

        // init truth table
        boolean[] maleFemale = {false, false};

        // read API response as String
        Scanner scanner = new Scanner(xml);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // check if male and/or female is true and update truth table
            if (line.contains("male") && line.contains("true")) maleFemale[0] = true;
            if (line.contains("female") && line.contains("true")) maleFemale[1] = true;

        }
        scanner.close(); // close reader

        // return truth table
        return maleFemale;

    }

}
