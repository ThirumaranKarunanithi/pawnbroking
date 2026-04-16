/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class QueAlertUIController implements Initializable {

    public String sMessage;
    public Object screenObj;
    public Method yesMethod;
    public Method noMethod;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label message;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        message.setText(sMessage);
    }   

    @FXML
    private void byesclicked(ActionEvent event) {
        
        Object[] parameters = new Object[0];			
        try {
                this.yesMethod.invoke(this.screenObj, parameters);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
        }
        PopupUtil.dialog.close();
    }

    @FXML
    private void bnoclicked(ActionEvent event) {

        Object[] parameters = new Object[0];			
        try {
                this.noMethod.invoke(this.screenObj, parameters);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
        }        
        PopupUtil.dialog.close();
    }
    
    public void setParent(String sMessage, Object screenObj, Method yesMethod, Method noMethod) {
        
        this.sMessage = sMessage;
        this.screenObj = screenObj;
        this.yesMethod = yesMethod;
        this.noMethod = noMethod;
        message.setText(this.sMessage);
    }
}
