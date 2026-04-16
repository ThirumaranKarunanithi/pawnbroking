/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.operationlist;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class ItemMasterDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txtItem;
    @FXML
    private ComboBox<String> cbStatus;

    private OperationListController parent;
    private boolean isDialogForAdd;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        txtItem.setEditable(true);
        txtItem.setMouseTransparent(false);
        txtItem.setFocusTraversable(true);     

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
    }    

    @FXML
    private void onSaveClicked(ActionEvent event) {
        
        String sItem = txtItem.getText().trim();
        String sStatus = cbStatus.getValue();
        
        if(isDialogForAdd) {
            try {
                if(this.parent.dbOp.isvalidItemToSave(sItem, CommonConstants.CURRENT_OPERATION)) {
                    if(this.parent.dbOp.saveItem(sItem, sStatus, CommonConstants.CURRENT_OPERATION)) {
                        this.parent.setJewelItemValues(CommonConstants.CURRENT_OPERATION);
                        this.parent.dialog.close();
                    }
                } else {
                    PopupUtil.showErrorAlert("Sorry same item already exists.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(ItemMasterDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ItemMasterDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                if(this.parent.dbOp.updateItem(sItem, sStatus, CommonConstants.CURRENT_OPERATION)) {
                    this.parent.setJewelItemValues(CommonConstants.CURRENT_OPERATION);
                    this.parent.dialog.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(ItemMasterDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    private void capitalizeCharOnPressed(KeyEvent e) {
        
        if(e.getCode() == KeyCode.BACK_SPACE){ 
            
            TextField txt_TextField = (TextField) e.getSource(); 
            
            String sText;
            int caretPos = txt_TextField.getCaretPosition();
            
            if(txt_TextField.getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txt_TextField.getText());
                sText = sb.toString();
                txt_TextField.setText(sText);
                txt_TextField.positionCaret(0);
            } else {
                StringBuilder sb = new StringBuilder(txt_TextField.getText());
                sb.deleteCharAt(txt_TextField.getCaretPosition() - 1);
                sText = sb.toString();
                txt_TextField.setText(sText);
                txt_TextField.positionCaret(caretPos-2);
            }            
            e.consume();
        }
    }
    
    public void setParent(OperationListController parent, boolean isDialogForAdd)
    {
        this.parent = parent;
        this.isDialogForAdd = isDialogForAdd;
    }
    
    public void setInitValues() {
    
        if(!isDialogForAdd)
        {
            txtItem.setEditable(false);
            txtItem.setMouseTransparent(true);
            txtItem.setFocusTraversable(false);     
            int index = this.parent.tbList.getSelectionModel().getSelectedIndex();
            ItemsBean bean = this.parent.tbList.getItems().get(index);
            txtItem.setText(bean.getSItem());
            cbStatus.setValue(bean.getSStatus());
        }
    }
    
}
