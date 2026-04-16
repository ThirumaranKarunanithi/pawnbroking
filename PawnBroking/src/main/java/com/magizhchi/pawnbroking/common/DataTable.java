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
public class DataTable {
    
    private List<DataRow> rows;

    public DataTable() {
        this.rows = new ArrayList<>();
    }
    
    public DataTable(DataRow...rows) {
        this.rows = new ArrayList<>();
        this.rows.addAll(Arrays.asList(rows));
    }
    
    public void add(DataRow...rows) {
        this.rows.addAll(Arrays.asList(rows));
    }    
    
    public DataRow getRow(int rowNum) {
        return this.rows.get(rowNum);
    }

    public List<DataRow> getAllRows() {
        return this.rows;
    }
    
    public int getRowCount() {
        return this.rows.size();
    }
    
    public int getColumnCount() {
        return this.rows.get(0).getColumnCount();
    }
    
}
