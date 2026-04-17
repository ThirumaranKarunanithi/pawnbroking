/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillclosing;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author tiruk
 */
public class GoldCloseVerifyCopiesDialogUIController implements Initializable {

    private BillClosingDBOperation dbOp;
    private GoldBillClosingController gParent;
    private SilverBillClosingController sParent;
    private boolean forReceived = true;
    private String sBillNumber = null;
    private String sameName = null; 
    private String sNomineeName = null;    
    private String sMaterialType = null;
    private String sIdType = null;
    private String sIdNumber = null;
    private String GOLD = "G";
    private String SILVER = "S";
    private String CUSTOMER_COPY = "CUST";
    private String COMPANY_COPY = "COMP";
    private String PACKING_COPY = "PACK";
    boolean isNewVerify = true;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private ComboBox<String> cbIsVerifiedCustCopy;
    @FXML
    private ComboBox<String> cbIsVerifiedCompCopy;
    @FXML
    private ComboBox<String> cbIsVerifiedPackCopy;
    @FXML
    private PasswordField txtCustomerCopy;
    @FXML
    private PasswordField txtCompanyCopy;
    @FXML
    private PasswordField txtPackingCopy;
    @FXML
    private TextField txtClosedBy;
    @FXML
    private Button btSame;
    @FXML
    private Button btNominee;
    @FXML
    private TextField txtRelationtoCustomer;
    @FXML
    private Button btSave;
    @FXML
    private VBox comboContainer;
    @FXML
    private VBox txtContainer;
    @FXML
    private VBox labelContainer;
    @FXML
    private Label lbCustCopy;
    @FXML
    private VBox notReceivedContainer;
    @FXML
    private VBox dialogContainer;
    @FXML
    private ComboBox<String> cbIsCardLostBondPrinted;
    @FXML
    private Label lbIdName;
    @FXML
    private TextField txtIdNumber;
    @FXML
    private Button btPrintCardLostBond;

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
                                                if(KV.isControlDown()) {
                                                    if(!btSave.isDisable()) {
                                                      //  btSaveOnAction(null);
                                                    }
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
                                                if(gParent != null) {
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
    }    

    public void setParent(GoldBillClosingController parent, 
            String sBillNumber, String sameName, String sNomineeName, 
            boolean forReceived, String idProofType, String idProofNumber)
    {
        this.gParent = parent;
        this.sBillNumber = sBillNumber;
        this.sameName = sameName;
        this.sNomineeName = sNomineeName;
        this.forReceived = forReceived;
        this.dbOp = parent.dbOp;
        this.sMaterialType = "GOLD";
        this.lbIdName.setText(idProofType);
        this.txtIdNumber.setText(idProofNumber);
                
        if(!forReceived) {
            labelContainer.getChildren().remove(lbCustCopy);
            comboContainer.getChildren().remove(cbIsVerifiedCustCopy);
            txtContainer.getChildren().remove(txtCustomerCopy);    
            txtIdNumber.requestFocus();
            txtIdNumber.positionCaret(txtIdNumber.getText().length());                    
        } else {
            dialogContainer.getChildren().remove(notReceivedContainer);
            txtCustomerCopy.requestFocus();
            txtCustomerCopy.positionCaret(txtCustomerCopy.getText().length());                    
        }                      
    }

    public void setParent(ActionEvent event, GoldBillClosingController parent, 
            String sBillNumber, boolean isNewVerify, boolean forReceived, String packingCopyCode)
    {
        try {
            this.gParent = parent;
            this.sBillNumber = sBillNumber;
            this.dbOp = parent.dbOp;
            this.sMaterialType = "GOLD";
            this.isNewVerify = false;
            this.forReceived = forReceived;
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(this.gParent.dpBillClosingDate.getValue());
            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate)) {
                btSave.setDisable(false);
            } else {
                btSave.setDisable(true);
            }
            
            DataTable table = this.dbOp.getVerificationValues(sBillNumber, sMaterialType);
            boolean isCustCopy = Boolean.valueOf(table.getRow(0).getColumn(0).toString());
            boolean isCompCopy = Boolean.valueOf(table.getRow(0).getColumn(1).toString());
            boolean isPackCopy = Boolean.valueOf(table.getRow(0).getColumn(2).toString());
            boolean isCardLostBondCopy = Boolean.valueOf(table.getRow(0).getColumn(3).toString());
            
            String sClosedBy = table.getRow(0).getColumn(4) != null 
                    ? String.valueOf(table.getRow(0).getColumn(4).toString()) 
                    : "";
            String sRelationClosedBy = table.getRow(0).getColumn(5) != null 
                    ? String.valueOf(table.getRow(0).getColumn(5).toString()) 
                    : "";
            String sProofType = table.getRow(0).getColumn(6) != null 
                    ? String.valueOf(table.getRow(0).getColumn(6).toString()) 
                    : "";
            String sProofNumber = table.getRow(0).getColumn(7) != null 
                    ? String.valueOf(table.getRow(0).getColumn(7).toString()) 
                    : "";
            
            cbIsVerifiedCompCopy.getItems().removeAll(cbIsVerifiedCompCopy.getItems());
            if(isCompCopy) {
                cbIsVerifiedCompCopy.getItems().add("YES");
                cbIsVerifiedCompCopy.setValue("YES");
            } else {
                cbIsVerifiedCompCopy.getItems().add("NO");
                cbIsVerifiedCompCopy.setValue("NO");            
            }

            cbIsVerifiedPackCopy.getItems().removeAll(cbIsVerifiedPackCopy.getItems());
            if(isPackCopy) {
                cbIsVerifiedPackCopy.getItems().add("YES");
                cbIsVerifiedPackCopy.setValue("YES");
            } else {
                cbIsVerifiedPackCopy.getItems().add("NO");
                cbIsVerifiedPackCopy.setValue("NO");            
            }
            
            txtClosedBy.setText(sClosedBy);
            txtRelationtoCustomer.setText(sRelationClosedBy);
            
            if(!forReceived) {
                
                cbIsCardLostBondPrinted.getItems().removeAll(cbIsCardLostBondPrinted.getItems());
                if(isCardLostBondCopy) {
                    cbIsCardLostBondPrinted.getItems().add("YES");
                    cbIsCardLostBondPrinted.setValue("YES");
                } else {
                    cbIsCardLostBondPrinted.getItems().add("NO");
                    cbIsCardLostBondPrinted.setValue("NO");            
                }
                lbIdName.setText(sProofType);
                txtIdNumber.setText(sProofNumber);
                
                labelContainer.getChildren().remove(lbCustCopy);
                comboContainer.getChildren().remove(cbIsVerifiedCustCopy);
                txtContainer.getChildren().remove(txtCustomerCopy);
                txtIdNumber.requestFocus();
                txtIdNumber.positionCaret(txtIdNumber.getText().length());
            } else {
                
                cbIsVerifiedCustCopy.getItems().removeAll(cbIsVerifiedCustCopy.getItems());
                if(isCustCopy) {
                    cbIsVerifiedCustCopy.getItems().add("YES");
                    cbIsVerifiedCustCopy.setValue("YES");
                } else {
                    cbIsVerifiedCustCopy.getItems().add("NO");
                    cbIsVerifiedCustCopy.setValue("NO");            
                }
                
                dialogContainer.getChildren().remove(notReceivedContainer);
                txtPackingCopy.requestFocus();
                txtPackingCopy.positionCaret(txtPackingCopy.getText().length());                      
            }
            
            if(packingCopyCode != null) {
                txtPackingCopy.setText(packingCopyCode);
                txtPackingCopyOnAction(event);     
                btSave.requestFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldCloseVerifyCopiesDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setParent(SilverBillClosingController parent, 
            String sBillNumber, String sameName, String sNomineeName, 
            boolean forReceived, String idProofType, String idProofNumber)
    {
        this.sParent = parent;
        this.sBillNumber = sBillNumber;
        this.sameName = sameName;
        this.sNomineeName = sNomineeName;
        this.forReceived = forReceived;
        this.dbOp = parent.dbOp;
        this.sMaterialType = "SILVER";
        this.lbIdName.setText(idProofType);
        this.txtIdNumber.setText(idProofNumber);
                
        if(!forReceived) {
            labelContainer.getChildren().remove(lbCustCopy);
            comboContainer.getChildren().remove(cbIsVerifiedCustCopy);
            txtContainer.getChildren().remove(txtCustomerCopy);    
            txtIdNumber.requestFocus();
            txtIdNumber.positionCaret(txtIdNumber.getText().length());                    
        } else {
            dialogContainer.getChildren().remove(notReceivedContainer);
            txtCustomerCopy.requestFocus();
            txtCustomerCopy.positionCaret(txtCustomerCopy.getText().length());                    
        }                      
    }

    public void setParent(ActionEvent event, SilverBillClosingController parent, 
            String sBillNumber, boolean isNewVerify, boolean forReceived, String packingCopyCode)
    {
        try {
            this.sParent = parent;
            this.sBillNumber = sBillNumber;
            this.dbOp = parent.dbOp;
            this.sMaterialType = "SILVER";
            this.isNewVerify = false;
            this.forReceived = forReceived;
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(this.sParent.dpBillClosingDate.getValue());
            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate)) {
                btSave.setDisable(false);
            } else {
                btSave.setDisable(true);
            }
            
            DataTable table = this.dbOp.getVerificationValues(sBillNumber, sMaterialType);
            boolean isCustCopy = Boolean.valueOf(table.getRow(0).getColumn(0).toString());
            boolean isCompCopy = Boolean.valueOf(table.getRow(0).getColumn(1).toString());
            boolean isPackCopy = Boolean.valueOf(table.getRow(0).getColumn(2).toString());
            boolean isCardLostBondCopy = Boolean.valueOf(table.getRow(0).getColumn(3).toString());
            
            String sClosedBy = table.getRow(0).getColumn(4) != null 
                    ? String.valueOf(table.getRow(0).getColumn(4).toString()) 
                    : "";
            String sRelationClosedBy = table.getRow(0).getColumn(5) != null 
                    ? String.valueOf(table.getRow(0).getColumn(5).toString()) 
                    : "";
            String sProofType = table.getRow(0).getColumn(6) != null 
                    ? String.valueOf(table.getRow(0).getColumn(6).toString()) 
                    : "";
            String sProofNumber = table.getRow(0).getColumn(7) != null 
                    ? String.valueOf(table.getRow(0).getColumn(7).toString()) 
                    : "";
            
            cbIsVerifiedCompCopy.getItems().removeAll(cbIsVerifiedCompCopy.getItems());
            if(isCompCopy) {
                cbIsVerifiedCompCopy.getItems().add("YES");
                cbIsVerifiedCompCopy.setValue("YES");
            } else {
                cbIsVerifiedCompCopy.getItems().add("NO");
                cbIsVerifiedCompCopy.setValue("NO");            
            }

            cbIsVerifiedPackCopy.getItems().removeAll(cbIsVerifiedPackCopy.getItems());
            if(isPackCopy) {
                cbIsVerifiedPackCopy.getItems().add("YES");
                cbIsVerifiedPackCopy.setValue("YES");
            } else {
                cbIsVerifiedPackCopy.getItems().add("NO");
                cbIsVerifiedPackCopy.setValue("NO");            
            }
            
            txtClosedBy.setText(sClosedBy);
            txtRelationtoCustomer.setText(sRelationClosedBy);
            
            if(!forReceived) {
                
                cbIsCardLostBondPrinted.getItems().removeAll(cbIsCardLostBondPrinted.getItems());
                if(isCardLostBondCopy) {
                    cbIsCardLostBondPrinted.getItems().add("YES");
                    cbIsCardLostBondPrinted.setValue("YES");
                } else {
                    cbIsCardLostBondPrinted.getItems().add("NO");
                    cbIsCardLostBondPrinted.setValue("NO");            
                }
                lbIdName.setText(sProofType);
                txtIdNumber.setText(sProofNumber);
                
                labelContainer.getChildren().remove(lbCustCopy);
                comboContainer.getChildren().remove(cbIsVerifiedCustCopy);
                txtContainer.getChildren().remove(txtCustomerCopy);
                txtIdNumber.requestFocus();
                txtIdNumber.positionCaret(txtIdNumber.getText().length());
            } else {
                
                cbIsVerifiedCustCopy.getItems().removeAll(cbIsVerifiedCustCopy.getItems());
                if(isCustCopy) {
                    cbIsVerifiedCustCopy.getItems().add("YES");
                    cbIsVerifiedCustCopy.setValue("YES");
                } else {
                    cbIsVerifiedCustCopy.getItems().add("NO");
                    cbIsVerifiedCustCopy.setValue("NO");            
                }
                
                dialogContainer.getChildren().remove(notReceivedContainer);
                txtCustomerCopy.requestFocus();
                txtCustomerCopy.positionCaret(txtCustomerCopy.getText().length());                      
            }
            
            if(packingCopyCode != null) {
                txtPackingCopy.setText(packingCopyCode);
                txtPackingCopyOnAction(event);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(GoldCloseVerifyCopiesDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void onSaveClicked(ActionEvent event) {
        try {
            boolean isCardLost = false;
            if(!forReceived) {
                isCardLost = cbIsCardLostBondPrinted.getValue().toUpperCase().equals("YES");
            }
            boolean isCustVerified = cbIsVerifiedCustCopy.getValue().toUpperCase().equals("YES");
            boolean isCompVerified = cbIsVerifiedCompCopy.getValue().toUpperCase().equals("YES");
            boolean isPackVerified = cbIsVerifiedPackCopy.getValue().toUpperCase().equals("YES");
            String sClosedBy = txtClosedBy.getText().toUpperCase().trim();
            String sRelationToClosedBy = txtRelationtoCustomer.getText().toUpperCase().trim();
            dbOp.saveVerifications(isCustVerified, isCompVerified, isPackVerified, isCardLost, 
                    sClosedBy, sRelationToClosedBy, 
                    sMaterialType, sBillNumber);
        } catch (Exception ex) {
            Logger.getLogger(GoldCloseVerifyCopiesDialogUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(sMaterialType.equals("GOLD")) {
            this.gParent.dialog.close();
        } else {
            this.sParent.dialog.close();
        }
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent event) {
    }

    @FXML
    private void btSameClicked(ActionEvent event) {  
        txtClosedBy.setText("SAME");
        txtRelationtoCustomer.setText("SAME");
        txtRelationtoCustomer.requestFocus();
        txtRelationtoCustomer.positionCaret(txtRelationtoCustomer.getText().length());        
    }

    @FXML
    private void btNomineeClicked(ActionEvent event) {
        if(sNomineeName != null) {
            txtClosedBy.setText("NOMI:" + sNomineeName);
            txtRelationtoCustomer.setText("");
        }
        txtRelationtoCustomer.requestFocus();
        txtRelationtoCustomer.positionCaret(txtRelationtoCustomer.getText().length());        
    }

    @FXML
    private void txtCustomerCopyOnAction(ActionEvent event) {
        String materialType = null;
        if(this.sMaterialType.equals("GOLD")) {
            materialType = GOLD;
        } else {
            materialType = SILVER;
        }
        String[] custCopy = txtCustomerCopy.getText().trim().split("-");
        if(custCopy[0].equals(sBillNumber) 
                && custCopy[1].equals(materialType) 
                && custCopy[2].equals(CUSTOMER_COPY)){
            cbIsVerifiedCustCopy.getItems().removeAll(cbIsVerifiedCustCopy.getItems());
            cbIsVerifiedCustCopy.getItems().add("YES");
            cbIsVerifiedCustCopy.setValue("YES");
            txtCompanyCopy.requestFocus();
            txtCompanyCopy.positionCaret(txtCompanyCopy.getText().length());        
        } else {
            cbIsVerifiedCustCopy.getItems().removeAll(cbIsVerifiedCustCopy.getItems());
            cbIsVerifiedCustCopy.getItems().add("NO");
            cbIsVerifiedCustCopy.setValue("NO");    
            PopupUtil.showErrorAlert(event, "Customer copy does not matched, So dont deliver this jewel.");
        }
    }

    @FXML
    private void txtCompanyCopyOnAction(ActionEvent event) {
        String materialType = null;
        if(this.sMaterialType.equals("GOLD")) {
            materialType = GOLD;
        } else {
            materialType = SILVER;
        }        
        String[] custCopy = txtCompanyCopy.getText().trim().split("-");
        if(custCopy[0].equals(sBillNumber) 
                && custCopy[1].equals(materialType) 
                && custCopy[2].equals(COMPANY_COPY)){
            cbIsVerifiedCompCopy.getItems().removeAll(cbIsVerifiedCompCopy.getItems());
            cbIsVerifiedCompCopy.getItems().add("YES");
            cbIsVerifiedCompCopy.setValue("YES");
            txtPackingCopy.requestFocus();
            txtPackingCopy.positionCaret(txtPackingCopy.getText().length());        
        } else {
            cbIsVerifiedCompCopy.getItems().removeAll(cbIsVerifiedCompCopy.getItems());
            cbIsVerifiedCompCopy.getItems().add("NO");
            cbIsVerifiedCompCopy.setValue("NO");        
            PopupUtil.showErrorAlert(event, "Company copy does not matched, So dont deliver this jewel.");
        }        
    }

    @FXML
    private void txtPackingCopyOnAction(ActionEvent event) {
        String materialType = null;
        if(this.sMaterialType.equals("GOLD")) {
            materialType = GOLD;
        } else {
            materialType = SILVER;
        }                
        String[] custCopy = txtPackingCopy.getText().trim().split("-");
        if(custCopy[0].equals(sBillNumber) 
                && custCopy[1].equals(materialType) 
                && custCopy[2].equals(PACKING_COPY)){
            cbIsVerifiedPackCopy.getItems().removeAll(cbIsVerifiedPackCopy.getItems());
            cbIsVerifiedPackCopy.getItems().add("YES");
            cbIsVerifiedPackCopy.setValue("YES");
            txtClosedBy.requestFocus();
            txtClosedBy.positionCaret(txtClosedBy.getText().length());   
            if(sMaterialType.equals("GOLD")) {
                this.gParent.changeStatusToDelivered(true);
            } else {
                this.sParent.changeStatusToDelivered(true);
            }            
        } else {
            cbIsVerifiedPackCopy.getItems().removeAll(cbIsVerifiedPackCopy.getItems());
            cbIsVerifiedPackCopy.getItems().add("NO");
            cbIsVerifiedPackCopy.setValue("NO");        
            PopupUtil.showErrorAlert(event, "Packing copy does not matched, So dont deliver this jewel.");
        }        
    }

    @FXML
    private void txtClosedByOnAction(ActionEvent event) {
        txtRelationtoCustomer.requestFocus();
        txtRelationtoCustomer.positionCaret(txtRelationtoCustomer.getText().length());        
    }

    @FXML
    private void txtRelationtoCustomerOnAction(ActionEvent event) {
    }

    @FXML
    private void txtIdNumberOnAction(ActionEvent event) {
    }

    @FXML
    private void btPrintCardLostBondClicked(ActionEvent event) {
        cbIsCardLostBondPrinted.getItems().removeAll(cbIsCardLostBondPrinted.getItems());
        cbIsCardLostBondPrinted.getItems().add("YES");
        cbIsCardLostBondPrinted.setValue("YES");
        
        if(sMaterialType.equals("GOLD")) {
            this.gParent.btCardLostPrintClicked(event);
        } else {
            this.sParent.btCardLostPrintClicked(event);
        }
    }

}
