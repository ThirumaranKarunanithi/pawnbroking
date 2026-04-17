/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class MISReportController implements Initializable {

    public ReportDBOperation dbOp;
    @FXML
    private TabPane tpCompScreen;
    @FXML
    private TableView<AllDetailsBean> tbTotalCompBillTableView;
    @FXML
    private TableView<AllDetailsBean> tbCompanyBillTableView;

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
        setCompanyMISValues();
    }    

    private void setCompanyMISValues() {   
        
        try {
            tbCompanyBillTableView.getItems().removeAll(tbCompanyBillTableView.getItems());
            tbTotalCompBillTableView.getItems().removeAll(tbTotalCompBillTableView.getItems());
            
            DataTable companyMISValues = dbOp.getCompMISValues();
            
            for(int i=0; i<companyMISValues.getRowCount(); i++) {
                String sMonths = companyMISValues.getRow(i).getColumn(0).toString();
                int sOpenBills = Integer.parseInt(companyMISValues.getRow(i).getColumn(1).toString());
                String sOpenCapAmt = companyMISValues.getRow(i).getColumn(2).toString();
                int sCloseBills = Integer.parseInt(companyMISValues.getRow(i).getColumn(3).toString());
                double sCloseAmount = Double.parseDouble(companyMISValues.getRow(i).getColumn(4).toString());
                double sProfit = Double.parseDouble(companyMISValues.getRow(i).getColumn(5).toString());
                int sStockBills = Integer.parseInt(companyMISValues.getRow(i).getColumn(6).toString());
                double sStockAmount = Double.parseDouble(companyMISValues.getRow(i).getColumn(7).toString());
                int sBillsEarned = Integer.parseInt(companyMISValues.getRow(i).getColumn(8).toString());
                double sAmountEarned = Double.parseDouble(companyMISValues.getRow(i).getColumn(9).toString());
                tbCompanyBillTableView.getItems().add(new AllDetailsBean(sMonths,
                        sOpenBills, sOpenCapAmt,
                        sCloseBills, sCloseAmount,
                        sStockBills, sStockAmount,
                        sBillsEarned, sAmountEarned,
                        sProfit, true));        
            }
            setCompanyHeaderValues("ALL");
        } catch (SQLException ex) {
            Logger.getLogger(MISReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCompanyHeaderValues(String selectOrDeSelectOrAll) {

        int iPcs = 0;
        int sOpenBills = 0;
        double sOpenCapAmt = 0;
        int sCloseBills = 0;
        double sCloseAmount = 0;
        double sProfit = 0;
        int sStockBills = 0;
        double sStockAmount = 0;
        int sBillsEarned = 0;
        double sAmountEarned = 0;

        for(AllDetailsBean bean : tbCompanyBillTableView.getItems()) {
            if(selectOrDeSelectOrAll.equals("SELECTED") && bean.isBChecked()) {
                iPcs++;
                sOpenBills = sOpenBills + bean.getSOpenBills();
                sOpenCapAmt = sOpenCapAmt + Double.parseDouble(bean.getSOpenCapAmt().replaceAll(",", ""));
                sCloseBills = sCloseBills + bean.getSCloseBills();
                sCloseAmount = sCloseAmount + Double.parseDouble(bean.getSCloseAmount().replaceAll(",", ""));
                sStockBills = sStockBills + bean.getSStockBills();
                sStockAmount = sStockAmount + Double.parseDouble(bean.getSStockAmount().replaceAll(",", ""));
                sBillsEarned = sBillsEarned + bean.getSBillsEarned();
                sAmountEarned = sAmountEarned + Double.parseDouble(bean.getSAmountEarned().replaceAll(",", ""));
                sProfit = sProfit + Double.parseDouble(bean.getSProfit().replaceAll(",", ""));
            } else if(selectOrDeSelectOrAll.equals("DESELECTED") && !bean.isBChecked()) {
                iPcs++;
                sOpenBills = sOpenBills + bean.getSOpenBills();
                sOpenCapAmt = sOpenCapAmt + Double.parseDouble(bean.getSOpenCapAmt().replaceAll(",", ""));
                sCloseBills = sCloseBills + bean.getSCloseBills();
                sCloseAmount = sCloseAmount + Double.parseDouble(bean.getSCloseAmount().replaceAll(",", ""));
                sStockBills = sStockBills + bean.getSStockBills();
                sStockAmount = sStockAmount + Double.parseDouble(bean.getSStockAmount().replaceAll(",", ""));
                sBillsEarned = sBillsEarned + bean.getSBillsEarned();
                sAmountEarned = sAmountEarned + Double.parseDouble(bean.getSAmountEarned().replaceAll(",", ""));
                sProfit = sProfit + Double.parseDouble(bean.getSProfit().replaceAll(",", ""));
            } else if(selectOrDeSelectOrAll.equals("ALL")) {
                iPcs++;
                sOpenBills = sOpenBills + bean.getSOpenBills();
                sOpenCapAmt = sOpenCapAmt + Double.parseDouble(bean.getSOpenCapAmt().replaceAll(",", ""));
                sCloseBills = sCloseBills + bean.getSCloseBills();
                sCloseAmount = sCloseAmount + Double.parseDouble(bean.getSCloseAmount().replaceAll(",", ""));
                sStockBills = sStockBills + bean.getSStockBills();
                sStockAmount = sStockAmount + Double.parseDouble(bean.getSStockAmount().replaceAll(",", ""));
                sBillsEarned = sBillsEarned + bean.getSBillsEarned();
                sAmountEarned = sAmountEarned + Double.parseDouble(bean.getSAmountEarned().replaceAll(",", ""));
                sProfit = sProfit + Double.parseDouble(bean.getSProfit().replaceAll(",", ""));
            }
        }
        String sMonths = Integer.toString(iPcs);
        tbTotalCompBillTableView.getItems().removeAll(tbTotalCompBillTableView.getItems());
        tbTotalCompBillTableView.getItems().add(new AllDetailsBean(sMonths, 
                sOpenBills, String.valueOf(sOpenCapAmt), 
                sCloseBills, sCloseAmount, 
                sStockBills, sStockAmount, 
                sBillsEarned, sAmountEarned, 
                sProfit, true));
    }
    
    private void selectOrDeSelectAllRep(TableView<AllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }
    
    @FXML
    private void btCompSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllRep(tbCompanyBillTableView, true);
        setCompanyHeaderValues("SELECTED");        
    }

    @FXML
    private void btCompDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllRep(tbCompanyBillTableView, false);
        setCompanyHeaderValues("SELECTED");        
    }

    @FXML
    private void btCompCalculateSelectedOnAction(ActionEvent event) {
        setCompanyHeaderValues("SELECTED"); 
    }

    @FXML
    private void btCompDeCalculateSelectedOnAction(ActionEvent event) {
        setCompanyHeaderValues("DESELECTED");
    }

    @FXML
    private void btCompCalculateAllSelectedOnAction(ActionEvent event) {
        setCompanyHeaderValues("ALL");
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbCompanyBillTableView.getSelectionModel().getSelectedIndex(); 
        
        if(event.getClickCount() == 1) {
            tbCompanyBillTableView.getItems().get(index).setBChecked(!tbCompanyBillTableView.getItems().get(index).getBCheckedProperty());
            setCompanyHeaderValues("SELECTED");
        }                 
    }
    
}
