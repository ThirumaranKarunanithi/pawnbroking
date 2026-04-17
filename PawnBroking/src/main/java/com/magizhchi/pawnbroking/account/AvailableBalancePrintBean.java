package com.magizhchi.pawnbroking.account;

/**
 *
 * @author Tiru
 */
public class AvailableBalancePrintBean {
    
    private double dRupee;
    private int dNumberOfNotes;
    private double dTotalAmount;

    public AvailableBalancePrintBean(double dRupee, int dNumberOfNotes, double dTotalAmount) {
        this.dRupee = dRupee;
        this.dNumberOfNotes = dNumberOfNotes;
        this.dTotalAmount = dTotalAmount;
    }

    public double getdRupee() {
        return dRupee;
    }

    public void setdRupee(double dRupee) {
        this.dRupee = dRupee;
    }

    public int getdNumberOfNotes() {
        return dNumberOfNotes;
    }

    public void setdNumberOfNotes(int dNumberOfNotes) {
        this.dNumberOfNotes = dNumberOfNotes;
    }

    public double getdTotalAmount() {
        return dTotalAmount;
    }

    public void setdTotalAmount(double dTotalAmount) {
        this.dTotalAmount = dTotalAmount;
    }
    
    
}
