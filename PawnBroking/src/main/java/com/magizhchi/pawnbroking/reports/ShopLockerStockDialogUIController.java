/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.account.TodaysAccountController;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.CommonDBOperation;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.mainscreen.OwnerMainScreenController;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * FXML Controller class
 *
 * @author tiruk
 */
public class ShopLockerStockDialogUIController implements Initializable {

    private CommonDBOperation dbOp;    
    OwnerMainScreenController parent;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private DatePicker dtFromDate;
    @FXML
    private DatePicker dtToDate;
    @FXML
    private ComboBox<String> cbMaterialType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new CommonDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ShopLockerStockDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }    

    @FXML
    private void onSaveClicked(ActionEvent event) {
        
        if(dtFromDate.getValue() != null && dtToDate.getValue() != null) {
        
            try {
                String sFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\lockerStock.jasper";                        
                
                String sMaterialType = cbMaterialType.getValue();
                String sFromDate = CommonConstants.DATETIMEFORMATTER.format(dtFromDate.getValue());
                String sToDate = CommonConstants.DATETIMEFORMATTER.format(dtToDate.getValue());
                
                List<ShopLockerPrintBean> ParamList = dbOp.shopLockerStock(sMaterialType, sFromDate, sToDate);

                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);
                
                String sTotalBills = String.valueOf(ParamList.size());
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("BillCalcCollectionBeanParam", tableList);
                parameters.put("MATERIALTYPE", "Shop Locker Stock for " + sMaterialType);
                parameters.put("FROMDATE", sFromDate);
                parameters.put("TODATE", sToDate);
                parameters.put("TOTALBILLS", sTotalBills);

                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();
                noticeUtil.generateNoticeOperation(sFileName, parameters);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert("Not any details filled to print the records. ");
        }                    
    }
    
    public void setParent(OwnerMainScreenController parent)
    {
        this.parent = parent;
    }
    
}
