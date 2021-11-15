package dk.cosby.cphbusiness.si.bankingoperationsmom.models;

public class LoanResponse {

    // "apr":"40","totalcost":"123123","costPrMonth":"10260.25","paybackPeriod":"12","bankname":"Always There For You"

    private String bankName;
    private String paybackPeriod;
    private String costPerMonth;
    private String totalCost;
    private String annualPercentageRate;

    public LoanResponse() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPaybackPeriod() {
        return paybackPeriod;
    }

    public void setPaybackPeriod(String paybackPeriod) {
        this.paybackPeriod = paybackPeriod;
    }

    public String getCostPerMonth() {
        return costPerMonth;
    }

    public void setCostPerMonth(String costPerMonth) {
        this.costPerMonth = costPerMonth;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getAnnualPercentageRate() {
        return annualPercentageRate;
    }

    public void setAnnualPercentageRate(String annualPercentageRate) {
        this.annualPercentageRate = annualPercentageRate;
    }
}
