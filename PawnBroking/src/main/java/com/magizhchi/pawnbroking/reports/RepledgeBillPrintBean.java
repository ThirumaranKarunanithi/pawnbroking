/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

/**
 *
 * @author Tiru
 */
public class RepledgeBillPrintBean {
    
    private String repledgeName;
    private String repledgeBillNumber;
    private String openedDate;
    private String companyBillNumber;
    private String amount;
    private String status;

    public RepledgeBillPrintBean() {}
    
    public RepledgeBillPrintBean(String repledgeName, String repledgeBillNumber, String openedDate, String companyBillNumber, String amount, String status) {
        this.repledgeName = repledgeName;
        this.repledgeBillNumber = repledgeBillNumber;
        this.openedDate = openedDate;
        this.companyBillNumber = companyBillNumber;
        this.amount = amount;
        this.status = status;
    }

    public String getRepledgeName() {
        return repledgeName;
    }

    public void setRepledgeName(String repledgeName) {
        this.repledgeName = repledgeName;
    }

    public String getRepledgeBillNumber() {
        return repledgeBillNumber;
    }

    public void setRepledgeBillNumber(String repledgeBillNumber) {
        this.repledgeBillNumber = repledgeBillNumber;
    }

    public String getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(String openedDate) {
        this.openedDate = openedDate;
    }

    public String getCompanyBillNumber() {
        return companyBillNumber;
    }

    public void setCompanyBillNumber(String companyBillNumber) {
        this.companyBillNumber = companyBillNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
}
