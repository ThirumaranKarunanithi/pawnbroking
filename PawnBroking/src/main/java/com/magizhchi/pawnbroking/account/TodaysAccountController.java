/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import com.magizhchi.pawnbroking.backup.DataBaseBackup;
import com.magizhchi.pawnbroking.billcalculator.BillCalculatorController;
import com.magizhchi.pawnbroking.billcalculator.RepPrintBean;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class TodaysAccountController implements Initializable {

    public final String FILE_NAME = "FILE_NAME";
    public final String PARAMAETERS = "PARAMAETERS";
    public boolean GNY_LOCKED_SELECTED = false;
    public TodaysAccountDBOperation dbOp;
    public Stage dialog;
    private double dTotalPf = 0;
    private double dGPf = 0;
    private double dSPf = 0;
    
    private File tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
    private File compFolder = new File(tempFile, CommonConstants.ACTIVE_COMPANY_ID);    
    
        
    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private HBox nodeAddToFilter1;
    @FXML
    public TableView<TodaysAccountBean> tbTodaysAccount;
    @FXML
    private HBox nodeAddToFilter2;
    @FXML
    private HBox nodeAddToFilter11;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btCloseAccount;
    @FXML
    private AnchorPane apPre;
    @FXML
    private AnchorPane apToday;
    @FXML
    private SplitPane divider;
    @FXML
    private TextField txtPreDate;
    @FXML
    private TextField txtPreActualBalance;
    @FXML
    private TextField txtPreAvailableBalance;
    @FXML
    private TextField txtPreDeficit;
    @FXML
    public DatePicker dpTodaysDate;
    @FXML
    public TextField txtActualBalance;
    @FXML
    public TextField txtAvailableBalance;
    @FXML
    public TextField txtDeficit;
    @FXML
    private TextField txtTotalDebit;
    @FXML
    private TextField txtTotalCredit;
    @FXML
    private TableView<TodaysJewelAccountBean> tbGNYLocked;
    @FXML
    private TextField txtGNYLCount;
    @FXML
    private TextField txtGNYLAmount;
    @FXML
    private TableView<TodaysClosedJewelAccountBean> tbGNYDelivered;
    @FXML
    private TextField txtGNYDCount;
    @FXML
    private TextField txtGNYDAmount;
    @FXML
    private TableView<TodaysJewelAccountBean> tbSNYLocked;
    @FXML
    private TextField txtSNYLCount;
    @FXML
    private TextField txtSNYLAmount;
    @FXML
    private TableView<TodaysClosedJewelAccountBean> tbSNYDelivered;
    @FXML
    private TextField txtSNYDCount;
    @FXML
    private TextField txtSNYDAmount;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRNYDelivered;
    @FXML
    private TextField txtRNYDCount;
    @FXML
    private TextField txtRNYDAmount;
    @FXML
    private TextField txtRNYDCompAmount;

    final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    final String billCalculatorScreen = "/com/magizhchi/pawnbroking/billcalculator/BillCalculator.fxml";
    
    @FXML
    private HBox hBoxPf;
    @FXML
    private TextField txtTotalPf;
    @FXML
    private HBox hBoxTotalFields;
    @FXML
    private TextField txtGoldPf;
    @FXML
    private TextField txtSilverPf;
    @FXML
    private TextField txtGNYDInterest;
    @FXML
    private TextField txtGNYDInteresteAmt;
    @FXML
    private TextField txtSNYDInterest;
    @FXML
    private TextField txtSNYDInteresteAmt;
    @FXML
    private Button btAvailableBalance;
    @FXML
    private Button btPrint;
    @FXML
    private Button btShowInBillCalc;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRNYDeliveredLater;
    @FXML
    private TextArea txtPreNote;
    @FXML
    private TextArea txtTodaysNote;
    @FXML
    private Button btRebilledShowInBillCalc;
    @FXML
    private Button btSuspenseShowInBillCalc1;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRNYSuspense;
    @FXML
    private TextField txtGoldBillNumberCheck;
    @FXML
    private TextField txtSilverBillNumberCheck;
    @FXML
    private Button btGNYLLock;
    @FXML
    private Button btSNYLLock;
    @FXML
    private Button btGNYD;
    @FXML
    private Button btSNYD;
    @FXML
    private TableView<TodaysJewelAccountBean> tbGNYRepToLocked;
    @FXML
    private TextField txtGNYLRepToCount;
    @FXML
    private TextField txtGNYLRepToAmount;
    @FXML
    private TextField txtGoldBillNumberRepToCheck;
    @FXML
    private Button btGNYLRepToLock;
    @FXML
    private TableView<TodaysJewelAccountBean> tbGNYLockerToRep;
    @FXML
    private TextField txtGNYLockerToRepCount;
    @FXML
    private TextField txtGNYLLockerToRepAmount;
    @FXML
    private TextField txtGoldBillNumberLockerToRepCheck;
    @FXML
    private TableView<TodaysClosedJewelAccountBean> tbGNYCashDrawerToCustomer;
    @FXML
    private TextField txtGNYDCashDrawerToCustomerCount;
    @FXML
    private TextField txtGNYDCashDrawerToCustomerAmount;
    @FXML
    private TextField txtGNYDCashDrawerToCustomerInterest;
    @FXML
    private TextField txtGNYDCashDrawerToCustomerInteresteAmt;
    @FXML
    private Button btGNYDCashDrawerToCustomer;
    @FXML
    private TableView<TodaysClosedJewelAccountBean> tbSNYCashDrawerToCustomer;
    @FXML
    private TextField txtSNYDCashDrawerToCustomerCount;
    @FXML
    private TextField txtSNYDCashDrawerToCustomerAmount;
    @FXML
    private TextField txtSNYDCashDrawerToCustomerInterest;
    @FXML
    private TextField txtSNYDCashDrawerToCustomerInteresteAmt;
    @FXML
    private Button btSNYDCashDrawerToCustomer;
    @FXML
    private TableView<TodaysClosedJewelAccountBean> tbGNYRepDrawerToCustomer;
    @FXML
    private TextField txtGNYLRepDrawerToCustomerCount;
    @FXML
    private TextField txtGNYLRepDrawerToCustomerAmount;
    @FXML
    private TextField txtGoldBillNumberRepDrawerToCustomerCheck;
    @FXML
    private Button btGNYLRepDrawerToCustomer;
    @FXML
    private TextField txtGNYLRepDrawerToCustomerInterest;
    @FXML
    private TextField txtGoldBillNumberShopLockerToCashDrawerCheck;
    @FXML
    private TextField txtSiilverBillNumberShopLockerToCashDrawerCheck;
    @FXML
    private Button btGNYShopLockerToRepDrawer;
    @FXML
    private TableView<TodaysJewelAccountBean> tbGNYRepDrawerToRepLocker;
    @FXML
    private TextField txtGNYRepDrawerToRepLockerCount;
    @FXML
    private TextField txtGNYRepDrawerToRepLockerAmount;
    @FXML
    private TextField txtGoldBillNumberRepDrawerToRepLockerCheck;
    @FXML
    private Button btGNYRepDrawerToRepLocker;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
                
        try {
            dbOp = new TodaysAccountDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        setInitValues();                                    
    }    
    
    private void setInitValues() {

        txtPreDeficit.setStyle("-fx-font-weight: bold");
        txtDeficit.setStyle("-fx-font-weight: bold");

        setPreValues();
        setTodaysValue();   
        setAllJewelAccountTableValues();
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.TODAYS_ACCOUNT_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btCloseAccount.setDisable(false);
                btAvailableBalance.setDisable(false);
            } else {
                btCloseAccount.setDisable(true);
                btAvailableBalance.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.TODAYS_ACCOUNT_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                tgOff.setDisable(false);
            } else {
                tgOff.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        

        try {
            if(CommonConstants.ROLEID.equals(CommonConstants.TIRU) || 
                    dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, 
                            CommonConstants.PF_TC_FD)) {
                if(!hBoxTotalFields.getChildren().contains(hBoxPf)) {
                    hBoxTotalFields.getChildren().add(hBoxPf);
                }
            } else {
                if(hBoxTotalFields.getChildren().contains(hBoxPf)) {
                    hBoxTotalFields.getChildren().remove(hBoxPf);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        

        try {
            if(CommonConstants.ROLEID.equals(CommonConstants.TIRU) || 
                    dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, 
                            CommonConstants.TA_S_BO_LOCKER)) {
                btSNYLLock.setDisable(false);
            } else {
                btSNYLLock.setDisable(true);
            }
            if(CommonConstants.ROLEID.equals(CommonConstants.TIRU) || 
                    dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, 
                            CommonConstants.TA_G_BO_LOCKER)) {
                btGNYLLock.setDisable(false);
                btGNYLRepToLock.setDisable(false);
            } else {
                btGNYLLock.setDisable(true);
                btGNYLRepToLock.setDisable(true);
            }
            if(CommonConstants.ROLEID.equals(CommonConstants.TIRU) || 
                    dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, 
                            CommonConstants.TA_S_BC_DELIVERED)) {
                //btSNYD.setDisable(false);
                btSNYDCashDrawerToCustomer.setDisable(false);
            } else {
                //btSNYD.setDisable(true);
                btSNYDCashDrawerToCustomer.setDisable(true);                
            }
            if(CommonConstants.ROLEID.equals(CommonConstants.TIRU) || 
                    dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, 
                            CommonConstants.TA_G_BC_DELIVERED)) {
                //btGNYD.setDisable(false);                
                //btGNYShopLockerToRepDrawer.setDisable(false);
                btGNYDCashDrawerToCustomer.setDisable(false);                
                btGNYLRepDrawerToCustomer.setDisable(false);
                btGNYRepDrawerToRepLocker.setDisable(false);
            } else {
                //btGNYD.setDisable(true);
                //btGNYShopLockerToRepDrawer.setDisable(true);
                btGNYDCashDrawerToCustomer.setDisable(true);
                btGNYLRepDrawerToCustomer.setDisable(true);
                btGNYRepDrawerToRepLocker.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }
           
    private void setTodaysValue() {
        
        try {
            HashMap<String, String> lastValues = dbOp.getTodaysAccountSettingsValues(CommonConstants.ACTIVE_COMPANY_ID);
            if(lastValues != null) 
            {
                boolean allow;
                if(tgOff.isSelected()) {
                    allow = DateRelatedCalculations
                            .isFirstDateIsLesserOrEqualToSecondDate(
                                    CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()), 
                                    lastValues.get("TODAYS_DATE"));
                } else {
                    allow = true;
                }
                if(allow)
                {
                    HashMap<String, String> gBOValues = dbOp.getBillOpeningAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> gBAAValues = dbOp.getBillAdvanceAmountAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> gBCValues = dbOp.getBillClosingAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> sBOValues = dbOp.getBillOpeningAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "SILVER", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> sBAAValues = dbOp.getBillAdvanceAmountAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "SILVER", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> sBCValues = dbOp.getBillClosingAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "SILVER", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> rBOValues = dbOp.getReBillOpeningAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> rBCValues = dbOp.getReBillClosingAccountValues(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> expenseValues = dbOp.getAllExpensesAccountValues(CommonConstants.ACTIVE_COMPANY_ID, CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    HashMap<String, String> incomeValues = dbOp.getAllIncomeAccountValues(CommonConstants.ACTIVE_COMPANY_ID, CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                    
                    tbTodaysAccount.getItems().removeAll(tbTodaysAccount.getItems());

                    if(gBOValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("GOLD BILL OPENING", 
                                Double.parseDouble(gBOValues.get("bill_count")), 
                                Double.parseDouble(gBOValues.get("debit")), 
                                Double.parseDouble(gBOValues.get("credit")),
                                gBOValues.get("credit_combo")));
                    }
                    if(gBAAValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("GOLD BILL ADVANCE AMOUNT", 
                                Double.parseDouble(gBAAValues.get("bill_count")), 
                                Double.parseDouble(gBAAValues.get("debit")), 
                                Double.parseDouble(gBAAValues.get("credit")),
                                gBAAValues.get("credit_combo")));
                    }                    
                    if(gBCValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("GOLD BILL CLOSING", 
                                Double.parseDouble(gBCValues.get("bill_count")), 
                                Double.parseDouble(gBCValues.get("debit")), 
                                Double.parseDouble(gBCValues.get("credit")),
                                gBCValues.get("credit_combo")));
                    }
                    if(sBOValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("SILVER BILL OPENING", 
                                Double.parseDouble(sBOValues.get("bill_count")), 
                                Double.parseDouble(sBOValues.get("debit")), 
                                Double.parseDouble(sBOValues.get("credit")),
                                sBOValues.get("credit_combo")));
                    }
                    if(sBAAValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("SILVER BILL ADVANCE AMOUNT", 
                                Double.parseDouble(sBAAValues.get("bill_count")), 
                                Double.parseDouble(sBAAValues.get("debit")), 
                                Double.parseDouble(sBAAValues.get("credit")),
                                sBAAValues.get("credit_combo")));
                    }                    
                    if(sBCValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("SILVER BILL CLOSING", 
                                Double.parseDouble(sBCValues.get("bill_count")), 
                                Double.parseDouble(sBCValues.get("debit")), 
                                Double.parseDouble(sBCValues.get("credit")),
                                sBCValues.get("credit_combo")));
                    }
                    if(rBOValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("REPLEDGE BILL OPENING", 
                                Double.parseDouble(rBOValues.get("bill_count")), 
                                Double.parseDouble(rBOValues.get("debit")), 
                                Double.parseDouble(rBOValues.get("credit")),
                                rBOValues.get("credit_combo")));
                    }
                    if(rBCValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("REPLEDGE BILL CLOSING", 
                                Double.parseDouble(rBCValues.get("bill_count")), 
                                Double.parseDouble(rBCValues.get("debit")), 
                                Double.parseDouble(rBCValues.get("credit")),
                                rBCValues.get("credit_combo")));
                    }
                    if(expenseValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("EXPENSES", 
                                Double.parseDouble(expenseValues.get("expense_count")), 
                                Double.parseDouble(expenseValues.get("debit")), 
                                Double.parseDouble(expenseValues.get("credit")),
                                expenseValues.get("credit_combo")));
                    }
                    if(incomeValues != null) {
                        tbTodaysAccount.getItems().add(new TodaysAccountBean("INCOMES", 
                                Double.parseDouble(incomeValues.get("income_count")), 
                                Double.parseDouble(incomeValues.get("debit")), 
                                Double.parseDouble(incomeValues.get("credit")),
                                incomeValues.get("credit_combo")));
                    }
                    
                    double dActualBalance = Double.parseDouble(txtPreActualBalance.getText());
                    double dTotalDebit = 0;
                    double dTotalCredit = 0;
                    double dPf = 0;
                    double dGPf = 0;
                    double dSPf = 0;                    
                    for(TodaysAccountBean bean : tbTodaysAccount.getItems()) {
                        dTotalDebit += bean.getDDebit();
                        dTotalCredit += bean.getDCredit();
                    }
                    
                    dPf += Double.parseDouble(gBOValues.get("credit")) - Double.parseDouble(rBOValues.get("debit"));
                    dPf += Double.parseDouble(sBOValues.get("credit"));
                    dPf += Double.parseDouble(gBCValues.get("interested_amt")) - 
                            Double.parseDouble(rBCValues.get("interested_amt"));
                    dPf += Double.parseDouble(sBCValues.get("interested_amt"));
                    
                    dGPf += Double.parseDouble(gBOValues.get("credit")) - Double.parseDouble(rBOValues.get("debit"));
                    dGPf += (Double.parseDouble(gBCValues.get("interested_amt")) 
                                + Double.parseDouble(gBCValues.get("total_other_charges")) 
                                - Double.parseDouble(gBCValues.get("discount_amount"))) 
                            - Double.parseDouble(rBCValues.get("interested_amt"));
                    
                    dSPf += Double.parseDouble(sBOValues.get("credit"));
                    dSPf += Double.parseDouble(sBCValues.get("interested_amt"));

                    if(hBoxTotalFields.getChildren().contains(hBoxPf)) {
                        txtTotalPf.setText(String.format("%.0f", dPf));
                        txtGoldPf.setText(String.format("%.0f", dGPf));
                        txtSilverPf.setText(String.format("%.0f", dSPf));
                    }
                    
                    this.dTotalPf = dPf;
                    this.dGPf = dGPf;
                    this.dSPf = dSPf;
                    
                    dActualBalance += dTotalCredit - dTotalDebit;
                    
                    txtTotalDebit.setText(Double.toString(dTotalDebit));
                    txtTotalCredit.setText(Double.toString(dTotalCredit));
                    
                    txtActualBalance.setText(Double.toString(dActualBalance));
                    if(tgOn.isSelected()) {
                        String sAvail = dbOp.getAvailableAmount(CommonConstants.ACTIVE_COMPANY_ID, CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                        if(sAvail != null) {
                            txtAvailableBalance.setText(sAvail);
                            txtAvailableBalance.setEditable(false);
                            txtAvailableBalance.setMouseTransparent(true);
                            txtAvailableBalance.setFocusTraversable(false);       
                        } else {
                            txtAvailableBalance.setText("0");
                            txtAvailableBalance.setEditable(true);
                            txtAvailableBalance.setMouseTransparent(false);
                            txtAvailableBalance.setFocusTraversable(true);       
                        }
                    }
                    double dAvailableBalance = Double.parseDouble(txtAvailableBalance.getText());
                    txtDeficit.setText(Double.toString(dAvailableBalance - dActualBalance));     
                    if(Double.parseDouble(txtDeficit.getText()) == 0) {
                        txtDeficit.setStyle("-fx-background-color: #55FF30");
                    } else {
                        txtDeficit.setStyle("-fx-background-color: #FF5555");
                    }
                    
                } else {
                    PopupUtil.showErrorAlert("Sorry the selected date account was not yet closed.");
                    setPreValues();
                    setTodaysValue();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setPreValues() {

        try {
            HashMap<String, String> preValues = dbOp.getTodaysAccountSettingsValues(CommonConstants.ACTIVE_COMPANY_ID);
            if(preValues != null) 
            {
                if(tgOn.isSelected())
                {
                    txtPreDate.setText(preValues.get("TODAYS_DATE"));
                    txtPreActualBalance.setText(preValues.get("TODAYS_ACTUAL_AMOUNT"));
                    txtPreAvailableBalance.setText(preValues.get("TODAYS_AVAILABLE_AMOUNT"));
                    txtPreDeficit.setText(preValues.get("TODAYS_DEFICIT_AMOUNT"));
                    txtPreNote.setText(preValues.get("TODAYS_NOTE"));
                    String sNextDate = DateRelatedCalculations.getNextDateWithFormatted(preValues.get("TODAYS_DATE"));
                    dpTodaysDate.setValue(LocalDate.parse(sNextDate, CommonConstants.DATETIMEFORMATTER));
                } else 
                {
                    txtPreDate.setText(preValues.get("PRE_DATE"));
                    txtPreActualBalance.setText(preValues.get("PRE_ACTUAL_AMOUNT"));
                    txtPreAvailableBalance.setText(preValues.get("PRE_AVAILABLE_AMOUNT"));
                    txtPreDeficit.setText(preValues.get("PRE_DEFICIT_AMOUNT"));  
                    txtPreNote.setText(preValues.get("PRE_NOTE"));
                    dpTodaysDate.setValue(LocalDate.parse(preValues.get("TODAYS_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtAvailableBalance.setText(preValues.get("TODAYS_AVAILABLE_AMOUNT"));
                }
                if(Double.parseDouble(txtPreDeficit.getText()) == 0) {
                    txtPreDeficit.setStyle("-fx-background-color: #55FF30");
                } else {
                    txtPreDeficit.setStyle("-fx-background-color: #FF5555");
                }
            } else {
                PopupUtil.showInfoAlert("Please set the starting date of the account for company id "+ CommonConstants.ACTIVE_COMPANY_ID +".");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbTodaysAccount.getSelectionModel().getSelectedIndex();
        
        if (event.getClickCount() == 2 && (index >= 0)) {
            
            double dCount = tbTodaysAccount.getItems().get(index).getDCount();            
            if(dCount > 0) 
            {
                switch (index) {
                    case 0:
                        try {
                            DataTable values = dbOp.getBillOpeningTableValue(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showBillOpening(values, event, "GOLD BILL OPENING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 1:
                        try {
                            DataTable values = dbOp.getBillAdvanceAmountTableValue(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showBillAdvanceAmount(values, event, "GOLD BILL ADVANCE AMOUNT", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 2:
                        try {
                            DataTable values = dbOp.getBillClosingTableValue(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showBillClosing(values, event, "GOLD BILL CLOSING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 3:
                        try {
                            DataTable values = dbOp.getBillOpeningTableValue(CommonConstants.ACTIVE_COMPANY_ID, "SILVER", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showBillOpening(values, event, "SILVER BILL OPENING", false);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 4:
                        try {
                            DataTable values = dbOp.getBillAdvanceAmountTableValue(CommonConstants.ACTIVE_COMPANY_ID, "SILVER", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showBillAdvanceAmount(values, event, "SILVER BILL ADVANCE AMOUNT", false);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 5:
                        try {
                            DataTable values = dbOp.getBillClosingTableValue(CommonConstants.ACTIVE_COMPANY_ID, "SILVER", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showBillClosing(values, event, "SILVER BILL CLOSING", false);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 6:
                        try {
                            DataTable values = dbOp.getReBillOpeningTableValue(CommonConstants.ACTIVE_COMPANY_ID, "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showReBillOpening(values, event, "REPLEDGE BILL OPENING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 7:
                        try {
                            DataTable values = dbOp.getReBillClosingTableValue(CommonConstants.ACTIVE_COMPANY_ID, 
                                    "GOLD", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showReBillClosing(values, event, "REPLEDGE BILL CLOSING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 8:
                        try {
                            DataTable values = dbOp.getExpenseTableValue(CommonConstants.ACTIVE_COMPANY_ID, CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showExpense(values, event, "EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    case 9:
                        try {
                            DataTable values = dbOp.getIncomeTableValue(CommonConstants.ACTIVE_COMPANY_ID, CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                            showIncome(values, event, "INCOME");
                        } catch (SQLException ex) {
                            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                        }   break;
                    default:
                        break;
                }
            } else {
                String sOperation = tbTodaysAccount.getItems().get(index).getSOperation();
                PopupUtil.showInfoAlert(event, "No any "+ sOperation +" records are available to show.");
            }
        }                        
    }

    private void showBillOpening(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CompanyBillOpeningDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            CompanyBillOpeningDialogUIController gon = (CompanyBillOpeningDialogUIController) loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showBillAdvanceAmount(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CompanyBillAdvanceAmountDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            CompanyBillAdavceAmountDialogUIController gon = (CompanyBillAdavceAmountDialogUIController) loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }
    
    private void showBillClosing(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CompanyBillClosingDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            CompanyBillClosingDialogUIController gon = (CompanyBillClosingDialogUIController) loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showReBillOpening(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("RepledgeBillOpeningDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeBillOpeningDialogUIController gon = (RepledgeBillOpeningDialogUIController) loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showReBillClosing(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("RepledgeBillClosingDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeBillClosingDialogUIController gon = (RepledgeBillClosingDialogUIController) loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showExpense(DataTable values, MouseEvent event, String sDialogTitle) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpenseDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            ExpenseDialogUIController gon = (ExpenseDialogUIController) loader.getController();
            gon.setParent(this);            
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showIncome(DataTable values, MouseEvent event, String sDialogTitle) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ExpenseDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            ExpenseDialogUIController gon = (ExpenseDialogUIController) loader.getController();
            gon.setParent(this);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(385);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }
    
    @FXML
    private void saveModeON(ActionEvent event) {
        
        dpTodaysDate.setEditable(false);
        dpTodaysDate.setMouseTransparent(true);
        dpTodaysDate.setFocusTraversable(false);                

        txtAvailableBalance.setEditable(true);
        txtAvailableBalance.setMouseTransparent(false);
        txtAvailableBalance.setFocusTraversable(true);       
        
        btCloseAccount.setDisable(false);
        btPrint.setDisable(true);
        
        setPreValues();
        setTodaysValue();        
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {

        dpTodaysDate.setEditable(true);
        dpTodaysDate.setMouseTransparent(false);
        dpTodaysDate.setFocusTraversable(true);                

        txtAvailableBalance.setEditable(false);
        txtAvailableBalance.setMouseTransparent(true);
        txtAvailableBalance.setFocusTraversable(false);       

        btCloseAccount.setDisable(true);
        btPrint.setDisable(false);
        
        setPreValues();
        setTodaysValue();                
    }

    @FXML
    private void btCloseAccountClicked(ActionEvent event) {
        
        String sTodaysDate = CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue());
        boolean validSoftware = false;
        if(DateRelatedCalculations.isDateWithInYear(CommonConstants.ACTIVE_MACHINE.getSoftwareInstalledDate(), sTodaysDate)) {
            validSoftware = true;
        } else {
            PopupUtil.showInfoAlert(event, "Sorry you are pirating the software. Kindly renew your software.");
        }
            
        if(tgOn.isSelected() && validSoftware) 
        {
            String sPreDate = txtPreDate.getText();
            String sPreActualAmount = txtPreActualBalance.getText();
            String sPreAvailableAmount = txtPreAvailableBalance.getText();
            String sPreDeficitAmount = txtPreDeficit.getText();            
            String sFileName = CommonConstants.FILEDATETIMEFORMATTER.format(dpTodaysDate.getValue()) + ".backup";
            String sTodaysActualAmount = txtActualBalance.getText();
            String sTodaysAvailableAmount = txtAvailableBalance.getText();
            String sTodaysDeficitAmount = txtDeficit.getText();            
            
            if(!sTodaysAvailableAmount.isEmpty() && Double.parseDouble(sTodaysAvailableAmount) > 0) {
            
                double dPreActualAmount = Double.parseDouble(sPreActualAmount);
                double dPreAvailableAmount = Double.parseDouble(sPreAvailableAmount);
                double dPreDeficitAmount = Double.parseDouble(sPreDeficitAmount);
                String sPreNote = txtPreNote.getText();
                double dTodaysActualAmount = Double.parseDouble(sTodaysActualAmount);
                double dTodaysAvailableAmount = Double.parseDouble(sTodaysAvailableAmount);
                double dTodaysDeficitAmount = Double.parseDouble(sTodaysDeficitAmount);
                String sTodaysNote = txtTodaysNote.getText();
                
                try {
                    if(dbOp.updateAccountTableMarkToNull()) {
                        if(dbOp.saveRecord(sPreDate, dPreActualAmount, dPreAvailableAmount, 
                                dPreDeficitAmount, sTodaysDate, dTodaysActualAmount, dTodaysAvailableAmount, 
                                dTodaysDeficitAmount, sTodaysNote, sPreNote)) {
                            if(dbOp.saveAvailableAmount(sTodaysDate, dTodaysAvailableAmount, dGPf, dSPf, dTotalPf)) {
                                
                                String sBackupFilePath = dbOp.getBackupFilePath(CommonConstants.ACTIVE_COMPANY_ID);
                                if(sBackupFilePath != null && !sBackupFilePath.isEmpty()) {
                                    new DataBaseBackup().doBackupOperation(sBackupFilePath, sFileName);
                                }
                                PopupUtil.showInfoAlert(event, "Todays date "+sTodaysDate+" account closed successfully.");
                                CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE = sTodaysDate;
                                setInitValues();
                            }
                        }
                    } else {
                        PopupUtil.showErrorAlert(event, "Sorry something went wrong.");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry todays available balance cannot be empty.");
            }
        }
    }

    @FXML
    private void txtAvailableBalanceOnPressed(KeyEvent e) {
        
        if(e.getCode() == KeyCode.BACK_SPACE){ 
            
            String sAmount;
            
            if(txtAvailableBalance.getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txtAvailableBalance.getText());
                sAmount = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder(txtAvailableBalance.getText());
                sb.deleteCharAt(txtAvailableBalance.getCaretPosition() - 1);
                sAmount = sb.toString();
            }
            
            if(!"".equals(sAmount) && Double.parseDouble(sAmount) > 0) 
            {
                double dActualAmount = Double.parseDouble(txtActualBalance.getText());
                double dAvailableAmount = Double.parseDouble(sAmount);
                double dDeficit = dAvailableAmount - dActualAmount;
                txtDeficit.setText(Double.toString(dDeficit));
            } else {
                txtDeficit.setText("-"+txtActualBalance.getText());
            }

            if(Double.parseDouble(txtDeficit.getText()) == 0) {
                txtDeficit.setStyle("-fx-background-color: #55FF30");
            } else {
                txtDeficit.setStyle("-fx-background-color: #FF5555");
            }                    
        }        
    }

    @FXML
    private void txtAvailableBalanceOnTyped(KeyEvent e) {
        
        if(!("0123456789.".contains(e.getCharacter()))){             
            e.consume();
        }         
        
        if("0123456789.".contains(e.getCharacter())){ 
            String sAmount;
            if(txtAvailableBalance.getCaretPosition() == txtAvailableBalance.getText().length()) {
                sAmount = txtAvailableBalance.getText() + e.getCharacter(); 
            } else {
                StringBuilder sb = new StringBuilder(txtAvailableBalance.getText());
                sb.insert(txtAvailableBalance.getCaretPosition(), e.getCharacter());
                sAmount = sb.toString();
            }
            double dActualAmount = Double.parseDouble(txtActualBalance.getText());
            double dAvailableAmount = Double.parseDouble(sAmount);
            double dDeficit = dAvailableAmount - dActualAmount;
            txtDeficit.setText(Double.toString(dDeficit));
            if(Double.parseDouble(txtDeficit.getText()) == 0) {
                txtDeficit.setStyle("-fx-background-color: #55FF30");
            } else {
                txtDeficit.setStyle("-fx-background-color: #FF5555");
            }                                
        }        
        
    }

    @FXML
    private void dpTodaysDateOnAction(ActionEvent event) {
        
        if(tgOff.isSelected()) 
        {
            try {
                HashMap<String, String> preValues = dbOp.getTodaysAccountSettingsValues(CommonConstants.ACTIVE_COMPANY_ID, CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
                if(preValues != null)
                {
                    tbTodaysAccount.getItems().removeAll(tbTodaysAccount.getItems());
                    txtPreDate.setText(preValues.get("PRE_DATE"));
                    txtPreActualBalance.setText(preValues.get("PRE_ACTUAL_AMOUNT"));
                    txtPreAvailableBalance.setText(preValues.get("PRE_AVAILABLE_AMOUNT"));
                    txtPreDeficit.setText(preValues.get("PRE_DEFICIT_AMOUNT"));  
                    dpTodaysDate.setValue(LocalDate.parse(preValues.get("TODAYS_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtAvailableBalance.setText(preValues.get("TODAYS_AVAILABLE_AMOUNT"));
                    txtTodaysNote.setText(preValues.get("TODAYS_NOTE"));
                    txtTodaysNote.setText(preValues.get("TODAYS_NOTE"));
                    if(Double.parseDouble(txtPreDeficit.getText()) == 0) {
                        txtPreDeficit.setStyle("-fx-background-color: #55FF30");
                    } else {
                        txtPreDeficit.setStyle("-fx-background-color: #FF5555");
                    }                    
                    setTodaysValue();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry the selected date account was not yet closed.");
                    setPreValues();
                    setTodaysValue();                
                }
            } catch (SQLException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setAllJewelAccountTableValues() {
    
        //inbound
        setNYCashDrawerToLockerTableValues();
        setSNYCashDrawerToShopLockerTableValues();
        setNYRepDrawerToShopLockerTableValues();
        setRNYRepLockerToRepDrawerTableValues();
        //outbound
        setNYShopLockerToCashDrawerTableValues();        
        setSNYShopLockerToCashDrawerTableValues();
        setRNYShopLockerToRepDrawerTableValues();
        setNYCashDrawerToCustomerTableValues();
        setSNYCashDrawerToCustomerTableValues();
        setNYRepDrawerToCustomerTableValues();
        setRNYRepDrawerToRepLockerTableValues();
    }

    private void setRNYRepLockerToRepDrawerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getRNYDeliveredTableValue("GOLD");
            DataTable nYLockedLaterValues = dbOp.getRNYDeliveredLaterTableValue("GOLD");
            DataTable nYSuspenseValues = dbOp.getRNYSuspenseTableValue("GOLD");
            if(nYLockedValues != null) {
                setValuesToGivenReTable(tbRNYDeliveredLater, nYLockedLaterValues);
                setValuesToGivenReTable(tbRNYSuspense, nYSuspenseValues);
                setValuesToGivenReTable(tbRNYDelivered, nYLockedValues);                
                setValuesToReFields(txtRNYDCount, txtRNYDAmount, txtRNYDCompAmount, tbRNYDelivered);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setNYShopLockerToCashDrawerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getLockerToCashDrawerTableValue("GOLD", CommonConstants.SHOP_LOCKER);
            if(nYLockedValues != null) {
                setValuesToDeliveredTable(tbGNYDelivered, nYLockedValues);
                setValuesToClosedFields(txtGNYDCount, txtGNYDAmount, txtGNYDInterest, txtGNYDInteresteAmt, tbGNYDelivered);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setSNYShopLockerToCashDrawerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getLockerToCashDrawerTableValue("SILVER", CommonConstants.SHOP_LOCKER);
            if(nYLockedValues != null) {
                setValuesToDeliveredTable(tbSNYDelivered, nYLockedValues);
                setValuesToClosedFields(txtSNYDCount, txtSNYDAmount, txtSNYDInterest, txtSNYDInteresteAmt, tbSNYDelivered);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private void setRNYShopLockerToRepDrawerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getRNYShopLockerToRepDrawerTableValue();
            if(nYLockedValues != null) {
                setValuesToGivenTable(tbGNYLockerToRep, nYLockedValues);
                setValuesToFields(txtGNYLockerToRepCount, txtGNYLLockerToRepAmount, tbGNYLockerToRep);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private void setRNYRepDrawerToRepLockerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getRNYRepledgeDrawerToRepLockerTableValue(CommonConstants.REPLEDGE_DRAWER);
            if(nYLockedValues != null) {
                setValuesToGivenTable(tbGNYRepDrawerToRepLocker, nYLockedValues);
                setValuesToFields(txtGNYRepDrawerToRepLockerCount, txtGNYRepDrawerToRepLockerAmount, 
                        tbGNYRepDrawerToRepLocker);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setNYCashDrawerToCustomerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getLockerToCashDrawerTableValue("GOLD", CommonConstants.CASH_DRAWER);
            if(nYLockedValues != null) {
                setValuesToDeliveredTable(tbGNYCashDrawerToCustomer, nYLockedValues);
                setValuesToClosedFields(txtGNYDCashDrawerToCustomerCount, 
                        txtGNYDCashDrawerToCustomerAmount, txtGNYDCashDrawerToCustomerInterest, 
                        txtGNYDCashDrawerToCustomerInteresteAmt, tbGNYCashDrawerToCustomer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private void setSNYCashDrawerToCustomerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getNYDeliveredTableValue("SILVER", "CLOSED", 
                    CommonConstants.CASH_DRAWER);
            if(nYLockedValues != null) {
                setValuesToDeliveredTable(tbSNYCashDrawerToCustomer, nYLockedValues);
                setValuesToClosedFields(txtSNYDCashDrawerToCustomerCount, 
                        txtSNYDCashDrawerToCustomerAmount, txtSNYDCashDrawerToCustomerInterest, 
                        txtSNYDCashDrawerToCustomerInteresteAmt, tbSNYCashDrawerToCustomer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private void setNYRepDrawerToCustomerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getNYDeliveredTableValue("GOLD", "CLOSED", 
                    CommonConstants.REPLEDGE_DRAWER);
            if(nYLockedValues != null) {
                setValuesToDeliveredTable(tbGNYRepDrawerToCustomer, nYLockedValues);
                setValuesToClosedFields(txtGNYLRepDrawerToCustomerCount, 
                        txtGNYLRepDrawerToCustomerAmount, txtGNYLRepDrawerToCustomerInterest, 
                        txtGNYLRepDrawerToCustomerInterest, tbGNYRepDrawerToCustomer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setNYCashDrawerToLockerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getNYLockedTableValue("GOLD", "OPENED", CommonConstants.CASH_DRAWER);
            if(nYLockedValues != null) {
                setValuesToGivenTable(tbGNYLocked, nYLockedValues);
                setValuesToFields(txtGNYLCount, txtGNYLAmount, tbGNYLocked);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setNYRepDrawerToShopLockerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getNYLockedTableValue("GOLD", "OPENED", CommonConstants.REPLEDGE_DRAWER);
            if(nYLockedValues != null) {
                setValuesToGivenTable(tbGNYRepToLocked, nYLockedValues);
                setValuesToFields(txtGNYLRepToCount, txtGNYLRepToAmount, tbGNYRepToLocked);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setSNYCashDrawerToShopLockerTableValues() {
        
        try {
            DataTable nYLockedValues = dbOp.getNYLockedTableValue("SILVER", "OPENED", CommonConstants.CASH_DRAWER);
            if(nYLockedValues != null) {
                setValuesToGivenTable(tbSNYLocked, nYLockedValues);
                setValuesToFields(txtSNYLCount, txtSNYLAmount, tbSNYLocked);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void setValuesToFields(TextField count, TextField amount, TableView<TodaysJewelAccountBean> table) {
    
        int iCount = 0;
        double dAmount = 0.0;
        for(TodaysJewelAccountBean bean : table.getItems()) {
            iCount++;
            dAmount += bean.getDAmount();
        }
        count.setText(Integer.toString(iCount));
        amount.setText(Double.toString(dAmount));
    }

    private void setValuesToClosedFields(TextField count, TextField amount,
            TextField interest, TextField intrAmt,
            TableView<TodaysClosedJewelAccountBean> table) {
    
        int iCount = 0;
        double dAmount = 0.0;
        double dIntrAmt = 0.0;
        for(TodaysClosedJewelAccountBean bean : table.getItems()) {
            iCount++;
            dAmount += bean.getDAmount();
            dIntrAmt += bean.getDInterestAmt();
        }
        count.setText(Integer.toString(iCount));
        amount.setText(Double.toString(dAmount));
        interest.setText(Double.toString(dIntrAmt));
        intrAmt.setText(Double.toString(dAmount + dIntrAmt));
    }
    
    private void setValuesToReFields(TextField count, TextField amount, TextField reAmount, TableView<TodaysAccountJewelRepledgeBean> table) {
    
        int iCount = 0;
        double dAmount = 0.0;
        double dReAmount = 0.0;
        for(TodaysAccountJewelRepledgeBean bean : table.getItems()) {
            iCount++;
            dAmount += bean.getSRepledgeAmount();
            dReAmount += bean.getSCompanyAmount();
        }
        count.setText(Integer.toString(iCount));
        amount.setText(Double.toString(dAmount));
        reAmount.setText(Double.toString(dReAmount));
    }
    
    private void setValuesToGivenTable(TableView<TodaysJewelAccountBean> table, DataTable nYLockedValues) {
    
        table.getItems().remove(0, table.getItems().size());
        for(int i=0; i<nYLockedValues.getRowCount(); i++) {            
            String sSlNo = nYLockedValues.getRow(i).getColumn(0).toString();
            String sBillNumber = nYLockedValues.getRow(i).getColumn(1).toString();
            double dAmount = Double.parseDouble(nYLockedValues.getRow(i).getColumn(2).toString());
            String sDate = nYLockedValues.getRow(i).getColumn(3).toString();
            String sStatus = nYLockedValues.getRow(i).getColumn(4).toString();
            String sItems = nYLockedValues.getRow(i).getColumn(5).toString();
            table.getItems().add(new TodaysJewelAccountBean(sSlNo, sBillNumber, dAmount, sDate, sStatus, sItems, false));
        }
    }

    private void setValuesToDeliveredTable(TableView<TodaysClosedJewelAccountBean> table, DataTable nYLockedValues) {
    
        table.getItems().remove(0, table.getItems().size());
        for(int i=0; i<nYLockedValues.getRowCount(); i++) {            
            String sSlNo = nYLockedValues.getRow(i).getColumn(0).toString();
            String sBillNumber = nYLockedValues.getRow(i).getColumn(1).toString();            
            String sOpenedDate = nYLockedValues.getRow(i).getColumn(2).toString();
            String sClosedDate = nYLockedValues.getRow(i).getColumn(3).toString();            
            double dAmount = Double.parseDouble(nYLockedValues.getRow(i).getColumn(4).toString());
            double dInterest = Double.parseDouble(nYLockedValues.getRow(i).getColumn(5).toString());
            double dInterestAmt = Double.parseDouble(nYLockedValues.getRow(i).getColumn(6).toString());
            double dAdvAmount = Double.parseDouble(nYLockedValues.getRow(i).getColumn(7).toString());            
            String sToGet = nYLockedValues.getRow(i).getColumn(8).toString();
            String sGot = nYLockedValues.getRow(i).getColumn(9).toString();
            String sClosedTime = nYLockedValues.getRow(i).getColumn(10).toString();
            String sCreatedUser = nYLockedValues.getRow(i).getColumn(11).toString();
            String sStatus = nYLockedValues.getRow(i).getColumn(12).toString();
            table.getItems().add(new TodaysClosedJewelAccountBean(sSlNo, sBillNumber, sOpenedDate, sClosedDate, 
                    dAmount, dInterest, dInterestAmt, dAdvAmount, 
                    sToGet, sGot, sClosedTime, sCreatedUser, false, sStatus));
        }
    }
    
    private void setValuesToGivenReTable(TableView<TodaysAccountJewelRepledgeBean> table, DataTable nYLockedValues) {
    
        table.getItems().remove(0, table.getItems().size());
        for(int i=0; i<nYLockedValues.getRowCount(); i++) {            
            String sSlNo = nYLockedValues.getRow(i).getColumn(0).toString();
            String sRepledgeBillNumber = nYLockedValues.getRow(i).getColumn(1).toString();
            String sRepledgeName = nYLockedValues.getRow(i).getColumn(2).toString();
            String sRepledgeDate = nYLockedValues.getRow(i).getColumn(3).toString();
            double dRepledgeAmount = Double.parseDouble(nYLockedValues.getRow(i).getColumn(4).toString());
            String sBillNumber = nYLockedValues.getRow(i).getColumn(5).toString();
            String sDate = nYLockedValues.getRow(i).getColumn(6).toString();
            double dAmount = Double.parseDouble(nYLockedValues.getRow(i).getColumn(7).toString());
            String sStatus = nYLockedValues.getRow(i).getColumn(8).toString();
            String sRepledgeBillId = nYLockedValues.getRow(i).getColumn(9).toString();
            String sSuspenseDate = null;
            if(nYLockedValues.getRow(i).getColumn(10) != null) {
                sSuspenseDate = nYLockedValues.getRow(i).getColumn(10).toString();
            }
            table.getItems().add(new TodaysAccountJewelRepledgeBean(sSlNo, sRepledgeBillNumber, sRepledgeName, 
                    sRepledgeDate, dRepledgeAmount, sBillNumber, sDate, 
                    dAmount, sStatus, sRepledgeBillId, sSuspenseDate));
        }
    }
    
    private void selectOrDeSelectAll(TableView<TodaysJewelAccountBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }

    private void selectBill(TableView<TodaysJewelAccountBean> table, String toSelectBillNumber) {
        table.getItems().stream().forEach((bean) -> {
            if(bean.getSBillNumber().equals(toSelectBillNumber)) {
                bean.setBChecked(true); 
                GNY_LOCKED_SELECTED = true;
            }
        });
        if(!GNY_LOCKED_SELECTED) {
            PopupUtil.showInfoAlert("Sorry the scanned bill is not there in the list.");
        }
        GNY_LOCKED_SELECTED = false;
    }

    private void selectOutboundBill(TableView<TodaysClosedJewelAccountBean> table, String toSelectBillNumber) {
        table.getItems().stream().forEach((bean) -> {
            if(bean.getSBillNumber().equals(toSelectBillNumber)) {
                bean.setBChecked(true); 
                GNY_LOCKED_SELECTED = true;
            }
        });
        if(!GNY_LOCKED_SELECTED) {
            PopupUtil.showInfoAlert("Sorry the scanned bill is not there in the list.");
        }
        GNY_LOCKED_SELECTED = false;
    }
    
    private void selectOrDeSelectAllClosed(TableView<TodaysClosedJewelAccountBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }
    
    private void updateStatus(TableView<TodaysJewelAccountBean> table, 
            String sStatus, String sPhysicalLoc, String sMaterialType) {
        
        try {
            int iCount = 0;
            StringBuilder remainingCriteria = new StringBuilder("AND (");
            String sCriteria = null;
            
            for(TodaysJewelAccountBean bean : table.getItems()) {
                if(bean.isBChecked()){
                    iCount++;
                    remainingCriteria.append("bill_number = '");
                    remainingCriteria.append(bean.getSBillNumber());
                    remainingCriteria.append("' OR ");
                }
            }
            if(iCount > 0) {
                remainingCriteria.replace(remainingCriteria.lastIndexOf(" OR "), remainingCriteria.length(), "");
                remainingCriteria.append(")");
                sCriteria = remainingCriteria.toString();
                if(dbOp.updateStatus(sStatus, sPhysicalLoc, sMaterialType, sCriteria)) {
                    PopupUtil.showInfoAlert("Selected bill has been changed to "+ sStatus +" status successfully.");                    
                }                
            } else {
                PopupUtil.showInfoAlert("Sorry any of a row should be selected to changed "+ sStatus +" status.");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateStatusForPhysicalLoc(TableView<TodaysJewelAccountBean> table, 
            String sPhysicalLoc, String sMaterialType) {
        
        try {
            int iCount = 0;
            StringBuilder remainingCriteria = new StringBuilder("AND (");
            String sCriteria = null;
            
            for(TodaysJewelAccountBean bean : table.getItems()) {
                if(bean.isBChecked()){
                    iCount++;
                    remainingCriteria.append("bill_number = '");
                    remainingCriteria.append(bean.getSBillNumber());
                    remainingCriteria.append("' OR ");
                }
            }
            if(iCount > 0) {
                remainingCriteria.replace(remainingCriteria.lastIndexOf(" OR "), remainingCriteria.length(), "");
                remainingCriteria.append(")");
                sCriteria = remainingCriteria.toString();
                if(dbOp.updateStatusForPhysicalLoc(sPhysicalLoc, sMaterialType, sCriteria)) {
                    PopupUtil.showInfoAlert("Selected bill has been changed to physical location "+ sPhysicalLoc +" successfully.");                    
                }                
            } else {
                PopupUtil.showInfoAlert("Sorry any of a row should be selected to physical location "+ sPhysicalLoc +".");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateStatusForPhysicalLocClosed(TableView<TodaysClosedJewelAccountBean> table, 
            String sPhysicalLoc, String sMaterialType) {
        
        try {
            int iCount = 0;
            StringBuilder remainingCriteria = new StringBuilder("AND (");
            String sCriteria = null;
            
            for(TodaysClosedJewelAccountBean bean : table.getItems()) {
                if(bean.isBChecked() && bean.getSStatus().equals("CLOSED")){
                    iCount++;
                    remainingCriteria.append("bill_number = '");
                    remainingCriteria.append(bean.getSBillNumber());
                    remainingCriteria.append("' OR ");
                }
            }
            if(iCount > 0) {
                remainingCriteria.replace(remainingCriteria.lastIndexOf(" OR "), remainingCriteria.length(), "");
                remainingCriteria.append(")");
                sCriteria = remainingCriteria.toString();
                if(dbOp.updateStatusForPhysicalLoc(sPhysicalLoc, sMaterialType, sCriteria)) {
                    PopupUtil.showInfoAlert("Selected bill has been changed to physical location "+ sPhysicalLoc +" successfully.");                    
                }                
            } else {
                //PopupUtil.showInfoAlert("Sorry any of a row should be selected to physical location "+ sPhysicalLoc +".");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateStatusForPhysicalLocRebilled(TableView<TodaysClosedJewelAccountBean> table, 
            String sPhysicalLoc, String sMaterialType) {
        
        try {
            int iCount = 0;
            StringBuilder remainingCriteria = new StringBuilder("AND (");
            String sCriteria = null;
            
            for(TodaysClosedJewelAccountBean bean : table.getItems()) {
                if(bean.isBChecked() 
                        && (bean.getSStatus().equals("REBILLED")
                        || bean.getSStatus().equals("REBILLED-ADDED")
                        || bean.getSStatus().equals("REBILLED-REMOVED")
                        || bean.getSStatus().equals("REBILLED-MULTIPLE"))){
                    iCount++;
                    remainingCriteria.append("bill_number = '");
                    remainingCriteria.append(bean.getSBillNumber());
                    remainingCriteria.append("' OR ");
                }
            }
            if(iCount > 0) {
                remainingCriteria.replace(remainingCriteria.lastIndexOf(" OR "), remainingCriteria.length(), "");
                remainingCriteria.append(")");
                sCriteria = remainingCriteria.toString();
                if(dbOp.updateStatusForPhysicalLoc(sPhysicalLoc, sMaterialType, sCriteria)) {
                    PopupUtil.showInfoAlert("Selected bill has been changed to physical location "+ sPhysicalLoc +" successfully.");                    
                }                
            } else {
                PopupUtil.showInfoAlert("Sorry any of a row should be selected to physical location "+ sPhysicalLoc +".");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updatePhysicalLoc(TableView<TodaysJewelAccountBean> table, 
            String sPhysicalLoc, String sMaterialType) {
        
        try {
            int iCount = 0;
            StringBuilder remainingCriteria = new StringBuilder("AND (");
            String sCriteria = null;
            
            for(TodaysJewelAccountBean bean : table.getItems()) {
                if(bean.isBChecked() && (bean.getSStatus().equals("OPENED") || bean.getSStatus().equals("LOCKED"))){
                    iCount++;
                    remainingCriteria.append("bill_number = '");
                    remainingCriteria.append(bean.getSBillNumber());
                    remainingCriteria.append("' OR ");
                }
            }
            if(iCount > 0) {
                remainingCriteria.replace(remainingCriteria.lastIndexOf(" OR "), remainingCriteria.length(), "");
                remainingCriteria.append(")");
                sCriteria = remainingCriteria.toString();
                if(dbOp.updateStatusForPhysicalLoc(sPhysicalLoc, sMaterialType, sCriteria)) {
                    PopupUtil.showInfoAlert("Selected bill has been changed to physical location "+ sPhysicalLoc +" successfully.");                    
                }                
            } else {
                PopupUtil.showInfoAlert("Sorry any of a row should be selected to change physical location "+ sPhysicalLoc +".");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateStatusForClosed(TableView<TodaysClosedJewelAccountBean> table, String sStatus, String sMaterialType) {
        
        try {
            int iCount = 0;
            StringBuilder remainingCriteria = new StringBuilder("AND (");
            String sCriteria = null;
            
            for(TodaysClosedJewelAccountBean bean : table.getItems()) {
                if(bean.isBChecked() && bean.getSStatus().equals("CLOSED")){
                    iCount++;
                    remainingCriteria.append("bill_number = '");
                    remainingCriteria.append(bean.getSBillNumber());
                    remainingCriteria.append("' OR ");
                }
            }
            if(iCount > 0) {
                remainingCriteria.replace(remainingCriteria.lastIndexOf(" OR "), remainingCriteria.length(), "");
                remainingCriteria.append(")");
                sCriteria = remainingCriteria.toString();
                if(dbOp.updateStatus(sStatus, CommonConstants.DELIVERED, sMaterialType, sCriteria)) {
                    PopupUtil.showInfoAlert("Selected bill has been changed to "+ sStatus +" status successfully.");                    
                }                
            } else {
                //PopupUtil.showInfoAlert("Sorry any of a row should be selected to changed "+ sStatus +" status.");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void btGNYLSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYLocked, true);
    }

    @FXML
    private void btGNYLDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYLocked, false);
    }

    @FXML
    private void btGNYLLockOnAction(ActionEvent event) {
        updateStatus(tbGNYLocked, "LOCKED", CommonConstants.SHOP_LOCKER, "GOLD");
        setNYCashDrawerToLockerTableValues();
    }

    @FXML
    private void btGNYDSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbGNYDelivered, true);
    }

    @FXML
    private void btGNYDDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbGNYDelivered, false);
    }

    @FXML
    private void btGNYDOnAction(ActionEvent event) {       
        updateStatusForPhysicalLocClosed(tbGNYDelivered, CommonConstants.CASH_DRAWER, "GOLD");
        updateStatusForPhysicalLocRebilled(tbGNYDelivered, CommonConstants.CASH_DRAWER, "GOLD");
        setNYShopLockerToCashDrawerTableValues();
        setNYCashDrawerToCustomerTableValues();
    }

    @FXML
    private void btSNYLSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbSNYLocked, true);
    }

    @FXML
    private void btSNYLDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbSNYLocked, false);
    }

    @FXML
    private void btSNYLLockOnAction(ActionEvent event) {
        updateStatus(tbSNYLocked, "LOCKED", CommonConstants.SHOP_LOCKER, "SILVER");
        setSNYCashDrawerToShopLockerTableValues();        
    }

    @FXML
    private void btSNYDSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbSNYDelivered, true);
    }

    @FXML
    private void btSNYDDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbSNYDelivered, false);
    }

    @FXML
    private void btSNYDOnAction(ActionEvent event) {     
        updateStatusForPhysicalLocClosed(tbSNYDelivered, CommonConstants.CASH_DRAWER, "SILVER");
        updateStatusForPhysicalLocRebilled(tbSNYDelivered, CommonConstants.CASH_DRAWER, "SILVER");
        setSNYShopLockerToCashDrawerTableValues();
        setSNYCashDrawerToCustomerTableValues();
    }

    @FXML
    private void tbRNYDeliveredOnMouseClicked(MouseEvent event) {
                
        int index = tbRNYDelivered.getSelectionModel().getSelectedIndex();        
        
        if(event.getClickCount() == 2 && (index >= 0)) 
        {
            String status = tbRNYDelivered.getItems().get(index).getSRepledgeStatus();
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            if("GIVEN".equals(status) || "OPENED".equals(status) || "SUSPENSE".equals(status)) {
                gon.closeBill(tbRNYDelivered.getItems().get(index).getSRepledgeBillId(), true);
            } else {
                gon.viewBill(tbRNYDelivered.getItems().get(index).getSRepledgeBillId());
            }

            dialog.setTitle("Repledge Bill Closing");      
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(0);
            dialog.setY(5);
            dialog.setWidth(bounds.getWidth());
            dialog.setHeight(bounds.getHeight()-5);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.showAndWait();        
        }

    }

    @FXML
    private void btAvailableBalanceClicked(ActionEvent event) {

        try {
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvailableBalanceDialog.fxml"));
            Parent root = null;
            try {
                root = (Parent) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            AvailableBalanceDialogUIController gon = (AvailableBalanceDialogUIController) loader.getController();
            List<AvailableBalanceBean> currencyList =
                    dbOp.getDenominationValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            
            if(tgOn.isSelected()) {
                gon.setParent(this, true);                
            } else {
                gon.setParent(this, false);                
            }
            gon.setInitValues(currencyList);
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(180);
            dialog.setY(175);
            dialog.setTitle("Available Balance");
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btPrintClicked(ActionEvent event) {
        try {
            String sFileName = CommonConstants.FILEDATETIMEFORMATTER.format(dpTodaysDate.getValue());
            String sBackupFilePath = dbOp.getBackupFilePath(CommonConstants.ACTIVE_COMPANY_ID);

            List<Map<String, Object>> allPages = new ArrayList<>();
            allPages.add(todaysAccountPrintPreparation());
            if(todaysAccountDenominationPrintPreparation() != null) {
                allPages.add(todaysAccountDenominationPrintPreparation());
            }
            if(billDebitPrintPreparation() != null) {
                allPages.add(billDebitPrintPreparation());
            }
            if(compAdvanceAmtPrintPreparation() != null) {
                allPages.add(compAdvanceAmtPrintPreparation());
            }
            if(billCreditPrintPreparation() != null) {
                allPages.add(billCreditPrintPreparation());
            }
            if(repBillCreditPrintPreparation() != null) {
                allPages.add(repBillCreditPrintPreparation());
            }
            if(repBillDebitPrintPreparation() != null) {
                allPages.add(repBillDebitPrintPreparation());
            }           
            if(repBillPlannedButNotPrintPreparation() != null) {
                allPages.add(repBillPlannedButNotPrintPreparation());
            }           
            if(creditDebitPrintPreparation() != null) {
                allPages.add(creditDebitPrintPreparation());
            }           
            if(billCreditIndexPrintPreparation() != null) {
                allPages.add(billCreditIndexPrintPreparation());
            }
            NoticeUtil noticeUtil = new NoticeUtil();                
            JasperPrint[] jPrint = noticeUtil.mergeaAndGenerateNoticeOperation(allPages);
            noticeUtil.mergeaAndGenerateNoticeOperationAndToPDF(jPrint[0], sBackupFilePath, sFileName);
        } catch (JRException | SQLException | FileNotFoundException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private Map<String, Object> todaysAccountPrintPreparation() {
        String sFileName = CommonConstants.REPORT_LOCATION
                + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                + "\\todays_account.jasper";
        List<TodaysAccountPrintBean> ParamList = new ArrayList<>();
        for(TodaysAccountBean bean: tbTodaysAccount.getItems()) {
            TodaysAccountPrintBean printBean = new TodaysAccountPrintBean();
            printBean.setsOperation(bean.getSOperation());
            printBean.setdCount((int)bean.getDCount());
            printBean.setdCredit(bean.getDCredit());
            printBean.setdDebit(bean.getDDebit());
            String combo = bean.getSCreditCombo();
            combo = combo.replace("( ", "(");
            combo = combo.replace(" )", ")");
            combo = combo.replace(") (", ")(");
            printBean.setsCreditCombo(combo);
            ParamList.add(printBean);
        }
        JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("BillCalcCollectionBeanParam", tableList);
        parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
        parameters.put("actualBalance", Double.parseDouble(txtActualBalance.getText()));
        parameters.put("deficit", Double.parseDouble(txtDeficit.getText()));
        parameters.put("availableBalance", Double.parseDouble(txtAvailableBalance.getText()));
        parameters.put("note", txtTodaysNote.getText());
        Map<String, Object> fileNameAndParameters = new HashMap<>();
        fileNameAndParameters.put(FILE_NAME, sFileName);
        fileNameAndParameters.put(PARAMAETERS, parameters);
        return fileNameAndParameters;   
    }

    private Map<String, Object> todaysAccountDenominationPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\todays_account_denomination.jasper";

            List<AvailableBalancePrintBean> currencyList =
                    dbOp.getDenominationPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));

            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(currencyList);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            parameters.put("actualBalance", Double.parseDouble(txtActualBalance.getText()));
            parameters.put("deficit", Double.parseDouble(txtDeficit.getText()));
            parameters.put("availableBalance", Double.parseDouble(txtAvailableBalance.getText()));
            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            return fileNameAndParameters;
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private Map<String, Object> creditDebitPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\expensesandincome.jasper";                        

            List<ExpenseIncomeTotalPrintVals> compBillDebitTotParamList = new ArrayList<>();	   
            HashMap<String, String> expenseValues = 
                    dbOp.getAllExpensesTotalPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            ExpenseIncomeTotalPrintVals expenseBean = new ExpenseIncomeTotalPrintVals("EXPENSES", 
                                Integer.parseInt(expenseValues.get("expense_count")), 
                                Double.parseDouble(expenseValues.get("debit")));            
            compBillDebitTotParamList.add(expenseBean);
            
            HashMap<String, String> incomeValues = 
                    dbOp.getAllIncomeAccountValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));            
            ExpenseIncomeTotalPrintVals incomeBean = new ExpenseIncomeTotalPrintVals("INCOME", 
                                Integer.parseInt(incomeValues.get("income_count")), 
                                Double.parseDouble(incomeValues.get("credit")));            
            compBillDebitTotParamList.add(incomeBean);

            List<ExpenseIncomePrintVals> compBillDebitGoldParamList = new ArrayList<>();            
            DataTable billDebitValues = 
                    dbOp.getExpenseDetailedPrintValue(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            for(int i=0; i<billDebitValues.getRowCount(); i++) {              
                ExpenseIncomePrintVals vals = new ExpenseIncomePrintVals();               
                String sExpenseId = billDebitValues.getRow(i).getColumn(1).toString();
                String sScreenName = billDebitValues.getRow(i).getColumn(2).toString();
                String sReason = billDebitValues.getRow(i).getColumn(3).toString(); 
                vals.setReason(sReason);                
                if(sScreenName.equals("EMPLOYEE SALARY AMOUNT")) {
                    String[] idAndName = billDebitValues.getRow(i).getColumn(3).toString().split(" - ");
                    try {
                        sReason = idAndName[1] + " (Total Salary: " + dbOp.getEmployeeTotalSal(idAndName[0])
                                + ", Total Reduced Amount: " + dbOp.getEmployeeAdvanceAmountReducedBySalId(sExpenseId) + ")\n"
                                + "(" + dbOp.getEmployeeAdvanceAmountDetails(sExpenseId) + ")";
                        vals.setReason(sReason);
                    } catch (SQLException ex) {
                        Logger.getLogger(ExpenseDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }            

                vals.setAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(4).toString()));
                compBillDebitGoldParamList.add(vals);
            }

            List<ExpenseIncomePrintVals> compBillDebitSilverParamList = new ArrayList<>();
            DataTable billCreditValues = 
                    dbOp.getIncomeDetailedPrintValue(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            for(int i=0; i<billCreditValues.getRowCount(); i++) {              
                ExpenseIncomePrintVals vals = new ExpenseIncomePrintVals();
                vals.setReason(billCreditValues.getRow(i).getColumn(3).toString());
                vals.setAmount(Double.parseDouble(billCreditValues.getRow(i).getColumn(4).toString()));
                compBillDebitSilverParamList.add(vals);
            }
            
            JRBeanCollectionDataSource totalList = new JRBeanCollectionDataSource(compBillDebitTotParamList);
            JRBeanCollectionDataSource goldList = new JRBeanCollectionDataSource(compBillDebitGoldParamList);
            JRBeanCollectionDataSource silverList = new JRBeanCollectionDataSource(compBillDebitSilverParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TotalBillDebitCollectionDataSet", totalList);
            parameters.put("BillOpenGoldDebitCollectionBeanParam", goldList);
            parameters.put("BillOpenSilverCollectionDataSet", silverList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            if(!compBillDebitSilverParamList.isEmpty()) {
                parameters.put("silverlabel", "INCOME");
            } else {
                parameters.put("silverlabel", "");
            }
            if(!compBillDebitGoldParamList.isEmpty()) {
                parameters.put("goldlabel", "EXPENSE");
            } else {
                parameters.put("goldlabel", "");
            }
           
            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(compBillDebitGoldParamList.isEmpty() && compBillDebitSilverParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }           
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    private Map<String, Object> compAdvanceAmtPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\advanceamount.jasper";                        

            double totAmt = 0;
            double totIntr = 0;
            DataTable billDebitTotValues = 
                    dbOp.getBillAdvanceAmountPrintValue(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));            
            List<CompanyAdvAmtPrintBean> ParamList = new ArrayList<>();	            
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {          
                CompanyAdvAmtPrintBean bean = new CompanyAdvAmtPrintBean();
                bean.setMaterialType(billDebitTotValues.getRow(i).getColumn(0).toString());
                bean.setBillNumber(billDebitTotValues.getRow(i).getColumn(1).toString());
                bean.setCustomerName(billDebitTotValues.getRow(i).getColumn(2).toString());
                bean.setOpeningDate(billDebitTotValues.getRow(i).getColumn(3).toString());
                bean.setAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(4).toString()));
                bean.setPaidAmt(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(5).toString()));
                totAmt += bean.getAmount();
                totIntr += bean.getPaidAmt();
                ParamList.add(bean);
            }

            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            parameters.put("totalCapital", totAmt);
            parameters.put("totalInterest", totIntr);

            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(ParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }              
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    private Map<String, Object> repBillDebitPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\repledgebilldebits.jasper";                        

            double totAmt = 0;
            double totIntr = 0;
            String closingDate = CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue());
            DataTable billDebitTotValues = 
                    dbOp.getRepledgeBillDebitPrintValues(closingDate);                        
            List<RepPrintBean> ParamList = new ArrayList<>();	            
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {
                String plannerId = dbOp.getRepBillCalcClosingPlannerId(closingDate, 
                        billDebitTotValues.getRow(i).getColumn(0).toString(), "BILL CLOSING");
                RepPrintBean bean = new RepPrintBean();                
                if(plannerId == null || plannerId.isEmpty()) {
                    bean.setSBillNumber(billDebitTotValues.getRow(i).getColumn(0).toString());
                } else {
                    bean.setSBillNumber(billDebitTotValues.getRow(i).getColumn(0).toString() 
                            + "\n(PID: " + plannerId + ")");
                }
                bean.setSRepName(billDebitTotValues.getRow(i).getColumn(1).toString());
                bean.setSRepBillNumber(billDebitTotValues.getRow(i).getColumn(2).toString());
                bean.setSOpeningDate(billDebitTotValues.getRow(i).getColumn(3).toString());
                bean.setDAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(4).toString()));
                bean.setDInterest(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(5).toString()));
                totAmt += bean.getDAmount();
                totIntr += bean.getDInterest();
                ParamList.add(bean);
            }

            DataTable nYSuspenseValues = dbOp.getRNYSuspenseTableValue("GOLD");            
            List<RepPrintBean> suspenseParamList = new ArrayList<>();	            
            for(int i=0; i<nYSuspenseValues.getRowCount(); i++) {          
                String suspenseDate = LocalDate.parse(nYSuspenseValues.getRow(i).getColumn(10).toString())
                        .format(CommonConstants.DATETIMEFORMATTER);
                String plannerId = dbOp.getRepBillCalcClosingPlannerId(suspenseDate, 
                        nYSuspenseValues.getRow(i).getColumn(5).toString(), "GET SUSPENSE");
                RepPrintBean bean = new RepPrintBean();
                if(plannerId == null || plannerId.isEmpty()) {
                    bean.setSBillNumber(nYSuspenseValues.getRow(i).getColumn(5).toString());
                } else {
                    bean.setSBillNumber(nYSuspenseValues.getRow(i).getColumn(5).toString() 
                            + "\n(PID: " + plannerId + ")");
                }
                bean.setSRepName(nYSuspenseValues.getRow(i).getColumn(2).toString());
                bean.setSRepBillNumber(nYSuspenseValues.getRow(i).getColumn(1).toString());
                bean.setSOpeningDate(nYSuspenseValues.getRow(i).getColumn(3).toString());
                bean.setDAmount(Double.parseDouble(nYSuspenseValues.getRow(i).getColumn(4).toString()));
                bean.setDInterest(0);
                bean.setSSuspenseDate(nYSuspenseValues.getRow(i).getColumn(10).toString());
                suspenseParamList.add(bean);
            }

            DataTable nYLockedValues = dbOp.getRNYDeliveredTableValue("GOLD");
            List<RepPrintBean> repImmediateParamList = new ArrayList<>();	            
            for(int i=0; i<nYLockedValues.getRowCount(); i++) {          
                RepPrintBean bean = new RepPrintBean();
                bean.setSBillNumber(nYLockedValues.getRow(i).getColumn(5).toString());
                bean.setSRepName(nYLockedValues.getRow(i).getColumn(2).toString());
                bean.setSRepBillNumber(nYLockedValues.getRow(i).getColumn(1).toString());
                bean.setSOpeningDate(nYLockedValues.getRow(i).getColumn(3).toString());
                bean.setDAmount(Double.parseDouble(nYLockedValues.getRow(i).getColumn(4).toString()));
                bean.setDInterest(0);
                bean.setSRepledgeBillId(nYLockedValues.getRow(i).getColumn(9).toString());
                bean.setSSuspenseDate("");
                repImmediateParamList.add(bean);
            }
            
            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);
            JRBeanCollectionDataSource suspenseTableList = new JRBeanCollectionDataSource(suspenseParamList);
            JRBeanCollectionDataSource RepImmediateBeanParam = new JRBeanCollectionDataSource(repImmediateParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            parameters.put("SuspenseCollectionBeanParam", suspenseTableList);
            parameters.put("RepImmediateBeanParam", RepImmediateBeanParam);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            parameters.put("totalCapital", totAmt);
            parameters.put("totalInterest", totIntr);

            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(ParamList.isEmpty() && suspenseParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }                
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    private Map<String, Object> repBillCreditPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\repledgebillcredits.jasper";                        

            double totAmt = 0;
            double totIntr = 0;
            String todaysDate = CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue());
            DataTable billDebitTotValues = 
                    dbOp.getRepledgeBillCreditPrintValues(todaysDate);            
            List<RepPrintBean> ParamList = new ArrayList<>();	            
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {     
                String plannerId = dbOp.getRepBillCalcOpenPlannerId(todaysDate, 
                        billDebitTotValues.getRow(i).getColumn(0).toString());
                
                RepPrintBean bean = new RepPrintBean();
                if(plannerId == null || plannerId.isEmpty()) {
                    bean.setSBillNumber(billDebitTotValues.getRow(i).getColumn(0).toString());
                } else {
                    bean.setSBillNumber(billDebitTotValues.getRow(i).getColumn(0).toString() 
                            + "\n(PID: " + plannerId + ")");
                }
                bean.setSRepName(billDebitTotValues.getRow(i).getColumn(1).toString());
                bean.setSRepBillNumber(billDebitTotValues.getRow(i).getColumn(2).toString());
                bean.setDAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(3).toString()));
                bean.setDInterest(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(4).toString()));
                totAmt += bean.getDAmount();
                totIntr += bean.getDInterest();
                ParamList.add(bean);
            }

            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            parameters.put("totalCapital", totAmt);
            parameters.put("totalInterest", totIntr);

            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(ParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }      
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }

    private Map<String, Object> repBillPlannedButNotPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\repledgePlannedButNotDone.jasper";                        

            String todaysDate = CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue());
            DataTable billDebitTotValues = 
                    dbOp.getRepBillOpenPlannedButNotPrintPreparation(todaysDate);            
            List<RepPrintBean> ParamList = new ArrayList<>();	            
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {                     
                RepPrintBean bean = new RepPrintBean();
                bean.setSBillNumber(billDebitTotValues.getRow(i).getColumn(4).toString() 
                        + "\n(PID: " + billDebitTotValues.getRow(i).getColumn(0).toString() + ")");
                bean.setSRepName(billDebitTotValues.getRow(i).getColumn(9).toString());
                bean.setSRepBillNumber(billDebitTotValues.getRow(i).getColumn(7).toString());
                bean.setDAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(11).toString()));
                bean.setDInterest(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(12).toString()));
                ParamList.add(bean);
            }

            DataTable billCloseValues = 
                    dbOp.getRepBillClosePlannedButNotPrintPreparation(todaysDate);                        
            List<RepPrintBean> ParamCloseList = new ArrayList<>();	            
            for(int i=0; i<billCloseValues.getRowCount(); i++) {                     
                RepPrintBean bean = new RepPrintBean();
                bean.setSBillNumber(billCloseValues.getRow(i).getColumn(5).toString() 
                        + "\n(PID: " + billCloseValues.getRow(i).getColumn(0).toString() + ")");
                bean.setSRepName(billCloseValues.getRow(i).getColumn(9).toString());
                bean.setSRepBillNumber(billCloseValues.getRow(i).getColumn(7).toString());
                bean.setSOpeningDate(billCloseValues.getRow(i).getColumn(6).toString());
                bean.setDAmount(Double.parseDouble(billCloseValues.getRow(i).getColumn(11).toString()));
                bean.setDInterest(Double.parseDouble(billCloseValues.getRow(i).getColumn(12).toString()));
                ParamCloseList.add(bean);
            }

            DataTable billSuspenseValues = 
                    dbOp.getRepBillSuspensePlannedButNotPrintPreparation(todaysDate);                        
            List<RepPrintBean> ParamSuspenseList = new ArrayList<>();	            
            for(int i=0; i<billSuspenseValues.getRowCount(); i++) {                     
                RepPrintBean bean = new RepPrintBean();
                bean.setSBillNumber(billSuspenseValues.getRow(i).getColumn(5).toString() 
                        + "\n(PID: " + billSuspenseValues.getRow(i).getColumn(0).toString() + ")");
                bean.setSRepName(billSuspenseValues.getRow(i).getColumn(9).toString());
                bean.setSRepBillNumber(billSuspenseValues.getRow(i).getColumn(7).toString());
                bean.setSOpeningDate(billSuspenseValues.getRow(i).getColumn(6).toString());
                bean.setDAmount(Double.parseDouble(billSuspenseValues.getRow(i).getColumn(11).toString()));
                bean.setDInterest(Double.parseDouble(billSuspenseValues.getRow(i).getColumn(12).toString()));
                ParamSuspenseList.add(bean);
            }
            
            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);
            JRBeanCollectionDataSource tableCloseList = new JRBeanCollectionDataSource(ParamCloseList);
            JRBeanCollectionDataSource tableSuspenseList = new JRBeanCollectionDataSource(ParamSuspenseList);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            parameters.put("repbillclosingdataset", tableCloseList);
            parameters.put("SuspenseDataset", tableSuspenseList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));

            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(ParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }      
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    private Map<String, Object> billCreditIndexPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\companybillcreditsindex.jasper";                        

            List<CompanyBillCreditTotalPrintBean> compBillDebitTotParamList = new ArrayList<>();	   
            DataTable billDebitTotValues = 
                    dbOp.getBillCreditTotalPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {            
                CompanyBillCreditTotalPrintBean bean = new CompanyBillCreditTotalPrintBean();
                bean.setMaterialType(billDebitTotValues.getRow(i).getColumn(0).toString());
                bean.setTotalCount(Integer.parseInt(billDebitTotValues.getRow(i).getColumn(1).toString()));
                bean.setTotalAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(2).toString()));
                bean.setTotalInterest(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(3).toString()));
                bean.setTotalAdvanceAmtPaidAlready(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(4).toString()));
                bean.setTotalOtherCharges(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(5).toString()));
                bean.setTotalLess(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(6).toString()));
                compBillDebitTotParamList.add(bean);
            }

            List<CompanyBillCreditPrintBean> compBillDebitGoldParamList 
                    = new ArrayList<>();
            List<CompanyBillCreditPrintBean> compBillDebitSilverParamList 
                    = new ArrayList<>();
            DataTable billDebitValues = 
                    dbOp.getBillCreditPrintValuesWithIndex(
                            CommonConstants.DATETIMEFORMATTER.format(
                                    dpTodaysDate.getValue()));            
            for(int i=0; i<billDebitValues.getRowCount(); i++) {              
                if(billDebitValues.getRow(i).getColumn(0).toString().equals("GOLD")) {
                    CompanyBillCreditPrintBean bean 
                            = new CompanyBillCreditPrintBean();
                    bean.setMaterialType(billDebitValues
                            .getRow(i).getColumn(0).toString());
                    bean.setBillNumber(billDebitValues
                            .getRow(i).getColumn(1).toString());
                    bean.setAmount(Double.parseDouble(billDebitValues
                            .getRow(i).getColumn(2).toString()));

                    if(Boolean.parseBoolean(billDebitValues
                            .getRow(i).getColumn(7).toString())) {
                        bean.setCustomerCopy('✓');
                    } else {
                        bean.setCustomerCopy('×');
                        if(Boolean.parseBoolean(billDebitValues
                                .getRow(i).getColumn(10).toString())) {
                            bean.setReason( billDebitValues
                                    .getRow(i).getColumn(13).toString() 
                                + "-" + billDebitValues
                                        .getRow(i).getColumn(14).toString());
                        }                        
                    }
                    
                    if(Boolean.parseBoolean(billDebitValues
                            .getRow(i).getColumn(8).toString())) {
                        bean.setCompanyCopy('✓');
                    } else {
                        bean.setCompanyCopy('×');
                    }
                    
                    if(Boolean.parseBoolean(billDebitValues
                            .getRow(i).getColumn(9).toString())) {
                        bean.setPackingCopy('✓');                        
                    } else {
                        bean.setPackingCopy('×');
                    }

                    if(billDebitValues.getRow(i).getColumn(16) != null
                            && billDebitValues
                                    .getRow(i).getColumn(16)
                                    .toString().contains("REBILLED")
                            && billDebitValues.getRow(i).getColumn(15) != null 
                            && !billDebitValues.getRow(i).getColumn(15).toString().isEmpty()) {
                        HashMap<String, String> repledgeMap = 
                                dbOp.getAllHeaderValuesByRepledgeBillId(
                                        billDebitValues.getRow(i).getColumn(15).toString(), 
                                        "GOLD");
                        if(repledgeMap != null && repledgeMap.get("REPLEDGE_NAME") != null) {
                            String oldReason = bean.getReason() != null ? bean.getReason() + "\n" : "";
                            bean.setReason(oldReason + billDebitValues.getRow(i).getColumn(16).toString()
                                    + " - " + repledgeMap.get("REPLEDGE_NAME"));
                        }
                    }
                    
                    if(billDebitValues.getRow(i).getColumn(11) != null 
                            && !billDebitValues.getRow(i).getColumn(11).toString().equals("SAME")) {
                        StringBuilder sb = new StringBuilder();
                        if(billDebitValues.getRow(i).getColumn(11).toString() != null) {
                            String oldReason = bean.getReason() != null ? bean.getReason() + "\n" : "";
                            sb.append(oldReason);
                            sb.append(billDebitValues.getRow(i).getColumn(11).toString());                            
                        }
                        if(billDebitValues.getRow(i).getColumn(12).toString() != null) {
                            sb.append("-");
                            sb.append(billDebitValues.getRow(i).getColumn(12).toString());
                        }
                        bean.setReason( sb.toString());
                    } else {
                        if(bean.getReason() == null) {
                            bean.setReason(" ");
                        }
                    }
                    compBillDebitGoldParamList.add(bean);
                } else if(billDebitValues.getRow(i).getColumn(0).toString().equals("SILVER")) {
                    CompanyBillCreditPrintBean bean = new CompanyBillCreditPrintBean();
                    bean.setMaterialType(billDebitValues.getRow(i).getColumn(0).toString());
                    bean.setBillNumber(billDebitValues.getRow(i).getColumn(1).toString());
                    bean.setAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(2).toString()));

                    if(Boolean.parseBoolean(billDebitValues.getRow(i).getColumn(7).toString())) {
                        bean.setCustomerCopy('✓');
                    } else {
                        bean.setCustomerCopy('×');
                        if(Boolean.parseBoolean(billDebitValues.getRow(i).getColumn(10).toString())) {
                            bean.setReason( billDebitValues.getRow(i).getColumn(13).toString() 
                                + "-" + billDebitValues.getRow(i).getColumn(14).toString());
                        }                        
                    }
                    
                    if(Boolean.parseBoolean(billDebitValues.getRow(i).getColumn(8).toString())) {
                        bean.setCompanyCopy('✓');
                    } else {
                        bean.setCompanyCopy('×');
                    }
                    
                    if(Boolean.parseBoolean(billDebitValues.getRow(i).getColumn(9).toString())) {
                        bean.setPackingCopy('✓');                        
                    } else {
                        bean.setPackingCopy('×');
                    }
                    
                    if(billDebitValues.getRow(i).getColumn(11) != null 
                            && !billDebitValues.getRow(i).getColumn(11).toString().equals("SAME")) {
                        StringBuilder sb = new StringBuilder();
                        if(bean.getReason() != null) {
                            sb.append(bean.getReason());
                        }
                        if(billDebitValues.getRow(i).getColumn(11).toString() != null) {
                            String oldReason = bean.getReason() != null ? bean.getReason() + "\n" : "";
                            sb.append(oldReason);
                            sb.append(billDebitValues.getRow(i).getColumn(11).toString());                            
                        }
                        if(billDebitValues.getRow(i).getColumn(12).toString() != null) {
                            sb.append("-");
                            sb.append(billDebitValues.getRow(i).getColumn(12).toString());
                        }
                        bean.setReason( sb.toString());
                    } else {
                        if(bean.getReason() == null) {
                            bean.setReason(" ");
                        }
                    }
                    
                    compBillDebitSilverParamList.add(bean);
                }
            }

            JRBeanCollectionDataSource totalList = new JRBeanCollectionDataSource(compBillDebitTotParamList);
            JRBeanCollectionDataSource goldList = new JRBeanCollectionDataSource(compBillDebitGoldParamList);
            JRBeanCollectionDataSource silverList = new JRBeanCollectionDataSource(compBillDebitSilverParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TotalBillDebitCollectionDataSet", totalList);
            parameters.put("BillOpenGoldDebitCollectionBeanParam", goldList);
            parameters.put("BillOpenSilverCollectionDataSet", silverList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            if(!compBillDebitSilverParamList.isEmpty()) {
                parameters.put("silverlabel", "SILVER");
            } else {
                parameters.put("silverlabel", "");
            }
            if(!compBillDebitGoldParamList.isEmpty()) {
                parameters.put("goldlabel", "GOLD");
            } else {
                parameters.put("goldlabel", "");
            }
            
            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(compBillDebitGoldParamList.isEmpty() && compBillDebitSilverParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }           
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    private Map<String, Object> billCreditPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\companybillcredits.jasper";                        

            List<CompanyBillCreditTotalPrintBean> compBillDebitTotParamList = new ArrayList<>();	   
            DataTable billDebitTotValues = 
                    dbOp.getBillCreditTotalPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {            
                CompanyBillCreditTotalPrintBean bean = new CompanyBillCreditTotalPrintBean();
                bean.setMaterialType(billDebitTotValues.getRow(i).getColumn(0).toString());
                bean.setTotalCount(Integer.parseInt(billDebitTotValues.getRow(i).getColumn(1).toString()));
                bean.setTotalAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(2).toString()));
                bean.setTotalInterest(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(3).toString()));
                bean.setTotalAdvanceAmtPaidAlready(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(4).toString()));
                bean.setTotalOtherCharges(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(5).toString()));
                bean.setTotalLess(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(6).toString()));
                compBillDebitTotParamList.add(bean);
            }

            List<CompanyBillCreditPrintBean> compBillDebitGoldParamList = new ArrayList<>();
            List<CompanyBillCreditPrintBean> compBillDebitSilverParamList = new ArrayList<>();
            DataTable billDebitValues = 
                    dbOp.getBillCreditPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));            
            for(int i=0; i<billDebitValues.getRowCount(); i++) {              
                if(billDebitValues.getRow(i).getColumn(0).toString().equals("GOLD")) {
                    CompanyBillCreditPrintBean bean = new CompanyBillCreditPrintBean();
                    bean.setMaterialType(billDebitValues.getRow(i).getColumn(0).toString());
                    bean.setBillNumber(billDebitValues.getRow(i).getColumn(1).toString());
                    bean.setAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(2).toString()));
                    bean.setInterest(Double.parseDouble(billDebitValues.getRow(i).getColumn(3).toString()));
                    bean.setAdvanceAmtPaid(Double.parseDouble(billDebitValues.getRow(i).getColumn(4).toString()));
                    bean.setOtherCharges(Double.parseDouble(billDebitValues.getRow(i).getColumn(5).toString()));
                    bean.setLess(Double.parseDouble(billDebitValues.getRow(i).getColumn(6).toString()));
                    compBillDebitGoldParamList.add(bean);
                } else if(billDebitValues.getRow(i).getColumn(0).toString().equals("SILVER")) {
                    CompanyBillCreditPrintBean bean = new CompanyBillCreditPrintBean();
                    bean.setMaterialType(billDebitValues.getRow(i).getColumn(0).toString());
                    bean.setBillNumber(billDebitValues.getRow(i).getColumn(1).toString());
                    bean.setAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(2).toString()));
                    bean.setInterest(Double.parseDouble(billDebitValues.getRow(i).getColumn(3).toString()));
                    bean.setAdvanceAmtPaid(Double.parseDouble(billDebitValues.getRow(i).getColumn(4).toString()));
                    bean.setOtherCharges(Double.parseDouble(billDebitValues.getRow(i).getColumn(5).toString()));
                    bean.setLess(Double.parseDouble(billDebitValues.getRow(i).getColumn(6).toString()));
                    compBillDebitSilverParamList.add(bean);
                }
            }

            JRBeanCollectionDataSource totalList = new JRBeanCollectionDataSource(compBillDebitTotParamList);
            JRBeanCollectionDataSource goldList = new JRBeanCollectionDataSource(compBillDebitGoldParamList);
            JRBeanCollectionDataSource silverList = new JRBeanCollectionDataSource(compBillDebitSilverParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TotalBillDebitCollectionDataSet", totalList);
            parameters.put("BillOpenGoldDebitCollectionBeanParam", goldList);
            parameters.put("BillOpenSilverCollectionDataSet", silverList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            if(!compBillDebitSilverParamList.isEmpty()) {
                parameters.put("silverlabel", "SILVER");
            } else {
                parameters.put("silverlabel", "");
            }
            if(!compBillDebitGoldParamList.isEmpty()) {
                parameters.put("goldlabel", "GOLD");
            } else {
                parameters.put("goldlabel", "");
            }
            
            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(compBillDebitGoldParamList.isEmpty() && compBillDebitSilverParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }           
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    private Map<String, Object> billDebitPrintPreparation() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\companybilldebits.jasper";                        

            List<CompanyBillDebitTotalPrintBean> compBillDebitTotParamList = new ArrayList<>();	   
            DataTable billDebitTotValues = 
                    dbOp.getBillDebitTotalPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            for(int i=0; i<billDebitTotValues.getRowCount(); i++) {            
                CompanyBillDebitTotalPrintBean bean = new CompanyBillDebitTotalPrintBean();
                bean.setMaterialType(billDebitTotValues.getRow(i).getColumn(0).toString());
                bean.setTotalCount(Integer.parseInt(billDebitTotValues.getRow(i).getColumn(1).toString()));
                bean.setTotalAmount(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(2).toString()));
                bean.setTotalInterest(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(3).toString()));
                bean.setTotalDocCharge(Double.parseDouble(billDebitTotValues.getRow(i).getColumn(4).toString()));
                compBillDebitTotParamList.add(bean);
            }

            List<CompanyBillDebitPrintBean> compBillDebitGoldParamList = new ArrayList<>();
            List<CompanyBillDebitPrintBean> compBillDebitSilverParamList = new ArrayList<>();
            DataTable billDebitValues = 
                    dbOp.getBillDebitPrintValues(CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));            
            for(int i=0; i<billDebitValues.getRowCount(); i++) {              
                if(billDebitValues.getRow(i).getColumn(0).toString().equals("GOLD")) {
                    CompanyBillDebitPrintBean bean = new CompanyBillDebitPrintBean();
                    bean.setMaterialType(billDebitValues.getRow(i).getColumn(0).toString());
                    bean.setBillNumber(billDebitValues.getRow(i).getColumn(1).toString());
                    bean.setTotalAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(2).toString()));
                    bean.setTotalInterest(Double.parseDouble(billDebitValues.getRow(i).getColumn(3).toString()));
                    bean.setTotalDocCharge(Double.parseDouble(billDebitValues.getRow(i).getColumn(4).toString()));
                    bean.setToGiveAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(5).toString()));
                    bean.setGivenAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(6).toString()));
                    bean.setCustomerImage(getBillOpeningImageURL("GOLD", "CUSTOMER", bean.getBillNumber()));
                    bean.setJewelImage(getBillOpeningImageURL("GOLD", "JEWEL", bean.getBillNumber()));
                    bean.setRatePerGram(dbOp.getRatePerGram("GOLD", bean.getBillNumber()));
                    String isNeed = "YES";
                    if(bean.getGivenAmount() != bean.getToGiveAmount()) {
                        isNeed = "YES" + "\n" + dbOp.getRebilledFromDetails("GOLD", bean.getBillNumber());
                        bean.setNeedAttention(isNeed);
                    } else {
                        isNeed = "NO" + "\n" + dbOp.getRebilledFromDetails("GOLD", bean.getBillNumber());
                        bean.setNeedAttention(isNeed);
                    }
                    compBillDebitGoldParamList.add(bean);
                } else if(billDebitValues.getRow(i).getColumn(0).toString().equals("SILVER")) {
                    CompanyBillDebitPrintBean bean = new CompanyBillDebitPrintBean();
                    bean.setMaterialType(billDebitValues.getRow(i).getColumn(0).toString());
                    bean.setBillNumber(billDebitValues.getRow(i).getColumn(1).toString());
                    bean.setTotalAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(2).toString()));
                    bean.setTotalInterest(Double.parseDouble(billDebitValues.getRow(i).getColumn(3).toString()));
                    bean.setTotalDocCharge(Double.parseDouble(billDebitValues.getRow(i).getColumn(4).toString()));
                    bean.setToGiveAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(5).toString()));                    
                    bean.setGivenAmount(Double.parseDouble(billDebitValues.getRow(i).getColumn(6).toString()));
                    bean.setCustomerImage(getBillOpeningImageURL("SILVER", "CUSTOMER", bean.getBillNumber()));
                    bean.setJewelImage(getBillOpeningImageURL("SILVER", "JEWEL", bean.getBillNumber()));
                    bean.setRatePerGram(dbOp.getRatePerGram("SILVER", bean.getBillNumber()));
                    String isNeed = "YES";
                    if(bean.getGivenAmount() != bean.getToGiveAmount()) {
                        isNeed = "YES" + "\n" + dbOp.getRebilledFromDetails("SILVER", bean.getBillNumber());
                        bean.setNeedAttention(isNeed);
                    } else {
                        isNeed = "NO" + "\n" + dbOp.getRebilledFromDetails("SILVER", bean.getBillNumber());
                        bean.setNeedAttention(isNeed);
                    }
                    compBillDebitSilverParamList.add(bean);
                }
            }

            JRBeanCollectionDataSource totalList = new JRBeanCollectionDataSource(compBillDebitTotParamList);
            JRBeanCollectionDataSource goldList = new JRBeanCollectionDataSource(compBillDebitGoldParamList);
            JRBeanCollectionDataSource silverList = new JRBeanCollectionDataSource(compBillDebitSilverParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TotalBillDebitCollectionDataSet", totalList);
            parameters.put("BillOpenGoldDebitCollectionBeanParam", goldList);
            parameters.put("BillOpenSilverCollectionDataSet", silverList);
            parameters.put("TODAYSDATE", CommonConstants.DATETIMEFORMATTER.format(dpTodaysDate.getValue()));
            if(!compBillDebitSilverParamList.isEmpty()) {
                parameters.put("silverlabel", "SILVER"); //nts;sp
            } else {
                parameters.put("silverlabel", "");
            }
            if(!compBillDebitGoldParamList.isEmpty()) {
                parameters.put("goldlabel", "GOLD"); //jq;fk;
            } else {
                parameters.put("goldlabel", "");
            }
            
            Map<String, Object> fileNameAndParameters = new HashMap<>();
            fileNameAndParameters.put(FILE_NAME, sFileName);
            fileNameAndParameters.put(PARAMAETERS, parameters);
            if(compBillDebitGoldParamList.isEmpty() && compBillDebitSilverParamList.isEmpty()) {
                return null;            
            } else {
                return fileNameAndParameters;            
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {   
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @FXML
    private void btShowInBillCalcClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(billCalculatorScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        BillCalculatorController gon = (BillCalculatorController) loader.getController();
        gon.beforeRepClose(tbRNYDelivered.getItems());

        dialog.setTitle("Bill Calculator");      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(0);
        dialog.setY(5);
        dialog.setWidth(bounds.getWidth());
        dialog.setHeight(bounds.getHeight()-5);
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.showAndWait();        
        
    }

    @FXML
    private void tbRNYDeliveredLaterOnMouseClicked(MouseEvent event) {
        
        int index = tbRNYDeliveredLater.getSelectionModel().getSelectedIndex();        
        
        if(event.getClickCount() == 2 && (index >= 0)) 
        {
            String status = tbRNYDeliveredLater.getItems().get(index).getSRepledgeStatus();
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            if("GIVEN".equals(status) || "OPENED".equals(status)) {
                gon.closeBill(tbRNYDeliveredLater.getItems().get(index).getSRepledgeBillId(), true);
            } else {
                gon.viewBill(tbRNYDeliveredLater.getItems().get(index).getSRepledgeBillId());
            }

            dialog.setTitle("Repledge Bill Closing");      
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(0);
            dialog.setY(5);
            dialog.setWidth(bounds.getWidth());
            dialog.setHeight(bounds.getHeight()-5);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.showAndWait();        
        }
        
    }

    @FXML
    private void tbGNYLockedOnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void tbSNYLockedOnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void btRebilledShowInBillCalcClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(billCalculatorScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        BillCalculatorController gon = (BillCalculatorController) loader.getController();
        gon.beforeRepClose(tbRNYDeliveredLater.getItems());

        dialog.setTitle("Bill Calculator");      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(0);
        dialog.setY(5);
        dialog.setWidth(bounds.getWidth());
        dialog.setHeight(bounds.getHeight()-5);
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.showAndWait();                
    }

    @FXML
    private void btSuspenseShowInBillCalcClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(billCalculatorScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        BillCalculatorController gon = (BillCalculatorController) loader.getController();
        gon.beforeRepClose(tbRNYSuspense.getItems());

        dialog.setTitle("Bill Calculator");      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(0);
        dialog.setY(5);
        dialog.setWidth(bounds.getWidth());
        dialog.setHeight(bounds.getHeight()-5);
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.showAndWait();                
    }

    @FXML
    private void txtGoldBillNumberCheckClicked(ActionEvent event) {
        String[] custCopy = txtGoldBillNumberCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "G";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectBill(tbGNYLocked, custCopy[0]);
            txtGoldBillNumberCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }      
    }

    @FXML
    private void txtSilverBillNumberCheckClicked(ActionEvent event) {
        String[] custCopy = txtSilverBillNumberCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String SILVER = "S";
        if(custCopy[1].equals(SILVER) 
                && custCopy[2].equals(PACKING_COPY)){
            selectBill(tbSNYLocked, custCopy[0]);
            txtSilverBillNumberCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }      
    }

    @FXML
    private void btGNYLRepToSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYRepToLocked, true);
    }

    @FXML
    private void btGNYLRepToDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYRepToLocked, false);
    }

    @FXML
    private void btGNYLRepToLockOnAction(ActionEvent event) {
        updateStatus(tbGNYRepToLocked, "LOCKED", CommonConstants.SHOP_LOCKER, "GOLD");
        setNYRepDrawerToShopLockerTableValues();
    }

    @FXML
    private void txtGoldBillNumberRepToCheckClicked(ActionEvent event) {
        String[] custCopy = txtGoldBillNumberRepToCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "G";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectBill(tbGNYRepToLocked, custCopy[0]);
            txtGoldBillNumberRepToCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }              
    }

    @FXML
    private void txtGoldBillNumberLockerToRepCheckClicked(ActionEvent event) {
        String[] custCopy = txtGoldBillNumberLockerToRepCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "G";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectBill(tbGNYLockerToRep, custCopy[0]);
            txtGoldBillNumberLockerToRepCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }              
    }

    @FXML
    private void btGNYLLockerToRepSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYLockerToRep, true);
    }

    @FXML
    private void btGNYLLockerToRepDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYLockerToRep, false);
    }


    @FXML
    private void btGNYDCashDrawerToCustomerSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbGNYCashDrawerToCustomer, true);
    }

    @FXML
    private void btGNYDCashDrawerToCustomerDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbGNYCashDrawerToCustomer, false);
    }

    @FXML
    private void btGNYDCashDrawerToCustomerOnAction(ActionEvent event) {
        updateStatusForClosed(tbGNYCashDrawerToCustomer, "DELIVERED", "GOLD");   
        updateStatusForPhysicalLocClosed(tbGNYCashDrawerToCustomer, CommonConstants.DELIVERED, "GOLD");
        updateStatusForPhysicalLocRebilled(tbGNYCashDrawerToCustomer, CommonConstants.REBILLED, "GOLD");
        setNYCashDrawerToCustomerTableValues();
    }

    @FXML
    private void btSNYDCashDrawerToCustomerSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbSNYCashDrawerToCustomer, true);
    }

    @FXML
    private void btSNYDCashDrawerToCustomerDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbSNYCashDrawerToCustomer, false);
    }

    @FXML
    private void btSNYDCashDrawerToCustomerOnAction(ActionEvent event) {
        updateStatusForClosed(tbSNYCashDrawerToCustomer, "DELIVERED", "SILVER");     
        setSNYCashDrawerToCustomerTableValues();
    }

    @FXML
    private void txtGoldBillNumberRepDrawerToCustomerCheckClicked(ActionEvent event) {
        String[] custCopy = txtGoldBillNumberRepDrawerToCustomerCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "G";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectOutboundBill(tbGNYRepDrawerToCustomer, custCopy[0]);
            txtGoldBillNumberRepDrawerToCustomerCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }                      
    }

    @FXML
    private void btGNYLRepDrawerToCustomerSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbGNYRepDrawerToCustomer, true);
    }

    @FXML
    private void btGNYLRepDrawerToCustomerDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllClosed(tbGNYRepDrawerToCustomer, true);
    }

    @FXML
    private void btGNYLRepDrawerToCustomerLockOnAction(ActionEvent event) {
        updateStatusForClosed(tbGNYRepDrawerToCustomer, "DELIVERED", "GOLD");     
        setNYRepDrawerToCustomerTableValues();
    }

    @FXML
    private void txtGoldBillNumberShopLockerToCashDrawerCheckClicked(ActionEvent event) {
        String[] custCopy = txtGoldBillNumberShopLockerToCashDrawerCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "G";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectOutboundBill(tbGNYDelivered, custCopy[0]);
            txtGoldBillNumberShopLockerToCashDrawerCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }              
    }

    @FXML
    private void txtSilverBillNumberShopLockerToCashDrawerCheckClicked(ActionEvent event) {
        String[] custCopy = txtSiilverBillNumberShopLockerToCashDrawerCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "S";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectOutboundBill(tbSNYDelivered, custCopy[0]);
            txtSiilverBillNumberShopLockerToCashDrawerCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }              
    }

    @FXML
    private void btGNYShopLockerToRepDrawerOnAction(ActionEvent event) {
        updateStatusForPhysicalLoc(tbGNYLockerToRep, CommonConstants.REPLEDGE_DRAWER, "GOLD");
        setRNYShopLockerToRepDrawerTableValues();      
        setRNYRepDrawerToRepLockerTableValues();
    }

    @FXML
    private void txtGoldBillNumberRepDrawerToRepLockerCheckClicked(ActionEvent event) {
        String[] custCopy = txtGoldBillNumberRepDrawerToRepLockerCheck.getText().trim().split("-");
        String PACKING_COPY = "PACK";
        String GOLD = "G";
        if(custCopy[1].equals(GOLD) 
                && custCopy[2].equals(PACKING_COPY)){
            selectBill(tbGNYRepDrawerToRepLocker, custCopy[0]);
            txtGoldBillNumberShopLockerToCashDrawerCheck.setText("");
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Scan. No such bill available in the list.");
        }                      
    }

    @FXML
    private void btGNYRepDrawerToRepLockerSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYRepDrawerToRepLocker, true);
    }

    @FXML
    private void btGNYRepDrawerToRepLockerDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbGNYRepDrawerToRepLocker, false);
    }

    @FXML
    private void btGNYRepDrawerToRepLockerOnAction(ActionEvent event) {        
        updatePhysicalLoc(tbGNYRepDrawerToRepLocker, CommonConstants.SHOP_LOCKER, "GOLD");
        setRNYRepDrawerToRepLockerTableValues();
    }
    
    public void showTab(String tabName) {
        
        if(tabName.equals(CommonConstants.JEWEL_ACCOUNT_TAB)) {
            tpScreen.getSelectionModel().selectLast();
        }
    }
    
    public Image getBillOpeningImage(String materialType, String imageName, String billNumber) 
            throws FileNotFoundException, IOException {
        
        File materialFolder = null;
        if(materialType.equals("GOLD")) {
            materialFolder = new File(compFolder, "GOLD");
        } else {
            materialFolder = new File(compFolder, "SILVER");
        }
        File billNumberFolder = new File(materialFolder, billNumber);
        
        if(imageName.equals("CUSTOMER")) {
            File custTemp = new File(billNumberFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
            if(custTemp.exists()) {                      
                try (FileInputStream fis = new FileInputStream(custTemp)){
                    return new Image(fis);
                }
            }
        }

        if(imageName.equals("JEWEL")) {
            File jewelTemp = new File(billNumberFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
            if(jewelTemp.exists()) {                      
                try (FileInputStream fis = new FileInputStream(jewelTemp)){
                    return new Image(fis);
                }
            }
        }
        
        return null;
    }

    public String getBillOpeningImageURL(String materialType, String imageName, String billNumber) 
            throws FileNotFoundException, IOException {
           
        File materialFolder = null;
        if(materialType.equals("GOLD")) {
            materialFolder = new File(compFolder, "GOLD");
        } else {
            materialFolder = new File(compFolder, "SILVER");
        }
        File billNumberFolder = new File(materialFolder, billNumber);
        
        if(imageName.equals("CUSTOMER")) {
            File custTemp = new File(billNumberFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
            if(custTemp.exists()) {                      
                return custTemp.getAbsolutePath();
            }
        }

        if(imageName.equals("JEWEL")) {
            File jewelTemp = new File(billNumberFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
            if(jewelTemp.exists()) {                      
                return jewelTemp.getAbsolutePath();
            }
        }
        
        return null;
    }
    
}
