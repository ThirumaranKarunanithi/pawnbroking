/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

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
import java.util.HashMap;
import javafx.collections.ObservableList;

/**
 *
 * @author Tiru
 */
public class CompanyMasterDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public CompanyMasterDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
    
    @SuppressWarnings("null")
    public String getId(String screenName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, screenName);
            stmt.setString(2, "MASTER");
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
    
    public boolean isvalidNameToSave(String sName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT NAME FROM COMPANY WHERE NAME = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sName);
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
    
    public boolean saveRecord(String sId, String sType, String sName, String sDoorNo, 
                                      String sStreetName, String sArea, String sCity, 
                                      String sState, String sLCHolderName, String sLCNumber, 
                                      String sLCDate, String sMobileNumber, String sLandlineNumber, 
                                      String sInterestType, String sStatus, String sNote, 
                                      boolean bAutoBillGeneration, boolean bEntryMode) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "insert into company(id, name, door_number, street, area, city, state, "
                + "lc_holder_name, lc_number, lc_dated, mobile_number, landline_number, "
                + "day_or_monthly_interest, status, note, user_id, "
                + "AUTO_BILL_GENERATION, type, entry_mode) "
                + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?::INTEREST_TYPE, ?::COMPANY_STATUS, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDateLCDate = java.sql.Date.valueOf(LocalDate.parse(sLCDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            
            stmt.setString(1, sId);            
            stmt.setString(2, sName);
            stmt.setString(3, sDoorNo);
            stmt.setString(4, sStreetName);
            stmt.setString(5, sArea);
            stmt.setString(6, sCity);
            stmt.setString(7, sState);
            stmt.setString(8, sLCHolderName);
            stmt.setString(9, sLCNumber);
            stmt.setDate(10, sqlDateLCDate);
            stmt.setString(11, sMobileNumber);
            stmt.setString(12, sLandlineNumber);
            stmt.setString(13, sInterestType);
            stmt.setString(14, sStatus);
            stmt.setString(15, sNote);
            stmt.setString(16, CommonConstants.USERID);
            stmt.setBoolean(17, bAutoBillGeneration);
            stmt.setString(18, sType);
            stmt.setBoolean(19, bEntryMode);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateAllToRestStatus() throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update company set status = 'REST'";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);             
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean setNextId(String screenName, String sNextIdWithPrefix) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, sNextIdWithPrefix);
            stmt.setString(2, screenName);
            stmt.setString(3, "MASTER");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public HashMap<String, String> getAllHeaderValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT NAME, DOOR_NUMBER, STREET, AREA, CITY, STATE, "
                + "LC_HOLDER_NAME, LC_NUMBER, LC_DATED, "
                + "MOBILE_NUMBER, LANDLINE_NUMBER, "
                + "CONCAT(GOLD_PRE_BILL_PREFIX, GOLD_PRE_BILL_NUMBER) GOLD_PRE_NUMBER, "
                + "CONCAT(SILVER_PRE_BILL_PREFIX, SILVER_PRE_BILL_NUMBER) SILVER_PRE_NUMBER, "
                + "DAY_OR_MONTHLY_INTEREST, STATUS, NOTE, AUTO_BILL_GENERATION, TYPE, ENTRY_MODE "
                + "FROM COMPANY "
                + "WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", sId);
                headerValues.put("NAME", rs.getString(1));
                headerValues.put("DOOR_NUMBER", rs.getString(2));
                headerValues.put("STREET", rs.getString(3));
                headerValues.put("AREA", rs.getString(4));
                headerValues.put("CITY", rs.getString(5));
                headerValues.put("STATE", rs.getString(6));
                headerValues.put("LC_HOLDER_NAME", rs.getString(7));
                headerValues.put("LC_NUMBER", rs.getString(8));
                headerValues.put("LC_DATED", format.format(rs.getDate(9).toLocalDate()));
                headerValues.put("MOBILE_NUMBER", rs.getString(10));
                headerValues.put("LANDLINE_NUMBER", rs.getString(11));
                headerValues.put("GOLD_PRE_NUMBER", rs.getString(12));
                headerValues.put("SILVER_PRE_NUMBER", rs.getString(13));
                headerValues.put("DAY_OR_MONTHLY_INTEREST", rs.getString(14));
                headerValues.put("STATUS", rs.getString(15));
                headerValues.put("NOTE", rs.getString(16));
                headerValues.put("AUTO_BILL_GENERATION", Boolean.toString(rs.getBoolean(17)).toUpperCase());
                headerValues.put("TYPE", rs.getString(18));
                headerValues.put("ENTRY_MODE", Boolean.toString(rs.getBoolean(19)).toUpperCase());
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
    
    public boolean isIdAlreadyExists(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT ID FROM COMPANY WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
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
    
    public boolean updateAllStatusToRest() throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "UPDATE COMPANY SET STATUS = ?::COMPANY_STATUS ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, "REST");
            stmt.setString(2, CommonConstants.USERID);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
        
    public boolean updateRecord(String sId, String sDoorNo, String sType, 
                                      String sStreetName, String sArea, String sCity, 
                                      String sState, String sLCHolderName, String sLCNumber, 
                                      String sLCDate, String sMobileNumber, String sLandlineNumber, 
                                      String sInterestType, String sStatus, String sNote, 
                                      boolean bAutoBillNumber, boolean bEntryMode) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        java.sql.Date sqlDateLCDate = java.sql.Date.valueOf(LocalDate.parse(sLCDate, CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE COMPANY SET DOOR_NUMBER = ?, STREET = ?, "
                + "AREA = ?, CITY = ?, STATE = ?, LC_HOLDER_NAME = ?, LC_NUMBER = ?, "
                + "LC_DATED = ?, MOBILE_NUMBER = ?, LANDLINE_NUMBER = ?, "
                + "DAY_OR_MONTHLY_INTEREST = ?::INTEREST_TYPE, "
                + "STATUS = ?::COMPANY_STATUS, NOTE = ?, USER_ID = ?, "
                + "AUTO_BILL_GENERATION = ?, TYPE = ?, ENTRY_MODE = ? "
                + "WHERE ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sDoorNo);
            stmt.setString(2, sStreetName);
            stmt.setString(3, sArea);
            stmt.setString(4, sCity);
            stmt.setString(5, sState);
            stmt.setString(6, sLCHolderName);
            stmt.setString(7, sLCNumber);
            stmt.setDate(8, sqlDateLCDate);
            stmt.setString(9, sMobileNumber);
            stmt.setString(10, sLandlineNumber);
            stmt.setString(11, sInterestType);
            stmt.setString(12, sStatus);
            stmt.setString(13, sNote);
            stmt.setString(14, CommonConstants.USERID);
            stmt.setBoolean(15, bAutoBillNumber);
            stmt.setString(16, sType);
            stmt.setBoolean(17, bEntryMode);
            stmt.setString(18, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean deleteAllNumberGenerator(String sId, String sMaterialType, String sNormalOrEMI) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_BILL_NUMBER_GENERATOR " +
                        "WHERE COMPANY_ID = ? " +
                        "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                        "AND normal_or_emi = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);  
            stmt.setString(3, sNormalOrEMI);  
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
    
    public boolean saveNumberGeneratorRecords(String sId, ObservableList<NumberGeneratorBean> tableValues, 
            String sMaterialType, String sNormalOrEMI) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into COMPANY_BILL_NUMBER_GENERATOR(company_id, jewel_material_type, "
                + "row_number, prefix, number_from, number_to, user_id, normal_or_emi) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            for(NumberGeneratorBean bean : tableValues) {
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, sId);
                stmt.setString(2, sMaterialType);
                stmt.setString(3, Integer.toString(bean.getIRowNo()));
                stmt.setString(4, bean.getSPrefix());
                stmt.setInt(5, (int) bean.getLFrom());
                stmt.setInt(6, (int) bean.getLTo());
                stmt.setString(7, CommonConstants.USERID);
                stmt.setString(8, sNormalOrEMI);
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

    public boolean updateGoldCurBillNumber(String sId, int iGoldCurBillRowNumber, 
                                      String sGoldCurBillPrefix, String sGoldCurBillNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set gold_cur_bill_row_number = ?, "
                    + "gold_cur_bill_number = ?, "
                    + "gold_cur_bill_prefix = ? "
                    + "WHERE ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setInt(1, iGoldCurBillRowNumber);
            stmt.setString(2, sGoldCurBillNumber);
            stmt.setString(3, sGoldCurBillPrefix);
            stmt.setString(4, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateEMIGoldCurBillNumber(String sId, int iGoldCurBillRowNumber, 
                                      String sGoldCurBillPrefix, String sGoldCurBillNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set emi_gold_cur_bill_row_number = ?, "
                    + "emi_gold_cur_bill_number = ?, "
                    + "emi_gold_cur_bill_prefix = ? "
                    + "WHERE ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setInt(1, iGoldCurBillRowNumber);
            stmt.setString(2, sGoldCurBillNumber);
            stmt.setString(3, sGoldCurBillPrefix);
            stmt.setString(4, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateFilePath(String sBackupFilePath, String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set backup_file_path = ? "
                    + "WHERE ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sBackupFilePath);
            stmt.setString(2, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateSilverCurBillNumber(String sId, int iGoldCurBillRowNumber, 
                                      String sGoldCurBillPrefix, String sGoldCurBillNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set silver_cur_bill_row_number = ?, "
                    + "silver_cur_bill_number = ?, "
                    + "silver_cur_bill_prefix = ? "
                    + "WHERE ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setInt(1, iGoldCurBillRowNumber);
            stmt.setString(2, sGoldCurBillNumber);
            stmt.setString(3, sGoldCurBillPrefix);
            stmt.setString(4, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAllNumberGeneratorValues(String sId, String sMaterialType, String sNormalOrEMI) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select row_number, prefix, number_from, number_to "
                + "from company_bill_number_generator "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE "
                + "and normal_or_emi = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sNormalOrEMI);
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
    
    public DataTable getGPreCurNumberValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select gold_pre_bill_row_number, gold_pre_bill_prefix, "
                + "gold_pre_bill_number, gold_cur_bill_row_number, "
                + "gold_cur_bill_prefix, gold_cur_bill_number "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getInt(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getInt(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));                
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

    public DataTable getEMIGPreCurNumberValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select emi_gold_pre_bill_row_number, emi_gold_pre_bill_prefix, "
                + "emi_gold_pre_bill_number, emi_gold_cur_bill_row_number, "
                + "emi_gold_cur_bill_prefix, emi_gold_cur_bill_number "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getInt(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getInt(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));                
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
    
    public DataTable getSPreCurNumberValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select silver_pre_bill_row_number, silver_pre_bill_prefix, "
                + "silver_pre_bill_number, silver_cur_bill_row_number, "
                + "silver_cur_bill_prefix, silver_cur_bill_number "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getInt(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getInt(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));                
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

    public DataTable getNoticeValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        String sql = "select COALESCE(notice_charge_date, NOW()), COALESCE(notice_charge_amount, 0) "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDate(1) != null ? format.format(rs.getDate(1).toLocalDate()) : "");
                row.addColumn(rs.getDouble(2));
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
    
    public String getBackupFilePath(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null; 
        String sFilePath = "";
        
        String sql = "select COALESCE(backup_file_path, '') "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
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
            roleMasterConn.close();
        } 

        return sFilePath;	
    }

    public String getCardLostCharge(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null; 
        String sFilePath = "";
        
        String sql = "select card_lost_charge_amount "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
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
            roleMasterConn.close();
        } 

        return sFilePath;	
    }
    
    public boolean deleteAllCompanyInterest(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_INTEREST "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);            
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

    public boolean saveCompanyInterestRecords(String sId, ObservableList<InterestBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into COMPANY_INTEREST(company_id, jewel_material_type, date_from, date_to, amount_from, amount_to, interest, user_id) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            for(InterestBean bean : tableValues) {

                java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSFromDate(), CommonConstants.DATETIMEFORMATTER));                
                java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSToDate(), CommonConstants.DATETIMEFORMATTER));                
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, sId);
                stmt.setString(2, sMaterialType);
                stmt.setDate(3, sqlFromDate);
                stmt.setDate(4, sqlToDate);
                stmt.setDouble(5, bean.getDFrom());
                stmt.setDouble(6, bean.getDTo());
                stmt.setDouble(7, bean.getDInterest());
                stmt.setString(8, CommonConstants.USERID);
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

    public DataTable getCompanyInterestValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, amount_from, amount_to, interest "
                + "from company_interest "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDate(1) != null ? format.format(rs.getDate(1).toLocalDate()) : "");
                row.addColumn(rs.getDate(2) != null ? format.format(rs.getDate(2).toLocalDate()) : "");
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
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

    public DataTable getCompanyDocumentChargeValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, amount_from, amount_to, document_charge "
                + "from company_document_charge "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDate(1) != null ? format.format(rs.getDate(1).toLocalDate()) : "");
                row.addColumn(rs.getDate(2) != null ? format.format(rs.getDate(2).toLocalDate()) : "");
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
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

    public DataTable getCompanyFineChargeValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select jewel_material_type, interest_type, month_days_from, month_days_to, calculation_method, charged_interest "
                + "from fine_charges "
                + "where company_id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getDouble(6));
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
    
    public boolean saveCompanyDocumentChargeRecords(String sId, ObservableList<DocumentChargeBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into COMPANY_DOCUMENT_CHARGE(company_id, jewel_material_type, date_from, date_to, amount_from, amount_to, document_charge, user_id) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            for(DocumentChargeBean bean : tableValues) {
                
                java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSFromDate(), CommonConstants.DATETIMEFORMATTER));                
                java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSToDate(), CommonConstants.DATETIMEFORMATTER));                
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, sId);
                stmt.setString(2, sMaterialType);
                stmt.setDate(3, sqlFromDate);
                stmt.setDate(4, sqlToDate);                
                stmt.setDouble(5, bean.getDFrom());
                stmt.setDouble(6, bean.getDTo());
                stmt.setDouble(7, bean.getDDocumentCharge());
                stmt.setString(8, CommonConstants.USERID);
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

    public boolean saveCompanyFineChargeRecords(String sId, ObservableList<FineChargeBean> tableValues) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into fine_charges(company_id, jewel_material_type, interest_type, "
                + "month_days_from, month_days_to, charged_interest, created_date, user_id, calculation_method) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, now(), ?, ?)";
        
        try {
            
            for(FineChargeBean bean : tableValues) {
                                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, sId);
                stmt.setString(2, bean.getSFromDate());
                stmt.setString(3, bean.getSToDate());
                stmt.setDouble(4, bean.getDFrom());
                stmt.setDouble(5, bean.getDTo());
                stmt.setDouble(6, bean.getDFineCharge());
                stmt.setString(7, CommonConstants.USERID);
                stmt.setString(8, bean.getSCalculationMethod());
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
    
    public boolean deleteAllCompanyDocumentCharge(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_DOCUMENT_CHARGE "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);            
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

    public boolean deleteAllCompanyFineCharge(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM fine_charges "
                + "WHERE COMPANY_ID = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);         
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
    
    public boolean deleteAllCompanyFormula(String sId, String sMaterialType, String sOperationType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_FORMULA "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND FORMULA_OPERATION_TYPE = ?::OPERATION_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);   
            stmt.setString(3, sOperationType);   
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
    
    public boolean saveCompanyFormulaRecords(String sId, ObservableList<FormulaBean> tableValues, String sMaterialType, String sOperationType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into COMPANY_FORMULA(company_id, jewel_material_type, formula_operation_type, date_from, date_to, amount_from, amount_to, formula, user_id) "
                + "values(?, ?::MATERIAL_TYPE, ?::OPERATION_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            for(FormulaBean bean : tableValues) {
                
                java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSFromDate(), CommonConstants.DATETIMEFORMATTER));                
                java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSToDate(), CommonConstants.DATETIMEFORMATTER));                
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, sId);
                stmt.setString(2, sMaterialType);
                stmt.setString(3, sOperationType);
                stmt.setDate(4, sqlFromDate);
                stmt.setDate(5, sqlToDate);                                                
                stmt.setDouble(6, bean.getDFrom());
                stmt.setDouble(7, bean.getDTo());
                stmt.setString(8, bean.getSFormula());
                stmt.setString(9, CommonConstants.USERID);
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
    
    public DataTable getCompanyFormulaValues(String sId, String sMaterialType, String sOperationType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, amount_from, amount_to, formula "
                + "from company_formula "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE "
                + "and formula_operation_type = ?::OPERATION_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sOperationType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDate(1) != null ? format.format(rs.getDate(1).toLocalDate()) : "");
                row.addColumn(rs.getDate(2) != null ? format.format(rs.getDate(2).toLocalDate()) : "");
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getString(5));
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

    public boolean deleteAllCompanyMonthSetting(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_MONTH_SETTING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);            
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

    public boolean deleteReductionOrMinimumDaysOrMonthsValues(String sId, String sMaterialType, String sType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_REDUCE_MONTHS_OR_DAYS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);        
            stmt.setString(3, sType);        
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
    
    public boolean saveCompanyMonthSettingRecords(String sId, ObservableList<MonthSettingBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into COMPANY_MONTH_SETTING(company_id, jewel_material_type, date_from, date_to, DAYS_FROM, DAYS_TO, AS_MONTH, user_id) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {
            
                            
            for(MonthSettingBean bean : tableValues) {
                
                java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSFromDate(), CommonConstants.DATETIMEFORMATTER));                
                java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSToDate(), CommonConstants.DATETIMEFORMATTER));                

                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, sId);
                stmt.setString(2, sMaterialType);
                stmt.setDate(3, sqlFromDate);
                stmt.setDate(4, sqlToDate);                                
                stmt.setDouble(5, bean.getDFrom());
                stmt.setDouble(6, bean.getDTo());
                stmt.setDouble(7, bean.getDAsMonth());
                stmt.setString(8, CommonConstants.USERID);
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

    public boolean saveReductionOrMinimumDaysOrMonthsValues(String sId, double value, String sReductionType, String sMaterialType, String sType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO COMPANY_REDUCE_MONTHS_OR_DAYS(COMPANY_ID, JEWEL_MATERIAL_TYPE, "
                + "DAYS_OR_MONTHS, REDUCTION_TYPE, REDUCTION_OR_MINIMUM_TYPE, USER_ID) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?::REDUCTION_OR_MINIMUM_TYPE, ?)";
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, value);
            stmt.setString(4, sReductionType);
            stmt.setString(5, sType);
            stmt.setString(6, CommonConstants.USERID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getCompanyMonthSettingValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, DAYS_FROM, DAYS_TO, as_month "
                + "from company_month_setting "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDate(1) != null ? format.format(rs.getDate(1).toLocalDate()) : "");
                row.addColumn(rs.getDate(2) != null ? format.format(rs.getDate(2).toLocalDate()) : "");
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
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

    public DataTable getCompanyDaysOrMonthsValues(String sId, String sMaterialType, String sType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT DAYS_OR_MONTHS, REDUCTION_TYPE "
                + "FROM COMPANY_REDUCE_MONTHS_OR_DAYS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDouble(1));
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

    public DataTable getOtherSettingsValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT TODAYS_RATE, COMPANY_RATE, REDUCTION_WEIGHT, DEFAULT_PURITY, CITY, AREA, " 
                + "ALLOW_TO_CHANGE_BILL_OPENING_DATE, ALLOW_TO_CHANGE_BILL_CLOSING_DATE, "
                + "ALLOW_TO_CHANGE_ADVANCE_AMOUNT_DATE, ALLOW_TO_CHANGE_REPLEDGE_BILL_OPENING_DATE, "
                + "ALLOW_TO_CHANGE_REPLEDGE_BILL_CLOSING_DATE, ALLOW_TO_CHANGE_REPLEDGE_NAME_IN_OPENING, "
                + "ALLOW_TO_CHANGE_BILL_OPENING_AMOUNT, ALLOW_TO_CHANGE_BILL_CLOSING_AMOUNT, " +
                "ALLOW_TO_CHANGE_ADVANCE_AMOUNT_AMOUNT, ALLOW_TO_CHANGE_REPLEDGE_BILL_OPENING_AMOUNT, " +
                "ALLOW_TO_CHANGE_REPLEDGE_BILL_CLOSING_AMOUNT, "
                + "bo_print_on_save, bo_print_company_copy, bo_print_customer_copy, bo_print_packing_copy, bo_print_directly, "
                + "customer_camera_name, jewel_camera_name, user_camera_name, camera_temp_file_name, "
                + "allow_to_change_bill_opening_given_amount, allow_to_change_bill_closing_received_amount, "
                + "verify_gbc_copies "
                + "FROM COMPANY_OTHER_SETTINGS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getDouble(1));
                row.addColumn(rs.getDouble(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getString(5) != null ? rs.getString(5) : "");
                row.addColumn(rs.getString(6) != null ? rs.getString(6) : "");
                row.addColumn(Boolean.toString(rs.getBoolean(7)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(8)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(9)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(10)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(11)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(12)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(13)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(14)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(15)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(16)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(17)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(18)).toUpperCase());
                row.addColumn(rs.getString(19) != null ? rs.getString(19) : CompanyMasterController.DO_NOT_PRINT);
                row.addColumn(rs.getString(20) != null ? rs.getString(20) : CompanyMasterController.DO_NOT_PRINT);
                row.addColumn(rs.getString(21) != null ? rs.getString(21) : CompanyMasterController.DO_NOT_PRINT);
                row.addColumn(Boolean.toString(rs.getBoolean(22)).toUpperCase());
                row.addColumn(rs.getString(23) != null ? rs.getString(23) : CompanyMasterController.DO_NOT_TAKE_PICTURE);
                row.addColumn(rs.getString(24) != null ? rs.getString(24) : CompanyMasterController.DO_NOT_TAKE_PICTURE);
                row.addColumn(rs.getString(25) != null ? rs.getString(25) : CompanyMasterController.DO_NOT_TAKE_PICTURE);
                row.addColumn(rs.getString(26));
                row.addColumn(Boolean.toString(rs.getBoolean(27)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(28)).toUpperCase());    
                row.addColumn(Boolean.toString(rs.getBoolean(29)).toUpperCase());    
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

    public DataTable getTodaysAccountSettingsValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
                    "PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
                    "TODAYS_DEFICIT_AMOUNT " +
                    "FROM COMPANY_TODAYS_ACCOUNT "
                    + "WHERE COMPANY_ID = ? "
                    + "AND REF_MARK = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, "L");
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(rs.getDouble(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));
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

    public DataTable getTodaysAccountSettingsValues(String sId, String date) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
                    "PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
                    "TODAYS_DEFICIT_AMOUNT " +
                    "FROM COMPANY_TODAYS_ACCOUNT "
                    + "WHERE COMPANY_ID = ? "
                    + "AND TODAYS_DATE = ? ";

        try {
            
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(date, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(rs.getDouble(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));
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
    
    public DataTable getStartingAccountSettingsValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
                    "PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
                    "TODAYS_DEFICIT_AMOUNT " +
                    "FROM COMPANY_TODAYS_ACCOUNT "
                    + "WHERE COMPANY_ID = ? "
                    + "ORDER BY TODAYS_DATE " 
                    + "LIMIT 1";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(rs.getDouble(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));
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
    
    public DataTable getAllDetailsValues(String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME, "
                + "LC_HOLDER_NAME, LC_NUMBER, "
                + "AREA, LANDLINE_NUMBER, STATUS "
                + "FROM COMPANY ";

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            
            stmt = roleMasterConn.prepareStatement(sql);               
            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    stmt.setString(i+1, "%"+sVals[i]+"%");
                }
            }
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
    
    public boolean deleteAllCompanyOtherSetting(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_OTHER_SETTINGS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);            
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
    
    public boolean saveCompanyGOtherSettingRecords(String sId, String sTodaysRate, String sCompRate, String sReductionWt, 
            String sDefaultPurityValue, String sDefaultCity, String sDefaultArea, 
            boolean sBODate, boolean sBCDate, boolean sAADate, 
            boolean sRBODate, boolean sRBCDate, boolean sRBOName,
            boolean sBOAmount, boolean sBCAmount, boolean sAAAmount, 
            boolean sRBOAmount, boolean sRBCAmount, boolean sBoPrint, 
            String sBoCompPrint, String sBoCustPrint, String sBoPackPrint, boolean sBoDirectPrint,
            String sCustomerCamera, String sJewelCamera, String sUserCamera, String sTempFilePath,
            String sMaterialType, boolean sBOGivenAmount, boolean sBCReceivedAmount,
            boolean sBCVerifyCopies) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "INSERT INTO company_other_settings(company_id, jewel_material_type, todays_rate, "
            + "default_purity, city, allow_to_change_bill_opening_date, allow_to_change_bill_closing_date, "
            + "allow_to_change_advance_amount_date, allow_to_change_repledge_bill_opening_date, "
            + "allow_to_change_repledge_bill_closing_date, ALLOW_TO_CHANGE_REPLEDGE_NAME_IN_OPENING, "
            + "allow_to_change_bill_opening_amount, allow_to_change_bill_closing_amount, " +
            "allow_to_change_advance_amount_amount, allow_to_change_repledge_bill_opening_amount, "
            + "allow_to_change_repledge_bill_closing_amount, area, company_rate, reduction_weight, "
            + "bo_print_on_save, bo_print_company_copy, bo_print_customer_copy, bo_print_packing_copy, bo_print_directly, "
            + "customer_camera_name, jewel_camera_name, user_camera_name, camera_temp_file_name, "
            + "allow_to_change_bill_opening_given_amount, allow_to_change_bill_closing_received_amount,"
                + "verify_gbc_copies) "
            + "VALUES (?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            
        PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
        stmt.setString(1, sId);
        stmt.setString(2, sMaterialType);
        stmt.setDouble(3, Double.parseDouble(sTodaysRate));
        stmt.setDouble(4, Double.parseDouble(sDefaultPurityValue));
        stmt.setString(5, sDefaultCity);
        stmt.setBoolean(6, sBODate);
        stmt.setBoolean(7, sBCDate);
        stmt.setBoolean(8, sAADate);
        stmt.setBoolean(9, sRBODate);
        stmt.setBoolean(10, sRBCDate);
        stmt.setBoolean(11, sRBOName);
        stmt.setBoolean(12, sBOAmount);
        stmt.setBoolean(13, sBCAmount);
        stmt.setBoolean(14, sAAAmount);
        stmt.setBoolean(15, sRBOAmount);
        stmt.setBoolean(16, sRBCAmount);
        stmt.setString(17, sDefaultArea);
        stmt.setDouble(18, Double.parseDouble(sCompRate));
        stmt.setDouble(19, Double.parseDouble(sReductionWt));
        stmt.setBoolean(20, sBoPrint);
        stmt.setString(21, sBoCompPrint);
        stmt.setString(22, sBoCustPrint);
        stmt.setString(23, sBoPackPrint);
        stmt.setBoolean(24, sBoDirectPrint);
        stmt.setString(25, sCustomerCamera);
        stmt.setString(26, sJewelCamera);
        stmt.setString(27, sUserCamera);
        stmt.setString(28, sTempFilePath);
        stmt.setBoolean(29, sBOGivenAmount);
        stmt.setBoolean(30, sBCReceivedAmount);  
        stmt.setBoolean(31, sBCVerifyCopies);  
        stmt.executeUpdate();
            
        roleMasterConn.commit();
        roleMasterConn.setAutoCommit(true);
            
        return true;

        } catch (Exception e) {
            throw e;         
        }finally {            
            roleMasterConn.close();
        } 
    }

    public boolean saveCompanySOtherSettingRecords(String sId, String sTodaysRate, String sCompRate, String sReductionWt, 
            String sDefaultPurityValue, String sDefaultCity, String sDefaultArea,
            boolean sBODate, boolean sBCDate, boolean sAADate, 
            boolean sBOAmount, boolean sBCAmount, boolean sAAAmount, 
            boolean sBOPrint, String sBOCompPrint, String sBOCustPrint, String sBOPackPrint, boolean sBODirectPrint,   
            String sMaterialType, boolean sBOGivenAmount, boolean sBCReceivedAmount,
            boolean sVerifyBC) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "INSERT INTO company_other_settings(company_id, jewel_material_type, todays_rate, "
                + "default_purity, city, allow_to_change_bill_opening_date, allow_to_change_bill_closing_date, "
                + "allow_to_change_advance_amount_date, allow_to_change_bill_opening_amount, "
                + "allow_to_change_bill_closing_amount, allow_to_change_advance_amount_amount, area, "
                + "company_rate, reduction_weight, bo_print_on_save, "
                + "bo_print_company_copy, bo_print_customer_copy, bo_print_packing_copy, bo_print_directly,"
                + "allow_to_change_bill_opening_given_amount, allow_to_change_bill_closing_received_amount,"
                + "verify_gbc_copies) "
                + "VALUES (?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            
        PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
        stmt.setString(1, sId);
        stmt.setString(2, sMaterialType);
        stmt.setDouble(3, Double.parseDouble(sTodaysRate));
        stmt.setDouble(4, Double.parseDouble(sDefaultPurityValue));
        stmt.setString(5, sDefaultCity);
        stmt.setBoolean(6, sBODate);
        stmt.setBoolean(7, sBCDate);
        stmt.setBoolean(8, sAADate);
        stmt.setBoolean(9, sBOAmount);
        stmt.setBoolean(10, sBCAmount);
        stmt.setBoolean(11, sAAAmount);
        stmt.setString(12, sDefaultArea);
        stmt.setDouble(13, Double.parseDouble(sCompRate));
        stmt.setDouble(14, Double.parseDouble(sReductionWt));        
        stmt.setBoolean(15, sBOPrint);
        stmt.setString(16, sBOCompPrint);
        stmt.setString(17, sBOCustPrint);
        stmt.setString(18, sBOPackPrint);
        stmt.setBoolean(19, sBODirectPrint);
        stmt.setBoolean(20, sBOGivenAmount);
        stmt.setBoolean(21, sBCReceivedAmount);  
        stmt.setBoolean(22, sVerifyBC);  
        
        stmt.executeUpdate();
            
        roleMasterConn.commit();
        roleMasterConn.setAutoCommit(true);
            
        return true;

        } catch (Exception e) {
            throw e;         
        }finally {            
            roleMasterConn.close();
        } 
    }
    
    public boolean deleteAllTodaysAccSetting(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_TODAYS_ACCOUNT "
                + "WHERE COMPANY_ID = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
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
    
    public boolean saveTodaysAccSetting(String sId, String sPreDate, 
            String sAvailableBalance, String sStartingDate) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "INSERT INTO COMPANY_TODAYS_ACCOUNT(" +
                    "COMPANY_ID, PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
                    "PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
                    "TODAYS_DEFICIT_AMOUNT, REF_MARK, USER_ID)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            java.sql.Date sqlPreDate = java.sql.Date.valueOf(LocalDate.parse(sPreDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlStartingDate = java.sql.Date.valueOf(LocalDate.parse(sStartingDate, CommonConstants.DATETIMEFORMATTER));
            
            PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
            stmt.setString(1, sId);
            stmt.setDate(2, sqlPreDate);
            stmt.setDouble(3, 0);
            stmt.setDouble(4, 0);
            stmt.setDouble(5, 0);
            stmt.setDate(6, sqlStartingDate);
            stmt.setDouble(7, Double.parseDouble(sAvailableBalance));
            stmt.setDouble(8, Double.parseDouble(sAvailableBalance));
            stmt.setDouble(9, 0);
            stmt.setString(10, "L");
            stmt.setString(11, CommonConstants.USERID);
            stmt.executeUpdate();

            roleMasterConn.commit();
            roleMasterConn.setAutoCommit(true);

            return true;

        } catch (Exception e) {
            throw e;         
        }finally {            
            roleMasterConn.close();
        } 
    }

    public boolean deleteAccRemaingColsedDays(String sId, String sDate) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM company_todays_account "
                + "WHERE COMPANY_ID = ? "
                + "AND todays_date > ?";
        
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlDate);           
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
    
    public boolean updateAccountTableMarkToNull(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_TODAYS_ACCOUNT " +
                    "SET REF_MARK=? " +
                    "WHERE COMPANY_ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, "");           
            stmt.setString(2, sId);

            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateAccountTableMarkToL(String sId, String sDate) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_TODAYS_ACCOUNT " +
                    "SET REF_MARK=? " +
                    "WHERE COMPANY_ID = ? "
                    + "AND todays_date = ?";
        
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, "L");           
            stmt.setString(2, sId);
            stmt.setDate(3, sqlDate);      

            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getCreditTableValues(String sCompanyId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, CREDITED_DATE, REASON, CREDITED_AMOUNT "
                + "FROM COMPANY_OTHER_CREDIT "
                + "WHERE COMPANY_ID = ? ";                

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sCompanyId);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(rs.getString(5));
                row.addColumn(Double.toString(rs.getDouble(6)));
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

    public DataTable getCreditTableValues(String sCompanyId, String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, CREDITED_DATE, REASON, CREDITED_AMOUNT "
                + "FROM COMPANY_OTHER_CREDIT "
                + "WHERE COMPANY_ID = ? "
                + "AND CREDITED_DATE BETWEEN ? AND ? ";                

        try {
            
            java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sCompanyId);
            stmt.setDate(2, sqlFromDate);
            stmt.setDate(3, sqlToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(rs.getString(5));
                row.addColumn(Double.toString(rs.getDouble(6)));
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
    
    public DataTable getDebitTableValues(String sCompanyId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, DEBITTED_DATE, REASON, DEBITTED_AMOUNT "
                + "FROM COMPANY_OTHER_DEBIT "
                + "WHERE COMPANY_ID = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sCompanyId);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(rs.getString(5));
                row.addColumn(Double.toString(rs.getDouble(6)));
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

    public DataTable getDebitTableValues(String sCompanyId, String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, DEBITTED_DATE, REASON, DEBITTED_AMOUNT "
                + "FROM COMPANY_OTHER_DEBIT "
                + "WHERE COMPANY_ID = ? "
                + "AND DEBITTED_DATE BETWEEN ? AND ? ";                

        try {
            
            java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sCompanyId);
            stmt.setDate(2, sqlFromDate);
            stmt.setDate(3, sqlToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(rs.getString(5));
                row.addColumn(Double.toString(rs.getDouble(6)));
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

    public boolean updateNoticeCharges(String sId, String sNoticeDate, double dNoticeAmount) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set notice_charge_date = ?, "
                    + "notice_charge_amount = ? "
                    + "WHERE ID = ?";
        
        try {

            java.sql.Date sqlDateLCDate = java.sql.Date.valueOf(LocalDate.parse(sNoticeDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setDate(1, sqlDateLCDate);
            stmt.setDouble(2, dNoticeAmount);
            stmt.setString(3, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public int companyCount() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT COUNT(NAME) FROM COMPANY ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);                           
            rs = stmt.executeQuery();

            if(rs.next())
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
    
    public boolean updateCardLostCharges(String sId, double dNoticeAmount) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set card_lost_charge_amount = ? "
                    + "WHERE ID = ?";
        
        try {

           stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setDouble(1, dNoticeAmount);
            stmt.setString(2, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getCompaniesList(String sCompanyType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME "
                + "FROM COMPANY "
                + "where type = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sCompanyType);            
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
    
    public boolean updateCompIdForSharingCustomers(String sId, String compIds) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set share_customers_from_cmps = ? "
                    + "WHERE ID = ?";
        
        try {

           stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, compIds);
            stmt.setString(2, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateAccountOtherSettings(String sId, 
        boolean sCompExpDate,
        boolean sEmpExpDate,
        boolean sRepExpDate,
        boolean sCompIncDate,
        boolean sEmpIncDate,
        boolean sRepIncDate ) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "update company set "
                    + "allow_to_change_emp_exp_date = ?, "
                    + "allow_to_change_comp_exp_date = ?, "
                    + "allow_to_change_rep_exp_date = ?, "
                    + "allow_to_change_emp_inc_date = ?, "
                    + "allow_to_change_comp_inc_date = ?, "
                    + "allow_to_change_rep_inc_date = ? "
                    + "WHERE ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setBoolean(1, sEmpExpDate);
            stmt.setBoolean(2, sCompExpDate);
            stmt.setBoolean(3, sRepExpDate);
            stmt.setBoolean(4, sEmpIncDate);
            stmt.setBoolean(5, sCompIncDate);
            stmt.setBoolean(6, sRepIncDate);
            stmt.setString(7, sId);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {            
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAccOtherSettingsValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT allow_to_change_emp_exp_date, "
                    + "allow_to_change_comp_exp_date, "
                    + "allow_to_change_rep_exp_date, "
                    + "allow_to_change_emp_inc_date, "
                    + "allow_to_change_comp_inc_date, "
                    + "allow_to_change_rep_inc_date  "
                + "FROM COMPANY "
                + "WHERE ID = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(Boolean.toString(rs.getBoolean(1)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(2)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(3)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(4)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(5)).toUpperCase());
                row.addColumn(Boolean.toString(rs.getBoolean(6)).toUpperCase());
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
