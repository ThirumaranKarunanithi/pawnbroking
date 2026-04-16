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
public class ExpenseIncomePrintVals {
    
    private String reason;
    private double amount;

    public ExpenseIncomePrintVals() {}
    
    public ExpenseIncomePrintVals(String reason, double amount) {
        this.reason = reason;
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    
}
