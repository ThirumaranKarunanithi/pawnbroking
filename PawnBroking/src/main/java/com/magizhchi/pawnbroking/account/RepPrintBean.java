/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

/**
 *
 * @author Tiru
 */
public class RepPrintBean {
    
    private String SOperation;
    private String SBillNumber;
    private String SOpeningDate;
    private String SRepBillNumber;
    private String SRepName;
    private double DAmount;
    private double DInterest;
    private double DTotalInterested;

    public RepPrintBean getEmptyValObject() {
            
        SOperation = "";
        SBillNumber = "";
        SOpeningDate = "";
        SRepBillNumber = "";
        SRepName = "";
        DAmount = 0;
        DInterest = 0;
        DTotalInterested = 0;   
        return this;
    }
    
    public double getDTotalInterested() {
        return DTotalInterested;
    }

    public void setDTotalInterested(double DTotalInterested) {
        this.DTotalInterested = DTotalInterested;
    }

    public double getDInterest() {
        return DInterest;
    }

    public void setDInterest(double DInterest) {
        this.DInterest = DInterest;
    }

    public double getDTotInterest() {
        return DTotInterest;
    }

    public void setDTotInterest(double DTotInterest) {
        this.DTotInterest = DTotInterest;
    }
    private double DTotInterest;

    public double getDAmount() {
        return DAmount;
    }

    public void setDAmount(double DAmount) {
        this.DAmount = DAmount;
    }

    public RepPrintBean() {
    
    }

    public String getSOperation() {
        return SOperation;
    }

    public void setSOperation(String SOperation) {
        this.SOperation = SOperation;
    }

    public String getSBillNumber() {
        return SBillNumber;
    }

    public void setSBillNumber(String SBillNumber) {
        this.SBillNumber = SBillNumber;
    }

    public String getSOpeningDate() {
        return SOpeningDate;
    }

    public void setSOpeningDate(String SOpeningDate) {
        this.SOpeningDate = SOpeningDate;
    }

    public String getSRepBillNumber() {
        return SRepBillNumber;
    }

    public void setSRepBillNumber(String SRepBillNumber) {
        this.SRepBillNumber = SRepBillNumber;
    }

    public String getSRepName() {
        return SRepName;
    }

    public void setSRepName(String SRepName) {
        this.SRepName = SRepName;
    }

}
