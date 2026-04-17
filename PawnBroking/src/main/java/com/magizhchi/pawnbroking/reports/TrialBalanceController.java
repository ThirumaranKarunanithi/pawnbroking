/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tiruk
 */
public class TrialBalanceController implements Initializable {

    public ReportDBOperation dbOp;
    public Stage dialog;
    
    @FXML
    private TabPane tpCompScreen;
    @FXML
    private Label lbTo2;
    @FXML
    private DatePicker dpGenerateFrom;
    @FXML
    private Label lbTo21;
    @FXML
    private DatePicker dpGenerateTo;
    @FXML
    private TextField txtOpenAmount;
    @FXML
    private Button btShowReport;
    @FXML
    private Button btPrintReport;
    @FXML
    private TableView<ParticularBean> tbExpense;
    @FXML
    private TableView<ParticularBean> tbIncome;
    @FXML
    private TableView<ParticularBean> tbAsset;
    @FXML
    private TableView<ParticularBean> tbLiability;
    @FXML
    private TextField txtCashInHand;
    @FXML
    private TextField txtProfitAndLoss;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new ReportDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MISReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String sDate = DateRelatedCalculations.getPreMonth();
        String eDate = DateRelatedCalculations.getTodaysDate();
        dpGenerateFrom.setValue(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
        dpGenerateTo.setValue(LocalDate.parse(eDate, CommonConstants.DATETIMEFORMATTER));
        
        //showExpenseReport();
    }    

    private void cleanAllTables()
    {
        tbExpense.getItems().removeAll(tbExpense.getItems());
        tbIncome.getItems().removeAll(tbIncome.getItems());
        tbAsset.getItems().removeAll(tbAsset.getItems());
        tbLiability.getItems().removeAll(tbLiability.getItems());
    }
    
    private void showReport() throws SQLException
    {
        
        String sOpenDate = CommonConstants.DATETIMEFORMATTER
                .format(dpGenerateFrom.getValue());
        String sCloseDate = CommonConstants.DATETIMEFORMATTER
                .format(dpGenerateTo.getValue());
        
        txtOpenAmount
                .setText(dbOp.getPreDayActualAmount(sOpenDate));
        
        cleanAllTables();
        doExpenseTableWork(sOpenDate, sCloseDate);
        doIncomeTableWork(sOpenDate, sCloseDate);
        doAssetTableWork(sOpenDate, sCloseDate);
        doLiabilityTableWork(sOpenDate, sCloseDate);
        cashInHandCalc();
        profitAndLossCalc();
    }
  
    private void doExpenseTableWork(String sOpenDate, String sCloseDate) throws SQLException {
        
        tbExpense.getItems().add(new ParticularBean("REP OPEN INTEREST", 
                dbOp.getRepOpenInterest(sOpenDate, sCloseDate)));
        //tbExpense.getItems().add(new ParticularBean("REP OPEN DOC CHARGE", 
                //dbOp.getRepOpenDocCharge(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("REP CLOSE INTEREST", 
                dbOp.getRepCloseInterest(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("REP OTHER BILL EXPENSE", 
                dbOp.getRepOtherBillExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("REP OTHER EXPENSE", 
                dbOp.getRepOtherExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("EMP DAILY ALLOWANCE", 
                dbOp.getEmpDailyAllowanceExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("EMP ADVANCE AMOUNT", 
                dbOp.getEmpAdvanceAmtExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("EMP SALARY AMOUNT", 
                dbOp.getEmpSalaryAmtExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("EMP OTHER EXPENSE", 
                dbOp.getEmpOtherAmtExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("COMP BILL EXPENSE", 
                dbOp.getCompBillExpense(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("COMP OTHER EXPENSE", 
                dbOp.getCompanyOtherExpense("EXPENSE", sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("GOLD DISCOUNT", 
                dbOp.getCompDiscount("GOLD", sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("SILVER DISCOUNT", 
                dbOp.getCompDiscount("SILVER", sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("ADVANCE AMOUNT REDUCED", 
                dbOp.getAdvanceAmountReduced(sOpenDate, sCloseDate)));
        tbExpense.getItems().add(new ParticularBean("TOTAL", 
                totalValue(tbExpense)));
    }
    
    private void doIncomeTableWork(String sOpenDate, String sCloseDate) throws SQLException {
        
        tbIncome.getItems().add(new ParticularBean("GOLD OPEN INTEREST", 
                dbOp.getCompOpenInterest("GOLD", sOpenDate, sCloseDate)));
        //tbIncome.getItems().add(new ParticularBean("GOLD OPEN DOC CHARGE", 
                //dbOp.getCompOpenDocCharge("GOLD", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("SILVER OPEN INTEREST", 
                dbOp.getCompOpenInterest("SILVER", sOpenDate, sCloseDate)));
        //tbIncome.getItems().add(new ParticularBean("SILVER OPEN DOC CHARGE", 
                //dbOp.getCompOpenDocCharge("SILVER", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("GOLD CLOSE INTEREST", 
                dbOp.getCompCloseInterest("GOLD", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("SILVER CLOSE INTEREST", 
                dbOp.getCompCloseInterest("SILVER", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("GOLD OTHER CHARGES", 
                dbOp.getCompOtherCharges("GOLD", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("SILVER OTHER CHARGES", 
                dbOp.getCompOtherCharges("SILVER", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("ADVANCE AMOUNT PAID", 
                dbOp.getAdvanceAmountPaid(sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("COMP BILL CREDIT", 
                dbOp.getCompBillCredit(sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("COMP OTHER CREDIT", 
                dbOp.getCompanyOtherIncome("INCOME", sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("REP BILL CREDIT", 
                dbOp.getRepBillCredit(sOpenDate, sCloseDate)));
        tbIncome.getItems().add(new ParticularBean("REP OTHER CREDIT", 
                dbOp.getRepOtherCredit(sOpenDate, sCloseDate)));

        tbIncome.getItems().add(new ParticularBean("TOTAL", 
                totalValue(tbIncome)));
    }
    
    private void doAssetTableWork(String sOpenDate, String sCloseDate) throws SQLException {
        
        tbAsset.getItems().add(new ParticularBean("GOLD BILL OPEN CAPITAL", 
                dbOp.getCompOpenCapital("GOLD", sOpenDate, sCloseDate)));
        tbAsset.getItems().add(new ParticularBean("SILVER BILL OPEN CAPITAL", 
                dbOp.getCompOpenCapital("SILVER", sOpenDate, sCloseDate)));
        tbAsset.getItems().add(new ParticularBean("GOLD BILL CLOSE CAPITAL", 
                "-" +dbOp.getCompCloseCapital("GOLD", sOpenDate, sCloseDate)));
        tbAsset.getItems().add(new ParticularBean("SILVER BILL CLOSE CAPITAL", 
                "-" +dbOp.getCompCloseCapital("SILVER", sOpenDate, sCloseDate)));
        tbAsset.getItems().add(new ParticularBean("OTHER ASSET", 
                dbOp.getCompanyOtherExpense("ASSET", sOpenDate, sCloseDate)));
        tbAsset.getItems().add(new ParticularBean("TOTAL", 
                totalValue(tbAsset)));
    }
    
    private void doLiabilityTableWork(String sOpenDate, String sCloseDate) throws SQLException {
        
        String sOpenAmt = txtOpenAmount.getText().isEmpty() ? "0" : txtOpenAmount.getText();
        double dOpenAmt = Double.parseDouble(sOpenAmt);
        
        tbLiability.getItems().add(new ParticularBean("OPEN AMOUNT", sOpenAmt));
        tbLiability.getItems().add(new ParticularBean("OUTSIDE INVESTMENT", 
                dbOp.getCompanyOtherIncome("LIABILITY", sOpenDate, sCloseDate)));
        tbLiability.getItems().add(new ParticularBean("EMPLOYEE INVESTMENT",
                this.getTotalEmployeeInvestment(sOpenDate, sCloseDate)));
        tbLiability.getItems().add(new ParticularBean("REP OPEN CAPITAL", 
                dbOp.getRepOpenCapital(sOpenDate, sCloseDate)));
        tbLiability.getItems().add(new ParticularBean("REP CLOSE CAPITAL", 
                "-"+dbOp.getRepCloseCapital(sOpenDate, sCloseDate)));
        tbLiability.getItems().add(new ParticularBean("TOTAL", 
                totalValue(tbLiability)));
    }
    
    private String getTotalEmployeeInvestment(String sOpeningDate, String sClosingDate) throws SQLException
    {
        String advAmt = dbOp.getEmployeeAdvAmtCredit(sOpeningDate, sClosingDate);
        String other = dbOp.getEmployeeOtherAmtCredit(sOpeningDate, sClosingDate);
        
        BigDecimal bAA = new BigDecimal(advAmt);
        BigDecimal bo = new BigDecimal(other);
        BigDecimal total = bAA.add(bo);
        return total.toPlainString();
    }
    
    private String totalValue(TableView<ParticularBean> tb)
    {
        BigDecimal total = new BigDecimal("0");
        for(ParticularBean bean : tb.getItems())
        {
            total = total.add(new BigDecimal(bean.getAmount()));
        }
        return total.toPlainString();
    }
    
    private void cashInHandCalc()
    {
        double expenseTotal = Double.parseDouble(
                tbExpense.getItems().get(tbExpense.getItems().size()-1).getAmount());
        double incomeTotal = Double.parseDouble(
                tbIncome.getItems().get(tbIncome.getItems().size()-1).getAmount());
        double assetTotal = Double.parseDouble(
                tbAsset.getItems().get(tbAsset.getItems().size()-1).getAmount());
        double liabilityTotal = Double.parseDouble(
                tbLiability.getItems().get(tbLiability.getItems().size()-1).getAmount());
        
        double credit = incomeTotal + liabilityTotal;
        double debit = expenseTotal + assetTotal;
        
        double cashInHand = credit - debit;
        txtCashInHand.setText(Double.toString(cashInHand));
    }
    
    private void profitAndLossCalc()
    {
        double expenseTotal = Double.parseDouble(
                tbExpense.getItems().get(tbExpense.getItems().size()-1).getAmount());
        double incomeTotal = Double.parseDouble(
                tbIncome.getItems().get(tbIncome.getItems().size()-1).getAmount());
                
        double profitAndLoss = incomeTotal - expenseTotal;
        txtProfitAndLoss.setText(Double.toString(profitAndLoss));
    }
    
    @FXML
    private void dpGenerateToOnChanged(ActionEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    @FXML
    private void btShowReportClicked(ActionEvent event) {
        try {
            showReport();
        } catch (SQLException ex) {
            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btPrintReportClicked(ActionEvent event) {
    }

    @FXML
    private void tbExpenseOnMouseClicked(MouseEvent event) {
        
        int index = tbExpense.getSelectionModel().getSelectedIndex();
        String sParticulars = tbExpense.getItems().
                    get(index).getParticulars(); 
        
        if (event.getClickCount() == 2 && (index >= 0)) {
            
            if(sParticulars != null 
                    && !sParticulars.isEmpty()) 
            {
                switch (sParticulars) {
                    case "REP OTHER BILL EXPENSE":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getRepOtherBillExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "REP OTHER BILL EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "REP OTHER EXPENSE":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getRepOtherExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "REP OTHER EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "EMP DAILY ALLOWANCE":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getEmpDailyAllowance(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "EMP DAILY ALLOWANCE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "EMP ADVANCE AMOUNT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getEmpAdvAmountExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "EMP ADVANCE AMOUNT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "EMP SALARY AMOUNT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getEmpSalaryAmountExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "EMP SALARY AMOUNT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "EMP OTHER EXPENSE":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getEmpOtherExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "EMP OTHER EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "COMP BILL EXPENSE":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getCompBillExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "COMP BILL EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "COMP OTHER EXPENSE":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getCompOtherExpense(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER
                                                    .format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "COMP OTHER EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    default:
                        break;
                }
            } else {
                PopupUtil.showInfoAlert(event, "No any "+ sParticulars +" records are available to show.");
            }
        }      
    }

    @FXML
    private void tbIncomeOnMouseClicked(MouseEvent event) {
        
        int index = tbIncome.getSelectionModel().getSelectedIndex();
        String sParticulars = tbIncome.getItems().
                    get(index).getParticulars(); 
        
        if (event.getClickCount() == 2 && (index >= 0)) {
            
            if(sParticulars != null 
                    && !sParticulars.isEmpty()) 
            {
                switch (sParticulars) {
                    case "COMP BILL CREDIT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getCompBillIncome(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showIncome(values, event, "COMP BILL CREDIT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "COMP OTHER CREDIT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getCompOtherIncome(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showIncome(values, event, "COMP OTHER CREDIT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "REP BILL CREDIT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getRepBillIncome(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showIncome(values, event, "REP BILL CREDIT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "REP OTHER CREDIT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getRepOtherIncome(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showIncome(values, event, "REP OTHER CREDIT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    default:
                        break;
                }
            } else {
                PopupUtil.showInfoAlert(event, "No any "+ sParticulars +" records are available to show.");
            }
        }      
    }

    @FXML
    private void tbAssetOnMouseClicked(MouseEvent event) {
        
        int index = tbAsset.getSelectionModel().getSelectedIndex();
        String sParticulars = tbAsset.getItems().
                    get(index).getParticulars(); 
        
        if (event.getClickCount() == 2 && (index >= 0)) {
            
            if(sParticulars != null 
                    && !sParticulars.isEmpty()) 
            {
                switch (sParticulars) {
                    case "GOLD BILL OPEN CAPITAL":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getBillOpeningTableValue(
                                CommonConstants.ACTIVE_COMPANY_ID, 
                                "GOLD", 
                                CommonConstants
                                        .DATETIMEFORMATTER.format(
                                                dpGenerateFrom.getValue()),
                                CommonConstants
                                    .DATETIMEFORMATTER.format(
                                            dpGenerateTo.getValue()));
                            showBillOpening(values, event, "GOLD BILL OPENING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "SILVER BILL OPEN CAPITAL":
                    {
                        try {
                            DataTable values = dbOp.getBillOpeningTableValue(
                                    CommonConstants.ACTIVE_COMPANY_ID, 
                                    "SILVER", 
                                    CommonConstants.DATETIMEFORMATTER.format(
                                            dpGenerateFrom.getValue()),
                                    CommonConstants.DATETIMEFORMATTER.format(
                                            dpGenerateTo.getValue()));
                            showBillOpening(values, event, "SILVER BILL OPENING", false);
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "GOLD BILL CLOSE CAPITAL":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getBillClosingTableValue(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            "GOLD", 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showBillClosing(values, event, "GOLD BILL CLOSING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "SILVER BILL CLOSE CAPITAL":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getBillClosingTableValue(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            "SILVER", 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showBillClosing(values, event, "GOLD BILL CLOSING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "OTHER ASSET":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getOtherAsset(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showExpense(values, event, "EXPENSE");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    default:
                        break;
                }
            } else {
                PopupUtil.showInfoAlert(event, "No any "+ sParticulars +" records are available to show.");
            }
        }      
    }

    @FXML
    private void tbLiabilityOnMouseClicked(MouseEvent event) {
        
        int index = tbLiability.getSelectionModel().getSelectedIndex();
        String sParticulars = tbLiability.getItems().
                    get(index).getParticulars(); 
        
        if (event.getClickCount() == 2 && (index >= 0)) {
            
            if(sParticulars != null 
                    && !sParticulars.isEmpty()) 
            {
                switch (sParticulars) {
                    case "REP OPEN CAPITAL":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getReGroupBillOpeningTableValue(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            "GOLD", 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showReBillOpening(values, event, "REPLEDGE BILL OPENING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "REP CLOSE CAPITAL":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getReGroupBillClosingTableValue(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            "GOLD", 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showReBillClosing(values, event, "REPLEDGE BILL OPENING", true);
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "OUTSIDE INVESTMENT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getOutSideInvestment(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showIncome(values, event, "OUTSIDE INVESTMENT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    case "EMPLOYEE INVESTMENT":
                    {
                        try {
                            DataTable values = 
                                    dbOp.getEmployeeInvestment(
                                            CommonConstants.ACTIVE_COMPANY_ID, 
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateFrom.getValue()),
                                            CommonConstants.DATETIMEFORMATTER.format(
                                                    dpGenerateTo.getValue()));
                            showIncome(values, event, "EMPLOYEE INVESTMENT");
                        } catch (SQLException ex) {
                            Logger.getLogger(TrialBalanceController.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                        break;
                    }
                    default:
                        break;
                }
            } else {
                PopupUtil.showInfoAlert(event, "No any "+ sParticulars +" records are available to show.");
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
            dialog.setY(150);            
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
            dialog.setY(150);            
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
            dialog.setY(150);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showReBillOpening(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("RepledgeNameGroupOpenDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeNameGroupOpenDialogUIController gon = 
                    (RepledgeNameGroupOpenDialogUIController) 
                    loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(150);            
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }

    private void showReBillClosing(DataTable values, MouseEvent event, String sDialogTitle, boolean isGoldOperation) {
        
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("RepledgeNameGroupCloseDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeNameGroupCloseDialogUIController gon = 
                    (RepledgeNameGroupCloseDialogUIController) 
                    loader.getController();
            gon.setParent(this, isGoldOperation);
            gon.setInitValues(values);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(50);
            dialog.setY(150);           
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
            dialog.setY(150);           
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
            dialog.setY(150);           
            dialog.setTitle(sDialogTitle);
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
    }
}
