/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billcalculator;

import com.magizhchi.pawnbroking.account.TodaysAccountController;
import com.magizhchi.pawnbroking.account.TodaysAccountJewelRepledgeBean;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.common.WebCamWork;
import com.magizhchi.pawnbroking.companybillclosing.AvailableBalanceBean;
import com.magizhchi.pawnbroking.companybillclosing.BillClosingDBOperation;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import com.magizhchi.pawnbroking.companybillopening.BillOpeningDBOperation;
import com.magizhchi.pawnbroking.companybillopening.CustomerBillsListBean;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.companybillopening.SilverBillOpeningController;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeBillClosingDBOperation;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import com.magizhchi.pawnbroking.repledgebillopening.RepledgeBillOpeningDBOperation;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileInputStream;
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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.bytedeco.javacv.FrameGrabber;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class BillCalculatorController implements Initializable {

    public BillClosingDBOperation dbOp;
    private BillOpeningDBOperation dbOpOpen;
    private RepledgeBillClosingDBOperation dbOpRep;
    public RepledgeBillOpeningDBOperation dbOpRepOpen;
    private DataTable companyNames = null;
    
    private String sRepledgeBillId = null;
    private String sReduceType = null;
    private String sMinimumType = null;
    private String sRebilledTo = null;
    public Stage dialog;
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";
    final String goldBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/GoldBillOpening.fxml";
    final String silverBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/SilverBillOpening.fxml";
    final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    String currentGoldPrefix = null;    
    int currentGoldBillOpenNumber = 0;        
    String currentSilverPrefix = null;    
    int currentSilverBillOpenNumber = 0;        

    public static boolean isCustomerImgAvailable = false;
    private DataTable otherSettingValues = null;
    private File tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
    private File compFolder = new File(tempFile, CommonConstants.ACTIVE_COMPANY_ID);
    private File materialFolder = new File(compFolder, "GOLD");
    
    
    int iRebillNumber = 0;
    DataTable repledgeNames;
    int iLastSelectedIndex = 0;
    boolean isFromAmountField = false;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private HBox filterHBOX;
    @FXML
    private HBox companyAloneFilterHBox;
    @FXML
    private ComboBox<String> cbMaterialType;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private ComboBox<String> cbOperation;
    @FXML
    private TableView<AllDetailsBean> tbList;
    @FXML
    private Label lbFrom1;
    @FXML
    private DatePicker dpClosingDate;
    @FXML
    private ComboBox<String> cbBeforeAfter;
    @FXML
    public Label lbAmtToGet;
    @FXML
    public TextField txtAmtToGet;
    @FXML
    private HBox hbAmount;
    @FXML
    private TextField txtAmount;
    @FXML
    private HBox hbClosingBillNumber;
    @FXML
    private TextField txtClosingBillNumber;
    @FXML
    private HBox hbOpeningBillNumber;
    @FXML
    private TextField txtOpeningBillNumber;
    @FXML
    private Button btRemoveFromTable;
    @FXML
    private TextField txtOpenInterest;
    @FXML
    private TextField txtCloseInterest;
    @FXML
    private HBox filterHBOX1;
    @FXML
    private HBox companyAloneFilterHBox1;
    @FXML
    private ComboBox<String> cbRepMaterialType;
    @FXML
    private Label lbFrom11;
    @FXML
    private DatePicker dpRepClosingDate;
    @FXML
    private ComboBox<String> cbRepBeforeAfter;
    @FXML
    private ComboBox<String> cbRepOperation;
    @FXML
    private TextField txtRepClosingBillNumber;
    @FXML
    private TextField txtRepOpeningBillNumber;
    @FXML
    private TextField txtRepAmount;
    @FXML
    private Button btRepRemoveFromTable;
    @FXML
    private TextField txtRepOpenInterest;
    @FXML
    private TextField txtRepCloseInterest;
    @FXML
    public Label lbAmtToGet1;
    @FXML
    public TextField txtRepAmtToGet;
    @FXML
    private TableView<RepAllDetailsBean> tbRepList;
    @FXML
    private HBox nodeRepAddToFilter;
    @FXML
    private HBox hbRepClosingBillNumber;
    @FXML
    private HBox hbRepOpeningBillNumber;
    @FXML
    private HBox hbRepAmount;
    @FXML
    private ComboBox<String> cbRepNames;
    @FXML
    private TextField txtOpenInterested;
    @FXML
    private TextField txtCloseInterested;
    @FXML
    private TextField txtRepOpenInterested;
    @FXML
    private TextField txtRepCloseInterested;
    @FXML
    private TextField txtOpenCapital;
    @FXML
    private TextField txtCloseCapital;
    @FXML
    private TextField txtRepOpenCapital;
    @FXML
    private TextField txtRepCloseCapital;
    @FXML
    private TextField txtTotalWt;
    @FXML
    private ComboBox<String> cbActiveCompany;
    @FXML
    private TextField txtTotalRatePerGm;
    @FXML
    private HBox hbSuspenseBillNumber;
    @FXML
    private TextField txtSuspenseBillNumber;
    @FXML
    private ComboBox<String> cbMultiOperation;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btDoOperation;
    @FXML
    private Button btCaptureCustomerImg;
    @FXML
    private ImageView ivCustomer;
    @FXML
    private TextField txtRepPlannerId;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btSaveBill;
    @FXML
    private Button btUpdateBill;
    @FXML
    private Button btClearAll;
    @FXML
    private Button btPrintSelected;
    @FXML
    private Button btRepSelectAll;
    @FXML
    private Button btRepDeSelectAll;
    @FXML
    private Button btRepPendingRecords;
    @FXML
    private Button btRepSuspense;
    @FXML
    private CheckBox chChangeRepAmount;
    @FXML
    private Button btPrintCompSelected;

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
                                            case ENTER :
                                            {
                                                /*if ( (!(KV.getTarget() instanceof TextField)) || (!(KV.getTarget() instanceof Button)))
                                                {
                                                    eventRobot.keyPress( java.awt.event.KeyEvent.VK_TAB );
                                                    eventRobot.keyRelease( java.awt.event.KeyEvent.VK_TAB );
                                                    KV.consume();
                                                }*/
                                                break;
                                            }
                                            case TAB :
                                            {
                                                if ( ! (KV.getTarget() instanceof TextField))
                                                {
                                                    KV.consume();
                                                }
                                                break;
                                            }
                                            case ESCAPE :
                                            {					               
                                                Stage sb = (Stage)lbAmtToGet.getScene().getWindow();//use any one object
                                                sb.close();
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
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        shortCutKeysCode();
        
        try {
            
        tbList.setRowFactory(tv -> new TableRow<AllDetailsBean>() {
            @Override
            public void updateItem(AllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getSRepledgeBillId() != null && !item.getSRepledgeBillId().isEmpty()) {
                    setStyle(Util.getStyle("#000000", "#FCBAEF").toString());
                } else {
                    setStyle("");
                }
            }
        });
            
            try {
                dbOp = new BillClosingDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
                dbOpOpen = new BillOpeningDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
                dbOpRep = new RepledgeBillClosingDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
                dbOpRepOpen = new RepledgeBillOpeningDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            dpClosingDate.setValue(LocalDate.now());
            dpRepClosingDate.setValue(LocalDate.now());
            
            nodeAddToFilter.getChildren().remove(hbAmount);
            nodeAddToFilter.getChildren().remove(hbOpeningBillNumber);
            nodeAddToFilter.getChildren().remove(hbSuspenseBillNumber);
                                   
            cbMultiOperationOnAction(null);
                    
            nodeRepAddToFilter.getChildren().remove(hbRepAmount);
            nodeRepAddToFilter.getChildren().remove(hbRepOpeningBillNumber);
            nodeRepAddToFilter.getChildren().remove(cbRepNames);
            
            setCompanyNameValues();
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtClosingBillNumber.setText(billRowAndNumber[1]);
                currentGoldPrefix = billRowAndNumber[1];
                currentGoldBillOpenNumber = Integer.parseInt(billRowAndNumber[2]);                
            }
            String[] sbillRowAndNumber = dbOp.getSilverCurrentBillNumber();
            if(sbillRowAndNumber != null) {
                currentSilverPrefix = sbillRowAndNumber[1];
                currentSilverBillOpenNumber = Integer.parseInt(sbillRowAndNumber[2]);                
            }
            
            try {
                otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
            

            Platform.runLater(() -> {
                txtClosingBillNumber.requestFocus();
                txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());
            });

            txtRepPlannerId.setEditable(false);
            txtRepPlannerId.setMouseTransparent(true);
            txtRepPlannerId.setFocusTraversable(false);             
            txtRepPlannerId.setText(dbOpRepOpen.getPlanId("REP_BILL_CALC"));
            btPrintSelected.setDisable(true);
            
            if(billRowAndNumber != null) {
                txtRepClosingBillNumber.setText(billRowAndNumber[1]);
            }
            
            MenuItem mi1 = new MenuItem("Change Amount");
            mi1.setOnAction((ActionEvent event) -> {
                String operation = 
                        tbRepList.getSelectionModel()
                                .getSelectedItem().getSOperation();
                if("BILL OPENING".equals(operation)) 
                { 
                }
            });

            ContextMenu menu = new ContextMenu();
            menu.getItems().add(mi1);
            tbRepList.setContextMenu(menu);

            Platform.runLater(() -> {
                txtRepClosingBillNumber.requestFocus();
                txtRepClosingBillNumber.positionCaret(txtRepClosingBillNumber.getText().length());
            });
            
        } catch (SQLException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void cbOperationOnAction(ActionEvent event) {
        Platform.runLater(()->{
            addRemoveComponents();
        });
    }

    public void companyTabToSelect(boolean selectCompTab) {
        if(selectCompTab) {
            tpScreen.getSelectionModel().select(0);     
        } else {
            tpScreen.getSelectionModel().select(1);
        }
    }
    
    public void beforeClose(String sClosingBillNumber, String sMaterialType) {        
        txtClosingBillNumber.setText(sClosingBillNumber);
        cbMaterialType.getSelectionModel().select(sMaterialType);   
        txtClosingBillNumberOnAction(null);
    }
    
    public void beforeClose(ObservableList<CustomerBillsListBean> otherTableValues, String closingDate) {
        
        otherTableValues.stream().forEach((bean) -> {
            if(bean.getCompId().equals(CommonConstants.ACTIVE_COMPANY_ID)) {
                txtClosingBillNumber.setText(bean.getBillNumber());
                cbMaterialType.getSelectionModel().select(bean.getMaterialType());   
                dpClosingDate.setValue(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
                txtClosingBillNumberOnAction(null);
            }
        });        
    }

    public void beforeRepClose(String sBillNumber) {        
        txtRepClosingBillNumber.setText(sBillNumber);        
        txtRepClosingBillNumberOnAction(null);
        tpScreen.getSelectionModel().select(1);  
    }
    
    public void beforeRepClose(ObservableList<TodaysAccountJewelRepledgeBean> otherTableValues) {
        
        otherTableValues.stream().forEach((bean) -> {
            txtRepClosingBillNumber.setText(bean.getSCompanyBillNumber());        
            txtRepClosingBillNumberOnAction(null);
        });        
        tpScreen.getSelectionModel().select(1);  
    }

    public void beforeRepClose(DataTable dataTableValues) {
        
        cbRepOperation.getSelectionModel().select("BILL CLOSING");
        addRemoveRepComponents();            
        for(int i=0; i<dataTableValues.getRowCount(); i++) {
            String sBillNumber = dataTableValues.getRow(i).getColumn(5).toString();
            txtRepClosingBillNumber.setText(sBillNumber);        
            txtRepClosingBillNumberOnAction(null);
        }
        tpScreen.getSelectionModel().select(1);  
    }
    
    public void beforeRepOpen(ObservableList
            <com.magizhchi.pawnbroking.stockdetails.AllDetailsBean> otherTableValues,
            String repName) {
        
        otherTableValues.stream().forEach((bean) -> {
            if(bean.isBChecked()) {
                cbRepOperation.getSelectionModel().select("BILL OPENING");
                addRemoveRepComponents();            
                cbRepNames.getSelectionModel().select(repName);
                txtRepOpeningBillNumber.setText(bean.getSBillNumber());        
                txtRepOpeningBillNumberOnAction(null);
            }
        });        
        tpScreen.getSelectionModel().select(1);  
    }
    
    private void addRemoveComponents() {
        try {
            
            if(cbOperation.getSelectionModel().getSelectedItem().equals("BILL CLOSING")) {
                if(!nodeAddToFilter.getChildren().contains(hbClosingBillNumber))
                    nodeAddToFilter.getChildren().add(3, hbClosingBillNumber);
                if(nodeAddToFilter.getChildren().contains(hbOpeningBillNumber))
                    nodeAddToFilter.getChildren().remove(hbOpeningBillNumber);
                if(nodeAddToFilter.getChildren().contains(hbAmount))
                    nodeAddToFilter.getChildren().remove(hbAmount);

                String[] billRowAndNumber = null;
                if(cbMaterialType.getValue().equals("GOLD")) {
                    billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                } else if(cbMaterialType.getValue().equals("SILVER")) {
                    billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                }
                if(billRowAndNumber != null) {
                    txtClosingBillNumber.setText(billRowAndNumber[1]);
                }
                Platform.runLater(() -> {
                    txtClosingBillNumber.requestFocus();
                    txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());
                });                
            } else if(cbOperation.getSelectionModel().getSelectedItem().equals("BILL OPENING")) {
                if(cbBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER")) {
                    if(!nodeAddToFilter.getChildren().contains(hbOpeningBillNumber))
                        nodeAddToFilter.getChildren().add(3, hbOpeningBillNumber);
                    if(nodeAddToFilter.getChildren().contains(hbClosingBillNumber))
                        nodeAddToFilter.getChildren().remove(hbClosingBillNumber);
                    if(nodeAddToFilter.getChildren().contains(hbAmount))
                        nodeAddToFilter.getChildren().remove(hbAmount);
                    
                    String[] billRowAndNumber = null;
                    if(cbMaterialType.getValue().toString().equals("GOLD")) {
                        billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                    } else if(cbMaterialType.getValue().toString().equals("SILVER")) {
                        billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                    }
                    if(billRowAndNumber != null) {
                        txtOpeningBillNumber.setText(billRowAndNumber[1]);
                    }
                    Platform.runLater(() -> {
                        txtOpeningBillNumber.requestFocus();
                        txtOpeningBillNumber.positionCaret(txtOpeningBillNumber.getText().length());
                    });
                } else {
                    if(!nodeAddToFilter.getChildren().contains(hbAmount))
                        nodeAddToFilter.getChildren().add(3, hbAmount);
                    if(nodeAddToFilter.getChildren().contains(hbOpeningBillNumber))
                        nodeAddToFilter.getChildren().remove(hbOpeningBillNumber);
                    if(nodeAddToFilter.getChildren().contains(hbClosingBillNumber))
                        nodeAddToFilter.getChildren().remove(hbClosingBillNumber);                
                }                     
            } else if(cbOperation.getSelectionModel().getSelectedItem().equals("REBILL")) {
                if(cbBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER")) {
                    if(!nodeAddToFilter.getChildren().contains(hbClosingBillNumber))
                        nodeAddToFilter.getChildren().add(3, hbClosingBillNumber); 
                    if(!nodeAddToFilter.getChildren().contains(hbOpeningBillNumber))
                        nodeAddToFilter.getChildren().add(4, hbOpeningBillNumber);                
                    if(nodeAddToFilter.getChildren().contains(hbAmount))
                        nodeAddToFilter.getChildren().remove(hbAmount);
                    String[] billRowAndNumber = null;
                    if(cbMaterialType.getValue().toString().equals("GOLD")) {
                        billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                    } else if(cbMaterialType.getValue().toString().equals("SILVER")) {
                        billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                    }
                    if(billRowAndNumber != null) {
                        txtOpeningBillNumber.setText(billRowAndNumber[1]);
                    }
                    Platform.runLater(() -> {
                        txtOpeningBillNumber.requestFocus();
                        txtOpeningBillNumber.positionCaret(txtOpeningBillNumber.getText().length());
                    });                    
                } else {
                    if(!nodeAddToFilter.getChildren().contains(hbClosingBillNumber))
                        nodeAddToFilter.getChildren().add(3, hbClosingBillNumber); 
                    if(!nodeAddToFilter.getChildren().contains(hbAmount))
                        nodeAddToFilter.getChildren().add(4, hbAmount);                
                    if(nodeAddToFilter.getChildren().contains(hbOpeningBillNumber))
                        nodeAddToFilter.getChildren().remove(hbOpeningBillNumber);            
                }

                String[] billRowAndNumber = null;
                if(cbMaterialType.getValue().toString().equals("GOLD")) {
                    billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                } else if(cbMaterialType.getValue().toString().equals("SILVER")) {
                    billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                }
                if(billRowAndNumber != null) {
                    txtClosingBillNumber.setText(billRowAndNumber[1]);
                }
                Platform.runLater(() -> {
                    txtClosingBillNumber.requestFocus();
                    txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());
                });
                
            }       
        } catch (SQLException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addRemoveRepComponents() {
        
        try {
            if(cbRepOperation.getSelectionModel().getSelectedItem().equals("BILL CLOSING")) {
                if(!nodeRepAddToFilter.getChildren().contains(hbRepClosingBillNumber))
                    nodeRepAddToFilter.getChildren().add(2, hbRepClosingBillNumber);
                if(nodeRepAddToFilter.getChildren().contains(hbRepOpeningBillNumber))
                    nodeRepAddToFilter.getChildren().remove(hbRepOpeningBillNumber);
                if(nodeRepAddToFilter.getChildren().contains(hbRepAmount))
                    nodeRepAddToFilter.getChildren().remove(hbRepAmount);
                if(nodeRepAddToFilter.getChildren().contains(cbRepNames))
                    nodeRepAddToFilter.getChildren().remove(cbRepNames);
                if(nodeRepAddToFilter.getChildren().contains(hbSuspenseBillNumber))
                    nodeRepAddToFilter.getChildren().remove(hbSuspenseBillNumber);                       
                
                nodeRepAddToFilter.getChildren().remove(btRepRemoveFromTable); 
                nodeRepAddToFilter.getChildren().add(3, btRepRemoveFromTable); 

                String[] billRowAndNumber = null;
                if(cbMaterialType.getValue().equals("GOLD")) {
                    billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                } else if(cbMaterialType.getValue().equals("SILVER")) {
                    billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                }
                if(billRowAndNumber != null) {
                    txtRepClosingBillNumber.setText(billRowAndNumber[1]);
                }
                Platform.runLater(() -> {
                    txtRepClosingBillNumber.requestFocus();
                    txtRepClosingBillNumber.positionCaret(txtRepClosingBillNumber.getText().length());
                });                

            } else if(cbRepOperation.getSelectionModel().getSelectedItem().equals("BILL OPENING")) {
                if(cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER")) {
                    if(!nodeRepAddToFilter.getChildren().contains(hbRepOpeningBillNumber))
                        nodeRepAddToFilter.getChildren().add(2, hbRepOpeningBillNumber);
                    if(nodeRepAddToFilter.getChildren().contains(hbRepClosingBillNumber))
                        nodeRepAddToFilter.getChildren().remove(hbRepClosingBillNumber);
                    if(nodeRepAddToFilter.getChildren().contains(hbRepAmount))
                        nodeRepAddToFilter.getChildren().remove(hbRepAmount);
                    if(nodeRepAddToFilter.getChildren().contains(cbRepNames))
                        nodeRepAddToFilter.getChildren().remove(cbRepNames);        
                    if(nodeRepAddToFilter.getChildren().contains(hbSuspenseBillNumber))
                        nodeRepAddToFilter.getChildren().remove(hbSuspenseBillNumber);                       
                    nodeRepAddToFilter.getChildren().remove(btRepRemoveFromTable); 
                    nodeRepAddToFilter.getChildren().add(3, btRepRemoveFromTable);                     
                    
                } else {
                    if(!nodeRepAddToFilter.getChildren().contains(hbRepAmount))
                        nodeRepAddToFilter.getChildren().add(4, hbRepAmount);
                    if(!nodeRepAddToFilter.getChildren().contains(hbRepOpeningBillNumber))
                        nodeRepAddToFilter.getChildren().add(3, hbRepOpeningBillNumber);
                    if(!nodeRepAddToFilter.getChildren().contains(cbRepNames))
                        nodeRepAddToFilter.getChildren().add(2, cbRepNames);
                    if(nodeRepAddToFilter.getChildren().contains(hbRepClosingBillNumber))
                        nodeRepAddToFilter.getChildren().remove(hbRepClosingBillNumber);   
                    if(nodeRepAddToFilter.getChildren().contains(hbSuspenseBillNumber))
                        nodeRepAddToFilter.getChildren().remove(hbSuspenseBillNumber);   
                    nodeRepAddToFilter.getChildren().remove(btRepRemoveFromTable); 
                    nodeRepAddToFilter.getChildren().add(5, btRepRemoveFromTable); 

                    try {                
                        cbRepNames.getItems().removeAll(cbRepNames.getItems());
                        repledgeNames = dbOpRepOpen.getAllRepledgeNames();
                        for(int i=0; i<repledgeNames.getRowCount(); i++) {
                            cbRepNames.getItems().add(repledgeNames.getRow(i).getColumn(1).toString());
                        }
                        cbRepNames.getSelectionModel().select(iLastSelectedIndex);
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }                     

                String[] billRowAndNumber = null;
                if(cbMaterialType.getValue().equals("GOLD")) {
                    billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                } else if(cbMaterialType.getValue().equals("SILVER")) {
                    billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                }
                if(billRowAndNumber != null) {
                    txtRepOpeningBillNumber.setText(billRowAndNumber[1]);
                }
                Platform.runLater(() -> {
                    txtRepOpeningBillNumber.requestFocus();
                    txtRepOpeningBillNumber.positionCaret(txtRepOpeningBillNumber.getText().length());
                });
                
            } else if(cbRepOperation.getSelectionModel().getSelectedItem().equals("GET SUSPENSE")) {

                if(!nodeRepAddToFilter.getChildren().contains(hbSuspenseBillNumber))
                    nodeRepAddToFilter.getChildren().add(2, hbSuspenseBillNumber);
                if(nodeRepAddToFilter.getChildren().contains(hbRepOpeningBillNumber))
                    nodeRepAddToFilter.getChildren().remove(hbRepOpeningBillNumber);
                if(nodeRepAddToFilter.getChildren().contains(hbRepAmount))
                    nodeRepAddToFilter.getChildren().remove(hbRepAmount);
                if(nodeRepAddToFilter.getChildren().contains(cbRepNames))
                    nodeRepAddToFilter.getChildren().remove(cbRepNames);
                if(nodeRepAddToFilter.getChildren().contains(hbRepClosingBillNumber))
                    nodeRepAddToFilter.getChildren().remove(hbRepClosingBillNumber);                   
                nodeRepAddToFilter.getChildren().remove(btRepRemoveFromTable); 
                nodeRepAddToFilter.getChildren().add(3, btRepRemoveFromTable); 

                String[] billRowAndNumber = null;
                if(cbMaterialType.getValue().equals("GOLD")) {
                    billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                } else if(cbMaterialType.getValue().equals("SILVER")) {
                    billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                }
                if(billRowAndNumber != null) {
                    txtSuspenseBillNumber.setText(billRowAndNumber[1]);
                }
                Platform.runLater(() -> {
                    txtSuspenseBillNumber.requestFocus();
                    txtSuspenseBillNumber.positionCaret(txtRepClosingBillNumber.getText().length());
                });                
                
            }     
        } catch (SQLException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setOpenHeaderValuesToFields(HashMap<String, String> headerValues, String sMaterialType, String sBillNumber) throws SQLException
    {
            String sBOrA = cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            String sOperation = cbOperation.getSelectionModel().getSelectedItem();
            
            if("REBILL".equals(sOperation)) {
                sOperation = "OPEN - " + "REBILL" + iRebillNumber;
            }
            
            String sAmount = headerValues.get("AMOUNT") != null 
                    ? headerValues.get("AMOUNT") : "0";
            
            String sGrsWt = headerValues.get("GROSS_WEIGHT") != null 
                    ? headerValues.get("GROSS_WEIGHT") : "0";
            
            double dRatePerGm = Double.parseDouble(sAmount)/Double.parseDouble(sGrsWt);
            
            tbList.getItems().add(new AllDetailsBean(sBOrA, sOperation, sBillNumber, 
                    headerValues.get("OPENING_DATE"), sMaterialType, headerValues.get("ITEMS"),
                    sAmount, headerValues.get("INTEREST"), 
                    headerValues.get("OPEN_TAKEN_AMOUNT"), headerValues.get("TOGIVE_AMOUNT"), 
                    "", "", 
                    headerValues.get("CUSTOMER_NAME"), sGrsWt, 
                    true, String.valueOf(Math.round(dRatePerGm))));
            
            closeDateRestriction();
            calculateTotalAmtToBeRecieved();
    }
    
    public void setAmountRelatedText(double dAmount, String sMaterialType) {
    
        try {
            String sInterest = dbOpOpen.getInterest(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), dAmount, sMaterialType).trim();
            String sDocumentCharge = dbOpOpen.getDocumentCharge(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), dAmount, sMaterialType).trim();
            String sFormula = dbOpOpen.getFormula(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), dAmount, sMaterialType);
            
            double dInterest = sInterest != null ? Double.parseDouble((!"".equals(sInterest)) ? sInterest : "0") : 0;
            double dDocumentCharge = sDocumentCharge != null ? Double.parseDouble((!"".equals(sDocumentCharge)) ? sDocumentCharge : "0") : 0;                   
            
            String[][] replacements = {{"AMOUNT", String.valueOf(dAmount)}, 
                                       {"INTEREST", sInterest},
                                       {"DOCUMENT_CHARGE", sDocumentCharge}};            
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");            
            String sTakenAmount = engine.eval(sFormula).toString() != null ? 
                    String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString()))) : "0";            
            String sToGive = Double.toString(dAmount - Double.parseDouble(sTakenAmount));
                        
            String sBOrA = cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            String sOperation = cbOperation.getSelectionModel().getSelectedItem();
            
            if("REBILL".equals(sOperation)) {
                sOperation = "OPEN - " + "REBILL" + iRebillNumber;
            }
            
            String billNumber = null;            
            if(cbMaterialType.getValue().equals("GOLD")) {
                billNumber = currentGoldPrefix + currentGoldBillOpenNumber;
                currentGoldBillOpenNumber++;
            } else if(cbMaterialType.getValue().equals("SILVER")) {
                billNumber = currentSilverPrefix + currentSilverBillOpenNumber;
                currentSilverBillOpenNumber++;
            }
            
            tbList.getItems().add(new AllDetailsBean(sBOrA, sOperation, billNumber, 
                    CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sMaterialType, "",
                    String.valueOf(dAmount), sTakenAmount, sTakenAmount, sToGive, "", "", "", "", true, ""));
            
            closeDateRestriction();
            calculateTotalAmtToBeRecieved();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setBeforeRebillAmountRelatedText(double dAmount, String sMaterialType) {
    
        try {
            String sInterest = dbOpOpen.getInterest(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), dAmount, sMaterialType).trim();
            String sDocumentCharge = dbOpOpen.getDocumentCharge(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), dAmount, sMaterialType).trim();
            String sFormula = dbOpOpen.getFormula(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), dAmount, sMaterialType);
            
            double dInterest = sInterest != null ? Double.parseDouble((!"".equals(sInterest)) ? sInterest : "0") : 0;
            double dDocumentCharge = sDocumentCharge != null ? Double.parseDouble((!"".equals(sDocumentCharge)) ? sDocumentCharge : "0") : 0;                   
            
            String[][] replacements = {{"AMOUNT", String.valueOf(dAmount)}, 
                                       {"INTEREST", sInterest},
                                       {"DOCUMENT_CHARGE", sDocumentCharge}};            
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");            
            String sTakenAmount = engine.eval(sFormula).toString() != null ? 
                    String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString()))) : "0";            
            String sToGive = Double.toString(dAmount - Double.parseDouble(sTakenAmount));
                        
            String sBOrA = cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            String sOperation = cbOperation.getSelectionModel().getSelectedItem();
            
            tbList.getItems().add(new AllDetailsBean(sBOrA, sOperation, "", 
                    CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sMaterialType, "",
                    String.valueOf(dAmount), sTakenAmount, sTakenAmount, sToGive, "", "", "", "", true, ""));
            
            closeDateRestriction();
            calculateTotalAmtToBeRecieved();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCloseHeaderValuesToFields(HashMap<String, String> headerValues, String sInterestType, 
            String[] sReduceDatas, String[] sMinimumDatas)
    {
        try {

            double dTakenDaysOrMonths = 0;
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = CommonConstants.DATETIMEFORMATTER.format(LocalDate.parse(headerValues.get("OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue());
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            String sMaterial = cbMaterialType.getSelectionModel().getSelectedItem();
            String[] sNoticeValues = dbOp.getNoticeValues();            
            
            if("MONTH".equals(sInterestType)) {
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        dTakenDaysOrMonths = dTakenMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        dTakenDaysOrMonths = dTakenMonths;
                        sTakenMonths = Double.toString(dTakenMonths);                       
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        dTakenDaysOrMonths = dTakenMonths;
                        sTakenMonths = Double.toString(dTakenMonths);                       
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            } else if("DAY".equals(sInterestType)) {
                                
                if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                           
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                            
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    dTakenDaysOrMonths = Double.parseDouble(sTakenDays);
                } else if("DAYS".equals(sReduceDatas[1])) {
                    
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);

                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                           
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                            
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    dTakenDaysOrMonths = Double.parseDouble(sTakenDays);
                }
            }
            
            String sFormula = dbOp.getFormula(CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue()), Double.parseDouble(headerValues.get("AMOUNT")), sMaterial);
            String sFineFormula = sFormula;
            String[][] replacements = {{"AMOUNT", headerValues.get("AMOUNT")},
                                        {"INTEREST", headerValues.get("INTEREST")},
                                        {"DOCUMENT_CHARGE", headerValues.get("DOCUMENT_CHARGE")},
                                        {"TAKEN_MONTHS", sTakenMonths},
                                        {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            
            // other charges section
            double dNoticeAmount = 0;
            if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sStartDate, sNoticeValues[0])) {
                dNoticeAmount = Double.parseDouble(sNoticeValues[1]);
            }                         
            String[] sFineValues = dbOp.getFineCharges(dTakenDaysOrMonths, sMaterial);
            String sFineStartingMonth = dbOp.getFineStartingMonth("GOLD");
            double dFineCharge = 0;
            String sFineTakenMonths = null; 
            
            if(sInterestType.equals(sFineValues[0])) {
                
                if(sFineValues[4].equals("ALL MONTHS")) {
                    sFineTakenMonths = sTakenMonths;
                } else if(sFineValues[4].equals("REMAINING MONTHS")) {
                    sFineTakenMonths = String.valueOf(Double.parseDouble(sTakenMonths) 
                    - Double.parseDouble(sFineStartingMonth));
                }
                
                
                String[][] fineReplacements = {{"AMOUNT", headerValues.get("AMOUNT")},
                                            {"INTEREST", sFineValues[3]},
                                            {"DOCUMENT_CHARGE", "0"},
                                            {"TAKEN_MONTHS", sFineTakenMonths},
                                            {"TAKEN_DAYS", sTakenDays}};
                for(String[] replacement: fineReplacements) {
                    sFineFormula = sFineFormula.replace(replacement[0], replacement[1]);
                }
                ScriptEngine fineEngine = new ScriptEngineManager().getEngineByExtension("js");
                dFineCharge = Double.valueOf(Math.round(Double.parseDouble(fineEngine.eval(sFineFormula).toString())));            
            }            
            
            double dTotalOtherCharges = dNoticeAmount + dFineCharge;
            
            String sToGet = Double.toString((Double.parseDouble(headerValues.get("AMOUNT")) 
                    + Double.parseDouble(sTakenAmount) + dTotalOtherCharges) 
                    - Double.parseDouble(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID")));                                    
            sRebilledTo = headerValues.get("REBILLED_TO");
            sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
            String sCombo = "OC: " + dTotalOtherCharges 
                    + ", AA: " + Double.parseDouble(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            String sBOrA = cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            String sOperation = cbOperation.getSelectionModel().getSelectedItem();
            String sBillNumber = txtClosingBillNumber.getText();
            if(sBOrA.equals("A")) {
                sToGet = headerValues.get("GOT_AMOUNT");
                sCombo = sCombo + ", Less: " +  headerValues.get("DISCOUNT_AMOUNT");
            }
            if("REBILL".equals(sOperation)) {
                sOperation = "CLOSE - " + "REBILL" + iRebillNumber;                
            }
            
            String sAmount = headerValues.get("AMOUNT") != null 
                    ? headerValues.get("AMOUNT") : "0";
            
            String sGrsWt = headerValues.get("GROSS_WEIGHT") != null 
                    ? headerValues.get("GROSS_WEIGHT") : "0";
            
            double dRatePerGm = Double.parseDouble(sAmount)/Double.parseDouble(sGrsWt);
            
            tbList.getItems().add(new AllDetailsBean(sBOrA, sOperation, sBillNumber, 
                    headerValues.get("OPENING_DATE"), sMaterial, headerValues.get("ITEMS"),
                    sAmount, headerValues.get("INTEREST"), 
                    sTakenAmount, sToGet, sCombo, sRepledgeBillId, 
                    headerValues.get("CUSTOMER_NAME"), sGrsWt, true, String.valueOf(Math.round(dRatePerGm))));
            
            closeDateRestriction();
            calculateTotalAmtToBeRecieved();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void calculateTotalAmtToBeRecieved() {
    
        double dOpenIntrAmt = 0;
        double dOpenIntr = 0;
        double dCloseIntrAmt = 0; 
        double dCloseIntr = 0; 
        double dTotWt = 0; 
        double dTotIntrAmt = 0;          
        double dTotOpenCapAmt = 0;
        double dTotCloseCapAmt = 0;
        boolean isRebillWorkDone = false;
        
        for(AllDetailsBean bean : tbList.getItems()) {
            if(bean.isBChecked()) {
                if(bean.getSOperation().equals("BILL CLOSING")) {                 
                    dTotCloseCapAmt = dTotCloseCapAmt + Double.parseDouble(bean.getSAmount());
                    dTotIntrAmt = dTotIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                    dCloseIntr = dCloseIntr + Double.parseDouble(bean.getSInterestedAmt());
                    dCloseIntrAmt = dCloseIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                    dTotWt = dTotWt + Double.parseDouble(bean.getSGrWt());
                } else if(bean.getSOperation().equals("BILL OPENING")) {
                    dTotOpenCapAmt = dTotOpenCapAmt + Double.parseDouble(bean.getSAmount());
                    dTotIntrAmt = dTotIntrAmt - Double.parseDouble(bean.getSTotalInterestedAmt());
                    dOpenIntr = dOpenIntr + Double.parseDouble(bean.getSInterestedAmt());
                    dOpenIntrAmt = dOpenIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                } else if(bean.getSOperation().contains("REBILL")) {
                    isRebillWorkDone = true;
                    if(bean.getSOperation().contains("OPEN")) {
                        dTotOpenCapAmt = dTotOpenCapAmt + Double.parseDouble(bean.getSAmount());
                        dTotIntrAmt = dTotIntrAmt - Double.parseDouble(bean.getSTotalInterestedAmt());
                        dOpenIntr = dOpenIntr + Double.parseDouble(bean.getSInterestedAmt());
                        dOpenIntrAmt = dOpenIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                    } else if(bean.getSOperation().contains("CLOSE")) {                    
                        dTotCloseCapAmt = dTotCloseCapAmt + Double.parseDouble(bean.getSAmount());
                        dTotIntrAmt = dTotIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt()); 
                        dCloseIntr = dCloseIntr + Double.parseDouble(bean.getSInterestedAmt());
                        dCloseIntrAmt = dCloseIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                    }
                }
            }
        }
        
        String sOpen = Double.toString(dOpenIntrAmt);
        String sOpenIntr = Double.toString(dOpenIntr);
        String sClose = Double.toString(dCloseIntrAmt);
        String sCloseIntr = Double.toString(dCloseIntr);
        String sTotWt = Double.toString(dTotWt);
        String sTotalAmtToGet = Double.toString(dTotIntrAmt);
        String sTotOpenCapAmt = Double.toString(dTotOpenCapAmt);
        String sTotCloseCapAmt = Double.toString(dTotCloseCapAmt);        
        if(dTotIntrAmt > 0) {
            lbAmtToGet.setText("Amount to Get:");
            lbAmtToGet.setStyle("-fx-background-color: #008000");
            txtAmtToGet.setStyle("-fx-background-color: #008000");
        } else {
            sTotalAmtToGet = sTotalAmtToGet.replace("-", "");
            lbAmtToGet.setText("Amount to Give:");     
            lbAmtToGet.setStyle("-fx-background-color: #cc0000");
            txtAmtToGet.setStyle("-fx-background-color: #cc0000");
        }
        
        String sTotalRatePerGm = String.valueOf(Math.round(dCloseIntrAmt / dTotWt));
        
        txtOpenCapital.setText(sTotOpenCapAmt);
        txtCloseCapital.setText(sTotCloseCapAmt);
        txtOpenInterest.setText(sOpenIntr);
        txtCloseInterest.setText(sCloseIntr);
        txtOpenInterested.setText(sOpen);
        txtCloseInterested.setText(sClose);
        txtTotalWt.setText(sTotWt);  
        txtTotalRatePerGm.setText(sTotalRatePerGm);
        txtAmtToGet.setText(sTotalAmtToGet);        
    }
    
    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }


    @FXML
    private void cbBeforeAfterOnAction(ActionEvent event) {
        Platform.runLater(()->{
            addRemoveComponents();
        });
    }

    @FXML
    private void tbListOnMouseClicked(MouseEvent event) throws SQLException {
        
        int index = tbList.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sMaterialType = tbList.getItems().get(index).getSMaterial();

            if("GOLD".equals(sMaterialType)) {

                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }

                GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
                if(tbList.getItems().get(index).getSBOrA().equals("B")) {
                    gon.closeBill(tbList.getItems().get(index).getSBillNumber(), false);
                } else {
                    gon.viewBill(tbList.getItems().get(index).getSBillNumber());
                }

                dialog.setTitle("Gold Bill Closing");      
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

            } else {

                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillClosingScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }

                SilverBillClosingController gon = (SilverBillClosingController) loader.getController();
                if(tbList.getItems().get(index).getSBOrA().equals("B")) {
                    gon.closeBill(tbList.getItems().get(index).getSBillNumber(), false);
                } else {
                    gon.viewBill(tbList.getItems().get(index).getSBillNumber());
                }

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

            tbList.getItems().get(index).setBChecked(!tbList.getItems().get(index).getBCheckedProperty());

            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                currentGoldPrefix = billRowAndNumber[1];
                currentGoldBillOpenNumber = Integer.parseInt(billRowAndNumber[2]);                
            }
            String[] sbillRowAndNumber = dbOp.getSilverCurrentBillNumber();
            if(sbillRowAndNumber != null) {
                currentSilverPrefix = sbillRowAndNumber[1];
                currentSilverBillOpenNumber = Integer.parseInt(sbillRowAndNumber[2]);                
            }
            
            ArrayList<AllDetailsBean> removeList = new ArrayList<>();
            ArrayList<AllDetailsBean> addList = new ArrayList<>();
            
            for(AllDetailsBean bean : tbList.getItems()) {
                removeList.add(bean);
                if(bean.isBChecked()) {
                    if(bean.getSOperation().equals("BILL OPENING") && bean.getSBOrA().equals("B")) {
                        if(cbMaterialType.getValue().equals("GOLD")) {
                            bean.setSBillNumber(currentGoldPrefix + currentGoldBillOpenNumber);                            
                            currentGoldBillOpenNumber++;
                        } else if(cbMaterialType.getValue().equals("SILVER")) {
                            bean.setSBillNumber(currentSilverPrefix + currentSilverBillOpenNumber);
                            currentSilverBillOpenNumber++;
                        }
                    }            
                } else {
                    if(bean.getSOperation().equals("BILL OPENING") && bean.getSBOrA().equals("B")) {
                        bean.setSBillNumber("");
                    }
                }
                addList.add(bean);
            }
            tbList.getItems().removeAll(removeList);
            tbList.getItems().addAll(addList);
            calculateTotalAmtToBeRecieved();
        }                   
    }

    @FXML
    private void txtAmountOnAction(ActionEvent event) {
        double dAmt = Double.parseDouble(txtAmount.getText());
        String sMaterial = cbMaterialType.getSelectionModel().getSelectedItem();
        if(cbOperation.getSelectionModel().getSelectedItem().equals("BILL OPENING")) {
            setAmountRelatedText(dAmt, sMaterial);
        } else if(cbOperation.getSelectionModel().getSelectedItem().equals("REBILL")) {
            if(cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE")) {
                iRebillNumber++;
                txtClosingBillNumberOnAction(null);
                setAmountRelatedText(dAmt, sMaterial);
            }
        }
        txtAmount.setText("");
        txtAmount.requestFocus();
    }

    @FXML
    private void txtClosingBillNumberOnAction(ActionEvent event) {

        String sBillNumber = txtClosingBillNumber.getText();
        String sMaterial = cbMaterialType.getSelectionModel().getSelectedItem();
        
        String val = txtClosingBillNumber.getText();
        if(val != null & !val.isEmpty()) {
            
            String[] billVals = val.split("-");                            
            String[] billRowAndNumber = null;
            
            try {
                if(sMaterial.equals("GOLD")) {                
                    billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                } else if(sMaterial.equals("SILVER")) {                
                    billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                }
            } catch (SQLException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(billRowAndNumber != null && billVals.length > 1) {
                int index = billRowAndNumber[1].length();
                sBillNumber = billVals[0].substring(index);
            } else {
                sBillNumber = billVals[0];
            }            
            txtClosingBillNumber.setText(sBillNumber);
            
            if(billVals.length > 1 && billVals[1] != null && !billVals[1].isEmpty()) {
                if("G".equals(billVals[1])) {
                    sMaterial = "GOLD";
                } else if("S".equals(billVals[1])) {
                    sMaterial = "SILVER";
                }
            }
            cbMaterialType.getSelectionModel().select(sMaterial);                        
            
        }        
        try {
            
            for(AllDetailsBean bean : tbList.getItems()) {
                if(bean.getSBillNumber().equals(sBillNumber)) {
                    PopupUtil.showErrorAlert("Same bill number already exists in list.");
                    return;
                }
            }
            
            if(cbOperation.getSelectionModel().getSelectedItem().equals("BILL CLOSING")
                    || (cbOperation.getSelectionModel().getSelectedItem().equals("REBILL") 
                        && event == null)) {
                                
                HashMap<String, String> headerValues = cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") 
                        ? dbOp.getAllBillingValuesToClose(sBillNumber, sMaterial) : 
                        dbOp.getAllClosedBillingValues(sBillNumber, sMaterial);
                
                /*if(cbOperation.getSelectionModel().getSelectedItem().equals("REBILL") 
                        && event == null && cbBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER")) {
                    headerValues = dbOp.getAllBillingValuesToClose(sBillNumber, sMaterial);
                }*/
                
                String sInterestType = dbOp.getInterestType();
                String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sMaterial, "REDUCTION");
                String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sMaterial, "MINIMUM");

                if(headerValues != null)
                {
                    setCloseHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];
                    sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");                    
                } else {
                    PopupUtil.showErrorAlert("Sorry invalid bill number.");
                }
                addRemoveComponents();
            } else if(cbOperation.getSelectionModel().getSelectedItem().equals("REBILL") 
                    && event != null) { 
                if(cbBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE")) {
                    txtAmount.requestFocus();
                } else {
                    txtOpeningBillNumber.requestFocus();
                }
            } 
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }

    @FXML
    private void txtOpeningBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtOpeningBillNumber.getText();
        String sMaterial = cbMaterialType.getSelectionModel().getSelectedItem();

        try {

            for(AllDetailsBean bean : tbList.getItems()) {
                if(bean.getSBillNumber().equals(sBillNumber)) {
                    PopupUtil.showErrorAlert("Same bill number ("+ sBillNumber + ")  already exists in list.");
                    return;
                }
            }

            if(cbOperation.getSelectionModel().getSelectedItem().equals("REBILL")) {            
                if(cbBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER")) {
                    iRebillNumber++;
                    txtClosingBillNumberOnAction(null);
                }                
            }

            if((cbOperation.getSelectionModel().getSelectedItem().equals("BILL OPENING")
                    && cbBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER"))
                    || (cbOperation.getSelectionModel().getSelectedItem().equals("REBILL")
                    && cbBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER"))) {

                HashMap<String, String> headerValues = dbOpOpen.getAllHeaderValues(sBillNumber, sMaterial);

                if(headerValues != null)
                {
                    setOpenHeaderValuesToFields(headerValues, sMaterial, sBillNumber);
                } else {
                    PopupUtil.showErrorAlert("Sorry invalid bill number.");
                }

            }
            addRemoveComponents();
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btRemoveFromTableClicked(ActionEvent event) {
        tbList.getItems().removeAll(tbList.getItems());
        txtOpenInterest.setText("0");
        txtCloseInterest.setText("0");
        txtAmtToGet.setText("0");
        btDoOperation.setDisable(false);
    }

    @FXML
    private void txtAmtToGetOnAction(ActionEvent event) {
        
    }

    @FXML
    private void txtAmountOnType(KeyEvent e) {
        if(!("0123456789.".contains(e.getCharacter()))){             
            e.consume();
        }                 
    }

    public void setAllRepledgeValuesToFields(String sRepledgeId, HashMap<String, String> repledgeValues, String sInterestType, String[] sReduceDatas, String[] sMinimumDatas) {
        
        try {
            
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpRepClosingDate.getValue());
            String sMaterial = cbRepMaterialType.getSelectionModel().getSelectedItem();
            
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            
            if("MONTH".equals(sInterestType)) {                
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOpRep.getRemainingDaysAsMonths(sEndDate, sRepledgeId, Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOpRep.getRemainingDaysAsMonths(sEndDate, sRepledgeId, Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOpRep.getRemainingDaysAsMonths(sEndDate, sRepledgeId, Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);                       
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }                
            } else if("DAY".equals(sInterestType)) {
                
                if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = sMinimumDatas[0];
                        }
                    }                    
                } else if("DAYS".equals(sReduceDatas[1])) {
                    
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                }
            }
            
            String sFormula = dbOpRep.getFormula(sRepledgeId, Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT")), "GOLD");
            String[][] replacements = {{"AMOUNT", repledgeValues.get("REPLEDGE_AMOUNT")},
                {"INTEREST", repledgeValues.get("REPLEDGE_INTEREST")},
                {"DOCUMENT_CHARGE", repledgeValues.get("REPLEDGE_DOCUMENT_CHARGE")},
                {"TAKEN_MONTHS", sTakenMonths},
                {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            String sToGive;
            
            String sBOrA = cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            if(sBOrA.equals("A")) {
                sToGive = repledgeValues.get("GIVEN_AMOUNT");
            } else {
                sToGive = Double.toString((Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT")) 
                    + Double.parseDouble(sTakenAmount)));
                
            }
            String sOperation = cbRepOperation.getSelectionModel().getSelectedItem();
            String sBillNumber = null;
            if(sOperation.equals("BILL CLOSING")) {
                sBillNumber = txtRepClosingBillNumber.getText();
            } else if(sOperation.equals("GET SUSPENSE")) {
                sBillNumber = txtSuspenseBillNumber.getText();
            }
            tbRepList.getItems().add(new RepAllDetailsBean(sBOrA, sOperation, sBillNumber, 
                    sStartDate, repledgeValues.get("REPLEDGE_BILL_NUMBER"), repledgeValues.get("REPLEDGE_NAME"), 
                    sMaterial, repledgeValues.get("ITEMS"), repledgeValues.get("REPLEDGE_AMOUNT"), 
                    repledgeValues.get("REPLEDGE_INTEREST"), sTakenAmount, sToGive, 
                    repledgeValues.get("REPLEDGE_ID"), repledgeValues.get("REPLEDGE_BILL_ID"),
                    repledgeValues.get("BILL_NUMBER"), repledgeValues.get("REPLEDGE_STATUS"), true));
            
            calculateRepTotalAmtToBeRecieved();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void calculateRepTotalAmtToBeRecieved() {
    
        double dOpenIntrAmt = 0; 
        double dOpenIntr = 0; 
        double dCloseIntrAmt = 0;
        double dCloseIntr = 0;
        double dTotIntrAmt = 0;  
        double dTotOpenCap = 0;  
        double dTotCloseCap = 0;
        
        for(RepAllDetailsBean bean : tbRepList.getItems()) {
            if(bean.isBChecked()) {
                if(bean.getSOperation().equals("BILL CLOSING")) {
                    dTotCloseCap = dTotCloseCap + Double.parseDouble(bean.getSAmount());
                    dTotIntrAmt = dTotIntrAmt - Double.parseDouble(bean.getSTotalInterestedAmt());
                    dCloseIntr = dCloseIntr + Double.parseDouble(bean.getSInterestedAmt());
                    dCloseIntrAmt = dCloseIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                } else if(bean.getSOperation().equals("BILL OPENING")) {
                    dTotOpenCap = dTotOpenCap + Double.parseDouble(bean.getSAmount());
                    dTotIntrAmt = dTotIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                    dOpenIntr = dOpenIntr + Double.parseDouble(bean.getSInterestedAmt());
                    dOpenIntrAmt = dOpenIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmt());
                }
            }
        }
        
        String sOpen = Double.toString(dOpenIntrAmt);
        String sOpenIntr = Double.toString(dOpenIntr);
        String sClose = Double.toString(dCloseIntrAmt);
        String sCloseIntr = Double.toString(dCloseIntr);
        String sTotalAmtToGet = Double.toString(dTotIntrAmt);
        String sTotalOpenCap = Double.toString(dTotOpenCap);
        String sTotalCloseCap = Double.toString(dTotCloseCap);
        
        if(dTotIntrAmt > 0) {
            lbAmtToGet1.setText("Amount to Get:");
            lbAmtToGet1.setStyle("-fx-background-color: #008000");
            txtRepAmtToGet.setStyle("-fx-background-color: #008000");
        } else {
            sTotalAmtToGet = sTotalAmtToGet.replace("-", "");
            lbAmtToGet1.setText("Amount to Give:");        
            lbAmtToGet1.setStyle("-fx-background-color: #CC0000");
            txtRepAmtToGet.setStyle("-fx-background-color: #CC0000");
        }
        
        txtRepOpenCapital.setText(sTotalOpenCap);
        txtRepCloseCapital.setText(sTotalCloseCap);
        txtRepOpenInterest.setText(sOpenIntr);
        txtRepCloseInterest.setText(sCloseIntr);
        txtRepOpenInterested.setText(sOpen);
        txtRepCloseInterested.setText(sClose);
        txtRepAmtToGet.setText(sTotalAmtToGet);        
    }
    
    
    @FXML
    private void cbRepBeforeAfterOnAction(ActionEvent event) {
        Platform.runLater(()->{
            addRemoveRepComponents();
        });        
    }

    @FXML
    private void cbRepOperationOnAction(ActionEvent event) {
        Platform.runLater(()->{
            addRemoveRepComponents();
        });        
    }

    @FXML
    private void txtRepClosingBillNumberOnAction(ActionEvent event) {

        try {

            String sRepPlannerId = txtRepPlannerId.getText();

            if(tgOn.isSelected() || (tgOff.isSelected() && !dbOpRepOpen.isRepPrinted(sRepPlannerId)) ) {
                String sBillNumber = txtRepClosingBillNumber.getText();
                String sMaterial = cbRepMaterialType.getSelectionModel().getSelectedItem();

                for(RepAllDetailsBean bean : tbRepList.getItems()) {
                    if(bean.getSBillNumber().equals(sBillNumber)) {
                        PopupUtil.showErrorAlert("Same bill number already exists in list.");
                        return;
                    }
                }

                if(cbRepOperation.getSelectionModel().getSelectedItem().equals("BILL CLOSING")) {

                    if(cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE")) {
                        HashMap<String, String> headerValues = 
                                dbOpRep.getAllHeaderValuesByCompanyBillNumber(sBillNumber, sMaterial);
                        if(headerValues != null) {
                            String sRepledgeId = headerValues.get("REPLEDGE_ID");                       
                            String sInterestType = dbOpRep.getInterestType(sRepledgeId);
                            String[] sReduceDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "REDUCTION");
                            String[] sMinimumDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "MINIMUM");
                            setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                        } else {
                            PopupUtil.showErrorAlert("Sorry invalid company bill number.");
                        }                                   
                    } else {
                        HashMap<String, String> headerValues = 
                                dbOpRep.getAllClosedHeaderValuesByCompanyBillNumber(sBillNumber, sMaterial);                
                        if(headerValues == null) {
                            headerValues = dbOpRep.getAllClosedHeaderValuesByCompanyReBillNumber(sBillNumber, "GOLD");
                        }
                        if(headerValues != null) {
                            String sRepledgeId = headerValues.get("REPLEDGE_ID");                       
                            String sInterestType = dbOpRep.getInterestType(sRepledgeId);
                            String[] sReduceDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "REDUCTION");
                            String[] sMinimumDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "MINIMUM");
                            setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                        } else {
                            PopupUtil.showErrorAlert("Sorry invalid company bill number.");
                        }                    
                    }                                
                }
                addRemoveRepComponents();            
            } else {
                PopupUtil.showInfoAlert("Operation cannot be done after printed.");
            }        
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    public void setRepOpenHeaderValuesToFields(HashMap<String, String> repledgeValues, String sMaterialType)
    {
            String sStartDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
            String sMaterial = cbRepMaterialType.getSelectionModel().getSelectedItem();
        
            String sBOrA = cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            String sOperation = cbRepOperation.getSelectionModel().getSelectedItem();
            String sBillNumber = txtRepOpeningBillNumber.getText();
            
            if(cbRepBeforeAfter.getSelectionModel()
                    .getSelectedItem().equals("AFTER")) {
                tbRepList.getItems().add(new RepAllDetailsBean(sBOrA, sOperation, sBillNumber, 
                        sStartDate, repledgeValues.get("REPLEDGE_BILL_NUMBER"), repledgeValues.get("REPLEDGE_NAME"), 
                        sMaterial, repledgeValues.get("ITEMS"), repledgeValues.get("REPLEDGE_AMOUNT"), 
                        repledgeValues.get("REPLEDGE_INTEREST"), repledgeValues.get("REPLEDGE_OPEN_TAKEN_AMOUNT"), 
                        repledgeValues.get("REPLEDGE_OPEN_TOGET_AMOUNT"),repledgeValues.get("REPLEDGE_ID"), 
                        repledgeValues.get("BILL_NUMBER"),
                        repledgeValues.get("REPLEDGE_BILL_ID"), repledgeValues.get("REPLEDGE_STATUS"), true));
            } else {
                
                try {
                    
                    int index = cbRepNames.getSelectionModel().getSelectedIndex();
                    String sRepledgeId = repledgeNames.getRow(index).getColumn(0).toString();
                    String sRepledgeName = repledgeNames.getRow(index).getColumn(1).toString();
                    double dAmount = 0;
                    
                    if(!chChangeRepAmount.isSelected()) {
                        dAmount = Double.parseDouble(
                                repledgeValues.get("AMOUNT"));
                    } else {
                        dAmount = Double.parseDouble(
                                txtRepAmount.getText());
                    }
                    
                    String sInterest = dbOpRepOpen.getInterest(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sRepledgeId, dAmount, "GOLD").trim();
                    String sDocumentCharge = dbOpRepOpen.getDocumentCharge(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sRepledgeId, dAmount, "GOLD").trim();
                    String sFormula = dbOpRepOpen.getFormula(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sRepledgeId, dAmount, "GOLD");

                    String[][] replacements = {{"AMOUNT", String.valueOf(dAmount)}, 
                                               {"INTEREST", sInterest},
                                               {"DOCUMENT_CHARGE", sDocumentCharge}};            

                    for(String[] replacement: replacements) {
                        sFormula = sFormula.replace(replacement[0], replacement[1]);
                    }

                    ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
                    String sTakenAmount = engine.eval(sFormula).toString() != null ? 
                            String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString()))) : "0";            
                    String sToGive = Double.toString(dAmount - Double.parseDouble(sTakenAmount));
                    String sAmount = String.format("%.0f", dAmount);
                    //sBillNumber = "NOT MENTIONED";
                    tbRepList.getItems().add(new RepAllDetailsBean(sBOrA, sOperation, sBillNumber, 
                            CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), "", sRepledgeName, 
                            sMaterialType, "", sAmount, sInterest, sTakenAmount,
                            sToGive,sRepledgeId, "", "", "", true));
                } catch (SQLException ex) {
                    Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ScriptException ex) {
                    Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
                }                                        
            }
    
            calculateRepTotalAmtToBeRecieved();
    }
    
    @FXML
    private void txtRepOpeningBillNumberOnAction(ActionEvent event) {
        
        if(!chChangeRepAmount.isSelected() 
                || isFromAmountField)
        {
            try {
                String sRepPlannerId = txtRepPlannerId.getText();        
                if(tgOn.isSelected() || (tgOff.isSelected() && !dbOpRepOpen.isRepPrinted(sRepPlannerId)) ) {

                    String sBillNumber = txtRepOpeningBillNumber.getText();
                    String sMaterial = cbRepMaterialType.getSelectionModel().getSelectedItem();

                    for(RepAllDetailsBean bean : tbRepList.getItems()) {
                        if(bean.getSBillNumber().equals(sBillNumber)) {
                            PopupUtil.showErrorAlert("Same bill number already exists in list.");
                            return;
                        }
                    }

                    if((cbRepOperation.getSelectionModel().getSelectedItem().equals("BILL OPENING")
                            && cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("AFTER"))) {

                        HashMap<String, String> headerValues = dbOpRepOpen.getAllHeaderValuesByCompanyBillNumber(sBillNumber, sMaterial);

                        if(headerValues != null)
                        {
                            setRepOpenHeaderValuesToFields(headerValues, sMaterial);
                        } else {
                            PopupUtil.showErrorAlert("Sorry invalid bill number.");
                        }
                    } else if((cbRepOperation.getSelectionModel().getSelectedItem().equals("BILL OPENING")
                            && cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE"))) {

                        HashMap<String, String> headerValues 
                                = dbOpRepOpen.getAllCompanyValues(
                                        sBillNumber, sMaterial);

                        if(headerValues != null)
                        {
                            setRepOpenHeaderValuesToFields(
                                    headerValues, sMaterial);
                        } else {
                            PopupUtil.showErrorAlert("Sorry invalid bill number.");
                        }                
                    }
                    addRemoveRepComponents();            
                } else {
                    PopupUtil.showInfoAlert("Operation cannot be done after printed.");
                }        
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }         
            isFromAmountField = false;
        }
        else 
        {
            txtRepAmount.requestFocus();
            txtRepAmount.positionCaret(
                    txtRepAmount.getText().length()); 
        }
    }

    public void setRepAmountRelatedText(
            String sRepledgeId, String sRepledgeName, 
            double dAmount, String sMaterialType) {
    
        try {

            String sInterest = dbOpRepOpen.getInterest(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sRepledgeId, dAmount, "GOLD").trim();
            String sDocumentCharge = dbOpRepOpen.getDocumentCharge(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sRepledgeId, dAmount, "GOLD").trim();
            String sFormula = dbOpRepOpen.getFormula(CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sRepledgeId, dAmount, "GOLD");
            
            String[][] replacements = {{"AMOUNT", 
                String.valueOf(dAmount)}, 
                                       {"INTEREST", sInterest},
                                       {"DOCUMENT_CHARGE", 
                                           sDocumentCharge}};            

            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], 
                        replacement[1]);
            }

            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = engine.eval(sFormula).toString() != null ? 
                    String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString()))) : "0";            
            String sToGive = Double.toString(dAmount - Double.parseDouble(sTakenAmount));
                        
            String sBOrA = cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE") ? "B" : "A";
            String sOperation = cbRepOperation.getSelectionModel().getSelectedItem();
            String sAmount = String.format("%.0f", dAmount);
            
            tbRepList.getItems().add(new RepAllDetailsBean(sBOrA, sOperation, "NOT MENTIONED", 
                    CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), "", sRepledgeName, 
                    sMaterialType, "", sAmount, sInterest, sTakenAmount,
                    sToGive,sRepledgeId, "", "", "", true));
            
            calculateRepTotalAmtToBeRecieved();
            
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void txtRepAmountOnAction(ActionEvent event) {
        
        if(!chChangeRepAmount.isSelected()) 
        {
            try {
                String sRepPlannerId = txtRepPlannerId.getText();        
                if(tgOn.isSelected() 
                        || (tgOff.isSelected() 
                        && !dbOpRepOpen.isRepPrinted(sRepPlannerId))) {
                    int index = cbRepNames.getSelectionModel().getSelectedIndex();
                    String sRepId = repledgeNames.getRow(index).getColumn(0).toString();
                    String sRepName = repledgeNames.getRow(index).getColumn(1).toString();
                    double dAmt = Double.parseDouble(txtRepAmount.getText());
                    setRepAmountRelatedText(sRepId, sRepName, dAmt, cbRepMaterialType.getValue());
                    txtRepAmount.setText("");
                } else {
                    PopupUtil.showInfoAlert("Operation cannot be done after printed.");
                }        
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            isFromAmountField = true;
            txtRepOpeningBillNumberOnAction(event);
        }
    }

    @FXML
    private void btRepRemoveFromTableClicked(ActionEvent event) {
        tbRepList.getItems().removeAll(tbRepList.getItems());
        txtRepOpenInterest.setText("0");
        txtRepCloseInterest.setText("0");
        txtRepAmtToGet.setText("0");        
    }

    @FXML
    private void txtRepAmtToGetOnAction(ActionEvent event) {
        
        double dGotAmt = Double.parseDouble(txtRepAmtToGet.getText());
        
        if(dGotAmt > 0) {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("RecievedAndBalanceAmtDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            RecievedAndBalanceAmtDialog gon = (RecievedAndBalanceAmtDialog) loader.getController();
            gon.setParent(this);
            gon.setInitValues("REPLEDGE");
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showErrorAlert("Got Amount should be greater than zero.");
        }                
    }

    @FXML
    private void tbRepListOnMouseClicked(MouseEvent event) {
        
        int index = tbRepList.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sOperation = tbRepList.getItems().get(index).getSOperation();

            if("BILL CLOSING".equals(sOperation)) {

                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }

                RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
                if(tbRepList.getItems().get(index).getSBOrA().equals("B")) {
                    gon.closeBill(tbRepList.getItems().get(index).getSRepBillId(), true);
                } else {
                    gon.viewBill(tbRepList.getItems().get(index).getSRepBillId());
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
        }  else if(event.getClickCount() == 1) {
            tbRepList.getItems().get(index).setBChecked(!tbRepList.getItems().get(index).getBCheckedProperty());
            calculateRepTotalAmtToBeRecieved();
        }                
    }

    @FXML
    private void cbMaterialTypeOnAction(ActionEvent event) {
        Platform.runLater(()->{
            addRemoveComponents();
        });        
    }

    @FXML
    private void cbRepNamesOnAction(ActionEvent event) {        
        Platform.runLater(() -> {            
            iLastSelectedIndex = cbRepNames.getSelectionModel().getSelectedIndex();
            txtRepOpeningBillNumber.requestFocus();
            txtRepOpeningBillNumber.positionCaret(txtRepOpeningBillNumber.getText().length());
        });
    }

    @FXML
    private void repTabClicked(Event event) {
        Platform.runLater(() -> {
            txtRepClosingBillNumber.requestFocus();
            txtRepClosingBillNumber.positionCaret(txtRepClosingBillNumber.getText().length());
        });        
    }

    @FXML
    private void companyTabClicked(Event event) {
        Platform.runLater(() -> {
            txtClosingBillNumber.requestFocus();
            txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());
        });        
    }

    private void selectOrDeSelectAll(TableView<AllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }

    private void selectOrDeSelectAllRep(TableView<RepAllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }
    
    @FXML
    private void btCompSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbList, true);
        calculateTotalAmtToBeRecieved();
    }

    @FXML
    private void btCompDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbList, false);
        calculateTotalAmtToBeRecieved();
    }

    @FXML
    private void btRepSelectAllOnAction(ActionEvent event) {
        if(tgOn.isSelected()) {
            selectOrDeSelectAllRep(tbRepList, true);
            calculateRepTotalAmtToBeRecieved();
        } else {
            PopupUtil.showInfoAlert("Operation can be done only on save mode.");
        }
    }

    @FXML
    private void btRepDeSelectAllOnAction(ActionEvent event) {
        if(tgOn.isSelected()) {
            selectOrDeSelectAllRep(tbRepList, false);
            calculateRepTotalAmtToBeRecieved();
        } else {
            PopupUtil.showInfoAlert("Operation can be done only on save mode.");
        }        
    }

    @FXML
    private void btPrintSelectedOnAction(ActionEvent event) {
        if(tgOff.isSelected()) {
            if(txtRepAmtToGet.getText() != null 
                    && !txtRepAmtToGet.getText().isEmpty()) {

                try {
                    String sFileName = CommonConstants.REPORT_LOCATION 
                            + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                            + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                            + "\\billcalcrep.jasper";                        

                    List<RepPrintBean> ParamList = new ArrayList<>();	            
                    for(RepAllDetailsBean bean : tbRepList.getItems()) {                
                        if(bean.isBChecked() 
                                && bean.getSOperation()
                                        .equals("BILL CLOSING")) {
                            RepPrintBean nBean = new RepPrintBean();
                            if(bean.getSStatus().equals("SUSPENSE")) {
                                nBean.setSOperation("CLOSE \n (ALR-SUS)");
                            } else {
                                nBean.setSOperation("CLOSE");
                            }
                            nBean.setSBillNumber(bean.getSBillNumber());
                            String sBarcodeVal = bean.getSBillNumber() 
                                    + "<->RBC<->" + "CLOSE";
                            nBean.setSBarcode(sBarcodeVal);
                            nBean.setSOpeningDate(bean.getSOpeningDate());
                            nBean.setSRepBillNumber(bean.getSRepBillNumber());
                            nBean.setSRepName(bean.getSRepName() + "\n(" + bean.getSActualBillNumber() +")");
                            nBean.setDAmount(Double.parseDouble(bean.getSAmount()));
                            nBean.setDInterest(Double.parseDouble(bean.getSInterestedAmt()));
                            nBean.setDTotalInterested(Double.parseDouble(bean.getSTotalInterestedAmt()));
                            ParamList.add(nBean);
                        }
                    }

                    /*if(ParamList.size() == 1 && ParamList.get(0).getSOperation().equals("CLOSE")) {
                        RepPrintBean nEmptyBean = new RepPrintBean();
                        ParamList.add(nEmptyBean.getEmptyValObject());TIRU  
                    }*/

                    for(RepAllDetailsBean bean : tbRepList.getItems()) {                
                        if(bean.isBChecked() 
                                && bean.getSOperation()
                                        .equals("BILL OPENING")) {
                            RepPrintBean nBean = new RepPrintBean();
                            nBean.setSOperation("OPEN");
                            nBean.setSBillNumber(bean.getSBillNumber());
                            nBean.setSBarcode(bean.getSBillNumber() 
                                    + "<->RBO<->" + bean.getSRepName()
                                    + "<->" + bean.getSAmount());
                            nBean.setSOpeningDate(bean.getSOpeningDate());
                            nBean.setSRepBillNumber(bean.getSRepBillNumber());
                            nBean.setSRepName(bean.getSRepName());
                            nBean.setDAmount(Double.parseDouble(bean.getSAmount()));
                            nBean.setDInterest(Double.parseDouble(bean.getSInterestedAmt()));
                            nBean.setDTotalInterested(Double.parseDouble(bean.getSTotalInterestedAmt()));
                            ParamList.add(nBean);
                        }
                    }

                    /*if((ParamList.size() == 1 && ParamList.get(0).getSOperation().equals("OPEN"))
                            || (ParamList.size() == 3 && ParamList.get(1).getSOperation().equals("OPEN"))) {
                        RepPrintBean nEmptyBean = new RepPrintBean();
                        ParamList.add(nEmptyBean.getEmptyValObject());
                    }*/

                    for(RepAllDetailsBean bean : tbRepList.getItems()) {                
                        if(bean.isBChecked() 
                                && bean.getSOperation().equals("GET SUSPENSE")) {
                            RepPrintBean nBean = new RepPrintBean();
                            nBean.setSOperation("GET SUSPENSE");
                            nBean.setSBillNumber(bean.getSBillNumber());
                            String sBarcodeVal = bean.getSBillNumber() 
                                    + "<->RBC<->" + "GET SUSPENSE";
                            nBean.setSBarcode(sBarcodeVal);
                            nBean.setSOpeningDate(bean.getSOpeningDate());
                            nBean.setSRepBillNumber(bean.getSRepBillNumber());
                            nBean.setSRepName(bean.getSRepName() + "\n(" + bean.getSActualBillNumber() +")");
                            nBean.setDAmount(Double.parseDouble(bean.getSAmount()));
                            nBean.setDInterest(Double.parseDouble(bean.getSInterestedAmt()));
                            nBean.setDTotalInterested(Double.parseDouble(bean.getSTotalInterestedAmt()));
                            ParamList.add(nBean);
                        }
                    }

                    JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("BillCalcCollectionBeanParam", tableList);
                    parameters.put("TODAYSDATE", DateRelatedCalculations.getTodaysDate());
                    parameters.put("PLANNERID", txtRepPlannerId.getText());
                    
                    parameters.put("CLOSINGCAPITAL", Double.parseDouble((txtRepCloseCapital.getText() != null) ? txtRepCloseCapital.getText() : "0"));
                    parameters.put("OPENINGCAPITAL", Double.parseDouble((txtRepOpenCapital.getText() != null) ? txtRepOpenCapital.getText() : "0"));
                    parameters.put("TOGIVEORGETVAL", Double.parseDouble((txtRepAmtToGet.getText() != null) ? txtRepAmtToGet.getText() : "0"));
                    parameters.put("TOGIVEORGETLABEL", lbAmtToGet1.getText());
                    parameters.put("CLOSEINTEREST", Double.parseDouble((txtRepCloseInterest.getText() != null) ? txtRepCloseInterest.getText() : "0"));
                    parameters.put("TOTALCLOSE", Double.parseDouble((txtRepCloseInterested.getText() != null) ? txtRepCloseInterested.getText() : "0"));
                    parameters.put("OPENINTEREST", Double.parseDouble((txtRepOpenInterest.getText() != null) ? txtRepOpenInterest.getText() : "0"));
                    parameters.put("TOTALOPEN", Double.parseDouble((txtRepOpenInterested.getText() != null) ? txtRepOpenInterested.getText() : "0"));
                    parameters.put("CLOSINGDATE", CommonConstants.DATETIMEFORMATTER.format(dpRepClosingDate.getValue()) 
                            + " " + DateRelatedCalculations.getCurrentTime());
                    parameters.put("PREPAREDUSER", CommonConstants.EMP_NAME);
                    parameters.put("EXECUTEDUSER", "");

                    JasperPrint print = null;
                    NoticeUtil noticeUtil = new NoticeUtil();
                    noticeUtil.generateNoticeOperation(sFileName, parameters);
                    
                    dbOpRep.setRepHeaderAsPrinted(txtRepPlannerId.getText());
                    btUpdateBill.setDisable(true);
                    
                } catch (JRException ex) {
                    PopupUtil.showErrorAlert(ex.getMessage());
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
                }   
            } else {
                PopupUtil.showInfoAlert("Not any details filled to print the records. ");
            }
        } else {
            PopupUtil.showInfoAlert("Operation can be done only on update mode.");
        }                
    }

    @FXML
    private void btRepPendingRecordsOnAction(ActionEvent event) {      
        try {
            String sRepPlannerId = txtRepPlannerId.getText();        
            if(tgOn.isSelected() || (tgOff.isSelected() && !dbOpRepOpen.isRepPrinted(sRepPlannerId)) ) {
                DataTable nYLockedValues = dbOpRep.getRNYDeliveredTableValue("GOLD");
                if(nYLockedValues != null) {
                    beforeRepClose(nYLockedValues);
                }
            } else {
                PopupUtil.showInfoAlert("Operation cannot be done after printed.");
            }        
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
            } catch (SQLException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
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
                addRemoveComponents();
            } catch (Exception ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                
    }

    @FXML
    private void txtSuspenseBillNumberOnAction(ActionEvent event) {
        
        try {
            String sRepPlannerId = txtRepPlannerId.getText();        
            if(tgOn.isSelected() || (tgOff.isSelected() && !dbOpRepOpen.isRepPrinted(sRepPlannerId)) ) {
            
            String sBillNumber = txtSuspenseBillNumber.getText();
            String sMaterial = cbRepMaterialType.getSelectionModel().getSelectedItem();

            for(RepAllDetailsBean bean : tbRepList.getItems()) {
                if(bean.getSBillNumber().equals(sBillNumber)) {
                    if(bean.getSOperation().equals("BILL CLOSING")) {
                        tbRepList.getItems().remove(bean);
                        bean.setSOperation("GET SUSPENSE");                        
                        tbRepList.getItems().add(bean);  
                        calculateRepTotalAmtToBeRecieved();
                        return;
                    } else {                   
                        PopupUtil.showErrorAlert("Same bill number already exists in list.");
                        return;
                    }
                }
            }

            if(cbRepOperation.getSelectionModel().getSelectedItem().equals("GET SUSPENSE")) {

                if(cbRepBeforeAfter.getSelectionModel().getSelectedItem().equals("BEFORE")) {
                    HashMap<String, String> headerValues = dbOpRep.getAllHeaderValuesByCompanyBillNumber(sBillNumber, sMaterial);
                    if(headerValues != null) {
                        String sRepledgeId = headerValues.get("REPLEDGE_ID");                       
                        String sInterestType = dbOpRep.getInterestType(sRepledgeId);
                        String[] sReduceDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "REDUCTION");
                        String[] sMinimumDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "MINIMUM");
                        setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    } else {
                        PopupUtil.showErrorAlert("Sorry invalid company bill number.");
                    }                                   
                } else {
                    HashMap<String, String> headerValues = 
                            dbOpRep.getAllClosedHeaderValuesByCompanyBillNumber(sBillNumber, sMaterial);                
                    if(headerValues == null) {
                        headerValues = dbOpRep.getAllClosedHeaderValuesByCompanyReBillNumber(sBillNumber, "GOLD");
                    }
                    if(headerValues != null) {
                        String sRepledgeId = headerValues.get("REPLEDGE_ID");                       
                        String sInterestType = dbOpRep.getInterestType(sRepledgeId);
                        String[] sReduceDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "REDUCTION");
                        String[] sMinimumDatas = dbOpRep.getReduceOrMinimumDaysOrMonths(sRepledgeId, sMaterial, "MINIMUM");
                        setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    } else {
                        PopupUtil.showErrorAlert("Sorry invalid company bill number.");
                    }                    
                }                                
            }

            addRemoveRepComponents();            
            } else {
                PopupUtil.showInfoAlert("Operation cannot be done after printed.");
            }        
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }

    @FXML
    private void cbMultiOperationOnAction(ActionEvent event) {
        switch (cbMultiOperation.getSelectionModel().getSelectedItem()) {
            case "MULTI CLOSE":
                cbOperation.getItems().removeAll(cbOperation.getItems());
                cbOperation.getItems().addAll("BILL CLOSING");
                cbOperation.setValue("BILL CLOSING");
                ArrayList<AllDetailsBean> openList = new ArrayList<>();
                for(AllDetailsBean bean : tbList.getItems()) {
                    if(bean.getSOperation().equals("BILL OPENING")) {
                        openList.add(bean);
                    }
                }   tbList.getItems().removeAll(openList);
                break;
            case "MULTI OPERATION":
                cbOperation.getItems().removeAll(cbOperation.getItems());
                cbOperation.getItems().addAll("BILL CLOSING", "BILL OPENING");
                cbOperation.setValue("BILL CLOSING");
                break;
            case "REBILLED MULTIPLE":
                cbOperation.getItems().removeAll(cbOperation.getItems());                                
                cbOperation.getItems().addAll("BILL CLOSING", "BILL OPENING");
                cbOperation.setValue("BILL CLOSING");
                break;
            default:
                break;
        }
    }

    @FXML
    private void btDoOperationClicked(ActionEvent event) {
        if(cbMultiOperation.getSelectionModel().getSelectedItem().equals("MULTI CLOSE")) {
            denominationWork(event);            
        } else if(cbMultiOperation.getSelectionModel().getSelectedItem().equals("MULTI OPERATION")) {
            denominationWork(event);            
        } else if(cbMultiOperation.getSelectionModel().getSelectedItem().equals("REBILLED MULTIPLE")) {
            rebilledMultipleOperationWork(event);
        } else {
            PopupUtil.showInfoAlert(event, "Choose any operation type to do the operation.");
        }
    }

    private void rebilledMultipleOperationWork(ActionEvent event) {

        int countOfBillClose = 0;
        int countOfBillOpen = 0;
        boolean valid = false;
        
        for(AllDetailsBean bean : tbList.getItems()) {
            if(bean.isBChecked() 
                    && bean.getSOperation().equals("BILL OPENING")) {
                countOfBillOpen++;
            }
            if(bean.isBChecked() 
                    && bean.getSOperation().equals("BILL CLOSING")) {
                countOfBillClose++;
            }            
        }

        if(countOfBillClose <= 1) {
            PopupUtil.showErrorAlert(event, "More than one bill closing operation should be happen in Rebilled-Multiple.");        
            valid = false;
        } else if(countOfBillClose > 1) {
            valid = true;
        }        
        
        if(valid) {
            if(countOfBillOpen == 1) {
                valid = true;
            } else if(countOfBillOpen < 1) {
                PopupUtil.showErrorAlert(event, "Atleast one Bill Opening amount must be given for Rebilled-Multiple.");        
                valid = false;
            } else if(countOfBillOpen > 1) {
                PopupUtil.showErrorAlert(event, "Only one Bill Opening can be done in Rebilled-Multiple.");        
                valid = false;
            }        
        }
        
        if(valid) {
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
            gon.doRebilledMultipleOperation(tbList.getItems(), ivCustomer.getImage(), null);

            dialog.setTitle("Gold Bill Closing");      
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
    
    
    private void denominationWork(ActionEvent event) {
        
        double dGotAmt = Double.parseDouble(txtAmtToGet.getText());        

        if(dGotAmt > 0) {     

            try {
                
                DenominationBean bean = null;

                if(cbMultiOperation.getValue().equals("MULTI CLOSE")) {                                
                    bean = doMultiCloseDenominationWork(event);
                } else if(cbMultiOperation.getValue().equals("MULTI OPERATION")) {
                    bean = doMultiOperationDenominationWork(event);
                }   
                
                if(bean != null) {
                    showDenominationScreen(event, bean);
                }
            } catch (SQLException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        }
            
    }

    private void showDenominationScreen(ActionEvent event, DenominationBean bean) throws SQLException {

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
        gon.setInitValues(this, bean);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(180);
        dialog.setY(100);
        dialog.setHeight(520);                
        dialog.setTitle("OPERATION: " + bean.getOperationName()
                + ", BILL NUMBERS: " + bean.getBillNumbers()
                + ", BILL MATERIAL TYPES: " + bean.getMaterialTypes());
        dialog.setResizable(false);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
        
    }
    
    private DenominationBean doMultiOperationDenominationWork(ActionEvent event) throws SQLException {

        String sOperationName = CommonConstants.MULTI_OPERATION_OPERATION;
        StringBuilder multiOperationBillNumbers = new StringBuilder();
        StringBuilder multiOperationMaterials = new StringBuilder();
            
        for(AllDetailsBean bean : tbList.getItems()) {
            if(bean.isBChecked() && bean.getSOperation().equals("BILL CLOSING")) {
                multiOperationBillNumbers.append(bean.getSBillNumber() + "-CLOSE");
                multiOperationBillNumbers.append(",");
                multiOperationMaterials.append(bean.getSMaterial());
                multiOperationMaterials.append(",");
            } else if(bean.isBChecked() && bean.getSOperation().equals("BILL OPENING")) {
                multiOperationBillNumbers.append(bean.getSBillNumber() + "-OPEN");
                multiOperationBillNumbers.append(",");
                multiOperationMaterials.append(bean.getSMaterial());
                multiOperationMaterials.append(",");              
            }
        }
        if(multiOperationBillNumbers.lastIndexOf(",") >= 0) {
            multiOperationBillNumbers.deleteCharAt(multiOperationBillNumbers.lastIndexOf(","));
        }
        if(multiOperationMaterials.lastIndexOf(",") >= 0) {
            multiOperationMaterials.deleteCharAt(multiOperationMaterials.lastIndexOf(","));
        }

        List<AvailableBalanceBean> currencyList = 
                dbOp.getDenominationValues(CommonConstants.MULTI_OPERATION_OPERATION, 
                        multiOperationBillNumbers.toString(), multiOperationMaterials.toString());                    
        return new DenominationBean(sOperationName, 
                multiOperationBillNumbers.toString(), multiOperationMaterials.toString(), false, true, currencyList);
    }    
    
    private DenominationBean doMultiCloseDenominationWork(ActionEvent event) throws SQLException {

        String sOperationName = CommonConstants.MULTI_CLOSING_OPERATION;
        StringBuilder multiCLoseBillNumbers = new StringBuilder();
        StringBuilder multiCLoseMaterials = new StringBuilder();

        for(AllDetailsBean bean : tbList.getItems()) {
            if(bean.isBChecked() && bean.getSOperation().equals("BILL CLOSING")) {
                multiCLoseBillNumbers.append(bean.getSBillNumber());
                multiCLoseBillNumbers.append(",");
                multiCLoseMaterials.append(bean.getSMaterial());
                multiCLoseMaterials.append(",");
            }
        }

        if(multiCLoseBillNumbers.lastIndexOf(",") >= 0) {
            multiCLoseBillNumbers.deleteCharAt(multiCLoseBillNumbers.lastIndexOf(","));
        }

        if(multiCLoseMaterials.lastIndexOf(",") >= 0) {
            multiCLoseMaterials.deleteCharAt(multiCLoseMaterials.lastIndexOf(","));
        }

        List<AvailableBalanceBean> currencyList = 
                dbOp.getDenominationValues(sOperationName, 
                        multiCLoseBillNumbers.toString(), multiCLoseMaterials.toString());                    

        return new DenominationBean(sOperationName, 
                multiCLoseBillNumbers.toString(), multiCLoseMaterials.toString(), true, false, currencyList);
    }
    
    public void doOperationForMultiClose() {
        
        String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue());
        
        for(AllDetailsBean bean : tbList.getItems()) {
            if(bean.isBChecked()) {
                if(bean.getSOperation().equals("BILL CLOSING")) {                                         
                    if(bean.getSMaterial().equals("GOLD")) {
                        goldBillClosingScreenWork(sEndDate, bean.getSBillNumber(), ivCustomer.getImage(), null);
                    } else {
                        silverBillClosingScreenWork(sEndDate, bean.getSBillNumber(), ivCustomer.getImage(), null);
                    }
                }
            }
        }    
    }

    public void doOperationForMultiOperation(ActionEvent event) {
        
        btDoOperation.setDisable(true);
        
        String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue());
        
        for(AllDetailsBean bean : tbList.getItems()) {
            if(bean.isBChecked()) {
                if(bean.getSOperation().equals("BILL CLOSING")) {                                         
                    if(bean.getSMaterial().equals("GOLD")) {
                        goldBillClosingScreenWork(sEndDate, bean.getSBillNumber(), ivCustomer.getImage(), null);
                    } else {
                        silverBillClosingScreenWork(sEndDate, bean.getSBillNumber(), ivCustomer.getImage(), null);
                    }                    
                } else if(bean.getSOperation().equals("BILL OPENING")) {                        
                    if(bean.getSMaterial().equals("GOLD")) {
                        goldBillOpeningScreenWork(event, "0.000", "70.0", bean.getSAmount(), ivCustomer.getImage(), null);                        
                    } else {
                        silverBillOpeningScreenWork(event, "0.000", "70.0", bean.getSAmount(), ivCustomer.getImage(), null);
                    }
                }
            }
        } 
    }
    
    public void goldBillOpeningScreenWork(ActionEvent event, 
            String grossWeight, String purity, String amount, Image customerImage, Image jewelImage) {
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_OPENING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            GoldBillOpeningController gon = (GoldBillOpeningController) loader.getController();
            gon.setParent(dialog);
            gon.autoBillOpen(grossWeight, purity, amount, customerImage, jewelImage);
            
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
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }
    
    public void silverBillOpeningScreenWork(ActionEvent event, 
            String grossWeight, String purity, String amount, Image customerImage, Image jewelImage) {
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                        CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_OPENING_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            SilverBillOpeningController gon = (SilverBillOpeningController) loader.getController();
            gon.setParent(dialog);
            gon.autoBillOpen(grossWeight, purity, amount, customerImage, jewelImage);
            
            dialog.setTitle("Silver Bill Opening - " 
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
        } else {
            PopupUtil.showInfoAlert("Sorry you do not have any permission in this sceen. ");
        }    
    }

    private void goldBillClosingScreenWork(String closeDate, String billNumber, 
                                Image customerImg, Image jewelImg) {

        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
        gon.autoCloseBill(closeDate, billNumber, customerImg, jewelImg);

        dialog.setTitle("Gold Bill Closing");      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(0);
        dialog.setY(5);
        dialog.setWidth(bounds.getWidth());
        dialog.setHeight(bounds.getHeight()-5);
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.close();
    }

    private void silverBillClosingScreenWork(String closeDate, String billNumber, 
                                Image customerImg, Image jewelImg) {

        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillClosingScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        SilverBillClosingController gon = (SilverBillClosingController) loader.getController();
        gon.autoCloseBill(closeDate, billNumber, customerImg, jewelImg);

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
        dialog.close();                     
    }
    
    @FXML
    private void btCaptureCustomerImgClicked(ActionEvent event) {
        captureCustomerImage(event);
    }

    private void captureCustomerImage(ActionEvent event) {
        isCustomerImgAvailable = false;        
        String sCustomerCamName = otherSettingValues.getRow(0).getColumn(2).toString();
        if(!sCustomerCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {
            try {
                File billNumberFolder = new File(materialFolder, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
                WebCamWork.captureImageFrom(sCustomerCamName, billNumberFolder.getAbsolutePath());
                try (FileInputStream fis = new FileInputStream(billNumberFolder.getAbsolutePath())) {
                    final Image img = new Image(fis);
                    ivCustomer.setImage(img);
                    isCustomerImgAvailable = true;
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FrameGrabber.Exception ex) {
                PopupUtil.showInfoAlert(event, "Invalid camera name was selected. "); 
            }                
        } else {
            PopupUtil.showInfoAlert(event, "Not any camera was selected to take pic. ");            
        }
    }
    
    @FXML
    private void ivCustomerImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivCustomer.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivCustomer.getImage(), "CUSTOMER IMAGE");
        }        
    }

    @FXML
    private void btRepSuspenseOnAction(ActionEvent event) {
        try {
            DataTable nYLockedValues = dbOpRep.getRNYSuspenseTableValue("GOLD");
            if(nYLockedValues != null) {
                beforeRepClose(nYLockedValues);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }

    @FXML
    private void txtRepPlannerIdOnAction(ActionEvent event) {
        if(tgOff.isSelected()) {
            String sRepPlannerId = txtRepPlannerId.getText();
            tbRepList.getItems().removeAll(tbRepList.getItems());
            calculateRepTotalAmtToBeRecieved();
            try {
                DataTable dataTable = 
                        dbOpRepOpen.getAllHeaderValuesByRepledgePlannerId(sRepPlannerId, "GOLD");
                for(int i=0; i<dataTable.getRowCount(); i++) {
                    RepAllDetailsBean bean = (RepAllDetailsBean) dataTable.getRow(i).getColumn(0); 
                    tbRepList.getItems().add(bean);
                }
                calculateRepTotalAmtToBeRecieved();
                boolean isPrinted = dbOpRepOpen.isRepPrinted(sRepPlannerId);
                String closingDate = dbOpRepOpen.getRepBillCalcClosingDate(sRepPlannerId);
                dpRepClosingDate.setValue(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
                btPrintSelected.setDisable(false);
                if(isPrinted) {
                    dpRepClosingDate.setEditable(false);
                    btUpdateBill.setDisable(true);
                } else {
                    dpRepClosingDate.setEditable(true);
                    btUpdateBill.setDisable(false);
                }
            } catch (SQLException ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        try {
            tbRepList.getItems().removeAll(tbRepList.getItems());
            calculateRepTotalAmtToBeRecieved();
            btPrintSelected.setDisable(true);
            txtRepPlannerId.setEditable(false);
            txtRepPlannerId.setMouseTransparent(true);
            txtRepPlannerId.setFocusTraversable(false);
            txtRepPlannerId.setText(dbOpRepOpen.getPlanId("REP_BILL_CALC"));
            dpRepClosingDate.setValue(LocalDate.now());
            dpRepClosingDate.setEditable(true);
            btSaveBill.setDisable(false);
            btUpdateBill.setDisable(true);
        } catch (SQLException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        try {
            tbRepList.getItems().removeAll(tbRepList.getItems());
            calculateRepTotalAmtToBeRecieved();
            txtRepPlannerId.setEditable(true);
            txtRepPlannerId.setMouseTransparent(false);
            txtRepPlannerId.setFocusTraversable(true);             
            btSaveBill.setDisable(true);
            btUpdateBill.setDisable(false);
            txtRepPlannerId.setText(Integer.toString(Integer.parseInt(dbOpRepOpen.getPlanId("REP_BILL_CALC"))-1));
            Platform.runLater(() -> {
                txtRepPlannerId.requestFocus();
                txtRepPlannerId.positionCaret(txtRepPlannerId.getText().length());
            });                    
        } catch (SQLException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        try {            
            boolean allow = true;
            
            for(RepAllDetailsBean bean : tbRepList.getItems()) {
                if(bean.getSBOrA().equals("A")) {
                    allow = false;
                }
            }
            
            if(allow) {
                String id = txtRepPlannerId.getText();
                String closingDate = CommonConstants.DATETIMEFORMATTER.format(dpRepClosingDate.getValue());        

                double openCapital = Double.parseDouble(txtRepOpenCapital.getText());
                double openInterest = Double.parseDouble(txtRepOpenInterest.getText());
                double openTotal = Double.parseDouble(txtRepOpenCapital.getText());
                double closeCapital = Double.parseDouble(txtRepOpenCapital.getText());
                double closeInterest = Double.parseDouble(txtRepOpenCapital.getText());
                double closeTotal = Double.parseDouble(txtRepOpenCapital.getText());
                String giveOrGet = lbAmtToGet1.getText();
                double totalValue = Double.parseDouble(txtRepAmtToGet.getText());

                dbOpRep.saveBillCalcRepHeader(id, closingDate, openCapital, openInterest, openTotal, 
                        closeCapital, closeInterest, closeTotal, giveOrGet, totalValue);

                for(RepAllDetailsBean bean : tbRepList.getItems()) {
                    if(bean.isBChecked()) {                        
                        dbOpRep.saveBillCalcRep(id, closingDate, bean);
                    }
                }

                dbOpRep.setRepPlannerNextCustomerId(Integer.toString(Integer.parseInt(id)+1));                
                btRepRemoveFromTableClicked(event);
                txtRepPlannerId.setText(dbOpRepOpen.getPlanId("REP_BILL_CALC"));
                PopupUtil.showInfoAlert(event, "Repledge planner id: "+ id +"is saved successfully.");
            } else {
                PopupUtil.showInfoAlert(event, "After work cannot be printed.");
            }
        } catch (Exception ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        try {
            String sRepPlannerId = txtRepPlannerId.getText();        
            if(tgOn.isSelected() || (tgOff.isSelected() && !dbOpRepOpen.isRepPrinted(sRepPlannerId)) ) {
             
                boolean allow = true;

                for(RepAllDetailsBean bean : tbRepList.getItems()) {
                    if(bean.getSBOrA().equals("A")) {
                        allow = false;
                    }
                }

                if(allow) {
                    String id = txtRepPlannerId.getText();
                    String closingDate = CommonConstants.DATETIMEFORMATTER.format(dpRepClosingDate.getValue());        

                    double openCapital = Double.parseDouble(txtRepOpenCapital.getText());
                    double openInterest = Double.parseDouble(txtRepOpenInterest.getText());
                    double openTotal = Double.parseDouble(txtRepOpenCapital.getText());
                    double closeCapital = Double.parseDouble(txtRepOpenCapital.getText());
                    double closeInterest = Double.parseDouble(txtRepOpenCapital.getText());
                    double closeTotal = Double.parseDouble(txtRepOpenCapital.getText());
                    String giveOrGet = lbAmtToGet1.getText();
                    double totalValue = Double.parseDouble(txtRepAmtToGet.getText());

                    dbOpRep.updateBillCalcRepHeader(id, closingDate, openCapital, openInterest, openTotal, 
                            closeCapital, closeInterest, closeTotal, giveOrGet, totalValue);
                    dbOpRep.deleteBillCalcRep(id, closingDate);
                    for(RepAllDetailsBean bean : tbRepList.getItems()) {
                        if(bean.isBChecked()) {                        
                            dbOpRep.saveBillCalcRep(id, closingDate, bean);
                        }
                    }

                    dbOpRep.setRepPlannerNextCustomerId(Integer.toString(Integer.parseInt(id)+1));                
                    btRepRemoveFromTableClicked(event);
                    //txtRepPlannerId.setText(dbOpRepOpen.getPlanId("REP_BILL_CALC"));
                    PopupUtil.showInfoAlert(event, "Repledge planner id: "+ id +"is updated successfully.");
                } else {
                    PopupUtil.showInfoAlert(event, "After work cannot be printed.");
                }
                
            } else {
                PopupUtil.showInfoAlert("Operation cannot be done after printed.");
            }
        } catch (Exception ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
    }
    
    private void closeDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue());
        
        if(otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {
                if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                    btDoOperation.setDisable(false);
                } else {
                    btDoOperation.setDisable(true);
                }                    
            } else {
                btDoOperation.setDisable(true);
            }
        }    
    }    

    @FXML
    private void dpClosingDateOnChanged(ActionEvent event) {        
        try {
            btRemoveFromTableClicked(event);
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btPrintCompSelectedOnAction(ActionEvent event) {
        
        /*if(txtRepAmtToGet.getText() != null 
                && !txtRepAmtToGet.getText().isEmpty()) {

            try {
                String sFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billcalccomp.jasper";                        

                List<RepPrintBean> ParamList = new ArrayList<>();	            
                for(AllDetailsBean bean : tbList.getItems()) {                
                    if(bean.isBChecked() 
                            && bean.getSOperation()
                                    .equals("BILL CLOSING")) {
                        RepPrintBean nBean = new RepPrintBean();
                        if(bean.getSStatus().equals("SUSPENSE")) {
                            nBean.setSOperation("CLOSE \n (ALR-SUS)");
                        } else {
                            nBean.setSOperation("CLOSE");
                        }
                        nBean.setSBillNumber(bean.getSBillNumber());
                        String sBarcodeVal = bean.getSBillNumber() 
                                + "<->RBC<->" + "CLOSE";
                        nBean.setSBarcode(sBarcodeVal);
                        nBean.setSOpeningDate(bean.getSOpeningDate());
                        nBean.setSRepBillNumber(bean.getSRepBillNumber());
                        nBean.setSRepName(bean.getSRepName() + "\n(" + bean.getSActualBillNumber() +")");
                        nBean.setDAmount(Double.parseDouble(bean.getSAmount()));
                        nBean.setDInterest(Double.parseDouble(bean.getSInterestedAmt()));
                        nBean.setDTotalInterested(Double.parseDouble(bean.getSTotalInterestedAmt()));
                        ParamList.add(nBean);
                    }
                }

                /*if(ParamList.size() == 1 && ParamList.get(0).getSOperation().equals("CLOSE")) {
                    RepPrintBean nEmptyBean = new RepPrintBean();
                    ParamList.add(nEmptyBean.getEmptyValObject());TIRU  
                }*/

                /*for(RepAllDetailsBean bean : tbRepList.getItems()) {                
                    if(bean.isBChecked() && bean.getSOperation()
                            .equals("BILL OPENING")) {
                        RepPrintBean nBean = new RepPrintBean();
                        nBean.setSOperation("OPEN");
                        nBean.setSBillNumber(bean.getSBillNumber());
                        nBean.setSBarcode(bean.getSBillNumber() + "<->RBO<->" + bean.getSRepName());
                        nBean.setSOpeningDate(bean.getSOpeningDate());
                        nBean.setSRepBillNumber(bean.getSRepBillNumber());
                        nBean.setSRepName(bean.getSRepName());
                        nBean.setDAmount(Double.parseDouble(bean.getSAmount()));
                        nBean.setDInterest(Double.parseDouble(bean.getSInterestedAmt()));
                        nBean.setDTotalInterested(Double.parseDouble(bean.getSTotalInterestedAmt()));
                        ParamList.add(nBean);
                    }
                }

                /*if((ParamList.size() == 1 && ParamList.get(0).getSOperation().equals("OPEN"))
                        || (ParamList.size() == 3 && ParamList.get(1).getSOperation().equals("OPEN"))) {
                    RepPrintBean nEmptyBean = new RepPrintBean();
                    ParamList.add(nEmptyBean.getEmptyValObject());
                }*/

                /*for(RepAllDetailsBean bean : tbRepList.getItems()) {                
                    if(bean.isBChecked() && bean.getSOperation().equals("GET SUSPENSE")) {
                        RepPrintBean nBean = new RepPrintBean();
                        nBean.setSOperation("GET SUSPENSE");
                        nBean.setSBillNumber(bean.getSBillNumber());
                        String sBarcodeVal = bean.getSBillNumber() 
                                + "<->RBC<->" + "GET SUSPENSE";
                        nBean.setSBarcode(sBarcodeVal);
                        nBean.setSOpeningDate(bean.getSOpeningDate());
                        nBean.setSRepBillNumber(bean.getSRepBillNumber());
                        nBean.setSRepName(bean.getSRepName() + "\n(" + bean.getSActualBillNumber() +")");
                        nBean.setDAmount(Double.parseDouble(bean.getSAmount()));
                        nBean.setDInterest(Double.parseDouble(bean.getSInterestedAmt()));
                        nBean.setDTotalInterested(Double.parseDouble(bean.getSTotalInterestedAmt()));
                        ParamList.add(nBean);
                    }
                }

                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("BillCalcCollectionBeanParam", tableList);
                parameters.put("TODAYSDATE", DateRelatedCalculations.getTodaysDate());
                parameters.put("PLANNERID", txtRepPlannerId.getText());

                parameters.put("CLOSINGCAPITAL", Double.parseDouble((txtRepCloseCapital.getText() != null) ? txtRepCloseCapital.getText() : "0"));
                parameters.put("OPENINGCAPITAL", Double.parseDouble((txtRepOpenCapital.getText() != null) ? txtRepOpenCapital.getText() : "0"));
                parameters.put("TOGIVEORGETVAL", Double.parseDouble((txtRepAmtToGet.getText() != null) ? txtRepAmtToGet.getText() : "0"));
                parameters.put("TOGIVEORGETLABEL", lbAmtToGet1.getText());
                parameters.put("CLOSEINTEREST", Double.parseDouble((txtRepCloseInterest.getText() != null) ? txtRepCloseInterest.getText() : "0"));
                parameters.put("TOTALCLOSE", Double.parseDouble((txtRepCloseInterested.getText() != null) ? txtRepCloseInterested.getText() : "0"));
                parameters.put("OPENINTEREST", Double.parseDouble((txtRepOpenInterest.getText() != null) ? txtRepOpenInterest.getText() : "0"));
                parameters.put("TOTALOPEN", Double.parseDouble((txtRepOpenInterested.getText() != null) ? txtRepOpenInterested.getText() : "0"));
                parameters.put("CLOSINGDATE", CommonConstants.DATETIMEFORMATTER.format(dpRepClosingDate.getValue()) 
                        + " " + DateRelatedCalculations.getCurrentTime());
                parameters.put("PREPAREDUSER", CommonConstants.EMP_NAME);
                parameters.put("EXECUTEDUSER", "");

                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();
                noticeUtil.generateNoticeOperation(sFileName, parameters);

                dbOpRep.setRepHeaderAsPrinted(txtRepPlannerId.getText());
                btUpdateBill.setDisable(true);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(BillCalculatorController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert(
                    "Not any details filled to print the records.");
        }*/
    }
}
