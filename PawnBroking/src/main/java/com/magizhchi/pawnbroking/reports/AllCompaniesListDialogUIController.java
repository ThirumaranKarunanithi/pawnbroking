/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.companymaster.AllCompaniesListBean;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class AllCompaniesListDialogUIController implements Initializable {

    Report8020Controller parent = null;
    DataTable values = null;    
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<AllCompaniesListBean> tbReduce;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
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
        StringBuilder sb = new StringBuilder();
        tbReduce.getItems().stream().forEach((bean) -> {
            if(bean.isBChecked()) {
                sb.append("'");
                sb.append(bean.getSId());
                sb.append("'");
                sb.append(",");
            }
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        parent.txtCompanyIds.setText(sb.toString());
        parent.dialog.close();        
    }
    
    private void selectOrDeSelectAll(TableView<AllCompaniesListBean> table, boolean toSelect) {
 
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }
 
    public void setParent(Report8020Controller parent) {
        this.parent = parent;
    }
    
    public void setInitValus(DataTable values) {
        
        this.values = values;        
        tbReduce.getItems().remove(0, tbReduce.getItems().size());
        for(int i=0; i<values.getRowCount(); i++) {            
            String sSlNo = Integer.toString(i+1);
            String sId = values.getRow(i).getColumn(0).toString();
            String sName = values.getRow(i).getColumn(1).toString();
            tbReduce.getItems().add(new AllCompaniesListBean(sSlNo, sId, sName, true));
        }        
    }

    
}
