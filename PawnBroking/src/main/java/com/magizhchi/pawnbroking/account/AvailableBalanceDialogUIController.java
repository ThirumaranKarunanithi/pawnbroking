/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import com.magizhchi.pawnbroking.common.CommonConstants;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class AvailableBalanceDialogUIController implements Initializable {

    private TodaysAccountController parent;
    private boolean forAdd = true;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TextField txtTotalAvailableBalance;
    @FXML
    private VBox vbRupee;
    @FXML
    private VBox vbNumberOfNotes;
    @FXML
    private VBox vbTotalAmt;
    
    List<TextField> lstRupee = new ArrayList<>();
    List<TextField> lstNumberOfNotes = new ArrayList<>();
    List<TextField> lstTotalAmt = new ArrayList<>();
    
    List<AvailableBalanceBean> currencyList = new ArrayList<>(); 
    @FXML
    private TextField txtDeficitAmt;
    @FXML
    private AnchorPane dialogpanel1;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txtActualAmt;
    @FXML
    private TextField txtCashActualAmt;
    @FXML
    private TextField txtCashAvailableAmt;
    @FXML
    private TextField txtCashDeficitAmt;
    @FXML
    private Tab tabDenomination;
    @FXML
    private Tab tabNonDenomination;
    @FXML
    private TabPane tabPaneScreen;
    @FXML
    private Button btDoneDenom;
    @FXML
    private Button btDoneNonDenom;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
                                                parent.dialog.close();
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
        
        txtCashActualAmt.setEditable(false);
        txtCashActualAmt.setMouseTransparent(true);
        txtCashActualAmt.setFocusTraversable(false);                

        txtCashDeficitAmt.setEditable(false);
        txtCashDeficitAmt.setMouseTransparent(true);
        txtCashDeficitAmt.setFocusTraversable(false);            
        
    }    

    public void setParent(TodaysAccountController parent, boolean forNew)
    {
        this.parent = parent;
        this.forAdd = forNew;
        
    }
    
    public void setInitValues(List<AvailableBalanceBean> existingCurrencyList) {
        
        if(forAdd) {
            txtCashAvailableAmt.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                   if (!newValue.matches("\\d{0,9}([\\.]\\d{0,2})?")) {
                        txtCashAvailableAmt.setText(oldValue);
                        double dDeficitAmt =  Double.parseDouble(oldValue) - Double.parseDouble(txtCashActualAmt.getText());
                        txtCashDeficitAmt.setText(Double.toString(dDeficitAmt));
                    }  else {
                        if(!"".equals(newValue)) {
                            double dDeficitAmt =  Double.parseDouble(newValue) - Double.parseDouble(txtCashActualAmt.getText());
                            txtCashDeficitAmt.setText(Double.toString(dDeficitAmt));
                        }
                    }
                }
            });        
            
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
                tabPaneScreen.getSelectionModel().select(tabDenomination);
            } else {
                txtTotalAvailableBalance.setText(parent.txtAvailableBalance.getText());
                txtDeficitAmt.setText(parent.txtDeficit.getText());
                txtCashAvailableAmt.setText(parent.txtAvailableBalance.getText());
                txtCashDeficitAmt.setText(parent.txtDeficit.getText());                
                tabPaneScreen.getSelectionModel().select(tabNonDenomination);
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
        
        txtCashActualAmt.setText(parent.txtActualBalance.getText());
        txtActualAmt.setText(parent.txtActualBalance.getText());        
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
        double deficit = totAmt - Double.parseDouble(parent.txtActualBalance.getText());        
        txtDeficitAmt.setText(Double.toString(deficit));        
        txtTotalAvailableBalance.setText(Double.toString(totAmt));
    }
    
    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void btSaveOnAction(ActionEvent event) {
        if(forAdd) {
            parent.txtAvailableBalance.setText(txtTotalAvailableBalance.getText());
            parent.txtDeficit.setText(txtDeficitAmt.getText());       
            if(Double.parseDouble(parent.txtDeficit.getText()) == 0) {
                parent.txtDeficit.setStyle("-fx-background-color: #55FF30");
            } else {
                parent.txtDeficit.setStyle("-fx-background-color: #FF5555");
            }                    
            
            List<AvailableBalanceBean> currencyListToSave = new ArrayList<>(); 
            for(int index=1; index<vbRupee.getChildren().size(); index++) {
                double dRupee = Double.parseDouble(((TextField)vbRupee.getChildren().get(index)).getText());
                int iNoOfNotes = Integer.parseInt(((TextField)vbNumberOfNotes.getChildren().get(index)).getText());
                double dTotAmt = Double.parseDouble(((TextField)vbTotalAmt.getChildren().get(index)).getText());
                currencyListToSave.add(new AvailableBalanceBean(dRupee, iNoOfNotes, dTotAmt));
            }

            try {
                String sTodaysDate = CommonConstants.DATETIMEFORMATTER.format(parent.dpTodaysDate.getValue());
                if(parent.dbOp.deleteDenomination(sTodaysDate)) {
                    if(parent.dbOp.saveDenominationValues(sTodaysDate, currencyListToSave)) {
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(AvailableBalanceDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            }      
            
        }
        parent.dialog.close();
    }

    @FXML
    private void onDoneClicked(ActionEvent event) {
        parent.txtAvailableBalance.setText(txtCashAvailableAmt.getText());
        parent.txtDeficit.setText(txtCashDeficitAmt.getText());
        parent.dialog.close();
    }


}
