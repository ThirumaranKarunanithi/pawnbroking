/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgemaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companymaster.CreditDebitBean;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RePledgeMasterController implements Initializable {

    public RepledgeMasterDBOperation dbOp;
    public Stage dialog;
    
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private String sLastSelectedId;
    private final String REPLEDGE_ID = "REPLEDGE_ID";
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtDoorNo;
    @FXML
    private TextField txtStreetName;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtState;
    @FXML
    private TextField txtMobileNumber;
    @FXML
    private TextField txtLandlineNumber;
    @FXML
    private ComboBox<String> cbInterestType;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtNote;
    @FXML
    private Label lbMsg;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btSaveHeader;
    @FXML
    private Button btUpdateHeader;
    @FXML
    private Button btClearAll;
    @FXML
    public TableView<InterestBean> tbGBOInterest;
    @FXML
    private Button btAddGBOInterest;
    @FXML
    private Button btEditGBOInterest;
    @FXML
    private Button btDeleteGBOInterest;
    @FXML
    private Button btSaveGBOInterest;
    @FXML
    public TableView<DocumentChargeBean> tbGBODocumentCharge;
    @FXML
    private Button btAddGBODocumentCharge;
    @FXML
    private Button btEditGBODocumentCharge;
    @FXML
    private Button btDeleteGBODocumentCharge;
    @FXML
    private Button btSaveGBODocumentCharge;
    @FXML
    public TableView<MonthSettingBean> tbGBMS;
    @FXML
    private Button btAddGBMS;
    @FXML
    private Button btEditGBMS;
    @FXML
    private Button btDeleteGBMS;
    @FXML
    private Button btSaveGBMS;
    @FXML
    private TextField txtGoldReductionDaysOrMonths;
    @FXML
    private ComboBox<String> cbGoldReductionDaysOrMonths;
    @FXML
    private Button btSaveGReductionMonthsOrDays;
    @FXML
    public TableView<FormulaBean> tbGBOFormula;
    @FXML
    private Button btAddGBOFormula;
    @FXML
    private Button btEditGBOFormula;
    @FXML
    private Button btDeleteGBOFormula;
    @FXML
    private Button btSaveGBOFormula;
    @FXML
    public TableView<FormulaBean> tbGBCFormula;
    @FXML
    private Button btAddGBCFormula;
    @FXML
    private Button btEditGBCFormula;
    @FXML
    private Button btDeleteGBCFormula;
    @FXML
    private Button btSaveGBCFormula;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField txtGoldMinimumDaysOrMonths;
    @FXML
    private ComboBox<String> cbGoldMinimumDaysOrMonths;
    @FXML
    private Button btSaveGMinimumMonthsOrDays;
    @FXML
    private Tab tabAdvanceAmountDetails1;
    @FXML
    private TableView<CreditDebitBean> tbCreditDetails;
    @FXML
    private TextField txtCreditCount;
    @FXML
    private TextField txtCreditAmount;
    @FXML
    private DatePicker dpCreditFrom;
    @FXML
    private DatePicker dpCreditTo;
    @FXML
    private Button btCreditFilterRecords;
    @FXML
    private Button btCreditAllRecords;
    @FXML
    private TableView<CreditDebitBean> tbDebitDetails;
    @FXML
    private TextField txtDebitCount;
    @FXML
    private TextField txtDebitAmount;
    @FXML
    private DatePicker dpDebitFrom;
    @FXML
    private DatePicker dpDebitTo;
    @FXML
    private Button btDebitFilterRecords;
    @FXML
    private Button btDebitAllRecords;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            dbOp = new RepledgeMasterDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        try {
            txtId.setText(dbOp.getId(REPLEDGE_ID));
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.REPLEDGE_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.REPLEDGE_MODULE_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                tgOff.setDisable(false);
                btShowAllRecords.setDisable(false);
                btAddToFilter.setDisable(false);
                btShowFilteredRecords.setDisable(false);
            } else {
                tgOff.setDisable(true);
                btShowAllRecords.setDisable(true);
                btAddToFilter.setDisable(true);
                btShowFilteredRecords.setDisable(true);                
            }
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        dpDate.setValue(LocalDate.now());
    }    

    @FXML
    private void txtIdOnAction(ActionEvent event) {
        
        String sId = txtId.getText();
        sLastSelectedId = sId;
        clearAllHeader();
        try {        
            HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sId);
            DataTable interestValues = dbOp.getRepledgeInterestValues(sId, "GOLD");
            DataTable documentChargeValues = dbOp.getRepledgeDocumentChargeValues(sId, "GOLD");
            DataTable oFormulaValues = dbOp.getRepledgeFormulaValues(sId, "GOLD", "OPEN");
            DataTable cFormulaValues = dbOp.getRepledgeFormulaValues(sId, "GOLD", "CLOSE");
            DataTable monthSettingValues = dbOp.getRepledgeMonthSettingValues(sId, "GOLD");
            DataTable reduceDaysOrMonthsValues = dbOp.getRepledgeReduceDaysOrMonthsValues(sId, "GOLD", "REDUCTION");
            DataTable minimumDaysOrMonthsValues = dbOp.getRepledgeReduceDaysOrMonthsValues(sId, "GOLD", "MINIMUM");
            DataTable sGoldOtherSettingValues = dbOp.getOtherSettingsValues(sId, "GOLD");
            
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues);
                setGoldOpenInterestValuesToField(interestValues);
                setGoldOpenDocumentChargeValuesToField(documentChargeValues);
                setGoldOpenFormulaValuesToField(oFormulaValues);
                setGoldCloseFormulaValuesToField(cFormulaValues);
                setGoldBMSValuesToField(monthSettingValues, reduceDaysOrMonthsValues, minimumDaysOrMonthsValues);
                
                setAllCreditValuesToTable(sId);
                setAllDebitValuesToTable(sId);
                
                doGBOInterestUpdateModeWork();
                doGBODocumentChargeUpdateModeWork();
                doGBOFormulaUpdateModeWork(); 
                doGBCFormulaUpdateModeWork(); 
                doGBMSUpdateModeWork();          
                creditDebitUpdateModeWork();
            } else {
                PopupUtil.showErrorAlert("Sorry invalid id.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }

    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtId.setText(headerValues.get("ID"));
        txtName.setText(headerValues.get("NAME"));
        txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
        txtStreetName.setText(headerValues.get("STREET"));
        txtArea.setText(headerValues.get("AREA"));
        txtCity.setText(headerValues.get("CITY"));
        txtState.setText(headerValues.get("STATE"));
        dpDate.setValue(LocalDate.parse(headerValues.get("STARTED_DATE"), CommonConstants.DATETIMEFORMATTER));
        txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
        txtLandlineNumber.setText(headerValues.get("LANDLINE_NUMBER"));
        cbInterestType.setValue(headerValues.get("DAY_OR_MONTHLY_INTEREST"));
        cbStatus.setValue(headerValues.get("STATUS"));
        txtNote.setText(headerValues.get("NOTE"));    
    }
    
    public void setGoldOpenInterestValuesToField(DataTable interestValues) {

        tbGBOInterest.getItems().remove(0, tbGBOInterest.getItems().size());
        for(int i=0; i<interestValues.getRowCount(); i++) {            
            String sFromDate = interestValues.getRow(i).getColumn(0).toString();
            String sToDate = interestValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(interestValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(interestValues.getRow(i).getColumn(3).toString());
            double dInterest = Double.parseDouble(interestValues.getRow(i).getColumn(4).toString());
            tbGBOInterest.getItems().add(new InterestBean(sFromDate, sToDate, dFrom, dTo, dInterest));
        }
    }
    
    public void setGoldOpenDocumentChargeValuesToField(DataTable documentChargeValues) {
        
        tbGBODocumentCharge.getItems().remove(0, tbGBODocumentCharge.getItems().size());
        for(int i=0; i<documentChargeValues.getRowCount(); i++) {            
            String sFromDate = documentChargeValues.getRow(i).getColumn(0).toString();
            String sToDate = documentChargeValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(documentChargeValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(documentChargeValues.getRow(i).getColumn(3).toString());
            double dDocumentCharge = Double.parseDouble(documentChargeValues.getRow(i).getColumn(4).toString());
            tbGBODocumentCharge.getItems().add(new DocumentChargeBean(sFromDate, sToDate, dFrom, dTo, dDocumentCharge));
        }
    }

    public void setGoldOpenFormulaValuesToField(DataTable formulaValues) {
        
        tbGBOFormula.getItems().remove(0, tbGBOFormula.getItems().size());
        for(int i=0; i<formulaValues.getRowCount(); i++) {            
            String sFromDate = formulaValues.getRow(i).getColumn(0).toString();
            String sToDate = formulaValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(formulaValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(formulaValues.getRow(i).getColumn(3).toString());
            String sFormula = formulaValues.getRow(i).getColumn(4).toString();
            tbGBOFormula.getItems().add(new FormulaBean(sFromDate, sToDate, dFrom, dTo, sFormula));
        }
    }

    public void setGoldCloseFormulaValuesToField(DataTable formulaValues) {
        
        tbGBCFormula.getItems().remove(0, tbGBCFormula.getItems().size());
        for(int i=0; i<formulaValues.getRowCount(); i++) {            
            String sFromDate = formulaValues.getRow(i).getColumn(0).toString();
            String sToDate = formulaValues.getRow(i).getColumn(1).toString();            
            double dFrom = Double.parseDouble(formulaValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(formulaValues.getRow(i).getColumn(3).toString());
            String sFormula = formulaValues.getRow(i).getColumn(4).toString();
            tbGBCFormula.getItems().add(new FormulaBean(sFromDate, sToDate, dFrom, dTo, sFormula));
        }
    }

    public void setGoldBMSValuesToField(DataTable monthSettingValues, DataTable daysOrMonthsValues, DataTable minimumDaysOrMonthsValues) {
        
        tbGBMS.getItems().remove(0, tbGBMS.getItems().size());
        for(int i=0; i<monthSettingValues.getRowCount(); i++) {            
            String sFromDate = monthSettingValues.getRow(i).getColumn(0).toString();
            String sToDate = monthSettingValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(monthSettingValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(monthSettingValues.getRow(i).getColumn(3).toString());
            double dDAsMonth= Double.parseDouble(monthSettingValues.getRow(i).getColumn(4).toString());
            tbGBMS.getItems().add(new MonthSettingBean(sFromDate, sToDate, dFrom, dTo, dDAsMonth));
        }
        
        if(daysOrMonthsValues.getRowCount() > 0) {
            txtGoldReductionDaysOrMonths.setText(daysOrMonthsValues.getRow(0).getColumn(0).toString());
            cbGoldReductionDaysOrMonths.setValue(daysOrMonthsValues.getRow(0).getColumn(1).toString());
        } else {
            txtGoldReductionDaysOrMonths.setText("0");
        }

        if(minimumDaysOrMonthsValues.getRowCount() > 0) {
            txtGoldMinimumDaysOrMonths.setText(minimumDaysOrMonthsValues.getRow(0).getColumn(0).toString());
            cbGoldMinimumDaysOrMonths.setValue(minimumDaysOrMonthsValues.getRow(0).getColumn(1).toString());
        } else {
            txtGoldMinimumDaysOrMonths.setText("0");
        }        
    }
        
    @FXML
    public void allowNumberOnlyOnType(KeyEvent e) 
    {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!e.getCharacter().matches("[0-9]")){ 
            e.consume();
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
    private void capitalizeCharOnPressed(KeyEvent e) {
        
        if(e.getCode() == KeyCode.BACK_SPACE){ 
            
            TextField txt_TextField = (TextField) e.getSource(); 
            
            String sText;
            int caretPos = txt_TextField.getCaretPosition();
            
            if(txt_TextField.getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txt_TextField.getText());
                sText = sb.toString();
                txt_TextField.setText(sText);
                txt_TextField.positionCaret(0);
            } else {
                StringBuilder sb = new StringBuilder(txt_TextField.getText());
                sb.deleteCharAt(txt_TextField.getCaretPosition() - 1);
                sText = sb.toString();
                txt_TextField.setText(sText);
                txt_TextField.positionCaret(caretPos-2);
            }            
            e.consume();
        }
    }
    
    @FXML
    private void saveModeON(ActionEvent event) 
    {
       sLastSelectedId = ""; 
       doHeaderSaveModeWork();
        
       doGBOInterestSaveModeWork();
       doGBODocumentChargeSaveModeWork();
       doGBOFormulaSaveModeWork();
       doGBCFormulaSaveModeWork();
       doGBMSSaveModeWork(); 
       creditDebitSaveModeWork();

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.REPLEDGE_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doHeaderSaveModeWork() 
    {
        clearAllHeader();
        txtId.setEditable(false);
        txtId.setMouseTransparent(true);
        txtId.setFocusTraversable(false);     
        txtName.setEditable(true);
        btUpdateHeader.setDisable(true);
        btSaveHeader.setDisable(false);
        try {
            txtId.setText(dbOp.getId(REPLEDGE_ID));
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtName.requestFocus();
    }

    
    public void doGBOInterestSaveModeWork() {
    
        btAddGBOInterest.setDisable(true);
        btEditGBOInterest.setDisable(true);
        btDeleteGBOInterest.setDisable(true);
        btSaveGBOInterest.setDisable(true);
        
        tbGBOInterest.getItems().remove(0, tbGBOInterest.getItems().size());
    }

    public void doGBODocumentChargeSaveModeWork() {
    
        btAddGBODocumentCharge.setDisable(true);
        btEditGBODocumentCharge.setDisable(true);
        btDeleteGBODocumentCharge.setDisable(true);
        btSaveGBODocumentCharge.setDisable(true);
        
        tbGBODocumentCharge.getItems().remove(0, tbGBODocumentCharge.getItems().size());
    }

    public void doGBOFormulaSaveModeWork() {
    
        btAddGBOFormula.setDisable(true);
        btEditGBOFormula.setDisable(true);
        btDeleteGBOFormula.setDisable(true);
        btSaveGBOFormula.setDisable(true);
        
        tbGBOFormula.getItems().remove(0, tbGBOFormula.getItems().size());
    }

    public void doGBCFormulaSaveModeWork() {
    
        btAddGBCFormula.setDisable(true);
        btEditGBCFormula.setDisable(true);
        btDeleteGBCFormula.setDisable(true);
        btSaveGBCFormula.setDisable(true);
        
        tbGBCFormula.getItems().remove(0, tbGBCFormula.getItems().size());
    }

    public void doGBMSSaveModeWork() {
    
        btAddGBMS.setDisable(true);
        btEditGBMS.setDisable(true);
        btDeleteGBMS.setDisable(true);
        btSaveGBMS.setDisable(true);
        btSaveGReductionMonthsOrDays.setDisable(true);
        btSaveGMinimumMonthsOrDays.setDisable(true);
        
        tbGBMS.getItems().remove(0, tbGBMS.getItems().size());
        txtGoldReductionDaysOrMonths.setText("0");
    }
    
    public void creditDebitSaveModeWork() {
    
        tbCreditDetails.getItems().removeAll(tbCreditDetails.getItems());
        tbDebitDetails.getItems().removeAll(tbDebitDetails.getItems());
        txtCreditCount.setText("0");
        txtCreditAmount.setText("0.0");
        txtDebitCount.setText("0");
        txtDebitAmount.setText("0.0");
        
        btCreditFilterRecords.setDisable(true);
        btCreditAllRecords.setDisable(true);
        btDebitFilterRecords.setDisable(true);
        btDebitAllRecords.setDisable(true);  
    }
    
           
    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        doHeaderUpdateModeWork();
    }
    
    public void doHeaderUpdateModeWork() {
    
        clearAllHeader();        
        txtId.setText(CommonConstants.REP_ID_PREFIX);
        txtId.setEditable(true);
        txtId.setMouseTransparent(false);
        txtId.setFocusTraversable(true);     
        txtName.setEditable(false);
        txtName.setMouseTransparent(true);
        txtName.setFocusTraversable(false);             
        btSaveHeader.setDisable(true);   
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.REPLEDGE_MODULE_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateHeader.setDisable(false);
            } else {
                btUpdateHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        txtId.requestFocus(); 
        txtId.positionCaret(txtId.getText().length());
    }

    public void doGBOInterestUpdateModeWork() {
    
        btAddGBOInterest.setDisable(false);
        btEditGBOInterest.setDisable(false);
        btDeleteGBOInterest.setDisable(false);
        btSaveGBOInterest.setDisable(false);
    }

    public void doGBODocumentChargeUpdateModeWork() {
    
        btAddGBODocumentCharge.setDisable(false);
        btEditGBODocumentCharge.setDisable(false);
        btDeleteGBODocumentCharge.setDisable(false);
        btSaveGBODocumentCharge.setDisable(false);
    }

    public void doGBOFormulaUpdateModeWork() {
    
        btAddGBOFormula.setDisable(false);
        btEditGBOFormula.setDisable(false);
        btDeleteGBOFormula.setDisable(false);
        btSaveGBOFormula.setDisable(false);
    }

    public void doGBCFormulaUpdateModeWork() {
    
        btAddGBCFormula.setDisable(false);
        btEditGBCFormula.setDisable(false);
        btDeleteGBCFormula.setDisable(false);
        btSaveGBCFormula.setDisable(false);
    }

    public void doGBMSUpdateModeWork() {
    
        btAddGBMS.setDisable(false);
        btEditGBMS.setDisable(false);
        btDeleteGBMS.setDisable(false);
        btSaveGBMS.setDisable(false);
        btSaveGReductionMonthsOrDays.setDisable(false);
        btSaveGMinimumMonthsOrDays.setDisable(false);
    }    
        
    public void creditDebitUpdateModeWork() {
    
        dpCreditFrom.setValue(LocalDate.now());
        dpCreditTo.setValue(LocalDate.now());
        dpDebitFrom.setValue(LocalDate.now());
        dpDebitTo.setValue(LocalDate.now());
        btCreditFilterRecords.setDisable(false);
        btCreditAllRecords.setDisable(false);
        btDebitFilterRecords.setDisable(false);
        btDebitAllRecords.setDisable(false);  
    }
    
    @FXML
    private void btSaveHeaderClicked(ActionEvent event) {
        
        sLastSelectedId = "";
        String sId = txtId.getText().toUpperCase();
        String sName = txtName.getText().toUpperCase();
        String sDoorNo = txtDoorNo.getText().toUpperCase();
        String sStreetName = txtStreetName.getText().toUpperCase();
        String sArea = txtArea.getText().toUpperCase();
        String sCity = txtCity.getText().toUpperCase();
        String sState = txtState.getText().toUpperCase();
        String sDate = CommonConstants.DATETIMEFORMATTER.format(dpDate.getValue());
        String sMobileNumber = txtMobileNumber.getText().toUpperCase();
        String sLandlineNumber = txtLandlineNumber.getText().toUpperCase();
        String sInterestType = cbInterestType.getValue().toUpperCase();
        String sStatus = cbStatus.getValue().toUpperCase();
        String sNote = txtNote.getText().toUpperCase();       
     
        if(isValidHeaderValues(sName))
        {
            try {
                if(dbOp.isvalidNameToSave(sName))
                {
                    if(dbOp.saveRecord(sId, sName, sDoorNo, sStreetName, sArea, sCity, sState, sDate, sMobileNumber, sLandlineNumber, sInterestType, sStatus, sNote)) 
                    {
                        dbOp.setNextId(REPLEDGE_ID, CommonConstants.REP_ID_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.REP_ID_PREFIX, ""))+1));
                        txtName.setText("");
                        try {
                            txtId.setText(dbOp.getId(REPLEDGE_ID));
                        } catch (SQLException ex) {
                            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        PopupUtil.showInfoAlert("Repledge company created successfully with id."+"("+sId+")");
                    }
                } else {
                    PopupUtil.showErrorAlert("Sorry same name already exists.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            PopupUtil.showErrorAlert("All mandatory fields should be filled.");
        }
    }

    public boolean isValidHeaderValues(String sName)
    {
        return !sName.isEmpty();
    }
    
    @FXML
    private void btUpdateHeaderClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            String sId = txtId.getText().toUpperCase();
            String sName = txtName.getText().toUpperCase();
            String sDoorNo = txtDoorNo.getText().toUpperCase();
            String sStreetName = txtStreetName.getText().toUpperCase();
            String sArea = txtArea.getText().toUpperCase();
            String sCity = txtCity.getText().toUpperCase();
            String sState = txtState.getText().toUpperCase();
            String sDate = CommonConstants.DATETIMEFORMATTER.format(dpDate.getValue());
            String sMobileNumber = txtMobileNumber.getText().toUpperCase();
            String sLandlineNumber = txtLandlineNumber.getText().toUpperCase();
            String sInterestType = cbInterestType.getValue().toUpperCase();
            String sStatus = cbStatus.getValue().toUpperCase();
            String sNote = txtNote.getText().toUpperCase();       

            if(isValidHeaderValues(sName))
            {
                try {
                    if(dbOp.updateRecord(sId, sDoorNo, sStreetName, sArea, sCity, sState, sDate, sMobileNumber, sLandlineNumber, sInterestType, sStatus, sNote)) 
                    {
                        PopupUtil.showInfoAlert("Repledge Id "+"("+sId+")"+" details updated successfully.");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert("All mandatory fields should be filled.");
            }
        } else {
            PopupUtil.showErrorAlert("No any repledge selected to update.");
        }
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        clearAllHeader();
    }

    public void clearAllHeader()
    {
        txtName.setText("");
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtState.setText("");
        dpDate.setValue(LocalDate.now());
        txtMobileNumber.setText("");
        txtLandlineNumber.setText("");
        cbInterestType.setValue("MONTH");
        cbStatus.setValue("ACTIVE");        
        txtNote.setText("");        
    }

    @FXML
    private void btAddGBOInterestClicked(ActionEvent event) {
                
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenInterestDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldOpenInterestDialogUIController gon = (GoldOpenInterestDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }

    @FXML
    private void btEditGBOInterestClicked(ActionEvent event) {
        
        int index = tbGBOInterest.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenInterestDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldOpenInterestDialogUIController gon = (GoldOpenInterestDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btDeleteGBOInterestClicked(ActionEvent event) {
        
        int index = tbGBOInterest.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbGBOInterest.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveGBOInterestClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<InterestBean> tableValues = tbGBOInterest.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllRepledgeInterest(sId, "GOLD")) {
                    if(dbOp.saveRepledgeInterestRecords(sId, tableValues, "GOLD")) {
                        PopupUtil.showInfoAlert("Interest for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void btAddGBODocumentChargeClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenDocumentChargeDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldOpenDocumentChargeDialogUIController gon = (GoldOpenDocumentChargeDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }

    @FXML
    private void btEditGBODocumentChargeClicked(ActionEvent event) {

        int index = tbGBODocumentCharge.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenDocumentChargeDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldOpenDocumentChargeDialogUIController gon = (GoldOpenDocumentChargeDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }        
    }

    @FXML
    private void btDeleteGBODocumentChargeClicked(ActionEvent event) {
        
        int index = tbGBODocumentCharge.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbGBODocumentCharge.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveGBODocumentChargeClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<DocumentChargeBean> tableValues = tbGBODocumentCharge.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllRepledgeDocumentCharge(sId, "GOLD")) {
                    if(dbOp.saveRepledgeDocumentChargeRecords(sId, tableValues, "GOLD")) {
                        PopupUtil.showInfoAlert("Document charges for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }

    @FXML
    private void btAddGBOFormulaClicked(ActionEvent event) {

        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenFormulaDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldOpenFormulaDialogUIController gon = (GoldOpenFormulaDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();        
    }

    @FXML
    private void btEditGBOFormulaClicked(ActionEvent event) {

        int index = tbGBOFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenFormulaDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldOpenFormulaDialogUIController gon = (GoldOpenFormulaDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }        
    }

    @FXML
    private void btDeleteGBOFormulaClicked(ActionEvent event) {
        
        int index = tbGBOFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbGBOFormula.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveGBOFormulaClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<FormulaBean> tableValues = tbGBOFormula.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllRepledgeFormula(sId, "GOLD", "OPEN")) {
                    if(dbOp.saveRepledgeFormulaRecords(sId, tableValues, "GOLD", "OPEN")) {
                        PopupUtil.showInfoAlert("Formula for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }

    @FXML
    private void btAddGBCFormulaClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldCloseFormulaDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldCloseFormulaDialogUIController gon = (GoldCloseFormulaDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();                
    }

    @FXML
    private void btEditGBCFormulaClicked(ActionEvent event) {
        
        int index = tbGBCFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldCloseFormulaDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldCloseFormulaDialogUIController gon = (GoldCloseFormulaDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }                
    }

    @FXML
    private void btDeleteGBCFormulaClicked(ActionEvent event) {
        
        int index = tbGBCFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbGBCFormula.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveGBCFormulaClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<FormulaBean> tableValues = tbGBCFormula.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllRepledgeFormula(sId, "GOLD", "CLOSE")) {
                    if(dbOp.saveRepledgeFormulaRecords(sId, tableValues, "GOLD", "CLOSE")) {
                        PopupUtil.showInfoAlert("Formula for gold bill closing saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
    
    @FXML
    private void btAddGBMSClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldMonthSettingDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldMonthSettingDialogUIController gon = (GoldMonthSettingDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();                        
    }

    @FXML
    private void btEditGBMSClicked(ActionEvent event) {
        
        int index = tbGBMS.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldMonthSettingDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldMonthSettingDialogUIController gon = (GoldMonthSettingDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }                        
    }

    @FXML
    private void btDeleteGBMSClicked(ActionEvent event) {
        
        int index = tbGBMS.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbGBMS.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveGBMSClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<MonthSettingBean> tableValues = tbGBMS.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllRepledgeMonthSetting(sId, "GOLD")) {
                    if(dbOp.saveRepledgeMonthSettingRecords(sId, tableValues, "GOLD")) {
                        PopupUtil.showInfoAlert("Month settings for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }
    
    @FXML
    private void btSaveReductionMonthsOrDaysClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sValue = txtGoldReductionDaysOrMonths.getText();
        String sType = cbGoldReductionDaysOrMonths.getSelectionModel().getSelectedItem();
        double dValue = "".equals(sValue) ? 0 : Double.parseDouble(sValue);
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteReduceDaysOrMonthsValues(sId, "GOLD", "REDUCTION")) {
                    if(dbOp.saveReduceDaysOrMonthsValues(sId, dValue, sType, "GOLD", "REDUCTION")) {
                        PopupUtil.showInfoAlert("Reduction months or days for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }

    @FXML
    private void btSaveMinimumMonthsOrDaysClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sValue = txtGoldMinimumDaysOrMonths.getText();
        String sType = cbGoldMinimumDaysOrMonths.getSelectionModel().getSelectedItem();
        double dValue = "".equals(sValue) ? 0 : Double.parseDouble(sValue);
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteReduceDaysOrMonthsValues(sId, "GOLD", "MINIMUM")) {
                    if(dbOp.saveReduceDaysOrMonthsValues(sId, dValue, sType, "GOLD", "MINIMUM")) {
                        PopupUtil.showInfoAlert("Reduction months or days for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                     
    }
    

    @FXML
    private void showAllRecordsClicked(ActionEvent event) {        
        
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues(null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sId = allDetailValues.getRow(i).getColumn(0).toString();
            String sName = allDetailValues.getRow(i).getColumn(1).toString();
            String sArea = allDetailValues.getRow(i).getColumn(2).toString();
            String sLandlineNumber = allDetailValues.getRow(i).getColumn(3).toString();
            String sInterestType = allDetailValues.getRow(i).getColumn(4).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(5).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sId, sName, sArea, sLandlineNumber, sInterestType, sStatus));
        }        
    }
    
    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbAllDetails.getSelectionModel().getSelectedIndex();
        
        if (event.getClickCount() == 2 && (index >= 0) ) {
            
            String sId = tbAllDetails.getItems().get(index).getSId();            
            tgOff.setSelected(true);
            saveModeOFF(null);
            txtId.setText(sId);
            txtIdOnAction(null);
            tpScreen.getSelectionModel().select(tabMainScreen);
        }
    }
    
    @FXML
    private void btAddToFilterClicked(ActionEvent event) {
        
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            String sFilterValue = txtAddToFilter.getText();
            alFilterDBColumnName.add(getDBColumnNameFor(sFilterName));
            alFilterName.add(sFilterName);
            alFilterValue.add(sFilterValue);
            if(txtFilter.getText().length() > 0) {
                txtFilter.setText(txtFilter.getText() + ", "+sFilterName+": "+sFilterValue);
            } else {
                txtFilter.setText(sFilterName+": "+sFilterValue);
            }
            cbAllDetailsFilter.getItems().remove(sFilterName);
            txtAddToFilter.setText("");
        }                
    }
    
    public String getDBColumnNameFor(String filterName) {
    
        switch (filterName)
        {        
            case "NAME":
                return "NAME";
            case "AREA":
                return "AREA";
            case "LANDLINE NUMBER":
                return "LANDLINE_NUMBER";
            case "INTEREST_TYPE":
                return "DAY_OR_MONTHLY_INTEREST";
            case "STATUS":
                return "STATUS";
            default:
                return null;
        }
    }   
    
    @FXML
    private void btFilterUndoOnAction(ActionEvent event) {
        
        cbAllDetailsFilter.getItems().add(alFilterName.get(alFilterName.size()-1));
        alFilterName.remove(alFilterName.size() - 1);
        alFilterValue.remove(alFilterValue.size() - 1);
        alFilterDBColumnName.remove(alFilterDBColumnName.size() - 1);
        String sToSetFilter = "";            
        for(int i=0; i<alFilterName.size(); i++)
        {
            if(sToSetFilter.length() > 0) {
                sToSetFilter += ", " + alFilterName.get(i) + ": " + alFilterValue.get(i);
            } else {
                sToSetFilter += alFilterName.get(i) + ": " + alFilterValue.get(i);
            }
        }
        txtFilter.setText(sToSetFilter);                  
    }

    @FXML
    private void btFilterClearAllClicked(ActionEvent event) {
        
        for(int i=0; i<alFilterName.size(); i++) {        
            cbAllDetailsFilter.getItems().add(alFilterName.get(i));
        }
        
        alFilterName.removeAll(alFilterName);
        alFilterValue.removeAll(alFilterValue);
        alFilterDBColumnName.removeAll(alFilterDBColumnName);
        
        txtFilter.setText("");        
    }

    @FXML
    private void showFilteredRecordsClicked(ActionEvent event) {
        
        String sFilterScript = "";
        
        for(int i=0; i<alFilterDBColumnName.size(); i++) {            
            if("".equals(sFilterScript)) {
                sFilterScript = "WHERE " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";                
            } else {
                sFilterScript += "AND " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";                
            }
        }

        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            allDetailValues = dbOp.getAllDetailsValues(sFilterScript, alFilterValue.toArray(sValsArray));
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(RePledgeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void setTableValues(TableView<CreditDebitBean> table, DataTable data, TextField txtCount, TextField txtAmount) {

        int count = 0;
        double totalAmount = 0.0;

        table.getItems().removeAll(table.getItems());

        for(int i=0; i<data.getRowCount(); i++) {            
            String sSlNo = data.getRow(i).getColumn(0).toString();
            String sId = data.getRow(i).getColumn(1).toString();
            String sType = data.getRow(i).getColumn(2).toString();
            String sDate = data.getRow(i).getColumn(3).toString();
            String sReason = data.getRow(i).getColumn(4).toString();
            String sAmount = data.getRow(i).getColumn(5).toString();

            double dAmount = Double.parseDouble(sAmount);
            table.getItems().add(new CreditDebitBean(sSlNo, sId, sType, sDate, sReason, dAmount));
            count++;
            totalAmount += dAmount;
        }        

        txtCount.setText(Integer.toString(count));
        txtAmount.setText(Double.toString(totalAmount));        
    }
    
    public void setAllCreditValuesToTable(String sCompanyId) {
    
        try {
            DataTable creditDetailValues = dbOp.getCreditTableValues(sCompanyId);
            setTableValues(tbCreditDetails, creditDetailValues, txtCreditCount, txtCreditAmount);
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllDebitValuesToTable(String sCompanyId) {
    
        try {
            DataTable debitDetailValues = dbOp.getDebitTableValues(sCompanyId);
            setTableValues(tbDebitDetails, debitDetailValues, txtDebitCount, txtDebitAmount);            
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btCreditFilterRecordsOnAction(ActionEvent event) {

        try {
            String sCompanyId = txtId.getText().toUpperCase().trim();
            String sFromDate = CommonConstants.DATETIMEFORMATTER.format(dpCreditFrom.getValue());
            String sToDate = CommonConstants.DATETIMEFORMATTER.format(dpCreditTo.getValue());
            DataTable creditDetailValues = dbOp.getCreditTableValues(sCompanyId, sFromDate, sToDate);
            setTableValues(tbCreditDetails, creditDetailValues, txtCreditCount, txtCreditAmount);
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void btCreditAllRecordsOnAction(ActionEvent event) {
        setAllCreditValuesToTable(txtId.getText().toUpperCase().trim());
    }

    @FXML
    private void btDebitFilterRecordsOnAction(ActionEvent event) {

        try {
            String sCompanyId = txtId.getText().toUpperCase().trim();
            String sFromDate = CommonConstants.DATETIMEFORMATTER.format(dpDebitFrom.getValue());
            String sToDate = CommonConstants.DATETIMEFORMATTER.format(dpDebitTo.getValue());
            DataTable debitDetailValues = dbOp.getDebitTableValues(sCompanyId, sFromDate, sToDate);
            setTableValues(tbDebitDetails, debitDetailValues, txtDebitCount, txtDebitAmount);     
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }

    @FXML
    private void btDebitAllRecordsOnAction(ActionEvent event) {
        setAllDebitValuesToTable(txtId.getText().toUpperCase().trim());
    }
}
