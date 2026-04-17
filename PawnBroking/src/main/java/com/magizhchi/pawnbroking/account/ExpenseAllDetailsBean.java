/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class ExpenseAllDetailsBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sExpenseDate;
    private final SimpleStringProperty sExpenseId;
    private final SimpleStringProperty sScreenName;
    private final SimpleStringProperty sReason;
    private final SimpleStringProperty sAmount;
    private final SimpleStringProperty sCreatedUser;
    
    public ExpenseAllDetailsBean(String sSlNo, String sExpenseDate, 
            String sExpenseId, String sScreenName, String sReason, String sAmount, 
            String sCreatedUser) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sExpenseDate = new SimpleStringProperty(sExpenseDate);
        this.sExpenseId = new SimpleStringProperty(sExpenseId);
        this.sScreenName = new SimpleStringProperty(sScreenName);
        this.sReason = new SimpleStringProperty(sReason);
        this.sAmount = new SimpleStringProperty(sAmount);
        this.sCreatedUser = new SimpleStringProperty(sCreatedUser);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public String getSExpenseDate() {
        return sExpenseDate.get();
    }

    public String getSExpenseId() {
        return sExpenseId.get();
    }

    public String getSScreenName() {
        return sScreenName.get();
    }

    public String getSReason() {
        return sReason.get();
    }

    public String getSAmount() {
        return sAmount.get();
    }

    public String getSCreatedUser() {
        return sCreatedUser.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
    }
    
    public void setSExpenseDate(String sExpenseDate) {
        this.sExpenseDate.set(sExpenseDate);
    }
        
    public void setSExpenseId(String sExpenseId) {
        this.sExpenseId.set(sExpenseId);
    }
    
    public void setSScreenName(String sScreenName) {
        this.sScreenName.set(sScreenName);
    }

    public void setSReason(String sReason) {
        this.sReason.set(sReason);
    }
        
    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }

    public void setSCreatedUser(String sCreatedUser) {
        this.sCreatedUser.set(sCreatedUser);
    }   
}
