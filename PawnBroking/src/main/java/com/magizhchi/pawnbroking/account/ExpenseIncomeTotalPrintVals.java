/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

/**
 *
 * @author Tiru
 */
public class ExpenseIncomeTotalPrintVals {
    
    private String operation;
    private int count;
    private double amount;

    public ExpenseIncomeTotalPrintVals() {}
    
    public ExpenseIncomeTotalPrintVals(String operation, int count, double amount) {
        this.operation = operation;
        this.count = count;
        this.amount = amount;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    
}
