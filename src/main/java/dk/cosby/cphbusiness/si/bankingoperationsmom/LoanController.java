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

        // Call rabbitMQ with request to get loans with the information provided

    }


}
