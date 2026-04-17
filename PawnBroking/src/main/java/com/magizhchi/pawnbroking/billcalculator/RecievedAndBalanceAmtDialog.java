/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billcalculator;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RecievedAndBalanceAmtDialog implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txtActualAmt;
    @FXML
    private TextField txtRecievedAmt;
    @FXML
    private TextField txtBalanceAmt;
    @FXML
    private Label lbBalance;

    private BillCalculatorController parent;
    @FXML
    private Label lbAmtRecieved;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        txtActualAmt.setEditable(false);
        txtActualAmt.setMouseTransparent(true);
        txtActualAmt.setFocusTraversable(false);  
        
        try {
                dialogpanel.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
                                {
                                    Robot  eventRobot = new Robot();

                                    @Override
                                    public void handle( KeyEvent KV )
                                    {
                                        switch ( KV.getCode() )
                                        {
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
        
        txtRecievedAmt.requestFocus();                   
    }    

    public void setInitValues(String str) {
        
        if("COMPANY".equals(str)) {
            String sActualAmt = this.parent.txtAmtToGet.getText();
            if(this.parent.lbAmtToGet.getText().contains("Give")) {
                sActualAmt = "-" + sActualAmt;
                lbAmtRecieved.setText("Amount Given:");
                lbBalance.setText("Balance to Get:");
            } else {
                lbAmtRecieved.setText("Amount Recieved:");
                lbBalance.setText("Balance To Give:");
            }                
            txtActualAmt.setText(sActualAmt);
        } else {
            String sActualAmt = this.parent.txtRepAmtToGet.getText();
            if(this.parent.lbAmtToGet1.getText().contains("Give")) {
                sActualAmt = "-" + sActualAmt;
                lbAmtRecieved.setText("Amount Given:");
                lbBalance.setText("Balance to Get:");
            } else {
                lbAmtRecieved.setText("Amount Recieved:");
                lbBalance.setText("Balance To Give:");
            }                
            txtActualAmt.setText(sActualAmt);        
        }
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
    
    public void setParent(BillCalculatorController parent)
    {
        this.parent = parent;
    }

    private void txtRecievedAmtOnAction(double dRecievedAmount) {
        double dActualAmt = Double.parseDouble(txtActualAmt.getText());
        double dBalance = 0;
        if(dActualAmt < 0) {
            dBalance = dRecievedAmount + dActualAmt;
        } else {
            dBalance = dRecievedAmount - dActualAmt;
        }
        
        txtBalanceAmt.setText(Double.toString(dBalance));  
    }
    
    @FXML
    private void txtAmountOnType(KeyEvent e) {
        if (!("0123456789.".contains(e.getCharacter()))) {
            e.consume();
            return;
        }
        Platform.runLater(() -> {
            String sAmount = txtRecievedAmt.getText().trim();
            if (!sAmount.isEmpty()) {
                try {
                    double dAmount = Double.parseDouble(sAmount);
                    if (dAmount > 0) {
                        txtRecievedAmtOnAction(dAmount);
                    }
                } catch (NumberFormatException ex) { /* ignore partial input */ }
            }
        });
    }

    @FXML
    private void txtAmountOnPress(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                String sAmount = txtRecievedAmt.getText().trim();
                if (!sAmount.isEmpty()) {
                    try {
                        double dAmount = Double.parseDouble(sAmount);
                        if (dAmount > 0) {
                            txtRecievedAmtOnAction(dAmount);
                        } else {
                            txtBalanceAmt.setText("");
                        }
                    } catch (NumberFormatException ex) {
                        txtBalanceAmt.setText("");
                    }
                } else {
                    txtBalanceAmt.setText("");
                }
            });
        }
    }

    
}
