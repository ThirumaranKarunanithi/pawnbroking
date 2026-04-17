/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillclosing;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class CustomerCopyUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private ComboBox<String> cbIdType;
    @FXML
    private TextField txtIdNumber;

    String materialType;
    GoldBillClosingController gParent = null;
    SilverBillClosingController sParent = null;
    
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
                                                if(materialType.equals("GOLD")) {
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
        Platform.runLater(() -> {
            txtIdNumber.requestFocus();
            txtIdNumber.positionCaret(txtIdNumber.getText().length());
        });        
    }    

    public void setParent(GoldBillClosingController gParent, String materialType) {
        this.gParent = gParent;
        this.materialType = materialType;
    }

    public void setParent(SilverBillClosingController sParent, String materialType) {
        this.sParent = sParent;
        this.materialType = materialType;
    }

    public void setInitValues(String idType, String idNumber) {
        cbIdType.setValue(idType);
        txtIdNumber.setText(idNumber);
    }
    
    @FXML
    private void onSaveClicked(ActionEvent event) {
        if(!txtIdNumber.getText().isEmpty()) { 
            if(materialType.equals("GOLD")) {
                gParent.idProofType = cbIdType.getValue();
                gParent.idProofNumber = txtIdNumber.getText();
                gParent.dialog.close();
            } else {
                sParent.idProofType = cbIdType.getValue();
                sParent.idProofNumber = txtIdNumber.getText();
                sParent.dialog.close();            
            }
        } else {
            lbMsg.setText("Id cannot be empty.");
        }
    }

    @FXML
    private void onCancelClicked(ActionEvent event) {
        if(materialType.equals("GOLD")) {
            gParent.dialog.close();
        } else {
            sParent.dialog.close();
        }        
    }

    @FXML
    private void capitalizeCharOnPressed(KeyEvent event) {
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }
    
}
