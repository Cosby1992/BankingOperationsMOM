package dk.cosby.cphbusiness.si.bankingoperationsmom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingOperationsMomApplication {




    public static void main(String[] args) {

        SpringApplication.run(BankingOperationsMomApplication.class, args);

        try {
            LoanMessageReceiver.receive();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
