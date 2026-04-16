/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllCompaniesListBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sName;
    private final SimpleBooleanProperty bChecked;

    public AllCompaniesListBean(String sSlNo, String sId, String sName, boolean bChecked) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sId = new SimpleStringProperty(sId);
        this.sName = new SimpleStringProperty(sName);
        this.bChecked = new SimpleBooleanProperty(bChecked);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
    }

    public String getSId() {
        return sId.get();
    }

    public void setSId(String sBillNumber) {
        this.sId.set(sBillNumber);
    }
    
    public String getSName() {
        return sName.get();
    }

    public void setSName(String sName) {
        this.sName.set(sName);
    }
    
    public boolean isBChecked() {
        return this.bCheckedProperty().get();
    }

    public SimpleBooleanProperty bCheckedProperty() {
        return bChecked;
    }

    public boolean getBCheckedProperty() {
        return this.bCheckedProperty().get();
    }    
    
    public void setBChecked(boolean bChecked) {
        this.bChecked.set(bChecked);
    }
    
}
