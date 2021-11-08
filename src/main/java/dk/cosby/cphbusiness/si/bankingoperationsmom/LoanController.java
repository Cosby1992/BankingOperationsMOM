package dk.cosby.cphbusiness.si.bankingoperationsmom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/loan")
public class LoanController {

    @GetMapping(value = {"", "/"})
    public String loanApplication(Model model) {
        LoanRequest loanRequest = new LoanRequest();
        model.addAttribute("loanRequest", loanRequest);
        return "loan";
    }

    @ResponseBody
    @RequestMapping(path = "/request", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public void applyForLoan(@ModelAttribute(value="loanRequest") LoanRequest loanRequest) {

        System.out.println(loanRequest.getLoanAmount());
        System.out.println(loanRequest.getPaybackPeriod());
        System.out.println(loanRequest.getFirstname());
        System.out.println(loanRequest.getLastname());
        System.out.println(loanRequest.getDateOfBirth().toString());

        // Send request through rabbitMQ
        try {
            LoanMessageSender.sendLoanRequest(loanRequest);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        // Get responses from MQ

        // show the responses in a nice way

        // Call rabbitMQ with request to get loans with the information provided



    }

    @RequestMapping(path = "/listen")
    public String listen(){
        try {
            LoanMessageReceiver.listen();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return "listen";
    }


}
