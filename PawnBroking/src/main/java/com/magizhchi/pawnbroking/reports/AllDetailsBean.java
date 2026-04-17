/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.Util;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private ReportDBOperation dbOp;
    
    private SimpleStringProperty sMonths;
    private SimpleIntegerProperty sOpenBills;
    private SimpleStringProperty sOpenCapAmt;
    private SimpleIntegerProperty sCloseBills;
    private SimpleDoubleProperty sCloseAmount;
    private SimpleIntegerProperty sStockBills;    
    private SimpleDoubleProperty sStockAmount;
    private SimpleIntegerProperty sBillsEarned;
    private SimpleDoubleProperty sAmountEarned;
    private SimpleDoubleProperty sProfit;
    private SimpleBooleanProperty bChecked;

    public AllDetailsBean(String sMonths, 
            int sOpenBills, String sOpenCapAmt, 
            int sCloseBills, double sCloseAmount, 
            int sStockBills, double sStockAmount, 
            int sBillsEarned, double sAmountEarned, 
            double sProfit, boolean bChecked) {
        this.sMonths = new SimpleStringProperty(sMonths);
        this.sOpenBills = new SimpleIntegerProperty(sOpenBills);
        this.sOpenCapAmt = new SimpleStringProperty(sOpenCapAmt);
        this.sCloseBills = new SimpleIntegerProperty(sCloseBills);
        this.sCloseAmount = new SimpleDoubleProperty(sCloseAmount);
        this.sStockBills = new SimpleIntegerProperty(sStockBills);
        this.sStockAmount = new SimpleDoubleProperty(sStockAmount);
        this.sBillsEarned = new SimpleIntegerProperty(sBillsEarned);
        this.sAmountEarned = new SimpleDoubleProperty(sAmountEarned);
        this.bChecked = new SimpleBooleanProperty(bChecked);
        this.sProfit = new SimpleDoubleProperty(sProfit);
    }        

    public String getSMonths() {
        return sMonths.get();
    }

    public void setSMonths(String sMonths) {
        this.sMonths.set(sMonths);
    }

    public int getSOpenBills() {
        return sOpenBills.get();
    }

    public void setSOpenBills(int sOpenBills) {
        this.sOpenBills.set(sOpenBills);
    }

    public String getSOpenCapAmt() {        
        return Util.format(Double.parseDouble(sOpenCapAmt.get()));
    }

    public void setSOpenCapAmt(String sOpenCapAmt) {
        this.sOpenCapAmt.set(sOpenCapAmt);
    }

    public int getSCloseBills() {
        return sCloseBills.get();
    }

    public void setSCloseBills(int sCloseBills) {
        this.sCloseBills.set(sCloseBills);
    }

    public String getSCloseAmount() {
        return Util.format(sCloseAmount.get());
    }

    public void setSCloseAmount(double sCloseAmount) {
        this.sCloseAmount.set(sCloseAmount);
    }

    public int getSStockBills() {
        return sStockBills.get();
    }

    public void setSStockBills(int sStockBills) {
        this.sStockBills.set(sStockBills);
    }

    public String getSStockAmount() {
        return Util.format(sStockAmount.get());
    }

    public void setSStockAmount(double sStockAmount) {
        this.sStockAmount.set(sStockAmount);
    }

    public int getSBillsEarned() {
        return sBillsEarned.get();
    }

    public void setSBillsEarned(int sBillsEarned) {
        this.sBillsEarned.set(sBillsEarned);
    }

    public String getSAmountEarned() {
        return Util.format(sAmountEarned.get());
    }

    public void setSAmountEarned(double sAmountEarned) {
        this.sAmountEarned.set(sAmountEarned);
    }
    
    public String getSProfit() {
        return Util.format(sProfit.get());
    }

    public void setSProfit(double sProfit) {
        this.sProfit.set(sProfit);
    }
    
    public boolean isBChecked() {
        return this.bCheckedProperty().get();
    }

    public SimpleBooleanProperty bCheckedProperty() {
        return bChecked;
    }

    public boolean getBCheckedProperty() {
        return this.bCheckedProperty().get();
    }    
    
    public void setBChecked(boolean bChecked) {
        this.bChecked.set(bChecked);
    }
    
}
