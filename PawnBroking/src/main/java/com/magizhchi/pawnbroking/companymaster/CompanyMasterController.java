/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import com.github.sarxos.webcam.Webcam;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.WebCamWork;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class CompanyMasterController implements Initializable {

    public CompanyMasterDBOperation dbOp;
    public Stage dialog;
    
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private String sLastSelectedId;
    private String sAccStartingDate = null;
    private String sAccLastDate = null;
    
    private final String COMPANY_ID = "COMPANY_ID";
    public static final String DO_NOT_TAKE_PICTURE = "DO NOT TAKE PICTURE";
    public static final String DO_NOT_PRINT = "DO NOT PRINT";
    
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
    private TextField txtLCHolderName;
    @FXML
    private TextField txtLCNumber;
    @FXML
    private DatePicker dpLCDate;
    @FXML
    private TextField txtMobileNumber;
    @FXML
    private TextField txtLandlineNumber;
    @FXML
    private ComboBox cbInterestType;
    @FXML
    private ComboBox cbStatus;
    @FXML
    private TextArea txtNote;
    @FXML
    private Button btSaveHeader;
    @FXML
    private Button btClearAll;   
    @FXML
    private Button btUpdateHeader; 
    @FXML
    public Label lbMsg;   
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    public TableView<NumberGeneratorBean> tbGBONumberGenerator;                      
    @FXML
    private TextField txtGBONGPreRow;
    @FXML
    private TextField txtGBONGPrePrefix;
    @FXML
    private TextField txtGBONGPreNumber;
    @FXML
    private TextField txtGBONGCurRow;
    @FXML
    private TextField txtGBONGCurPrefix;
    @FXML
    private TextField txtGBONGCurNumber;
    @FXML
    private TextField txtGoldPreBillNumber;
    @FXML
    private TextField txtSilverPreBillNumber;
    @FXML
    private Button btEditGBONumber;
    @FXML
    private Button btSaveGBONumber;
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
    public TableView<NumberGeneratorBean> tbSBONumberGenerator;
    @FXML
    private TextField txtSBONGPreRow;
    @FXML
    private TextField txtSBONGPrePrefix;
    @FXML
    private TextField txtSBONGPreNumber;
    @FXML
    private TextField txtSBONGCurRow;
    @FXML
    private TextField txtSBONGCurPrefix;
    @FXML
    private TextField txtSBONGCurNumber;
    @FXML
    private Button btEditSBONumber;
    @FXML
    private Button btSaveSBONumber;
    @FXML
    public TableView<InterestBean> tbSBOInterest;
    @FXML
    private Button btAddSBOInterest;
    @FXML
    private Button btEditSBOInterest;
    @FXML
    private Button btDeleteSBOInterest;
    @FXML
    private Button btSaveSBOInterest;
    @FXML
    public TableView<DocumentChargeBean> tbSBODocumentCharge;
    @FXML
    private Button btAddSBODocumentCharge;
    @FXML
    private Button btEditSBODocumentCharge;
    @FXML
    private Button btDeleteSBODocumentCharge;
    @FXML
    private Button btSaveSBODocumentCharge;
    @FXML
    public TableView<MonthSettingBean> tbSBMS;
    @FXML
    private Button btAddSBMS;
    @FXML
    private Button btEditSBMS;
    @FXML
    private Button btDeleteSBMS;
    @FXML
    private Button btSaveSBMS;
    @FXML
    public TableView<FormulaBean> tbSBOFormula;
    @FXML
    private Button btAddSBOFormula;
    @FXML
    private Button btEditSBOFormula;
    @FXML
    private Button btDeleteSBOFormula;
    @FXML
    private Button btSaveSBOFormula;
    @FXML
    public TableView<FormulaBean> tbSBCFormula;
    @FXML
    private Button btAddSBCFormula;
    @FXML
    private Button btEditSBCFormula;
    @FXML
    private Button btDeleteSBCFormula;
    @FXML
    private Button btSaveSBCFormula;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private TabPane tpScreen;    
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private TextField txtFilter;
    @FXML
    private TextField txtGoldReductionDaysOrMonths;
    @FXML
    private ComboBox<String> cbGoldReductionDaysOrMonths;
    @FXML
    private Button btSaveGReductionMonthsOrDays;
    @FXML
    private TextField txtGoldMinimumDaysOrMonths;
    @FXML
    private ComboBox<String> cbGoldMinimumDaysOrMonths;
    @FXML
    private Button btSaveGMinimumMonthsOrDays;
    @FXML
    private TextField txtSilverMinimumDaysOrMonths;
    @FXML
    private ComboBox<String> cbSilverMinimumDaysOrMonths;
    @FXML
    private Button btSaveSMinimumMonthsOrDays;
    @FXML
    private TextField txtSilverReductionDaysOrMonths;
    @FXML
    private Button btSaveSReductionMonthsOrDays;
    @FXML
    private ComboBox<String> cbSilverReductionDaysOrMonths;
    @FXML
    private TextField txtTodaysGoldRate;
    @FXML
    private TextField txtGDefaultPurityValue;
    @FXML
    private TextField txtGDefaultCity;
    @FXML
    private Button btGSaveOtherSetting;
    @FXML
    private TextField txtTodaysSilverRate;
    @FXML
    private TextField txtSDefaultPurityValue;
    @FXML
    private TextField txtSDefaultCity;
    @FXML
    private Button btSSaveOtherSetting;
    @FXML
    private ComboBox<String> cbAllowToChangeGBODate;
    @FXML
    private ComboBox<String> cbAllowToChangeGBCDate;
    @FXML
    private ComboBox<String> cbAllowToChangeGAADate;
    @FXML
    private ComboBox<String> cbAllowToChangeSBODate;
    @FXML
    private ComboBox<String> cbAllowToChangeSBCDate;
    @FXML
    private ComboBox<String> cbAllowToChangeSAADate;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBODate;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBCDate;
    @FXML
    private DatePicker dpAccStartingDate;
    @FXML
    private TextField txtAccPreDate;
    @FXML
    private TextField txtAccPreDayAvailableBalance;
    @FXML
    private Button btAccSaveTodaysSetting;
    @FXML
    private DatePicker dpToBeSetLastDate;
    @FXML
    private TextField txtAvailableAmount;
    @FXML
    private TextField txtDeficitAmount;
    @FXML
    private Button btSaveDateAsLast;
    @FXML
    private TextField txtActualAmount;
    @FXML
    private ComboBox<String> cbcompanyType;
    @FXML
    private ComboBox<String> cbAutoBillGeneration;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBOName;
    @FXML
    private ComboBox<String> cbAllowToChangeGBOAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeGBCAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeGAAAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBOAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBCAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeSBOAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeSBCAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeSAAAmount;
    @FXML
    private Tab tabAdvanceAmountDetails1;
    @FXML
    private TableView<CreditDebitBean> tbCreditDetails;
    @FXML
    private TextField txtCreditCount;
    @FXML
    private TextField txtCreditAmount;
    @FXML
    private TableView<CreditDebitBean> tbDebitDetails;
    @FXML
    private TextField txtDebitCount;
    @FXML
    private TextField txtDebitAmount;
    @FXML
    private DatePicker dpCreditFrom;
    @FXML
    private DatePicker dpCreditTo;
    @FXML
    private Button btCreditFilterRecords;
    @FXML
    private Button btCreditAllRecords;
    @FXML
    private DatePicker dpDebitFrom;
    @FXML
    private DatePicker dpDebitTo;
    @FXML
    private Button btDebitFilterRecords;
    @FXML
    private Button btDebitAllRecords;
    @FXML
    private TextField txtGDefaultArea;
    @FXML
    private TextField txtSDefaultArea;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private TextField txtGoldCompanyRate;
    @FXML
    private TextField txtGoldReductionWt;
    @FXML
    private TextField txtSilverCompanyRate;
    @FXML
    private TextField txtSilverReductionWt;
    @FXML
    public TableView<FineChargeBean> tbOCFineCharges;
    @FXML
    private Button btAddOCFineCharge;
    @FXML
    private Button btEditOCFineCharge;
    @FXML
    private Button btDeleteOCFineCharge;
    @FXML
    private Button btSaveOCFineCharge;
    @FXML
    private DatePicker dpNoticeTillDate;
    @FXML
    private TextField txtNoticeChargeAmount;
    @FXML
    private Button btSaveOCNotice;
    @FXML
    private TextField txtFilePath;
    @FXML
    private Button btSaveFIlePath;
    @FXML
    private ComboBox<String> cbGBoPrintOnSaveClicked;
    @FXML
    private ComboBox<String> cbSBoPrintOnSaveClicked;
    @FXML
    private ComboBox<String> cbGBCompanyCopyPrint;
    @FXML
    private ComboBox<String> cbGBCustomerCopyPrint;
    @FXML
    private Button btGSavePrintSetting;
    @FXML
    private ComboBox<String> cbSBCompanyCopyPrint;
    @FXML
    private ComboBox<String> cbSBCustomerCopyPrint;
    @FXML
    private Button btSSavePrintSetting;
    @FXML
    private TextField txtCardLostFineCharge;
    @FXML
    private Button btSaveCardLostCharge;
    @FXML
    private ComboBox<String> cbGBPackingCopyPrint;
    @FXML
    private ComboBox<String> cbSBPackingCopyPrint;
    @FXML
    private ComboBox<String> cbGBCustomerCamera;
    @FXML
    private ComboBox<String> cbGBJewelCamera;
    @FXML
    private ComboBox<String> cbGBUserCamera;
    @FXML
    private Button btGSaveCameraSetting;
    @FXML
    private TextField txtCamTempFilePath;
    @FXML
    private ComboBox<String> cbGBoDirectPrint;
    @FXML
    private ComboBox<String> cbSBoDirectPrint;
    @FXML
    public TextField txtShareCustomerListFrom;
    @FXML
    private Button btSaveShareCustomerListFrom;
    @FXML
    public TableView<NumberGeneratorBean> tbGBONumberGenerator1;
    @FXML
    private TextField txtGBONGPreRow1;
    @FXML
    private TextField txtGBONGPrePrefix1;
    @FXML
    private TextField txtGBONGPreNumber1;
    @FXML
    private TextField txtGBONGCurRow1;
    @FXML
    private TextField txtGBONGCurPrefix1;
    @FXML
    private TextField txtGBONGCurNumber1;
    @FXML
    private Button btEditGBONumber1;
    @FXML
    private Button btSaveGBONumber1;
    @FXML
    private TableView<?> tbGBOInterest1;
    @FXML
    private Button btAddGBOInterest1;
    @FXML
    private Button btEditGBOInterest1;
    @FXML
    private Button btDeleteGBOInterest1;
    @FXML
    private Button btSaveGBOInterest1;
    @FXML
    private TableView<?> tbGBODocumentCharge1;
    @FXML
    private Button btAddGBODocumentCharge1;
    @FXML
    private Button btEditGBODocumentCharge1;
    @FXML
    private Button btDeleteGBODocumentCharge1;
    @FXML
    private Button btSaveGBODocumentCharge1;
    @FXML
    private TableView<?> tbGBOFormula1;
    @FXML
    private Button btAddGBOFormula1;
    @FXML
    private Button btEditGBOFormula1;
    @FXML
    private Button btDeleteGBOFormula1;
    @FXML
    private Button btSaveGBOFormula1;
    @FXML
    private TableView<?> tbGBCFormula1;
    @FXML
    private Button btAddGBCFormula1;
    @FXML
    private Button btEditGBCFormula1;
    @FXML
    private Button btDeleteGBCFormula1;
    @FXML
    private Button btSaveGBCFormula1;
    @FXML
    private TextField txtTodaysGoldRate1;
    @FXML
    private TextField txtGoldCompanyRate1;
    @FXML
    private TextField txtGoldReductionWt1;
    @FXML
    private TextField txtGDefaultPurityValue1;
    @FXML
    private TextField txtGDefaultArea1;
    @FXML
    private TextField txtGDefaultCity1;
    @FXML
    private ComboBox<String> cbAllowToChangeGBODate1;
    @FXML
    private ComboBox<String> cbAllowToChangeGBCDate1;
    @FXML
    private ComboBox<String> cbAllowToChangeGAADate1;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBODate1;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBCDate1;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBOName1;
    @FXML
    private ComboBox<String> cbAllowToChangeGBOAmount1;
    @FXML
    private ComboBox<String> cbAllowToChangeGBCAmount1;
    @FXML
    private ComboBox<String> cbAllowToChangeGAAAmount1;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBOAmount1;
    @FXML
    private ComboBox<String> cbAllowToChangeGRBCAmount1;
    @FXML
    private Button btGSaveOtherSetting1;
    @FXML
    private ComboBox<String> cbGBoPrintOnSaveClicked1;
    @FXML
    private ComboBox<String> cbGBCompanyCopyPrint1;
    @FXML
    private ComboBox<String> cbGBCustomerCopyPrint1;
    @FXML
    private ComboBox<String> cbGBPackingCopyPrint1;
    @FXML
    private ComboBox<String> cbGBoDirectPrint1;
    @FXML
    private Button btGSavePrintSetting1;
    @FXML
    private ComboBox<String> cbGBCustomerCamera1;
    @FXML
    private ComboBox<String> cbGBJewelCamera1;
    @FXML
    private ComboBox<String> cbGBUserCamera1;
    @FXML
    private TextField txtCamTempFilePath1;
    @FXML
    private Button btGSaveCameraSetting1;
    @FXML
    private ComboBox<String> cbAllowToChangeEmpExpDate;
    @FXML
    private ComboBox<String> cbAllowToChangeCompExpDate;
    @FXML
    private ComboBox<String> cbAllowToChangeRepExpDate;
    @FXML
    private ComboBox<String> cbAllowToChangeEmpIncDate;
    @FXML
    private ComboBox<String> cbAllowToChangeCompIncDate;
    @FXML
    private ComboBox<String> cbAllowToChangeRepInc;
    @FXML
    private Button btAccSaveOtherSetting;
    @FXML
    private ComboBox<String> cbAllowToChangeGBOGivenAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeGBCReceivedAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeSBOGivenAmount;
    @FXML
    private ComboBox<String> cbAllowToChangeSBCReceivedAmount;
    @FXML
    private ComboBox<String> cbEntryMode;
    @FXML
    private ComboBox<String> cbVerifyGBCCopies;
    @FXML
    private Button btGSaveVerifySetting;
    @FXML
    private ComboBox<String> cbVerifySBCCopies;
    @FXML
    private Button btSSaveVerifySetting;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        doHeaderInitWork();    
    }    
    
    public void doHeaderInitWork() {
        
        try {
            dbOp = new CompanyMasterDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            txtId.setText(dbOp.getId(COMPANY_ID));
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dpLCDate.setValue(LocalDate.now());
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.COMPANY_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.COMPANY_MODULE_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
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
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        btUpdateHeader.setDisable(true);
        txtName.requestFocus();
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
    private void allowNumberAndDotOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
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
    public void btSaveHeaderClicked(ActionEvent e)
    {
        try {
            if(dbOp.companyCount() < CommonConstants.ACTIVE_MACHINE.getNumberOfCompaniesCanCreate()) {
                sLastSelectedId = "";
                String sId = txtId.getText().toUpperCase();
                String sType = cbcompanyType.getValue().toUpperCase();
                String sName = txtName.getText().toUpperCase();
                String sDoorNo = txtDoorNo.getText().toUpperCase();
                String sStreetName = txtStreetName.getText().toUpperCase();
                String sArea = txtArea.getText().toUpperCase();
                String sCity = txtCity.getText().toUpperCase();
                String sState = txtState.getText().toUpperCase();
                String sLCHolderName = txtLCHolderName.getText().toUpperCase();
                String sLCNumber = txtLCNumber.getText().toUpperCase();
                String sLCDate = CommonConstants.DATETIMEFORMATTER.format(dpLCDate.getValue());
                String sMobileNumber = txtMobileNumber.getText().toUpperCase();
                String sLandlineNumber = txtLandlineNumber.getText().toUpperCase();
                String sInterestType = cbInterestType.getValue().toString().toUpperCase();
                String sStatus = cbStatus.getValue().toString().toUpperCase();
                String sNote = txtNote.getText().toUpperCase();       
                boolean bAutoBillNumber = cbAutoBillGeneration.getValue().toUpperCase().equals("YES");
                boolean bEntryMode = cbEntryMode.getValue().toUpperCase().equals("YES");
                
                if(isValidHeaderValues(sName))
                {                
                    if(dbOp.isvalidNameToSave(sName))
                    {
                        if(sStatus.equals("ACTIVE"))
                        {
                            try {
                                dbOp.updateAllToRestStatus();
                                CommonConstants.ACTIVE_COMPANY_ID = sId;
                            } catch (Exception ex) {
                                Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        try {
                            if(dbOp.saveRecord(sId, sType, sName, sDoorNo,
                                    sStreetName, sArea, sCity,
                                    sState, sLCHolderName, sLCNumber,
                                    sLCDate, sMobileNumber, sLandlineNumber,
                                    sInterestType, sStatus, sNote, bAutoBillNumber, bEntryMode))
                            {
                                dbOp.setNextId(COMPANY_ID, CommonConstants.CMP_ID_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.CMP_ID_PREFIX, ""))+1));
                                txtName.setText("");
                                try {
                                    txtId.setText(dbOp.getId(COMPANY_ID));
                                } catch (SQLException ex) {
                                    Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                PopupUtil.showInfoAlert("New company created successfully with id."+"("+sId+")");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        PopupUtil.showErrorAlert("Sorry same name already exists.");
                    }
                } else {
                    PopupUtil.showErrorAlert("All mandatory fields should be filled.");
                }
            } else {
                PopupUtil.showErrorAlert("Sorry you cannot create more than " 
                        + CommonConstants.ACTIVE_MACHINE.getNumberOfCompaniesCanCreate() 
                        + " Companies in this machine.");
            }
        } catch (SQLException ex) {
            PopupUtil.showErrorAlert(ex+"");
        }            
    }
    
    @FXML
    public void btUpdateHeaderClicked(ActionEvent e)
    {
        if(!"".equals(sLastSelectedId)) {
            txtId.setText(sLastSelectedId);
        }
        String sId = txtId.getText().toUpperCase();
        String sType = cbcompanyType.getValue().toUpperCase();
        String sName = txtName.getText().toUpperCase();
        String sDoorNo = txtDoorNo.getText().toUpperCase();
        String sStreetName = txtStreetName.getText().toUpperCase();
        String sArea = txtArea.getText().toUpperCase();
        String sCity = txtCity.getText().toUpperCase();
        String sState = txtState.getText().toUpperCase();
        String sLCHolderName = txtLCHolderName.getText().toUpperCase();
        String sLCNumber = txtLCNumber.getText().toUpperCase();
        String sLCDate = CommonConstants.DATETIMEFORMATTER.format(dpLCDate.getValue());
        String sMobileNumber = txtMobileNumber.getText().toUpperCase();
        String sLandlineNumber = txtLandlineNumber.getText().toUpperCase();
        String sInterestType = cbInterestType.getValue().toString().toUpperCase();
        String sStatus = cbStatus.getValue().toString().toUpperCase();
        String sNote = txtNote.getText().toUpperCase();       
        boolean bAutoBillNumber = cbAutoBillGeneration.getValue().toUpperCase().equals("YES");
        boolean bEntryMode = cbEntryMode.getValue().toUpperCase().equals("YES");
        
        try {
            if(dbOp.isIdAlreadyExists(sId))
            {
                if("ACTIVE".equals(sStatus)) {
                    dbOp.updateAllToRestStatus();
                    CommonConstants.ACTIVE_COMPANY_ID = sId;
                    CommonConstants.ACTIVE_COMPANY_ACC_STARTING_DATE = sAccStartingDate;
                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE = sAccLastDate;
                }
                if(dbOp.updateRecord(sId, sDoorNo, sType, sStreetName, sArea, sCity, sState, 
                        sLCHolderName, sLCNumber, sLCDate, sMobileNumber, sLandlineNumber, 
                        sInterestType, sStatus, sNote, bAutoBillNumber, bEntryMode))
                {
                    PopupUtil.showInfoAlert("Saved changes to Company with id ("+ sId +") successfully.");
                }
            } else {
                PopupUtil.showErrorAlert("Invalid id.");
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void btClearAllClicked(ActionEvent e)
    {
        clearAllHeader();
    }
    
    @FXML
    public void saveModeON(ActionEvent e)
    {
       sLastSelectedId = ""; 
       doHeaderSaveModeWork();

       doGBONumberSaveModeWork();
       doGBOInterestSaveModeWork();
       doGBODocumentChargeSaveModeWork();
       doGBOFormulaSaveModeWork();
       doGBCFormulaSaveModeWork();
       doGBMSSaveModeWork();
       doGOtherSettingsSaveModeWork();
       doGPrintSettingsSaveModeWork();
               
       doSBONumberSaveModeWork();
       doSBOInterestSaveModeWork();
       doSBODocumentChargeSaveModeWork();
       doSBOFormulaSaveModeWork();
       doSBCFormulaSaveModeWork();
       doSBMSSaveModeWork();       
       doSOtherSettingsSaveModeWork();       
       doSPrintSettingsSaveModeWork();
               
       doOCFineChargeSaveModeWork();
               
       doAccOtherSettingsSaveModeWork();
       doAccTodaysSettingsSaveModeWork();
       creditDebitSaveModeWork();        

       doEMIGBONumberSaveModeWork();
       doEMIGBOInterestSaveModeWork();
       doEMIGBODocumentChargeSaveModeWork();
       doEMIGBOFormulaSaveModeWork();
       doEMIGBCFormulaSaveModeWork();
       doEMIGOtherSettingsSaveModeWork();
       doEMIGPrintSettingsSaveModeWork();
    }
    
    public void doHeaderSaveModeWork() {
    
        clearAllHeader();
        txtId.setEditable(false);
        txtId.setMouseTransparent(true);
        txtId.setFocusTraversable(false);     
        txtName.setEditable(true);
        btUpdateHeader.setDisable(true);
        btSaveHeader.setDisable(false);
        try {
            txtId.setText(dbOp.getId(COMPANY_ID));
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.COMPANY_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
        txtGoldMinimumDaysOrMonths.setText("0");
    }
    
    public void doGBONumberSaveModeWork() {
    
        btEditGBONumber.setDisable(true);
        btSaveGBONumber.setDisable(true);
        
        tbGBONumberGenerator.getItems().remove(0, tbGBONumberGenerator.getItems().size());
        txtGBONGPreRow.setText("");
        txtGBONGPrePrefix.setText("");
        txtGBONGPreNumber.setText("");
        txtGBONGCurRow.setText("");
        txtGBONGCurPrefix.setText("");
        txtGBONGCurNumber.setText("");
    }

    public void doGOtherSettingsSaveModeWork() {
    
        txtTodaysGoldRate.setText("");
        txtGoldCompanyRate.setText("");
        txtGoldReductionWt.setText("");
        txtGDefaultPurityValue.setText("");
        txtGDefaultCity.setText("");
        txtGDefaultArea.setText("");
        cbAllowToChangeGAADate.setValue("YES");
        cbAllowToChangeGBCDate.setValue("YES");
        cbAllowToChangeGBODate.setValue("YES");        
        cbAllowToChangeGRBODate.setValue("YES");
        cbAllowToChangeGRBCDate.setValue("YES");

        cbAllowToChangeGAAAmount.setValue("YES");
        cbAllowToChangeGBCAmount.setValue("YES");
        cbAllowToChangeGBOAmount.setValue("YES");        
        cbAllowToChangeGRBOAmount.setValue("YES");
        cbAllowToChangeGRBCAmount.setValue("YES");
        cbAllowToChangeGBOGivenAmount.setValue("YES");
        cbAllowToChangeGBCReceivedAmount.setValue("YES");
        cbVerifyGBCCopies.setValue("YES");
        
        cbAllowToChangeGRBOName.setValue("YES");
        btGSaveOtherSetting.setDisable(true);
    }

    public void doGPrintSettingsSaveModeWork() {
    
        cbGBoPrintOnSaveClicked.setValue("YES"); 
        cbGBCompanyCopyPrint.setValue(DO_NOT_PRINT);        
        cbGBCustomerCopyPrint.setValue(DO_NOT_PRINT);
        cbGBPackingCopyPrint.setValue(DO_NOT_PRINT);
        cbGBoDirectPrint.setValue("YES"); 
        btGSavePrintSetting.setDisable(true);
        
        cbGBCustomerCamera.setValue(DO_NOT_TAKE_PICTURE);
        cbGBJewelCamera.setValue(DO_NOT_TAKE_PICTURE);
        cbGBUserCamera.setValue(DO_NOT_TAKE_PICTURE);
        btGSaveCameraSetting.setDisable(true);
    }
    
    public void doSBOInterestSaveModeWork() {
    
        btAddSBOInterest.setDisable(true);
        btEditSBOInterest.setDisable(true);
        btDeleteSBOInterest.setDisable(true);
        btSaveSBOInterest.setDisable(true);
        
        tbSBOInterest.getItems().remove(0, tbSBOInterest.getItems().size());
    }

    public void doSBODocumentChargeSaveModeWork() {
    
        btAddSBODocumentCharge.setDisable(true);
        btEditSBODocumentCharge.setDisable(true);
        btDeleteSBODocumentCharge.setDisable(true);
        btSaveSBODocumentCharge.setDisable(true);
        
        tbSBODocumentCharge.getItems().remove(0, tbSBODocumentCharge.getItems().size());
    }

    public void doSBOFormulaSaveModeWork() {
    
        btAddSBOFormula.setDisable(true);
        btEditSBOFormula.setDisable(true);
        btDeleteSBOFormula.setDisable(true);
        btSaveSBOFormula.setDisable(true);
        
        tbSBOFormula.getItems().remove(0, tbSBOFormula.getItems().size());
    }

    public void doSBCFormulaSaveModeWork() {
    
        btAddSBCFormula.setDisable(true);
        btEditSBCFormula.setDisable(true);
        btDeleteSBCFormula.setDisable(true);
        btSaveSBCFormula.setDisable(true);
        
        tbSBCFormula.getItems().remove(0, tbSBCFormula.getItems().size());
    }

    public void doSBMSSaveModeWork() {
    
        btAddSBMS.setDisable(true);
        btEditSBMS.setDisable(true);
        btDeleteSBMS.setDisable(true);
        btSaveSBMS.setDisable(true);
        btSaveSReductionMonthsOrDays.setDisable(true);
        btSaveSMinimumMonthsOrDays.setDisable(true);
        
        tbSBMS.getItems().remove(0, tbSBMS.getItems().size());
        txtSilverReductionDaysOrMonths.setText("0");
        txtSilverMinimumDaysOrMonths.setText("0");
    }
    
    public void doSBONumberSaveModeWork() {
    
        btEditSBONumber.setDisable(true);
        btSaveSBONumber.setDisable(true);
        
        tbSBONumberGenerator.getItems().remove(0, tbSBONumberGenerator.getItems().size());
        txtSBONGPreRow.setText("");
        txtSBONGPrePrefix.setText("");
        txtSBONGPreNumber.setText("");
        txtSBONGCurRow.setText("");
        txtSBONGCurPrefix.setText("");
        txtSBONGCurNumber.setText("");
    }

    public void doSOtherSettingsSaveModeWork() {
    
        txtTodaysSilverRate.setText("");
        txtSilverCompanyRate.setText("");
        txtSilverReductionWt.setText("");
        txtSDefaultPurityValue.setText("");
        txtSDefaultCity.setText("");
        txtSDefaultArea.setText("");

        cbAllowToChangeSAADate.setValue("YES");
        cbAllowToChangeSBCDate.setValue("YES");
        cbAllowToChangeSBODate.setValue("YES");  

        cbAllowToChangeSAAAmount.setValue("YES");
        cbAllowToChangeSBCAmount.setValue("YES");
        cbAllowToChangeSBOAmount.setValue("YES"); 
        
        cbAllowToChangeSBOGivenAmount.setValue("YES"); 
        cbAllowToChangeSBCReceivedAmount.setValue("YES"); 
        cbVerifySBCCopies.setValue("YES"); 
        
        btSSaveOtherSetting.setDisable(true);
    }

    public void doAccOtherSettingsSaveModeWork() {
    
        cbAllowToChangeCompExpDate.setValue("YES");
        cbAllowToChangeEmpExpDate.setValue("YES");
        cbAllowToChangeRepExpDate.setValue("YES");  

        cbAllowToChangeCompIncDate.setValue("YES");
        cbAllowToChangeEmpIncDate.setValue("YES");
        cbAllowToChangeEmpIncDate.setValue("YES"); 

        btAccSaveOtherSetting.setDisable(true);
    }
    
    public void doSPrintSettingsSaveModeWork() {
            
        cbSBoPrintOnSaveClicked.setValue("YES"); 
        cbSBCompanyCopyPrint.setValue(DO_NOT_PRINT); 
        cbSBCustomerCopyPrint.setValue(DO_NOT_PRINT); 
        cbSBPackingCopyPrint.setValue(DO_NOT_PRINT); 
        cbSBoDirectPrint.setValue("YES"); 
        btSSavePrintSetting.setDisable(true);
    }
    
    public void doOCFineChargeSaveModeWork() {
    
        btAddOCFineCharge.setDisable(true);
        btEditOCFineCharge.setDisable(true);
        btDeleteOCFineCharge.setDisable(true);
        btSaveOCFineCharge.setDisable(true);
        
        btSaveOCNotice.setDisable(true);
        btSaveCardLostCharge.setDisable(true);
        
        tbOCFineCharges.getItems().remove(0, tbOCFineCharges.getItems().size());
    }
    
    public void doAccTodaysSettingsSaveModeWork() {
    
        dpAccStartingDate.setValue(LocalDate.now());
        txtAccPreDate.setText(DateRelatedCalculations.getPreviousDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpAccStartingDate.getValue())));
        txtAccPreDayAvailableBalance.setText("0");

        dpToBeSetLastDate.setValue(LocalDate.now());
        txtActualAmount.setText("0");
        txtAvailableAmount.setText("0");
        txtDeficitAmount.setText("0");
        
        btAccSaveTodaysSetting.setDisable(true);
        btSaveDateAsLast.setDisable(true);
        btSaveFIlePath.setDisable(true);
        btSaveShareCustomerListFrom.setDisable(true);
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
    public void saveModeOFF(ActionEvent e)
    {               
        doHeaderUpdateModeWork();        
    }
    
    @FXML
    public void txtIdOnAction(ActionEvent e)
    {
        String sId = txtId.getText();
        sLastSelectedId = sId;
        clearAllHeader();
        try {
            HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sId);
            DataTable numberGenValues = dbOp.getAllNumberGeneratorValues(sId, "GOLD", CommonConstants.NORMAL);
            DataTable preCurNumValues = dbOp.getGPreCurNumberValues(sId);
            DataTable interestValues = dbOp.getCompanyInterestValues(sId, "GOLD");
            DataTable documentChargeValues = dbOp.getCompanyDocumentChargeValues(sId, "GOLD");
            DataTable oFormulaValues = dbOp.getCompanyFormulaValues(sId, "GOLD", "OPEN");
            DataTable cFormulaValues = dbOp.getCompanyFormulaValues(sId, "GOLD", "CLOSE");
            DataTable monthSettingValues = dbOp.getCompanyMonthSettingValues(sId, "GOLD");
            DataTable sGoldReductionDaysOrMonthsValues = dbOp.getCompanyDaysOrMonthsValues(sId, "GOLD", "REDUCTION");
            DataTable sGoldMinimumDaysOrMonthsValues = dbOp.getCompanyDaysOrMonthsValues(sId, "GOLD", "MINIMUM");
            DataTable sGoldOtherSettingValues = dbOp.getOtherSettingsValues(sId, "GOLD");

            DataTable sNumberGenValues = dbOp.getAllNumberGeneratorValues(sId, "SILVER", CommonConstants.NORMAL);
            DataTable sPreCurNumValues = dbOp.getSPreCurNumberValues(sId);
            DataTable sInterestValues = dbOp.getCompanyInterestValues(sId, "SILVER");
            DataTable sDocumentChargeValues = dbOp.getCompanyDocumentChargeValues(sId, "SILVER");
            DataTable sOFormulaValues = dbOp.getCompanyFormulaValues(sId, "SILVER", "OPEN");
            DataTable sCFormulaValues = dbOp.getCompanyFormulaValues(sId, "SILVER", "CLOSE");
            DataTable sMonthSettingValues = dbOp.getCompanyMonthSettingValues(sId, "SILVER");
            DataTable sSilverReductionDaysOrMonthsValues = dbOp.getCompanyDaysOrMonthsValues(sId, "SILVER", "REDUCTION");
            DataTable sSilverMinimumDaysOrMonthsValues = dbOp.getCompanyDaysOrMonthsValues(sId, "SILVER", "MINIMUM");
            DataTable sSilverOtherSettingValues = dbOp.getOtherSettingsValues(sId, "SILVER");
            
            DataTable sFineChargeValues = dbOp.getCompanyFineChargeValues(sId);
            DataTable sNoticeValues = dbOp.getNoticeValues(sId);
            
            DataTable sAccOtherSettingValues = dbOp.getAccOtherSettingsValues(sId);
            DataTable sAccTodaysSettingValues = dbOp.getTodaysAccountSettingsValues(sId);
            DataTable sAccStartingSettingValues = dbOp.getStartingAccountSettingsValues(sId);
            
            String sBackupFilePath = dbOp.getBackupFilePath(sId);
            String sCardLostCharge = dbOp.getCardLostCharge(sId);
            
            DataTable emiNumberGenValues = dbOp.getAllNumberGeneratorValues(sId, "GOLD", CommonConstants.EMI);
            DataTable emiPreCurNumValues = dbOp.getEMIGPreCurNumberValues(sId);
            
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues);
                setGoldNumberGeneratorValuesToField(numberGenValues, preCurNumValues);
                setGoldOpenInterestValuesToField(interestValues);
                setGoldOpenDocumentChargeValuesToField(documentChargeValues);
                setGoldOpenFormulaValuesToField(oFormulaValues);
                setGoldCloseFormulaValuesToField(cFormulaValues);
                setGoldBMSValuesToField(monthSettingValues, sGoldReductionDaysOrMonthsValues, sGoldMinimumDaysOrMonthsValues);
                setGoldOtherSettingsToField(sGoldOtherSettingValues);
                        
                setSilverNumberGeneratorValuesToField(sNumberGenValues, sPreCurNumValues);
                setSilverOpenInterestValuesToField(sInterestValues);
                setSilverOpenDocumentChargeValuesToField(sDocumentChargeValues);
                setSilverOpenFormulaValuesToField(sOFormulaValues);
                setSilverCloseFormulaValuesToField(sCFormulaValues);
                setSilverBMSValuesToField(sMonthSettingValues, sSilverReductionDaysOrMonthsValues, sSilverMinimumDaysOrMonthsValues);
                setSilverOtherSettingsToField(sSilverOtherSettingValues);
                
                setAccOtherSettingsToField(sAccOtherSettingValues);
                setAccTodaysSettingsToField(sAccTodaysSettingValues, sAccStartingSettingValues);
                txtFilePath.setText(sBackupFilePath);
                setFineChargeValuesToField(sFineChargeValues, sNoticeValues);
                txtCardLostFineCharge.setText(sCardLostCharge);
                
                setAllCreditValuesToTable(sId);
                setAllDebitValuesToTable(sId);
                
                doGBONumberUpdateModeWork();
                doGBOInterestUpdateModeWork();
                doGBODocumentChargeUpdateModeWork();
                doGBOFormulaUpdateModeWork(); 
                doGBCFormulaUpdateModeWork(); 
                doGBMSUpdateModeWork();
                doGOtherSettingsUpdateModeWork();
                doGPrintSettingsUpdateModeWork();
                
                doSBONumberUpdateModeWork();
                doSBOInterestUpdateModeWork();
                doSBODocumentChargeUpdateModeWork();
                doSBOFormulaUpdateModeWork(); 
                doSBCFormulaUpdateModeWork(); 
                doSBMSUpdateModeWork();
                doSOtherSettingsUpdateModeWork();
                doSPrintSettingsUpdateModeWork();
                        
                doOCFineChargeUpdateModeWork();
                        
                doAccSaveOtherSettingsUpdateModeWork();
                doAccTodaysSettingsUpdateModeWork();
                
                creditDebitUpdateModeWork();
                
                doEMIGBONumberUpdateModeWork();
                doEMIGBOInterestUpdateModeWork();
                doEMIGBODocumentChargeUpdateModeWork();
                doEMIGBOFormulaUpdateModeWork(); 
                doEMIGBCFormulaUpdateModeWork(); 
                doEMIGOtherSettingsUpdateModeWork();
                doEMIGPrintSettingsUpdateModeWork();
                
                setEMIGoldNumberGeneratorValuesToField(emiNumberGenValues, emiPreCurNumValues);
                
            } else {
                PopupUtil.showErrorAlert("Sorry invalid id.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


    public void doHeaderUpdateModeWork() {
    
        clearAllHeader();        
        txtId.setText("");
        txtId.setEditable(true);
        txtId.setMouseTransparent(false);
        txtId.setFocusTraversable(true);     
        txtName.setEditable(false);
        txtName.setMouseTransparent(true);
        txtName.setFocusTraversable(false);     
        btSaveHeader.setDisable(true); 
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.COMPANY_MODULE_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateHeader.setDisable(false);
            } else {
                btUpdateHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        txtId.setText(CommonConstants.CMP_ID_PREFIX);
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
    
    public void doGBONumberUpdateModeWork() {
    
        btEditGBONumber.setDisable(false);
        btSaveGBONumber.setDisable(false);
    }

    public void doGOtherSettingsUpdateModeWork() {
    
        btGSaveOtherSetting.setDisable(false);
    }

    public void doGPrintSettingsUpdateModeWork() {
    
        btGSavePrintSetting.setDisable(false);

        cbGBCompanyCopyPrint.getItems().removeAll(cbGBCompanyCopyPrint.getItems());
        cbGBCustomerCopyPrint.getItems().removeAll(cbGBCustomerCopyPrint.getItems());
        cbGBPackingCopyPrint.getItems().removeAll(cbGBPackingCopyPrint.getItems());
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for(PrintService service : services) {
            cbGBCompanyCopyPrint.getItems().add(service.getName());
            cbGBCustomerCopyPrint.getItems().add(service.getName());
            cbGBPackingCopyPrint.getItems().add(service.getName());
        }
        cbGBCompanyCopyPrint.getItems().add(DO_NOT_PRINT);
        cbGBCustomerCopyPrint.getItems().add(DO_NOT_PRINT);
        cbGBPackingCopyPrint.getItems().add(DO_NOT_PRINT);
        
        cbGBCustomerCamera.getItems().removeAll(cbGBCustomerCamera.getItems());
        cbGBJewelCamera.getItems().removeAll(cbGBJewelCamera.getItems());
        cbGBUserCamera.getItems().removeAll(cbGBUserCamera.getItems());
        try {
            for(Webcam webcam : WebCamWork.getWebCamLists()) {
                cbGBCustomerCamera.getItems().add(webcam.getName());
                cbGBJewelCamera.getItems().add(webcam.getName());
                cbGBUserCamera.getItems().add(webcam.getName());
            }
        } catch (Exception e) {
            // No webcam available or driver error — skip
        }
        cbGBCustomerCamera.getItems().add(DO_NOT_TAKE_PICTURE);
        cbGBJewelCamera.getItems().add(DO_NOT_TAKE_PICTURE);
        cbGBUserCamera.getItems().add(DO_NOT_TAKE_PICTURE);
        btGSaveCameraSetting.setDisable(false);
    }
    
    public void doSBOInterestUpdateModeWork() {
    
        btAddSBOInterest.setDisable(false);
        btEditSBOInterest.setDisable(false);
        btDeleteSBOInterest.setDisable(false);
        btSaveSBOInterest.setDisable(false);
    }

    public void doSBODocumentChargeUpdateModeWork() {
    
        btAddSBODocumentCharge.setDisable(false);
        btEditSBODocumentCharge.setDisable(false);
        btDeleteSBODocumentCharge.setDisable(false);
        btSaveSBODocumentCharge.setDisable(false);
    }

    public void doSBOFormulaUpdateModeWork() {
    
        btAddSBOFormula.setDisable(false);
        btEditSBOFormula.setDisable(false);
        btDeleteSBOFormula.setDisable(false);
        btSaveSBOFormula.setDisable(false);
    }

    public void doSBCFormulaUpdateModeWork() {
    
        btAddSBCFormula.setDisable(false);
        btEditSBCFormula.setDisable(false);
        btDeleteSBCFormula.setDisable(false);
        btSaveSBCFormula.setDisable(false);
    }

    public void doSBMSUpdateModeWork() {
    
        btAddSBMS.setDisable(false);
        btEditSBMS.setDisable(false);
        btDeleteSBMS.setDisable(false);
        btSaveSBMS.setDisable(false);
        btSaveSReductionMonthsOrDays.setDisable(false);
        btSaveSMinimumMonthsOrDays.setDisable(false);
    }
    
    public void doSBONumberUpdateModeWork() {
    
        btEditSBONumber.setDisable(false);
        btSaveSBONumber.setDisable(false);
    }

    public void doSOtherSettingsUpdateModeWork() {
    
        btSSaveOtherSetting.setDisable(false);
    }

    public void doAccSaveOtherSettingsUpdateModeWork() {
    
        btAccSaveOtherSetting.setDisable(false);
    }
    
    public void doSPrintSettingsUpdateModeWork() {
    
        cbSBCompanyCopyPrint.getItems().removeAll(cbSBCompanyCopyPrint.getItems());
        cbSBCustomerCopyPrint.getItems().removeAll(cbSBCustomerCopyPrint.getItems());
        cbSBPackingCopyPrint.getItems().removeAll(cbSBPackingCopyPrint.getItems());
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for(PrintService service : services) {
            cbSBCompanyCopyPrint.getItems().add(service.getName());
            cbSBCustomerCopyPrint.getItems().add(service.getName());
            cbSBPackingCopyPrint.getItems().add(service.getName());
        }
        cbSBCompanyCopyPrint.getItems().add(DO_NOT_PRINT);
        cbSBCustomerCopyPrint.getItems().add(DO_NOT_PRINT);
        cbSBPackingCopyPrint.getItems().add(DO_NOT_PRINT);
        
        btSSavePrintSetting.setDisable(false);
    }
    
    public void doAccTodaysSettingsUpdateModeWork() {
    
        btAccSaveTodaysSetting.setDisable(false);
        btSaveDateAsLast.setDisable(false);
        btSaveFIlePath.setDisable(false);
        btSaveShareCustomerListFrom.setDisable(false);
    }
    
    public void doOCFineChargeUpdateModeWork() {
    
        btAddOCFineCharge.setDisable(false);
        btEditOCFineCharge.setDisable(false);
        btDeleteOCFineCharge.setDisable(false);
        btSaveOCFineCharge.setDisable(false);
        btSaveOCNotice.setDisable(false);
        btSaveCardLostCharge.setDisable(false);
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

    public void setGoldBMSValuesToField(DataTable monthSettingValues, DataTable sReductionDaysOrMonthsValues, DataTable sMinimumDaysOrMonthsValues) {
        
        tbGBMS.getItems().remove(0, tbGBMS.getItems().size());
        for(int i=0; i<monthSettingValues.getRowCount(); i++) {            
            String sFromDate = monthSettingValues.getRow(i).getColumn(0).toString();
            String sToDate = monthSettingValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(monthSettingValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(monthSettingValues.getRow(i).getColumn(3).toString());
            double dDAsMonth= Double.parseDouble(monthSettingValues.getRow(i).getColumn(4).toString());
            tbGBMS.getItems().add(new MonthSettingBean(sFromDate, sToDate, dFrom, dTo, dDAsMonth));
        }
        
        if(sReductionDaysOrMonthsValues.getRowCount() > 0) {
            txtGoldReductionDaysOrMonths.setText(sReductionDaysOrMonthsValues.getRow(0).getColumn(0).toString());
            cbGoldReductionDaysOrMonths.setValue(sReductionDaysOrMonthsValues.getRow(0).getColumn(1).toString());
        } else {
            txtGoldReductionDaysOrMonths.setText("0");
        }
        
        if(sMinimumDaysOrMonthsValues.getRowCount() > 0) {
            txtGoldMinimumDaysOrMonths.setText(sMinimumDaysOrMonthsValues.getRow(0).getColumn(0).toString());
            cbGoldMinimumDaysOrMonths.setValue(sMinimumDaysOrMonthsValues.getRow(0).getColumn(1).toString());
        } else {
            txtGoldMinimumDaysOrMonths.setText("0");
        }
        
    }
    
    public void setGoldNumberGeneratorValuesToField(DataTable numberGenValues, DataTable preCurNumValues) {
    
        tbGBONumberGenerator.getItems().remove(0, tbGBONumberGenerator.getItems().size());
        
        for(int i=0; i<numberGenValues.getRowCount(); i++) {
            
            int iRowNum = Integer.parseInt(numberGenValues.getRow(i).getColumn(0).toString());
            String sPrefix = (String) numberGenValues.getRow(i).getColumn(1).toString();
            long lFrom = Long.parseLong(numberGenValues.getRow(i).getColumn(2).toString());
            long lTo = Long.parseLong(numberGenValues.getRow(i).getColumn(3).toString());
            tbGBONumberGenerator.getItems().add(new NumberGeneratorBean(iRowNum, sPrefix, lFrom, lTo));
        }
        
        if(numberGenValues.getRowCount() == 0) {
            for(int i=1; i<=5; i++) {
                tbGBONumberGenerator.getItems().add(new NumberGeneratorBean(i, "", 0, 0));
            }
        }
        txtGBONGPreRow.setText(preCurNumValues.getRow(0).getColumn(0) != null ? preCurNumValues.getRow(0).getColumn(0).toString(): "0");
        txtGBONGPrePrefix.setText(preCurNumValues.getRow(0).getColumn(1) != null ? preCurNumValues.getRow(0).getColumn(1).toString(): "");
        txtGBONGPreNumber.setText(preCurNumValues.getRow(0).getColumn(2) != null ? preCurNumValues.getRow(0).getColumn(2).toString(): "0");
        txtGBONGCurRow.setText(preCurNumValues.getRow(0).getColumn(3) != null ? preCurNumValues.getRow(0).getColumn(3).toString(): "0");
        txtGBONGCurPrefix.setText(preCurNumValues.getRow(0).getColumn(4) != null ? preCurNumValues.getRow(0).getColumn(4).toString(): "");
        txtGBONGCurNumber.setText(preCurNumValues.getRow(0).getColumn(5) != null ? preCurNumValues.getRow(0).getColumn(5).toString(): "0");
    }

    public void setEMIGoldNumberGeneratorValuesToField(DataTable numberGenValues, DataTable preCurNumValues) {
    
        tbGBONumberGenerator1.getItems().remove(0, tbGBONumberGenerator1.getItems().size());
        
        for(int i=0; i<numberGenValues.getRowCount(); i++) {
            
            int iRowNum = Integer.parseInt(numberGenValues.getRow(i).getColumn(0).toString());
            String sPrefix = (String) numberGenValues.getRow(i).getColumn(1).toString();
            long lFrom = Long.parseLong(numberGenValues.getRow(i).getColumn(2).toString());
            long lTo = Long.parseLong(numberGenValues.getRow(i).getColumn(3).toString());
            tbGBONumberGenerator1.getItems().add(new NumberGeneratorBean(iRowNum, sPrefix, lFrom, lTo));
        }
        
        if(numberGenValues.getRowCount() == 0) {
            for(int i=1; i<=5; i++) {
                tbGBONumberGenerator1.getItems().add(new NumberGeneratorBean(i, "", 0, 0));
            }
        }
        txtGBONGPreRow1.setText(preCurNumValues.getRow(0).getColumn(0) != null ? preCurNumValues.getRow(0).getColumn(0).toString(): "0");
        txtGBONGPrePrefix1.setText(preCurNumValues.getRow(0).getColumn(1) != null ? preCurNumValues.getRow(0).getColumn(1).toString(): "");
        txtGBONGPreNumber1.setText(preCurNumValues.getRow(0).getColumn(2) != null ? preCurNumValues.getRow(0).getColumn(2).toString(): "0");
        txtGBONGCurRow1.setText(preCurNumValues.getRow(0).getColumn(3) != null ? preCurNumValues.getRow(0).getColumn(3).toString(): "0");
        txtGBONGCurPrefix1.setText(preCurNumValues.getRow(0).getColumn(4) != null ? preCurNumValues.getRow(0).getColumn(4).toString(): "");
        txtGBONGCurNumber1.setText(preCurNumValues.getRow(0).getColumn(5) != null ? preCurNumValues.getRow(0).getColumn(5).toString(): "0");
    }
    
    public void setGoldOtherSettingsToField(DataTable otherSettingsValues) {
        
        if(otherSettingsValues.getRowCount() > 0) {
            txtTodaysGoldRate.setText(otherSettingsValues.getRow(0).getColumn(0).toString());
            txtGoldCompanyRate.setText(otherSettingsValues.getRow(0).getColumn(1).toString());
            txtGoldReductionWt.setText(otherSettingsValues.getRow(0).getColumn(2).toString());
            txtGDefaultPurityValue.setText(otherSettingsValues.getRow(0).getColumn(3).toString());
            txtGDefaultCity.setText(otherSettingsValues.getRow(0).getColumn(4).toString());
            txtGDefaultArea.setText(otherSettingsValues.getRow(0).getColumn(5).toString());
            cbAllowToChangeGBODate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(6).toString()) ? "YES" : "NO");
            cbAllowToChangeGBCDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(7).toString()) ? "YES" : "NO");
            cbAllowToChangeGAADate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(8).toString()) ? "YES" : "NO");
            cbAllowToChangeGRBODate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(9).toString()) ? "YES" : "NO");
            cbAllowToChangeGRBCDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(10).toString()) ? "YES" : "NO");
            cbAllowToChangeGRBOName.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(11).toString()) ? "YES" : "NO");
            cbAllowToChangeGBOAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(12).toString()) ? "YES" : "NO");
            cbAllowToChangeGBCAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(13).toString()) ? "YES" : "NO");
            cbAllowToChangeGAAAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(14).toString()) ? "YES" : "NO");
            cbAllowToChangeGRBOAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(15).toString()) ? "YES" : "NO");
            cbAllowToChangeGRBCAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(16).toString()) ? "YES" : "NO");            
            cbAllowToChangeGBOGivenAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(26).toString()) ? "YES" : "NO");
            cbAllowToChangeGBCReceivedAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(27).toString()) ? "YES" : "NO");
            
            cbVerifyGBCCopies.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(28).toString()) ? "YES" : "NO");
            //print            
            cbGBoPrintOnSaveClicked.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(17).toString()) ? "YES" : "NO");
            cbGBCompanyCopyPrint.setValue(otherSettingsValues.getRow(0).getColumn(18).toString());
            cbGBCustomerCopyPrint.setValue(otherSettingsValues.getRow(0).getColumn(19).toString());            
            cbGBPackingCopyPrint.setValue(otherSettingsValues.getRow(0).getColumn(20).toString());   
            cbGBoDirectPrint.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(21).toString()) ? "YES" : "NO");            
            //camera 
            cbGBCustomerCamera.setValue(otherSettingsValues.getRow(0).getColumn(22).toString());
            cbGBJewelCamera.setValue(otherSettingsValues.getRow(0).getColumn(23).toString());
            cbGBUserCamera.setValue(otherSettingsValues.getRow(0).getColumn(24).toString());
            txtCamTempFilePath.setText(otherSettingsValues.getRow(0).getColumn(25) != null 
                    ? otherSettingsValues.getRow(0).getColumn(25).toString()
                    : "");
        } else {
            txtTodaysGoldRate.setText("");
            txtGoldCompanyRate.setText("");
            txtGoldReductionWt.setText("");
            txtGDefaultPurityValue.setText("");
            txtGDefaultCity.setText(""); 
            txtGDefaultArea.setText("");
            cbAllowToChangeGBODate.setValue("YES");
            cbAllowToChangeGBCDate.setValue("YES");
            cbAllowToChangeGAADate.setValue("YES");
            cbAllowToChangeGRBODate.setValue("YES");
            cbAllowToChangeGRBCDate.setValue("YES");
            cbAllowToChangeGRBOName.setValue("YES");
            cbAllowToChangeGBOAmount.setValue("YES");
            cbAllowToChangeGBCAmount.setValue("YES");
            cbAllowToChangeGAAAmount.setValue("YES");
            cbAllowToChangeGRBOAmount.setValue("YES");
            cbAllowToChangeGRBCAmount.setValue("YES");
            cbAllowToChangeGBOGivenAmount.setValue("YES");
            cbAllowToChangeGBCReceivedAmount.setValue("YES");
            cbVerifyGBCCopies.setValue("YES");
            //print
            cbGBoPrintOnSaveClicked.setValue("YES");
            cbGBCompanyCopyPrint.setValue(DO_NOT_PRINT);
            cbGBCustomerCopyPrint.setValue(DO_NOT_PRINT);
            cbGBPackingCopyPrint.setValue(DO_NOT_PRINT);
            cbGBoDirectPrint.setValue("YES");
            //camera 
            cbGBCustomerCamera.setValue(DO_NOT_TAKE_PICTURE);
            cbGBJewelCamera.setValue(DO_NOT_TAKE_PICTURE);
            cbGBUserCamera.setValue(DO_NOT_TAKE_PICTURE);    
            txtCamTempFilePath.setText("");
        }
    }

    public void setAccTodaysSettingsToField(DataTable todaysAccount, DataTable sAccStartingSettingValues) {
        
        if(sAccStartingSettingValues.getRowCount() > 0) {
            dpAccStartingDate.setValue(LocalDate.parse(sAccStartingSettingValues.getRow(0).getColumn(4).toString(), CommonConstants.DATETIMEFORMATTER));
            txtAccPreDate.setText(sAccStartingSettingValues.getRow(0).getColumn(0).toString());
            txtAccPreDayAvailableBalance.setText(sAccStartingSettingValues.getRow(0).getColumn(6).toString());      
            sAccStartingDate = sAccStartingSettingValues.getRow(0).getColumn(4).toString();
        } else {
            dpAccStartingDate.setValue(LocalDate.now());
            txtAccPreDate.setText("");
            txtAccPreDayAvailableBalance.setText("0");
            sAccStartingDate = null;
        }

        if(todaysAccount.getRowCount() > 0) {
            dpToBeSetLastDate.setValue(LocalDate.parse(todaysAccount.getRow(0).getColumn(4).toString(), CommonConstants.DATETIMEFORMATTER));
            txtActualAmount.setText(todaysAccount.getRow(0).getColumn(5).toString());
            txtAvailableAmount.setText(todaysAccount.getRow(0).getColumn(6).toString());            
            txtDeficitAmount.setText(todaysAccount.getRow(0).getColumn(7).toString());     
            sAccLastDate =todaysAccount.getRow(0).getColumn(4).toString();
        } else {
            dpToBeSetLastDate.setValue(LocalDate.now());
            txtActualAmount.setText("0");
            txtAvailableAmount.setText("0");
            txtDeficitAmount.setText("0");
            sAccLastDate = null;
        }        
    }
    
    public void setSilverOtherSettingsToField(DataTable otherSettingsValues) {
        
        if(otherSettingsValues.getRowCount() > 0) {
            txtTodaysSilverRate.setText(otherSettingsValues.getRow(0).getColumn(0).toString());
            txtSilverCompanyRate.setText(otherSettingsValues.getRow(0).getColumn(1).toString());
            txtSilverReductionWt.setText(otherSettingsValues.getRow(0).getColumn(2).toString());            
            txtSDefaultPurityValue.setText(otherSettingsValues.getRow(0).getColumn(3).toString());
            txtSDefaultCity.setText(otherSettingsValues.getRow(0).getColumn(4).toString());
            txtSDefaultArea.setText(otherSettingsValues.getRow(0).getColumn(5).toString());
            cbAllowToChangeSBODate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(6).toString()) ? "YES" : "NO");
            cbAllowToChangeSBCDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(7).toString()) ? "YES" : "NO");
            cbAllowToChangeSAADate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(8).toString()) ? "YES" : "NO");
            cbAllowToChangeSBOAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(9).toString()) ? "YES" : "NO");
            cbAllowToChangeSBCAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(10).toString()) ? "YES" : "NO");
            cbAllowToChangeSAAAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(11).toString()) ? "YES" : "NO");
            cbAllowToChangeSBOGivenAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(26).toString()) ? "YES" : "NO");
            cbAllowToChangeSBCReceivedAmount.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(27).toString()) ? "YES" : "NO");
            cbVerifySBCCopies.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(28).toString()) ? "YES" : "NO");
            //print
            cbSBoPrintOnSaveClicked.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(17).toString()) ? "YES" : "NO");
            cbSBCompanyCopyPrint.setValue(otherSettingsValues.getRow(0).getColumn(18).toString());
            cbSBCustomerCopyPrint.setValue(otherSettingsValues.getRow(0).getColumn(19).toString());            
            cbSBPackingCopyPrint.setValue(otherSettingsValues.getRow(0).getColumn(20).toString());   
            cbSBoDirectPrint.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(21).toString()) ? "YES" : "NO");            
            
        } else {
            txtTodaysSilverRate.setText("");
            txtSilverCompanyRate.setText("");
            txtSilverReductionWt.setText("");
            txtSDefaultPurityValue.setText("");
            txtSDefaultCity.setText("");   
            txtSDefaultArea.setText("");   
            cbAllowToChangeSBODate.setValue("YES");
            cbAllowToChangeSBCDate.setValue("YES");
            cbAllowToChangeSAADate.setValue("YES");            
            cbAllowToChangeSBOAmount.setValue("YES");
            cbAllowToChangeSBCAmount.setValue("YES");
            cbAllowToChangeSAAAmount.setValue("YES"); 
            cbAllowToChangeSBOGivenAmount.setValue("YES");
            cbAllowToChangeSBCReceivedAmount.setValue("YES");
            cbVerifySBCCopies.setValue("YES");
            //print
            cbSBoPrintOnSaveClicked.setValue("YES");
            cbSBCompanyCopyPrint.setValue(DO_NOT_PRINT);
            cbSBCustomerCopyPrint.setValue(DO_NOT_PRINT);
            cbSBPackingCopyPrint.setValue(DO_NOT_PRINT);
            cbSBoDirectPrint.setValue("YES");
        }

    }
    
    public void setSilverOpenInterestValuesToField(DataTable interestValues) {

        tbSBOInterest.getItems().remove(0, tbSBOInterest.getItems().size());
        for(int i=0; i<interestValues.getRowCount(); i++) {    
            String sFromDate = interestValues.getRow(i).getColumn(0).toString();
            String sToDate = interestValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(interestValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(interestValues.getRow(i).getColumn(3).toString());
            double dInterest = Double.parseDouble(interestValues.getRow(i).getColumn(4).toString());
            tbSBOInterest.getItems().add(new InterestBean(sFromDate, sToDate, dFrom, dTo, dInterest));
        }
    }

    public void setSilverOpenDocumentChargeValuesToField(DataTable documentChargeValues) {
        
        tbSBODocumentCharge.getItems().remove(0, tbSBODocumentCharge.getItems().size());
        for(int i=0; i<documentChargeValues.getRowCount(); i++) {            
            String sFromDate = documentChargeValues.getRow(i).getColumn(0).toString();
            String sToDate = documentChargeValues.getRow(i).getColumn(1).toString();            
            double dFrom = Double.parseDouble(documentChargeValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(documentChargeValues.getRow(i).getColumn(3).toString());
            double dDocumentCharge = Double.parseDouble(documentChargeValues.getRow(i).getColumn(4).toString());
            tbSBODocumentCharge.getItems().add(new DocumentChargeBean(sFromDate, sToDate, dFrom, dTo, dDocumentCharge));
        }
    }

    public void setSilverOpenFormulaValuesToField(DataTable formulaValues) {
        
        tbSBOFormula.getItems().remove(0, tbSBOFormula.getItems().size());
        for(int i=0; i<formulaValues.getRowCount(); i++) {            
            String sFromDate = formulaValues.getRow(i).getColumn(0).toString();
            String sToDate = formulaValues.getRow(i).getColumn(1).toString();                        
            double dFrom = Double.parseDouble(formulaValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(formulaValues.getRow(i).getColumn(3).toString());
            String sFormula = formulaValues.getRow(i).getColumn(4).toString();
            tbSBOFormula.getItems().add(new FormulaBean(sFromDate, sToDate, dFrom, dTo, sFormula));
        }
    }

    public void setSilverCloseFormulaValuesToField(DataTable formulaValues) {
        
        tbSBCFormula.getItems().remove(0, tbSBCFormula.getItems().size());
        for(int i=0; i<formulaValues.getRowCount(); i++) {            
            String sFromDate = formulaValues.getRow(i).getColumn(0).toString();
            String sToDate = formulaValues.getRow(i).getColumn(1).toString();                        
            double dFrom = Double.parseDouble(formulaValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(formulaValues.getRow(i).getColumn(3).toString());
            String sFormula = formulaValues.getRow(i).getColumn(4).toString();
            tbSBCFormula.getItems().add(new FormulaBean(sFromDate, sToDate, dFrom, dTo, sFormula));
        }
    }

    public void setSilverBMSValuesToField(DataTable monthSettingValues, DataTable sReductionDaysOrMonths, DataTable sMinimumDaysOrMonths) {
        
        tbSBMS.getItems().remove(0, tbSBMS.getItems().size());
        for(int i=0; i<monthSettingValues.getRowCount(); i++) {    
            String sFromDate = monthSettingValues.getRow(i).getColumn(0).toString();
            String sToDate = monthSettingValues.getRow(i).getColumn(1).toString();                        
            double dFrom = Double.parseDouble(monthSettingValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(monthSettingValues.getRow(i).getColumn(3).toString());
            double dDAsMonth= Double.parseDouble(monthSettingValues.getRow(i).getColumn(4).toString());
            tbSBMS.getItems().add(new MonthSettingBean(sFromDate, sToDate, dFrom, dTo, dDAsMonth));
        }
        
        if(sReductionDaysOrMonths.getRowCount() > 0) {
            txtSilverReductionDaysOrMonths.setText(sReductionDaysOrMonths.getRow(0).getColumn(0).toString());
            cbSilverReductionDaysOrMonths.setValue(sReductionDaysOrMonths.getRow(0).getColumn(1).toString());
        } else {
            txtSilverReductionDaysOrMonths.setText("0");
        }        

        if(sMinimumDaysOrMonths.getRowCount() > 0) {
            txtSilverMinimumDaysOrMonths.setText(sMinimumDaysOrMonths.getRow(0).getColumn(0).toString());
            cbSilverMinimumDaysOrMonths.setValue(sMinimumDaysOrMonths.getRow(0).getColumn(1).toString());
        } else {
            txtSilverMinimumDaysOrMonths.setText("0");
        }                
    }
    
    public void setSilverNumberGeneratorValuesToField(DataTable numberGenValues, DataTable preCurNumValues) {
    
        tbSBONumberGenerator.getItems().remove(0, tbSBONumberGenerator.getItems().size());
        
        for(int i=0; i<numberGenValues.getRowCount(); i++) {
            
            int iRowNum = Integer.parseInt(numberGenValues.getRow(i).getColumn(0).toString());
            String sPrefix = (String) numberGenValues.getRow(i).getColumn(1).toString();
            long lFrom = Long.parseLong(numberGenValues.getRow(i).getColumn(2).toString());
            long lTo = Long.parseLong(numberGenValues.getRow(i).getColumn(3).toString());
            tbSBONumberGenerator.getItems().add(new NumberGeneratorBean(iRowNum, sPrefix, lFrom, lTo));
        }
        
        if(numberGenValues.getRowCount() == 0) {
            for(int i=1; i<=5; i++) {
                tbSBONumberGenerator.getItems().add(new NumberGeneratorBean(i, "", 0, 0));
            }
        }
        txtSBONGPreRow.setText(preCurNumValues.getRow(0).getColumn(0) != null ? preCurNumValues.getRow(0).getColumn(0).toString(): "0");
        txtSBONGPrePrefix.setText(preCurNumValues.getRow(0).getColumn(1) != null ? preCurNumValues.getRow(0).getColumn(1).toString(): "");
        txtSBONGPreNumber.setText(preCurNumValues.getRow(0).getColumn(2) != null ? preCurNumValues.getRow(0).getColumn(2).toString(): "0");
        txtSBONGCurRow.setText(preCurNumValues.getRow(0).getColumn(3) != null ? preCurNumValues.getRow(0).getColumn(3).toString(): "0");
        txtSBONGCurPrefix.setText(preCurNumValues.getRow(0).getColumn(4) != null ? preCurNumValues.getRow(0).getColumn(4).toString(): "");
        txtSBONGCurNumber.setText(preCurNumValues.getRow(0).getColumn(5) != null ? preCurNumValues.getRow(0).getColumn(5).toString(): "0");
    }
    
    public void clearAllHeader()
    {
        txtName.setText("");
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtLCHolderName.setText("");
        txtLCNumber.setText("");
        dpLCDate.setValue(LocalDate.now());
        txtMobileNumber.setText("");
        txtLandlineNumber.setText("");
        txtGoldPreBillNumber.setText("");
        txtSilverPreBillNumber.setText("");
        cbInterestType.setValue("MONTH");
        cbStatus.setValue("ACTIVE");        
        txtNote.setText("");        
    }
    
    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtId.setText(headerValues.get("ID"));
        cbcompanyType.setValue(headerValues.get("TYPE"));
        txtName.setText(headerValues.get("NAME"));
        txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
        txtStreetName.setText(headerValues.get("STREET"));
        txtArea.setText(headerValues.get("AREA"));
        txtCity.setText(headerValues.get("CITY"));
        txtState.setText(headerValues.get("STATE"));
        txtLCHolderName.setText(headerValues.get("LC_HOLDER_NAME"));
        txtLCNumber.setText(headerValues.get("LC_NUMBER"));        
        dpLCDate.setValue(LocalDate.parse(headerValues.get("LC_DATED"), CommonConstants.DATETIMEFORMATTER));
        txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
        txtLandlineNumber.setText(headerValues.get("LANDLINE_NUMBER"));
        txtGoldPreBillNumber.setText(headerValues.get("GOLD_PRE_NUMBER"));
        txtSilverPreBillNumber.setText(headerValues.get("SILVER_PRE_NUMBER"));
        cbInterestType.setValue(headerValues.get("DAY_OR_MONTHLY_INTEREST"));
        cbStatus.setValue(headerValues.get("STATUS"));
        txtNote.setText(headerValues.get("NOTE"));    
        cbAutoBillGeneration.setValue(Boolean.valueOf(headerValues.get("AUTO_BILL_GENERATION")) ? "YES" : "NO");
        cbEntryMode.setValue(Boolean.valueOf(headerValues.get("ENTRY_MODE")) ? "YES" : "NO");
    }

    public void setFineChargeValuesToField(DataTable documentChargeValues, DataTable sNoticeValues) {
        
        tbOCFineCharges.getItems().remove(0, tbOCFineCharges.getItems().size());
        for(int i=0; i<documentChargeValues.getRowCount(); i++) {            
            String sFromDate = documentChargeValues.getRow(i).getColumn(0).toString();
            String sToDate = documentChargeValues.getRow(i).getColumn(1).toString();
            double dFrom = Double.parseDouble(documentChargeValues.getRow(i).getColumn(2).toString());
            double dTo = Double.parseDouble(documentChargeValues.getRow(i).getColumn(3).toString());
            String sCalculation = documentChargeValues.getRow(i).getColumn(4).toString();
            double dFineCharge = Double.parseDouble(documentChargeValues.getRow(i).getColumn(5).toString());
            tbOCFineCharges.getItems().add(new FineChargeBean(sFromDate, sToDate, dFrom, dTo, sCalculation, dFineCharge));
        }

        String sNoticeDate = sNoticeValues.getRow(0).getColumn(0).toString();
        String sNoticeAmount = sNoticeValues.getRow(0).getColumn(1).toString();       
        
                
        if(sNoticeDate != null && !sNoticeDate.isEmpty()) {        
            dpNoticeTillDate.setValue(LocalDate.parse(sNoticeDate, CommonConstants.DATETIMEFORMATTER));
        }
        txtNoticeChargeAmount.setText(sNoticeAmount);
    }
    
    public boolean isValidHeaderValues(String sName)
    {
        return !sName.isEmpty();
    }
    
    @FXML
    private void btEditGBONumberClicked(ActionEvent event) {
        
        int index = tbGBONumberGenerator.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenNumberGeneratorDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldOpenNumberGeneratorDialogUIController gon = (GoldOpenNumberGeneratorDialogUIController) loader.getController();
            gon.setParent(this, false, true);
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
    private void txtGBONGCurRowActionPerformed(ActionEvent event) {
        
        int iCurRow = Integer.parseInt(txtGBONGCurRow.getText()) - 1;
        if(iCurRow < 5) {
            NumberGeneratorBean bean = tbGBONumberGenerator.getItems().get(iCurRow);
            txtGBONGCurPrefix.setText(bean.getSPrefix());
            txtGBONGCurNumber.setText(Long.toString(bean.getLFrom()));
        }
    }

    @FXML
    private void btSaveGBONumberClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        int iGoldCurBillRowNumber = Integer.parseInt(txtGBONGCurRow.getText());
        String sGoldCurBillPrefix = txtGBONGCurPrefix.getText();
        String sGoldCurBillNumber = txtGBONGCurNumber.getText();        
        ObservableList<NumberGeneratorBean> tableValues = tbGBONumberGenerator.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllNumberGenerator(sId, "GOLD", CommonConstants.NORMAL)) {
                    if(dbOp.saveNumberGeneratorRecords(sId, tableValues, "GOLD", CommonConstants.NORMAL)) {
                        if(dbOp.updateGoldCurBillNumber(sId, iGoldCurBillRowNumber, sGoldCurBillPrefix, sGoldCurBillNumber)) {
                            PopupUtil.showInfoAlert("Number generation format for gold bill opening saved successfully to the id("+sId+").");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                if(dbOp.deleteAllCompanyInterest(sId, "GOLD")) {
                    if(dbOp.saveCompanyInterestRecords(sId, tableValues, "GOLD")) {
                        PopupUtil.showInfoAlert("Interest for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
                if(dbOp.deleteAllCompanyDocumentCharge(sId, "GOLD")) {
                    if(dbOp.saveCompanyDocumentChargeRecords(sId, tableValues, "GOLD")) {
                        PopupUtil.showInfoAlert("Document charges for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
                if(dbOp.deleteAllCompanyFormula(sId, "GOLD", "OPEN")) {
                    if(dbOp.saveCompanyFormulaRecords(sId, tableValues, "GOLD", "OPEN")) {
                        PopupUtil.showInfoAlert("Formula for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
                if(dbOp.deleteAllCompanyFormula(sId, "GOLD", "CLOSE")) {
                    if(dbOp.saveCompanyFormulaRecords(sId, tableValues, "GOLD", "CLOSE")) {
                        PopupUtil.showInfoAlert("Formula for gold bill closing saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
                if(dbOp.deleteAllCompanyMonthSetting(sId, "GOLD")) {
                    if(dbOp.saveCompanyMonthSettingRecords(sId, tableValues, "GOLD")) {
                        PopupUtil.showInfoAlert("Month settings for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }

    @FXML
    private void btSaveGoldReductionMonthsOrDaysClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sValue = txtGoldReductionDaysOrMonths.getText();
        String sType = cbGoldReductionDaysOrMonths.getSelectionModel().getSelectedItem();
        double dValue = "".equals(sValue) ? 0 : Double.parseDouble(sValue);
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteReductionOrMinimumDaysOrMonthsValues(sId, "GOLD", "REDUCTION")) {
                    if(dbOp.saveReductionOrMinimumDaysOrMonthsValues(sId, dValue, sType, "GOLD", "REDUCTION")) {
                        PopupUtil.showInfoAlert("Reduction months or days for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }
    
    @FXML
    private void btSaveGoldMinimumMonthsOrDaysClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sValue = txtGoldMinimumDaysOrMonths.getText();
        String sType = cbGoldMinimumDaysOrMonths.getSelectionModel().getSelectedItem();
        double dValue = "".equals(sValue) ? 0 : Double.parseDouble(sValue);
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteReductionOrMinimumDaysOrMonthsValues(sId, "GOLD", "MINIMUM")) {
                    if(dbOp.saveReductionOrMinimumDaysOrMonthsValues(sId, dValue, sType, "GOLD", "MINIMUM")) {
                        PopupUtil.showInfoAlert("Minimum months or days for silver bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                     
    }
    
    @FXML
    private void btEditSBONumberClicked(ActionEvent event) {
        
        int index = tbSBONumberGenerator.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenNumberGeneratorDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverOpenNumberGeneratorDialogUIController gon = (SilverOpenNumberGeneratorDialogUIController) loader.getController();
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
    private void txtSBONGCurRowActionPerformed(ActionEvent event) {
        
        int iCurRow = Integer.parseInt(txtSBONGCurRow.getText()) - 1;
        if(iCurRow < 5) {
            NumberGeneratorBean bean = tbSBONumberGenerator.getItems().get(iCurRow);
            txtSBONGCurPrefix.setText(bean.getSPrefix());
            txtSBONGCurNumber.setText(Long.toString(bean.getLFrom()));
        }
    }

    @FXML
    private void btSaveSBONumberClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        int iGoldCurBillRowNumber = Integer.parseInt(txtSBONGCurRow.getText());
        String sGoldCurBillPrefix = txtSBONGCurPrefix.getText();
        String sGoldCurBillNumber = txtSBONGCurNumber.getText();        
        ObservableList<NumberGeneratorBean> tableValues = tbSBONumberGenerator.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllNumberGenerator(sId, "SILVER", CommonConstants.NORMAL)) {
                    if(dbOp.saveNumberGeneratorRecords(sId, tableValues, "SILVER", CommonConstants.NORMAL)) {
                        if(dbOp.updateSilverCurBillNumber(sId, iGoldCurBillRowNumber, sGoldCurBillPrefix, sGoldCurBillNumber)) {
                            PopupUtil.showInfoAlert("Number generation format for gold bill opening saved successfully to the id("+sId+").");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btAddSBOInterestClicked(ActionEvent event) {
                
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenInterestDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        SilverOpenInterestDialogUIController gon = (SilverOpenInterestDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }

    @FXML
    private void btEditSBOInterestClicked(ActionEvent event) {
        
        int index = tbSBOInterest.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenInterestDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverOpenInterestDialogUIController gon = (SilverOpenInterestDialogUIController) loader.getController();
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
    private void btDeleteSBOInterestClicked(ActionEvent event) {
        
        int index = tbSBOInterest.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbSBOInterest.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveSBOInterestClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<InterestBean> tableValues = tbSBOInterest.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllCompanyInterest(sId, "SILVER")) {
                    if(dbOp.saveCompanyInterestRecords(sId, tableValues, "SILVER")) {
                        PopupUtil.showInfoAlert("Interest for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void btAddSBODocumentChargeClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenDocumentChargeDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        SilverOpenDocumentChargeDialogUIController gon = (SilverOpenDocumentChargeDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }

    @FXML
    private void btEditSBODocumentChargeClicked(ActionEvent event) {

        int index = tbSBODocumentCharge.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenDocumentChargeDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverOpenDocumentChargeDialogUIController gon = (SilverOpenDocumentChargeDialogUIController) loader.getController();
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
    private void btDeleteSBODocumentChargeClicked(ActionEvent event) {
        
        int index = tbSBODocumentCharge.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbSBODocumentCharge.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveSBODocumentChargeClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<DocumentChargeBean> tableValues = tbSBODocumentCharge.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllCompanyDocumentCharge(sId, "SILVER")) {
                    if(dbOp.saveCompanyDocumentChargeRecords(sId, tableValues, "SILVER")) {
                        PopupUtil.showInfoAlert("Document charges for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }

    @FXML
    private void btAddSBOFormulaClicked(ActionEvent event) {

        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenFormulaDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        SilverOpenFormulaDialogUIController gon = (SilverOpenFormulaDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();        
    }

    @FXML
    private void btEditSBOFormulaClicked(ActionEvent event) {

        int index = tbSBOFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverOpenFormulaDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverOpenFormulaDialogUIController gon = (SilverOpenFormulaDialogUIController) loader.getController();
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
    private void btDeleteSBOFormulaClicked(ActionEvent event) {
        
        int index = tbSBOFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbSBOFormula.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveSBOFormulaClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<FormulaBean> tableValues = tbSBOFormula.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllCompanyFormula(sId, "SILVER", "OPEN")) {
                    if(dbOp.saveCompanyFormulaRecords(sId, tableValues, "SILVER", "OPEN")) {
                        PopupUtil.showInfoAlert("Formula for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }

    @FXML
    private void btAddSBCFormulaClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverCloseFormulaDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        SilverCloseFormulaDialogUIController gon = (SilverCloseFormulaDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();                
    }

    @FXML
    private void btEditSBCFormulaClicked(ActionEvent event) {
        
        int index = tbSBCFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverCloseFormulaDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverCloseFormulaDialogUIController gon = (SilverCloseFormulaDialogUIController) loader.getController();
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
    private void btDeleteSBCFormulaClicked(ActionEvent event) {
        
        int index = tbSBCFormula.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbSBCFormula.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveSBCFormulaClicked(ActionEvent event) {
        
        String sId = txtId.getText().toUpperCase();
        ObservableList<FormulaBean> tableValues = tbSBCFormula.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllCompanyFormula(sId, "SILVER", "CLOSE")) {
                    if(dbOp.saveCompanyFormulaRecords(sId, tableValues, "SILVER", "CLOSE")) {
                        PopupUtil.showInfoAlert("Formula for gold bill closing saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }

    @FXML
    private void btAddSBMSClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverMonthSettingDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        SilverMonthSettingDialogUIController gon = (SilverMonthSettingDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();                        
    }

    @FXML
    private void btEditSBMSClicked(ActionEvent event) {
        
        int index = tbSBMS.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverMonthSettingDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverMonthSettingDialogUIController gon = (SilverMonthSettingDialogUIController) loader.getController();
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
    private void btDeleteSBMSClicked(ActionEvent event) {
        
        int index = tbSBMS.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbSBMS.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }
    }

    @FXML
    private void btSaveSBMSClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<MonthSettingBean> tableValues = tbSBMS.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllCompanyMonthSetting(sId, "SILVER")) {
                    if(dbOp.saveCompanyMonthSettingRecords(sId, tableValues, "SILVER")) {
                        PopupUtil.showInfoAlert("Document charges for gold bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }    
            
    @FXML
    private void btSaveSilverReductionMonthsOrDaysClicked(ActionEvent event) {
                
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sValue = txtSilverReductionDaysOrMonths.getText();
        String sType = cbSilverReductionDaysOrMonths.getSelectionModel().getSelectedItem();
        double dValue = "".equals(sValue) ? 0 : Double.parseDouble(sValue);
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteReductionOrMinimumDaysOrMonthsValues(sId, "SILVER", "REDUCTION")) {
                    if(dbOp.saveReductionOrMinimumDaysOrMonthsValues(sId, dValue, sType, "SILVER", "REDUCTION")) {
                        PopupUtil.showInfoAlert("Reduction months or days for silver bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                     
    }
    
    @FXML
    private void btSaveSilverMinimumMonthsOrDaysClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sValue = txtSilverMinimumDaysOrMonths.getText();
        String sType = cbSilverMinimumDaysOrMonths.getSelectionModel().getSelectedItem();
        double dValue = "".equals(sValue) ? 0 : Double.parseDouble(sValue);
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteReductionOrMinimumDaysOrMonthsValues(sId, "SILVER", "MINIMUM")) {
                    if(dbOp.saveReductionOrMinimumDaysOrMonthsValues(sId, dValue, sType, "SILVER", "MINIMUM")) {
                        PopupUtil.showInfoAlert("Minimum months or days for silver bill opening saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                     
    }
    
    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues(null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().remove(0, tbAllDetails.getItems().size());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sId = allDetailValues.getRow(i).getColumn(0).toString();
            String sName = allDetailValues.getRow(i).getColumn(1).toString();
            String sLCHolderName = allDetailValues.getRow(i).getColumn(2).toString();
            String sLCNumber = allDetailValues.getRow(i).getColumn(3).toString();
            String sArea = allDetailValues.getRow(i).getColumn(4).toString();
            String sLandlineNumber = allDetailValues.getRow(i).getColumn(5).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(6).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sId, sName, sLCHolderName, sLCNumber, sArea, sLandlineNumber, sStatus));
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
            case "L/C HOLDER NAME":
                return "LC_HOLDER_NAME";
            case "L/C NUMBER":
                return "LC_NUMBER";
            case "AREA":
                return "AREA";
            case "LANDLINE NUMBER":
                return "LANDLINE_NUMBER";
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
            if("".equals(sFilterScript))
                sFilterScript = "WHERE " + alFilterDBColumnName.get(i) + " LIKE ? ";
            else
                sFilterScript += "AND " + alFilterDBColumnName.get(i) + " LIKE ? ";
        }

        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            allDetailValues = dbOp.getAllDetailsValues(sFilterScript, alFilterValue.toArray(sValsArray));
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void btGSaveOtherSettingClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sTodaysRate = txtTodaysGoldRate.getText();
        String sGoldCompanyRate = txtGoldCompanyRate.getText();
        String sReductionWt = txtGoldReductionWt.getText();
        String sDefaultPurity = txtGDefaultPurityValue.getText();
        String sDefaultCity = txtGDefaultCity.getText();
        String sDefaultArea = txtGDefaultArea.getText();
        boolean sBODate = cbAllowToChangeGBODate.getValue().equals("YES");
        boolean sBCDate = cbAllowToChangeGBCDate.getValue().equals("YES");
        boolean sAADate = cbAllowToChangeGAADate.getValue().equals("YES");
        boolean sRBODate = cbAllowToChangeGRBODate.getValue().equals("YES");
        boolean sRBCDate = cbAllowToChangeGRBCDate.getValue().equals("YES");
        boolean sRBOName = cbAllowToChangeGRBOName.getValue().equals("YES");
        boolean sBOAmount = cbAllowToChangeGBOAmount.getValue().equals("YES");
        boolean sBCAmount = cbAllowToChangeGBCAmount.getValue().equals("YES");
        boolean sAAAmount = cbAllowToChangeGAAAmount.getValue().equals("YES");
        boolean sRBOAmount = cbAllowToChangeGRBOAmount.getValue().equals("YES");
        boolean sRBCAmount = cbAllowToChangeGRBCAmount.getValue().equals("YES");
        boolean sBOGivenAmount = cbAllowToChangeGBOGivenAmount.getValue().equals("YES");
        boolean sBCReceivedAmount = cbAllowToChangeGBCReceivedAmount.getValue().equals("YES");
        
        //print
        boolean sBOPrint = cbGBoPrintOnSaveClicked.getValue().equals("YES");
        String sBOCompPrint = cbGBCompanyCopyPrint.getValue();
        String sBOCustPrint = cbGBCustomerCopyPrint.getValue();
        String sBOPackPrint = cbGBPackingCopyPrint.getValue();
        boolean sBODirectPrint = cbGBoDirectPrint.getValue().equals("YES");
        //CAMERA
        String sCustomerCamera = cbGBCustomerCamera.getValue();
        String sJewelCamera = cbGBJewelCamera.getValue();
        String sUserCamera = cbGBUserCamera.getValue();
        String sTempFilePath = txtCamTempFilePath.getText();
        
        //verify
        boolean sBCVerify = cbVerifyGBCCopies.getValue().equals("YES");
        
        try {
            if(dbOp.deleteAllCompanyOtherSetting(sId, "GOLD")) {
                if(dbOp.saveCompanyGOtherSettingRecords(sId, sTodaysRate, sGoldCompanyRate, sReductionWt, 
                        sDefaultPurity, sDefaultCity, sDefaultArea, sBODate, sBCDate, sAADate, sRBODate, 
                        sRBCDate, sRBOName, sBOAmount, sBCAmount, sAAAmount, sRBOAmount, sRBCAmount, 
                        sBOPrint, sBOCompPrint, sBOCustPrint, sBOPackPrint, sBODirectPrint, 
                        sCustomerCamera, sJewelCamera, sUserCamera, sTempFilePath, "GOLD", 
                        sBOGivenAmount, sBCReceivedAmount, sBCVerify)) {
                    PopupUtil.showInfoAlert("Settings saved successfully to the id("+sId+").");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btSSaveOtherSettingClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sTodaysRate = txtTodaysSilverRate.getText();
        String sCompRate = txtSilverCompanyRate.getText();
        String sReductionWt = txtSilverReductionWt.getText();
        String sDefaultPurity = txtSDefaultPurityValue.getText();
        String sDefaultCity = txtSDefaultCity.getText();
        String sDefaultArea = txtSDefaultArea.getText();
        
        boolean sBODate = cbAllowToChangeSBODate.getValue().equals("YES");
        boolean sBCDate = cbAllowToChangeSBCDate.getValue().equals("YES");
        boolean sAADate = cbAllowToChangeSAADate.getValue().equals("YES");
        boolean sBOAmount = cbAllowToChangeSBOAmount.getValue().equals("YES");
        boolean sBCAmount = cbAllowToChangeSBCAmount.getValue().equals("YES");
        boolean sAAAmount = cbAllowToChangeSAAAmount.getValue().equals("YES");
        boolean sBOGivenAmount = cbAllowToChangeSBOGivenAmount.getValue().equals("YES");
        boolean sBCReceivedAmount = cbAllowToChangeSBCReceivedAmount.getValue().equals("YES");
        boolean sVerifyBC = cbVerifySBCCopies.getValue().equals("YES");
        //print
        boolean sBOPrint = cbSBoPrintOnSaveClicked.getValue().equals("YES");
        String sBOCompPrint = cbSBCompanyCopyPrint.getValue();
        String sBOCustPrint = cbSBCustomerCopyPrint.getValue();
        String sBOPackPrint = cbSBPackingCopyPrint.getValue();
        boolean sBODirectPrint = cbSBoDirectPrint.getValue().equals("YES");
        
        try {
            if(dbOp.deleteAllCompanyOtherSetting(sId, "SILVER")) {
                if(dbOp.saveCompanySOtherSettingRecords(sId, sTodaysRate, sCompRate, sReductionWt, 
                        sDefaultPurity, sDefaultCity, sDefaultArea, sBODate, sBCDate, sAADate, sBOAmount, 
                        sBCAmount, sAAAmount, sBOPrint, sBOCompPrint, sBOCustPrint, 
                        sBOPackPrint, sBODirectPrint, "SILVER", sBOGivenAmount, sBCReceivedAmount, sVerifyBC)) {
                    PopupUtil.showInfoAlert("Settings saved successfully to the id("+sId+").");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void btAccSaveTodaysSettingClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sStartingDate = CommonConstants.DATETIMEFORMATTER.format(dpAccStartingDate.getValue());
        String sPreDate = txtAccPreDate.getText();
        String sAvailableBalance = txtAccPreDayAvailableBalance.getText() != "" ? txtAccPreDayAvailableBalance.getText() : "0";
        
        try {
            if(dbOp.deleteAllTodaysAccSetting(sId)){
                if(dbOp.saveTodaysAccSetting(sId, sPreDate, sAvailableBalance, sStartingDate)){
                    PopupUtil.showInfoAlert("Settings saved successfully to the id("+sId+").");
                    CommonConstants.ACTIVE_COMPANY_ACC_STARTING_DATE = sStartingDate;
                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE = sStartingDate;
                    DataTable sAccTodaysSettingValues = dbOp.getTodaysAccountSettingsValues(sId);
                    DataTable sAccStartingSettingValues = dbOp.getStartingAccountSettingsValues(sId);
                    setAccTodaysSettingsToField(sAccTodaysSettingValues, sAccStartingSettingValues);                    
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void dpAccStartingDateTextChanged(ActionEvent event) {
        
        if(tgOff.isSelected()) {
            String sAccStartingDate = CommonConstants.DATETIMEFORMATTER.format(dpAccStartingDate.getValue());
            String sPreDay = DateRelatedCalculations.getPreviousDateWithFormatted(sAccStartingDate);    

            txtAccPreDate.setText(sPreDay);
            txtAccPreDayAvailableBalance.requestFocus();
        }
    }

    @FXML
    private void dpToBeSetLastDateTextChanged(ActionEvent event) {
        
        if(tgOff.isSelected() && sAccStartingDate != null && sAccLastDate != null) {
            txtId.setText(sLastSelectedId);
            String sId = txtId.getText().toUpperCase();
            String sToBeSetDate = CommonConstants.DATETIMEFORMATTER.format(dpToBeSetLastDate.getValue());            
            if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sAccStartingDate, sToBeSetDate) 
                    && DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sToBeSetDate, sAccLastDate)) {

                try {
                    DataTable sAccTodaysSettingValues = dbOp.getTodaysAccountSettingsValues(sId, sToBeSetDate);
                    if(sAccTodaysSettingValues.getRowCount() > 0) {
                        dpToBeSetLastDate.setValue(LocalDate.parse(sAccTodaysSettingValues.getRow(0).getColumn(4).toString(), CommonConstants.DATETIMEFORMATTER));
                        txtActualAmount.setText(sAccTodaysSettingValues.getRow(0).getColumn(5).toString());
                        txtAvailableAmount.setText(sAccTodaysSettingValues.getRow(0).getColumn(6).toString());            
                        txtDeficitAmount.setText(sAccTodaysSettingValues.getRow(0).getColumn(7).toString());            
                    }        
                } catch (SQLException ex) {
                    Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                    PopupUtil.showErrorAlert("Sorry the given date cannot be lesser than the starting date or greater than the last account closed date.");
                try {
                    dpToBeSetLastDate.setValue(LocalDate.parse(CommonConstants.ACTIVE_COMPANY_ACC_STARTING_DATE, CommonConstants.DATETIMEFORMATTER));
                    DataTable todaysAccount = dbOp.getTodaysAccountSettingsValues(sId);
                    if(todaysAccount.getRowCount() > 0) {
                        dpToBeSetLastDate.setValue(LocalDate.parse(todaysAccount.getRow(0).getColumn(4).toString(), CommonConstants.DATETIMEFORMATTER));
                        txtActualAmount.setText(todaysAccount.getRow(0).getColumn(5).toString());
                        txtAvailableAmount.setText(todaysAccount.getRow(0).getColumn(6).toString());
                        txtDeficitAmount.setText(todaysAccount.getRow(0).getColumn(7).toString());        
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    private void btSaveDateAsLastClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();        
        String sToBeSetDate = CommonConstants.DATETIMEFORMATTER.format(dpToBeSetLastDate.getValue());
            
        try {
            if(dbOp.updateAccountTableMarkToNull(sId)) {
                if(dbOp.updateAccountTableMarkToL(sId, sToBeSetDate)) {
                    dbOp.deleteAccRemaingColsedDays(sId, sToBeSetDate);
                    PopupUtil.showInfoAlert("Settings saved successfully to the id("+sId+").");
                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE = sToBeSetDate;
                    DataTable sAccTodaysSettingValues = dbOp.getTodaysAccountSettingsValues(sId);
                    DataTable sAccStartingSettingValues = dbOp.getStartingAccountSettingsValues(sId);
                    setAccTodaysSettingsToField(sAccTodaysSettingValues, sAccStartingSettingValues);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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

    @FXML
    private void btAddOCFineChargeClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FineChargeDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        FineChargeDialogUIController gon = (FineChargeDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
        
    }

    @FXML
    private void btEditOCFineChargeClicked(ActionEvent event) {
        
        int index = tbOCFineCharges.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("FineChargeDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            FineChargeDialogUIController gon = (FineChargeDialogUIController) loader.getController();
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
    private void btDeleteOCFineChargeClicked(ActionEvent event) {
        
        int index = tbOCFineCharges.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            tbOCFineCharges.getItems().remove(index);
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }        
    }

    @FXML
    private void btSaveOCFineChargeClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<FineChargeBean> tableValues = tbOCFineCharges.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllCompanyFineCharge(sId)) {
                    if(dbOp.saveCompanyFineChargeRecords(sId, tableValues)) {
                        PopupUtil.showInfoAlert("Fine charges saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }             
    }

    @FXML
    private void btSaveOCNoticeClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sNoticeDate = CommonConstants.DATETIMEFORMATTER.format(dpNoticeTillDate.getValue());
        double dNoticeAmount = Double.parseDouble(txtNoticeChargeAmount.getText());

        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.updateNoticeCharges(sId, sNoticeDate, dNoticeAmount)) {
                    PopupUtil.showInfoAlert("Notice charges saved successfully to the id("+sId+").");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                             
    }

    @FXML
    private void btSaveFIlePathClicked(ActionEvent event) {
        
        String sId = txtId.getText().toUpperCase();
        String sBackupFilePath = txtFilePath.getText();        
        try {
            if(dbOp.updateFilePath(sBackupFilePath, sId)) {
                PopupUtil.showInfoAlert("Backup File Path is save successfully.");
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void browseOnMouseClicked(ActionEvent event) {
        dialog = new Stage();
        DirectoryChooser chooser = new DirectoryChooser();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setX(CommonConstants.SCREEN_X);
        stage.setY(CommonConstants.SCREEN_Y);        
        File file = chooser.showDialog(stage);
        if(file != null) {
            txtFilePath.setText(file.getAbsolutePath());        
        }
    }

    @FXML
    private void btGSavePrintSettingClicked(ActionEvent event) {
        
        btGSaveOtherSettingClicked(null);
    }

    @FXML
    private void btSSavePrintSettingClicked(ActionEvent event) {
        btSSaveOtherSettingClicked(null);
    }

    @FXML
    private void btSaveCardLostChargeClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        double dCardLostAmount = Double.parseDouble(
                txtCardLostFineCharge.getText().isEmpty() 
                        ? "0" 
                        : txtCardLostFineCharge.getText());

        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.updateCardLostCharges(sId, dCardLostAmount)) {
                    PopupUtil.showInfoAlert("Card Lost charges saved successfully to the id("+sId+").");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                                     
    }

    @FXML
    private void btGSaveCameraSettingClicked(ActionEvent event) {
        btGSaveOtherSettingClicked(null);
    }

    @FXML
    private void browseCamTempFIleOnMouseClicked(ActionEvent event) {
        dialog = new Stage();
        DirectoryChooser chooser = new DirectoryChooser();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setX(CommonConstants.SCREEN_X);
        stage.setY(CommonConstants.SCREEN_Y);        
        File file = chooser.showDialog(stage);
        if(file != null) {
            txtCamTempFilePath.setText(file.getAbsolutePath());        
        }        
    }

    @FXML
    private void btSaveShareCustomerListFromClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        String sCompIds = txtShareCustomerListFrom.getText();

        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.updateCompIdForSharingCustomers(sId, sCompIds)) {
                    PopupUtil.showInfoAlert("Companies to share customer ids saved successfully to the id("+sId+").");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                                             
    }

    @FXML
    private void showAllCompaniesListOnMouseClicked(ActionEvent event) {
        
        try {
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AllCompaniesListDialog.fxml"));
            Parent root = null;
            try {
                root = (Parent) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            AllCompaniesListDialogUIController gon = (AllCompaniesListDialogUIController) loader.getController();
            gon.setParent(this);
            gon.setInitValus(dbOp.getCompaniesList(cbcompanyType.getValue()));
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(400);
            dialog.setY(200);
            dialog.setTitle("All Companies List");
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void doEMIGBOInterestSaveModeWork() {
    
        btAddGBOInterest1.setDisable(true);
        btEditGBOInterest1.setDisable(true);
        btDeleteGBOInterest1.setDisable(true);
        btSaveGBOInterest1.setDisable(true);
        
        tbGBOInterest.getItems().remove(0, tbGBOInterest.getItems().size());
    }

    public void doEMIGBODocumentChargeSaveModeWork() {
    
        btAddGBODocumentCharge1.setDisable(true);
        btEditGBODocumentCharge1.setDisable(true);
        btDeleteGBODocumentCharge1.setDisable(true);
        btSaveGBODocumentCharge1.setDisable(true);
        
        tbGBODocumentCharge1.getItems().remove(0, tbGBODocumentCharge1.getItems().size());
    }

    public void doEMIGBOFormulaSaveModeWork() {
    
        btAddGBOFormula1.setDisable(true);
        btEditGBOFormula1.setDisable(true);
        btDeleteGBOFormula1.setDisable(true);
        btSaveGBOFormula1.setDisable(true);
        
        tbGBOFormula1.getItems().remove(0, tbGBOFormula1.getItems().size());
    }

    public void doEMIGBCFormulaSaveModeWork() {
    
        btAddGBCFormula1.setDisable(true);
        btEditGBCFormula1.setDisable(true);
        btDeleteGBCFormula1.setDisable(true);
        btSaveGBCFormula1.setDisable(true);
        
        tbGBCFormula1.getItems().remove(0, tbGBCFormula1.getItems().size());
    }

    public void doEMIGBONumberSaveModeWork() {
    
        btEditGBONumber1.setDisable(true);
        btSaveGBONumber1.setDisable(true);
        
        tbGBONumberGenerator1.getItems().remove(0, tbGBONumberGenerator1.getItems().size());
        txtGBONGPreRow1.setText("");
        txtGBONGPrePrefix1.setText("");
        txtGBONGPreNumber1.setText("");
        txtGBONGCurRow1.setText("");
        txtGBONGCurPrefix1.setText("");
        txtGBONGCurNumber1.setText("");
    }

    public void doEMIGOtherSettingsSaveModeWork() {
    
        txtTodaysGoldRate1.setText("");
        txtGoldCompanyRate1.setText("");
        txtGoldReductionWt1.setText("");
        txtGDefaultPurityValue1.setText("");
        txtGDefaultCity1.setText("");
        txtGDefaultArea1.setText("");
        cbAllowToChangeGAADate1.setValue("YES");
        cbAllowToChangeGBCDate1.setValue("YES");
        cbAllowToChangeGBODate1.setValue("YES");        
        cbAllowToChangeGRBODate1.setValue("YES");
        cbAllowToChangeGRBCDate1.setValue("YES");

        cbAllowToChangeGAAAmount1.setValue("YES");
        cbAllowToChangeGBCAmount1.setValue("YES");
        cbAllowToChangeGBOAmount1.setValue("YES");        
        cbAllowToChangeGRBOAmount1.setValue("YES");
        cbAllowToChangeGRBCAmount1.setValue("YES");
        
        cbAllowToChangeGRBOName1.setValue("YES");
        btGSaveOtherSetting1.setDisable(true);
    }

    public void doEMIGPrintSettingsSaveModeWork() {
    
        cbGBoPrintOnSaveClicked1.setValue("YES"); 
        cbGBCompanyCopyPrint1.setValue(DO_NOT_PRINT);        
        cbGBCustomerCopyPrint1.setValue(DO_NOT_PRINT);
        cbGBPackingCopyPrint1.setValue(DO_NOT_PRINT);
        cbGBoDirectPrint1.setValue("YES"); 
        btGSavePrintSetting1.setDisable(true);
        
        cbGBCustomerCamera1.setValue(DO_NOT_TAKE_PICTURE);
        cbGBJewelCamera1.setValue(DO_NOT_TAKE_PICTURE);
        cbGBUserCamera1.setValue(DO_NOT_TAKE_PICTURE);
        btGSaveCameraSetting1.setDisable(true);
    }

    public void doEMIGBOInterestUpdateModeWork() {
    
        btAddGBOInterest1.setDisable(false);
        btEditGBOInterest1.setDisable(false);
        btDeleteGBOInterest1.setDisable(false);
        btSaveGBOInterest1.setDisable(false);
    }

    public void doEMIGBODocumentChargeUpdateModeWork() {
    
        btAddGBODocumentCharge1.setDisable(false);
        btEditGBODocumentCharge1.setDisable(false);
        btDeleteGBODocumentCharge1.setDisable(false);
        btSaveGBODocumentCharge1.setDisable(false);
    }

    public void doEMIGBOFormulaUpdateModeWork() {
    
        btAddGBOFormula1.setDisable(false);
        btEditGBOFormula1.setDisable(false);
        btDeleteGBOFormula1.setDisable(false);
        btSaveGBOFormula1.setDisable(false);
    }

    public void doEMIGBCFormulaUpdateModeWork() {
    
        btAddGBCFormula1.setDisable(false);
        btEditGBCFormula1.setDisable(false);
        btDeleteGBCFormula1.setDisable(false);
        btSaveGBCFormula1.setDisable(false);
    }

    public void doEMIGBONumberUpdateModeWork() {
    
        btEditGBONumber1.setDisable(false);
        btSaveGBONumber1.setDisable(false);
    }

    public void doEMIGOtherSettingsUpdateModeWork() {
    
        btGSaveOtherSetting1.setDisable(false);
    }

    public void doEMIGPrintSettingsUpdateModeWork() {
    
        btGSavePrintSetting1.setDisable(false);

        cbGBCompanyCopyPrint1.getItems().removeAll(cbGBCompanyCopyPrint1.getItems());
        cbGBCustomerCopyPrint1.getItems().removeAll(cbGBCustomerCopyPrint1.getItems());
        cbGBPackingCopyPrint1.getItems().removeAll(cbGBPackingCopyPrint1.getItems());
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for(PrintService service : services) {
            cbGBCompanyCopyPrint1.getItems().add(service.getName());
            cbGBCustomerCopyPrint1.getItems().add(service.getName());
            cbGBPackingCopyPrint1.getItems().add(service.getName());
        }
        cbGBCompanyCopyPrint1.getItems().add(DO_NOT_PRINT);
        cbGBCustomerCopyPrint1.getItems().add(DO_NOT_PRINT);
        cbGBPackingCopyPrint1.getItems().add(DO_NOT_PRINT);
        
        cbGBCustomerCamera1.getItems().removeAll(cbGBCustomerCamera1.getItems());
        cbGBJewelCamera1.getItems().removeAll(cbGBJewelCamera1.getItems());
        cbGBUserCamera1.getItems().removeAll(cbGBUserCamera1.getItems());
        try {
            for(Webcam webcam : WebCamWork.getWebCamLists()) {
                cbGBCustomerCamera1.getItems().add(webcam.getName());
                cbGBJewelCamera1.getItems().add(webcam.getName());
                cbGBUserCamera1.getItems().add(webcam.getName());
            }
        } catch (Exception e) {
            // No webcam available or driver error — skip
        }
        cbGBCustomerCamera1.getItems().add(DO_NOT_TAKE_PICTURE);
        cbGBJewelCamera1.getItems().add(DO_NOT_TAKE_PICTURE);
        cbGBUserCamera1.getItems().add(DO_NOT_TAKE_PICTURE);
        btGSaveCameraSetting1.setDisable(false);
    }

    @FXML
    private void btEMIEditGBONumberClicked(ActionEvent event) {
        
        int index = tbGBONumberGenerator1.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldOpenNumberGeneratorDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldOpenNumberGeneratorDialogUIController gon = (GoldOpenNumberGeneratorDialogUIController) loader.getController();
            gon.setParent(this, false, false);
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
    private void btEMISaveGBONumberClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        int iGoldCurBillRowNumber = Integer.parseInt(txtGBONGCurRow1.getText());
        String sGoldCurBillPrefix = txtGBONGCurPrefix1.getText();
        String sGoldCurBillNumber = txtGBONGCurNumber1.getText();        
        ObservableList<NumberGeneratorBean> tableValues = tbGBONumberGenerator1.getItems();
        
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAllNumberGenerator(sId, "GOLD", CommonConstants.EMI)) {
                    if(dbOp.saveNumberGeneratorRecords(sId, tableValues, "GOLD", CommonConstants.EMI)) {
                        if(dbOp.updateEMIGoldCurBillNumber(sId, iGoldCurBillRowNumber, sGoldCurBillPrefix, sGoldCurBillNumber)) {
                            PopupUtil.showInfoAlert("Number generation format for gold bill opening saved successfully to the id("+sId+").");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void txtEMIGBONGCurRowActionPerformed(ActionEvent event) {
        
        int iCurRow = Integer.parseInt(txtGBONGCurRow1.getText()) - 1;
        if(iCurRow < 5) {
            NumberGeneratorBean bean = tbGBONumberGenerator1.getItems().get(iCurRow);
            txtGBONGCurPrefix1.setText(bean.getSPrefix());
            txtGBONGCurNumber1.setText(Long.toString(bean.getLFrom()));
        }        
    }

    @FXML
    private void btAccSaveOtherSettingClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);      
        String sId = txtId.getText().toUpperCase();
        boolean sCompExpDate = cbAllowToChangeCompExpDate.getValue().equals("YES");
        boolean sEmpExpDate = cbAllowToChangeEmpExpDate.getValue().equals("YES");
        boolean sRepExpDate = cbAllowToChangeRepExpDate.getValue().equals("YES");
        boolean sCompIncDate = cbAllowToChangeCompIncDate.getValue().equals("YES");
        boolean sEmpIncDate = cbAllowToChangeEmpIncDate.getValue().equals("YES");
        boolean sRepIncDate = cbAllowToChangeRepInc.getValue().equals("YES");
        
        try {
            if(dbOp.updateAccountOtherSettings(sId, sCompExpDate, sEmpExpDate, sRepExpDate, sCompIncDate, sEmpIncDate, sRepIncDate)) {
                PopupUtil.showInfoAlert("Settings saved successfully to the id("+sId+").");
            }
        } catch (Exception ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }
    
    public void setAccOtherSettingsToField(DataTable otherSettingsValues) {
        
        if(otherSettingsValues.getRowCount() > 0) {
            cbAllowToChangeEmpExpDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(0).toString()) ? "YES" : "NO");
            cbAllowToChangeCompExpDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(1).toString()) ? "YES" : "NO");
            cbAllowToChangeRepExpDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(2).toString()) ? "YES" : "NO");
            cbAllowToChangeEmpIncDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(3).toString()) ? "YES" : "NO");
            cbAllowToChangeCompIncDate.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(4).toString()) ? "YES" : "NO");
            cbAllowToChangeRepInc.setValue(Boolean.valueOf(otherSettingsValues.getRow(0).getColumn(5).toString()) ? "YES" : "NO");
        } else {
            cbAllowToChangeEmpExpDate.setValue("YES");
            cbAllowToChangeCompExpDate.setValue("YES");
            cbAllowToChangeRepExpDate.setValue("YES");            
            cbAllowToChangeEmpIncDate.setValue("YES");
            cbAllowToChangeCompIncDate.setValue("YES");
            cbAllowToChangeRepInc.setValue("YES"); 
        }

    }
    
}
