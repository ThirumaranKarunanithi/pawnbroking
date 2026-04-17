/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.ledger;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class LedgerController implements Initializable {

    LedgerDBOperation dbOp;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private HBox nodeAddToFilter1;
    @FXML
    private ComboBox<String> cbCompAloneMaterialType;
    @FXML
    private Label lbFrom1;
    @FXML
    private DatePicker dpFrom;
    @FXML
    private Label lbTo1;
    @FXML
    private DatePicker dpTo;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private TextField txtCompNumberOfPcs;
    @FXML
    private TextField txtCompTotalCapitalAmt;
    private TextField txtCompInterestedAmt;
    private TextField txtCompTotalInterestedAmt;
    @FXML
    private TableView<AllDetailsBean> tbCompanyBillDetails;
    @FXML
    private Button btPrint;
    @FXML
    private Button btShowAllRecords1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new LedgerDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LedgerDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String sDate = DateRelatedCalculations.getPreYear();
        String eDate = DateRelatedCalculations.getTodaysDate();
        dpFrom.setValue(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
        dpTo.setValue(LocalDate.parse(eDate, CommonConstants.DATETIMEFORMATTER));        
    }    

    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        try {
            String sMaterialType = cbCompAloneMaterialType.getValue();
            String sFromDate = CommonConstants.DATETIMEFORMATTER.format(dpFrom.getValue());
            String sToDate = CommonConstants.DATETIMEFORMATTER.format(dpTo.getValue());
            
            DataTable allDetailValues = dbOp.getLedgerValues(sMaterialType, sFromDate, sToDate);
            setCompAloneDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCompAloneDetailValuesToField(DataTable allDetailValues) {
        
        tbCompanyBillDetails.getItems().removeAll(tbCompanyBillDetails.getItems());
        try {
            dbOp.connectDB();
        } catch(Exception e) {
            System.out.println("Error in connecting db");
        }
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sOpeningDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sCustomerName = allDetailValues.getRow(i).getColumn(2).toString();
            String sSpouseType = allDetailValues.getRow(i).getColumn(3).toString();
            String sSpouseName = allDetailValues.getRow(i).getColumn(4).toString();
            String sAddress = allDetailValues.getRow(i).getColumn(5).toString();
            String sAmount = allDetailValues.getRow(i).getColumn(6).toString();
            String sItems = allDetailValues.getRow(i).getColumn(7).toString();    
            String sGrWt = allDetailValues.getRow(i).getColumn(8).toString();
            String sClosingDate = "";
            if(allDetailValues.getRow(i).getColumnCount() == 10) {
                sClosingDate = allDetailValues.getRow(i).getColumn(9).toString();
            }
                        
            tbCompanyBillDetails.getItems().add(new AllDetailsBean(sBillNumber, sOpeningDate, sCustomerName,
                    sSpouseType, sSpouseName,
                    sAddress, Double.parseDouble(sAmount), sItems, 
                    String.format("%.3f", Double.parseDouble(sGrWt)), sClosingDate, true));
        }        
        try {
            dbOp.disConnectDB();
            setHeaderValues("ALL");
        } catch(Exception e) {
            System.out.println("Error in connecting db");
        }
    }        
    
    public void setHeaderValues(String selectOrDeSelectOrAll) {

        int iPcs = 0;
        double dAmt = 0;
        double dIntrAmt = 0;
        double dTotIntrAmt = 0;        

        for(AllDetailsBean bean : tbCompanyBillDetails.getItems()) {
            if(selectOrDeSelectOrAll.equals("SELECTED") && bean.isBChecked()) {
                iPcs++;
                dAmt = dAmt + bean.getSAmount();
            } else if(selectOrDeSelectOrAll.equals("DESELECTED") && !bean.isBChecked()) {
                iPcs++;
                dAmt = dAmt + bean.getSAmount();
            } else if(selectOrDeSelectOrAll.equals("ALL")) {
                iPcs++;
                dAmt = dAmt + bean.getSAmount();
            }
        }
        
        txtCompNumberOfPcs.setText(Integer.toString(iPcs));
        txtCompTotalCapitalAmt.setText(String.format("%.0f", dAmt));
        txtCompInterestedAmt.setText(String.format("%.0f", dIntrAmt));
        txtCompTotalInterestedAmt.setText(String.format("%.0f", dTotIntrAmt));
    }
    
    @FXML
    private void btCompSelectAllOnAction(ActionEvent event) {
    }

    @FXML
    private void btCompDeSelectAllOnAction(ActionEvent event) {
    }

    @FXML
    private void btCompCalculateSelectedOnAction(ActionEvent event) {
    }

    @FXML
    private void btCompDeCalculateSelectedOnAction(ActionEvent event) {
    }

    @FXML
    private void btCompCalculateAllSelectedOnAction(ActionEvent event) {
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
    }

    @FXML
    private void btPrintClicked(ActionEvent event) {
        
        if(txtCompNumberOfPcs.getText() != null && !txtCompNumberOfPcs.getText().equals("0")) {
        
            try {
                String sFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\ledger.jasper";                        
                
                List<LedgerPrint> ParamList = new ArrayList<>();	            
                for(AllDetailsBean bean : tbCompanyBillDetails.getItems()) {                
                    if(bean.isBChecked()) {
                        LedgerPrint nBean = new LedgerPrint();
                        nBean.setBillNumber(bean.getSBillNumber());
                        nBean.setOpeningDate(bean.getSOpeningDate());
                        nBean.setCustomerName(bean.getSName() + " " + bean.getSSpouseType() + " " + bean.getSSpouseName());
                        nBean.setAddress(bean.getSAdress());
                        nBean.setAmount(bean.getSAmount());
                        nBean.setItems(bean.getSItems());
                        nBean.setGrWt(bean.getSGrossWeight());
                        nBean.setClosingDate(bean.getSClosingDate());                        
                        ParamList.add(nBean);
                    }
                }
                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("BillCalcCollectionBeanParam", tableList);
                parameters.put("fromdate", CommonConstants.DATETIMEFORMATTER.format(dpFrom.getValue()));
                parameters.put("todate", CommonConstants.DATETIMEFORMATTER.format(dpTo.getValue()));

                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();
                noticeUtil.generateNoticeOperation(sFileName, parameters);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert("Not any details filled to print the records. ");
        }
        
    }

    @FXML
    private void showInStockRecordsClicked(ActionEvent event) {
        try {
            String sMaterialType = cbCompAloneMaterialType.getValue();
            String sFromDate = CommonConstants.DATETIMEFORMATTER.format(dpFrom.getValue());
            String sToDate = CommonConstants.DATETIMEFORMATTER.format(dpTo.getValue());
            
            DataTable allDetailValues = dbOp.getLedgerStockValues(sMaterialType, sFromDate, sToDate);
            setCompAloneDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(LedgerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
