package dk.cosby.cphbusiness.si.bankingoperationsmom.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    // This return the homepage (index.html)
    @GetMapping("/")
    public String index() {
        return "index";
    }




}
