/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import com.magizhchi.pawnbroking.noticegeneration.NoticeGeneration;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.ComboBox;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * FXML Controller class
 *
 * @author tiruk
 */
public class Report8020Controller implements Initializable {

    public ReportDBOperation dbOp;
    double totalAmount = 0;
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
    private TableView<AllDetailsBean8020> tbNotice;
    @FXML
    private Button btPrintReport;
    @FXML
    private TextField txtTotAmount;
    @FXML
    private Button btShowReport;
    @FXML
    private TableView<AllDetailsBean8020> tbSummary;
    @FXML
    private ComboBox<String> cbMaterialType;
    @FXML
    TextField txtCompanyIds;

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
        
        String sDate = DateRelatedCalculations.getNextDateWithFormatted(DateRelatedCalculations.getPreYear());
        String eDate = DateRelatedCalculations.getPreviousDateWithFormatted(DateRelatedCalculations.getOneYear(sDate));
        dpGenerateFrom.setValue(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
        dpGenerateTo.setValue(LocalDate.parse(eDate, CommonConstants.DATETIMEFORMATTER));
        
    }    

    private void setCompanyMISValues() {   
        
        try {
            tbNotice.getItems().removeAll(tbNotice.getItems());
            tbNotice.getItems().removeAll(tbNotice.getItems());            
            
            DataTable companyMISValues = dbOp.get8020ReportDetailValues(txtCompanyIds.getText().trim(),
                    getMaterialTypeValue(),
                    CommonConstants.DATETIMEFORMATTER.format(dpGenerateFrom.getValue()), 
                    CommonConstants.DATETIMEFORMATTER.format(dpGenerateTo.getValue()));
            
            for(int i=0; i<companyMISValues.getRowCount(); i++) {
                String rownumber = companyMISValues.getRow(i).getColumn(0).toString();
                String sCustomerName = companyMISValues.getRow(i).getColumn(1).toString();
                String sSpouseType = companyMISValues.getRow(i).getColumn(2).toString();
                String sSpouseName = companyMISValues.getRow(i).getColumn(3).toString();
                String sDoorNumber = companyMISValues.getRow(i).getColumn(4).toString();
                String sStreet = companyMISValues.getRow(i).getColumn(5).toString();
                String sArea = companyMISValues.getRow(i).getColumn(6).toString();
                String sCity = companyMISValues.getRow(i).getColumn(7).toString();
                String sMobileNumber = companyMISValues.getRow(i).getColumn(8).toString();
                double dAmount = Double.parseDouble(companyMISValues.getRow(i).getColumn(9).toString());
                double dTotAmount = Double.parseDouble(companyMISValues.getRow(i).getColumn(10).toString());
                float fPercent = Float.parseFloat(companyMISValues.getRow(i).getColumn(11).toString());
                float fRunningTotal = Float.parseFloat(companyMISValues.getRow(i).getColumn(12).toString());
                int iClass = Integer.parseInt(companyMISValues.getRow(i).getColumn(13).toString());

                String sCustomerDetails = sCustomerName + " " 
                        + sSpouseType + " " + sSpouseName +",\n"
                        + sDoorNumber + ", " + sStreet + ", "
                        + sArea + ","
                        + sCity + ".\n"
                        + "Mobile: " + sMobileNumber + ".";
                
                if(i == 0) {
                    totalAmount = dTotAmount;
                }
                tbNotice.getItems().add(new AllDetailsBean8020(rownumber, sCustomerDetails, sMobileNumber, 
                        dAmount, fPercent, fRunningTotal, iClass));        
            }
        } catch (SQLException ex) {
            Logger.getLogger(MISReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getMaterialTypeValue() {
        String materialType = cbMaterialType.getValue();    
        switch(materialType) {            
            case "GOLD": return "'GOLD'";
            case "SILVER": return "'SILVER'";
            case "BOTH": return "'GOLD', 'SILVER'";
            default: return "'GOLD', 'SILVER'";
        }
    }
    
    private void setSummaryValues() {   
        
        try {
            tbSummary.getItems().removeAll(tbSummary.getItems());
            tbSummary.getItems().removeAll(tbSummary.getItems());
            
            DataTable companyMISValues = dbOp.get8020ReportSummaryValues(txtCompanyIds.getText().trim(),
                    getMaterialTypeValue(),                    
                    CommonConstants.DATETIMEFORMATTER.format(dpGenerateFrom.getValue()), 
                    CommonConstants.DATETIMEFORMATTER.format(dpGenerateTo.getValue()));
            
            for(int i=0; i<companyMISValues.getRowCount(); i++) {
                int iClass = Integer.parseInt(companyMISValues.getRow(i).getColumn(0).toString());
                String sNumberOfCustomers = companyMISValues.getRow(i).getColumn(1).toString();
                double dTotalAmount = Double.parseDouble(companyMISValues.getRow(i).getColumn(2).toString());
                tbSummary.getItems().add(new AllDetailsBean8020(null, sNumberOfCustomers, null, 
                        dTotalAmount, 0, 0, iClass));        
            }
        } catch (SQLException ex) {
            Logger.getLogger(MISReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void dpGenerateToOnChanged(ActionEvent event) {
    }


    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void btPrintReportClicked(ActionEvent event) {
        try {
            
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\8020report.jasper";                        
            
            List<Report8020DetailsPrintBean> ParamList = new ArrayList<>();	            
            for(AllDetailsBean8020 bean : tbNotice.getItems()) {                
                Report8020DetailsPrintBean nBean = new Report8020DetailsPrintBean();
                nBean.setSSlNo(bean.getSSlNo());
                nBean.setSCustomerDetails(bean.getSCustomerDetails());
                nBean.setDAmount(bean.getSAmount());
                nBean.setFPercent(bean.getSPercent());
                nBean.setIClass(bean.getSClass());
                ParamList.add(nBean);
            }                                
            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

            List<Report8020SummaryPrintBean> summaryParamList = new ArrayList<>();	            
            for(AllDetailsBean8020 bean : tbSummary.getItems()) {                
                Report8020SummaryPrintBean nBean = new Report8020SummaryPrintBean();
                nBean.setIClass(bean.getSClass());
                nBean.setSNumOfCust(bean.getSCustomerDetails());
                nBean.setDAmount(bean.getSAmount());
                summaryParamList.add(nBean);
            }
            JRBeanCollectionDataSource sumList = new JRBeanCollectionDataSource(summaryParamList);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            parameters.put("SummaryBeanParam", sumList);
            String sTitle = " From " + CommonConstants.DATETIMEFORMATTER.format(dpGenerateFrom.getValue()) 
                    + " To " + CommonConstants.DATETIMEFORMATTER.format(dpGenerateTo.getValue()) 
                    + " 80-20 Report.";
            parameters.put("TITLE", sTitle);
            parameters.put("DATE", DateRelatedCalculations.getTodaysDate());
            parameters.put("TOTAL_AMOUNT", txtTotAmount.getText());
            
            JasperPrint print = null;
            NoticeUtil noticeUtil = new NoticeUtil();
            noticeUtil.generateNoticeOperation(sFileName, parameters);

        } catch (JRException ex) {
            PopupUtil.showErrorAlert(ex.getMessage());
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }       
                
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    @FXML
    private void btShowReportClicked(ActionEvent event) {
        if(!txtCompanyIds.getText().isEmpty()) {
            setCompanyMISValues();            
            txtTotAmount.setText(Util.format(totalAmount));
            setSummaryValues();
        }
    }

    @FXML
    private void cbMaterialTypeOnAction(ActionEvent event) {
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
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
            gon.setInitValus(dbOp.getCompaniesList());
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

    
}
