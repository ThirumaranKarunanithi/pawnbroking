/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillclosing;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author tiruk
 */
public class TotalPayAmountController implements Initializable {

    String customerName; 
    String closingDate;
    String billNumber;
    String closingAmount;
                    
    @FXML
    private Label lbDate;
    @FXML
    private Label lbCustomerName;
    @FXML
    private Label lbBillNumber;
    @FXML
    private Label lbClosingAmount;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void setAmountAndDate(String customerName, 
            String closingDate, 
            String billNumber, 
            String closingAmount)
    {
        lbCustomerName.setText(customerName);
        lbDate.setText(closingDate);
        lbBillNumber.setText(billNumber);
        lbClosingAmount.setText(closingAmount);
    }
}
