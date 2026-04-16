/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.itemmaster;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class ItemsBean {
    
    private final SimpleStringProperty sItem;
    private final SimpleStringProperty sStatus;

    public ItemsBean(String sItem, String sStatus) {
        
        this.sItem = new SimpleStringProperty(sItem);
        this.sStatus = new SimpleStringProperty(sStatus);
    }

    public String getSItem() {
        return sItem.get();
    }
        
    public void setSItem(String sItem) {
        this.sItem.set(sItem);
    }    

    public String getSStatus() {
        return sStatus.get();
    }
        
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }    
    
}
