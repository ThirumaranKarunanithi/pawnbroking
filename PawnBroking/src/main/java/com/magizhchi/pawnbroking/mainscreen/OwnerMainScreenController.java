/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.mainscreen;

import com.magizhchi.pawnbroking.account.TodaysAccountController;
import com.magizhchi.pawnbroking.billcalculator.BillCalculatorController;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.CommonDBOperation;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.ScreenUtil;
import com.magizhchi.pawnbroking.companyadvanceamount.GoldAdvanceAmountController;
import com.magizhchi.pawnbroking.companyadvanceamount.SilverAdvanceAmountController;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import com.magizhchi.pawnbroking.companybillopening.BillOpeningBean;
import com.magizhchi.pawnbroking.companybillopening.EMIGoldBillOpeningController;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.companybillopening.SilverBillOpeningController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import com.magizhchi.pawnbroking.reports.BillOpeningFilingStickerDialogUIController;
import com.magizhchi.pawnbroking.reports.LockerStickerDialogUIController;
import com.magizhchi.pawnbroking.reports.RepledgeBillPrintBean;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class OwnerMainScreenController implements Initializable {

    String companyMasterScreen = "/com/magizhchi/pawnbroking/companymaster/CompanyMaster.fxml";
    String jewelItemMasterScreen = "/com/magizhchi/pawnbroking/itemmaster/ItemMaster.fxml";
    String repledgeMasterScreen = "/com/magizhchi/pawnbroking/repledgemaster/RepledgeMaster.fxml";
    String employeeMasterScreen = "/com/magizhchi/pawnbroking/employeemaster/EmployeeMaster.fxml";
    String roleMasterScreen = "/com/magizhchi/pawnbroking/rolemaster/RoleMaster.fxml"; 
    String userMasterScreen = "/com/magizhchi/pawnbroking/usermaster/UserMaster.fxml"; 
    String billEditOperationScreen = "/com/magizhchi/pawnbroking/billeditoperation/BillEditOperation.fxml";
    String repledgeBillEditOperationScreen = "/com/magizhchi/pawnbroking/repledgebilleditoperation/RepledgeBillEditOperation.fxml";
    
    String goldBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/GoldBillOpening.fxml";  
    String silverBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/SilverBillOpening.fxml";  
    String emiGoldBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/EMIGoldBillOpening.fxml";  
    String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";  
    String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";  
    String goldAdvanceAmountScreen = "/com/magizhchi/pawnbroking/companyadvanceamount/GoldAdvanceAmount.fxml";  
    String silverAdvanceAmountScreen = "/com/magizhchi/pawnbroking/companyadvanceamount/SilverAdvanceAmount.fxml";      
    String repledgeGoldBillOpeningScreen = "/com/magizhchi/pawnbroking/repledgebillopening/RepledgeGoldBillOpening.fxml"; 
    String repledgeGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";     
    String debitPanelScreen = "/com/magizhchi/pawnbroking/debit/DebitPanel.fxml"; 
    String creditPanelScreen = "/com/magizhchi/pawnbroking/credit/CreditPanel.fxml";    
    String todaysAccountScreen = "/com/magizhchi/pawnbroking/account/TodaysAccount.fxml";
    String billcalculatorScreen = "/com/magizhchi/pawnbroking/billcalculator/BillCalculator.fxml";
    
    String stockDetailsScreen = "/com/magizhchi/pawnbroking/stockdetails/StockDetails.fxml";
    String goldRebillMapperScreen = "/com/magizhchi/pawnbroking/rebillmapper/GoldRebillMapper.fxml";
    String silverRebillMapperScreen = "/com/magizhchi/pawnbroking/rebillmapper/SilverRebillMapper.fxml";
    String customerDetailsScreen = "/com/magizhchi/pawnbroking/customerdetails/CustomerDetails.fxml";
    String rebilledAfterRepledged = "/com/magizhchi/pawnbroking/rebillmapper/ReBilledAfterRepledge.fxml";
    String noticeGeneration = "/com/magizhchi/pawnbroking/noticegeneration/NoticeGeneration.fxml";
    String ledgerScreen = "/com/magizhchi/pawnbroking/ledger/Ledger.fxml";
    
    String msiReportScreen = "/com/magizhchi/pawnbroking/reports/MISReports.fxml";
    String billStickerScreen = "/com/magizhchi/pawnbroking/reports/BillOpeningFIlingStickersDialog.fxml";  
    String lockerStickerScreen = "/com/magizhchi/pawnbroking/reports/LockerStickersDialog.fxml";  
    String report8020Screen = "/com/magizhchi/pawnbroking/reports/Report8020.fxml";
    String trialBalanceScreen = "/com/magizhchi/pawnbroking/reports/TrialBalance.fxml";
    String shopLockerStockDialog = "/com/magizhchi/pawnbroking/reports/ShopLockerStockDialog.fxml";
    String shopBillStockDialog = "/com/magizhchi/pawnbroking/reports/ShopBillStockDialog.fxml";
    
    final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    
    private DataTable companyNames = null;
    private CommonDBOperation dbOp;    
    public Stage dialog;
    
    private Stage loginScreenStage;
    private Stage mainScreenStage;
    
    
    @FXML
    private HBox nodeToShowPanel;
    @FXML
    private VBox operationScreenPanel;
    @FXML
    private VBox settingsScreenPanel;
    @FXML
    private ImageView ivSettings;
    @FXML
    private ImageView ivOperation;    
    @FXML
    private Label lbMsg;
    @FXML
    private VBox specialOptionsScreenPanel;
    @FXML
    private ImageView ivSpecialOptions;
    @FXML
    private ComboBox<String> cbActiveCompany;
    @FXML
    private VBox reportScreenPanel;
    @FXML
    private ImageView ivReports;
    @FXML
    private ImageView ivSale;
    @FXML
    private VBox saleScreenPanel;
    @FXML
    private HBox bottomScreensTabHBox;
    @FXML
    private Button btCompileAllReports;
    @FXML
    private ImageView ivHeading;
    @FXML
    private TableView<AllDetailsBean> tbCompanyBillReminder;
    @FXML
    private HBox hbReports;
    @FXML
    private TableView<RepAllDetailsBean> tbRepledgeBillReminder;
    @FXML
    public TextField txtBillNumber;
    @FXML
    private ImageView ivUserImg;
    @FXML
    private ImageView ivSearch;
    @FXML
    private ImageView ivSound;
    @FXML
    private HBox hbReports1;
    @FXML
    private Button btImgMigration;
    @FXML
    private VBox EMIScreenPanel;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView ivLogo;
    @FXML
    private HBox hbchecker;
    @FXML
    private VBox chartPanel;
    @FXML
    private HBox mainPanel;
    @FXML
    private VBox chartPanel1;
    @FXML
    private ImageView magizhchiAddPanel;
    @FXML
    private VBox lcNumberOfBillsContainer;
    @FXML
    private VBox lcTotalAmountContainer;
    

    private LineChart<String, Number> lcNumberOfBills;
    private LineChart<String, Number> lcTotalAmount;
    
        public void shortCutKeysCode() {
        try {
                anchorPane.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
                                {
                                    Robot  eventRobot = new Robot();

                                    @Override
                                    public void handle( KeyEvent KV )
                                    {
                                        switch ( KV.getCode() )
                                        {
                                            case F :
                                            {
                                                if(KV.isControlDown()) {
                                                    billNumberFocus();
                                                }
                                                break;
                                            }                                                                                        
                                            default:
                                                break;
                                        }
                                    }
                                });
        } catch (AWTException e) {			
                e.printStackTrace();
        }    
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        shortCutKeysCode();
        
        try {
            dbOp = new CommonDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
        //String imageUrl = "file:///C:/Users/MyUser/image.jpg";
        String sHeadingImage = CommonConstants.REPORT_LOCATION 
                + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                + "\\images\\text.gif";

        String sTiruImage = "/com/magizhchi/pawnbroking/employeemaster/tiru_user.png";
        
        Platform.runLater(() -> {
        try {
            FileInputStream fis = new FileInputStream(sHeadingImage);
            ivHeading.setImage(new Image(fis));
            fis.close();

            if(CommonConstants.USERID.equals("TIRU")) {
                ivUserImg.setImage(new Image(getClass().getResourceAsStream(sTiruImage)));
            }
                
            if(CommonConstants.IS_LOGIN_USER_IMAGE_AVAILABLE) {
                File custTemp = new File(new File(CommonConstants.TEMP_FILE_LOCATION), 
                    CommonConstants.LOGIN_USER_IMAGE_NAME);            
                FileInputStream customerFIS = new FileInputStream(custTemp.getAbsolutePath());
                ivUserImg.setImage(new Image(customerFIS));
                customerFIS.close();
            }
            
            
            try {
                String soundOnLocation = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\images\\sound_on.png";           
                FileInputStream soundOnFis = new FileInputStream(soundOnLocation);
                ivSound.setImage(new Image(soundOnFis));        
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {        
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        });
        
        doSaleScreensTabOptionWork();
        //setCompanyNameValues();
        nodeToShowPanel.getChildren().remove(settingsScreenPanel);
        nodeToShowPanel.getChildren().remove(specialOptionsScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(saleScreenPanel);
        nodeToShowPanel.getChildren().remove(EMIScreenPanel);
        
        if(!CommonConstants.EMPID.equals(CommonConstants.TIRU)) {
            hbchecker.getChildren().remove(btCompileAllReports);
            hbchecker.getChildren().remove(btImgMigration);
        }
        
        try {
            cbActiveCompany.getItems().removeAll(cbActiveCompany.getItems());
            companyNames = dbOp.getAllCompanyNames();
            for(int i=0; i<companyNames.getRowCount(); i++) {          
                cbActiveCompany.getItems().add(companyNames.getRow(i).getColumn(0).toString());
                if(companyNames.getRow(i).getColumn(1).toString().equals(CommonConstants.ACTIVE)) {
                    cbActiveCompany.setValue(companyNames.getRow(i).getColumn(0).toString());
                }
            }     
            getCompanyBillReminderVals();
            getRepledgeBillReminderVals();                
        } catch (SQLException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }                

        billNumberFocus();
        btLogoClicked(null);
        
        // Build charts programmatically (FXML loader cannot instantiate LineChart)
        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis   yAxis1 = new NumberAxis();
        lcNumberOfBills = new LineChart<>(xAxis1, yAxis1);
        lcNumberOfBills.setTitle("Total Number Of Bill's");
        lcNumberOfBills.setPrefHeight(200);
        lcNumberOfBillsContainer.getChildren().add(lcNumberOfBills);

        CategoryAxis xAxis2 = new CategoryAxis();
        NumberAxis   yAxis2 = new NumberAxis();
        lcTotalAmount = new LineChart<>(xAxis2, yAxis2);
        lcTotalAmount.setTitle("Total Bill Amount");
        lcTotalAmount.setPrefHeight(200);
        lcTotalAmountContainer.getChildren().add(lcTotalAmount);
        /*VoiceUtil.textToSpeech("Good Morning " + CommonConstants.EMP_NAME 
                + ", Welcome to the Pawnbroking software.");
        */
        /*ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            VoiceUtil.textToSpeech("Hi " + CommonConstants.EMP_NAME 
                    + ", Welcome to the pawnbroking software.");
            //speechToText(OwnerMainScreenController.this);
        });*/   
    }    

    public void billNumberFocus() {
        Platform.runLater(() -> {
            txtBillNumber.setText("");
            txtBillNumber.requestFocus();
        });             
    }
    
    public void lineChartNumberOfBillsWork() {
        
        try {
           
            
            lcNumberOfBills.getData().removeAll(lcNumberOfBills.getData());
            lcTotalAmount.getData().removeAll(lcTotalAmount.getData());
            /*lcNumberOfBills.getData().removeAll(lcNumberOfBills.getData());
            lcTotalAmount.getData().removeAll(lcTotalAmount.getData());
            lcNumberOfBills.getData().removeAll(lcNumberOfBills.getData());
            lcTotalAmount.getData().removeAll(lcTotalAmount.getData());
            */
            DataTable companyMISValues = dbOp.getCompMISValues();
            List sMnthList = (ArrayList) companyMISValues.getRow(companyMISValues.getRowCount()-1).getColumn(0);
            int maxLoop = companyMISValues.getRowCount()-2;            
            
            XYChart.Series noOfBillsG = new XYChart.Series<>();
            noOfBillsG.setName("Gold");
            XYChart.Series totBillAmtG = new XYChart.Series<>();
            totBillAmtG.setName("Gold");
            XYChart.Series noOfBillsS = new XYChart.Series<>();
            noOfBillsS.setName("Silver");
            XYChart.Series totBillAmtS = new XYChart.Series<>();
            totBillAmtS.setName("Silver");
            XYChart.Series noOfBillsTot = new XYChart.Series<>();
            noOfBillsTot.setName("Total");
            XYChart.Series totBillAmtTot = new XYChart.Series<>();
            totBillAmtTot.setName("Total");
            int totalBills = 0;
            int totalBillAmt = 0;
            boolean startLoop = false;
            for(int i=0; i<=maxLoop; i++) {                
                String sMonths = companyMISValues.getRow(i).getColumn(0).toString();                
                if(sMnthList.size() >= 7 && sMnthList.get(sMnthList.size()-7).equals(sMonths)) {
                    startLoop = true;
                }
                if(startLoop) {
                    String sMaterial = companyMISValues.getRow(i).getColumn(1).toString();
                    int sOpenBills = Integer.parseInt(companyMISValues.getRow(i).getColumn(2).toString());
                    int sOpenCapAmt = (int) Double.parseDouble(companyMISValues.getRow(i).getColumn(3).toString());
                    if(sMaterial.equals("GOLD")) {
                        noOfBillsG.getData().add(new XYChart.Data<>(sMonths, sOpenBills));                        
                        totBillAmtG.getData().add(new XYChart.Data<>(sMonths, sOpenCapAmt));                        
                        totalBills = sOpenBills;
                        totalBillAmt = sOpenCapAmt;
                    } else if(sMaterial.equals("SILVER")) {
                        noOfBillsS.getData().add(new XYChart.Data<>(sMonths, sOpenBills));
                        totBillAmtS.getData().add(new XYChart.Data<>(sMonths, sOpenCapAmt));
                        totalBills += sOpenBills;
                        totalBillAmt += sOpenCapAmt;      
                        noOfBillsTot.getData().add(new XYChart.Data<>(sMonths, totalBills));
                        totBillAmtTot.getData().add(new XYChart.Data<>(sMonths, totalBillAmt));
                    }  
                }
            }
            
            lcNumberOfBills.getData().addAll(noOfBillsG);
            lcNumberOfBills.getData().addAll(noOfBillsS);            
            lcNumberOfBills.getData().addAll(noOfBillsTot);
            
            lcTotalAmount.getData().addAll(totBillAmtG);                 
            lcTotalAmount.getData().addAll(totBillAmtS);            
            lcTotalAmount.getData().addAll(totBillAmtTot);
                       
        } catch (SQLException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getCompanyBillReminderVals() {
    
        try {
            tbCompanyBillReminder.getItems().removeAll(tbCompanyBillReminder.getItems());
            DataTable companyBillReminderValues = 
                    dbOp.getCompanyBillReminderVals(DateRelatedCalculations.getTodaysDate());            
            for(int i=0; i<companyBillReminderValues.getRowCount(); i++) {            
                String sBillNumber = companyBillReminderValues.getRow(i).getColumn(0).toString();
                String sDate = companyBillReminderValues.getRow(i).getColumn(1).toString();
                String sName = companyBillReminderValues.getRow(i).getColumn(2).toString();
                String sRatePerGm = companyBillReminderValues.getRow(i).getColumn(3).toString();
                String sAmount = companyBillReminderValues.getRow(i).getColumn(4).toString();
                String sAccClosingDate = companyBillReminderValues.getRow(i).getColumn(5).toString();
                String sMobileNumber = companyBillReminderValues.getRow(i).getColumn(6).toString();
                String sMaterialType = companyBillReminderValues.getRow(i).getColumn(7).toString();
                
                tbCompanyBillReminder.getItems().add(new AllDetailsBean(null, sBillNumber, sName, "", "", 0, 
                        sRatePerGm, sDate, 
                        Double.parseDouble(sAmount), "", sAccClosingDate, "", "", "", "", "", sMaterialType, 
                        sMobileNumber, false, null));
            }        
            
        } catch (SQLException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getRepledgeBillReminderVals() {
    
        try {
            tbRepledgeBillReminder.getItems().removeAll(tbRepledgeBillReminder.getItems());
            DataTable allDetailValues = dbOp.getRepAloneAllDetailsValues("GOLD", DateRelatedCalculations.getTodaysDate());
            for(int i=0; i<allDetailValues.getRowCount(); i++) {            
                String sRepledgeBillId = allDetailValues.getRow(i).getColumn(0).toString();;
                String sRepledgeId = allDetailValues.getRow(i).getColumn(1).toString();
                String sRepledgeBillNumber = allDetailValues.getRow(i).getColumn(8).toString();
                String sRepledgeName = allDetailValues.getRow(i).getColumn(2).toString();
                String sCompBillNumber = allDetailValues.getRow(i).getColumn(3).toString();
                String sRepAmount = allDetailValues.getRow(i).getColumn(5).toString();
                String sRepInterest = allDetailValues.getRow(i).getColumn(6).toString();
                String sRepDocumentCharge = allDetailValues.getRow(i).getColumn(7).toString();
                String sRepOpeningDate = allDetailValues.getRow(i).getColumn(4).toString();
                String sRepClosingDate = allDetailValues.getRow(i).getColumn(9).toString();
                String sInterestedAmount = "0";
                String sTotalInterestedAmount = sRepAmount;
                tbRepledgeBillReminder.getItems().add(new RepAllDetailsBean(sRepledgeBillId, sRepledgeId, 
                        sRepledgeBillNumber,
                        sRepledgeName, sCompBillNumber, Double.parseDouble(sRepAmount), sRepInterest, sRepOpeningDate, 
                        sRepClosingDate, sInterestedAmount, sTotalInterestedAmount, 
                        sRepDocumentCharge, "GOLD", false, null));
            }                    
        } catch (SQLException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doSaleScreensTabOptionWork() {
        if(CommonConstants.ROLEID.equals(CommonConstants.TIRU)
                && CommonConstants.ACTIVE_MACHINE.isIsPaidForJewelSaleSoftware()) {
        } else {
            bottomScreensTabHBox.getChildren().remove(ivSale);
        }
    }
    
    public void setCompanyNameValues() {
        Platform.runLater(()->{
            try {
                cbActiveCompany.getItems().removeAll(cbActiveCompany.getItems());
                companyNames = dbOp.getAllCompanyNames();
                for(int i=0; i<companyNames.getRowCount(); i++) {          
                    cbActiveCompany.getItems().add(companyNames.getRow(i).getColumn(0).toString());
                    if(companyNames.getRow(i).getColumn(1).toString().equals(CommonConstants.ACTIVE)) {
                        cbActiveCompany.setValue(companyNames.getRow(i).getColumn(0).toString());
                    }
                }     
                lineChartNumberOfBillsWork();
                getCompanyBillReminderVals();
                getRepledgeBillReminderVals();                
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }
    
    public void setParent(Stage loginScreenStage, Stage mainScreenStage) {
        
        this.loginScreenStage = loginScreenStage;
        this.mainScreenStage = mainScreenStage;
        this.lbMsg.setText("Welcome " + CommonConstants.EMP_NAME +".");
        this.loginScreenStage.close();  
        this.mainScreenStage.setOnCloseRequest((WindowEvent e) -> {
            /*VoiceUtil.textToSpeech("mIKKA NANDRI " + CommonConstants.EMP_NAME );
            try {
                Thread.sleep(500);                
                //System.exit(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            } */           
        });
        
    }
    
    private void btEMIIconClicked(MouseEvent event) {
        
        nodeToShowPanel.getChildren().remove(settingsScreenPanel);        
        nodeToShowPanel.getChildren().remove(specialOptionsScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(saleScreenPanel);
        nodeToShowPanel.getChildren().remove(operationScreenPanel);
        if(!nodeToShowPanel.getChildren().contains(operationScreenPanel))
                    nodeToShowPanel.getChildren().add(0, EMIScreenPanel);    
        
        //ivEMI.setId("shiny-orange");
        ivOperation.setId("record-sales");
        ivSettings.setId("record-sales");               
        ivSpecialOptions.setId("record-sales");               
        ivReports.setId("record-sales");
        ivSale.setId("record-sales");                
    }
    
    @FXML
    private void btOperationIconClicked(MouseEvent event) {
        
        nodeToShowPanel.getChildren().remove(settingsScreenPanel);        
        nodeToShowPanel.getChildren().remove(specialOptionsScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(saleScreenPanel);
        nodeToShowPanel.getChildren().remove(EMIScreenPanel);
        if(!nodeToShowPanel.getChildren().contains(operationScreenPanel))
                    nodeToShowPanel.getChildren().add(0, operationScreenPanel);    
        
        ivOperation.setId("shiny-orange");
        ivSettings.setId("record-sales");               
        ivSpecialOptions.setId("record-sales");               
        ivReports.setId("record-sales");
        ivSale.setId("record-sales");   
        //ivEMI.setId("record-sales");
    }
    
    @FXML
    private void btSettingsIconClicked(MouseEvent event) {
        
        nodeToShowPanel.getChildren().remove(operationScreenPanel);
        nodeToShowPanel.getChildren().remove(specialOptionsScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(saleScreenPanel);
        nodeToShowPanel.getChildren().remove(EMIScreenPanel);
        if(!nodeToShowPanel.getChildren().contains(settingsScreenPanel))
                    nodeToShowPanel.getChildren().add(0, settingsScreenPanel);
        
        ivSettings.setId("shiny-orange");
        ivOperation.setId("record-sales");
        ivSpecialOptions.setId("record-sales");           
        ivReports.setId("record-sales"); 
        ivSale.setId("record-sales");
        //ivEMI.setId("record-sales");
    }

    @FXML
    private void btSpecialOptionsIconClicked(MouseEvent event) {
        
        nodeToShowPanel.getChildren().remove(operationScreenPanel);
        nodeToShowPanel.getChildren().remove(settingsScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(saleScreenPanel);
        nodeToShowPanel.getChildren().remove(EMIScreenPanel);
        if(!nodeToShowPanel.getChildren().contains(specialOptionsScreenPanel))
                    nodeToShowPanel.getChildren().add(0, specialOptionsScreenPanel);
        
        ivSpecialOptions.setId("shiny-orange");
        ivSettings.setId("record-sales");
        ivOperation.setId("record-sales");
        ivReports.setId("record-sales"); 
        ivSale.setId("record-sales");
        //ivEMI.setId("record-sales");
    }
    
    @FXML
    private void btReportIconClicked(MouseEvent event) {
        
        nodeToShowPanel.getChildren().remove(operationScreenPanel);
        nodeToShowPanel.getChildren().remove(settingsScreenPanel);
        nodeToShowPanel.getChildren().remove(specialOptionsScreenPanel);
        nodeToShowPanel.getChildren().remove(saleScreenPanel);
        nodeToShowPanel.getChildren().remove(EMIScreenPanel);
        if(!nodeToShowPanel.getChildren().contains(reportScreenPanel))
                    nodeToShowPanel.getChildren().add(0, reportScreenPanel);
        
        ivReports.setId("shiny-orange");
        ivSettings.setId("record-sales");
        ivOperation.setId("record-sales");
        ivSpecialOptions.setId("record-sales"); 
        ivSale.setId("record-sales");
        //ivEMI.setId("record-sales");
    }
    
    @FXML
    private void btSaleIconClicked(MouseEvent event) {

        nodeToShowPanel.getChildren().remove(operationScreenPanel);
        nodeToShowPanel.getChildren().remove(settingsScreenPanel);
        nodeToShowPanel.getChildren().remove(specialOptionsScreenPanel);
        nodeToShowPanel.getChildren().remove(reportScreenPanel);
        nodeToShowPanel.getChildren().remove(EMIScreenPanel);
        if(!nodeToShowPanel.getChildren().contains(saleScreenPanel))
                    nodeToShowPanel.getChildren().add(0, saleScreenPanel);
        
        ivSale.setId("shiny-orange");
        ivSettings.setId("record-sales");
        ivOperation.setId("record-sales");
        ivSpecialOptions.setId("record-sales"); 
        ivReports.setId("record-sales"); 
        //ivEMI.setId("record-sales");
    }

    @FXML
    private void btGoldBillOpeningOnAction(ActionEvent event) {
        goldBillOpeningScreenWork(event, null, false);        
    }

    public void goldBillOpeningScreenWork(ActionEvent event, String billNumber, boolean onlyForView) {
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_OPENING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillOpeningScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            GoldBillOpeningController gon = (GoldBillOpeningController) loader.getController();
            gon.setParent(dialog);
            if(billNumber != null) {
                gon.viewBill(billNumber, onlyForView);
            }
            
            dialog.setTitle("Gold Bill Opening - " 
                    + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);
            if(CommonConstants.ACTIVE_MACHINE.getLanguage().equals(CommonConstants.TAMIL)) {
                scene.getStylesheets().add("/com/magizhchi/pawnbroking/companybillopening/billopeningTamil.css");
            }
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            dialog.showAndWait();
            
            billNumberFocus();
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }
    
    @FXML
    private void btGoldBillClosingOnAction(ActionEvent event) {
        goldBilClosingScreen(event, null, null, false);
    }

    private void goldBilClosingScreen(ActionEvent event, String billNumber, 
            String copyName, boolean onlyForView) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_CLOSING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
            if(billNumber != null) {
                if(copyName != null && copyName.equals("PACK")) {
                    gon.viewClosedBill(event, billNumber, CommonConstants.DELIVERED);
                } else {
                    gon.closeBill(billNumber, onlyForView);
                }
            }
            dialog.setTitle("Gold Bill Closing - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
            
            getCompanyBillReminderVals();      
            billNumberFocus();
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }
    @FXML
    private void btGoldAdvanceAmountOnAction(ActionEvent event) {
        goldBilAdvanceAmtScreen(event, null, null, false);
    }
    
    private void goldBilAdvanceAmtScreen(ActionEvent event, String billNumber, String copyName, boolean onlyForView) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_ADVANCE_AMOUNT_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldAdvanceAmountScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldAdvanceAmountController gon = (GoldAdvanceAmountController) loader.getController();
            if(billNumber != null) {
                gon.viewBill(billNumber, onlyForView);
            }
            
            dialog.setTitle("Gold Advance Amount Receipt - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();     
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btSilverBillOpeningOnAction(ActionEvent event) {
        silverBillOpeningScreen(event, null, false);
        billNumberFocus();
    }

    public void silverBillOpeningScreen(ActionEvent event, String billNumber, boolean onlyForView) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.SILVER_BILL_OPENING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillOpeningScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverBillOpeningController gon = (SilverBillOpeningController) loader.getController();
            if(billNumber != null) {
                gon.viewBill(billNumber, onlyForView);
            }
            
            dialog.setTitle("Silver Bill Opening - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();     
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }
    
    @FXML
    private void btSilverBillClosingOnAction(ActionEvent event) {
        silverBillClosingScreen(event, null, null, false);
        billNumberFocus();
    }

    private void silverBillClosingScreen(ActionEvent event, String billNumber, String copyName, boolean onlyForView) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.SILVER_BILL_CLOSING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverBillClosingController gon = (SilverBillClosingController) loader.getController();
            if(billNumber != null) {
                if(copyName != null && copyName.equals("PACK")) {
                    gon.viewClosedBill(event, billNumber, CommonConstants.DELIVERED);
                } else {
                    gon.closeBill(billNumber, onlyForView);
                }
            }
            dialog.setTitle("Silver Bill Closing - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();    
            
            getCompanyBillReminderVals();  
            billNumberFocus();
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }
    
    @FXML
    private void btSilverAdvanceAmountOnAction(ActionEvent event) {
        silverBilAdvanceAmtScreen(event, null, null, false);
    }

    private void silverBilAdvanceAmtScreen(ActionEvent event, String billNumber, String copyName, boolean onlyForView) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.SILVER_ADVANCE_AMOUNT_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(silverAdvanceAmountScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            SilverAdvanceAmountController gon = (SilverAdvanceAmountController) loader.getController();
            if(billNumber != null) {
                gon.viewBill(billNumber, onlyForView);
            }
            
            dialog.setTitle("Silver Advance Amount Receipt - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();            
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }
    
    @FXML
    private void btRepledgeGoldBillOpeningOnAction(ActionEvent event) {
    
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_OPENING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(repledgeGoldBillOpeningScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Repledge Bill Opening - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();      
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btRepledgeGoldBillClosingOnAction(ActionEvent event) {

        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(repledgeGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Repledge Bill Closing - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();    
            
            getRepledgeBillReminderVals();
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }
    
    @FXML
    private void btDebitPanelOnAction(ActionEvent event) {
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(debitPanelScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dialog.setTitle("Debit Panel - " + CommonConstants.ACTIVE_COMPANY_NAME);      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(1366*30/100);
        dialog.setY(bounds.getHeight()*22/100);
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();                                        
    }
    
    @FXML
    private void btCreditPanelOnAction(ActionEvent event) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(creditPanelScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dialog.setTitle("Credit Panel - " + CommonConstants.ACTIVE_COMPANY_NAME);      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(1366*28/100);
        dialog.setY(bounds.getHeight()*20/100);
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();                                        

    }
    
    @FXML
    private void btTodaysAccountOnAction(ActionEvent event) {
        todaysAccountScreenWork(event, null, false);
    }
    
    private void todaysAccountScreenWork(ActionEvent event, String tabName, boolean onlyForView) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.TODAYS_ACCOUNT_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            if(CommonConstants.ACTIVE_COMPANY_ACC_STARTING_DATE == null){ 

                PopupUtil.showInfoAlert("Please set the account starting date for company id "+ CommonConstants.ACTIVE_COMPANY_ID +".");

            } else {

                Stage dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(todaysAccountScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }

                TodaysAccountController gon = (TodaysAccountController) loader.getController();
                if(tabName != null) {
                    gon.showTab(tabName);
                }
                
                dialog.setTitle("Todays Account - " + CommonConstants.ACTIVE_COMPANY_NAME);      
                dialog.setX(CommonConstants.SCREEN_X);
                dialog.setY(CommonConstants.SCREEN_Y + 20);
                dialog.setWidth(CommonConstants.SCREEN_WIDTH);
                dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
                dialog.setResizable(false);
                Scene scene = new Scene(root);        
                dialog.setScene(scene);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();                                
            }
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }        
    }
    
    @FXML
    private void btJewelItemModuleOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.JEWEL_ITEM_MODULE_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(jewelItemMasterScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Jewel Item Module - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }
    
    @FXML
    private void btCompanyModuleOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.COMPANY_MODULE_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
            
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(companyMasterScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Company Module");      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();   
            setCompanyNameValues();
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btRePledgeModuleOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.REPLEDGE_MODULE_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(repledgeMasterScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Repledge Module - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();             
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }
    
    @FXML
    private void btEmployeeModuleOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.EMPLOYEE_MODULE_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(employeeMasterScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Employee Module - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();          
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btBillEditOperationOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.BILL_EDIT_OPERATION_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(billEditOperationScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Bill Edit Operation - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btRoleModuleOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.ROLE_MODULE_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(roleMasterScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Role Module - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();    
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btUserModuleOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.USER_MODULE_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(userMasterScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("User Module - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();               
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btStockDetailsOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.STOCK_DETAILS_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(stockDetailsScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Stock Details - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();    
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btGoldRebillMapperOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.GOLD_REBILL_MAPPER_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldRebillMapperScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Gold ReBill Mapper - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }        
        
    }

    @FXML
    private void btSilverRebillMapperOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.GOLD_REBILL_MAPPER_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(silverRebillMapperScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Silver ReBill Mapper - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }        
    }

    @FXML
    private void btActiveCompanyOnAction(ActionEvent event) {
            
            int sIndex = cbActiveCompany.getSelectionModel().getSelectedIndex();

            if(sIndex >= 0) {
                try {
                    dbOp.updateAllToRestStatus();
                    CommonConstants.ACTIVE_COMPANY_ID = companyNames.getRow(sIndex).getColumn(2).toString();
                    CommonConstants.ACTIVE_COMPANY_ACC_STARTING_DATE = companyNames.getRow(sIndex).getColumn(3).toString();
                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE = companyNames.getRow(sIndex).getColumn(3).toString();
                    CommonConstants.ACTIVE_COMPANY_NAME = companyNames.getRow(sIndex).getColumn(4).toString();
                    CommonConstants.ACTIVE_COMPANY_TYPE = companyNames.getRow(sIndex).getColumn(5).toString();
                    dbOp.updateActiveCompany(CommonConstants.ACTIVE_COMPANY_ID);
                    cbActiveCompany.setValue(companyNames.getRow(sIndex).getColumn(0).toString());           
                    PopupUtil.showInfoAlert("Current active company is \n"+ companyNames.getRow(sIndex).getColumn(0).toString() +". ");
                    
                    lineChartNumberOfBillsWork();
                    getCompanyBillReminderVals();
                    getRepledgeBillReminderVals();
                } catch (Exception ex) {
                    Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }        
    }

    @FXML
    private void btBillCalculatorOnAction(ActionEvent event) {
        billCalculatorScreen(event, null, null, true);
    }

    private void billCalculatorScreen(ActionEvent event, String billNumber, String sMaterialType, boolean forCompanyTab) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.BILL_CALCULATOR);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(billcalculatorScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            BillCalculatorController gon = (BillCalculatorController) loader.getController();
            
            /*if(billNumber == null) {
                gon.companyTabToSelect(forCompanyTab);
            } else*/ 
            if(billNumber != null && forCompanyTab) {
                gon.beforeClose(billNumber, sMaterialType);
            } else if(billNumber != null && !forCompanyTab) {
                gon.beforeRepClose(billNumber);
            }
            
            dialog.setTitle("Bill Calc");      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();          
            
            setCompanyNameValues();
            billNumberFocus();
            
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }           
    }
    
    @FXML
    private void btRepBillEditOperationOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.BILL_EDIT_OPERATION_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(repledgeBillEditOperationScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Repledge Bill Edit Operation - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btMSIReportOnAction(ActionEvent event) {
        
        //CHANGE THE PERMISSION DONT FORGET
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.REPORTS_TAB, CommonConstants.MIS_REPORTS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(msiReportScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("MSI Report - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }        
    }

    @FXML
    private void btCustomerDetailsOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.CUSTOMER_DETAILS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(customerDetailsScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Customer Details - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }             
    }

    @FXML
    private void btReBilledAfterRepledgedOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.GOLD_REBILL_MAPPER_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(rebilledAfterRepledged));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Rebilled After Repledged - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }                
    }

    @FXML
    private void btNoticeGenerationOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.GOLD_REBILL_MAPPER_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(noticeGeneration));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Notice Generation - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }                
    }

    @FXML
    private void btCompileAllReportsOnAction(ActionEvent event) {
        
        NoticeUtil noticeUtil = new NoticeUtil();
        noticeUtil.compileAllReportFiles();
    }

    @FXML
    private void btBackSidePrintOnAction(ActionEvent event) {
            try {
                String sCompanyFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billopening_backside.jasper";                        
                
                List<BillOpeningBean> ParamList = new ArrayList<>();	            
                BillOpeningBean bean = new BillOpeningBean();
                ParamList.add(bean);
                
                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

                Map<String, Object> parameters = new HashMap<>();
                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();
                noticeUtil.generateNoticeOperation(sCompanyFileName, parameters);
            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }           
    }

    @FXML
    private void btBlankOpeningOnAction(ActionEvent event) {
        
            try {
                String sCompanyFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billopening_company_blank.jasper";                        
                String sCustomerFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billopening_customer_blank.jasper";  
                
                List<BillOpeningBean> ParamList = new ArrayList<>();	            
                BillOpeningBean bean = new BillOpeningBean();
                ParamList.add(bean);
                
                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

                Map<String, Object> parameters = new HashMap<>();
                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();
                noticeUtil.mergeaAndGenerateNoticeIndividual("Blank Bill Opening", parameters, 
                        sCompanyFileName, sCustomerFileName);
            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }   
    }

    @FXML
    private void btLedgerOnAction(ActionEvent event) {
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.GOLD_REBILL_MAPPER_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        
 
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ledgerScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Ledger - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }                       
    }

    private void selectOrDeSelectAllRep(TableView<RepAllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }

    private void selectOrDeSelectAll(TableView<AllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }
    

    private void btCompBillRemSelAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbCompanyBillReminder, true);
    }

    private void btCompBillRemDeSelAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbCompanyBillReminder, false);
    }

    private void btCompBillDoNotRemOnAction(ActionEvent event) {
        for(AllDetailsBean bean : tbCompanyBillReminder.getItems()) {
            if(bean.isBChecked()) {
                try {
                    dbOp.updateRemindStatus(bean.getSBillNumber(), bean.getSMaterialType());
                } catch (Exception ex) {
                    Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        getCompanyBillReminderVals();
    }

    private void btRepBillRemSelAllOnAction(ActionEvent event) {
        selectOrDeSelectAllRep(tbRepledgeBillReminder, true);
    }

    private void btRepBillDoNotRemOnAction(ActionEvent event) {
        for(RepAllDetailsBean bean : tbRepledgeBillReminder.getItems()) {
            if(bean.isBChecked()) {
                try {
                    dbOp.updateRepRemindStatus(bean.getSRepledgeBillId());
                } catch (Exception ex) {
                    Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        getRepledgeBillReminderVals();
    }

    private void btRepBillRemDeSelAllOnAction(ActionEvent event) {
        selectOrDeSelectAllRep(tbRepledgeBillReminder, false);
    }

    @FXML
    private void tbCompRemAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbCompanyBillReminder.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sMaterialType = tbCompanyBillReminder.getItems().get(index).getSMaterialType();

            if("GOLD".equals(sMaterialType)) {

                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }

                GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
                gon.closeBill(tbCompanyBillReminder.getItems().get(index).getSBillNumber(), true);

                dialog.setTitle("Gold Bill Closing");      
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                dialog.setX(0);
                dialog.setY(5);
                dialog.setWidth(bounds.getWidth());
                dialog.setHeight(bounds.getHeight());
                dialog.setResizable(false);
                Scene scene = new Scene(root);        
                dialog.setScene(scene);
                dialog.showAndWait();        

            } else {

                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillClosingScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }

                SilverBillClosingController gon = (SilverBillClosingController) loader.getController();
                gon.closeBill(tbCompanyBillReminder.getItems().get(index).getSBillNumber(), true);

                dialog.setTitle("Silver Bill Closing");      
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
        } else if(event.getClickCount() == 1) {
            if(tbCompanyBillReminder != null && index <= tbCompanyBillReminder.getItems().size()) {
                tbCompanyBillReminder.getItems().get(index)
                        .setBChecked(!tbCompanyBillReminder.getItems().get(index).getBCheckedProperty());
            }
        }         
        
    }

    @FXML
    private void tbRepRemAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbRepledgeBillReminder.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            gon.closeBill(tbRepledgeBillReminder.getItems().get(index).getSRepledgeBillId(), true);

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
            
        } else if(event.getClickCount() == 1) {
            tbRepledgeBillReminder.getItems().get(index)
                    .setBChecked(!tbRepledgeBillReminder.getItems().get(index).getBCheckedProperty());
        }        
        
    }

    @FXML
    private void btFIlingStickerOnAction(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(billStickerScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        BillOpeningFilingStickerDialogUIController gon = (BillOpeningFilingStickerDialogUIController) loader.getController();
        gon.setParent(this);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();        
    }

    @FXML
    private void btLockerStickerOnAction(ActionEvent event) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(lockerStickerScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        LockerStickerDialogUIController gon = (LockerStickerDialogUIController) loader.getController();
        gon.setParent(this);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();        
    }

    @FXML
    public void txtBillNumberOnAction(ActionEvent event) {
        try {
            searchBarWorks(event);
        } catch (IOException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void searchBarWorks(ActionEvent event) throws IOException {
        
        String val = txtBillNumber.getText();
        if(val != null & !val.isEmpty()) {
            
            String[] billVals = val.split("-"); 
            
            if(val.contains("-G-COMP") || val.contains("-G-CUST") || val.contains("-G-PACK")) {                
                if(billVals.length == 3) {
                    doScreenWork(event, 
                        CommonConstants.GOLD_BILL_CLOSING_SCREEN, billVals[0], billVals[2], "GOLD", false, true);
                } else {
                    doScreenWork(event, 
                        CommonConstants.GOLD_BILL_CLOSING_SCREEN, billVals[0], null, "GOLD", false, true);
                }
            } else if(val.contains("-S-COMP") || val.contains("-S-CUST") || val.contains("-S-PACK")) {                
                if(billVals.length == 3) {
                    doScreenWork(event, 
                        CommonConstants.SILVER_BILL_CLOSING_SCREEN, billVals[0], billVals[2], "SILVER", false, true);
                } else {
                    doScreenWork(event, 
                        CommonConstants.SILVER_BILL_CLOSING_SCREEN, billVals[0], null, "SILVER", false, true);
                }
            } else if(billVals.length == 2) {
                Properties keys = getProperties();
                String key = keys.getProperty(billVals[1]);
                doScreenWork(event, key, billVals[0], null, null, true, true);                
            } else {
                Properties keys = getProperties();
                String key = keys.getProperty(val);
                doScreenWork(event, key, null, null, null, true, true);
            }
        }        
    }
    
    private Properties getProperties() throws FileNotFoundException, IOException {

        Properties prop = new Properties();
        String propFileName = "searchbar.properties";

        InputStream  inputStream = getClass().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
            return prop;
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }   
    }
    
    
    private void doScreenWork(ActionEvent event, String screenName,  
            String billNumber, String copyName, String materialType, boolean onlyForView, boolean isFirstTab) {        
        if(screenName != null) {
            switch(screenName) {            
                case CommonConstants.GOLD_BILL_OPENING_SCREEN:
                    goldBillOpeningScreenWork(event, billNumber, onlyForView);   
                    break;
                case CommonConstants.GOLD_BILL_CLOSING_SCREEN:
                    goldBilClosingScreen(event, billNumber, copyName, onlyForView);
                    break;
                case CommonConstants.SILVER_BILL_OPENING_SCREEN:
                    silverBillOpeningScreen(event, billNumber, onlyForView);
                    break;
                case CommonConstants.SILVER_BILL_CLOSING_SCREEN:
                    silverBillClosingScreen(event, billNumber, copyName, onlyForView);
                    break;
                case CommonConstants.BILL_CALCULATOR:
                    billCalculatorScreen(event, billNumber, materialType, isFirstTab);
                    break;
                case CommonConstants.GOLD_ADVANCE_AMOUNT_SCREEN:
                    goldBilAdvanceAmtScreen(event, billNumber, copyName, onlyForView);
                    break;
                case CommonConstants.SILVER_ADVANCE_AMOUNT_SCREEN:
                    silverBilAdvanceAmtScreen(event, billNumber, copyName, onlyForView);
                    break;
                case CommonConstants.TODAYS_ACCOUNT_SCREEN:
                    todaysAccountScreenWork(event, null, false);
                    break;
                case CommonConstants.JEWEL_ACCOUNT_TAB:
                    todaysAccountScreenWork(event, screenName, false);
                    break;
                default:
                    break;
            }
        } else {
            PopupUtil.showErrorAlert(event, "Invalid Search Keyword.");
        }
    }
    
    @FXML
    private void btSearchIconClicked(MouseEvent event) {
        txtBillNumberOnAction(null);
    }

    @FXML
    private void ivSoundClicked(MouseEvent event) {
        try {
            String sHeadingImage = null;
            if(CommonConstants.IRAIVA_SOUND_ON) {
                sHeadingImage = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\images\\sound_off.png";    
                CommonConstants.IRAIVA_SOUND_ON = false;
            } else {
                sHeadingImage = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\images\\sound_on.png"; 
                CommonConstants.IRAIVA_SOUND_ON = true;
            }
            FileInputStream fis = new FileInputStream(sHeadingImage);
            ivSound.setImage(new Image(fis));        
            fis.close();

        } catch (IOException ex) {
            Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btImgMigrationOnAction(ActionEvent event) {
    }

    @FXML
    private void btEMIGoldBIllOPeningClicked(ActionEvent event) {
        goldEMIBillOpeningScreenWork(event, null, false);        
    }

    public void goldEMIBillOpeningScreenWork(ActionEvent event, String billNumber, boolean onlyForView) {
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_OPENING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(emiGoldBillOpeningScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            EMIGoldBillOpeningController gon = (EMIGoldBillOpeningController) loader.getController();
            //gon.setParent(dialog);
            //if(billNumber != null) {
                //gon.viewBill(billNumber, onlyForView);
            //}
            
            dialog.setTitle("Gold Bill Opening - " 
                    + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);
            if(CommonConstants.ACTIVE_MACHINE.getLanguage().equals(CommonConstants.TAMIL)) {
                scene.getStylesheets().add("/com/magizhchi/pawnbroking/companybillopening/billopeningTamil.css");
            }
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            dialog.showAndWait();
            
            billNumberFocus();
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }

    @FXML
    private void bt8020ReportOnAction(ActionEvent event) {        
        //CHANGE THE PERMISSION DONT FORGET
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.REPORTS_TAB, CommonConstants.MIS_REPORTS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(report8020Screen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("80-20 Report - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }        
    }

    @FXML
    private void btShopLockerStockOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.REPORTS_TAB, CommonConstants.MIS_REPORTS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(shopLockerStockDialog));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Shop Locker Stock - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }                
    }

    @FXML
    private void btShopBillStockOnAction(ActionEvent event) {
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.REPORTS_TAB, CommonConstants.MIS_REPORTS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(shopBillStockDialog));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Shop Bill Stock - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }                
    }

    @FXML
    private void btRepledgeBillStockOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.REPORTS_TAB, CommonConstants.MIS_REPORTS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            try {
                String sFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\repledgeStock.jasper";                        

                List<RepledgeBillPrintBean> ParamList = dbOp.repledgeBillStock();

                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("BillCalcCollectionBeanParam", tableList);
                parameters.put("TODAYSDATE", DateRelatedCalculations.getTodaysDate());

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
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }                            
    }

    @FXML
    private void btMonthlyReportOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.REPORTS_TAB, CommonConstants.MIS_REPORTS);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(trialBalanceScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(OwnerMainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Shop Bill Stock - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();                                   
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }   
    }

    @FXML
    private void btLogoClicked(MouseEvent event) {
        if(mainPanel.getChildren().contains(chartPanel)) {
            mainPanel.getChildren().remove(chartPanel);
            mainPanel.getChildren().add(2, magizhchiAddPanel);
        }else {
            mainPanel.getChildren().remove(magizhchiAddPanel);
            mainPanel.getChildren().add(2, chartPanel);
            lineChartNumberOfBillsWork();
        }
    }

    @FXML
    private void btExitIconClicked(MouseEvent event) {
        ScreenUtil.customerWindow.dispose();
        this.mainScreenStage.close();
    }
        
}
