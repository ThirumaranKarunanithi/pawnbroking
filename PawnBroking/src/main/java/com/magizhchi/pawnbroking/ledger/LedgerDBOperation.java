/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.ledger;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class LedgerDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public LedgerDBOperation(String sDB, String sIP, String sPort, String sSchema,
                    String sDBUsername, String sDBPassword) throws ClassNotFoundException
    {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                    throw e;
            }

            this.sDB = sDB;
            this.sIP = sIP;
            this.sPort = sPort;
            this.sSchema = sSchema;
            this.sDBUsername = sDBUsername;
            this.sDBPassword = sDBPassword;
    }

    public void connectDB() throws SQLException
    {		
        try {
            if(roleMasterConn == null || roleMasterConn.isClosed()) {
                roleMasterConn = DriverManager.getConnection("jdbc:"+sDB+"://"+sIP+":"+sPort+"/"+sSchema,sDBUsername, sDBPassword);
            }
        } catch (SQLException e) {
                throw e;
        }		
    }

    public void disConnectDB() throws SQLException
    {		
        try {
            roleMasterConn.close();
        } catch (SQLException e) {
                throw e;
        }		
    }
    
    public void commit() {
        try {
            roleMasterConn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(LedgerDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public boolean getAddViewUpdate(String sRoleId, String sTabName, String sScreenName, String sActionName) throws SQLException
    {

        boolean addViewUpdate = false;
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT " + sActionName + " " +
                "FROM role_detail " +
                "WHERE tab_name = ? " +
                "AND screen_name = ? " +
                "AND role_id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, sTabName);
            stmt.setString(2, sScreenName);
            stmt.setString(3, sRoleId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                addViewUpdate = rs.getBoolean(1);
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        }        
        return addViewUpdate;
    }
    
    public DataTable getLedgerValues(String sMaterialType, String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date, CB.customer_name, CB.spouse_type, CB.spouse_name,"
                + "CONCAT(door_number, ', ', street, ', ',  area, ', ',  city) address, "
                + "CB.amount, CB.items, CB.gross_weight, CB.closing_date, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL "
                + "FROM COMPANY_BILLING CB "
                + "WHERE CB.COMPANY_ID = ? "
                + "and CB.opening_date between ? and ? "
                + "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "ORDER BY CB.opening_date, BILL";
        try {
            java.sql.Date fromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date toDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setDate(2, fromDate);
            stmt.setDate(3, toDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));     
                if(rs.getDate(10) != null) {
                    row.addColumn(format.format(rs.getDate(10).toLocalDate()));
                }
                dataTable.add(row);                	       
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return dataTable;	
    }

    public DataTable getLedgerStockValues(String sMaterialType, String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date, CB.customer_name, CB.spouse_type, CB.spouse_name,"
                + "CONCAT(door_number, ', ', street, ', ',  area, ', ',  city) address, "
                + "CB.amount, CB.items, CB.gross_weight, CB.closing_date, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL "
                + "FROM COMPANY_BILLING CB "
                + "WHERE CB.COMPANY_ID = ? "
                + "and CB.opening_date between ? and ? "
                + "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "and cb.status in ('OPENED', 'LOCKED') "
                + "ORDER BY CB.opening_date, BILL";
        try {
            java.sql.Date fromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date toDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setDate(2, fromDate);
            stmt.setDate(3, toDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));     
                if(rs.getDate(10) != null) {
                    row.addColumn(format.format(rs.getDate(10).toLocalDate()));
                }
                dataTable.add(row);                	       
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return dataTable;	
    }
    
}
