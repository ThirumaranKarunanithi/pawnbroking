/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.noticegeneration;

/**
 *
 * @author Tiru
 */
public class PanelPrintBean {
    
    private String SLNO;
    private String SBillNumber;
    private String SCustomerDetails;

    public PanelPrintBean() {
    }

    
    public PanelPrintBean(String SLNO, String SBillNumber, String SCustomerDetails) {
        this.SLNO = SLNO;
        this.SBillNumber = SBillNumber;
        this.SCustomerDetails = SCustomerDetails;
    }        

    public String getSLNO() {
        return SLNO;
    }

    public void setSLNO(String SLNO) {
        this.SLNO = SLNO;
    }

    public String getSBillNumber() {
        return SBillNumber;
    }

    public void setSBillNumber(String SBillNumber) {
        this.SBillNumber = SBillNumber;
    }

    public String getSCustomerDetails() {
        return SCustomerDetails;
    }

    public void setSCustomerDetails(String SCustomerDetails) {
        this.SCustomerDetails = SCustomerDetails;
    }
    
    
}
