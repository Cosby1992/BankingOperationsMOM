package dk.cosby.cphbusiness.si.bankingoperationsmom;

import java.sql.Date;

public class LoanRequest {

    private double loanAmount;
    private int paybackPeriod;
    private String firstname;
    private String Lastname;
    private Date dateOfBirth;

    public LoanRequest() {
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public int getPaybackPeriod() {
        return paybackPeriod;
    }

    public void setPaybackPeriod(int paybackPeriod) {
        this.paybackPeriod = paybackPeriod;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
