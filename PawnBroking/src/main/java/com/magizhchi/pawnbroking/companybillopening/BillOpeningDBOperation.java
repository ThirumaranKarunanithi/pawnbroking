/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillopening;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Tiru
 */
public class BillOpeningDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    private File tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
    private File custTemp = new File(tempFile, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
    private File jewelTemp = new File(tempFile, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
    private File userTemp = new File(tempFile, CommonConstants.OPEN_USER_IMAGE_NAME);
    
    public BillOpeningDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DataTable getAllActiveJewelItems(String sMaterialType, String sItemName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ITEM "
                + "FROM JEWEL_ITEMS "
                + "WHERE JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS = ?::REPLEDGE_STATUS ";

        if(sItemName != null) {
            sql += "AND ITEM LIKE '%"+sItemName+"%' ORDER BY ITEM";
        }
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sMaterialType);
            stmt.setString(2, "ACTIVE");
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
        
        String sql = "SELECT DISTINCT CONCAT('\t CUST-ID: ', customer_id, ' - ', customer_name, ' ', spouse_type, ' ',  spouse_name, "
                + "' \n\t ', door_number, ', ', street, ' ',  area,"
                + "' \n\t ', city, '. MOB: ', mobile_number, ' / ', mobile_number_2), "
                + "customer_name, gender, spouse_type, "
                + "spouse_name, door_number, street, area, city, mobile_number, COALESCE(customer_status,''),"
                + "COALESCE(mobile_number_2,''), COALESCE(cust_id_proof_type,''), "
                + "COALESCE(cust_id_proof_number,''), COALESCE(refered_by_name,''), COALESCE(customer_id,'') "
                + ", COALESCE(customer_occupation,'') "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ") ";

        if(sCustomerName != null) {
            sql += "AND customer_name LIKE '%"+sCustomerName+"%' ORDER BY customer_name, spouse_name";
        }
        try {
            stmt = roleMasterConn.prepareStatement(sql);  
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
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                row.addColumn(rs.getString(17));
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

    public DataTable getFilteredCustomerNamesByMobileNumber(String sMobileNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT CONCAT(customer_name, ' ', spouse_type, ' ',  spouse_name, "
                + "' \n\t ', door_number, ', ', street, ' ',  area), "
                + "customer_name, gender, spouse_type, "
                + "spouse_name, door_number, street, area, city, mobile_number, COALESCE(customer_status,'') "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? ";

        if(sMobileNumber != null) {
            sql += "AND mobile_number LIKE '%"+sMobileNumber+"%' ORDER BY customer_name, spouse_name";
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
    
    public DataTable getFilteredStreetNames(String sStreetName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT CONCAT(street, ' - ', area), street, area, city "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? ";

        if(sStreetName != null) {
            sql += "AND street LIKE '%"+sStreetName+"%' ORDER BY street";
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
    
    public DataTable getAllCustomerNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CONCAT(customer_name, ' - ',  area), customer_name, gender, spouse_type, "
                + "spouse_name, door_number, street, area, city, mobile_number, COALESCE(customer_status,'')"
                + "FROM customer_details "
                + "WHERE COMPANY_ID = ? ";

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
    
    public DataTable getAllJewelItems(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ITEM "
                + "FROM JEWEL_ITEMS "
                + "WHERE JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sMaterialType);
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
            roleMasterConn.close();
        } 

        return dataTable;	
    }
    
    public String[] getGoldCurrentBillNumber() throws SQLException
    {

        String[] sBillNumber = new String[6];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT GOLD_CUR_BILL_ROW_NUMBER, " +
                    "GOLD_CUR_BILL_PREFIX, "
                + "COALESCE(GOLD_CUR_BILL_NUMBER,'0'), "
                + "AUTO_BILL_GENERATION, TYPE, entry_mode " +
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
                sBillNumber[5] = Boolean.toString(rs.getBoolean(6));
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
    
    public String getInterest(String sDate, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT INTEREST " +
                    "FROM COMPANY_INTEREST " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO "
                    + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {
            
            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, dAmount);
            stmt.setDate(4, sqlDateOpenDate);
            
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
        return "0";
    }

    public String getDocumentCharge(String sDate, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT DOCUMENT_CHARGE " +
                    "FROM COMPANY_DOCUMENT_CHARGE " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO "
                    + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {
            
            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, dAmount);
            stmt.setDate(4, sqlDateOpenDate);
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
        return "0";
    }

    public String getFormula(String sDate, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
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
            stmt.setString(3, "OPEN");
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
            roleMasterConn.close();
        }        
        return "0";
    }
    
    public boolean isvalidBillNumberToSave(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT BILL_NUMBER "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return false;		       
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

        return true;
    }

    public boolean isAlreadyExistingCustomer(String sName, String sSpouseType, String sSpouseName,
            String sDoorNo, String sStreetName, String sArea, String sCity) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT DISTINCT customer_name, gender, spouse_type,  " +
                "spouse_name, door_number, street, area, city " +
                "FROM COMPANY_BILLING " +
                "where COMPANY_ID = ? " +
                "and CUSTOMER_NAME = ? " +
                "AND spouse_type = ? " +
                "AND SPOUSE_NAME = ? " +
                "AND door_number = ? " +
                "AND street = ? " +
                "AND area = ? " +
                "AND city = ? " +
                "GROUP BY customer_name, gender, spouse_type,  " +
                "spouse_name, door_number, street, area, city " +
                "ORDER BY customer_name";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sName);
            stmt.setString(3, sSpouseType);
            stmt.setString(4, sSpouseName);
            stmt.setString(5, sDoorNo);            
            stmt.setString(6, sStreetName);
            stmt.setString(7, sArea);
            stmt.setString(8, sCity);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return true;		       
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
    
    public boolean saveRecord(String sRepledgeBillId, String sRebilledFrom, String sBillNumber, String sBillOpeningDate, String sName, 
                                String sGender, 
                                String sSpouseType, String sSpouseName, String sDoorNo, 
                                String sStreetName, String sArea, String sCity, 
                                String sMobileNumber, String sItems, 
                                double dGrossWeight, double dNetWeight, double dPurity, 
                                String sStatus, String sNote, double dAmount, 
                                double dInterest, double dDocumentCharge, double dTakenAmount, 
                                double dToGiveAmount, double dGivenAmount, String sMaterialType, 
                                String sAcceptedDate, String sNomineeName,
                                String mobileNumber2, String idProof, String idNumber, 
                                String recommendedBy, String customerId, String sOccupation,
                                String physicalLocation) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        boolean val = false;
        
        String sql = "INSERT INTO COMPANY_BILLING(COMPANY_ID, JEWEL_MATERIAL_TYPE, BILL_NUMBER, "
                + "OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, DOOR_NUMBER, STREET, "
                + "AREA, CITY, MOBILE_NUMBER, ITEMS, GROSS_WEIGHT, "
                + "NET_WEIGHT, PURITY, AMOUNT, INTEREST, DOCUMENT_CHARGE, "
                + "OPEN_TAKEN_AMOUNT, TOGIVE_AMOUNT, GIVEN_AMOUNT, STATUS, NOTE, "
                + "CREATED_USER_ID, created_date, REPLEDGE_BILL_ID, REBILLED_FROM, "
                + "accepted_closing_date, NOMINEE_NAME, remind_status, "
                + "mobile_number_2, cust_id_proof_type, cust_id_proof_number, "
                + "refered_by_name, customer_id, customer_occupation, physical_location) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?, ?::GENDER_TYPE, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, "
                + "?, ?::COMPANY_BILL_STATUS, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sBillOpeningDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlAccDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sAcceptedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            stmt.setDate(4, sqlDateOpenDate);
            stmt.setString(5, sName);
            stmt.setString(6, sGender);
            stmt.setString(7, sSpouseType);
            stmt.setString(8, sSpouseName);
            stmt.setString(9, sDoorNo);
            stmt.setString(10, sStreetName);
            stmt.setString(11, sArea);
            stmt.setString(12, sCity);
            stmt.setString(13, sMobileNumber);
            stmt.setString(14, sItems);
            stmt.setDouble(15, dGrossWeight);
            stmt.setDouble(16, dNetWeight);
            stmt.setDouble(17, dPurity);
            stmt.setDouble(18, dAmount);
            stmt.setDouble(19, dInterest);
            stmt.setDouble(20, dDocumentCharge);
            stmt.setDouble(21, dTakenAmount);
            stmt.setDouble(22, dToGiveAmount);
            stmt.setDouble(23, dGivenAmount);            
            stmt.setString(24, sStatus);
            stmt.setString(25, sNote);
            stmt.setString(26, CommonConstants.USERID);
            stmt.setString(27, sRepledgeBillId);
            stmt.setString(28, sRebilledFrom);
            stmt.setDate(29, sqlAccDateOpenDate);
            stmt.setString(30, sNomineeName);
            stmt.setString(31, CommonConstants.ACTIVE);      
            stmt.setString(32, mobileNumber2);
            stmt.setString(33, idProof);
            stmt.setString(34, idNumber);
            stmt.setString(35, recommendedBy);
            stmt.setString(36, customerId);
            stmt.setString(37, sOccupation);
            stmt.setString(38, physicalLocation);
            
            val = stmt.executeUpdate() >= 1;
            return val;
        } catch (SQLException e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean isValidNextBillNumber(String sCurRowNumber, int sNextNumber, String sMaterialType) throws SQLException
    {
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT ROW_NUMBER "
                + "FROM COMPANY_BILL_NUMBER_GENERATOR "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND ROW_NUMBER = ? "
                + "AND ? BETWEEN NUMBER_FROM AND NUMBER_TO";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sCurRowNumber);
            stmt.setInt(4, sNextNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return true;
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return false;
    }
    
    public boolean updateGoldPreNumberDetail(String preRowNumber, String preNumber, String prePrefix, 
            String curRowNumber, String curNumber, String curPrefix ) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;

        String sql = "UPDATE COMPANY SET "
                + "GOLD_CUR_BILL_ROW_NUMBER = ?, "
                + "GOLD_CUR_BILL_PREFIX = ?, "
                + "GOLD_CUR_BILL_NUMBER = ?,"
                + "GOLD_PRE_BILL_ROW_NUMBER = ?, "
                + "GOLD_PRE_BILL_PREFIX = ?, "
                + "GOLD_PRE_BILL_NUMBER = ? "
                + "WHERE ID = ? ";
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);      
            stmt.setInt(1, Integer.parseInt(curRowNumber));
            stmt.setString(2, curPrefix);
            stmt.setString(3, curNumber);
            stmt.setInt(4, Integer.parseInt(preRowNumber));
            stmt.setString(5, prePrefix);
            stmt.setString(6, preNumber);
            stmt.setString(7, CommonConstants.ACTIVE_COMPANY_ID);

            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateSilverPreNumberDetail(String preRowNumber, String preNumber, String prePrefix, 
            String curRowNumber, String curNumber, String curPrefix ) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;

        String sql = "UPDATE COMPANY SET "
                + "SILVER_CUR_BILL_ROW_NUMBER = ?, "
                + "SILVER_CUR_BILL_PREFIX = ?, "
                + "SILVER_CUR_BILL_NUMBER = ?,"
                + "SILVER_PRE_BILL_ROW_NUMBER = ?, "
                + "SILVER_PRE_BILL_PREFIX = ?, "
                + "SILVER_PRE_BILL_NUMBER = ? "
                + "WHERE ID = ? ";
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);      
            stmt.setInt(1, Integer.parseInt(curRowNumber));
            stmt.setString(2, curPrefix);
            stmt.setString(3, curNumber);
            stmt.setInt(4, Integer.parseInt(preRowNumber));
            stmt.setString(5, prePrefix);
            stmt.setString(6, preNumber);
            stmt.setString(7, CommonConstants.ACTIVE_COMPANY_ID);

            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public String[] getNextNumber(String sRowNumber, String sMaterialType) throws SQLException
    {

        String[] sBillNumber = new String[2];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT PREFIX, NUMBER_FROM "
                + "FROM COMPANY_BILL_NUMBER_GENERATOR "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND ROW_NUMBER = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRowNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                sBillNumber[0] = rs.getString(1);	
                sBillNumber[1] = Integer.toString(rs.getInt(2));	
            }		    

            return sBillNumber;

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
    }
    
    public HashMap<String, String> getAllHeaderValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, GROSS_WEIGHT, NET_WEIGHT, PURITY, "
                + "AMOUNT, INTEREST, DOCUMENT_CHARGE, OPEN_TAKEN_AMOUNT, TOGIVE_AMOUNT, GIVEN_AMOUNT, "
                + "STATUS, NOTE, REPLEDGE_BILL_ID, accepted_closing_date, REBILLED_TO, REBILLED_FROM, NOMINEE_NAME, "
                + "COALESCE(customer_status,''), CREATED_USER_ID, to_char(created_date, 'dd-MM-YY / HH24:MI:ss'), "
                + "mobile_number_2, cust_id_proof_type, cust_id_proof_number, refered_by_name, "
                + "customer_id, customer_occupation, physical_location "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED', 'CANCELED') "
                + "AND BILL_NUMBER = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("BILL_NUMBER", sBillNumber);
                headerValues.put("OPENING_DATE", format.format(rs.getDate(1).toLocalDate()));
                headerValues.put("CUSTOMER_NAME", rs.getString(2));
                headerValues.put("GENDER", rs.getString(3));
                headerValues.put("SPOUSE_TYPE", rs.getString(4));
                headerValues.put("SPOUSE_NAME", rs.getString(5));
                headerValues.put("DOOR_NUMBER", rs.getString(6));
                headerValues.put("STREET", rs.getString(7));
                headerValues.put("AREA", rs.getString(8));
                headerValues.put("CITY", rs.getString(9));
                headerValues.put("MOBILE_NUMBER", rs.getString(10));
                headerValues.put("ITEMS", rs.getString(11));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(12)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(13)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(14)));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(18)));
                headerValues.put("TOGIVE_AMOUNT", Double.toString(rs.getDouble(19)));
                headerValues.put("GIVEN_AMOUNT", Double.toString(rs.getDouble(20)));
                headerValues.put("STATUS", rs.getString(21));
                headerValues.put("NOTE", rs.getString(22));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(23) != null ? rs.getString(23) : "");
                if(rs.getDate(24) != null) {
                    headerValues.put("accepted_closing_date", format.format(rs.getDate(24).toLocalDate()));
                }
                headerValues.put("REBILLED_TO", rs.getString(25));
                headerValues.put("REBILLED_FROM", rs.getString(26)); 
                headerValues.put("NOMINEE_NAME", rs.getString(27)); 
                headerValues.put("CUSTOMER_STATUS", rs.getString(28)); 
                headerValues.put("CREATED_USER_ID", rs.getString(29)); 
                headerValues.put("CREATED_TIME", rs.getString(30));   
                headerValues.put("mobile_number_2", rs.getString(31)); 
                headerValues.put("cust_id_proof_type", rs.getString(32)); 
                headerValues.put("cust_id_proof_number", rs.getString(33)); 
                headerValues.put("refered_by_name", rs.getString(34)); 
                headerValues.put("customer_id", rs.getString(35));
                headerValues.put("customer_occupation", rs.getString(36));
                headerValues.put("physical_location", rs.getString(37));
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

    public HashMap<String, Image> getAllHeaderImages(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, Image> headerValues = new HashMap<>();
        
        String sql = "SELECT open_customer_image, open_jewel_image, open_user_image "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED', 'CANCELED') "
                + "AND BILL_NUMBER = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                byte customerBuf[] = rs.getBytes(1);
                if(customerBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(customerBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("CUSTOMER_IMG", SwingFXUtils.toFXImage(bImage, null));
                    
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillOpeningController.isOpenCustomerImgAvailable = true;
                    } else {
                        SilverBillOpeningController.isOpenCustomerImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillOpeningController.isOpenCustomerImgAvailable = false;
                    } else {
                        SilverBillOpeningController.isOpenCustomerImgAvailable = false;
                    }
                }

                byte jewelBuf[] = rs.getBytes(2);
                if(jewelBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(jewelBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("JEWEL_IMG", SwingFXUtils.toFXImage(bImage, null));

                    if(sMaterialType.equals("GOLD")) {
                        GoldBillOpeningController.isOpenJewelImgAvailable = true;
                    } else {
                        SilverBillOpeningController.isOpenJewelImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillOpeningController.isOpenJewelImgAvailable = false;
                    } else {
                        SilverBillOpeningController.isOpenJewelImgAvailable = false;
                    }
                }

                byte userBuf[] = rs.getBytes(3);
                if(userBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(userBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("USER_IMG", SwingFXUtils.toFXImage(bImage, null));

                    if(sMaterialType.equals("GOLD")) {
                        GoldBillOpeningController.isOpenUserImgAvailable = true;
                    } else {
                        SilverBillOpeningController.isOpenUserImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillOpeningController.isOpenUserImgAvailable = false;
                    } else {
                        SilverBillOpeningController.isOpenUserImgAvailable = false;
                    }
                }                
                return headerValues;		       
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return null;
    }
    
    public DataTable getAdvanceAmountTableValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT BILL_NUMBER, PAID_DATE, BILL_AMOUNT, PAID_AMOUNT, TOTAL_AMOUNT "
                + "FROM COMPANY_ADVANCE_AMOUNT "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType); 
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(3)));
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
    
    public boolean isNotCanceledBill(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT BILL_NUMBER "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? "
                + "AND STATUS = 'CANCELED'";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return false;		       
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

        return true;
    }
    
    public boolean updateRecord(String sBillNumber, String sBillOpeningDate, String sCustomerName, String sGender, 
                                String sSpouseType, String sSpouseName, String sDoorNo, 
                                String sStreetName, String sArea, String sCity, 
                                String sMobileNumber, String sStatus, String sNote,
                                String sItems, String sGrossWeight, String sNetWeight, String sPurity,
                                double dAmount, double dInterest, double dDocumentCharge, 
                                double dTakenAmount, double dToGiveAmount, double dGivenAmount, 
                                String sMaterialType, String sAcceptedDate, String sNomineeName, 
                                String mobileNumber2, String idProof, String idNumber, 
                                String recommendedBy, String customerId, String sOccupation,
                                String physicalLocation)
            throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;         
        boolean val = false;
        
        java.sql.Date sqlDateBillOpening = java.sql.Date.valueOf(LocalDate.parse(sBillOpeningDate, CommonConstants.DATETIMEFORMATTER));
        java.sql.Date sqlAccDateBillOpening = java.sql.Date.valueOf(LocalDate.parse(sAcceptedDate, CommonConstants.DATETIMEFORMATTER));
        String sql = "UPDATE COMPANY_BILLING SET "
                + "OPENING_DATE = ?, CUSTOMER_NAME = ?, GENDER = ?::GENDER_TYPE, "
                + "SPOUSE_TYPE = ?, SPOUSE_NAME = ?, "
                + "DOOR_NUMBER = ?, STREET = ?, "
                + "AREA = ?, CITY = ?, "
                + "MOBILE_NUMBER = ?, "
                + "STATUS = ?::COMPANY_BILL_STATUS, NOTE = ?, CREATED_USER_ID = ?, created_date = NOW(), "
                + "AMOUNT = ?, INTEREST = ?, DOCUMENT_CHARGE = ?, "
                + "OPEN_TAKEN_AMOUNT = ?, TOGIVE_AMOUNT = ?, GIVEN_AMOUNT = ?, "
                + "items=?, gross_weight=?, net_weight=?, purity=?, accepted_closing_date=?, NOMINEE_NAME = ?, "
                + "mobile_number_2=?, cust_id_proof_type=?, cust_id_proof_number=?, "
                + "refered_by_name=?, customer_id=?, customer_occupation=?, physical_location=? "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
                + "AND BILL_NUMBER = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setDate(1, sqlDateBillOpening);
            stmt.setString(2, sCustomerName);
            stmt.setString(3, sGender);
            stmt.setString(4, sSpouseType);
            stmt.setString(5, sSpouseName);
            stmt.setString(6, sDoorNo);
            stmt.setString(7, sStreetName);
            stmt.setString(8, sArea);
            stmt.setString(9, sCity);
            stmt.setString(10, sMobileNumber);
            stmt.setString(11, sStatus);
            stmt.setString(12, sNote);
            stmt.setString(13, CommonConstants.USERID);
            stmt.setDouble(14, dAmount);
            stmt.setDouble(15, dInterest);
            stmt.setDouble(16, dDocumentCharge);
            stmt.setDouble(17, dTakenAmount);
            stmt.setDouble(18, dToGiveAmount);
            stmt.setDouble(19, dGivenAmount);     
            stmt.setString(20, sItems);
            stmt.setDouble(21, Double.parseDouble(sGrossWeight));
            stmt.setDouble(22, Double.parseDouble(sNetWeight));
            stmt.setDouble(23, Double.parseDouble(sPurity));      
            stmt.setDate(24, sqlAccDateBillOpening);
            stmt.setString(25, sNomineeName);
            stmt.setString(26, mobileNumber2);
            stmt.setString(27, idProof);
            stmt.setString(28, idNumber);
            stmt.setString(29, recommendedBy);
            stmt.setString(30, customerId);
            stmt.setString(31, sOccupation);
            stmt.setString(32, physicalLocation);            
            stmt.setString(33, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(34, sMaterialType);
            stmt.setString(35, sBillNumber);
            val =  stmt.executeUpdate() >= 1;
            return val;
        } catch (SQLException | NumberFormatException e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateCustomerMobileNumber(String sName, String sSpouseType, String sSpouseName,
            String sDoorNo, String sStreetName, String sArea, String sCity, String sMobileNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              

        String sql = "UPDATE COMPANY_BILLING SET " +
                "MOBILE_NUMBER = ? " +
                "where COMPANY_ID = ? " +
                "and CUSTOMER_NAME = ? " +
                "AND spouse_type = ? " +
                "AND SPOUSE_NAME = ? " +
                "AND door_number = ? " +
                "AND street = ? " +
                "AND area = ? " +
                "AND city = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sMobileNumber);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(3, sName);
            stmt.setString(4, sSpouseType);
            stmt.setString(5, sSpouseName);
            stmt.setString(6, sDoorNo);            
            stmt.setString(7, sStreetName);
            stmt.setString(8, sArea);
            stmt.setString(9, sCity);
            return stmt.executeUpdate() >= 1;

            
            
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
        
        
        String sql = "SELECT BILL_NUMBER, OPENING_DATE, AMOUNT, "
                + "CUSTOMER_NAME, GENDER, SPOUSE_TYPE, "
                + "SPOUSE_NAME, "
                + "STREET, AREA, MOBILE_NUMBER, ITEMS, "
                + "GROSS_WEIGHT, NET_WEIGHT, PURITY, "
                + "STATUS, NOTE, REPLEDGE_BILL_ID "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND STATUS IN ('OPENED', 'LOCKED', 'CANCELED') "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
                //+ "TO_NUMBER(REGEXP_REPLACE("
                //+ "BILL_NUMBER, 'D', '', 'g'), '99G999D9S') BILL  "

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY OPENING_DATE DESC";
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
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(3)));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(Double.toString(rs.getDouble(12)));
                row.addColumn(Double.toString(rs.getDouble(13)));
                row.addColumn(Double.toString(rs.getDouble(14)));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                row.addColumn(rs.getString(17) == null ? "" : rs.getString(17));
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
        
        
        String sql = "SELECT COALESCE(TODAYS_RATE, 0), COALESCE(DEFAULT_PURITY,75.0), COALESCE(CITY, ''), "
                + "ALLOW_TO_CHANGE_BILL_OPENING_DATE, ALLOW_TO_CHANGE_BILL_OPENING_AMOUNT, COALESCE(AREA, ''), "
                + "bo_print_on_save, bo_print_company_copy, bo_print_customer_copy, bo_print_packing_copy,"
                + "customer_camera_name, jewel_camera_name, user_camera_name, bo_print_directly, "
                + "allow_to_change_bill_opening_given_amount "
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
                row.addColumn(rs.getDouble(1));
                row.addColumn(rs.getDouble(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getBoolean(4));
                row.addColumn(rs.getBoolean(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getBoolean(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getBoolean(14));
                row.addColumn(rs.getBoolean(15));
                dataTable.add(row);                	       
            } else {
                final DataRow row = new DataRow();
                row.addColumn(0);
                row.addColumn(0);
                row.addColumn("");
                row.addColumn(false);
                row.addColumn(false);                
                row.addColumn("");
                row.addColumn(false);
                row.addColumn(CompanyMasterController.DO_NOT_PRINT);
                row.addColumn(CompanyMasterController.DO_NOT_PRINT);
                row.addColumn(CompanyMasterController.DO_NOT_PRINT);
                row.addColumn(CompanyMasterController.DO_NOT_TAKE_PICTURE);
                row.addColumn(CompanyMasterController.DO_NOT_TAKE_PICTURE);
                row.addColumn(CompanyMasterController.DO_NOT_TAKE_PICTURE);  
                row.addColumn(false);
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
    
    public String getFirstDuePaidDate(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT PAID_DATE " +
                    "FROM COMPANY_ADVANCE_AMOUNT " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND BILL_NUMBER = ? " +
                    "ORDER BY PAID_DATE " +
                    "LIMIT 1";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
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
    
    public boolean isvalidItemTyped(String sItem, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT ITEM "
                + "FROM JEWEL_ITEMS "
                + "WHERE JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND ITEM = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sMaterialType);
            stmt.setString(2, sItem);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return true;		       
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
    
    public HashMap<String, String> getAllHeaderValuesByRepledgeBillId(String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
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
    
    public String getReductionWt(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT reduction_weight " +
                    "FROM company_other_settings " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            
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
        return "0";
    }

    public String getTodaysRate(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT todays_rate " +
                    "FROM company_other_settings " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            
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
        return "0";
    }
    
    public String getCompanyRate(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT company_rate " +
                    "FROM company_other_settings " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            
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
        return "0";
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
    
    public DataTable getAllCustomerAllBillValues(DataTable customerNames, int sIndex) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date OP_DATE, CB.items, "
                + "CB.amount, "
                + "CB.JEWEL_MATERIAL_TYPE material_type, "
                + "COALESCE(CB.repledge_bill_id, ''), "
                + "CB.GROSS_WEIGHT, CB.COMPANY_ID, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL "
                + "FROM COMPANY_BILLING CB "
                + "WHERE CB.COMPANY_ID  in (" + getCompIdsToShareCustomers() + ")  "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
                + "AND CUSTOMER_NAME = ? "
                + "AND GENDER = ?::gender_type " 
                + "AND SPOUSE_TYPE = ? "
                + "AND SPOUSE_NAME = ? "
                + "AND DOOR_NUMBER = ? "
                + "AND STREET = ? "
                + "AND AREA = ? "
                + "AND CITY = ? "
                + "AND MOBILE_NUMBER = ? ";
        try {

            sql += " ORDER BY OP_DATE, BILL, material_type ";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, customerNames.getRow(sIndex).getColumn(1).toString());
            stmt.setString(2, customerNames.getRow(sIndex).getColumn(2).toString());
            stmt.setString(3, customerNames.getRow(sIndex).getColumn(3).toString());
            stmt.setString(4, customerNames.getRow(sIndex).getColumn(4).toString());
            stmt.setString(5, customerNames.getRow(sIndex).getColumn(5).toString());
            stmt.setString(6, customerNames.getRow(sIndex).getColumn(6).toString());
            stmt.setString(7, customerNames.getRow(sIndex).getColumn(7).toString());
            stmt.setString(8, customerNames.getRow(sIndex).getColumn(8).toString());
            stmt.setString(9, customerNames.getRow(sIndex).getColumn(9).toString());
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
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
    
    public DataTable getAllCustomerAllBillValues(HashMap<String, String> headerValues) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CB.bill_number, "
                + "CB.opening_date OP_DATE, CB.items, "
                + "CB.amount, "
                + "CB.JEWEL_MATERIAL_TYPE material_type, "
                + "COALESCE(CB.repledge_bill_id, ''), "
                + "CB.GROSS_WEIGHT, CB.COMPANY_ID, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL "
                + "FROM COMPANY_BILLING CB "
                + "WHERE CB.COMPANY_ID in (" + getCompIdsToShareCustomers() + ")  "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
                + "AND CUSTOMER_NAME = ? "
                + "AND GENDER = ?::gender_type " 
                + "AND SPOUSE_TYPE = ? "
                + "AND SPOUSE_NAME = ? "
                + "AND DOOR_NUMBER = ? "
                + "AND STREET = ? "
                + "AND AREA = ? "
                + "AND CITY = ? "
                + "AND MOBILE_NUMBER = ? "
                + "AND BILL_NUMBER NOT IN (?)"; 

        try {

            sql += " ORDER BY OP_DATE, BILL, material_type ";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, headerValues.get("CUSTOMER_NAME"));
            stmt.setString(2, headerValues.get("GENDER"));
            stmt.setString(3, headerValues.get("SPOUSE_TYPE"));
            stmt.setString(4, headerValues.get("SPOUSE_NAME"));
            stmt.setString(5, headerValues.get("DOOR_NUMBER"));
            stmt.setString(6, headerValues.get("STREET"));
            stmt.setString(7, headerValues.get("AREA"));
            stmt.setString(8, headerValues.get("CITY"));
            stmt.setString(9, headerValues.get("MOBILE_NUMBER"));
            stmt.setString(10, headerValues.get("BILL_NUMBER"));
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(Double.toString(rs.getDouble(7)));
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
    
    public boolean deleteDenomination(String sOperation, String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM company_bill_denomination "
                + "WHERE COMPANY_ID = ? "
                + "AND operation = ? "
                + "and bill_number like ? "
                + "and jewel_material_type like ? ";
        
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sOperation);
            stmt.setString(3, sBillNumber);
            stmt.setString(4, "%" + sMaterialType + "%");
            stmt.executeUpdate();
            
            return true;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean saveDenominationValues(String sOperation, String sBillNumber, String sMaterialType,  
            List<AvailableBalanceBean> currencyListToSave) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into company_bill_denomination("
                + "company_id, operation, bill_number, currency_val, number_of_notes, "
                + "tot_amt_on_that_cur, created_date, user_id, jewel_material_type) "
                + "values(?, ?, ?, ?, ?, ?, now(), ?, ?)";
        
        try {
            
            for(AvailableBalanceBean bean : currencyListToSave) {
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
                stmt.setString(2, sOperation);
                stmt.setString(3, sBillNumber);
                stmt.setDouble(4, bean.getDRupee());
                stmt.setDouble(5, bean.getDNumberOfNotes());
                stmt.setDouble(6, bean.getDTotalAmount());
                stmt.setString(7, CommonConstants.USERID);
                stmt.setString(8, sMaterialType);
                stmt.executeUpdate();
            }            
            
            roleMasterConn.commit();
            roleMasterConn.setAutoCommit(true);
            
            return true;

        } catch (Exception e) {
            throw e;         
        }finally {            
            roleMasterConn.close();
        } 
    }
    
    public List<AvailableBalanceBean> getDenominationValues(String sOperation, String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        List<AvailableBalanceBean> currencyList = null;
        
        
        String sql = "select currency_val, number_of_notes, tot_amt_on_that_cur "
                + "from company_bill_denomination "
                + "where company_id = ? "
                + "and operation = ? "
                + "AND bill_number LIKE ? "
                + "and jewel_material_type like ? "
                + "ORDER BY currency_val DESC";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sOperation);
            stmt.setString(3, "%" + sBillNumber + "%");
            stmt.setString(4, "%" + sMaterialType + "%");

            rs = stmt.executeQuery();

            while(rs.next())
            {	
                if(currencyList == null) {
                    currencyList = new ArrayList<>(); 
                }
                currencyList.add(new AvailableBalanceBean(rs.getDouble(1), 
                        (int)rs.getDouble(2), 
                        rs.getDouble(3)));
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

        return currencyList;	
    }
    
    public HashMap<String, String> getRebilledFromVals(String sMaterialType, String sBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "select SUM(total_advance_amount_paid), SUM(close_taken_amount), SUM(amount), "
                + "SUM(total_other_charges), SUM(discount_amount), SUM(got_amount) "
                + "from company_billing "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE ";
                String que = "";
                for(String str : sBillNumber.split(",")) {
                    que = que + "?,";
                }                
                StringBuilder sb = new StringBuilder(que);
                sb.deleteCharAt(sb.lastIndexOf(","));                
                sql += "AND bill_number IN (" + sb.toString() + ")";
                
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            int nextNum = 3;
            for(String str : sBillNumber.split(",")) {
                stmt.setString(nextNum, str);
                nextNum++;
            }
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                headerValues.put("total_advance_amount_paid", Double.toString(rs.getDouble(1)));
                headerValues.put("close_taken_amount", Double.toString(rs.getDouble(2)));
                headerValues.put("amount", Double.toString(rs.getDouble(3)));
                headerValues.put("total_other_charges", Double.toString(rs.getDouble(4)));
                headerValues.put("discount_amount", Double.toString(rs.getDouble(5)));
                headerValues.put("got_amount", Double.toString(rs.getDouble(6)));
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

        return headerValues;	
    }
    
    public String getUserName(String sUserId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT CONCAT(id, ' - ', user_name) " +
"  FROM user_master " +
"  WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sUserId);            
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
        return "0";
    }
    
    public String getCompIdsToShareCustomers() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null; 
        String sFilePath = "";
        
        String sql = "select share_customers_from_cmps "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                sFilePath = rs.getString(1);
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
        } 

        return sFilePath;	
    }

    public void migrateOpenImages(String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, Image> headerValues = new HashMap<>();
        
                       
        String sql = "SELECT COMPANY_ID, JEWEL_MATERIAL_TYPE, BILL_NUMBER, "
                + "open_customer_image, open_jewel_image, open_user_image "
                + "FROM COMPANY_BILLING "
                + "WHERE OPENING_DATE BETWEEN ? AND ? "
                + "and company_id = ?";

        try {

            java.sql.Date sqlDateFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));            
            java.sql.Date sqlDateToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));            
            
            stmt = roleMasterConn.prepareStatement(sql);    
            stmt.setDate(1, sqlDateFromDate);
            stmt.setDate(2, sqlDateToDate);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);  
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                File compFolder = new File(tempFile, rs.getString(1));
                if(!compFolder.exists()) {
                    compFolder.mkdir();
                }
                File materialFolder = new File(compFolder, rs.getString(2));
                if(!materialFolder.exists()) {
                    materialFolder.mkdir();
                }
                File billNumberFolder = new File(materialFolder, rs.getString(3));

                byte customerBuf[] = rs.getBytes(4);
                if(customerBuf != null) {  
                    if(!billNumberFolder.exists()) {
                        billNumberFolder.mkdir();
                    }                    
                    File custTemp = new File(billNumberFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
                    System.out.println(custTemp.getAbsolutePath());
                    File cfile = new File(custTemp.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(cfile);
                    fos.write(customerBuf);                
                    fos.close();
                }

                byte jewelBuf[] = rs.getBytes(5);
                if(jewelBuf != null) {  
                    if(!billNumberFolder.exists()) {
                        billNumberFolder.mkdir();
                    }                                        
                    File custTemp = new File(billNumberFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
                    System.out.println(custTemp.getAbsolutePath());
                    File cfile = new File(custTemp.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(cfile);
                    fos.write(jewelBuf);                
                    fos.close();
                }

                byte userBuf[] = rs.getBytes(6);
                if(userBuf != null) {  
                    if(!billNumberFolder.exists()) {
                        billNumberFolder.mkdir();
                    }                                        
                    File custTemp = new File(billNumberFolder, CommonConstants.OPEN_USER_IMAGE_NAME);
                    System.out.println(custTemp.getAbsolutePath());
                    File cfile = new File(custTemp.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(cfile);
                    fos.write(userBuf);                
                    fos.close();
                }                
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public void migrateCloseImages(String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, Image> headerValues = new HashMap<>();
                
        String sql = "SELECT COMPANY_ID, JEWEL_MATERIAL_TYPE, BILL_NUMBER, "
                + "close_customer_image, close_jewel_image, close_user_image "
                + "FROM COMPANY_BILLING "
                + "WHERE CLOSING_DATE BETWEEN ? AND ? "
                + "and company_id = ?";

        try {

            java.sql.Date sqlDateFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));            
            java.sql.Date sqlDateToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));            
            
            stmt = roleMasterConn.prepareStatement(sql);    
            stmt.setDate(1, sqlDateFromDate);
            stmt.setDate(2, sqlDateToDate);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);  
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                File compFolder = new File(tempFile, rs.getString(1));
                if(!compFolder.exists()) {
                    compFolder.mkdir();
                }
                File materialFolder = new File(compFolder, rs.getString(2));
                if(!materialFolder.exists()) {
                    materialFolder.mkdir();
                }
                File billNumberFolder = new File(materialFolder, rs.getString(3));

                byte customerBuf[] = rs.getBytes(4);
                if(customerBuf != null) {  
                    if(!billNumberFolder.exists()) {
                        billNumberFolder.mkdir();
                    }
                    File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
                    System.out.println(custTemp.getAbsolutePath());
                    File cfile = new File(custTemp.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(cfile);
                    fos.write(customerBuf);                
                    fos.close();
                }

                byte jewelBuf[] = rs.getBytes(5);
                if(jewelBuf != null) {  
                    if(!billNumberFolder.exists()) {
                        billNumberFolder.mkdir();
                    }                    
                    File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_JEWEL_IMAGE_NAME);
                    System.out.println(custTemp.getAbsolutePath());
                    File cfile = new File(custTemp.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(cfile);
                    fos.write(jewelBuf);                
                    fos.close();
                }

                byte userBuf[] = rs.getBytes(6);
                if(userBuf != null) {  
                    if(!billNumberFolder.exists()) {
                        billNumberFolder.mkdir();
                    }                    
                    File custTemp = new File(billNumberFolder, CommonConstants.CLOSE_USER_IMAGE_NAME);
                    System.out.println(custTemp.getAbsolutePath());
                    File cfile = new File(custTemp.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(cfile);
                    fos.write(userBuf);                
                    fos.close();
                }
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public String[] getDenominationDetails(String sOperation, String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String[] details = new String[2];
        
        String sql = "select bill_number, jewel_material_type "
                + "from company_bill_denomination "
                + "where company_id = ? "
                + "and operation = ? "
                + "AND bill_number LIKE ? "
                + "and jewel_material_type like ? "
                + "ORDER BY currency_val DESC";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sOperation);
            stmt.setString(3, "%" + sBillNumber + "%");
            stmt.setString(4, "%" + sMaterialType + "%");
            
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                details[0] =  rs.getString(1);
                details[1] =  rs.getString(2);
            }		                
            return details;
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public double getReceivedAmount(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT GOT_AMOUNT "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
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

    public double getGivenAmount(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT given_amount "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
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
    
    public String getBillOpenedDate(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED', 'CANCELED') "
                + "AND BILL_NUMBER = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
               return format.format(rs.getDate(1).toLocalDate());
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
    
}
