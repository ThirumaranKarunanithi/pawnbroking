/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billcalculator;

import com.magizhchi.pawnbroking.companybillclosing.AvailableBalanceBean;
import java.util.List;

/**
 *
 * @author Tiru
 */
public class DenominationBean {
    
    private String operationName;
    private String billNumbers;
    private String materialTypes;
    private boolean isMultiClose;
    private boolean isMultiOperation;
    private List<AvailableBalanceBean> currencyList;

    public DenominationBean(String operationName, String billNumbers, String materialTypes, boolean isMultiClose, boolean isMultiOperation, List<AvailableBalanceBean> currencyList) {
        this.operationName = operationName;
        this.billNumbers = billNumbers;
        this.materialTypes = materialTypes;
        this.isMultiClose = isMultiClose;
        this.isMultiOperation = isMultiOperation;
        this.currencyList = currencyList;
    }    
    
    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getBillNumbers() {
        return billNumbers;
    }

    public void setBillNumbers(String billNumbers) {
        this.billNumbers = billNumbers;
    }

    public String getMaterialTypes() {
        return materialTypes;
    }

    public void setMaterialTypes(String materialTypes) {
        this.materialTypes = materialTypes;
    }

    public boolean isIsMultiClose() {
        return isMultiClose;
    }

    public void setIsMultiClose(boolean isMultiClose) {
        this.isMultiClose = isMultiClose;
    }

    public boolean isIsMultiOperation() {
        return isMultiOperation;
    }

    public void setIsMultiOperation(boolean isMultiOperation) {
        this.isMultiOperation = isMultiOperation;
    }
    
    public List<AvailableBalanceBean> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<AvailableBalanceBean> currencyList) {
        this.currencyList = currencyList;
    }
            
}
