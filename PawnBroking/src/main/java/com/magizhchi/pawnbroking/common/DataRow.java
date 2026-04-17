/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author SONY
 */
public class DataRow {
    
    private List<Object> objects;

    public DataRow() {
        this.objects = new ArrayList<>();
    }
    
    public DataRow(Object...objects) {
        this.objects = new ArrayList<>();
        this.objects.addAll(Arrays.asList(objects));
    }
    
    public void addColumn(Object...objects) {
        this.objects.addAll(Arrays.asList(objects));
    }

    public Object getColumn(int index) {
        return this.objects.get(index);
    }
    
    public int getColumnCount() {
        return this.objects.size();
    }
    
    public String toString() {
        StringBuilder rowVals = new StringBuilder();
        rowVals.append("[");
        for(Object obj : objects) {
            rowVals.append(obj.toString()).append(",");
        }
        rowVals.deleteCharAt(rowVals.lastIndexOf(",")).append("]");
        return rowVals.toString();
    }
}
