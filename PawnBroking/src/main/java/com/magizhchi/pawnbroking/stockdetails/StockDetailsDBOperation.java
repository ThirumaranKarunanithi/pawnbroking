/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.stockdetails;

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
public class StockDetailsDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public StockDetailsDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(StockDetailsDBOperation.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public DataTable getCompAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date, CB.items, "
                + "CB.amount, CB.interest, "
                + "CB.status, CB.DOCUMENT_CHARGE, "
                + "CB.TOTAL_ADVANCE_AMOUNT_PAID, "
                + "CB.JEWEL_MATERIAL_TYPE, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL, "
                + "CB.gross_weight,  CB.REPLEDGE_BILL_ID, "
                + "CONCAT(CB.CUSTOMER_NAME, ' ', CB.SPOUSE_TYPE, ' ',  CB.SPOUSE_NAME), CB.AREA "               
                + "FROM COMPANY_BILLING CB "
                + "WHERE CB.COMPANY_ID = ? "
                + "AND (CB.repledge_bill_id IS NULL OR CB.repledge_bill_id = '')"
                + "AND STATUS IN ('OPENED', 'LOCKED') ";

        if(!sMaterialType.equals("BOTH")) {
            sql += "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        }
        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY BILL";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(2, sMaterialType);
            }
            
            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("OPENING_DATE") || sFilterScript.contains("AMOUNT")) {
                        //stmt.SET.setObject(i+3, sVals[i]);
                    } else {
                        //stmt.setString(i+3, "%"+sVals[i]+"%");
                    }
                }
            }
            
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));
                row.addColumn(rs.getString(9));                
                row.addColumn(Double.toString(rs.getDouble(11)));                
                row.addColumn(rs.getString(12));  
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
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

    public DataTable getAllCompAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date, CB.items, "
                + "CB.amount, CB.interest, "
                + "CB.status, CB.DOCUMENT_CHARGE, "
                + "CB.TOTAL_ADVANCE_AMOUNT_PAID, "
                + "CB.JEWEL_MATERIAL_TYPE, CB.gross_weight, "
                + "COALESCE(CB.repledge_bill_id, ''), "
                + "CONCAT(CB.CUSTOMER_NAME, ' ', CB.SPOUSE_TYPE, ' ',  CB.SPOUSE_NAME), CB.AREA, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL "
                + "FROM COMPANY_BILLING CB "
                + "WHERE CB.COMPANY_ID = ? "
                + "AND STATUS IN ('OPENED', 'LOCKED') ";

        if(!sMaterialType.equals("BOTH")) {
            sql += "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        }
        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY BILL";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(2, sMaterialType);
            }
            
            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("OPENING_DATE") || sFilterScript.contains("AMOUNT")) {
                        //stmt.SET.setObject(i+3, sVals[i]);
                    } else {
                        //stmt.setString(i+3, "%"+sVals[i]+"%");
                    }
                }
            }
            
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));
                row.addColumn(rs.getString(9));
                row.addColumn(Double.toString(rs.getDouble(10)));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
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
    
    public DataTable getRepCompAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date, CB.items, "
                + "CB.amount, CB.interest, "
                + "CB.status, CB.DOCUMENT_CHARGE, "
                + "CB.TOTAL_ADVANCE_AMOUNT_PAID, "
                + "CB.JEWEL_MATERIAL_TYPE, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL, "
                + "CB.gross_weight,  CB.REPLEDGE_BILL_ID, "
                + "CONCAT(CB.CUSTOMER_NAME, ' ', CB.SPOUSE_TYPE, ' ',  CB.SPOUSE_NAME), CB.AREA " +
                "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                "AND CB.repledge_bill_id IS NOT NULL " +
                "AND RB.COMPANY_ID = ? " +
                "AND CB.STATUS IN ('OPENED', 'LOCKED') ";

        try {

            if(!sMaterialType.equals("BOTH")) {
                sql += "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
            }
            
            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY BILL";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(2, sMaterialType);
            }            
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));
                row.addColumn(rs.getString(9));
                row.addColumn(Double.toString(rs.getDouble(11)));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
                
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

    public DataTable getRepAloneAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
                
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.COMPANY_BILL_NUMBER, " +
                    "RB.OPENING_DATE, RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, " +
                    " REGEXP_REPLACE(COALESCE(RB.REPLEDGE_BILL_ID, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND CB.repledge_bill_id IS NOT NULL " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY RB.OPENING_DATE DESC ";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);            
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(6)));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));
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

    public DataTable getAllRepAloneAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
                
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.COMPANY_BILL_NUMBER, " +
                    "RB.OPENING_DATE, RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, " +
                    " REGEXP_REPLACE(COALESCE(RB.REPLEDGE_BILL_ID, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND CB.repledge_bill_id IS NOT NULL " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') ";
        
        if(!sMaterialType.equals("BOTH")) {
            sql += "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        }

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY BILL";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(2, sMaterialType);  
            }
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(6)));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));
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
    
    public DataTable getFilteredCustomerNames(String sCustomerName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT "
                + "CONCAT(customer_name, ' ', spouse_type, ' ',  spouse_name, ' \n\t ', street, ' \n\t ', area), "
                + "customer_name, gender, spouse_type, "
                + "spouse_name, door_number, street, area, city, mobile_number "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? ";

        if(sCustomerName != null) {
            sql += "AND customer_name LIKE '%"+sCustomerName+"%' ORDER BY customer_name, spouse_name";
        }
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
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
    
    public String getInterestType() throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT DAY_OR_MONTHLY_INTEREST FROM COMPANY WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getString(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return null;
    }

    public String getRepInterestType(String sRepId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT DAY_OR_MONTHLY_INTEREST FROM REPLEDGE WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sRepId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getString(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return null;
    }
    
    public String[] getRepReduceOrMinimumDaysOrMonths(String repId, String sMaterialType, String sType) throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[2];
        
        String sql ="SELECT COALESCE(DAYS_OR_MONTHS, 0), REDUCTION_TYPE "
                + "FROM REPLEDGE_REDUCE_MONTHS_OR_DAYS "
                + "WHERE REPLEDGE_ID = ? "                 
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE ";

        if(!sMaterialType.equals("BOTH")) {
            sql += "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        }

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, repId);            
            stmt.setString(2, sType);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(3, sMaterialType);               
            }
            
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                data[0] = Integer.toString(rs.getInt(1));		                   
                data[1] = rs.getString(2);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return data;
    }

    public String[] getReduceOrMinimumDaysOrMonths(String sMaterialType, String sType) throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[2];
        
        String sql ="SELECT COALESCE(DAYS_OR_MONTHS, 0), REDUCTION_TYPE "
                + "FROM COMPANY_REDUCE_MONTHS_OR_DAYS "
                + "WHERE COMPANY_ID = ? " 
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE ";

        if(!sMaterialType.equals("BOTH")) {
            sql += "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        }
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sType);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(3, sMaterialType);               
            }
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                data[0] = Integer.toString(rs.getInt(1));		                   
                data[1] = rs.getString(2);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return data;
    }
    
    public String getCompFormula(String sDate, double dAmount, String sMaterialType) throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT FORMULA " +
                    "FROM COMPANY_FORMULA " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND FORMULA_OPERATION_TYPE = ?::OPERATION_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO "
                + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, "CLOSE");
            stmt.setDouble(4, dAmount);
            stmt.setDate(5, sqlDateOpenDate); 
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getString(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return "0";
    }

    public String getRepFormula(String sRepledgeId, double dAmount, String sMaterialType) throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT FORMULA " +
                    "FROM REPLEDGE_FORMULA " +
                    "WHERE REPLEDGE_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND FORMULA_OPERATION_TYPE = ?::OPERATION_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sRepledgeId);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, "CLOSE");
            stmt.setDouble(4, dAmount);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getString(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return "";
    }
    
    public double getRemainingDaysAsMonths(String sDate, double iRemainingDays, String sMaterialType) throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT AS_MONTH " +
                    "FROM COMPANY_MONTH_SETTING " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN DAYS_FROM AND DAYS_TO "
                + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));            
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, iRemainingDays);
            stmt.setDate(4, sqlDateOpenDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getDouble(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null) {
                rs.close();
            }
            if(stmt != null) {
                stmt.close();
            }
            //roleMasterConn.close();
        }        
        return 0;
    }

    public double getRepRemainingDaysAsMonths(String sRepId, String sDate, double iRemainingDays, String sMaterialType) throws SQLException
    {

        //connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT AS_MONTH " +
                    "FROM REPLEDGE_MONTH_SETTING " +
                    "WHERE REPLEDGE_ID = ? " +
                    "AND ? BETWEEN DAYS_FROM AND DAYS_TO "
                + "AND ? BETWEEN DATE_FROM AND DATE_TO ";

        try {

            if(!sMaterialType.equals("BOTH")) {
                sql += "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
            }
            
            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));            
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sRepId);            
            stmt.setDouble(2, iRemainingDays);
            stmt.setDate(3, sqlDateOpenDate);
            if(!sMaterialType.equals("BOTH")) {
                stmt.setString(4, sMaterialType);               
            }
            
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getDouble(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            //roleMasterConn.close();
        }        
        return 0;
    }
    
    public DataTable getAllRepledgeNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT name FROM REPLEDGE ORDER BY NAME";
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                dataTable.add(row);                	       
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            //roleMasterConn.close();
        } 

        return dataTable;	
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

    public String[] getFineCharges(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[4];
        
        String sql ="SELECT interest_type, month_days_from, month_days_to, charged_interest "
                + "FROM fine_charges "
                + "WHERE company_id = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);            
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                data[0] = rs.getString(1);		                   
                data[1] = Double.toString(rs.getDouble(2));
                data[2] = Double.toString(rs.getDouble(3));
                data[3] = Double.toString(rs.getDouble(4));
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs!=null && !rs.isClosed())
                rs.close();
            if(rs!=null && !stmt.isClosed())
                stmt.close();
        }        
        return data;
    }
    
    public boolean getAddViewUpdate(String sRoleId, String sTabName, String sScreenName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_view " +
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
                return rs.getBoolean(1);
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
        return false;
    }
    
}
