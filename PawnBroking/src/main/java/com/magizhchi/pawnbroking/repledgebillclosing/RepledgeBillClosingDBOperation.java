/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillclosing;

import com.magizhchi.pawnbroking.billcalculator.RepAllDetailsBean;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class RepledgeBillClosingDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public RepledgeBillClosingDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(RepledgeBillClosingDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public DataTable getAllRepledgeNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME FROM REPLEDGE WHERE STATUS = ?::REPLEDGE_STATUS";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, "ACTIVE");
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
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
    
    public HashMap<String, String> getAllHeaderValuesByRepledgeBillId(String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, "
                + "RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, "
                + "CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, "
                + "CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, "
                + "RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND CB.REPLEDGE_BILL_ID IS NOT NULL " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.REPLEDGE_BILL_ID = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeBillId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                
                return headerValues;		       
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

        return null;
    }

    public HashMap<String, String> getAllClosedHeaderValuesByRepledgeBillId(String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.GIVEN_AMOUNT, RB.closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.STATUS IN ('CLOSED', 'RECEIVED') " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND RB.REPLEDGE_BILL_ID = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeBillId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                headerValues.put("GIVEN_AMOUNT", Double.toString(rs.getDouble(32)));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(33).toLocalDate()));
                
                return headerValues;		       
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

        return null;
    }
    
    public HashMap<String, String> getAllHeaderValuesByRepledgeBillNumber(String sRepledgeId, String sRepledgeBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "CB.BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN') " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND RB.REPLEDGE_ID = ? " +
                    "AND RB.REPLEDGE_BILL_NUMBER = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeId);
            stmt.setString(4, sRepledgeBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(32).toLocalDate()));
                
                return headerValues;		       
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

        return null;
    }

    public HashMap<String, String> getAllClosedHeaderValuesByRepledgeBillNumber(String sRepledgeId, String sRepledgeBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "CB.BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.GIVEN_AMOUNT, RB.closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.STATUS IN ('CLOSED', 'RECEIVED') " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND RB.REPLEDGE_ID = ? " +
                    "AND RB.REPLEDGE_BILL_NUMBER = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeId);
            stmt.setString(4, sRepledgeBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                headerValues.put("GIVEN_AMOUNT", Double.toString(rs.getDouble(32)));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(33).toLocalDate()));
                
                return headerValues;		       
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

        return null;
    }
    
    public HashMap<String, String> getAllHeaderValuesByCompanyBillNumber(String sCompanyBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, "
                + "RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, "
                + "CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, "
                + "CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, "
                + "RB.STATUS, RB.NOTE, RB.closing_date, CB.rebilled_to, RB.suspense_date  " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND CB.REPLEDGE_BILL_ID IS NOT NULL " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " + 
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.REPLEDGE_BILL_ID = "
                + "(SELECT REPLEDGE_BILL_ID "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND BILL_NUMBER = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE)";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, sCompanyBillNumber);
            stmt.setString(5, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));    
                headerValues.put("REBILLED_TO", rs.getString(33));
                if(rs.getDate(34) != null) { 
                    headerValues.put("SUSPENSE_DATE", format.format(rs.getDate(34).toLocalDate()));
                }
                
                return headerValues;		       
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

        return null;
    }

    public DataTable getRebilledRepValues(String sRebillId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT RB.company_bill_number, "
                  + "RB.AMOUNT REP_AMT, "
                  + "CB.bill_number, "
                  + "CB.amount CMP_AMT, " +
                    "RB.opening_date REP_DATE, " +
                    "CB.TOTAL_ADVANCE_AMOUNT_PAID,  " +
                    "RB.REPLEDGE_NAME, "
                  + "RB.repledge_bill_number, "
                  + "(CB.AMOUNT/CB.gross_weight) RATE_PER_GM, "
                  + "CB.gross_weight, "
                  + "CB.ITEMS, "
                  + "RB.REPLEDGE_BILL_ID, "
                  + "EXTRACT(DAY FROM RB.opening_date) REP_DAY " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB  " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID  " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID  " +
                    "AND CB.repledge_bill_id IS NOT NULL  " +
                    "AND RB.COMPANY_ID = ?  " +
                    "AND CB.STATUS IN ('OPENED', 'LOCKED') " +
                    "AND CB.bill_number NOT IN (RB.company_bill_number) "
                + "AND CB.REPLEDGE_BILL_ID = ? ";
        
        sql += "order by REP_DAY, RB.REPLEDGE_NAME ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRebillId);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(Double.toString(rs.getDouble(2)));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));                
                row.addColumn(Double.toString(rs.getDouble(6)));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(Double.toString(rs.getDouble(9)));
                row.addColumn(Double.toString(rs.getDouble(10)));
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
    
    public HashMap<String, String> getAllClosedHeaderValuesByCompanyBillNumber(String sCompanyBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, " +
                "RB.OPENING_DATE, RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, "
                + "CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, "
                + "CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, "
                + "RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.GIVEN_AMOUNT, RB.CLOSING_DATE " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.STATUS IN ('CLOSED', 'RECEIVED') " +                    
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.BILL_NUMBER = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sCompanyBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                headerValues.put("GIVEN_AMOUNT", Double.toString(rs.getDouble(32)));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(33).toLocalDate()));
                return headerValues;		       
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

        return null;
    }

    public HashMap<String, String> getAllClosedHeaderValuesByCompanyReBillNumber(String sCompanyBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.STATUS IN ('CLOSED', 'RECEIVED') " +                    
                    "AND RB.COMPANY_BILL_NUMBER = CB.REBILLED_FROM " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.BILL_NUMBER = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sCompanyBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(32).toLocalDate()));
                
                return headerValues;		       
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

        return null;
    }
    
    public String getInterestType(String sRepledgeId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT DAY_OR_MONTHLY_INTEREST FROM REPLEDGE WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sRepledgeId);
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
            roleMasterConn.close();
        }        
        return null;
    }
    
    public String[] getReduceOrMinimumDaysOrMonths(String sRepledgeId, String sMaterialType, String sType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[2];
        
        String sql ="SELECT DAYS_OR_MONTHS, REDUCTION_TYPE "
                + "FROM REPLEDGE_REDUCE_MONTHS_OR_DAYS "
                + "WHERE REPLEDGE_ID = ? " 
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sRepledgeId);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sType);
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
            roleMasterConn.close();
        }        
        return data;
    }
    
    public double getRemainingDaysAsMonths(String sDate, String sRepledgeId, double iRemainingDays, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT AS_MONTH " +
                    "FROM REPLEDGE_MONTH_SETTING " +
                    "WHERE REPLEDGE_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN DAYS_FROM AND DAYS_TO "
                + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));            
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sRepledgeId);            
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
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return 0;
    }
    
    public String getFormula(String sRepledgeId, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
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
            roleMasterConn.close();
        }        
        return "";
    }
    
    public boolean closeBill(String sRepledgeBillId, String sInterestType, String sBillCosingDate, 
                            String sTotalDaysOrMonths, int iMinimumDaysOrMonths, int iReduceDaysOrMonths, 
                            double dTakenDaysOrMonths, double dCloseTakenAmount, double dToGiveAmount, 
                            double dGivenAmount, String sStatus, String sNote, String sReduceType, String sMinimumType, 
                            String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        java.sql.Date sqlDateBillClosing = java.sql.Date.valueOf(LocalDate.parse(sBillCosingDate, CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE REPLEDGE_BILLING SET "
                    + "INTEREST_TYPE = ?::INTEREST_TYPE, "
                    + "CLOSING_DATE = ?, "
                    + "TOTAL_DAYS_OR_MONTHS = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS = ?, "
                    + "REDUCE_DAYS_OR_MONTHS = ?, "
                    + "TAKEN_DAYS_OR_MONTHS = ?, "
                    + "CLOSE_TAKEN_AMOUNT = ?, "
                    + "TOGIVE_AMOUNT = ?, "
                    + "GIVEN_AMOUNT = ?, "
                    + "STATUS = ?::REPLEDGE_BILL_STATUS, "
                    + "NOTE = ?, "
                    + "REDUCE_DAYS_OR_MONTHS_TYPE = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS_TYPE = ?, "
                    + "CLOSED_USER_ID = ?, "
                    + "CLOSED_DATE = now() "
                    + "WHERE REPLEDGE_BILL_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sInterestType);
            stmt.setDate(2, sqlDateBillClosing);
            stmt.setString(3, sTotalDaysOrMonths);
            stmt.setInt(4, iMinimumDaysOrMonths);
            stmt.setInt(5, iReduceDaysOrMonths);
            stmt.setDouble(6, dTakenDaysOrMonths);
            stmt.setDouble(7, dCloseTakenAmount);
            stmt.setDouble(8, dToGiveAmount);
            stmt.setDouble(9, dGivenAmount);
            stmt.setString(10, sStatus);
            stmt.setString(11, sNote);
            stmt.setString(12, sReduceType);
            stmt.setString(13, sMinimumType);
            stmt.setString(14, CommonConstants.USERID);
            stmt.setString(15, sRepledgeBillId);
            stmt.setString(16, sMaterialType); 

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateBill(String sRepledgeBillId, String sInterestType, String sBillCosingDate, 
                            String sTotalDaysOrMonths, int iMinimumDaysOrMonths, int iReduceDaysOrMonths, 
                            double dTakenDaysOrMonths, double dCloseTakenAmount, double dToGiveAmount, 
                            double dGivenAmount, String sStatus, String sNote, String sReduceType, String sMinimumType, 
                            String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        java.sql.Date sqlDateBillClosing = java.sql.Date.valueOf(LocalDate.parse(sBillCosingDate, CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE REPLEDGE_BILLING SET "
                    + "INTEREST_TYPE = ?::INTEREST_TYPE, "
                    + "CLOSING_DATE = ?, "
                    + "TOTAL_DAYS_OR_MONTHS = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS = ?, "
                    + "REDUCE_DAYS_OR_MONTHS = ?, "
                    + "TAKEN_DAYS_OR_MONTHS = ?, "
                    + "CLOSE_TAKEN_AMOUNT = ?, "
                    + "TOGIVE_AMOUNT = ?, "
                    + "GIVEN_AMOUNT = ?, "
                    + "STATUS = ?::REPLEDGE_BILL_STATUS, "
                    + "NOTE = ?, "
                    + "REDUCE_DAYS_OR_MONTHS_TYPE = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS_TYPE = ?, "
                    + "CLOSED_USER_ID = ?,"
                    + "CLOSED_DATE = now() "
                    + "WHERE REPLEDGE_BILL_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('CLOSED', 'RECEIVED') ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sInterestType);
            stmt.setDate(2, sqlDateBillClosing);
            stmt.setString(3, sTotalDaysOrMonths);
            stmt.setInt(4, iMinimumDaysOrMonths);
            stmt.setInt(5, iReduceDaysOrMonths);
            stmt.setDouble(6, dTakenDaysOrMonths);
            stmt.setDouble(7, dCloseTakenAmount);
            stmt.setDouble(8, dToGiveAmount);
            stmt.setDouble(9, dGivenAmount);
            stmt.setString(10, sStatus);
            stmt.setString(11, sNote);
            stmt.setString(12, sReduceType);
            stmt.setString(13, sMinimumType);
            stmt.setString(14, CommonConstants.USERID);
            stmt.setString(15, sRepledgeBillId);
            stmt.setString(16, sMaterialType); 

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateSuspense(String sRepledgeBillId, String sBillCosingDate, String sStatus, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        java.sql.Date sqlDateBillClosing = java.sql.Date.valueOf(LocalDate.parse(sBillCosingDate, 
                CommonConstants.DATETIMEFORMATTER));        
        String sql = "UPDATE REPLEDGE_BILLING SET "                    
                    + "STATUS = ?::REPLEDGE_BILL_STATUS, "
                    + "suspense_date = ?, "
                    + "CLOSED_USER_ID = ?,"
                    + "CLOSED_DATE = now() "
                    + "WHERE REPLEDGE_BILL_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('OPENED', 'GIVEN') ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sStatus);
            stmt.setDate(2, sqlDateBillClosing);
            stmt.setString(3, CommonConstants.USERID);
            stmt.setString(4, sRepledgeBillId);
            stmt.setString(5, sMaterialType); 

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateCompanyBillToEmpty(String sRepledgeBillId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_BILLING SET "
                + "REPLEDGE_BILL_ID = ? "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REPLEDGE_BILL_ID = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, null);           
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);
            stmt.setString(4, sRepledgeBillId);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateCompanyBillToNewRepBillId(String sCompanyBillNumber, 
            String sRepledgeBillIds, String sMaterialType, String sParentRepId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_BILLING SET "
                + "REPLEDGE_BILL_ID = ? "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "and repledge_bill_id like '%" + sParentRepId + "%' "
                + "AND repledge_bill_id like '%,%'";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sRepledgeBillIds);           
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);
            //stmt.setString(4, sCompanyBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateCompanyBillStatusToOpened(String sRepledgeBillId, String sCompanyBillStatus, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        /*String sql = "UPDATE COMPANY_BILLING SET "
                + "STATUS = ?::COMPANY_BILL_STATUS "                
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REPLEDGE_BILL_ID = ? ";
        */
        String sql = "UPDATE COMPANY_BILLING SET "
                + "STATUS = ?::COMPANY_BILL_STATUS "                
                + "WHERE company_id = ? "
                + "AND bill_number = (SELECT bill_number " +
                "FROM public.company_billing " +
                "where company_id = ? " +
                "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "and repledge_bill_id = ? " +
                "and status in ('OPENED', 'LOCKED') " +                 
                "order by opening_date DESC " +
                "LIMIT 1)";

        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sCompanyBillStatus);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, sMaterialType);
            stmt.setString(5, sRepledgeBillId);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateCompanyBillPhysicalLocation(String sRepledgeBillId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_BILLING SET "
                + "physical_location = ? "                
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REPLEDGE_BILL_ID = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.REPLEDGE_DRAWER);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);
            stmt.setString(4, sRepledgeBillId);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, " +
                    "RB.CLOSING_DATE, RB.AMOUNT, " +
                    "RB.STATUS, CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, CB.STATUS, " +
                    " REGEXP_REPLACE(COALESCE(RB.REPLEDGE_BILL_ID, '0'), '[^0-9]*' ,'0')::integer BILL, RB.CLOSING_DATE " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.STATUS IN ('CLOSED', 'RECEIVED') " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
                    //+ "ORDER BY RB.CLOSING_DATE DESC, RB.REPLEDGE_NAME";

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            
            sql = sql + " ORDER BY RB.CLOSING_DATE DESC";
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);

            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("GENDER =")) {
                        stmt.setString(i+3, sVals[i]);
                    } else {
                        stmt.setString(i+3, "%"+sVals[i]+"%");
                    }
                }
            }
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(5)));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(format.format(rs.getDate(8).toLocalDate()));                
                row.addColumn(Double.toString(rs.getDouble(9)));
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
        
    public DataTable getOtherSettingsValues(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ALLOW_TO_CHANGE_REPLEDGE_BILL_CLOSING_DATE, ALLOW_TO_CHANGE_REPLEDGE_BILL_CLOSING_AMOUNT "
                + "FROM COMPANY_OTHER_SETTINGS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getBoolean(1));
                row.addColumn(rs.getBoolean(2));
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
    
    public DataTable getCreditTableValues(String sReBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, CREDITED_DATE, TO_BE_CREDITED_AMOUNT, CREDITED_AMOUNT "
                + "FROM REPLEDGE_BILL_CREDIT "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REPLEDGE_BILL_NUMBER = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType); 
            stmt.setString(3, sReBillNumber);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(format.format(rs.getDate(3).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
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

    public DataTable getDebitTableValues(String sReBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, DEBITTED_DATE, TO_BE_DEBITTED_AMOUNT, DEBITTED_AMOUNT "
                + "FROM REPLEDGE_BILL_DEBIT "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REPLEDGE_BILL_NUMBER = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType); 
            stmt.setString(3, sReBillNumber);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(format.format(rs.getDate(3).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
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
    
    public boolean deleteRepledgeBillDebitTable(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "DELETE FROM repledge_bill_debit " +
                    "WHERE jewel_material_type = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ?";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sMaterialType); 
            stmt.setString(2, sBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean deleteRepledgeBillCreditTable(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "DELETE FROM repledge_bill_credit " +
                    "WHERE jewel_material_type = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ?";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sMaterialType); 
            stmt.setString(2, sBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean deleteRepledgeBillingTable(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "DELETE FROM repledge_billing " +
                    "WHERE jewel_material_type = ?::MATERIAL_TYPE "
                + "AND company_bill_number = ?";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sMaterialType); 
            stmt.setString(2, sBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }    
    
    public String[] getGoldCurrentBillNumber() throws SQLException
    {

        String[] sBillNumber = new String[5];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT GOLD_CUR_BILL_ROW_NUMBER, " +
                    "GOLD_CUR_BILL_PREFIX, COALESCE(GOLD_CUR_BILL_NUMBER,'0'), AUTO_BILL_GENERATION, TYPE " +
                    "FROM COMPANY " +
                    "WHERE id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                sBillNumber[0] = rs.getString(1);
                sBillNumber[1] = rs.getString(2);
                sBillNumber[2] = rs.getString(3);
                sBillNumber[3] = Boolean.toString(rs.getBoolean(4));
                sBillNumber[4] = rs.getString(5);
                return sBillNumber;		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return null;
    }
 
    public String[] getSilverCurrentBillNumber() throws SQLException
    {

        String[] sBillNumber = new String[4];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT SILVER_CUR_BILL_ROW_NUMBER, " +
                    "SILVER_CUR_BILL_PREFIX, COALESCE(SILVER_CUR_BILL_NUMBER,'0'), AUTO_BILL_GENERATION " +
                    "FROM COMPANY " +
                    "WHERE id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                sBillNumber[0] = rs.getString(1);
                sBillNumber[1] = rs.getString(2);
                sBillNumber[2] = rs.getString(3);
                sBillNumber[3] = Boolean.toString(rs.getBoolean(4));
                return sBillNumber;		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return null;
    }
    
    public String getRepIds(String sCompBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT REPLEDGE_BILL_ID FROM COMPANY_BILLING WHERE BILL_NUMBER = ? and COMPANY_ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sCompBillNumber);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
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
            roleMasterConn.close();
        }        
        return null;
    }
    
    public DataTable getRNYDeliveredTableValue(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY RB.REPLEDGE_NAME) AS SLNO,  " +
                        "RB.REPLEDGE_BILL_NUMBER, RB.REPLEDGE_NAME, RB.OPENING_DATE, RB.AMOUNT, " +
                        "CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, " +
                        "RB.STATUS, RB.REPLEDGE_BILL_ID " +
                        "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                        "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
                        "AND CB.REPLEDGE_BILL_ID = RB.REPLEDGE_BILL_ID " +
                        "AND CB.COMPANY_ID = RB.COMPANY_ID " +
                        "AND CB.REPLEDGE_BILL_ID IS NOT NULL  " +
                        "AND RB.STATUS NOT IN ('RECEIVED', 'SUSPENSE') " +
                        "AND CB.STATUS IN ('CLOSED', 'REBILLED-REMOVED') " +
                        "AND CB.COMPANY_ID = ? " +
                        "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getString(6));
                row.addColumn(formatter.format(rs.getDate(7)));
                row.addColumn(rs.getDouble(8));
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
    
    public DataTable getRNYSuspenseTableValue(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY RB.REPLEDGE_NAME) AS SLNO,  " +
                "RB.REPLEDGE_BILL_NUMBER, RB.REPLEDGE_NAME, RB.OPENING_DATE, RB.AMOUNT, " +
                "CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, " +
                "RB.STATUS, RB.REPLEDGE_BILL_ID, RB.suspense_date " +
                "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
                "AND CB.REPLEDGE_BILL_ID = RB.REPLEDGE_BILL_ID " +
                "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                "AND CB.COMPANY_ID = RB.COMPANY_ID " +
                "AND CB.REPLEDGE_BILL_ID IS NOT NULL  " +
                "AND RB.STATUS = 'SUSPENSE' " +
                "AND CB.COMPANY_ID = ? " +
                "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getString(6));
                row.addColumn(formatter.format(rs.getDate(7)));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
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
        
    public boolean isRepBillIdStausOpenedOrGiven(String sRepBillId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        String sql = "SELECT REPLEDGE_BILL_ID "
                + "FROM REPLEDGE_billing "
                + "WHERE REPLEDGE_BILL_ID = ? "
                + "and STATUS in ('OPENED', 'GIVEN')";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sRepBillId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return rs.getString(1)!=null;
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
    
    
    public String[] getLastBillNumberAndStatus(String sRepledgeBillId) throws SQLException
    {

        String[] bill = new String[4];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT bill_number, opening_date, "
                + "status, repledge_bill_id " +
                    "FROM public.company_billing " +
                    "where company_id = ? " + 
                    "and repledge_bill_id like '%"+sRepledgeBillId+"%' " +
                    "order by opening_date DESC " +
                    "LIMIT 1";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                bill[0] = rs.getString(1);
                bill[1] = rs.getString(2);
                bill[2] = rs.getString(3);
                bill[3] = rs.getString(4);
                return bill;		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return null;
    }
    
    public String getRepBillClosingDate(String sRepBillId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT closing_date "
                + "FROM repledge_billing "
                + "WHERE COMPANY_ID = ? "
                + "AND repledge_bill_id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepBillId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return format.format(rs.getDate(1).toLocalDate());
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return null;
    }

    public boolean saveBillCalcRepHeader(String sId, String sClosingDate, 
            double openCapital, double openInterest, double openTotal,
            double closeCapital, double closeInterest, double closeTotal,
            String giveOrGet, double totalValue) throws Exception
    {	
        connectDB();
        PreparedStatement stmt = null;
        
        java.sql.Date sqlDateRepBillClosing = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                CommonConstants.DATETIMEFORMATTER));
        
        String sql = "INSERT INTO public.repledge_bill_calc_header(" +
"	planner_id, open_capital, open_interest, open_interested, close_capital, close_interest, close_interested, "
                + "give_or_get_text, total_value, created_user_id, created_date, closing_date, "
                + "last_updated_user_id, last_updated_date, note, is_printed, COMPANY_ID)" +
"	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, ?, now(), ?, ?, ?)";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sId);
            stmt.setDouble(2, openCapital);
            stmt.setDouble(3, openInterest);
            stmt.setDouble(4, openTotal);
            stmt.setDouble(5, closeCapital);
            stmt.setDouble(6, closeInterest);
            stmt.setDouble(7, closeTotal);
            stmt.setString(8, giveOrGet);
            stmt.setDouble(9, totalValue);
            stmt.setString(10, CommonConstants.USERID);
            stmt.setDate(11, sqlDateRepBillClosing);
            stmt.setString(12, CommonConstants.USERID);
            stmt.setString(13, "");
            stmt.setBoolean(14, Boolean.FALSE);
            stmt.setString(15, CommonConstants.ACTIVE_COMPANY_ID);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateBillCalcRepHeader(String sId, String sClosingDate, 
            double openCapital, double openInterest, double openTotal,
            double closeCapital, double closeInterest, double closeTotal,
            String giveOrGet, double totalValue) throws Exception
    {	
        connectDB();
        PreparedStatement stmt = null;
        
        java.sql.Date sqlDateRepBillClosing = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE repledge_bill_calc_header "
                + "SET "
                + "open_capital = ?,"
                + "open_interest = ?,"
                + "open_interested = ?,"
                + "close_capital = ?,"
                + "close_interest = ?,"
                + "close_interested = ?,"
                + "give_or_get_text = ?,"
                + "total_value = ?,"
                + "closing_date = ?,"
                + "last_updated_user_id = ?,"
                + "last_updated_date = now(),"
                + "note = ?,"
                + "COMPANY_ID = ?  "
                + "WHERE planner_id = ?";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);                 
            stmt.setDouble(1, openCapital);
            stmt.setDouble(2, openInterest);
            stmt.setDouble(3, openTotal);
            stmt.setDouble(4, closeCapital);
            stmt.setDouble(5, closeInterest);
            stmt.setDouble(6, closeTotal);
            stmt.setString(7, giveOrGet);
            stmt.setDouble(8, totalValue);
            stmt.setDate(9, sqlDateRepBillClosing);
            stmt.setString(10, CommonConstants.USERID);
            stmt.setString(11, "");
            stmt.setString(12, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(13, sId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean saveBillCalcRep(String sId, String sClosingDate, RepAllDetailsBean bean) throws Exception
    {	
        connectDB();
        PreparedStatement stmt = null;
        
        java.sql.Date sqlDateRepBillOpening = java.sql.Date.valueOf(LocalDate.parse(bean.getSOpeningDate(), 
                CommonConstants.DATETIMEFORMATTER));
        java.sql.Date sqlDateRepBillClosing = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                CommonConstants.DATETIMEFORMATTER));
        
        String sql = "INSERT INTO public.repledge_bill_calc( " +
                    "planner_id, company_id, before_after, jewel_material_type, repledge_bill_id, operation, "
                + "company_bill_number, repledge_opening_date, repledge_bill_number, repledge_id, repledge_name, "
                + "items, amount, interest, interested_amount, total_interested_amount, created_user_id, created_date, "
                + "closing_date, status, note, company_actual_bill_number) " +
                "VALUES (?, ?, ?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), "
                + "?, ?::repledge_bill_status, ?, ?)";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, bean.getSBOrA());
            stmt.setString(4, bean.getSMaterial());
            stmt.setString(5, bean.getSRepBillId());
            stmt.setString(6, bean.getSOperation());
            stmt.setString(7, bean.getSBillNumber());
            stmt.setDate(8, sqlDateRepBillOpening);
            stmt.setString(9, bean.getSRepBillNumber());
            stmt.setString(10, bean.getSRepId());
            stmt.setString(11, bean.getSRepName());
            stmt.setString(12, bean.getSItems());
            stmt.setDouble(13, Double.parseDouble(bean.getSAmount()));
            stmt.setDouble(14, Double.parseDouble(bean.getSInterest()));
            stmt.setDouble(15, Double.parseDouble(bean.getSInterestedAmt()));
            stmt.setDouble(16, Double.parseDouble(bean.getSTotalInterestedAmt()));
            stmt.setString(17, CommonConstants.USERID);            
            stmt.setDate(18, sqlDateRepBillClosing);
            stmt.setString(19, !bean.getSStatus().isEmpty() ? bean.getSStatus() : null);   
            stmt.setString(20, "");   
            stmt.setString(21, bean.getSActualBillNumber());   
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean deleteBillCalcRep(String sId, String sClosingDate) throws Exception
    {	
        connectDB();
        PreparedStatement stmt = null;
        
        java.sql.Date sqlDateRepBillClosing = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                CommonConstants.DATETIMEFORMATTER));
        
        String sql = "DELETE FROM repledge_bill_calc " +
                        "WHERE planner_id = ? " +
                        "AND closing_date = ? ";
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sId);
            stmt.setDate(2, sqlDateRepBillClosing);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean setRepPlannerNextCustomerId(String sNextId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR "
                + "SET OPERATION_NEXT_ID = ?  "
                + "WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, sNextId);
            stmt.setString(2, "REP_BILL_CALC");
            stmt.setString(3, "PLAN_ID");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean setRepHeaderAsPrinted(String plannerId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE repledge_bill_calc_header "
                + "SET is_printed = ?  "
                + "WHERE planner_id = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setBoolean(1, Boolean.TRUE);
            stmt.setString(2, plannerId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
}
