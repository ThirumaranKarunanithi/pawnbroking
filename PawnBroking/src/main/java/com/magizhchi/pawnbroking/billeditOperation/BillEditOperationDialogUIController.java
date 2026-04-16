/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billeditOperation;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companyadvanceamount.AdvanceAmountBean;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class BillEditOperationDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;

    public BillEditOperationController parent;
    private boolean isDialogForAdd;
    @FXML
    private DatePicker dpAADate;
    private TextField txtAAAmount;
    @FXML
    private TextField txtAABillNumber;
    @FXML
    private TextField txtAATotalAmount;
    
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
    }    

    @FXML
    private void onSaveClicked(ActionEvent event) {
        
        
        String sAADate = CommonConstants.DATETIMEFORMATTER.format(dpAADate.getValue());

        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sAADate))
        {
        
            String sAABillNumber = txtAABillNumber.getText().trim();
            String sAATotalAmount = txtAATotalAmount.getText().trim();
            double dAATotalAmount = Double.parseDouble(sAATotalAmount);

            try {
                if(this.parent.dbOp.updateAADate(sAABillNumber, this.parent.cbBillMaterialType.getValue(), sAADate, dAATotalAmount)) {
                    DataTable advanceAmountDetailValues = this.parent.dbOp.getAdvanceAmountTableValues(sAABillNumber, this.parent.cbBillMaterialType.getValue());
                    this.parent.setAllDetailValuesToFieldInAdvanceReceiptTable(advanceAmountDetailValues);                
                    this.parent.dialog.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        } else {
            PopupUtil.showErrorAlert("Sorry this bill due date account was closed.");
        }        
    }


    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }
 
    public void setParent(BillEditOperationController parent, boolean isDialogForAdd)
    {
        this.parent = parent;
        this.isDialogForAdd = isDialogForAdd;
    }
    
    public void setInitValues() {
    
        int index = this.parent.tbAdvanceReceiptDetails.getSelectionModel().getSelectedIndex();
        AdvanceAmountBean bean = this.parent.tbAdvanceReceiptDetails.getItems().get(index);
        txtAABillNumber.setText(bean.getSBillNumber());
        dpAADate.setValue(LocalDate.parse(bean.getSDate(), CommonConstants.DATETIMEFORMATTER));
        txtAATotalAmount.setText(Double.toString(bean.getDTotalAmount()));
    }
}
