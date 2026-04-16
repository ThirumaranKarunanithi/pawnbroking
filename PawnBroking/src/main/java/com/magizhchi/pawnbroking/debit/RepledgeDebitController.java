/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.debit;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RepledgeDebitController implements Initializable {

    private RepledgeDebitDBOperation dbOp;
    private String sLastSelectedId = null;
    private String sROLastSelectedId = null;
    public DataTable dtRepledgeNames;
    private DataTable otherSettingValues;
    
    private final String BILLING_SCREEN_NAME = "REPLEDGE_BILL_DEBIT";
    private final String OTHER_SCREEN_NAME = "REPLEDGE_OTHER_DEBIT";
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private TextField txtRODebitId;
    @FXML
    private TextField txtROCompanyId;
    @FXML
    private TextField txtROCompanyName;
    @FXML
    private TextField txtRORepledgeId;
    @FXML
    private ComboBox<String> cbRORepledgeName;
    @FXML
    private DatePicker dpRODebittedDate;
    @FXML
    private ComboBox<String> cbROExpenseType;
    @FXML
    private TextField txtROName;
    @FXML
    private TextArea txtROReason;
    @FXML
    private TextArea txtRONote;
    @FXML
    private TextField txtROInvoiceNo;
    @FXML
    private TextField txtROInvoiceAmount;
    @FXML
    private TextField txtRODebittedAmount;
    @FXML
    private Label lbMessage1;
    @FXML
    private Button btROSaveBill;
    @FXML
    private Button btROClearAll;
    @FXML
    private TextField txtDebitId;
    @FXML
    private TextField txtCompanyId;
    @FXML
    private TextField txtCompanyName;
    @FXML
    private DatePicker dpDebittedDate;
    @FXML
    private TextField txtRepledgeId;
    @FXML
    private ComboBox<String> cbRepledgeName;
    @FXML
    private TextField txtRepledgeBillNumber;
    @FXML
    private TextField txtBillNumber;
    @FXML
    private TextField txtCompanyBillStatus;
    @FXML
    private TextField txtCompanyOpenedDate;
    @FXML
    private TextField txtCompanyClosedDate;
    @FXML
    private TextField txtCompanyBillAmount;
    @FXML
    private TextField txtItems;
    @FXML
    private TextField txtRepledgeBillStatus;
    @FXML
    private TextField txtRepledgeOpenedDate;
    @FXML
    private TextField txtRepledgeClosedDate;
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtRepledgeBillAmount;
    @FXML
    private TextField txtToGetAmount;
    @FXML
    private TextField txtGotAmount;
    @FXML
    private TextField txtToGiveAmount;
    @FXML
    private TextField txtGivenAmount;
    @FXML
    private TextField txtDebittedAmount;
    @FXML
    private Label lbMessage;
    @FXML
    private Button btSaveBill;
    @FXML
    private Button btClearAll;
    @FXML
    private TextField txtRepBillId;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btUpdateBill;
    @FXML
    private HBox hSaveModeButtons1;
    @FXML
    private ToggleGroup ViewModeGroup1;
    @FXML
    private Button btROUpdateBill;
    @FXML
    private ToggleButton tgROOn;
    @FXML
    private ToggleButton tgROOff;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            try {
                dbOp = new RepledgeDebitDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }

            txtRODebitId.setText(dbOp.getId(OTHER_SCREEN_NAME));
            txtROCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtROCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpRODebittedDate.setValue(LocalDate.now());

            txtDebitId.setText(dbOp.getId(BILLING_SCREEN_NAME));
            txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpDebittedDate.setValue(LocalDate.now());
            
            setRepledgeNames();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            otherSettingValues = dbOp.getOtherSettingsValues(); 
            
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_EXPENSES_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
                btROSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
                btROSaveBill.setDisable(true);
            }
            
            try {
                /*if(dbOp.allowToChangeDate()) {
                    dpDebittedDate.setMouseTransparent(false);
                    dpDebittedDate.setFocusTraversable(true);                
                    dpRODebittedDate.setMouseTransparent(false);
                    dpRODebittedDate.setFocusTraversable(true);                
                } else {
                    dpDebittedDate.setMouseTransparent(true);
                    dpDebittedDate.setFocusTraversable(false);                
                    dpRODebittedDate.setMouseTransparent(true);
                    dpRODebittedDate.setFocusTraversable(false);                
                }*/
                closeDateRestriction();
                closeRODateRestriction();
                
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    

    public void setRepledgeNames() {
        
        try {
            cbRORepledgeName.getItems().removeAll(cbRORepledgeName.getItems());
            cbRepledgeName.getItems().removeAll(cbRepledgeName.getItems());
            dtRepledgeNames = dbOp.getAllRepledgeNames();
            for(int i=0; i<dtRepledgeNames.getRowCount(); i++) {          
                cbRORepledgeName.getItems().add(dtRepledgeNames.getRow(i).getColumn(1).toString());
                cbRepledgeName.getItems().add(dtRepledgeNames.getRow(i).getColumn(1).toString());
            }
            txtRORepledgeId.setText(dtRepledgeNames.getRow(0).getColumn(0).toString());
            txtRepledgeId.setText(dtRepledgeNames.getRow(0).getColumn(0).toString());
            cbRORepledgeName.getSelectionModel().select(0);
            cbRepledgeName.getSelectionModel().select(0);
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }
    
    @FXML
    private void capitalizeCharOnType(KeyEvent e) {
        Platform.runLater(() -> {
            TextField txt_TextField = (TextField) e.getSource();
            int caretPos = txt_TextField.getCaretPosition();
            txt_TextField.setText(txt_TextField.getText().toUpperCase());
            txt_TextField.positionCaret(caretPos);
        });
    }

    @FXML
    private void capitalizeCharOnPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                TextField txt_TextField = (TextField) e.getSource();
                int caretPos = txt_TextField.getCaretPosition();
                txt_TextField.setText(txt_TextField.getText().toUpperCase());
                txt_TextField.positionCaret(caretPos);
            });
        }
    }
    
    @FXML
    private void cbRORepledgeNameOnAction(ActionEvent event) {
        
        int index = cbRORepledgeName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtRepledgeNames.getRow(index).getColumn(0).toString();                
        txtRORepledgeId.setText(sRepledgeId);
        try {
            closeRODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void dpRODebittedDateTextChanged(ActionEvent event) {
        try {
            closeRODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.-".contains(e.getCharacter()))){ 
            e.consume();
        }                        
    }

    @FXML
    private void btROSaveBillClicked(ActionEvent event) {

        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpRODebittedDate.getValue());
        
        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
        {
        
            String sId = txtRODebitId.getText();
            String sRepledgeId = txtRORepledgeId.getText();
            String sRepledgeName = cbRORepledgeName.getValue().toUpperCase();
            String sExpenseType = cbROExpenseType.getValue();
            String sName = txtROName.getText().toUpperCase();
            String sReason = txtROReason.getText().toUpperCase();
            String sNote = txtRONote.getText().toUpperCase();
            String sInvoiceNo = txtROInvoiceNo.getText().toUpperCase();
            String sInvoiceAmount = txtROInvoiceAmount.getText().toUpperCase();
            String sDebittedAmount = txtRODebittedAmount.getText().toUpperCase();

            double dInvoiceAmount = Double.parseDouble(!("".equals(sInvoiceAmount))? sInvoiceAmount : "0");
            double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");  

            if(isValidROHeaderValues(sName, sReason, sDebittedAmount)) {
                try {
                    if(dbOp.saveROBillDebit(sId, sRepledgeId, sRepledgeName, sDebittedDate, sExpenseType, sName, sReason, sNote, sInvoiceNo, dInvoiceAmount, dDebittedAmount)) {
                        dbOp.setNextId(OTHER_SCREEN_NAME, CommonConstants.REP_DEBIT_RO_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.REP_DEBIT_RO_PREFIX, ""))+1));
                        PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" debitted in "+ sRepledgeName +" for "+sExpenseType+".");
                        txtRODebitId.setText(dbOp.getId(OTHER_SCREEN_NAME));
                        btROClearAllClicked(null);
                    } 
                } catch (Exception ex) {
                    Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                txtRODebittedAmount.requestFocus();
            }        
        } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
        }                     
    }

    public boolean isValidROHeaderValues(String sName, String sReason, String sDebittedAmount)
    {        
        if(!sName.isEmpty() && !sReason.isEmpty() && !sDebittedAmount.isEmpty()) {
            return Double.parseDouble(sDebittedAmount) > 0 || Double.parseDouble(sDebittedAmount) < 0;
        } else {
            return false;
        }
    }

    public boolean isValidHeaderValues(String sRepledgeId, String sRepledgeName, String sCompBillNumber, String sDebittedAmount)
    {        
        if(!sRepledgeId.isEmpty() && !sRepledgeName.isEmpty() 
                && !sCompBillNumber.isEmpty() && !sDebittedAmount.isEmpty()) {
            return Double.parseDouble(sDebittedAmount) > 0 || Double.parseDouble(sDebittedAmount) < 0;
        } else {
            return false;
        }
    }

    public boolean isValidHeaderValues(String sRepledgeId, double dDebittedAmt)
    {        
        if(!sRepledgeId.isEmpty() && dDebittedAmt > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    @FXML
    private void btROClearAllClicked(ActionEvent event) {
        clearEOAllHeader();
    }
    
    public void clearEOAllHeader()
    {
        dpRODebittedDate.setValue(LocalDate.now());
        txtROName.setText("");
        txtROReason.setText("");
        txtRONote.setText("");        
        txtROInvoiceNo.setText("");
        txtROInvoiceAmount.setText("");
        txtRODebittedAmount.setText("");
    }

    @FXML
    private void dpDebittedDateTextChanged(ActionEvent event) {
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cbRepledgeNameOnAction(ActionEvent event) {
        int index = cbRepledgeName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtRepledgeNames.getRow(index).getColumn(0).toString();                
        txtRepledgeId.setText(sRepledgeId);        
    }

    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtRepBillId.setText(headerValues.get("REPLEDGE_BILL_ID"));
        txtRepledgeId.setText(headerValues.get("REPLEDGE_ID"));
        cbRepledgeName.setValue(headerValues.get("REPLEDGE_NAME"));
        txtRepledgeBillNumber.setText(headerValues.get("REPLEDGE_BILL_NUMBER"));
        txtBillNumber.setText(headerValues.get("BILL_NUMBER"));
        txtCompanyBillStatus.setText(headerValues.get("STATUS"));
        txtCompanyOpenedDate.setText(headerValues.get("OPENING_DATE"));
        txtCompanyClosedDate.setText(headerValues.get("CLOSING_DATE"));
        txtCompanyBillAmount.setText(headerValues.get("AMOUNT"));
        txtItems.setText(headerValues.get("ITEMS"));
        txtRepledgeBillStatus.setText(headerValues.get("REPLEDGE_STATUS"));
        txtRepledgeOpenedDate.setText(headerValues.get("REPLEDGE_OPENING_DATE"));
        txtRepledgeClosedDate.setText(headerValues.get("REPLEDGE_CLOSING_DATE"));        
        //txtNote.setText(headerValues.get("NOTE"));
        txtRepledgeBillAmount.setText(headerValues.get("REPLEDGE_AMOUNT"));
        txtToGetAmount.setText(headerValues.get("REPLEDGE_TOGET_AMOUNT"));
        txtGotAmount.setText(headerValues.get("REPLEDGE_GOT_AMOUNT"));
        txtToGiveAmount.setText(headerValues.get("REPLEDGE_TOGIVE_AMOUNT"));
        txtGivenAmount.setText(headerValues.get("REPLEDGE_GIVEN_AMOUNT"));
    }

    public void clearAllFields()
    {
        txtRepBillId.setText("");
        txtRepledgeBillNumber.setText("");
        txtBillNumber.setText("");
        txtCompanyBillStatus.setText("");
        txtCompanyOpenedDate.setText("");
        txtCompanyClosedDate.setText("");
        txtCompanyBillAmount.setText("");
        txtItems.setText("");
        txtRepledgeBillStatus.setText("");
        txtRepledgeOpenedDate.setText("");
        txtRepledgeClosedDate.setText("");        
        txtNote.setText("");
        txtRepledgeBillAmount.setText("");
        txtToGetAmount.setText("");
        txtGotAmount.setText("");
        txtToGiveAmount.setText("");
        txtGivenAmount.setText("");
    }
    
    @FXML
    private void txtRepledgeBillNumberOnAction(ActionEvent event) {
        
        String sRepledgeId = txtRepledgeId.getText();
        String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
        
        try {
            
            HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByRepledgeBillNumber(sRepledgeId, sRepledgeBillNumber, "GOLD");
            if(headerValues != null) {    
                setAllHeaderValuesToFields(headerValues);
                closeDateRestriction();
            } else {
                PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                btClearAllClicked(null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void txtBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtBillNumber.getText();
        
        try {
            
            HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByCompanyBillNumber(sBillNumber, "GOLD");
            if(headerValues != null) {    
                setAllHeaderValuesToFields(headerValues);
                closeDateRestriction();
            } else {
                PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                btClearAllClicked(null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
        
        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
        {
        
            String sId = txtDebitId.getText();
            String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
            String sRepledgeBillStatus = txtRepledgeBillStatus.getText();
            String sBillNumber = txtBillNumber.getText();
            String sStatus = txtCompanyBillStatus.getText();
            String sRepledgeBillAmount = txtRepledgeBillAmount.getText().toUpperCase();
            String sBillAmount = txtCompanyBillAmount.getText().toUpperCase();
            String sDebittedAmount = txtDebittedAmount.getText().toUpperCase();
            String sNote = txtNote.getText().toUpperCase();
            String sRepledgeId = txtRepledgeId.getText();
            String sRepledgeName = cbRepledgeName.getValue().toUpperCase();

            double dRepledgeBillAmount = Double.parseDouble(!("".equals(sRepledgeBillAmount))? sRepledgeBillAmount : "0");
            double dBillAmount = Double.parseDouble(!("".equals(sBillAmount))? sBillAmount : "0");
            double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");  

            if(isValidHeaderValues(sRepledgeId, sRepledgeName, sBillNumber, sDebittedAmount)) {
                try {
                    if(dbOp.saveBillDebit(sId, "GOLD", sRepledgeBillNumber, sRepledgeBillStatus, sBillNumber, sStatus, dRepledgeBillAmount, dBillAmount, sDebittedDate, dDebittedAmount, dDebittedAmount, sNote, sRepledgeId, sRepledgeName)) {
                        dbOp.setNextId(BILLING_SCREEN_NAME, CommonConstants.REP_DEBIT_RB_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.REP_DEBIT_RB_PREFIX, ""))+1));
                        PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" debitted for bill "+ sBillNumber +" in "+sRepledgeName+".");
                        txtDebitId.setText(dbOp.getId(BILLING_SCREEN_NAME));
                        btROClearAllClicked(null);
                    } 
                } catch (Exception ex) {
                    Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                txtRODebittedAmount.requestFocus();
            }        
        } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
        }                     
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        clearAllFields();
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        sLastSelectedId = null;
        clearAllFields();
        doAllSaveModeONWork();
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAllSaveModeONWork() {
    
        try {
            btSaveBill.setDisable(false);
            btUpdateBill.setDisable(true);
            txtDebitId.setEditable(false);
            txtDebitId.setMouseTransparent(true);
            txtDebitId.setFocusTraversable(false);
            
            txtDebitId.setText(dbOp.getId(BILLING_SCREEN_NAME));
            txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpDebittedDate.setValue(LocalDate.now());
                    
            txtGivenAmount.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        sLastSelectedId = null;
        clearAllFields();
        doAllSaveModeOFFWork();
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAllSaveModeOFFWork() {
    
        btSaveBill.setDisable(true);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(false);
        txtDebitId.setText("");        
        txtDebitId.setEditable(true);
        txtDebitId.setMouseTransparent(false);
        txtDebitId.setFocusTraversable(true);    
        txtDebitId.setText(CommonConstants.REP_DEBIT_RB_PREFIX);
        
        txtDebitId.requestFocus(); 
        txtDebitId.positionCaret(txtDebitId.getText().length());
    }

    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {
            
                String sId = txtDebitId.getText();
                String sDebit = txtDebittedAmount.getText().toUpperCase();
                String sNote = txtNote.getText().toUpperCase();

                double dDebit = Double.parseDouble(!("".equals(sDebit))? sDebit : "0");  

                if(isValidHeaderValues(sId, dDebit)) {
                    try {
                        if(dbOp.updateBillDebit(sId, dDebit, sNote, sDebittedDate)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");
                        } 
                    } catch (Exception ex) {
                        Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtDebittedAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                     
        } else {
            PopupUtil.showErrorAlert(event, "Not any bill number was selected to debit amount.");
        }                
    }

    @FXML
    private void txtDebitIdOnAction(ActionEvent event) {
        
        if(tgOff.isSelected()) 
        {
            String sDebitId = txtDebitId.getText();
            sLastSelectedId = sDebitId;
            clearAllFields();
            
            try {
                HashMap<String, String> headerValues = dbOp.getAllRepBillingHeaderValues(sDebitId);

                if(headerValues != null)
                {
                    txtDebitId.setText(headerValues.get("ID"));       
                    dpDebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtDebittedAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    setAllHeaderValuesToFields(headerValues);
                    closeDateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid debit id.");
                    clearAllFields();
                }

            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }


    @FXML
    private void btROUpdateBillClicked(ActionEvent event) {
        
        if(sROLastSelectedId != null) {
                    
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpRODebittedDate.getValue());

            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {
                String sId = txtRODebitId.getText();                
                String sName = txtROName.getText().toUpperCase();
                String sReason = txtROReason.getText().toUpperCase();
                String sNote = txtRONote.getText().toUpperCase();
                String sInvoiceNo = txtROInvoiceNo.getText().toUpperCase();
                String sInvoiceAmount = txtROInvoiceAmount.getText().toUpperCase();
                String sDebittedAmount = txtRODebittedAmount.getText().toUpperCase();
                String sRepledgeId = txtRepledgeId.getText();
                String sRepledgeName = cbRepledgeName.getValue().toUpperCase();

                double dInvoiceAmount = Double.parseDouble(!("".equals(sInvoiceAmount))? sInvoiceAmount : "0");
                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");  

                if(isValidROHeaderValues(sName, sReason, sDebittedAmount)) {
                    try {
                        if(dbOp.updateROBillDebit(sId, sDebittedDate, sNote, sInvoiceNo, dInvoiceAmount, dDebittedAmount,
                                sRepledgeId, sRepledgeName)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");                    
                        } 
                    } catch (Exception ex) {
                        Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtDebittedAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }  
        } else {
            PopupUtil.showErrorAlert(event, "Not any debit id was selected to update details.");
        }                            
    }

    @FXML
    private void saveModeROON(ActionEvent event) {
        sROLastSelectedId = null;
        clearEOAllHeader();
        doEOAllSaveModeONWork();      
        try {
            closeRODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doEOAllSaveModeONWork() {
    
        try {
            btROSaveBill.setDisable(false);
            btROClearAll.setDisable(false);
            btROUpdateBill.setDisable(true);
            txtRODebitId.setEditable(false);
            txtRODebitId.setMouseTransparent(true);
            txtRODebitId.setFocusTraversable(false);
            cbROExpenseType.getSelectionModel().select(0);
            cbROExpenseType.setMouseTransparent(false);
            cbROExpenseType.setFocusTraversable(true);
            
            txtROName.setEditable(true);
            txtROName.setMouseTransparent(false);
            txtROName.setFocusTraversable(true);    

            txtROReason.setEditable(true);
            txtROReason.setMouseTransparent(false);
            txtROReason.setFocusTraversable(true);    
            
            txtRODebitId.setText(dbOp.getId(OTHER_SCREEN_NAME));
            txtROCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtROCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpRODebittedDate.setValue(LocalDate.now());
                    
            txtRODebittedAmount.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeROOFF(ActionEvent event) {
        btROSaveBill.setDisable(false);
        clearEOAllHeader();
        doROAllSaveModeOFFWork();
        try {
            closeRODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doROAllSaveModeOFFWork() {
    
        btROSaveBill.setDisable(true);
        btROClearAll.setDisable(true);
        btROUpdateBill.setDisable(false);
        txtRODebitId.setText("");        
        txtRODebitId.setEditable(true);
        txtRODebitId.setMouseTransparent(false);
        txtRODebitId.setFocusTraversable(true);    
        cbROExpenseType.getSelectionModel().select(0);
        cbROExpenseType.setEditable(false);
        cbROExpenseType.setMouseTransparent(true);
        cbROExpenseType.setFocusTraversable(false);        
        txtRODebitId.setText(CommonConstants.REP_DEBIT_RO_PREFIX);
        
        txtROName.setEditable(false);
        txtROName.setMouseTransparent(true);
        txtROName.setFocusTraversable(false);    

        txtROReason.setEditable(false);
        txtROReason.setMouseTransparent(true);
        txtROReason.setFocusTraversable(false);    
        
        txtRODebitId.requestFocus(); 
        txtRODebitId.positionCaret(txtRODebitId.getText().length());
    }

    @FXML
    private void txtRODebitIdOnAction(ActionEvent event) {
        
        if(tgROOff.isSelected()) 
        {
            String sRODebitId = txtRODebitId.getText();
            sROLastSelectedId = sRODebitId;
            clearEOAllHeader();
            
            try {
                HashMap<String, String> headerValues = dbOp.getAllROHeaderValues(sRODebitId);

                if(headerValues != null)
                {
                    txtRODebitId.setText(headerValues.get("DEBIT_ID"));       
                    txtROCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
                    txtROCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
                    dpRODebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtRORepledgeId.setText(headerValues.get("REPLEDGE_ID"));
                    cbRORepledgeName.setValue(headerValues.get("REPLEDGE_NAME"));
                    cbROExpenseType.setValue(headerValues.get("EXPENSE_TYPE"));
                    txtROName.setText(headerValues.get("NAME"));       
                    txtROReason.setText(headerValues.get("REASON"));       
                    txtRONote.setText(headerValues.get("NOTE"));       
                    txtROInvoiceNo.setText(headerValues.get("INVOICE_NUMBER"));       
                    txtROInvoiceAmount.setText(headerValues.get("INVOICE_AMOUNT"));             
                    txtRODebittedAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    closeRODateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid debit id.");
                    clearEOAllHeader();
                }

            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
        
    private void closeDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
                
        if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {                
                if(tgOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_VIEW") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sOpeningDate = dbOp.getCompanyCreditOpenedDate(txtDebitId.getText());
                            if(sOpeningDate != null) {
                                String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                        .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                        DateRelatedCalculations.getNextDateWithFormatted(
                                                CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                        sOpenedDate)) {
                                    btUpdateBill.setDisable(false);
                                }
                            }
                    } else {
                        btUpdateBill.setDisable(true);
                    }                    
                }                    
            } else {
                if(tgOn.isSelected()) {
                    btSaveBill.setDisable(true);
                } else {
                    btUpdateBill.setDisable(true);
                }
            }
        }    
    }            

    private void closeRODateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpRODebittedDate.getValue());
                
        if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {                
                if(tgROOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btROSaveBill.setDisable(false);
                    } else {
                        btROSaveBill.setDisable(true);
                    }
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_VIEW") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sOpeningDate = dbOp.getROCompanyCreditOpenedDate(txtRODebitId.getText());
                            if(sOpeningDate != null) {
                                String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                        .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                        DateRelatedCalculations.getNextDateWithFormatted(
                                                CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                        sOpenedDate)) {
                                    btROUpdateBill.setDisable(false);
                                }
                            }
                    } else {
                        btROUpdateBill.setDisable(true);
                    }                    
                }                    
            } else {
                if(tgROOn.isSelected()) {
                    btROSaveBill.setDisable(true);
                } else {
                    btROUpdateBill.setDisable(true);
                }
            }
        }    
    }                
    
}
