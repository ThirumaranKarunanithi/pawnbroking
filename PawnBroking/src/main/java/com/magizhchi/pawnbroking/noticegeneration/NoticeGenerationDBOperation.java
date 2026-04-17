/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.noticegeneration;

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
public class NoticeGenerationDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public NoticeGenerationDBOperation(String sDB, String sIP, String sPort, String sSchema,
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

    private void connectDB() throws SQLException
    {		
            try {
                    roleMasterConn = DriverManager.getConnection("jdbc:"+sDB+"://"+sIP+":"+sPort+"/"+sSchema,sDBUsername, sDBPassword);
            } catch (SQLException e) {
                    throw e;
            }		
    }

    public void commit() {
        try {
            roleMasterConn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGenerationDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
               
    public DataTable getNoticeValues(String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT bill_number, customer_name, spouse_type,  " +
                    "spouse_name, door_number, street, area, city, opening_date, amount, mobile_number, JEWEL_MATERIAL_TYPE " +
                    "FROM COMPANY_BILLING " +
                    "where COMPANY_ID = ? " +
                    "AND opening_date between ? and ? " +
                    "AND STATUS IN ('OPENED', 'LOCKED') " +
                    "GROUP BY bill_number, customer_name, gender, spouse_type,  " +
                    "spouse_name, door_number, street, area, city, opening_date, amount, mobile_number, JEWEL_MATERIAL_TYPE " +
                    "ORDER BY customer_name, spouse_type, spouse_name ";
        
        try {
            java.sql.Date sqlDateFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlDateToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDateFromDate);
            stmt.setDate(3, sqlDateToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
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

    public DataTable getMultiBilledCustomerList(String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT cust.cnt, customer_name, spouse_type,  " +
                    "spouse_name, door_number, street, area, city " +
                    "FROM (SELECT DISTINCT count(customer_name) cnt, customer_name, spouse_type,  " +
                    "spouse_name, door_number, street, area, city " +
                    "FROM COMPANY_BILLING  " +
                    "where COMPANY_ID = ? " +
                    "and opening_date between ? and ? " +
                    "AND STATUS IN ('OPENED', 'LOCKED') " +
                    "GROUP BY customer_name, gender, spouse_type,  " +
                    "spouse_name, door_number, street, area, city " +
                    "ORDER BY customer_name, spouse_type, spouse_name " +
                    ") cust " +
                    "where cust.cnt > 1";
        
        try {
            java.sql.Date sqlDateFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlDateToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDateFromDate);
            stmt.setDate(3, sqlDateToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
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

    public int getNoticeCustomerCount(String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                    
        
        String sql = "SELECT COUNT(cust.cnt) " +
                    "FROM (SELECT DISTINCT count(customer_name) cnt, customer_name, spouse_type,  " +
                    "spouse_name, door_number, street, area, city " +
                    "FROM COMPANY_BILLING  " +
                    "where COMPANY_ID = ? " +
                    "and opening_date between ? and ? " +
                    "AND STATUS IN ('OPENED', 'LOCKED') " +
                    "GROUP BY customer_name, gender, spouse_type,  " +
                    "spouse_name, door_number, street, area, city " +
                    "ORDER BY customer_name " +
                    ") cust ";
        
        try {
            java.sql.Date sqlDateFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlDateToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDateFromDate);
            stmt.setDate(3, sqlDateToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                return rs.getInt(1);
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

        return 0;	
    }    
    
    public String[] getNoticeValues() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[2];
        
        String sql ="SELECT notice_charge_date, COALESCE(notice_charge_amount, 0) "
                + "FROM company "
                + "WHERE ID = ? ";
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                data[0] = format.format(rs.getDate(1).toLocalDate());		                   
                data[1] = Double.toString(rs.getDouble(2));
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs!=null && !rs.isClosed())
                rs.close();
            if(!stmt.isClosed())
                stmt.close();
            roleMasterConn.close();
        }        
        return data;
    }
    
    public DataTable getThisCustomerNoticeValues(String sFromDate, String sToDate, AllDetailsBean bean) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\\\D', '', 'g'), '9999999999')) AS SLNO, " +
                    "BILL_NUMBER, OPENING_DATE, AMOUNT   " +
                    "FROM COMPANY_BILLING  " +
                    "where COMPANY_ID = ?  " +
                    "AND opening_date between ? and ?  " +
                    "AND STATUS IN ('OPENED', 'LOCKED')  " +
                    "AND customer_name = ? " +
                    "AND spouse_type = ?   " +
                    "AND spouse_name = ? " +
                    "AND door_number = ? " +
                    "AND street = ? " +
                    "AND area = ? " +
                    "AND city = ? " +
                    "ORDER BY OPENING_DATE ";
        
        try {
            java.sql.Date sqlDateFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlDateToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDateFromDate);
            stmt.setDate(3, sqlDateToDate);
            stmt.setString(4, bean.getCustomerName());
            stmt.setString(5, bean.getSpouseType());
            stmt.setString(6, bean.getSpouseName());
            stmt.setString(7, bean.getDoorNo());
            stmt.setString(8, bean.getStreet());
            stmt.setString(9, bean.getArea());
            stmt.setString(10, bean.getCity());
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
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
