/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companyadvanceamount;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
public class GoldAdvanceAmountEditDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;

    int index = -1;
    private GoldAdvanceAmountController parent;
    private boolean isDialogForAdd;
    private String sTotalAmtPaidAlready = "0";
    private double dPreviousPaidAmt = 0;
    AdvanceAmountBean storedBean;
    
    @FXML
    private TextField txtBillAmt;
    @FXML
    private TextField txtPaidAmt;
    @FXML
    private DatePicker dpPaidDate;
    @FXML
    private TextField txtTotalAmtPaidYet;

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
        
        String sPaidDate = CommonConstants.DATETIMEFORMATTER.format(dpPaidDate.getValue());

        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sPaidDate))
        {

            double dBillAmt = Double.parseDouble(txtBillAmt.getText());
            double dPaidAmt = Double.parseDouble(txtPaidAmt.getText());
            double dTotAmtPaid = Double.parseDouble(txtTotalAmtPaidYet.getText());
            storedBean.setSDate(sPaidDate);
            storedBean.setDPaidAmount(dPaidAmt);
            storedBean.setDTotalAmount(dTotAmtPaid);
            this.parent.tbAdvanceReceiptDetails.getItems().set(this.index, storedBean);  
            
            double dTotalAmt = 0;
            int index = 0;
            ObservableList<AdvanceAmountBean> otherTableValues = this.parent.tbAdvanceReceiptDetails.getItems();
            for(AdvanceAmountBean bean : otherTableValues) {
                dTotalAmt = dTotalAmt + bean.getDPaidAmount();
                this.parent.tbAdvanceReceiptDetails.getItems().get(index).setDTotalAmount(dTotalAmt);
                index++;
            }
            this.parent.txtAdvanceReceiptDetailTotalAmount.setText(Double.toString(dTotalAmt));
            this.parent.dialog.close();   
        } else {
            lbMsg.setText("Sorry this date account was closed.");
            lbMsg.setDisable(false);
        }                       
    }

    @FXML
    private void allowDotWithNumberOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!e.getCharacter().matches("[0-9]")){ 
            e.consume();
        }
    }

    public void setParent(GoldAdvanceAmountController parent, boolean isDialogForAdd)
    {
        this.parent = parent;
        this.isDialogForAdd = isDialogForAdd;
    }
    
    public void setInitValues() {
    
        if(!isDialogForAdd)
        {
            int index = this.parent.tbAdvanceReceiptDetails.getSelectionModel().getSelectedIndex();
            AdvanceAmountBean bean = this.parent.tbAdvanceReceiptDetails.getItems().get(index);
            dpPaidDate.setValue(LocalDate.parse(bean.getSDate(), CommonConstants.DATETIMEFORMATTER));
            txtBillAmt.setText(Double.toString(bean.getDBillAmount()));
            txtPaidAmt.setText(Double.toString(bean.getDPaidAmount()));
            txtTotalAmtPaidYet.setText(Double.toString(bean.getDTotalAmount()));
            sTotalAmtPaidAlready = Double.toString(bean.getDTotalAmount());
            dPreviousPaidAmt = bean.getDPaidAmount();
            storedBean = bean;
            this.index = index;
        }
    }

    @FXML
    private void txtGotAdvanceAmountOnPress(KeyEvent e) {
        if(e.getCode() == KeyCode.BACK_SPACE){ 
            
            int val = txtPaidAmt.getText().length() < 0 ? txtPaidAmt.getText().length()-1 : 0;
            String sGotAmount = txtPaidAmt.getText().substring(0, val);

            if(!"".equals(sGotAmount)) 
            {
                double dPaidAlreadyAmount = Double.parseDouble(sTotalAmtPaidAlready);
                double dGotAmount = Double.parseDouble(sGotAmount);
                double dTotalAmount = dPaidAlreadyAmount - dPreviousPaidAmt + dGotAmount;
                txtTotalAmtPaidYet.setText(Double.toString(dTotalAmount));
            } else {
                txtTotalAmtPaidYet.setText(sTotalAmtPaidAlready);
            }
        }        
    }

    @FXML
    private void txtGotAdvanceAmountOnTyped(KeyEvent e) {
        
        if(!("0123456789.".contains(e.getCharacter()))){             
            e.consume();
        }         
        
        if("0123456789.".contains(e.getCharacter())){ 
            
            String sGotAmount = txtPaidAmt.getText() + e.getCharacter();

            if(!"".equals(sGotAmount)) 
            {
                double dPaidAlreadyAmount = Double.parseDouble(sTotalAmtPaidAlready);
                double dGotAmount = Double.parseDouble(sGotAmount);
                double dTotalAmount = dPaidAlreadyAmount - dPreviousPaidAmt + dGotAmount;
                txtTotalAmtPaidYet.setText(Double.toString(dTotalAmount));
            } else {
                txtTotalAmtPaidYet.setText(sTotalAmtPaidAlready);
            }
        }      
    }
}
