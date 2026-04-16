/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.mainscreen.OwnerMainScreenController;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import net.sf.jasperreports.engine.JRException;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class BillOpeningFilingStickerDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;

    public OwnerMainScreenController parent;
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

    public void setParent(OwnerMainScreenController parent)
    {
        this.parent = parent;
    }
    
    @FXML
    private void onSaveClicked(ActionEvent event) {
        
        boolean directPrint = false;
        if(txtFrom.getText() != null && !txtFrom.getText().isEmpty()) {
        
            try {
                String sPackingFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\file_sticker_for_bills.jasper";                        
                PrintService service = PrintServiceLookup.lookupDefaultPrintService();        
                String sPrinterName = service.getName();
                // packing copy params                
                List<Map<String, Object>> paramList = new ArrayList<>();
                paramList.add(packingParamPreparation());
                
                NoticeUtil noticeUtil = new NoticeUtil();                
                if(directPrint) {
                    noticeUtil.mergeaAndGenerateNoticeOperationDirectPrint(true, 
                            sPrinterName, packingParamPreparation(), 
                            sPackingFileName);
                } else {
                    noticeUtil.mergeaAndGenerateNoticeOperation("Filing Sticker", paramList, 
                            sPackingFileName);                    
                }
            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(BillOpeningFilingStickerDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(BillOpeningFilingStickerDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert("Not any details filled to print the records. ");
        }            
    }

    private Map<String, Object> packingParamPreparation() throws SQLException {
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FROMBILLNO", txtFrom.getText());
        parameters.put("TOBILLNO", txtTo.getText()); 
        return parameters;
    }
    
    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }
    
}
