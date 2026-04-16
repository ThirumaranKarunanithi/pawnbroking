/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rebillmapper;

import com.magizhchi.pawnbroking.account.TodaysAccountJewelRepledgeBean;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class GoldRebillMapperController implements Initializable {

    
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
    @FXML
    private ComboBox<String> cbStatusTo;
    @FXML
    private Tab tabRebiledMultiple;
    @FXML
    private HBox nodeAddToFilter1;
    @FXML
    private HBox hbClosingBillNumber;
    @FXML
    private TextField txtClosingBillNumber;
    @FXML
    private HBox hbAmount;
    @FXML
    private TextField txtMultiRebilledOpeningBillNumber;
    @FXML
    private Button btRemoveFromTable;
    @FXML
    private TableView<AllDetailsBean> tbClosingList;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRepList;
    @FXML
    private Button btMultiRebilledSave;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            try {
                dbOp = new ReBillMapperDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtFromBillNumber.setText(billRowAndNumber[1]);
                txtToBillNumber.setText(billRowAndNumber[1]);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tbClosingList.setRowFactory(tv -> new TableRow<AllDetailsBean>() {
            @Override
            public void updateItem(AllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle(Util.getStyle("#000000", "#CCCCCC").toString());
                } else if (item.getSRepledgeBillId() != null && !item.getSRepledgeBillId().isEmpty()) {
                    setStyle(Util.getStyle("#000000", "#FCBAEF").toString());
                } else {
                    setStyle("");
                }
            }
        });
        
        Platform.runLater(() -> {
            txtFromBillNumber.requestFocus();
            txtFromBillNumber.positionCaret(txtFromBillNumber.getText().length());
        });                
    }    

    @FXML
    private void txtFromBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtFromBillNumber.getText();
        clearFromAll();
        txtFromBillNumber.setText(sBillNumber);
        try {
            HashMap<String, String> headerValues = dbOp.getClosedBillingValues(sBillNumber, "GOLD");    
            if(headerValues != null)
            {
                HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), "GOLD");
                setAllFromHeaderValuesToFields(headerValues, repledgeValues);
                sLastSelectedFromId = sBillNumber;
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
                clearFromAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
            HashMap<String, String> headerValues = dbOp.getAllOpenedBillingValues(sBillNumber, "GOLD");    
            if(headerValues != null)
            {
                HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), "GOLD");
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
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
        
        String sBillOpeningDate = txtToOpenedDate.getText();
        String sBillClosingingDate = txtFromClosedDate.getText();
        
        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, 
                sBillOpeningDate))
        {
            if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sBillOpeningDate, sBillClosingingDate))
            {
                try {
                    String sFromBillNumber = txtFromBillNumber.getText();
                    String sRepledgeBillId = txtFromRepledgeBillId.getText();
                    String sToBillNumber = txtToBillNumber.getText();
                    String sToStatus;
                    String sFromStatus = cbStatusTo.getValue().toUpperCase();

                    if(sLastSelectedFromId != null && sLastSelectedToId != null) {
                        if(sRepledgeBillId != null && sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)) {
                            sToStatus = "LOCKED";
                        } else {
                            sToStatus = "OPENED";
                        }
                        dbOp.updateEmptyFromBill(sFromBillNumber, "GOLD");
                        dbOp.updateFromBill(sToBillNumber, sFromBillNumber, sRepledgeBillId, sToStatus, "GOLD");
                        dbOp.updateEmptyToBill(sToBillNumber, "GOLD");
                        dbOp.updateToBill(sFromBillNumber, sToBillNumber, sFromStatus, "GOLD");
                        PopupUtil.showInfoAlert("Bill " + sLastSelectedFromId +" is mapped to " + sLastSelectedToId 
                                + " as rebill successfully.");  
                        clearAll();
                    } else {
                        PopupUtil.showErrorAlert("Both from and to bill number was not chosen to map.");
                    }

                } catch (Exception ex) {
                    Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert("Rebilled-To Bill opening date and "
                        + "Rebilled-From Bill closing date cannot be different.");
            }                         
        } else {
            PopupUtil.showErrorAlert("Sorry this bill opening date account was closed.");
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
    
        try {
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
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtFromBillNumber.setText(billRowAndNumber[1]);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Platform.runLater(() -> {
            txtFromBillNumber.requestFocus();
            txtFromBillNumber.positionCaret(txtFromBillNumber.getText().length());
        });                        
    }
    
    public void clearToAll() {

        try {
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
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtToBillNumber.setText(billRowAndNumber[1]);        
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Platform.runLater(() -> {
            txtToBillNumber.requestFocus();
            txtToBillNumber.positionCaret(txtToBillNumber.getText().length());
        });                        
    }

    @FXML
    private void cbStatusToOnAction(ActionEvent event) {
    }

    @FXML
    private void txtClosingBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtClosingBillNumber.getText();
        String sMaterial = "GOLD";
        
        String val = txtClosingBillNumber.getText();
        if(val != null & !val.isEmpty()) {
            
            String[] billVals = val.split("-");                            
            String[] billRowAndNumber = null;
            
            try {               
                billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(billRowAndNumber != null && billVals.length > 1) {
                int index = billRowAndNumber[1].length();
                sBillNumber = billVals[0].substring(index);
            } else {
                sBillNumber = billVals[0];
            }            
            txtClosingBillNumber.setText(sBillNumber);
            
            if(billVals.length > 1 && billVals[1] != null && !billVals[1].isEmpty()) {
                if("G".equals(billVals[1])) {
                    sMaterial = "GOLD";
                } else if("S".equals(billVals[1])) {
                    PopupUtil.showInfoAlert("Silver bill cannot be added to gold multi rebill.");
                    initializeTxtClosingBillNumber();
                    return;
                }
            }            
        }        
        
        try {
            
            for(AllDetailsBean bean : tbClosingList.getItems()) {
                if(bean != null && bean.getSBillNumber().equals(sBillNumber)) {
                    PopupUtil.showErrorAlert("Same bill number already exists in list.");
                    initializeTxtClosingBillNumber();
                    return;
                }
            }
            
            HashMap<String, String> headerValues = dbOp.getAllClosedBillingValues(sBillNumber, sMaterial);                               

            if(headerValues != null)
            {
                setCloseHeaderValuesToFields(headerValues);                
                initializeTxtClosingBillNumber();
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }
    
    public void setCloseHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        try {            
            
            tbClosingList.getItems().add(new AllDetailsBean("GOLD", "BILL CLOSING", headerValues.get("BILL_NUMBER"), 
                    headerValues.get("OPENING_DATE"), headerValues.get("CLOSING_DATE"), headerValues.get("CUSTOMER_NAME"),
                    headerValues.get("AMOUNT"), headerValues.get("STATUS"), headerValues.get("REPLEDGE_BILL_ID")));                        

            
            if(headerValues.get("REPLEDGE_BILL_ID") != null && !headerValues.get("REPLEDGE_BILL_ID").contains(",")) {
                doMultiRebilledRepledgeTableWork(headerValues.get("REPLEDGE_BILL_ID"));
            } else {
                if(headerValues.get("REPLEDGE_BILL_ID") != null) {
                    for(String repBId : headerValues.get("REPLEDGE_BILL_ID").split(",")) {
                        HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(repBId, "GOLD");
                        if(repledgeValues != null) {
                            String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                            String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                            String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                            double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                            String sCBillNumber = repledgeValues.get("BILL_NUMBER");
                            String sDate = repledgeValues.get("OPENING_DATE");
                            double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                            String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                            String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                            tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                                    sRepledgeDate, 
                                    dRepledgeAmount, sCBillNumber, sDate, dAmount, sStatus, sRepBillId, ""));
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initializeTxtClosingBillNumber() {
        try {
            txtClosingBillNumber.setText("");
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtClosingBillNumber.setText(billRowAndNumber[1]);
            }
            Platform.runLater(() -> {
                txtClosingBillNumber.requestFocus();
                txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());
            });
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeTxtOpeningBillNumber() {
        try {
            txtMultiRebilledOpeningBillNumber.setText("");
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtMultiRebilledOpeningBillNumber.setText(billRowAndNumber[1]);
            }
            Platform.runLater(() -> {
                txtMultiRebilledOpeningBillNumber.requestFocus();
                txtMultiRebilledOpeningBillNumber.positionCaret(txtMultiRebilledOpeningBillNumber.getText().length());
            });
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void doMultiRebilledRepledgeTableWork(String sRepledgeBillId) {
        try {
            HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(sRepledgeBillId, "GOLD");
            if(repledgeValues != null) {
                String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                String sBillNumber = repledgeValues.get("BILL_NUMBER");
                String sDate = repledgeValues.get("OPENING_DATE");
                double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                        sRepledgeDate, 
                        dRepledgeAmount, sBillNumber, sDate, dAmount, sStatus, sRepledgeBillId, ""));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @FXML
    private void txtMultiRebilledOpeningBillNumberOnAction(ActionEvent event) {
        try {
            for(AllDetailsBean bean : tbClosingList.getItems()) {
                if(bean.getSOperation().equals("BILL OPENING")) {
                    PopupUtil.showErrorAlert("Only one Bill Opening can be done in Rebilled-Multiple.");
                    return;
                }
            }
            
            String sBillNumber = txtMultiRebilledOpeningBillNumber.getText();
            HashMap<String, String> headerValues = dbOp.getAllOpenedBillingValues(sBillNumber, "GOLD");
            
            if(headerValues != null)
            {
                tbClosingList.getItems().add(new AllDetailsBean("GOLD", "BILL OPENING", headerValues.get("BILL_NUMBER"),
                        headerValues.get("OPENING_DATE"), "", headerValues.get("CUSTOMER_NAME"),
                        headerValues.get("AMOUNT"), headerValues.get("STATUS"), headerValues.get("REPLEDGE_BILL_ID")));
                
                if(headerValues.get("REPLEDGE_BILL_ID") != null && !headerValues.get("REPLEDGE_BILL_ID").contains(",")) {
                    doMultiRebilledRepledgeTableWork(headerValues.get("REPLEDGE_BILL_ID"));
                } else {
                    if(headerValues.get("REPLEDGE_BILL_ID") != null) {
                        for(String repBId : headerValues.get("REPLEDGE_BILL_ID").split(",")) {
                            HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(repBId, "GOLD");
                            if(repledgeValues != null) {
                                String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                                String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                                String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                                double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                                String sCBillNumber = repledgeValues.get("BILL_NUMBER");
                                String sDate = repledgeValues.get("OPENING_DATE");
                                double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                                String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                                String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                                tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                                        sRepledgeDate, 
                                        dRepledgeAmount, sCBillNumber, sDate, dAmount, sStatus, sRepBillId, ""));
                            }
                        }
                    }
                }
                tbClosingList.getItems().add(null);
                tbRepList.getItems().add(null);                
                
                txtClosingBillNumber.setDisable(false);
                txtMultiRebilledOpeningBillNumber.setDisable(true);
                initializeTxtClosingBillNumber();
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void txtAmountOnType(KeyEvent event) {
    }

    @FXML
    private void btRemoveFromTableClicked(ActionEvent event) {
        tbClosingList.getItems().removeAll(tbClosingList.getItems());
        tbRepList.getItems().removeAll(tbRepList.getItems());
        txtClosingBillNumber.setText("");
        txtClosingBillNumber.setDisable(true);
        txtMultiRebilledOpeningBillNumber.setDisable(false);
        initializeTxtOpeningBillNumber();
    }

    @FXML
    private void tbClosingListOnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void tbRNYDeliveredOnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void tabRebilledMultipleSelectionChanged(Event event) {
        initializeTxtOpeningBillNumber();
        txtClosingBillNumber.setDisable(true);
    }

    @FXML
    private void btMultiRebilledSaveClicked(ActionEvent event) {
        
        try {
            StringBuilder fromSb = new StringBuilder();
            StringBuilder repBillIdSb = new StringBuilder();
            String sToBillNumber = null;
            String sFromStatus = "REBILLED-MULTIPLE";
            
            for(AllDetailsBean bean : tbClosingList.getItems()) {
                if(bean != null) {
                    if(bean.getSOperation().equals("BILL CLOSING")) {

                        dbOp.updateEmptyFromBill(bean.getSBillNumber(), "GOLD");
                        dbOp.updateToBill(bean.getSBillNumber(), sToBillNumber, sFromStatus, "GOLD");
                        
                        fromSb.append(bean.getSBillNumber());
                        fromSb.append(",");
                        
                        if(bean.getSRepledgeBillId() != null 
                                && bean.getSRepledgeBillId().contains(CommonConstants.REP_BILL_ID_PREFIX)) {
                            repBillIdSb.append(bean.getSRepledgeBillId());
                            repBillIdSb.append(",");
                        }
                    } else if(bean.getSOperation().equals("BILL OPENING")) {                    
                        sToBillNumber = bean.getSBillNumber();
                        dbOp.updateEmptyToBill(sToBillNumber, "GOLD");
                    }
                }
            }
                        
            fromSb.deleteCharAt(fromSb.lastIndexOf(","));
            if(repBillIdSb.length() > 0) {
                repBillIdSb.deleteCharAt(repBillIdSb.lastIndexOf(","));
            }
            
            String sFromBillNumber = fromSb.toString();
            String sRepledgeBillId = (repBillIdSb.length() > 0) ? repBillIdSb.toString() : null;            
            String sToStatus;            

            if(sToBillNumber != null && sFromBillNumber != null) {
                if(sRepledgeBillId != null && sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)) {
                    sToStatus = "LOCKED";
                } else {
                    sToStatus = "OPENED";
                }                
                dbOp.updateFromBill(sToBillNumber, sFromBillNumber, sRepledgeBillId, sToStatus, "GOLD");                                
                PopupUtil.showInfoAlert("Bills " + sFromBillNumber +" is mapped to " + sToBillNumber 
                        + " as multi rebill successfully.");  
                
                btRemoveFromTableClicked(null);
            } else {
                PopupUtil.showErrorAlert("Both from and to bill number was not chosen to map.");
            }

        } catch (Exception ex) {
            Logger.getLogger(GoldRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
