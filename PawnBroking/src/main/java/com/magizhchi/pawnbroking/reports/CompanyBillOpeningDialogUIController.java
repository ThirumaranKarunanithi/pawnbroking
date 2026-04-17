/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.companybillopening.SilverBillOpeningController;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class CompanyBillOpeningDialogUIController implements Initializable {

    private TrialBalanceController parent;
    private boolean isGoldOperation;
    public Stage dialog;
    
    final String goldBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/GoldBillOpening.fxml";
    final String silverBillOpeningScreen = "/com/magizhchi/pawnbroking/companybillopening/SilverBillOpening.fxml";
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<CompanyBillOpeningAllDetailsBean> tbTodaysAccount;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbTodaysAccount.setRowFactory(tv -> new TableRow<CompanyBillOpeningAllDetailsBean>() {
            @Override
            public void updateItem(CompanyBillOpeningAllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (Double.parseDouble(item.getSToGive()) != Double.parseDouble(item.getSGiven())) {
                    setStyle(Util.getStyle("#000000", "#6FB7FF").toString());    
                } else {
                    setStyle("");
                }
            }
        });
        
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
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbTodaysAccount.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sStatus = tbTodaysAccount.getItems().get(index).getSStatus();
            if("OPENED".equals(sStatus) || "LOCKED".equals(sStatus)) 
            {
                if(isGoldOperation) {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillOpeningScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    GoldBillOpeningController gon = (GoldBillOpeningController) loader.getController();
                    gon.viewBill(tbTodaysAccount.getItems().get(index).getSBillNumber());

                    dialog.setTitle("Gold Bill Opening");      
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    dialog.setX(0);
                    dialog.setY(5);
                    dialog.setWidth(bounds.getWidth());
                    dialog.setHeight(bounds.getHeight()-5);
                    dialog.setResizable(false);
                    Scene scene = new Scene(root);        
                    dialog.setScene(scene);
                    dialog.showAndWait();        

                } else {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillOpeningScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    SilverBillOpeningController gon = (SilverBillOpeningController) loader.getController();
                    gon.viewBill(tbTodaysAccount.getItems().get(index).getSBillNumber());

                    dialog.setTitle("Silver Bill Opening");      
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    dialog.setX(0);
                    dialog.setY(5);
                    dialog.setWidth(bounds.getWidth());
                    dialog.setHeight(bounds.getHeight()-5);
                    dialog.setResizable(false);
                    Scene scene = new Scene(root);        
                    dialog.setScene(scene);
                    dialog.showAndWait();                    
                }
            } else 
            {
                if(isGoldOperation) {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
                    gon.viewBill(tbTodaysAccount.getItems().get(index).getSBillNumber());

                    dialog.setTitle("Gold Bill Closing");      
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    dialog.setX(0);
                    dialog.setY(5);
                    dialog.setWidth(bounds.getWidth());
                    dialog.setHeight(bounds.getHeight()-5);
                    dialog.setResizable(false);
                    Scene scene = new Scene(root);        
                    dialog.setScene(scene);
                    dialog.showAndWait();        

                } else {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillClosingScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    SilverBillClosingController gon = (SilverBillClosingController) loader.getController();
                    gon.viewBill(tbTodaysAccount.getItems().get(index).getSBillNumber());

                    dialog.setTitle("Silver Bill Closing");      
                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    dialog.setX(0);
                    dialog.setY(5);
                    dialog.setWidth(bounds.getWidth());
                    dialog.setHeight(bounds.getHeight()-5);
                    dialog.setResizable(false);
                    Scene scene = new Scene(root);        
                    dialog.setScene(scene);
                    dialog.showAndWait();                    
                }
            
            }
        }
    }
    
    public void setParent(TrialBalanceController parent, boolean isGoldOperation)
    {
        this.parent = parent;
        this.isGoldOperation = isGoldOperation;
    }
    
    public void setInitValues(DataTable values) {
    
        tbTodaysAccount.getItems().remove(0, tbTodaysAccount.getItems().size());
        for(int i=0; i<values.getRowCount(); i++) {            
            String sSlNo = values.getRow(i).getColumn(0).toString();
            String sDate = values.getRow(i).getColumn(2).toString();
            String sBillNumber = values.getRow(i).getColumn(1).toString();
            String sStatus = values.getRow(i).getColumn(8).toString();
            String sName = values.getRow(i).getColumn(3).toString();
            String sItems = values.getRow(i).getColumn(4).toString();
            String sAmount = values.getRow(i).getColumn(5).toString();
            String sToGive = values.getRow(i).getColumn(6).toString();
            String sGiven = values.getRow(i).getColumn(7).toString();
            String sCreatedUser = values.getRow(i).getColumn(9).toString();
            String sInterest = values.getRow(i).getColumn(10).toString();
            String sDocumentCharge = values.getRow(i).getColumn(11).toString();
            String sCreatedTime = values.getRow(i).getColumn(12).toString();
            double dInterstedAmt = Double.parseDouble(sAmount) - Double.parseDouble(sToGive) 
                    - Double.parseDouble(sDocumentCharge);
            String sInterestedAmt = String.format("%.0f", dInterstedAmt);
            tbTodaysAccount.getItems().add(new CompanyBillOpeningAllDetailsBean(sSlNo, 
                    sDate, sBillNumber, sStatus, sName, sItems, sAmount, sInterest, 
                    sInterestedAmt, sDocumentCharge, sToGive, sGiven, sCreatedUser, sCreatedTime));
        }
    }
    
}
