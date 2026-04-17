/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillclosing;

import com.github.sarxos.webcam.Webcam;
import com.magizhchi.pawnbroking.account.TodaysAccountJewelRepledgeBean;
import com.magizhchi.pawnbroking.billcalculator.BillCalculatorController;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.ScreenUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.common.WebCamWork;
import com.magizhchi.pawnbroking.companyadvanceamount.AdvanceAmountBean;
import com.magizhchi.pawnbroking.companybillopening.BillOpeningDBOperation;
import com.magizhchi.pawnbroking.companybillopening.CustomerBillsListBean;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.bytedeco.javacv.FrameGrabber;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class GoldBillClosingController implements Initializable {

    public BillClosingDBOperation dbOp;
    private BillOpeningDBOperation dbOpOpen;
    
    public String sLastSelectedId = null;
    private String sRepledgeBillId = null;
    private String sReduceType = null;
    private String sMinimumType = null;
    private String sRebilledTo = null;
    public String sRebilledNewAmt = null;
    public String sNewWeight = null;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private final String goldBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/GoldBillOpening.fxml";
    private final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";
    boolean isNewVerifyEntry = false;
    boolean isHalfJewel = false;
    
    public Stage dialog;
    private DataTable otherSettingValues = null;
    
    public String idProofType;
    public String idProofNumber;
    
    public static boolean isOpenCustomerImgAvailable = false;
    public static boolean isOpenJewelImgAvailable = false;
    public static boolean isOpenUserImgAvailable = false;
    public static boolean isCloseCustomerImgAvailable = false;
    public static boolean isCloseJewelImgAvailable = false;
    public static boolean isCloseUserImgAvailable = false;
    
    //private WebCamWork webCamWork = new WebCamWork();    
    
    private File tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
    private File compFolder = new File(tempFile, CommonConstants.ACTIVE_COMPANY_ID);
    private File materialFolder = new File(compFolder, "GOLD");
    
    final String billCalculatorScreen = "/com/magizhchi/pawnbroking/billcalculator/BillCalculator.fxml";
    
    @FXML
    public TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private ComboBox<String> cbSpouseType;
    @FXML
    private TextField txtBillNumber;
    @FXML
    public DatePicker dpBillClosingDate;
    @FXML
    private DatePicker dpBillOpeningDate;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private ToggleGroup rgGenderGroup;
    @FXML
    private TextField txtSpouseName;
    @FXML
    private TextField txtDoorNo;
    @FXML
    private TextField txtStreetName;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtMobileNumber;
    @FXML
    private TextArea txtItems;
    @FXML
    private TextField txtInterestType;
    @FXML
    private TextField txtActualTotalDaysOrMonths;
    @FXML
    private TextField txtToReduceDaysOrMonths;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtNote;
    @FXML
    public TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    public TextField txtTakenAmount;
    @FXML
    public TextField txtToGetAmount;
    @FXML
    public TextField txtGotAmount;
    @FXML
    public ToggleButton tgOn;
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
    private HBox nodeAddToFilter;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private DatePicker dpAddToFilter;
    @FXML
    private ComboBox<String> cbAddToFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private Label lbActualTotalDaysOrMonths;
    @FXML
    private Label lbToReduceDaysOrMonths;
    @FXML
    private Label lbTakenDaysOrMonths;
    @FXML
    private TextField txtTakenDaysOrMonths;
    @FXML
    private TextField txtPreStatus;
    @FXML
    public TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    public TextField txtPurity;
    @FXML
    private ComboBox<String> cbOperationType;
    @FXML
    public TextField txtTotalAdvanceAmountPaid;
    @FXML
    private TextField txtAdvanceReceiptDetailTotalAmount;
    @FXML
    private TableView<AdvanceAmountBean> tbAdvanceReceiptDetails;
    @FXML
    private TextField txtMinimumDaysOrMonths;
    @FXML
    private Label lbMinimumDaysOrMonths;
    @FXML
    private TextArea txtRepledgeBillId;
    @FXML
    private TextField txtRepledgeName;
    @FXML
    private TextField txtRepledgeBillNumber;
    @FXML
    private TextField txtRepledgeBillAmount;
    @FXML
    private TextArea txtReBilledFrom;
    @FXML
    public TextField txtReBilledTo;
    @FXML
    private TextField txtRepledgeBillStatus;
    @FXML
    private TextField txtCompanyBillNumber;
    @FXML
    private Label lbScreenMessage;
    @FXML
    public Tab tabAdvanceAmountDetails;
    @FXML
    private HBox hSaveModeButtons;
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
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private Label lbRebillMsg;
    @FXML
    public TextField txtTotalOtherCharges;
    @FXML
    private TableView<OtherChargesBean> tbOtherCharges;
    @FXML
    public TextField txtDiscountAmount;
    @FXML
    private TextField txtAcceptedEndingDate;
    @FXML
    private TextField txtRepledgeOpenedDate;
    @FXML
    private TableView<CustomerBillsListBean> tbCutomerBillsList;
    @FXML
    private TextField txtNomineeName;
    @FXML
    private TextField txtRatePerGm;
    @FXML
    private HBox filterHBOX;
    @FXML
    private HBox companyAloneFilterHBox;
    @FXML
    private Label lbFrom1;
    @FXML
    private HBox nodeAddToFilter1;
    @FXML
    private HBox hbClosingBillNumber;
    @FXML
    private TextField txtClosingBillNumber;
    @FXML
    private HBox hbAmount;
    @FXML
    private Button btRemoveFromTable;
    @FXML
    public TextField txtOpenCapital;
    @FXML
    private TextField txtOpenInterest;
    @FXML
    private TextField txtOpenInterested;
    @FXML
    public TextField txtCloseCapital;
    @FXML
    public TextField txtCloseInterest;
    @FXML
    public TextField txtCloseInterested;
    @FXML
    public TextField txtTotalWt;
    @FXML
    private Label lbAmtToGet;
    @FXML
    private TextField txtAmtToGet;
    @FXML
    public TableView<com.magizhchi.pawnbroking.billcalculator.AllDetailsBean> tbClosingList;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRepList;
    @FXML
    private DatePicker dpMultiRebilledClosingDate;
    @FXML
    private Label lbFrom11;
    @FXML
    private TextArea txtMultiRebilledItems;
    @FXML
    private TextField txtMultiRebilledOpeningAmt;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbOtherDetailsRepList;
    @FXML
    private Tab tabRebiledMultiple;
    @FXML
    private Label lbMultiRebillNewNumberMsg;
    @FXML
    private ToggleGroup rgCustomerCopyGroup;
    @FXML
    private Button btCardLostPrint;
    @FXML
    private TextField txtInterestPerMonth;
    @FXML
    private TextField txtCreatedEmpName;
    @FXML
    private TextField txtCreatedTime;
    @FXML
    private TextField txtClosedEmpName;
    @FXML
    private TextField txtClosedTime;
    @FXML
    private Button btCaptureCustomerImg;
    @FXML
    public ImageView ivCustomerBill;
    @FXML
    public ImageView ivJewelBill;
    @FXML
    private ImageView ivUserBill;
    @FXML
    private ImageView ivOpenCustomerBill;
    @FXML
    private ImageView ivOpenJewelBill;
    @FXML
    private ImageView ivOpenUserBill;
    @FXML
    private Button btCaptureCustomerImgRebillMulti;
    @FXML
    private ImageView ivCustomerBillRebilMultiple;
    @FXML
    private Button btJewelImgRebillMulti;
    @FXML
    private ImageView ivJewelBillRebilMultiple;
    @FXML
    private Button btShowInBillCalc;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btDenom;
    @FXML
    private Button btVerifyAllCopies;
    @FXML
    private Label lbStatus1;
    @FXML
    private TextField txtPhysicalLocation;
    @FXML
    private Button btMultiRebilledClose;
    @FXML
    private Button btPrintReciept;
    @FXML
    private ComboBox<String> cbCustomerImageCameraName;
    @FXML
    private ComboBox<String> cbUserImageCameraName;
    @FXML
    private ComboBox<String> cbJewelImageCameraName;
    @FXML
    private Button btCaptureJewelImg;

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
                                            case O :
                                            {
                                                if(KV.isControlDown()) {
                                                    tgOff.setSelected(true);
                                                    saveModeOFF(null);
                                                }
                                                break;
                                            }                                            
                                            case N :
                                            {
                                                if(KV.isControlDown()) {
                                                    tgOn.setSelected(true);
                                                    saveModeON(null);
                                                }
                                                break;
                                            }                                            
                                            case B :
                                            {
                                                if(KV.isControlDown() && txtBillNumber.isEditable()) {
                                                    txtBillNumber.requestFocus();
                                                    txtBillNumber.positionCaret(txtBillNumber.getText().length());
                                                }
                                                break;
                                            }     
                                            case L :
                                            {
                                                if(KV.isControlDown()) {
                                                    txtDiscountAmount.requestFocus();
                                                    txtDiscountAmount.positionCaret(txtDiscountAmount.getText().length());
                                                }
                                                break;
                                            }     
                                            case R :
                                            {
                                                if(KV.isControlDown()) {
                                                    cbOperationType.getSelectionModel().select(1);
                                                    cbStatus.getSelectionModel().select(0);
                                                    denominationWork(KV);
                                                }
                                                break;
                                            }                                            
                                            case ADD :
                                            {
                                                if(KV.isControlDown()) {
                                                    cbOperationType.getSelectionModel().select(1);
                                                    cbStatus.getSelectionModel().select(1);
                                                    denominationWork(KV);
                                                }
                                                break;
                                            }                                            
                                            case EQUALS :
                                            {
                                                if(KV.isControlDown()) {
                                                    cbOperationType.getSelectionModel().select(1);
                                                    cbStatus.getSelectionModel().select(1);
                                                    denominationWork(KV);
                                                }
                                                break;
                                            }                                            
                                            case SUBTRACT :
                                            {
                                                if(KV.isControlDown()) {
                                                    cbOperationType.getSelectionModel().select(1);
                                                    cbStatus.getSelectionModel().select(2);
                                                    denominationWork(KV);
                                                }
                                                break;
                                            }                                            
                                            case MINUS :
                                            {
                                                if(KV.isControlDown()) {
                                                    cbOperationType.getSelectionModel().select(1);
                                                    cbStatus.getSelectionModel().select(2);
                                                    denominationWork(KV);
                                                }
                                                break;
                                            }                                            
                                            case S :
                                            {
                                                if(KV.isControlDown()) {
                                                    if(!btSaveBill.isDisable()) {
                                                        denominationWork(KV);                                                    
                                                        btSaveBillClicked(null);
                                                    } else if(!btUpdateBill.isDisable()) {
                                                        btUpdateBillClicked(null);
                                                    }
                                                }
                                                break;
                                            }                                            
                                            case D :
                                            {
                                                if(KV.isControlDown()) {
                                                    denominationWork(KV);                                                                                                        
                                                }
                                                break;
                                            }                                            
                                            case NUMPAD1 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureCustomerImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case NUMPAD2 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureJewelImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case DIGIT1 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureCustomerImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case DIGIT2 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureJewelImgClicked(null);
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
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        shortCutKeysCode();
        isOpenCustomerImgAvailable = false;
        isOpenJewelImgAvailable = false;
        isOpenUserImgAvailable = false;
        isCloseCustomerImgAvailable = false;
        isCloseJewelImgAvailable = false;
        isCloseUserImgAvailable = false;
        
        ivCustomerBill.setImage(CommonConstants.noImage);
        ivCustomerBill.setCache(false);
        ivJewelBill.setImage(CommonConstants.noImage);
        ivJewelBill.setCache(false);
        ivUserBill.setImage(CommonConstants.noImage);
        ivUserBill.setCache(false);
        ivOpenCustomerBill.setImage(CommonConstants.noImage);
        ivOpenCustomerBill.setCache(false);
        ivOpenJewelBill.setImage(CommonConstants.noImage);
        ivOpenJewelBill.setCache(false);
        ivOpenUserBill.setImage(CommonConstants.noImage);
        ivOpenUserBill.setCache(false);
        
        try {
            dbOp = new BillClosingDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            dbOpOpen = new BillOpeningDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                    
        
        tbOtherCharges.setRowFactory(tv -> new TableRow<OtherChargesBean>() {
            @Override
            public void updateItem(OtherChargesBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getDAmount() > 0) {
                    setStyle(Util.getStyle("#000000", "#55FF30").toString());
                } else {
                    setStyle("");
                }
            }
        });

        tbCutomerBillsList.setRowFactory(tv -> new TableRow<CustomerBillsListBean>() {
            @Override
            public void updateItem(CustomerBillsListBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (
                        DateRelatedCalculations
                                .isFirstDateIsLesserOrEqualToSecondDate(
                                        item.getDate()
                        , item.getNoticedDate())) {
                    setStyle(Util.getStyle("#000000", "#FE7A7D").toString());
                } else if (item.getRepledgeBillId() != null 
                        && !item.getRepledgeBillId().isEmpty()) {
                    setStyle(Util.getStyle("#000000", "#FCBAEF").toString());
                } else {
                    setStyle("");
                }
            }
        });
        
        tbClosingList.setRowFactory(tv -> new TableRow<com.magizhchi.pawnbroking.billcalculator.AllDetailsBean>() {
            @Override
            public void updateItem(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean item, boolean empty) {
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
        
        lbScreenMessage.setVisible(false);
        lbRebillMsg.setVisible(false);
        dpBillClosingDate.setValue(LocalDate.now());
        dpBillOpeningDate.setValue(LocalDate.now());
        dpMultiRebilledClosingDate.setValue(LocalDate.now());
        nodeAddToFilter.getChildren().remove(dpAddToFilter);
        nodeAddToFilter.getChildren().remove(cbAddToFilter);
        
        try {
            otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(
                    CommonConstants.ROLEID, 
                    CommonConstants.OPERATION_TAB, 
                    CommonConstants.GOLD_BILL_CLOSING_SCREEN, 
                    "ALLOW_ADD") || 
                    CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                
                    String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());

                    if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillClosingDate))
                    {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }
            } else {
                btSaveBill.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
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

            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtBillNumber.setText(billRowAndNumber[1]);
                txtClosingBillNumber.setText(billRowAndNumber[1]);
            }                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        btCardLostPrint.setDisable(true);        
        
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
            txtGotAmount.setEditable(true);               
            txtGotAmount.setMouseTransparent(false);
            txtGotAmount.setFocusTraversable(true);                
        } else {
            txtGotAmount.setEditable(false);               
            txtGotAmount.setMouseTransparent(true);
            txtGotAmount.setFocusTraversable(false);                
        }
        
        Platform.runLater(() -> {
            txtBillNumber.requestFocus();
            txtBillNumber.positionCaret(txtBillNumber.getText().length());
        });      
        
        cbCustomerImageCameraName.getItems().add("DEFAULT");
        cbJewelImageCameraName.getItems().add("DEFAULT");
        cbUserImageCameraName.getItems().add("DEFAULT");
        try {
            for(Webcam webcam : WebCamWork.getWebCamLists()) {
                cbCustomerImageCameraName.getItems().add(webcam.getName());
                cbJewelImageCameraName.getItems().add(webcam.getName());
                cbUserImageCameraName.getItems().add(webcam.getName());
            }
        } catch (Exception e) {
            // No webcam available or driver error — continue with DEFAULT only
        }
        cbCustomerImageCameraName.getSelectionModel().select(0);
        cbJewelImageCameraName.getSelectionModel().select(0);
        cbUserImageCameraName.getSelectionModel().select(0);
    }

    @FXML
    private void txtBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtBillNumber.getText();
        String val = txtBillNumber.getText();
        if(val != null & !val.isEmpty()) {
            try {
                String[] billVals = val.split("-");
                String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();

                if(billRowAndNumber != null && billVals.length > 1) {
                    int index = billRowAndNumber[1].length();
                    sBillNumber = billVals[0].substring(index);
                } else {
                    sBillNumber = billVals[0];
                }            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        btClearAllClicked(null);
        txtBillNumber.setText(sBillNumber);
        
        try {
            HashMap<String, String> headerValues = tgOn.isSelected() 
                    ? dbOp.getAllBillingValuesToClose(sBillNumber, "GOLD") 
                    : dbOp.getAllClosedBillingValues(sBillNumber, "GOLD");
            String sInterestType = dbOp.getInterestType();
            String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths("GOLD", "REDUCTION");
            String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths("GOLD", "MINIMUM");
            String[] sNoticeValues = dbOp.getNoticeValues();            
            String sCardLostCharge = dbOp.getCardLostCharge();
            
            if(headerValues != null)
            {
                if(tgOff.isSelected()) {
                    dpBillClosingDate.setValue(LocalDate.parse(headerValues.get("CLOSING_DATE"), 
                            CommonConstants.DATETIMEFORMATTER));
                }
                setAllHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, 
                        sMinimumDatas, sNoticeValues, sCardLostCharge);
                setCustomerBillsListTableVals(headerValues, sNoticeValues[0]);
                sLastSelectedId = sBillNumber;
                sReduceType = sReduceDatas[1];
                sMinimumType = sMinimumDatas[1];
                sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
                
                Platform.runLater(() -> {
                    try {
                        setOpenImagesToTheField(headerValues);
                        if(tgOff.isSelected()) {
                            setClosedImagesToTheField(headerValues);
                        } else {
                            if(isOpenCustomerImgAvailable && isOpenJewelImgAvailable && (event != null)) {
                                PopupUtil.showTwoImageViewer(event, ivOpenCustomerBill.getImage(), 
                                        ivOpenJewelBill.getImage(), "OPEN CUSTOMER IMAGE");
                            }
                        }                                            
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } else {
                PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                btClearAllClicked(null);

                String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                if(billRowAndNumber != null) {
                    txtBillNumber.setText(billRowAndNumber[1]);
                }            
                Platform.runLater(() -> {
                    txtBillNumber.requestFocus();
                    txtBillNumber.positionCaret(txtBillNumber.getText().length());
                });                        
            }     
            
            //doClosingAmountScreenWork();
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doClosingAmountScreenWork()
    {
        final JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(() -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TotalPayAmount.fxml"));
                Parent root = loader.load();
                jfxPanel.setScene(new Scene(root, 1366, 768));
                // Give the controller access to the main app
                TotalPayAmountController controller = loader.getController();
                String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
                
                String customerName 
                        = txtCustomerName.getText() + " "
                        + cbSpouseType.getValue() + " "
                        + txtSpouseName.getText();
                
                controller.setAmountAndDate(
                        customerName
                        , "Bill Number: " + sLastSelectedId
                        , "For Closing Date: " + sBillClosingDate
                        , "To Pay Amount: " + txtToGetAmount.getText()
                        );
                SwingUtilities.invokeLater(() -> {
                    //ScreenUtil.customerWindow.remove(0);
                    Platform.setImplicitExit(true);
                    ScreenUtil.customerWindow.add(jfxPanel);
                    SwingUtilities.updateComponentTreeUI(jfxPanel);
                    ScreenUtil.customerWindow.invalidate();
                    ScreenUtil.customerWindow.validate();
                    ScreenUtil.customerWindow.repaint();
                    ScreenUtil.customerWindow.pack();
                    ScreenUtil.customerWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    ScreenUtil.customerWindow.setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        ScreenUtil.showOnScreen(1, ScreenUtil.customerWindow);
    }
    
    public void setOpenImagesToTheField(HashMap<String, String> headerValues) throws FileNotFoundException, IOException {
                
        File billNumberFolder = new File(materialFolder, headerValues.get("BILL_NUMBER"));
        
        File custTemp = new File(billNumberFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
        if(custTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(custTemp)) {
                final Image img = new Image(fis);
                ivOpenCustomerBill.setImage(img);
                isOpenCustomerImgAvailable = true;
            }
        } else {                      
            ivCustomerBill.setImage(CommonConstants.noImage);
            isOpenCustomerImgAvailable = false;
        }

        File jewelTemp = new File(billNumberFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
        if(jewelTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(jewelTemp)) {
                final Image img = new Image(fis);
                ivOpenJewelBill.setImage(img);
                isOpenJewelImgAvailable = true;
            }
        } else {                      
            ivJewelBill.setImage(CommonConstants.noImage);
            isOpenJewelImgAvailable = false;
        }

        File userTemp = new File(billNumberFolder, CommonConstants.OPEN_USER_IMAGE_NAME);
        if(userTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(userTemp)) {
                final Image img = new Image(fis);
                ivOpenUserBill.setImage(img);
                isOpenUserImgAvailable = true;
            }
        } else {                      
            ivUserBill.setImage(CommonConstants.noImage);
        }
    }
    
    public void setClosedImagesToTheField(HashMap<String, String> headerValues) throws FileNotFoundException, IOException {
                
        File billNumberFolder = new File(materialFolder, headerValues.get("BILL_NUMBER"));
        
        File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
        if(custTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(custTemp)) {
                final Image img = new Image(fis);
                ivCustomerBill.setImage(img);
                isCloseCustomerImgAvailable = true;
            }
        } else {                      
            ivCustomerBill.setImage(CommonConstants.noImage);
        }

        File jewelTemp = new File(billNumberFolder, CommonConstants.CLOSE_JEWEL_IMAGE_NAME);
        if(jewelTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(jewelTemp)) {
                final Image img = new Image(fis);
                ivJewelBill.setImage(img);
                isCloseJewelImgAvailable = true;
            }
        } else {                      
            ivJewelBill.setImage(CommonConstants.noImage);
        }

        File userTemp = new File(billNumberFolder, CommonConstants.CLOSE_USER_IMAGE_NAME);
        if(userTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(userTemp)) {
                final Image img = new Image(fis);
                ivUserBill.setImage(img);
                isCloseUserImgAvailable = true;
            }
        } else {                      
            ivUserBill.setImage(CommonConstants.noImage);
        }
    }
    
    public void setAllDetailValuesToFieldInAdvanceReceiptTable(DataTable allDetailValues) {

        tbAdvanceReceiptDetails.getItems().removeAll(tbAdvanceReceiptDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sBillAmount = allDetailValues.getRow(i).getColumn(2).toString();
            String sPaidAmount = allDetailValues.getRow(i).getColumn(3).toString();
            String sTotalAmount = allDetailValues.getRow(i).getColumn(4).toString();
            
            double dBillAmount = Double.parseDouble(sBillAmount);
            double dPaidAmount = Double.parseDouble(sPaidAmount);
            double dTotalAmount = Double.parseDouble(sTotalAmount);
            tbAdvanceReceiptDetails.getItems().add(new AdvanceAmountBean(sBillNumber, sDate, dBillAmount, dPaidAmount, dTotalAmount));
        }        
    }     
    
    
    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues, 
            String sInterestType, String[] sReduceDatas, String[] sMinimumDatas, 
            String[] sNoticeValues, String sCardLostCharge)
    {
        try {            
            txtBillNumber.setText(headerValues.get("BILL_NUMBER"));
            dpBillOpeningDate.setValue(LocalDate.parse(headerValues.get("OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
            txtCustomerName.setText(headerValues.get("CUSTOMER_NAME"));
            String sGender = headerValues.get("GENDER");
            if(null != sGender) switch (sGender) {
                case "MALE":
                    rgGenderGroup.getToggles().get(0).setSelected(true);
                    break;
                case "FEMALE":
                    rgGenderGroup.getToggles().get(1).setSelected(true);
                    break;
                case "OTHER":
                    rgGenderGroup.getToggles().get(2).setSelected(true);
                    break;
                default:
                    break;
            }
            cbSpouseType.setValue(headerValues.get("SPOUSE_TYPE"));
            txtSpouseName.setText(headerValues.get("SPOUSE_NAME"));
            txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
            txtStreetName.setText(headerValues.get("STREET"));
            txtArea.setText(headerValues.get("AREA"));
            txtCity.setText(headerValues.get("CITY"));
            txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
            txtItems.setText(headerValues.get("ITEMS"));
            txtGrossWeight.setText(headerValues.get("GROSS_WEIGHT"));
            txtNetWeight.setText(headerValues.get("NET_WEIGHT"));
            txtPurity.setText(headerValues.get("PURITY"));
            txtPhysicalLocation.setText(headerValues.get("physical_location"));
            
            txtAmount.setText(headerValues.get("AMOUNT"));
            txtInterest.setText(headerValues.get("INTEREST"));
            // interest per month
            String sOpenFormula = dbOp.getOpenFormula(CommonConstants.DATETIMEFORMATTER.format(
                    dpBillOpeningDate.getValue()), Double.parseDouble(txtAmount.getText()), "GOLD");
            String[][] openReplacements = {{"AMOUNT", txtAmount.getText()}, 
                                       {"INTEREST", txtInterest.getText()},
                                       {"DOCUMENT_CHARGE", "0"}};            
            for(String[] replacement: openReplacements) {
                sOpenFormula = sOpenFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine openEngine = new ScriptEngineManager().getEngineByExtension("js");            
            String sOpenTakenAmount = openEngine.eval(sOpenFormula).toString() != null ? 
                    String.valueOf(Math.round(Double.parseDouble(openEngine.eval(sOpenFormula).toString()))) : "0";            
            txtInterestPerMonth.setText(sOpenTakenAmount);
            
            txtNote.setText(headerValues.get("NOTE"));
            txtInterestType.setText(sInterestType);
            txtPreStatus.setText(headerValues.get("STATUS"));
            txtNomineeName.setText(headerValues.get("NOMINEE_NAME"));

            String sCustomerCopy = headerValues.get("CUSTOMER_COPY");
            if(null != sCustomerCopy) switch (sCustomerCopy) {
                case "Received":
                    rgCustomerCopyGroup.getToggles().get(0).setSelected(true);
                    btCardLostPrint.setDisable(true);
                    break;
                case "Not Received":
                    rgCustomerCopyGroup.getToggles().get(1).setSelected(true);
                    btCardLostPrint.setDisable(false);
                    break;
                default:
                    break;
            }
            idProofType = headerValues.get("ID_TYPE");
            idProofNumber = headerValues.get("ID_NUMBER");
            
            double dRatePerGm = Double.parseDouble(headerValues.get("AMOUNT")) 
                    / Double.parseDouble(headerValues.get("GROSS_WEIGHT"));
            txtRatePerGm.setText(String.valueOf(Math.round(dRatePerGm)));            
            
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
            String sAccClosingDate = headerValues.get("ACCEPTED_CLOSING_DATE");
            if(sAccClosingDate!=null && !sAccClosingDate.isEmpty()) { 
                txtAcceptedEndingDate.setText(sAccClosingDate);
                if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sAccClosingDate, sEndDate)) {
                    txtAcceptedEndingDate.setStyle("-fx-background-color: #FF5555");
                } else {
                    txtAcceptedEndingDate.setStyle("-fx-background-color: #55FF30");
                }                
            }
            String sTotalAdvAmtPaid = headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID");
            txtTotalAdvanceAmountPaid.setText(sTotalAdvAmtPaid);
            if(Double.parseDouble(sTotalAdvAmtPaid)>0) {
                txtTotalAdvanceAmountPaid.setStyle("-fx-background-color: #FF5555");
            } else {
                txtTotalAdvanceAmountPaid.setStyle("-fx-background-color: #FFFFFF");
            }
            
            txtDiscountAmount.setText("0");
            
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());            
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            
            if("MONTH".equals(sInterestType)) {
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                lbActualTotalDaysOrMonths.setText("Actual Total Months:");
                txtActualTotalDaysOrMonths.setText(Long.toString(lActualTotalMonths[0]) + " Months and " + Long.toString(lActualTotalMonths[1]) + " Days.");
                lbTakenDaysOrMonths.setText("For Months:");                               
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Days:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])
                        || "MONTHS FROM TOTAL MONTH".equals(sMinimumDatas[1])) {      
                        lbMinimumDaysOrMonths.setText("Minimum Months:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                       
                } else if("DAYS".equals(sMinimumDatas[1])) {                    
                        lbMinimumDaysOrMonths.setText("Minimum Days:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                }
            } else if("DAY".equals(sInterestType)) {
                
                lbActualTotalDaysOrMonths.setText("Actual Total Days:");
                txtActualTotalDaysOrMonths.setText(Long.toString(lTotalDays));
                lbTakenDaysOrMonths.setText("For Days:");               
                
                if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    
                    lbToReduceDaysOrMonths.setText("To Reduce Months:");
                    txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    
                        lbMinimumDaysOrMonths.setText("Minimum Months:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);

                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                           
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {                    
                        lbMinimumDaysOrMonths.setText("Minimum Days:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);

                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                            
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    txtTakenDaysOrMonths.setText(sTakenDays);
                    
                } else if("DAYS".equals(sReduceDatas[1])) {
                    
                    lbToReduceDaysOrMonths.setText("To Reduce Days:");
                    txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);

                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    
                        lbMinimumDaysOrMonths.setText("Minimum Months:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);

                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                           
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {                    
                        lbMinimumDaysOrMonths.setText("Minimum Days:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);

                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                            
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    txtTakenDaysOrMonths.setText(sTakenDays);
                }
            }
            
            String sFormula = dbOp.getFormula(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.parseDouble(headerValues.get("AMOUNT")), "GOLD");
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
            tbOtherCharges.getItems().removeAll(tbOtherCharges.getItems());
            
            double dCardLostCharge = 0;
            if(((RadioButton) rgCustomerCopyGroup.getSelectedToggle()).getText().equals("Not Received")) {
                dCardLostCharge = Double.parseDouble(sCardLostCharge);
            }
            tbOtherCharges.getItems().add(new OtherChargesBean("Card Lost Charge", dCardLostCharge, dCardLostCharge));
                        
            double dNoticeAmount = 0;
            if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sStartDate, sNoticeValues[0])) {
                dNoticeAmount = Double.parseDouble(sNoticeValues[1]);
            }             
            tbOtherCharges.getItems().add(new OtherChargesBean("Notice Charge", dNoticeAmount, dNoticeAmount));
                 
            double dTakenDaysOrMonths = Double.parseDouble(txtTakenDaysOrMonths.getText());
            String[] sFineValues = dbOp.getFineCharges(dTakenDaysOrMonths, "GOLD");
            String sFineStartingMonth = dbOp.getFineStartingMonth("GOLD");
            double dChargeInterest = 0;            
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
                dChargeInterest = Double.parseDouble(sFineValues[3]);
            }
            if(sFineTakenMonths == null) {
                sFineTakenMonths = "0";
            }
            tbOtherCharges.getItems().add(new OtherChargesBean("Fine Charge for " + sFineTakenMonths + " Months", 
                    dChargeInterest, dFineCharge));
            
            double dTotalOtherCharges = 0;
            ObservableList<OtherChargesBean> otherTableValues = tbOtherCharges.getItems();
            for(OtherChargesBean bean : otherTableValues) {
                dTotalOtherCharges = dTotalOtherCharges + bean.getDAmount();
            }
            
            txtTakenAmount.setText(sTakenAmount);
            if(Double.parseDouble(sTakenAmount)>0) {
                txtTakenAmount.setStyle("-fx-background-color: #55FF30");
            } else {
                txtTakenAmount.setStyle("-fx-background-color: #FFFFFF");
            }
            
            txtTotalOtherCharges.setText(Double.toString(dTotalOtherCharges));
            if(dTotalOtherCharges>0) {
                txtTotalOtherCharges.setStyle("-fx-background-color: #55FF30");
            } else {
                txtTotalOtherCharges.setStyle("-fx-background-color: #FFFFFF");
            }
            
            //toget amount or amount to be recieved
            String sToGet = Double.toString((Double.parseDouble(headerValues.get("AMOUNT")) 
                    + Double.parseDouble(sTakenAmount)) 
                    - Double.parseDouble(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"))
                    + dTotalOtherCharges);
            txtToGetAmount.setText(sToGet);
            
            if(tgOn.isSelected()) {
                txtGotAmount.setText(sToGet);
            } else {
                txtDiscountAmount.setText(headerValues.get("DISCOUNT_AMOUNT"));
                txtGotAmount.setText(headerValues.get("GOT_AMOUNT"));
            }
            txtGotAmount.requestFocus();
            txtGotAmount.positionCaret(txtGotAmount.getText().length());
            cbStatus.getItems().removeAll(cbStatus.getItems());
            
            if(tgOn.isSelected()) {
                if(cbOperationType.getSelectionModel().getSelectedItem().equals("BILL CLOSING") 
                        && dbOp.isReadyToDeliver(headerValues.get("REPLEDGE_BILL_ID"), "GOLD")) {
                    cbStatus.getItems().addAll("CLOSED", "DELIVERED");
                    cbStatus.setValue("CLOSED");
                } else if(cbOperationType.getSelectionModel().getSelectedItem().equals("REBILL")) {
                    cbStatus.getItems().addAll("REBILLED", "REBILLED-ADDED", "REBILLED-REMOVED", "REBILLED-MULTIPLE");
                    cbStatus.setValue("REBILLED");
                } else if(cbOperationType.getSelectionModel().getSelectedItem().equals("BILL CLOSING")) {
                    cbStatus.getItems().addAll("CLOSED");
                    cbStatus.setValue("CLOSED");
                }                                
            } else {
                if(null != headerValues.get("STATUS")) switch (headerValues.get("STATUS")) {
                    case "DELIVERED":
                        cbStatus.getItems().addAll("DELIVERED");
                        //btUpdateBill.setDisable(true);
                        break;
                    default:
                        if(dbOp.isReadyToDeliver(headerValues.get("REPLEDGE_BILL_ID"), "GOLD")) {
                            cbStatus.getItems().addAll("CLOSED", "DELIVERED");
                        } else {
                            cbStatus.getItems().addAll("CLOSED");
                            btUpdateBill.setDisable(false);
                        }
                        break;
                }
                
                if(headerValues.get("STATUS").equals("REBILLED") || headerValues.get("STATUS").equals("REBILLED-ADDED")
                    || headerValues.get("STATUS").equals("REBILLED-REMOVED") 
                        || headerValues.get("STATUS").equals("REBILLED-MULTIPLE")) {
                    cbOperationType.setValue("REBILL");
                }
                cbStatus.setValue(headerValues.get("STATUS"));
            }
            
            DataTable advanceAmountDetailValues = dbOp.getAdvanceAmountTableValues(headerValues.get("BILL_NUMBER"), "GOLD");
            txtAdvanceReceiptDetailTotalAmount.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            setAllDetailValuesToFieldInAdvanceReceiptTable(advanceAmountDetailValues);
            
            setAllCreditValuesToTable(headerValues.get("BILL_NUMBER"));
            setAllDebitValuesToTable(headerValues.get("BILL_NUMBER"));
            
            txtReBilledFrom.setText(headerValues.get("REBILLED_FROM"));
            txtReBilledTo.setText(headerValues.get("REBILLED_TO"));
            txtCreatedEmpName.setText(dbOp.getUserName(headerValues.get("CREATED_USER_ID")));
            txtCreatedTime.setText(headerValues.get("CREATED_TIME"));
            txtClosedEmpName.setText(headerValues.get("CLOSED_USER_ID").contains("USR") 
                    ? dbOp.getUserName(headerValues.get("CLOSED_USER_ID"))
                    : headerValues.get("CLOSED_USER_ID"));
            txtClosedTime.setText(headerValues.get("CLOSED_TIME"));
                        
            if(headerValues.get("REPLEDGE_BILL_ID") != null && !headerValues.get("REPLEDGE_BILL_ID").contains(",")) {
                HashMap<String, String> repledgeValues = 
                        dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), "GOLD");
                if(repledgeValues != null) {
                    txtRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
                    txtRepledgeName.setText(repledgeValues.get("REPLEDGE_NAME"));
                    txtRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
                    txtCompanyBillNumber.setText(repledgeValues.get("BILL_NUMBER"));
                    txtRepledgeOpenedDate.setText(repledgeValues.get("REPLEDGE_OPENING_DATE"));
                    txtRepledgeBillAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
                    txtRepledgeBillStatus.setText(repledgeValues.get("REPLEDGE_STATUS"));
                    lbScreenMessage.setText("NOTE: THIS BILL IS IN '"+ repledgeValues.get("REPLEDGE_NAME") +"', IN THE NUMBER '"+ repledgeValues.get("REPLEDGE_BILL_NUMBER") +"'.");
                    lbScreenMessage.setVisible(true);
                } else {
                    lbScreenMessage.setText("");
                    lbScreenMessage.setVisible(false);
                }
            } else {
                txtRepledgeBillId.setText(headerValues.get("REPLEDGE_BILL_ID"));
                StringBuilder sRepBillDetails = new StringBuilder();
                int repBillsCount = 0;
                if(txtRepledgeBillId.getText() != null) {
                    for(String repBId : txtRepledgeBillId.getText().split(",")) {

                        HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(repBId, "GOLD");
                        if(repledgeValues != null) {
                            String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                            String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                            String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                            double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                            String sBillNumber = repledgeValues.get("BILL_NUMBER");
                            String sDate = repledgeValues.get("OPENING_DATE");
                            double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                            String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                            String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                            tbOtherDetailsRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                                    sRepledgeDate, 
                                    dRepledgeAmount, sBillNumber, sDate, dAmount, sStatus, sRepBillId, ""));

                            repBillsCount++;
                            sRepBillDetails.append(Integer.toString(repBillsCount));
                            sRepBillDetails.append(".) ");
                            sRepBillDetails.append(sRepledgeName);
                            sRepBillDetails.append(" - ");
                            sRepBillDetails.append(sRepledgeBillNumber);
                            if(repBillsCount%3 == 0) {
                                sRepBillDetails.append(",\n");
                            } else {
                                sRepBillDetails.append(", ");
                            }
                        }
                    }              
                    if(sRepBillDetails.lastIndexOf(",") >= 0) {
                        sRepBillDetails.deleteCharAt(sRepBillDetails.lastIndexOf(","));
                    }
                    if(sRepBillDetails.lastIndexOf("\n") >= 0) {
                        sRepBillDetails.deleteCharAt(sRepBillDetails.lastIndexOf("\n"));
                    }
                    lbScreenMessage.setText("REBILLED-MULTIPLE: '" + sRepBillDetails.toString() + "'.");
                    lbScreenMessage.setVisible(true);
                }
            }                
            
            sRebilledTo = headerValues.get("REBILLED_TO");
            sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");            
            closeDateRestriction();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }      
    
    public void setCustomerBillsListTableVals(HashMap<String, String> headerValues, String sNoticedDate) {
    
        try {
            DataTable allDetailValues = dbOp.getAllCustomerAllBillValues(headerValues);
            tbCutomerBillsList.getItems().removeAll(tbCutomerBillsList.getItems());
            for(int i=0; i<allDetailValues.getRowCount(); i++) {
                String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
                String sDate = allDetailValues.getRow(i).getColumn(1).toString();
                String sItems = allDetailValues.getRow(i).getColumn(2).toString();
                String sAmount = allDetailValues.getRow(i).getColumn(3).toString();
                String sMaterialType = allDetailValues.getRow(i).getColumn(4).toString();
                String sRepBillId = allDetailValues.getRow(i).getColumn(5).toString();
                String sGrossWt = allDetailValues.getRow(i).getColumn(6).toString();
                String sCompanyId = allDetailValues.getRow(i).getColumn(7).toString();
                tbCutomerBillsList.getItems().add(new CustomerBillsListBean(sRepBillId, sMaterialType, 
                        sBillNumber, sDate, sItems, sGrossWt, sAmount, sNoticedDate, sCompanyId));                
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setAllCreditValuesToTable(String sBillNumber) {
    
        try {
            DataTable creditDetailValues = dbOp.getCreditTableValues(sBillNumber, "GOLD");
            
            int count = 0;
            double totalAmount = 0.0;
            
            tbCreditDetails.getItems().removeAll(tbCreditDetails.getItems());

            for(int i=0; i<creditDetailValues.getRowCount(); i++) {            
                String sSlNo = creditDetailValues.getRow(i).getColumn(0).toString();
                String sId = creditDetailValues.getRow(i).getColumn(1).toString();
                String sDate = creditDetailValues.getRow(i).getColumn(2).toString();
                String sAmountToBe = creditDetailValues.getRow(i).getColumn(3).toString();
                String sAmount = creditDetailValues.getRow(i).getColumn(4).toString();

                double dAmountToBe = Double.parseDouble(sAmountToBe);
                double dAmount = Double.parseDouble(sAmount);
                tbCreditDetails.getItems().add(new CreditDebitBean(sSlNo, sId, sDate, dAmountToBe, dAmount));
                count++;
                totalAmount += dAmount;
            }        
            
            txtCreditCount.setText(Integer.toString(count));
            txtCreditAmount.setText(Double.toString(totalAmount));
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllDebitValuesToTable(String sBillNumber) {
    
        try {
            DataTable debitDetailValues = dbOp.getDebitTableValues(sBillNumber, "GOLD");
            
            int count = 0;
            double totalAmount = 0.0;
            
            tbDebitDetails.getItems().removeAll(tbDebitDetails.getItems());

            for(int i=0; i<debitDetailValues.getRowCount(); i++) {            
                String sSlNo = debitDetailValues.getRow(i).getColumn(0).toString();
                String sId = debitDetailValues.getRow(i).getColumn(1).toString();
                String sDate = debitDetailValues.getRow(i).getColumn(2).toString();
                String sAmountToBe = debitDetailValues.getRow(i).getColumn(3).toString();
                String sAmount = debitDetailValues.getRow(i).getColumn(4).toString();

                double dAmountToBe = Double.parseDouble(sAmountToBe);
                double dAmount = Double.parseDouble(sAmount);
                tbDebitDetails.getItems().add(new CreditDebitBean(sSlNo, sId, sDate, dAmountToBe, dAmount));
                count++;
                totalAmount += dAmount;
            }        
            
            txtDebitCount.setText(Integer.toString(count));
            txtDebitAmount.setText(Double.toString(totalAmount));
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void rbToggleChanged(MouseEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        
        btClearAllClicked(null);
        btSaveBill.setDisable(false);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(true);
        txtBillNumber.setText("");        
        sLastSelectedId = null;        
        cbOperationType.getItems().removeAll(cbOperationType.getItems());
        cbOperationType.getItems().addAll("BILL CLOSING", "REBILL");
        cbOperationType.setValue("BILL CLOSING");
        cbStatus.getItems().removeAll(cbStatus.getItems());
        cbStatus.getItems().addAll("CLOSED", "DELIVERED");
        cbStatus.setValue("CLOSED");
        dpBillClosingDate.setValue(LocalDate.now());
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
            txtGotAmount.setEditable(true);               
            txtGotAmount.setMouseTransparent(false);
            txtGotAmount.setFocusTraversable(true);                
        } else {
            txtGotAmount.setEditable(false);               
            txtGotAmount.setMouseTransparent(true);
            txtGotAmount.setFocusTraversable(false);                
        }

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
            }
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtBillNumber.setText(billRowAndNumber[1]);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        btVerifyAllCopies.setDisable(true);
        
        Platform.runLater(() -> {
            txtBillNumber.requestFocus();
            txtBillNumber.positionCaret(txtBillNumber.getText().length());
        });        

    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        btClearAllClicked(null);
        btSaveBill.setDisable(true);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(false);
        txtBillNumber.setText("");        
        sLastSelectedId = null;
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
            txtGotAmount.setEditable(true);
            txtGotAmount.setMouseTransparent(false);
            txtGotAmount.setFocusTraversable(true);  
        } else {
            txtGotAmount.setEditable(false);
            txtGotAmount.setMouseTransparent(true);
            txtGotAmount.setFocusTraversable(false);  
        }        
        cbOperationType.getItems().removeAll(cbOperationType.getItems());
        cbOperationType.getItems().addAll("BILL CLOSING");
        cbOperationType.setValue("BILL CLOSING");        
        cbStatus.getItems().removeAll(cbStatus.getItems());
        cbStatus.getItems().addAll("CLOSED", "DELIVERED");
        cbStatus.setValue("CLOSED");
                        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                    CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_UPDATE") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateBill.setDisable(false);
            } else {
                btUpdateBill.setDisable(true);
            }
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtBillNumber.setText(billRowAndNumber[1]);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        btVerifyAllCopies.setDisable(false);
        
        Platform.runLater(() -> {
            txtBillNumber.requestFocus();
            txtBillNumber.positionCaret(txtBillNumber.getText().length());
        });        
    }

    @FXML
    void btSaveBillClicked(ActionEvent event) {

        if(sLastSelectedId != null) {
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillClosingDate))
            {
                if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), sBillClosingDate))
                {
                    try {
                        String sLastDuePaidDate = dbOp.getLastDuePaidDate(sLastSelectedId, "GOLD");
                        
                        if(sLastDuePaidDate == null || DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sLastDuePaidDate, sBillClosingDate))
                        {
                            boolean isCustVerfied = false;
                            boolean isCompVerfied = false;
                            boolean isPackVerfied = false;
                                                        
                            String sInterestType = txtInterestType.getText().toUpperCase();
                            String sTotalDaysOrMonths = txtActualTotalDaysOrMonths.getText().toUpperCase();
                            String sMinimumDaysOrMonths = txtMinimumDaysOrMonths.getText().toUpperCase();
                            String sToReduceDaysOrMonths = txtToReduceDaysOrMonths.getText().toUpperCase();
                            String sTakenDaysOrMonths = txtTakenDaysOrMonths.getText().toUpperCase();
                            String sTakenAmount = txtTakenAmount.getText().toUpperCase();
                            String sToGet = txtToGetAmount.getText().toUpperCase();
                            String sGot = txtGotAmount.getText().toUpperCase();
                            String sOperationType = cbOperationType.getValue();
                            String sStatus = cbStatus.getValue().toUpperCase();
                            String sNote = txtNote.getText().toUpperCase();
                            String sOtherCharge = txtTotalOtherCharges.getText();
                            String sDiscount = txtDiscountAmount.getText();
                            String sCustomerCopy = ((RadioButton) rgCustomerCopyGroup.getSelectedToggle()).getText();                                                        
                            
                            
                            double dNoticeCharge = 0;
                            double dFineInterest = 0;
                            double dFineCharge = 0;

                            ObservableList<OtherChargesBean> otherTableValues = tbOtherCharges.getItems();
                            for(OtherChargesBean bean : otherTableValues) {
                                if(bean.getSReason().equals("Notice Charge")) {
                                    dNoticeCharge = bean.getDAmount();
                                } else if(bean.getSReason().equals("Fine Charge")) {
                                    dFineInterest = bean.getDAmountToBe();
                                    dFineCharge = bean.getDAmount();
                                }
                            }
                            
                            int iMinimumDaysOrMonths = Integer.parseInt(!("".equals(sMinimumDaysOrMonths))? sMinimumDaysOrMonths : "0");
                            int iReducedDaysOrMonths = Integer.parseInt(!("".equals(sToReduceDaysOrMonths))? sToReduceDaysOrMonths : "0");
                            double dTakenDaysOrMonths = Double.parseDouble(!("".equals(sTakenDaysOrMonths))? sTakenDaysOrMonths : "0");
                            double dTakenAmount = Double.parseDouble(!("".equals(sTakenAmount))? sTakenAmount : "0");
                            double dToGet = Double.parseDouble(!("".equals(sToGet))? sToGet : "0");
                            double dGot = Double.parseDouble(!("".equals(sGot))? sGot : "0");
                            double dOtherCharges = Double.parseDouble(!("".equals(sOtherCharge))? sOtherCharge : "0");
                            double dDiscount = Double.parseDouble(!("".equals(sDiscount))? sDiscount : "0");                                                        
                            
                            try {                                     
                                if(isValidHeaderValues(dGot)) {                                                                        

                                    if("REBILL".equals(sOperationType)) {
                                        doReBillWork(sLastSelectedId, sBillClosingDate);                                                                             
                                    }
                                    
                                    if(tgOn.isSelected() 
                                            ? dbOp.closeBill(sLastSelectedId, sRebilledTo, sInterestType, sBillClosingDate, 
                                                    sTotalDaysOrMonths, iMinimumDaysOrMonths, iReducedDaysOrMonths, dTakenDaysOrMonths, 
                                                    dTakenAmount, dToGet, dGot, sStatus, sNote, sReduceType, sMinimumType, "GOLD",
                                                    dNoticeCharge, dFineInterest, dFineCharge, dOtherCharges, dDiscount,
                                                    sCustomerCopy, idProofType, idProofNumber) 
                                            : dbOp.updateBill(sLastSelectedId, sInterestType, sBillClosingDate, sTotalDaysOrMonths, 
                                                    iMinimumDaysOrMonths, iReducedDaysOrMonths, dTakenDaysOrMonths, dTakenAmount, 
                                                    dToGet, dGot, sStatus, sNote, sReduceType, sMinimumType, "GOLD",
                                                    dNoticeCharge, dFineInterest, dFineCharge, dOtherCharges, dDiscount,
                                                    sCustomerCopy, idProofType, idProofNumber)) {
                                        
                                        if(sStatus.equals("DELIVERED")) {
                                            dbOp.updateCompanyBillPhysicalLocation(sLastSelectedId, "GOLD", 
                                                    CommonConstants.DELIVERED);
                                        } else if( sStatus.equals("REBILLED") 
                                                || sStatus.equals("REBILLED-ADDED") 
                                                || sStatus.equals("REBILLED-REMOVED")
                                                || sStatus.equals("REBILLED-MULTIPLE")) {
                                            //dbOp.updateCompanyBillPhysicalLocation(sLastSelectedId, "GOLD", 
                                              //      CommonConstants.REBILLED);                                            
                                        }
                                        
                                        btCaptureUserImgClicked(event);
                                        saveImages(sLastSelectedId);                                                                                
                                        
                                        if(tgOn.isSelected()) {
                                            isNewVerifyEntry = true;
                                            btVerifyAllCopiesClicked(event);
                                            PopupUtil.showInfoAlert(event, "Bill " + sLastSelectedId +" closed successfully.");  
                                        } else {
                                            PopupUtil.showInfoAlert(event, "Bill " + sLastSelectedId +" updated successfully.");  
                                        }
                                        btClearAllClicked(null);
                                        
                                        String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                                        if(billRowAndNumber != null) {
                                            txtBillNumber.setText(billRowAndNumber[1]);
                                        }     
                                        //customerWindow.dispose();
                                        Platform.runLater(() -> {
                                            txtBillNumber.requestFocus();
                                            txtBillNumber.positionCaret(txtBillNumber.getText().length());
                                        });        

                                    } else {
                                        PopupUtil.showErrorAlert(event, "Problem in closing bill.");
                                    }
                                } else {
                                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            PopupUtil.showErrorAlert(event, "Sorry bill cannot close before the last advance amount paid date.");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry you cannot close the bill before the opened date.");
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this bill closing date account was closed.");
            }
        } else {
            PopupUtil.showErrorAlert(event, "Not any bill number was selected to close bill.");
        }
    }

    public void doReBillWork(String sBillNumber, String sClosingDate) {
                
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillOpeningScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldBillOpeningController gon = (GoldBillOpeningController) loader.getController();
        gon.sRebilledNewAmt = sRebilledNewAmt;
        gon.sNewWeight = sNewWeight;
        gon.sRebilledDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
        gon.reBill(sBillNumber, sClosingDate, 
                sRepledgeBillId, cbStatus.getValue(), 
                ivCustomerBill.getImage(), ivOpenJewelBill.getImage(), ivUserBill.getImage(),
                this);

        dialog.setTitle("Gold Bill Opening");      
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
    
    public boolean isValidHeaderValues(double dGot)
    {
        return dGot > 0;
    }
    
    @FXML
    private void btUpdateBillClicked(ActionEvent event) {        
        btSaveBillClicked(null);
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        //customerWindow.dispose();
        txtBillNumber.setText("");
        clearAllHeader();
        sLastSelectedId = null;
        sRepledgeBillId = null;
        sRebilledTo = null;
        sReduceType = null;
        sMinimumType = null;
        sRebilledTo = null;
                
        ivOpenCustomerBill.setImage(CommonConstants.noImage);                    
        ivOpenJewelBill.setImage(CommonConstants.noImage);                    
        ivOpenUserBill.setImage(CommonConstants.noImage);                    
        ivCustomerBill.setImage(CommonConstants.noImage);                    
        ivJewelBill.setImage(CommonConstants.noImage);                    
        ivUserBill.setImage(CommonConstants.noImage);    
    }

    @FXML
    private void cbAllDetailsFilterOnAction(ActionEvent event) {
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            if(null != sFilterName) switch (sFilterName) {
                case "DATE":
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(dpAddToFilter))
                        nodeAddToFilter.getChildren().add(1, dpAddToFilter);
                    break;
                case "GENDER":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("MALE", "FEMALE", "OTHER");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                case "SPOUSE TYPE":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("S/O", "F/O", "H/O", "D/O", "M/O", "W/O");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                case "ITEMS":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    DataTable jewelItemValues;
                    try {                
                        jewelItemValues = dbOp.getAllJewelItems("GOLD");
                        for(int i=0; i<jewelItemValues.getRowCount(); i++) {
                            cbAddToFilter.getItems().add(jewelItemValues.getRow(i).getColumn(0).toString());
                        }
                        cbAddToFilter.getSelectionModel().select(0);
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }   break;
                case "STATUS":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("CLOSED", "DELIVERED", "REBILLED");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                default:
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(txtAddToFilter))
                        nodeAddToFilter.getChildren().add(1, txtAddToFilter);
                    break;
            }
        }        
    }

    @FXML
    private void btAddToFilterClicked(ActionEvent event) {        
        
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            String sFilterValue = "";
            if(nodeAddToFilter.getChildren().contains(txtAddToFilter)) {
                sFilterValue = txtAddToFilter.getText();
            }else if(nodeAddToFilter.getChildren().contains(dpAddToFilter)) {
                sFilterValue = CommonConstants.DATETIMEFORMATTER.format(dpAddToFilter.getValue());
            }else if(nodeAddToFilter.getChildren().contains(cbAddToFilter)) {
                sFilterValue = cbAddToFilter.getItems().get(cbAddToFilter.getSelectionModel().getSelectedIndex());
            }
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

    public String getDBColumnNameFor(String filterName) 
    {
    
        switch (filterName)
        {        
            case "BILL NO":
                return "BILL_NUMBER";
            case "DATE":
                return "CLOSING_DATE";
            case "AMOUNT":
                return "AMOUNT";
            case "NAME":
                return "CUSTOMER_NAME";
            case "GENDER":
                return "GENDER";
            case "SPOUSE TYPE":
                return "SPOUSE_TYPE";
            case "SPOUSE NAME":
                return "SPOUSE_NAME";
            case "STREET":
                return "STREET";
            case "AREA":
                return "AREA";
            case "MOBILE NO":
                return "MOBILE_NUMBER";
            case "ITEMS":
                return "ITEMS";
            case "GROSS WEIGHT":
                return "GROSS_WEIGHT";
            case "NET WEIGHT":
                return "NET_WEIGHT";
            case "PURITY":
                return "PURITY";
            case "STATUS":
                return "STATUS";
            case "NOTE":
                return "NOTE";
            default:
                return null;
        }
    }
    
    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        
        try {
            
            DataTable allDetailValues = 
                    dbOp.getAllDetailsValues("GOLD", null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }

    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sAmount = allDetailValues.getRow(i).getColumn(2).toString();
            String sName = allDetailValues.getRow(i).getColumn(3).toString();
            String sGender = allDetailValues.getRow(i).getColumn(4).toString();
            String sSpouseType = allDetailValues.getRow(i).getColumn(5).toString();
            String sSpouseName = allDetailValues.getRow(i).getColumn(6).toString();
            String sStreet = allDetailValues.getRow(i).getColumn(7).toString();
            String sArea = allDetailValues.getRow(i).getColumn(8).toString();
            String sMobileNumber = allDetailValues.getRow(i).getColumn(9).toString();
            String sItems = allDetailValues.getRow(i).getColumn(10).toString();
            String sGrossWeight = allDetailValues.getRow(i).getColumn(11).toString();
            String sNetWeight = allDetailValues.getRow(i).getColumn(12).toString();
            String sPurity = allDetailValues.getRow(i).getColumn(13).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(14).toString();
            String sNote = allDetailValues.getRow(i).getColumn(15).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sBillNumber, sDate, sAmount, sName, sGender, sSpouseType, sSpouseName, sStreet, sArea, sMobileNumber, sItems, sGrossWeight, sNetWeight, sPurity, sStatus, sNote));
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
            if(null != alFilterDBColumnName.get(i)) switch (alFilterDBColumnName.get(i)) {
                case "CLOSING_DATE":
                    sFilterScript += "AND TO_CHAR(" + alFilterDBColumnName.get(i) + ", 'dd-MM-YYYY') ::TEXT LIKE ? ";
                    break;
                case "GENDER":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " = ?::GENDER_TYPE ";
                    break;
                default:
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";
                    break;
            }
        }

        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            allDetailValues = dbOp.getAllDetailsValues("GOLD", sFilterScript, alFilterValue.toArray(sValsArray));
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        if(!hSaveModeButtons.isDisable()) {
            int index = tbAllDetails.getSelectionModel().getSelectedIndex();

            if (event.getClickCount() == 2 && (index >= 0) ) {
                viewClosedBill(null, tbAllDetails.getItems().get(index).getSBillNumber());
            }                
        }
    }

    public void closeBill(String sBillNumber, boolean onlyForView) {
    
        tgOn.setSelected(true);
        saveModeON(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        btSaveBill.setDisable(onlyForView);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }

    public void closeBill(String sBillNumber, String sStatus, boolean onlyForView) {
    
        tgOn.setSelected(true);
        saveModeON(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        btSaveBill.setDisable(onlyForView);
        tpScreen.getSelectionModel().select(tabMainScreen);
        cbStatus.setValue(sStatus);
    }
    
    public void viewBill(String sBillNumber) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        btUpdateBill.setDisable(true);
        hSaveModeButtons.setDisable(true);
        dpBillClosingDate.setEditable(false);
        dpBillClosingDate.setMouseTransparent(true);
        dpBillClosingDate.setFocusTraversable(false);          
        tpScreen.getSelectionModel().select(tabMainScreen);
    }
    
    public void viewClosedBill(ActionEvent event, String sBillNumber) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(event);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }

    public void viewClosedBill(ActionEvent event, String sBillNumber, String sStatus) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        tpScreen.getSelectionModel().select(tabMainScreen);
        if(sStatus.equals("DELIVERED")) {
            verifyAllCopiesWork(event, sBillNumber);
            changeStatusToDelivered(true);
        }  else {
            cbStatus.setValue(sStatus);
        }
    }
    
    public void clearAllHeader()
    {
        dpBillOpeningDate.setValue(LocalDate.now());
        txtCustomerName.setText("");
        txtSpouseName.setText("");
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtMobileNumber.setText("");
        txtItems.setText("");
        txtGrossWeight.setText("");
        txtNetWeight.setText("");
        txtPurity.setText("");
        txtInterestType.setText("");
        lbActualTotalDaysOrMonths.setText("Actual Total Days/Months:");
        txtActualTotalDaysOrMonths.setText("");
        txtMinimumDaysOrMonths.setText("");
        lbToReduceDaysOrMonths.setText("To Reduce Days/Months:");
        txtToReduceDaysOrMonths.setText("");
        txtNote.setText("");        
        txtAmount.setText("");
        txtInterest.setText("");
        txtInterestPerMonth.setText("");
        lbTakenDaysOrMonths.setText("Taken Days/Months:");
        txtTakenDaysOrMonths.setText("");
        txtTakenAmount.setText("");    
        txtTotalAdvanceAmountPaid.setText("");
        txtTotalOtherCharges.setText("");
        txtToGetAmount.setText("");
        txtGotAmount.setText("");
        txtPreStatus.setText("");
        cbOperationType.setValue("BILL CLOSING");
        cbStatus.setValue("CLOSED");
        txtDiscountAmount.setText("");
        txtTotalOtherCharges.setText("");
        tbOtherCharges.getItems().removeAll(tbOtherCharges.getItems());
        tbCutomerBillsList.getItems().removeAll(tbCutomerBillsList.getItems());
        txtAcceptedEndingDate.setText("");
        txtNomineeName.setText("");
        txtRatePerGm.setText("");
        
        txtAdvanceReceiptDetailTotalAmount.setText("");
        tbAdvanceReceiptDetails.getItems().removeAll(tbAdvanceReceiptDetails.getItems());
        txtReBilledFrom.setText("");
        txtReBilledTo.setText("");
        txtCreatedEmpName.setText("");
        txtCreatedTime.setText("");
        txtClosedEmpName.setText("");
        txtClosedTime.setText("");
        
        txtRepledgeBillId.setText("");
        txtRepledgeName.setText("");
        txtRepledgeBillNumber.setText("");
        txtCompanyBillNumber.setText("");
        txtRepledgeOpenedDate.setText("");
        txtRepledgeBillAmount.setText("");
        txtRepledgeBillStatus.setText("");
        tbOtherDetailsRepList.getItems().removeAll(tbOtherDetailsRepList.getItems());
        
        lbScreenMessage.setText("");
        lbScreenMessage.setVisible(false);
    }

    @FXML
    private void dpBillClosingDateTextChanged(ActionEvent event) {
        if(sLastSelectedId != null) {            
            try {
                HashMap<String, String> headerValues = tgOn.isSelected() 
                        ? dbOp.getAllBillingValuesToClose(sLastSelectedId, "GOLD") 
                        : dbOp.getAllClosedBillingValues(sLastSelectedId, "GOLD");
                String sInterestType = dbOp.getInterestType();
                String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths("GOLD", "REDUCTION");
                String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths("GOLD", "MINIMUM");
                String[] sNoticeValues = dbOp.getNoticeValues();
                String sCardLostCharge = dbOp.getCardLostCharge();
                
                if(headerValues != null)
                {
                    setAllHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, 
                            sMinimumDatas, sNoticeValues, sCardLostCharge);
                } 
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }            
            
        }
    }

    private void closeDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
        String sMultiRebillBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue());
        
        if(otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sMultiRebillBillClosingDate))
            {
                if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                    btMultiRebilledClose.setDisable(false);
                } else {
                    btMultiRebilledClose.setDisable(true);
                }                    
            } else {
                btMultiRebilledClose.setDisable(true);
            }
            
            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {
                if(tgOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }                    
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.GOLD_BILL_CLOSING_SCREEN, "ALLOW_UPDATE") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sClosingDate = dbOp.getBillClosedDate(txtBillNumber.getText(), "GOLD");
                            String sClosedDate = CommonConstants.DATETIMEFORMATTER
                                    .format(LocalDate.parse(sClosingDate, CommonConstants.DATETIMEFORMATTER));
                            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                    sClosedDate)) {
                                btUpdateBill.setDisable(false);
                            }
                    } else {
                        btUpdateBill.setDisable(true);
                    }                        
                }
            } else {
                if(tgOn.isSelected()) {
                    btSaveBill.setDisable(true);
                } else {
                    btUpdateBill.setDisable(true);
                }
            }  
        }    
    }
    
    @FXML
    private void cbStatusOnAction(ActionEvent event) {
    
    }

    @FXML
    private void cbOperationTypeOnAction(ActionEvent event) {
        if(tgOn.isSelected()) {
            String sSelectedValue = cbOperationType.getSelectionModel().getSelectedItem();
            cbStatus.getItems().removeAll(cbStatus.getItems());
            if("REBILL".equals(sSelectedValue)) {
                try {
                    cbStatus.getItems().addAll("REBILLED", "REBILLED-ADDED", "REBILLED-REMOVED", "REBILLED-MULTIPLE");
                    cbStatus.setValue("REBILLED");
                    
                    String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                    lbRebillMsg.setVisible(true);
                    lbRebillMsg.setText(" Will be Rebilled To: " + billRowAndNumber[1] + billRowAndNumber[2]);
                } catch (SQLException ex) {
                    Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                cbStatus.getItems().addAll("CLOSED", "DELIVERED");
                cbStatus.setValue("CLOSED");            
                lbRebillMsg.setVisible(false);
            }
        }
    }

    public void setRebilledTo(String sRebilledToBillNumber) {
        this.sRebilledTo = sRebilledToBillNumber;
    }

    @FXML
    private void txtGotAmountOnAction(ActionEvent event) {        
        denominationWork(event);
    }

    private void denominationWork(Event event) {
        double dGotAmt = Double.parseDouble(txtGotAmount.getText());
        
        if(dGotAmt > 0 && "BILL CLOSING".equals(cbOperationType.getSelectionModel().getSelectedItem())) 
        {
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
                String[] denominationDetails = new String[2];
                String operationName = "";
                String denominationBillNumber = sLastSelectedId;
                String materialType = "GOLD";
                boolean isMultiClose = false;
                boolean isMultiOperation = false;
                List<AvailableBalanceBean> currencyList = null;
                List<AvailableBalanceBean> multiClosingCurrencyList = null;  
                List<AvailableBalanceBean> multiOperationCurrencyList = null;  
                List<AvailableBalanceBean> billClosingCurrencyList =
                        dbOp.getDenominationValues(CommonConstants.G_BILL_CLOSING_OPERATION, sLastSelectedId, "GOLD");
                
                if(tgOn.isSelected()) {
                    gon.setParent(this, true);
                } else {
                    gon.setParent(this, false);
                }

                if(billClosingCurrencyList == null) {
                    multiClosingCurrencyList =
                        dbOp.getDenominationValues(CommonConstants.MULTI_CLOSING_OPERATION, sLastSelectedId, "GOLD");                    
                    if(multiClosingCurrencyList == null) {
                        String multiOpBillNumber = sLastSelectedId + "-CLOSE";
                        multiOperationCurrencyList =
                            dbOp.getDenominationValues(CommonConstants.MULTI_OPERATION_OPERATION, multiOpBillNumber, "GOLD"); 
                        if(multiOperationCurrencyList != null) {
                            currencyList = multiOperationCurrencyList;
                            operationName = CommonConstants.MULTI_OPERATION_OPERATION;
                            isMultiClose = false;
                            isMultiOperation = true;
                            denominationDetails =  
                                    dbOp.getDenominationDetails(CommonConstants.MULTI_OPERATION_OPERATION, 
                                            multiOpBillNumber, "GOLD");
                            denominationBillNumber = denominationDetails[0];
                            materialType = denominationDetails[1];                                                
                        }
                    }  else {
                        currencyList = multiClosingCurrencyList;
                        operationName = CommonConstants.MULTI_CLOSING_OPERATION;
                        isMultiClose = true;
                        isMultiOperation = false;
                        denominationDetails =  
                                dbOp.getDenominationDetails(CommonConstants.MULTI_CLOSING_OPERATION, 
                                        sLastSelectedId, "GOLD");
                        denominationBillNumber = denominationDetails[0];
                        materialType = denominationDetails[1];                    
                    }
                } else {                    
                    currencyList = billClosingCurrencyList;
                    operationName = CommonConstants.G_BILL_CLOSING_OPERATION;
                }
                
                
                gon.setInitValues(currencyList, isMultiClose, isMultiOperation);
                Scene scene = new Scene(root);
                dialog.setScene(scene);
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(520);
                dialog.setTitle("OPERATION: " + operationName 
                        + ", BILL NUMBERS: " + denominationBillNumber
                        + ", BILL MATERIAL TYPES: " + materialType);
                dialog.setResizable(false);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(dGotAmt > 0 && "REBILLED".equals(cbStatus.getSelectionModel().getSelectedItem())) 
        {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReBillRecievedAndBalanceAmtDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ReBillRecievedAndBalanceAmtDialog gon = (ReBillRecievedAndBalanceAmtDialog) loader.getController();
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.G_REBILL_CLOSE_OPERATION, sLastSelectedId, "GOLD");
                
                if(tgOn.isSelected()) {
                    gon.setParent(this);
                    gon.setInitValues(true, "REBILLED");
                } else {
                    gon.setParent(this);
                    gon.setInitValues(false, "REBILLED");
                }
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(550);
                dialog.setWidth(1000);
                dialog.setScene(scene);
                if(tgOn.isSelected()) {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" + txtPurity.getText() + "." );
                } else {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" 
                    + txtPurity.getText() + "."
                    + " Rebilled To " + txtReBilledTo.getText());                
                }
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(dGotAmt > 0 && "REBILLED-ADDED".equals(cbStatus.getSelectionModel().getSelectedItem())) 
        {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReBillRecievedAndBalanceAmtDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ReBillRecievedAndBalanceAmtDialog gon = (ReBillRecievedAndBalanceAmtDialog) loader.getController();
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.G_REBILL_CLOSE_OPERATION, sLastSelectedId, "GOLD");
                
                if(tgOn.isSelected()) {
                    gon.setParent(this);
                    gon.setInitValues(true, "REBILLED-ADDED");
                } else {
                    gon.setParent(this);
                    gon.setInitValues(false, "REBILLED-ADDED");
                }
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(550);
                dialog.setWidth(1000);
                dialog.setScene(scene);
                if(tgOn.isSelected()) {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" + txtPurity.getText() + "." );
                } else {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" 
                    + txtPurity.getText() + "."
                    + " Rebilled To " + txtReBilledTo.getText());                
                }
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(dGotAmt > 0 && "REBILLED-REMOVED".equals(cbStatus.getSelectionModel().getSelectedItem())) 
        {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReBillRecievedAndBalanceAmtDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ReBillRecievedAndBalanceAmtDialog gon = (ReBillRecievedAndBalanceAmtDialog) loader.getController();
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.G_REBILL_CLOSE_OPERATION, sLastSelectedId, "GOLD");
                
                if(tgOn.isSelected()) {
                    gon.setParent(this);
                    gon.setInitValues(true, "REBILLED-REMOVED");
                } else {
                    gon.setParent(this);
                    gon.setInitValues(false, "REBILLED-REMOVED");
                }
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(550);
                dialog.setWidth(1000);
                dialog.setScene(scene);
                if(tgOn.isSelected()) {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" + txtPurity.getText() + "." );
                } else {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" 
                    + txtPurity.getText() + "."
                    + " Rebilled To " + txtReBilledTo.getText());                
                }
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(dGotAmt > 0 && "REBILLED-MULTIPLE".equals(cbStatus.getSelectionModel().getSelectedItem())) 
        {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReBillRecievedAndBalanceAmtDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ReBillRecievedAndBalanceAmtDialog gon = (ReBillRecievedAndBalanceAmtDialog) loader.getController();                
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.G_REBILL_CLOSE_OPERATION, sLastSelectedId, "GOLD");
                String sMultiBillNumber = 
                        dbOp.getDenominationMultiBillNumbers(CommonConstants.G_REBILL_CLOSE_OPERATION, 
                                sLastSelectedId, "GOLD")[0];
                if(tgOn.isSelected()) {
                    gon.setParent(this);
                    gon.setInitValues(true, "REBILLED-MULTIPLE", txtReBilledTo.getText());
                } else {
                    gon.setParent(this);
                    gon.setInitValues(false, "REBILLED-MULTIPLE", txtReBilledTo.getText());
                }
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(550);
                dialog.setWidth(1000);
                dialog.setScene(scene);
                if(tgOn.isSelected()) {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" + txtPurity.getText() + "." );
                } else {
                    dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" 
                    + txtPurity.getText() + "."
                    + " All Multi Rebilled From Numbers " + sMultiBillNumber
                    + " Rebilled To " + txtReBilledTo.getText());                
                }
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            PopupUtil.showErrorAlert(event, "Got Amount should be greater than zero.");
        }            
    }
    
    @FXML
    private void txtDiscountAmountOnPress(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                String sToGetAmount = txtToGetAmount.getText();
                String sDiscountAmount = txtDiscountAmount.getText().trim();
                try {
                    double dToGetAmount = Double.parseDouble(sToGetAmount);
                    double dDiscountAmount = sDiscountAmount.isEmpty() ? 0 : Double.parseDouble(sDiscountAmount);
                    txtGotAmount.setText(Double.toString(dToGetAmount - dDiscountAmount));
                } catch (NumberFormatException ex) {
                    txtGotAmount.setText(sToGetAmount);
                }
            });
        }
    }

    @FXML
    private void txtDiscountAmountOnType(KeyEvent e) {
        if (!("0123456789.".contains(e.getCharacter()))) {
            e.consume();
            return;
        }
        Platform.runLater(() -> {
            String sToGetAmount = txtToGetAmount.getText();
            String sDiscountAmount = txtDiscountAmount.getText().trim();
            try {
                double dToGetAmount = Double.parseDouble(sToGetAmount);
                double dDiscountAmount = sDiscountAmount.isEmpty() ? 0 : Double.parseDouble(sDiscountAmount);
                txtGotAmount.setText(Double.toString(dToGetAmount - dDiscountAmount));
            } catch (NumberFormatException ex) {
                txtGotAmount.setText(sToGetAmount);
            }
        });
    }

    @FXML
    private void txtDiscountAmountOnAction(ActionEvent event) {
        
        Platform.runLater(() -> {
            txtGotAmount.requestFocus();
            txtGotAmount.positionCaret(txtGotAmount.getText().length());
        });                
    }

    @FXML
    private void btOpenInRepledgeBillClosingClicked(ActionEvent event) {
        
        String sRepBillId = txtRepledgeBillId.getText();
        if(sRepBillId != null && !sRepBillId.isEmpty()) {

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
            gon.closeBill(sRepBillId, true);

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
    private void tbCutomerBillsListOnMouseClicked(MouseEvent event) {
        
        int index = tbCutomerBillsList.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            if(dialog != null) {
                dialog.close();
                dialog = null;
            }

            String sMaterialType = tbCutomerBillsList.getItems().get(index).getMaterialType();

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
                gon.closeBill(tbCutomerBillsList.getItems().get(index).getBillNumber(), true);

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
                gon.closeBill(tbCutomerBillsList.getItems().get(index).getBillNumber(), true);

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
        }                
    }

    public void doRebilledMultipleOperation(ObservableList<com.magizhchi.pawnbroking.billcalculator.AllDetailsBean> beans, 
                Image customerImage, Image jewelImage) {
        tpScreen.getSelectionModel().select(tabRebiledMultiple);
        tbClosingList.getItems().removeAll(tbClosingList.getItems());
        for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : beans) {
            if(bean.isBChecked()) {
                if(bean.getSBOrA().equals("B") && bean.getSOperation().equals("BILL CLOSING")) {
                    txtClosingBillNumber.setText(bean.getSBillNumber());
                    txtClosingBillNumberOnAction(null);
                } else if(bean.getSBOrA().equals("B") && bean.getSOperation().equals("BILL OPENING")) {
                    txtMultiRebilledOpeningAmt.setText(bean.getSAmount());
                    txtMultiRebilledOpeningAmtOnAction(null);
                }
            }
        }      

        if(customerImage != null) {                            
            ivCustomerBillRebilMultiple.setImage(customerImage);
            isCloseCustomerImgAvailable = true;
        }

        if(jewelImage != null) {                            
            ivJewelBillRebilMultiple.setImage(jewelImage);
            isCloseJewelImgAvailable = true;
        }        
    }



    @FXML
    private void txtClosingBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtClosingBillNumber.getText();
        String sMaterial = "GOLD";
        
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
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
                    PopupUtil.showInfoAlert(event, "Silver bill cannot be added to gold multi rebill.");
                    initializeTxtClosingBillNumber();
                    return;
                }
            }            
        }        
        
        try {
            
            for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : tbClosingList.getItems()) {
                if(bean.getSBillNumber().equals(sBillNumber)) {
                    PopupUtil.showErrorAlert(event, "Same bill number already exists in list.");
                    initializeTxtClosingBillNumber();
                    return;
                }
            }
            
            HashMap<String, String> headerValues = dbOp.getAllBillingValuesToClose(sBillNumber, sMaterial);                                
            String sInterestType = dbOp.getInterestType();
            String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sMaterial, "REDUCTION");
            String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sMaterial, "MINIMUM");

            if(headerValues != null)
            {
                setCloseHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                sReduceType = sReduceDatas[1];
                sMinimumType = sMinimumDatas[1];
                sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");   
                
                initializeTxtClosingBillNumber();
            } else {
                PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }

    private void initializeTxtClosingBillNumber() {
        try {
            txtClosingBillNumber.setText("");
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtClosingBillNumber.setText(billRowAndNumber[1]);
            }
            Platform.runLater(() -> {
                txtClosingBillNumber.requestFocus();
                txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());
            });
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void txtAmountOnType(KeyEvent event) {
    }

    @FXML
    private void btRemoveFromTableClicked(ActionEvent event) {
        tbClosingList.getItems().removeAll(tbClosingList.getItems());
        tbRepList.getItems().removeAll(tbRepList.getItems());
        txtMultiRebilledItems.setText("");
        txtTotalWt.setText("0");
        txtCloseCapital.setText("0");
        txtCloseInterest.setText("0");
        txtCloseInterested.setText("0");
        txtOpenCapital.setText("0");
        txtOpenInterest.setText("0");
        txtOpenInterested.setText("0");
        txtAmtToGet.setText("0");
        Platform.runLater(() -> {
            txtClosingBillNumber.requestFocus();
            txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());                
        });        
    }

    @FXML
    private void txtAmtToGetOnAction(ActionEvent event) {
        
        double dGotAmt = Double.parseDouble(txtAmtToGet.getText());
        
        if(dGotAmt > 0 && isValidStructureForMultiRebill()) 
        {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReBillRecievedAndBalanceAmtDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ReBillRecievedAndBalanceAmtDialog gon = (ReBillRecievedAndBalanceAmtDialog) loader.getController();
                StringBuilder sb = new StringBuilder();
                for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : tbClosingList.getItems()) {                        
                    if(bean.getSOperation().equals("BILL CLOSING")) {                            
                        sb.append(bean.getSBillNumber());
                        sb.append(",");
                    }
                }
                if(sb.lastIndexOf(",") > 0) {
                    sb.deleteCharAt(sb.lastIndexOf(","));
                }
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.G_REBILL_CLOSE_OPERATION, sb.toString(), "GOLD");
                if(tgOn.isSelected()) {
                    gon.setParent(this);
                    gon.setInitValues(true, "REBILLED-MULTIPLE", sb.toString());
                } else {
                    gon.setParent(this);
                    gon.setInitValues(false, "REBILLED-MULTIPLE", sb.toString());
                }
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(550);
                dialog.setWidth(1000);
                dialog.setScene(scene);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  else {
            PopupUtil.showErrorAlert(event, "Bill closing should be more than one and bill opening should be only one.");
        }
    }

    @FXML
    private void tbClosingListOnMouseClicked(MouseEvent event) {
    }

    @FXML
    private void tbRNYDeliveredOnMouseClicked(MouseEvent event) {
        int index = tbOtherDetailsRepList.getSelectionModel().getSelectedIndex();        
        
        if(event.getClickCount() == 2 && (index >= 0)) 
        {
            String status = tbOtherDetailsRepList.getItems().get(index).getSRepledgeStatus();
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            if("GIVEN".equals(status) || "OPENED".equals(status)) {
                gon.closeBill(tbOtherDetailsRepList.getItems().get(index).getSRepledgeBillId(), true);
            } else {
                gon.viewBill(tbOtherDetailsRepList.getItems().get(index).getSRepledgeBillId());
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
    
    public void setCloseHeaderValuesToFields(HashMap<String, String> headerValues, String sInterestType, 
            String[] sReduceDatas, String[] sMinimumDatas)
    {
        try {

            double dTakenDaysOrMonths = 0;
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = CommonConstants.DATETIMEFORMATTER.format(LocalDate.parse(headerValues.get("OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue());
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            String sMaterial = "GOLD";
            String[] sNoticeValues = dbOp.getNoticeValues();            
            
            if("MONTH".equals(sInterestType)) {
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        dTakenDaysOrMonths = dTakenMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        dTakenDaysOrMonths = dTakenMonths;
                        sTakenMonths = Double.toString(dTakenMonths);                       
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), sMaterial) : 0;
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
            
            String sFormula = dbOp.getFormula(CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue()), Double.parseDouble(headerValues.get("AMOUNT")), sMaterial);
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
            String sBillNumber = txtClosingBillNumber.getText();
            
            String sAmount = headerValues.get("AMOUNT") != null 
                    ? headerValues.get("AMOUNT") : "0";
            
            String sGrsWt = headerValues.get("GROSS_WEIGHT") != null 
                    ? headerValues.get("GROSS_WEIGHT") : "0";
            
            double dRatePerGm = Double.parseDouble(sAmount)/Double.parseDouble(sGrsWt);
            
            tbClosingList.getItems().add(new com.magizhchi.pawnbroking.billcalculator.AllDetailsBean("B", "BILL CLOSING", sBillNumber, 
                    headerValues.get("OPENING_DATE"), sMaterial, headerValues.get("ITEMS"),
                    sAmount, headerValues.get("INTEREST"), 
                    sTakenAmount, sToGet, sCombo, sRepledgeBillId, 
                    headerValues.get("CUSTOMER_NAME"), sGrsWt, true, String.valueOf(Math.round(dRatePerGm))));
            
            String totItems = txtMultiRebilledItems.getText();
            if(totItems == null || totItems.isEmpty()) {
                totItems = headerValues.get("ITEMS");
            } else {
                totItems = totItems + ", \n" + headerValues.get("ITEMS");
            }
            txtMultiRebilledItems.setText(totItems);
            
            if(headerValues.get("REPLEDGE_BILL_ID") != null && !headerValues.get("REPLEDGE_BILL_ID").contains(",")) {
                doMultiRebilledRepledgeTableWork(sRepledgeBillId);
            } else {
                if(headerValues.get("REPLEDGE_BILL_ID") != null) {
                    for(String repBId : headerValues.get("REPLEDGE_BILL_ID").split(",")) {
                        HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(repBId, "GOLD");
                        if(repledgeValues != null) {
                            String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                            String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                            String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                            double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                            String sCBillNumber = repledgeValues.get("BILL_NUMBER");
                            String sDate = repledgeValues.get("OPENING_DATE");
                            double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                            String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                            String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                            tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                                    sRepledgeDate, 
                                    dRepledgeAmount, sCBillNumber, sDate, dAmount, sStatus, sRepBillId, ""));
                        }
                    }
                }
            }
            closeDateRestriction();
            calculateTotalAmtToBeRecieved();            
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doMultiRebilledRepledgeTableWork(String sRepledgeBillId) {
        try {
            HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(sRepledgeBillId, "GOLD");
            if(repledgeValues != null) {
                String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                String sBillNumber = repledgeValues.get("BILL_NUMBER");
                String sDate = repledgeValues.get("OPENING_DATE");
                double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                        sRepledgeDate, 
                        dRepledgeAmount, sBillNumber, sDate, dAmount, sStatus, sRepledgeBillId, ""));
            }
        } catch (SQLException ex) {
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
        
        for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : tbClosingList.getItems()) {
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
        } else {
            sTotalAmtToGet = sTotalAmtToGet.replace("-", "");
            lbAmtToGet.setText("Amount to Give:");        
        }
        
        txtOpenCapital.setText(sTotOpenCapAmt);
        txtCloseCapital.setText(sTotCloseCapAmt);
        txtOpenInterest.setText(sOpenIntr);
        txtCloseInterest.setText(sCloseIntr);
        txtOpenInterested.setText(sOpen);
        txtCloseInterested.setText(sClose);
        txtTotalWt.setText(sTotWt);  
        txtAmtToGet.setText(sTotalAmtToGet);        
    }

    @FXML
    private void txtMultiRebilledOpeningAmtOnAction(ActionEvent event) {
        
        for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : tbClosingList.getItems()) {
            if(bean.getSOperation().equals("BILL OPENING")) {
                PopupUtil.showErrorAlert(event, "Only one Bill Opening can be done in Rebilled-Multiple.");
                return;
            }
        }
        
        double dAmt = Double.parseDouble(txtMultiRebilledOpeningAmt.getText());
        String sMaterial = "GOLD";       
        setMultiRebilledOpeningAmountRelatedText(dAmt, sMaterial);
        txtAmount.setText("");
        txtAmount.requestFocus();        
    }
    
    public void setMultiRebilledOpeningAmountRelatedText(double dAmount, String sMaterialType) {
    
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
                        
            String sBOrA = "B";
            String sOperation = "BILL OPENING";
            String sTotWt = txtTotalWt.getText();
            String sItems = txtMultiRebilledItems.getText();
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            String openingBillNUmber = billRowAndNumber[1] + billRowAndNumber[2];
            
            tbClosingList.getItems().add(new com.magizhchi.pawnbroking.billcalculator.AllDetailsBean(sBOrA, sOperation, openingBillNUmber, 
                    CommonConstants.DATETIMEFORMATTER.format(LocalDate.now()), sMaterialType, "",
                    String.valueOf(dAmount), sTakenAmount, sTakenAmount, sToGive, "", "", "", sTotWt, true, ""));
            
            calculateTotalAmtToBeRecieved();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean isValidStructureForMultiRebill() {
        
        int bo = 0;
        int bc = 0;
        
        for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : tbClosingList.getItems()) {
            if(bean.getSOperation().equals("BILL OPENING")) {
                bo++;
            } else if(bean.getSOperation().equals("BILL CLOSING")) {
                bc++;
            }
        }
        return bo == 1 && bc > 1;
    }
    
    @FXML
    private void btMultiRebilledCloseOnAction(ActionEvent event) {
        

        StringBuilder sFromBillNumbers = new StringBuilder();
        StringBuilder sFromRepBillIds = new StringBuilder();
        
        if(isValidStructureForMultiRebill()) {
            String sMultiClosingDate = CommonConstants.DATETIMEFORMATTER
                    .format(dpMultiRebilledClosingDate.getValue());
            for(com.magizhchi.pawnbroking.billcalculator.AllDetailsBean bean : tbClosingList.getItems()) {
                if(bean.getSOperation().equals("BILL CLOSING")) {
                    try {
                        dpBillClosingDate.setValue(LocalDate.parse(sMultiClosingDate, CommonConstants.DATETIMEFORMATTER));
                        txtBillNumber.setText(bean.getSBillNumber());
                        txtBillNumberOnAction(null);
                        cbStatus.setValue("REBILLED-MULTIPLE");
                        String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                        setRebilledTo(billRowAndNumber[1] + billRowAndNumber[2]);
                        
                        if(isCloseCustomerImgAvailable) {                            
                            ivCustomerBill.setImage(ivCustomerBillRebilMultiple.getImage());
                            isCloseCustomerImgAvailable = true;
                        }
                        if(isCloseJewelImgAvailable) {
                            ivJewelBill.setImage(ivJewelBillRebilMultiple.getImage());
                            isCloseJewelImgAvailable = true;
                        }
                        
                        btSaveBillClicked(null);
                        sFromBillNumbers.append(bean.getSBillNumber());
                        sFromBillNumbers.append(",");
                        if(bean.getSRepledgeBillId() != null) {
                            sFromRepBillIds.append(bean.getSRepledgeBillId());
                            sFromRepBillIds.append(",");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }                
            }
            if(sFromBillNumbers.length() > 0) {
                sFromBillNumbers.deleteCharAt(sFromBillNumbers.lastIndexOf(","));
            }
            if(sFromRepBillIds.length() > 0) {
                sFromRepBillIds.deleteCharAt(sFromRepBillIds.lastIndexOf(","));
            }
            
            String gotAmt = "";
            if(lbAmtToGet.getText().contains("GET")) {
                gotAmt = txtAmtToGet.getText();
            } else {
                gotAmt = "-" + txtAmtToGet.getText();
            }
            txtGotAmount.setText(txtAmtToGet.getText());            
            doMultiReBillWork(sFromBillNumbers.toString(), sFromRepBillIds.toString(), sMultiClosingDate);
        } else {
            PopupUtil.showErrorAlert(event, "Bill closing should be more than one and bill opening should be only one.");
        }        
    }
    
    public void doMultiReBillWork(String sBillNumbers, String sRepledgeBillIds, String sClosingDate) {
                
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillOpeningScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        GoldBillOpeningController gon = (GoldBillOpeningController) loader.getController();
        gon.sRebilledNewAmt = txtOpenCapital.getText();
        gon.sNewWeight = String.format("%.3f", Double.parseDouble(txtTotalWt.getText()));
        gon.sRebilledDate = CommonConstants.DATETIMEFORMATTER.format(dpMultiRebilledClosingDate.getValue());
        
        File tempLoc = new File(CommonConstants.TEMP_FILE_LOCATION);
        Image custImage = null;
        if(isCloseCustomerImgAvailable) {
            if(tempLoc.exists()) {
                try {
                    custImage = ivCustomerBillRebilMultiple.getImage();
                    BufferedImage image = SwingFXUtils.fromFXImage(custImage, null);
                    File temp = new File(tempLoc, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
                    ImageIO.write(image, "PNG", temp);
                    image.flush();                    
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        Image jewelImage = null;
        if(isCloseJewelImgAvailable) {
            if(tempLoc.exists()) {
                try {
                    jewelImage = ivJewelBillRebilMultiple.getImage();
                    BufferedImage image = SwingFXUtils.fromFXImage(jewelImage, null);
                    File temp = new File(tempLoc, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
                    ImageIO.write(image, "PNG", temp);
                    image.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        gon.reBillMultiple(sBillNumbers, sClosingDate, 
                sRepledgeBillIds, txtMultiRebilledItems.getText(), 
                gon.sNewWeight, lbAmtToGet.getText(), txtAmtToGet.getText(), 
                custImage, jewelImage,
                this);

        dialog.setTitle("Gold Bill Opening");      
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
    private void tabRebilledMultipleSelectionChanged(Event event) {
        if(tabRebiledMultiple.isSelected()) {
            try {
                String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                if(billRowAndNumber != null) {
                    txtClosingBillNumber.setText(billRowAndNumber[1]);
                    lbMultiRebillNewNumberMsg.setText(" Will be Rebilled To: " + billRowAndNumber[1] + billRowAndNumber[2]);
                }
                Platform.runLater(() -> {
                    txtClosingBillNumber.requestFocus();
                    txtClosingBillNumber.positionCaret(txtClosingBillNumber.getText().length());                
                });
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }

    @FXML
    private void rbCustomerCopyToggleChanged(MouseEvent event) {        
        if(sLastSelectedId != null) {
            OtherChargesBean[] bean = new OtherChargesBean[3];
            bean[0] = tbOtherCharges.getItems().get(0);
            bean[1] = tbOtherCharges.getItems().get(1);
            bean[2] = tbOtherCharges.getItems().get(2);
            double dTotalOtherCharges = 0;
            double dCardLostCharge = 0;
                    
            if(((RadioButton) rgCustomerCopyGroup.getSelectedToggle()).getText().equals("Not Received")) {
                try {
                    btCardLostPrint.setDisable(false);
                    showCustomerCopyDialog(event);
                    String sCardLostCharge = dbOp.getCardLostCharge();
                    dCardLostCharge = Double.parseDouble(sCardLostCharge);                                        
                } catch (SQLException ex) {
                    Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                }                
            } else {
                idProofType = null;
                idProofNumber = null;
                btCardLostPrint.setDisable(true);
            }

            bean[0].setDAmount(dCardLostCharge);
            bean[0].setDAmountToBe(dCardLostCharge);
            tbOtherCharges.getItems().removeAll(tbOtherCharges.getItems());
            tbOtherCharges.getItems().add(bean[0]);
            tbOtherCharges.getItems().add(bean[1]);
            tbOtherCharges.getItems().add(bean[2]);                    
            ObservableList<OtherChargesBean> otherTableValues = tbOtherCharges.getItems();
            for(OtherChargesBean beanS : otherTableValues) {
                dTotalOtherCharges = dTotalOtherCharges + beanS.getDAmount();
            }            
            //amount related work
            txtTotalOtherCharges.setText(Double.toString(dTotalOtherCharges));
            if(dTotalOtherCharges>0) {
                txtTotalOtherCharges.setStyle("-fx-background-color: #55FF30");
            } else {
                txtTotalOtherCharges.setStyle("-fx-background-color: #FFFFFF");
            }

            //toget amount or amount to be recieved
            String sToGet = Double.toString((Double.parseDouble(txtAmount.getText()) 
                    + Double.parseDouble(txtTakenAmount.getText())) 
                    - Double.parseDouble(txtTotalAdvanceAmountPaid.getText())
                    + dTotalOtherCharges);
            txtToGetAmount.setText(sToGet);

            if(tgOn.isSelected()) {
                txtGotAmount.setText(sToGet);
            }
            
        }        
    }
    
    private void showCustomerCopyDialog(MouseEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerCopyDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomerCopyUIController gon = (CustomerCopyUIController) loader.getController();
        gon.setParent(this, "GOLD");
        if(idProofNumber != null) {
            gon.setInitValues(idProofType, idProofNumber);        
        }
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setTitle("Customer Copy Missed");
        dialog.setResizable(false);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }

    @FXML
    public void btCardLostPrintClicked(ActionEvent event) {
        
        if(txtSpouseName.getText() != null && !txtSpouseName.getText().isEmpty()) {
        
            try {
                String sCompanyFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\cardlostbond.jasper";                        
                
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("CUSTOMER_NAME", txtCustomerName.getText() + " "
                        +cbSpouseType.getValue() + " " + txtSpouseName.getText());
                parameters.put("CUSTOMER_ADDRESS", txtDoorNo.getText() + ", " 
                        +txtStreetName.getText()+","
                        +txtArea.getText()+","
                        +txtCity.getText()+".");
                String sBillOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
                String[] boDate = sBillOpeningDate.split("-");
                parameters.put("YEAR", boDate[2]);
                parameters.put("MONTH", boDate[1]);
                parameters.put("DAY", boDate[0]);
                parameters.put("AMOUNT", txtAmount.getText());
                parameters.put("BILL_NUMBER", txtBillNumber.getText());
                String sItems = txtItems.getText().replace("\n", " ");
                parameters.put("ITEMS", sItems);
                parameters.put("LAWYER_ADDRESS", "");
                parameters.put("LAWYER_NAME", "");
                String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
                parameters.put("TODAYS_DATE", sBillClosingDate);

                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();                
                noticeUtil.mergeaAndGenerateNoticeIndividual("Bond", parameters, 
                        sCompanyFileName);
                //noticeUtil.mergeAndGenerateNoticeOperation(sCompanyFileName, sCustomerFileName, parameters);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert(event, "Not any details filled to print the records. ");
        }
        
    }
    
    @FXML
    private void btCaptureCustomerImgClicked(ActionEvent event) {
        //Platform.runLater(() -> {
            captureCustomerImage(event, false);
        //});
    }

    private void captureCustomerImage(ActionEvent event, boolean forMultiRebill) {
        isCloseCustomerImgAvailable = false;
        String sCustomerCamName = otherSettingValues.getRow(0).getColumn(2).toString();
        if(!cbCustomerImageCameraName.getValue().equals("DEFAULT"))
        {
            sCustomerCamName = cbCustomerImageCameraName.getValue();
        }
        if(!sCustomerCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {
            try {
                File billNumberFolder = new File(materialFolder, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
                WebCamWork.captureImageFrom(sCustomerCamName, billNumberFolder.getAbsolutePath());
                try (FileInputStream fis = new FileInputStream(billNumberFolder.getAbsolutePath())) {
                    final Image img = new Image(fis);
                    if(forMultiRebill) {
                        ivCustomerBillRebilMultiple.setImage(img);
                    } else {
                        ivCustomerBill.setImage(img);
                    }
                    isCloseCustomerImgAvailable = true;
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
    private void btCaptureJewelImgClicked(ActionEvent event) {
        //Platform.runLater(() -> {
            captureJewelImage(event, false);            
        //});
    }
    
    private void captureJewelImage(ActionEvent event, boolean forMultiRebill) {

        isCloseJewelImgAvailable = false;
        String sJewelCamName = otherSettingValues.getRow(0).getColumn(3).toString();
        if(!cbJewelImageCameraName.getValue().equals("DEFAULT"))
        {
            sJewelCamName = cbJewelImageCameraName.getValue();
        }
        if(!sJewelCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {
            try {
                File billNumberFolder = new File(materialFolder, CommonConstants.CLOSE_JEWEL_IMAGE_NAME);
                WebCamWork.captureImageFrom(sJewelCamName, billNumberFolder.getAbsolutePath());
                try (FileInputStream fis = new FileInputStream(billNumberFolder.getAbsolutePath())) {
                    final Image img = new Image(fis);
                    if(forMultiRebill) {
                        ivJewelBillRebilMultiple.setImage(img);
                    } else {
                        ivJewelBill.setImage(img);
                    }
                    isCloseJewelImgAvailable = true;
                    changeStatusToDelivered(false);
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
    
    private void btCaptureUserImgClicked(ActionEvent event) {
        isCloseUserImgAvailable = false;
        String sJewelCamName = otherSettingValues.getRow(0).getColumn(4).toString();
        if(!cbUserImageCameraName.getValue().equals("DEFAULT"))
        {
            sJewelCamName = cbUserImageCameraName.getValue();
        }
        if(!sJewelCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {
            try {
                File billNumberFolder = new File(materialFolder, txtBillNumber.getText());
                if(!billNumberFolder.exists()) {
                    billNumberFolder.mkdir();
                }
                File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_USER_IMAGE_NAME);
                WebCamWork.captureImageFrom(sJewelCamName, custTemp.getAbsolutePath());
                isCloseUserImgAvailable = true;
            } catch (FrameGrabber.Exception ex) {
                PopupUtil.showInfoAlert(event, "Invalid camera name was selected. "); 
            }
        } else {
            //PopupUtil.showInfoAlert("Not any camera was selected to take pic. ");                
        }
    }

    @FXML
    private void btOpenRebilledToBillClicked(ActionEvent event) {
        if(txtReBilledTo.getText() != null || !txtReBilledTo.getText().isEmpty()) {
            try {
                String sStatus = dbOp.getBillStatus(txtReBilledTo.getText(), "GOLD");
                if(sStatus.equals("OPENED") || sStatus.equals("LOCKED")) {
                    closeBill(txtReBilledTo.getText(), false);
                } else {
                    viewBill(txtReBilledTo.getText());
                }
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
   
    @FXML
    private void ivCustomerImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivCustomerBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivCustomerBill.getImage(), "CUSTOMER IMAGE");
        }
    }

    @FXML
    private void ivJewelImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivJewelBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivJewelBill.getImage(), "JEWEL IMAGE");
        }
    }

    @FXML
    private void ivUserImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivUserBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivUserBill.getImage(), "USER IMAGE");
        }        
    }

    @FXML
    private void ivOpenCustomerImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivOpenCustomerBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivOpenCustomerBill.getImage(), "OPEN CUSTOMER IMAGE");
        }
    }

    @FXML
    private void ivOpenJewelImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivOpenJewelBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivOpenJewelBill.getImage(), "OPEN JEWEL IMAGE");
        }
    }

    @FXML
    private void ivOpenUserImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivOpenUserBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivOpenUserBill.getImage(), "OPEN USER IMAGE");
        }        
    }    

    @FXML
    private void btCaptureCustomerImgRebillMultiClicked(ActionEvent event) {
        //Platform.runLater(() -> {
            captureCustomerImage(event, true);
        //});        
    }

    @FXML
    private void btJewelImgRebillMultiClicked(ActionEvent event) {
        //Platform.runLater(() -> {
            captureJewelImage(event, true);
        //});        
    }
    
    private void saveImages(String sBillNumber) throws FileNotFoundException, IOException {
        
        File billNumberFolder = new File(materialFolder, sBillNumber);
        
        if(isCloseCustomerImgAvailable) {              
            if(!billNumberFolder.exists()) {
                billNumberFolder.mkdir();
            }
            File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(ivCustomerBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }

        if(isCloseJewelImgAvailable) {              
            if(!billNumberFolder.exists()) {
                billNumberFolder.mkdir();
            }
            File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_JEWEL_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(ivJewelBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }

        /*if(isCloseUserImgAvailable) {              
            if(!billNumberFolder.exists()) {
                billNumberFolder.mkdir();
            }
            File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_USER_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(ivUserBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }
        System.gc();*/
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
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
        gon.beforeClose(tbCutomerBillsList.getItems(), sBillClosingDate);

        dialog.setTitle("Bill Calculator");      
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
        
    }
    
    public void autoCloseBill(String closingDate, String billNumber, Image customerImage, Image jewelImage) {
    
        dpBillClosingDate.setValue(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
        txtBillNumber.setText(billNumber);
        txtBillNumberOnAction(null);

        if(customerImage != null) {                            
            ivCustomerBill.setImage(customerImage);
            isCloseCustomerImgAvailable = true;
        }

        if(jewelImage != null) {                            
            ivJewelBill.setImage(jewelImage);
            isCloseJewelImgAvailable = true;
        }
        
        btSaveBillClicked(null);        
    }

    @FXML
    private void btDenomClicked(ActionEvent event) {
        denominationWork(event);
    }

    @FXML
    private void btVerifyAllCopiesClicked(ActionEvent event) {
        verifyAllCopiesWork(event, null);
    }

    private void verifyAllCopiesWork(ActionEvent event, String billNumber) {
        
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(5).toString())) {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            if(event != null) {
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            }            
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoldCloseVerifyCopiesDialog.fxml"));
            Parent root = null;
            try {
                root = (Parent) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            Scene scene = new Scene(root);

            GoldCloseVerifyCopiesDialogUIController gon = (GoldCloseVerifyCopiesDialogUIController) loader.getController();
            
            String sameName = txtCustomerName.getText() + " "
                        +cbSpouseType.getValue() + " " + txtSpouseName.getText();
            boolean isCardReceived = false;
            if(((RadioButton) rgCustomerCopyGroup.getSelectedToggle()).getText().equals("Not Received")) {
                isCardReceived = false;
            } else {
                isCardReceived = true;
            }
            if(isNewVerifyEntry) {
                String nomineeName = txtNomineeName.getText() != null 
                        ? txtNomineeName.getText().toString().trim()
                        : "";
                gon.setParent(this, sLastSelectedId, sameName, 
                        nomineeName, 
                        isCardReceived, idProofType, idProofNumber);                
            } else {
                if(billNumber != null) {
                    String packingCopyCode = billNumber + "-G-PACK";
                    gon.setParent(event, this, sLastSelectedId, isNewVerifyEntry, isCardReceived, packingCopyCode);
                } else {
                    gon.setParent(event, this, sLastSelectedId, isNewVerifyEntry, isCardReceived, null);
                }
            }
            /*if(isNewVerifyEntry) {
                gon.initFocus();
            }*/
            //dialog.setX(180);
            //dialog.setY(100);
            dialog.setHeight(450);
            dialog.setWidth(500);
            dialog.setScene(scene);
            dialog.setTitle("Verify Copies for bill " + sLastSelectedId);
            dialog.showAndWait();                        
            isNewVerifyEntry = false;
        }            
    }
    public void changeStatusToDelivered(boolean isDBUpdate) {
        try {
            if((txtRepledgeBillId.getText() == null || txtRepledgeBillId.getText().isEmpty()
                    || txtRepledgeBillStatus.getText().equals("SUSPENSE"))                
                    && !cbOperationType.getValue().equals("REBILL")) {

                cbStatus.setValue("DELIVERED");
                txtPhysicalLocation.setText(CommonConstants.DELIVERED);
                if(isDBUpdate) {
                    dbOp.updateStatus(sLastSelectedId, "DELIVERED", CommonConstants.DELIVERED, "GOLD");
                    dbOp.updateCompanyBillPhysicalLocation(sLastSelectedId, "GOLD", 
                                                    CommonConstants.DELIVERED);
                }
            } else if(cbOperationType.getValue().equals("REBILL")) {
                txtPhysicalLocation.setText(CommonConstants.REBILLED);
                dbOp.updateCompanyBillPhysicalLocation(sLastSelectedId, "GOLD", 
                                                CommonConstants.REBILLED);
            }
        } catch (Exception ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void dpMultiRebilledClosingDateOnChanged(ActionEvent event) {
        try {
            btRemoveFromTableClicked(event);
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btPrintRecieptClicked(ActionEvent event) {
        if(sLastSelectedId != null)
        {
            try {
                String sCompanyFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\Billclosing_RecievedAmt.jasper";                        
                
                Map<String, Object> parameters = new HashMap<>();
                String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
                parameters.put("name", 
                        txtCustomerName.getText() + " " 
                        + cbSpouseType.getValue() + "\n"
                        + txtSpouseName.getText() + " ");
                if(tgOff.isSelected()) {
                    parameters.put("enquiry_closing", 
                        "Closing Date:");
                    parameters.put("topay_paid", 
                        "Paid Amt:");
                } else {
                    parameters.put("enquiry_closing", 
                        "Enqiry Date:");
                    parameters.put("topay_paid", 
                        "To Pay:");
                }
                parameters.put("bill_number", 
                        sLastSelectedId);
                parameters.put("closing_date", 
                        sBillClosingDate);
                parameters.put("Capital_amount", 
                        Double.parseDouble(txtAmount.getText()));
                parameters.put("months", 
                        Float.parseFloat(txtTakenDaysOrMonths.getText()));
                parameters.put("Interest", 
                        Double.parseDouble(txtTakenAmount.getText()));
                parameters.put("adv_amt", 
                        Double.parseDouble(txtTotalAdvanceAmountPaid.getText()));
                parameters.put("ToBePaid", 
                        Double.parseDouble(txtGotAmount.getText()));                
                parameters.put("reduced_adv_amt", 
                        Double.parseDouble(txtToGetAmount.getText()));
                parameters.put("interested_amount", 
                        Double.parseDouble(txtAmount.getText()) 
                + Double.parseDouble(txtTakenAmount.getText()));
                
                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();                
                noticeUtil.mergeaAndGenerateNoticeIndividual("TOBEPAID", parameters, 
                        sCompanyFileName);
                //noticeUtil.mergeAndGenerateNoticeOperation(sCompanyFileName, sCustomerFileName, parameters);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } 
        } else 
        {
            
        }
    }
}
