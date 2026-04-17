/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillopening;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class ReBillRecievedAndBalanceAmtDialog implements Initializable {

    String sGrossWeight = null;
    String sPurity = null;
    String sCompanyRate = null;
    String sReductionRate = null;        
    double dNetWt = 0;
    private String sLastSelectedId;
    private String sOperation;
    private boolean forAdd = true;
    private String sMaterialType;    
    
    List<TextField> lstRupee = new ArrayList<>();
    List<TextField> lstNumberOfNotes = new ArrayList<>();
    List<TextField> lstTotalAmt = new ArrayList<>();
    
    List<AvailableBalanceBean> currencyList = new ArrayList<>();     
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txtCloseIntr;
    @FXML
    private TextField txtOpenNewAmt;
    @FXML
    private TextField txtOpenOldAmt;
    @FXML
    private TextField txtAmtGotInHand;
    @FXML
    private TextField txtToGive;

    private GoldBillOpeningController gParent;
    private SilverBillOpeningController sParent;
      
    private BillOpeningDBOperation dbOp;
    @FXML
    private Label lbGetOrGive;
    @FXML
    private TextField txtTotalAdvAmtPaid;
    @FXML
    private TextField txtOpenTakenIntr;
    @FXML
    private TextField txtTotalOtherCharges;
    @FXML
    private TextField txtDiscountAmount;
    @FXML
    private TextField txtBalanceToGive;
    @FXML
    private Label lbBalance;
    @FXML
    private Tab tabDenomination;
    @FXML
    private VBox vbRupee;
    @FXML
    private VBox vbNumberOfNotes;
    @FXML
    private VBox vbTotalAmt;
    @FXML
    private TextField txtToGetAmt;
    @FXML
    private TextField txtGotAmt;
    @FXML
    private Button btDoneDenom;
    @FXML
    private Tab tabNonDenomination;
    @FXML
    private TabPane tpScreen;
    @FXML
    private TextField txtCloseToGetAmount;
    @FXML
    private TextField txtTotalToGetAmount;
    @FXML
    private TextField txtRatePerGm;
    @FXML
    private TextField txtSuggestionAmt;
    @FXML
    private Label lbdenomToGetAmt;
    @FXML
    private Label lbdenomGotAmt;
    @FXML
    private Label lbdenomBalAmt;
    @FXML
    private TextField txtdENOMBalanceAmt;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        txtOpenOldAmt.setEditable(false);
        txtOpenOldAmt.setMouseTransparent(true);
        txtOpenOldAmt.setFocusTraversable(false);  

        txtCloseIntr.setEditable(false);
        txtCloseIntr.setMouseTransparent(true);
        txtCloseIntr.setFocusTraversable(false);  

        txtTotalAdvAmtPaid.setEditable(false);
        txtTotalAdvAmtPaid.setMouseTransparent(true);
        txtTotalAdvAmtPaid.setFocusTraversable(false);  

        txtOpenTakenIntr.setEditable(false);
        txtOpenTakenIntr.setMouseTransparent(true);
        txtOpenTakenIntr.setFocusTraversable(false);  
        
        txtTotalOtherCharges.setEditable(false);
        txtTotalOtherCharges.setMouseTransparent(true);
        txtTotalOtherCharges.setFocusTraversable(false);  

        txtDiscountAmount.setEditable(false);
        txtDiscountAmount.setMouseTransparent(true);
        txtDiscountAmount.setFocusTraversable(false);  
        
            try {
                dialogpanel.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
                                {
                                    Robot  eventRobot = new Robot();

                                    @Override
                                    public void handle( KeyEvent KV )
                                    {
                                        switch ( KV.getCode() )
                                        {
                                            case ENTER :
                                            {
                                                if ( (!(KV.getTarget() instanceof TextField)) || (!(KV.getTarget() instanceof Button)))
                                                {
                                                    eventRobot.keyPress( java.awt.event.KeyEvent.VK_TAB );
                                                    eventRobot.keyRelease( java.awt.event.KeyEvent.VK_TAB );
                                                    KV.consume();
                                                }
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
                                                if(gParent != null) {
                                                    gParent.dialog.close();
                                                } else {
                                                    sParent.dialog.close();
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

        try {
            dbOp = new BillOpeningDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }    

    public void setInitValues(boolean forNew, String sRebilledFromNumber) {
    
        String sTotalAdvAmt = null;
        String sCloseInterestAmt = null;
        String sOldAmt = null;
        String sNewAmt = null;
        String sTotalOtherCharges = null;
        String sDiscount = null;
        String sClosedGotAmt = null;
        this.forAdd = forNew;
        if(gParent != null) {
            try {
                HashMap<String, String> headerValues = this.gParent.dbOp.getRebilledFromVals("GOLD", sRebilledFromNumber);
                sTotalAdvAmt = headerValues.get("total_advance_amount_paid");
                sCloseInterestAmt = headerValues.get("close_taken_amount");
                sOldAmt = headerValues.get("amount");
                sNewAmt = this.gParent.txtAmount.getText();
                sTotalOtherCharges = headerValues.get("total_other_charges");
                sDiscount = headerValues.get("discount_amount");
                sClosedGotAmt = headerValues.get("got_amount");
                
                sGrossWeight = this.gParent.txtGrossWeight.getText();
                sPurity = this.gParent.txtPurity.getText();
                sReductionRate = this.gParent.dbOp.getReductionWt("GOLD");
                sCompanyRate = this.gParent.dbOp.getCompanyRate("GOLD");
                this.sLastSelectedId = this.gParent.sLastSelectedId;
                this.sOperation = CommonConstants.G_REBILL_CLOSE_OPERATION;
                this.dbOp = this.gParent.dbOp;
                
                double dGrossWt = Double.parseDouble(sGrossWeight);
                float fPurity = sPurity.isEmpty() ? 0f : Float.parseFloat(sPurity);
                if(fPurity < 88) {
                    dNetWt = (fPurity * dGrossWt)/100;
                } else {
                    double dReductionWt = Double.parseDouble(sReductionRate);
                    dNetWt = dGrossWt - (dGrossWt * dReductionWt);
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(ReBillRecievedAndBalanceAmtDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                HashMap<String, String> headerValues = this.sParent.dbOp.getRebilledFromVals("SILVER", sRebilledFromNumber);
                sTotalAdvAmt = headerValues.get("total_advance_amount_paid");
                sCloseInterestAmt = headerValues.get("close_taken_amount");
                sOldAmt = headerValues.get("amount");
                sNewAmt = this.sParent.txtAmount.getText();
                sTotalOtherCharges = headerValues.get("total_other_charges");
                sDiscount = headerValues.get("discount_amount");
                sClosedGotAmt = headerValues.get("got_amount");
                
                sGrossWeight = this.sParent.txtGrossWeight.getText();
                sPurity = this.sParent.txtPurity.getText();
                sReductionRate = this.sParent.dbOp.getReductionWt("SILVER");
                sCompanyRate = this.sParent.dbOp.getCompanyRate("SILVER");
                this.sLastSelectedId = this.sParent.sLastSelectedId;
                this.sOperation = CommonConstants.S_REBILL_CLOSE_OPERATION;
                this.dbOp = this.sParent.dbOp;
                
                double dGrossWt = Double.parseDouble(sGrossWeight);
                float fPurity = sPurity.isEmpty() ? 0f : Float.parseFloat(sPurity);
                if(fPurity > 80) {
                    dNetWt = (fPurity * dGrossWt)/100;
                } else {
                    double dReductionWt = Double.parseDouble(dbOp.getReductionWt("SILVER"));
                    dNetWt = dGrossWt - (dGrossWt * dReductionWt);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ReBillRecievedAndBalanceAmtDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        txtTotalAdvAmtPaid.setText(sTotalAdvAmt);
        txtCloseIntr.setText(sCloseInterestAmt);
        txtTotalOtherCharges.setText(sTotalOtherCharges);
        txtDiscountAmount.setText(sDiscount);
        txtOpenOldAmt.setText(sOldAmt);
        txtAmtGotInHand.setText("0");
        txtBalanceToGive.setText("0");
        txtCloseToGetAmount.setText(sClosedGotAmt);
        double dNewAmt = Double.valueOf(sNewAmt);
        txtOpenNewAmt.setText(sNewAmt);
        txtOpenNewAmt.setEditable(false);
        txtOpenNewAmt.setMouseTransparent(true);
        txtOpenNewAmt.setFocusTraversable(false);
        setAmountRelatedText(dNewAmt, gParent != null ? "GOLD" : "SILVER");
        tpScreen.getSelectionModel().select(tabNonDenomination);
        Platform.runLater(() -> {
            txtOpenNewAmt.requestFocus();
            txtOpenNewAmt.positionCaret(txtOpenNewAmt.getText().length());
        });
    }
    
    public void setInitValues(List<AvailableBalanceBean> existingCurrencyList) {
        
        if(forAdd) {
            
            if(existingCurrencyList == null) {
                currencyList.add(new AvailableBalanceBean(2000, 0, 0));
                currencyList.add(new AvailableBalanceBean(500, 0, 0));
                currencyList.add(new AvailableBalanceBean(200, 0, 0));
                currencyList.add(new AvailableBalanceBean(100, 0, 0));
                currencyList.add(new AvailableBalanceBean(50, 0, 0));
                currencyList.add(new AvailableBalanceBean(20, 0, 0));
                currencyList.add(new AvailableBalanceBean(10, 0, 0));
                currencyList.add(new AvailableBalanceBean(5, 0, 0));
                currencyList.add(new AvailableBalanceBean(2, 0, 0));
                currencyList.add(new AvailableBalanceBean(1, 0, 0));
            } else {
                currencyList = existingCurrencyList;
            }
            btDoneDenom.setDisable(false);
        } else {
            if(existingCurrencyList != null) {
                currencyList = existingCurrencyList;
                tpScreen.getSelectionModel().select(tabDenomination);
            } else {
                /*txtTotalAvailableBalance.setText(parent.txtAvailableBalance.getText());
                txtDeficitAmt.setText(parent.txtDeficit.getText());
                txtCashAvailableAmt.setText(parent.txtAvailableBalance.getText());
                txtCashDeficitAmt.setText(parent.txtDeficit.getText());                
                tabPaneScreen.getSelectionModel().select(tabNonDenomination);*/
            }
            btDoneDenom.setDisable(true);
        }
        
        for(AvailableBalanceBean bean : currencyList) {
            
            final TextField txtRupee = new TextField();
            txtRupee.setEditable(false);
            txtRupee.setMouseTransparent(true);
            txtRupee.setFocusTraversable(false);                
            final TextField txtTotalAmt = new TextField();
            txtTotalAmt.setEditable(false);
            txtTotalAmt.setMouseTransparent(true);
            txtTotalAmt.setFocusTraversable(false);                
            final TextField txtNumberOfNotes = new TextField();
            if(forAdd) {
                txtNumberOfNotes.setEditable(true);
                txtNumberOfNotes.setMouseTransparent(false);
                txtNumberOfNotes.setFocusTraversable(true);                            
            } else {
                txtNumberOfNotes.setEditable(false);
                txtNumberOfNotes.setMouseTransparent(true);
                txtNumberOfNotes.setFocusTraversable(false);                            
            }
            
            txtRupee.setText(Double.toString(bean.getDRupee()));
            txtNumberOfNotes.setText(Integer.toString(bean.getDNumberOfNotes()));
            txtTotalAmt.setText(Double.toString(bean.getDTotalAmount()));
            

            txtNumberOfNotes.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    int index = lstNumberOfNotes.indexOf(txtNumberOfNotes);
                    if (!newValue.matches("\\d*")) {                    
                        txtNumberOfNotes.setText(oldValue);   
                        int iAmount = Integer.parseInt(oldValue);    

                        setValuesToFileds(index, iAmount);
                    }  else {
                        if(!"".equals(newValue)) 
                        {
                            int iAmount = Integer.parseInt(newValue);
                            setValuesToFileds(index, iAmount);
                        } else {
                            lstTotalAmt.get(index).setText("");
                        }
                    }
                }
            });
            lstRupee.add(txtRupee);
            lstNumberOfNotes.add(txtNumberOfNotes);
            lstTotalAmt.add(txtTotalAmt);
            
            vbRupee.getChildren().add(txtRupee);
            vbNumberOfNotes.getChildren().add(txtNumberOfNotes);
            vbTotalAmt.getChildren().add(txtTotalAmt);     
            
            if(existingCurrencyList != null) {
                int index = lstNumberOfNotes.indexOf(txtNumberOfNotes);
                setValuesToFileds(index, bean.getDNumberOfNotes());
            }
        }
        
        txtToGetAmt.setText(txtToGive.getText());        
    }
    
    public void setValuesToFileds(int index, int numberOfNotes) {
        TextField fldRupee = lstRupee.get(index);
        TextField fldTotalAmt = lstTotalAmt.get(index);
        double dTotalAmt = numberOfNotes * Double.parseDouble(fldRupee.getText());
        fldTotalAmt.setText(Double.toString(dTotalAmt));    
        
        double totAmt = 0;
        for(TextField txtTotAmt : lstTotalAmt) {            
            totAmt = totAmt + Double.parseDouble(txtTotAmt.getText());            
        }
        
        double deficit = 0;
        
        if(lbGetOrGive.getText().contains("Give")) {
            deficit = Double.parseDouble(txtToGive.getText()) - totAmt;    
        } else if(lbGetOrGive.getText().contains("Get")) {
            deficit = totAmt - Double.parseDouble(txtToGive.getText());    
        }         

        if(deficit <= 0) {
            lbdenomBalAmt.setText("Balance To Get:");
            txtdENOMBalanceAmt.setText(Double.toString(deficit).replace("-", ""));            
            lbBalance.setText("Balance To Get:");
            txtBalanceToGive.setText(Double.toString(deficit).replace("-", ""));
        } else {
            lbdenomBalAmt.setText("Balance To Give:");
            txtdENOMBalanceAmt.setText(Double.toString(deficit));        
            lbBalance.setText("Balance To Give:");
            txtBalanceToGive.setText(Double.toString(deficit));
        }                
        
        txtGotAmt.setText(Double.toString(totAmt));
        txtAmtGotInHand.setText(Double.toString(totAmt));
    }
    
    @FXML
    private void onDoneClicked(ActionEvent event) {
    }
    
    @FXML
    private void allowNumberOnlyOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }                
    }

    @FXML
    private void allowDotWithNumberOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }        
    }
    
    public void setParent(GoldBillOpeningController gParent)
    {
        this.gParent = gParent;
        this.sMaterialType = "GOLD";
    }

    public void setParent(SilverBillOpeningController sParent)
    {
        this.sParent = sParent;
        this.sMaterialType = "SILVER";
    }

    @FXML
    private void txtGotAmountOnPress(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                String sAmount = txtAmtGotInHand.getText().trim();
                if (!sAmount.isEmpty()) {
                    try {
                        double dAmount = Double.parseDouble(sAmount);
                        if (dAmount > 0) {
                            setGotAmountRelatedText(dAmount);
                        } else {
                            txtBalanceToGive.setText("");
                        }
                    } catch (NumberFormatException ex) {
                        txtBalanceToGive.setText("");
                    }
                } else {
                    txtBalanceToGive.setText("");
                }
            });
        }
    }

    @FXML
    private void txtGotAmountOnType(KeyEvent e) {
        if (!("0123456789.".contains(e.getCharacter()))) {
            e.consume();
            return;
        }
        Platform.runLater(() -> {
            String sAmount = txtAmtGotInHand.getText().trim();
            if (!sAmount.isEmpty()) {
                try {
                    double dAmount = Double.parseDouble(sAmount);
                    if (dAmount > 0) {
                        setGotAmountRelatedText(dAmount);
                    }
                } catch (NumberFormatException ex) { /* ignore partial input */ }
            }
        });
    }

    @FXML
    private void txtAmountOnType(KeyEvent e) {
        if (!("0123456789.".contains(e.getCharacter()))) {
            e.consume();
            return;
        }
        Platform.runLater(() -> {
            String sAmount = txtOpenNewAmt.getText().trim();
            if (!sAmount.isEmpty()) {
                try {
                    double dAmount = Double.parseDouble(sAmount);
                    if (dAmount > 0) {
                        setAmountRelatedText(dAmount, gParent != null ? "GOLD" : "SILVER");
                    }
                } catch (NumberFormatException ex) { /* ignore partial input */ }
            }
        });
    }

    @FXML
    private void txtAmountOnPress(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                String sAmount = txtOpenNewAmt.getText().trim();
                if (!sAmount.isEmpty()) {
                    try {
                        double dAmount = Double.parseDouble(sAmount);
                        if (dAmount > 0) {
                            setAmountRelatedText(dAmount, gParent != null ? "GOLD" : "SILVER");
                        } else {
                            txtBalanceToGive.setText("");
                        }
                    } catch (NumberFormatException ex) {
                        txtBalanceToGive.setText("");
                    }
                } else {
                    txtBalanceToGive.setText("");
                }
            });
        }
    }

    public void setAmountRelatedText(double dAmount, String sMaterialType) {
    
        try {
            String sInterest = dbOp.getInterest(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(new Date()), 
                    dAmount, gParent != null ? "GOLD" : "SILVER").trim();
            String sDocumentCharge = dbOp.getDocumentCharge(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(new Date()), 
                    dAmount, gParent != null ? "GOLD" : "SILVER").trim();
            String sFormula = dbOp.getFormula(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(new Date()), 
                    dAmount, gParent != null ? "GOLD" : "SILVER");
            
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
            txtOpenTakenIntr.setText(sTakenAmount);
            double dToGive = (dAmount - Double.parseDouble(sTakenAmount)) 
                    - (Double.parseDouble(txtOpenOldAmt.getText()) 
                            + Double.parseDouble(txtCloseIntr.getText())
                            + Double.parseDouble(txtTotalOtherCharges.getText())
                            - Double.parseDouble(txtDiscountAmount.getText())
                            - Double.parseDouble(txtTotalAdvAmtPaid.getText()));
            if(dToGive >= 0 ) {
                lbGetOrGive.setText("Actual Amt To Give:");
                txtToGive.setStyle(Util.getTextFieldStyle("#000000", "#FF5555").toString());
                lbdenomToGetAmt.setText("To Give Amount:");
                lbdenomGotAmt.setText("Given Amount:");
                lbdenomBalAmt.setText("Balance To Give:");
            } else {
                lbGetOrGive.setText("Actual Amt To Get:");
                txtToGive.setStyle(Util.getTextFieldStyle("#FFFFFF", "#009800").toString());
                lbdenomToGetAmt.setText("To Get Amount:");
                lbdenomGotAmt.setText("Received Amount:");
                lbdenomBalAmt.setText("Balance To Get:");
            }
            String sToGive = Double.toString(dToGive);
            sToGive = sToGive.replace("-", "");            
            txtToGive.setText(sToGive);
            txtToGetAmt.setText(sToGive);
            
            double dGrossWt = Double.parseDouble(sGrossWeight);
            double dRatePerGm = dAmount / dGrossWt;
            txtRatePerGm.setText(String.valueOf(Math.round(dRatePerGm)));
            
            double dSuggestionAmt = dNetWt * Double.parseDouble(sCompanyRate);
            txtSuggestionAmt.setText(String.valueOf(Math.round(dSuggestionAmt)));
            if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt >= dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #55FF30");
            } else if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt < dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #FF8888");
            } else {
                txtSuggestionAmt.setStyle("-fx-background-color: #FFFFFF");
            }
            
            double totToGet = Double.parseDouble(txtCloseToGetAmount.getText()) + Double.parseDouble(sTakenAmount);
            txtTotalToGetAmount.setText(Double.toString(totToGet));
            
            setGotAmountRelatedText(Double.parseDouble(txtAmtGotInHand.getText().isEmpty() 
                    ? "0" : txtAmtGotInHand.getText()));
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setGotAmountRelatedText(double dRcdAmt) {
    
        double deficit = 0;
        
        if(lbGetOrGive.getText().contains("Give")) {
            deficit = Double.parseDouble(txtToGive.getText()) - dRcdAmt;    
        } else if(lbGetOrGive.getText().contains("Get")) {
            deficit = dRcdAmt - Double.parseDouble(txtToGive.getText());    
        }         

        if(deficit <= 0) {
            lbBalance.setText("Balance To Get:");
            txtBalanceToGive.setText(Double.toString(deficit).replace("-", ""));            
            //lbBalance.setText("Balance To Get:");
            //txtCashBalanceAmt.setText(Double.toString(deficit).replace("-", ""));
        } else {
            lbBalance.setText("Balance To Give:");
            txtBalanceToGive.setText(Double.toString(deficit));        
            //lbCashBalance.setText("Balance To Give:");
            //txtCashBalanceAmt.setText(Double.toString(deficit));
        }                
        
/*        double dActualAmt = Double.parseDouble(txtToGive.getText());
        double dBalance = 1;       

        if(lbGetOrGive.getText().contains("Get")) {
            dBalance *= -1;
        } else {
            dRcdAmt *= -1;
        }
        dBalance = dBalance * (dRcdAmt - dActualAmt);
        if(dBalance <= 0) {
            lbBalance.setText("Balance To Give:");
        } else {
            lbBalance.setText("Balance To Get:");
        }

        String sBalToGive = Double.toString(dBalance);
        sBalToGive = sBalToGive.replace("-", "");
        txtBalanceToGive.setText(sBalToGive);*/
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void txtOpenNewAmtOnAction(ActionEvent event) {
        Platform.runLater(() -> {
            txtAmtGotInHand.requestFocus();
            txtAmtGotInHand.positionCaret(txtAmtGotInHand.getText().length());
        });                        
    }

    @FXML
    private void txtAmtGotInHandOnAction(ActionEvent event) {
        Platform.runLater(() -> {
            txtOpenNewAmt.requestFocus();
            txtOpenNewAmt.positionCaret(txtOpenNewAmt.getText().length());
        });                        
    }

    @FXML
    private void btSaveOnAction(ActionEvent event) {
        
        if(forAdd) {
            List<AvailableBalanceBean> currencyListToSave = new ArrayList<>(); 
            for(int index=1; index<vbRupee.getChildren().size(); index++) {
                double dRupee = Double.parseDouble(((TextField)vbRupee.getChildren().get(index)).getText());
                int iNoOfNotes = Integer.parseInt(((TextField)vbNumberOfNotes.getChildren().get(index)).getText());
                double dTotAmt = Double.parseDouble(((TextField)vbTotalAmt.getChildren().get(index)).getText());
                currencyListToSave.add(new AvailableBalanceBean(dRupee, iNoOfNotes, dTotAmt));
            }

            try {                
                String sBillNumber = this.sLastSelectedId;
                if(this.dbOp.deleteDenomination(sOperation, sBillNumber, sMaterialType)) {
                    if(this.dbOp.saveDenominationValues(sOperation, sBillNumber, sMaterialType, currencyListToSave)) {
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(AvailableBalanceDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            }     
            
        }
        if(gParent != null) {
            gParent.dialog.close();
        } else {
            sParent.dialog.close();
        }        
    }

    @FXML
    private void tabDenominationSelectionChanged(Event event) {
        if(tpScreen.getSelectionModel().isSelected(0) && vbNumberOfNotes.getChildren().size() > 1) {
            Platform.runLater(() -> {
                TextField TF = (TextField)vbNumberOfNotes.getChildren().get(1);
                TF.requestFocus();
                //TF.positionCaret(TF.getText().length());
            });         
        }        
    }

    @FXML
    private void tabNonDenominationSelectionChanged(Event event) {
        if(tpScreen.getSelectionModel().isSelected(1)) {
            Platform.runLater(() -> {
                txtOpenNewAmt.requestFocus();
                txtOpenNewAmt.positionCaret(txtOpenNewAmt.getText().length());
            });                
        }        
    }
    
}
