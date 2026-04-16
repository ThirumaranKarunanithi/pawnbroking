/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rebillmapper;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class SilverRebillMapperController implements Initializable {

    ReBillMapperDBOperation dbOp;
    private String sLastSelectedFromId = null;
    private String sLastSelectedToId = null;
    public Stage dialog;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private TextField txtFromOpenedDate;
    @FXML
    private TextField txtFromClosedDate;
    @FXML
    private TextField txtFromCustomerName;
    @FXML
    private TextField txtFromAmount;
    @FXML
    private TextField txtFromStatus;
    @FXML
    private TextField txtToBillNumber;
    @FXML
    private TextField txtToCustomerName;
    @FXML
    private TextField txtToAmount;
    @FXML
    private TextField txtToStatus;
    @FXML
    private Label lbScreenMessage;
    @FXML
    private Button btSaveBill;
    @FXML
    private Button btClearAll;
    @FXML
    private TextField txtFromRepledgeBillId;
    @FXML
    private TextField txtFromRepledgeName;
    @FXML
    private TextField txtFromRepledgeBillNumber;
    @FXML
    private TextField txtFromCompanyBillNumber;
    @FXML
    private TextField txtFromRepledgeBillAmount;
    @FXML
    private TextField txtReBilledFrom;
    @FXML
    private TextField txtReBilledTo;
    @FXML
    private TextField txtToRepledgeBillId;
    @FXML
    private TextField txtToRepledgeName;
    @FXML
    private TextField txtToRepledgeBillNumber;
    @FXML
    private TextField txtToCompanyBillNumber;
    @FXML
    private TextField txtToRepledgeBillAmount;
    @FXML
    private TextField txtToOpenedDate;
    @FXML
    private TextField txtFromRepledgeBillStatus;
    @FXML
    private TextField txtToRepledgeBillStatus;
    @FXML
    private TextField txtFromBillNumber;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new ReBillMapperDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SilverRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void txtFromBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtFromBillNumber.getText();
        clearFromAll();
        txtFromBillNumber.setText(sBillNumber);
        try {
            HashMap<String, String> headerValues = dbOp.getClosedBillingValues(sBillNumber, "SILVER");    
            if(headerValues != null)
            {
                HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), "SILVER");
                setAllFromHeaderValuesToFields(headerValues, repledgeValues);
                sLastSelectedFromId = sBillNumber;
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
                clearFromAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllFromHeaderValuesToFields(HashMap<String, String> headerValues, HashMap<String, String> repledgeValues)
    {
        txtFromBillNumber.setText(headerValues.get("BILL_NUMBER"));
        txtFromOpenedDate.setText(headerValues.get("OPENING_DATE"));
        txtFromClosedDate.setText(headerValues.get("CLOSING_DATE"));
        txtFromCustomerName.setText(headerValues.get("CUSTOMER_NAME"));
        txtFromAmount.setText(headerValues.get("AMOUNT"));
        txtFromStatus.setText(headerValues.get("STATUS"));
        txtReBilledTo.setText(headerValues.get("REBILLED_TO"));
        if(repledgeValues != null) {
            txtFromRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
            txtFromRepledgeName.setText(repledgeValues.get("REPLEDGE_NAME"));
            txtFromRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
            txtFromCompanyBillNumber.setText(repledgeValues.get("BILL_NUMBER"));
            txtFromRepledgeBillAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
            txtFromRepledgeBillStatus.setText(repledgeValues.get("REPLEDGE_STATUS"));
        }
    }
    
    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void txtToBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtToBillNumber.getText();
        clearToAll();
        txtToBillNumber.setText(sBillNumber);
        try {
            HashMap<String, String> headerValues = dbOp.getAllOpenedBillingValues(sBillNumber, "SILVER");    
            if(headerValues != null)
            {
                HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), "SILVER");
                if(repledgeValues!= null && repledgeValues.get("BILL_NUMBER").equals(sBillNumber)) {
                    PopupUtil.showErrorAlert("Bill number and the repledged company bill number is same. So cannot be mapped this type of bill.");
                    clearToAll();
                } else {
                    setAllToHeaderValuesToFields(headerValues, repledgeValues);
                    sLastSelectedToId = sBillNumber;
                }
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
                clearToAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllToHeaderValuesToFields(HashMap<String, String> headerValues, HashMap<String, String> repledgeValues)
    {
        txtToBillNumber.setText(headerValues.get("BILL_NUMBER"));
        txtToOpenedDate.setText(headerValues.get("OPENING_DATE"));
        txtToCustomerName.setText(headerValues.get("CUSTOMER_NAME"));
        txtToAmount.setText(headerValues.get("AMOUNT"));
        txtToStatus.setText(headerValues.get("STATUS"));
        txtReBilledFrom.setText(headerValues.get("REBILLED_FROM"));
        if(repledgeValues != null) {
            txtToRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
            txtToRepledgeName.setText(repledgeValues.get("REPLEDGE_NAME"));
            txtToRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
            txtToCompanyBillNumber.setText(repledgeValues.get("BILL_NUMBER"));
            txtToRepledgeBillAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
            txtToRepledgeBillStatus.setText(repledgeValues.get("REPLEDGE_STATUS"));
        }
    }
    
    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        try {
            String sFromBillNumber = txtFromBillNumber.getText();
            String sRepledgeBillId = txtFromRepledgeBillId.getText();
            String sToBillNumber = txtToBillNumber.getText();
            String sToStatus;
            String sFromStatus = "REBILLED";

            if(sLastSelectedFromId != null && sLastSelectedToId != null) {
                if(sRepledgeBillId != null && sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)) {
                    sToStatus = "LOCKED";
                } else {
                    sToStatus = "OPENED";
                }
                dbOp.updateEmptyFromBill(sFromBillNumber, "SILVER");
                dbOp.updateFromBill(sToBillNumber, sFromBillNumber, sRepledgeBillId, sToStatus, "SILVER");
                dbOp.updateEmptyToBill(sToBillNumber, "SILVER");
                dbOp.updateToBill(sFromBillNumber, sToBillNumber, sFromStatus, "SILVER");
                PopupUtil.showInfoAlert("Bill " + sLastSelectedFromId +" is mapped to " + sLastSelectedToId +" as rebill successfully.");  
                clearAll();
            } else {
                PopupUtil.showErrorAlert("Both from and to bill number was not chosen to map.");
            }

        } catch (Exception ex) {
            Logger.getLogger(SilverRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        
        clearAll();        
    }
    
    private void clearAll() {
        
        lbScreenMessage.setText("");
        txtReBilledTo.setText("");
        txtReBilledFrom.setText("");
        
        clearFromAll();
        clearToAll();
    }
    
    public void clearFromAll() {
    
        sLastSelectedFromId = null;
        
        txtFromBillNumber.setText("");
        txtFromOpenedDate.setText("");
        txtFromClosedDate.setText("");
        txtFromCustomerName.setText("");
        txtFromAmount.setText("");
        txtFromStatus.setText("");
        txtFromRepledgeBillId.setText("");
        txtFromRepledgeName.setText("");
        txtFromRepledgeBillNumber.setText("");
        txtFromCompanyBillNumber.setText("");
        txtFromRepledgeBillAmount.setText("");
        txtFromRepledgeBillStatus.setText("");        
    }
    
    public void clearToAll() {

        sLastSelectedToId = null;

        txtToBillNumber.setText("");
        txtToCustomerName.setText("");
        txtToAmount.setText("");
        txtToStatus.setText("");
        txtToRepledgeBillId.setText("");
        txtToRepledgeName.setText("");
        txtToRepledgeBillNumber.setText("");
        txtToCompanyBillNumber.setText("");
        txtToRepledgeBillAmount.setText("");
        txtToOpenedDate.setText("");
        txtToRepledgeBillStatus.setText("");        
    }
}
