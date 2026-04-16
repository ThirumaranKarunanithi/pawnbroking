/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billcalculator;

/**
 *
 * @author Tiru
 */
public class CompPrintBean {
    
    private String SMaterialType;
    private String SBillNumber;
    private String SJewelName;
    private String SOpeningDate;
    private String SNoOfMonths;
    private double DAmount;
    private double DInterest;
    private double DTotalInterested;
    
    public CompPrintBean getEmptyValObject() {
            
        SMaterialType = "";
        SBillNumber = "";
        SOpeningDate = "";
        SJewelName = "";
        SNoOfMonths = "0";
        DAmount = 0;
        DInterest = 0;
        DTotalInterested = 0;   
        return this;
    }
    
    
}
