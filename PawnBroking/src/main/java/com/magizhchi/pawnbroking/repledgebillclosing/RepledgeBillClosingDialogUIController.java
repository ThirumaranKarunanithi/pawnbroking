/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillclosing;

import com.magizhchi.pawnbroking.account.TodaysAccountJewelRepledgeBean;
import com.magizhchi.pawnbroking.common.CommonConstants;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RepledgeBillClosingDialogUIController implements Initializable {

    private RepledgeGoldBillClosingController parent;
    private RepledgeBillClosingDBOperation dbOp;
    
    String sCompanyBillNumber = null; 
    String sRepBillIds = null;
            
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRepList;

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
        
        try {
            dbOp = new RepledgeBillClosingDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    

    @FXML
    private void tbRNYDeliveredOnMouseClicked(MouseEvent event) {            
        int index = tbRepList.getSelectionModel().getSelectedIndex();
        if (event.getClickCount() == 1 && (index >= 0) ) {
            String sRepledgeBillId = tbRepList.getItems().get(index).getSRepledgeBillId();    
            parent.tbDialogTablesOnMouseClicked(sRepledgeBillId, this.sCompanyBillNumber, this.sRepBillIds);
        }
        parent.dialog.close();
    }
    
    public void setParent(RepledgeGoldBillClosingController parent, String sCompanyBillNumber, String sRepBillIds)
    {
        this.parent = parent;
        this.sCompanyBillNumber = sCompanyBillNumber;
        this.sRepBillIds = sRepBillIds;
    }
    
    public void setInitValues(String sRepBillIds) {
    
        if(sRepBillIds != null) {
            int repBillsCount = 0;        
            for(String repBId : sRepBillIds.split(",")) {

                try {
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
                        tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName,
                                sRepledgeDate,
                                dRepledgeAmount, sBillNumber, sDate, dAmount, sStatus, sRepBillId, ""));

                        repBillsCount++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RepledgeBillClosingDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
