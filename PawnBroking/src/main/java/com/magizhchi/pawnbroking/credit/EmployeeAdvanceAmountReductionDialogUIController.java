/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.credit;

import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public class EmployeeAdvanceAmountReductionDialogUIController implements Initializable {

    EmployeeCreditController parent = null;
    int reduceCount = 0;
    double reduceAmount = 0;
    DataTable values = null;    
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<EmployeeReduceBean> tbReduce;
    @FXML
    private TextField txtSelectedReduction;
    @FXML
    private TextField txtSelectedReductionAmount;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {

        reduceCount = 0;
        reduceAmount = 0.0;
        
        tbReduce.getItems().forEach((bean) -> {
            if(bean.isBChecked()) {
                reduceCount++;
                reduceAmount += bean.getDAmount();
            }
        }); 
        
        txtSelectedReduction.setText(Integer.toString(reduceCount));
        txtSelectedReductionAmount.setText(Double.toString(reduceAmount));
        
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    private void selectOrDeSelectAll(TableView<EmployeeReduceBean> table, boolean toSelect) {
 
        reduceCount = 0;
        reduceAmount = 0.0;
        
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
            
            if(toSelect) {
                reduceCount++;
                reduceAmount += bean.getDAmount();
            } 
        });
        
        txtSelectedReduction.setText(Integer.toString(reduceCount));
        txtSelectedReductionAmount.setText(Double.toString(reduceAmount));

    }
    
    @FXML
    private void btReductionSelectAllOnAction(ActionEvent event) {
        
        selectOrDeSelectAll(tbReduce, true);
    }

    @FXML
    private void btReductionDeSelectAllOnAction(ActionEvent event) {
        
        selectOrDeSelectAll(tbReduce, false);
    }

    @FXML
    private void btSaveOnAction(ActionEvent event) {
        
        DataTable values = new DataTable();
        tbReduce.getItems().stream().forEach((bean) -> {
            final DataRow row = new DataRow();
            row.addColumn(bean.getSSlNo());
            row.addColumn(bean.getSId());
            row.addColumn(bean.getSDate());
            row.addColumn(bean.getSReason());
            row.addColumn(bean.getDAmount());
            row.addColumn(bean.isBChecked());
            values.add(row); 
        });
        parent.dtReduceList = values;
        parent.dialog.close();
    }
    
    public void setParent(EmployeeCreditController parent) {
 
        this.parent = parent;
    }
    
    public void setInitValus(DataTable values) {
        
        this.values = values;        
        tbReduce.getItems().remove(0, tbReduce.getItems().size());
        for(int i=0; i<values.getRowCount(); i++) {            
            String sSlNo = values.getRow(i).getColumn(0).toString();
            String sId = values.getRow(i).getColumn(1).toString();
            String sDate = values.getRow(i).getColumn(2).toString();
            String sReason = values.getRow(i).getColumn(3).toString();
            double sAmount = Double.parseDouble(values.getRow(i).getColumn(4).toString());
            boolean isReduced = Boolean.parseBoolean(values.getRow(i).getColumn(5).toString());
            tbReduce.getItems().add(new EmployeeReduceBean(sSlNo, sId, sDate, sReason, sAmount, isReduced));
            if(isReduced) {
                reduceCount++;
                reduceAmount += sAmount;
            }
        }        
        txtSelectedReduction.setText(Integer.toString(reduceCount));
        txtSelectedReductionAmount.setText(Double.toString(reduceAmount));
    }

}
