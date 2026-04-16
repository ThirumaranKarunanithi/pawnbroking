/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class PasswordFieldUIController implements Initializable {

    private String checkingPassWord = null;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private PasswordField txtPwd;
    @FXML
    private Label lbMsg;

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
                                                PopupUtil.dialog.close();
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

    @FXML
    private void bokclicked(ActionEvent event) {
        isRightPassword(txtPwd.getText().trim());
    }

    @FXML
    private void txtPwdOnAction(ActionEvent event) {
        isRightPassword(txtPwd.getText().trim());
    }
    
    private void isRightPassword(String pass) {
        if(pass.equals(checkingPassWord)) {
            PopupUtil.isRightPassord = true;
            PopupUtil.dialog.close();
        } else {
            PopupUtil.isRightPassord = false;
            lbMsg.setText("Invalid password. Your action will be recorded.");
        }
    }
    
    public void setCheckPassword(String sCheckPass) 
    {
        this.checkingPassWord = sCheckPass;
    }
    
}
