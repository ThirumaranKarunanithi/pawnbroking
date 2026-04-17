/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgemaster;

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
public class RepledgeMasterDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public RepledgeMasterDBOperation(String sDB, String sIP, String sPort, String sSchema,
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

        String sql ="SELECT NAME FROM REPLEDGE WHERE NAME = ?";

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
    
    public boolean saveRecord(String sId, String sName, String sDoorNo, 
                                      String sStreetName, String sArea, String sCity, 
                                      String sState, String sDate, String sMobileNumber, String sLandlineNumber, 
                                      String sInterestType, String sStatus, String sNote) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "insert into REPLEDGE(id, name, door_number, street, area, city, state, STARTED_DATE, mobile_number, landline_number, day_or_monthly_interest, status, note, user_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::INTEREST_TYPE, ?::REPLEDGE_STATUS, ?, ?)";
        
        try {

            java.sql.Date sqlDateStartedDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            
            stmt.setString(1, sId);
            stmt.setString(2, sName);
            stmt.setString(3, sDoorNo);
            stmt.setString(4, sStreetName);
            stmt.setString(5, sArea);
            stmt.setString(6, sCity);
            stmt.setString(7, sState);
            stmt.setDate(8, sqlDateStartedDate);
            stmt.setString(9, sMobileNumber);
            stmt.setString(10, sLandlineNumber);
            stmt.setString(11, sInterestType);
            stmt.setString(12, sStatus);
            stmt.setString(13, sNote);
            stmt.setString(14, CommonConstants.USERID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateRecord(String sId, String sDoorNo, 
                                String sStreetName, String sArea, String sCity, 
                                String sState, String sDate, String sMobileNumber, String sLandlineNumber, 
                                String sInterestType, String sStatus, String sNote) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE REPLEDGE " +
                    "SET DOOR_NUMBER=?, STREET=?, AREA=?, CITY=?, STATE=?, " +
                    "STARTED_DATE=?, MOBILE_NUMBER=?, LANDLINE_NUMBER=?, DAY_OR_MONTHLY_INTEREST=?::INTEREST_TYPE, " +
                    "STATUS=?::REPLEDGE_STATUS, NOTE=? " +
                    "WHERE ID=?";
        
        try {

            java.sql.Date sqlDateStartedDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            
            stmt.setString(1, sDoorNo);
            stmt.setString(2, sStreetName);
            stmt.setString(3, sArea);
            stmt.setString(4, sCity);
            stmt.setString(5, sState);
            stmt.setDate(6, sqlDateStartedDate);
            stmt.setString(7, sMobileNumber);
            stmt.setString(8, sLandlineNumber);
            stmt.setString(9, sInterestType);
            stmt.setString(10, sStatus);
            stmt.setString(11, sNote);
            stmt.setString(12, sId);
            
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
                + "STARTED_DATE, MOBILE_NUMBER, LANDLINE_NUMBER, "
                + "DAY_OR_MONTHLY_INTEREST, STATUS, NOTE "
                + "FROM REPLEDGE "
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
                headerValues.put("STARTED_DATE", format.format(rs.getDate(7).toLocalDate()));
                headerValues.put("MOBILE_NUMBER", rs.getString(8));
                headerValues.put("LANDLINE_NUMBER", rs.getString(9));
                headerValues.put("DAY_OR_MONTHLY_INTEREST", rs.getString(10));
                headerValues.put("STATUS", rs.getString(11));
                headerValues.put("NOTE", rs.getString(12));
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

        String sql ="SELECT ID FROM REPLEDGE WHERE ID = ?";

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

    public boolean deleteAllRepledgeInterest(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM REPLEDGE_INTEREST "
                + "WHERE REPLEDGE_ID = ? "
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

    public boolean saveRepledgeInterestRecords(String sId, ObservableList<InterestBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into REPLEDGE_INTEREST(repledge_id, jewel_material_type, date_from, date_to, amount_from, amount_to, interest, user_id) "
                + "values(?, ?::MATERIAL_TYPE,?, ?, ?, ?, ?, ?)";
        
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

    public DataTable getRepledgeInterestValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, amount_from, amount_to, interest "
                + "from repledge_interest "
                + "where repledge_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
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

    public DataTable getRepledgeDocumentChargeValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, amount_from, amount_to, document_charge "
                + "from repledge_document_charge "
                + "where repledge_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
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

    public boolean saveRepledgeDocumentChargeRecords(String sId, ObservableList<DocumentChargeBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into REPLEDGE_DOCUMENT_CHARGE(repledge_id, jewel_material_type, date_from, date_to, amount_from, amount_to, document_charge, user_id) "
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
    
    public boolean deleteAllRepledgeDocumentCharge(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM REPLEDGE_DOCUMENT_CHARGE "
                + "WHERE REPLEDGE_ID = ? "
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

    public boolean deleteAllRepledgeFormula(String sId, String sMaterialType, String sOperationType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM REPLEDGE_FORMULA "
                + "WHERE REPLEDGE_ID = ? "
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
    
    public boolean saveRepledgeFormulaRecords(String sId, ObservableList<FormulaBean> tableValues, String sMaterialType, String sOperationType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into REPLEDGE_FORMULA(repledge_id, jewel_material_type, formula_operation_type, date_from, date_to, amount_from, amount_to, formula, user_id) "
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
    
    public DataTable getRepledgeFormulaValues(String sId, String sMaterialType, String sOperationType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, amount_from, amount_to, formula "
                + "from repledge_formula "
                + "where repledge_id = ? "
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
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));                                
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

    public boolean deleteAllRepledgeMonthSetting(String sId, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM REPLEDGE_MONTH_SETTING "
                + "WHERE REPLEDGE_ID = ? "
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

    public boolean deleteReduceDaysOrMonthsValues(String sId, String sMaterialType, String sType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM REPLEDGE_REDUCE_MONTHS_OR_DAYS "
                + "WHERE REPLEDGE_ID = ? "
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
    
    public boolean saveRepledgeMonthSettingRecords(String sId, ObservableList<MonthSettingBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into REPLEDGE_MONTH_SETTING(repledge_id, jewel_material_type, date_from, date_to, DAYS_FROM, DAYS_TO, AS_MONTH, user_id) "
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

    public boolean saveReduceDaysOrMonthsValues(String sId, double sValue, String sReductionType, String sMaterialType, String sType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO REPLEDGE_REDUCE_MONTHS_OR_DAYS(REPLEDGE_ID, JEWEL_MATERIAL_TYPE, "
                + "DAYS_OR_MONTHS, REDUCTION_TYPE, REDUCTION_OR_MINIMUM_TYPE, USER_ID) "
                + "values(?, ?::MATERIAL_TYPE, ?, ?, ?::REDUCTION_OR_MINIMUM_TYPE, ?)";
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, sValue);
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
    
    public DataTable getRepledgeMonthSettingValues(String sId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "select date_from, date_to, DAYS_FROM, DAYS_TO, as_month "
                + "from repledge_month_setting "
                + "where repledge_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(format.format(rs.getDate(1).toLocalDate()));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));                
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

    public DataTable getRepledgeReduceDaysOrMonthsValues(String sId, String sMaterialType, String sType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT DAYS_OR_MONTHS, REDUCTION_TYPE "
                + "FROM REPLEDGE_REDUCE_MONTHS_OR_DAYS "
                + "WHERE REPLEDGE_ID = ? "
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
    
    public DataTable getAllDetailsValues(String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME, "
                + "AREA, LANDLINE_NUMBER, "
                + "DAY_OR_MONTHLY_INTEREST, STATUS "
                + "FROM REPLEDGE ";

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
        
        
        String sql = "SELECT TODAYS_RATE, DEFAULT_PURITY, CITY, "
                + "ALLOW_TO_CHANGE_BILL_OPENING_DATE, ALLOW_TO_CHANGE_BILL_CLOSING_DATE, "
                + "ALLOW_TO_CHANGE_ADVANCE_AMOUNT_DATE "
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
                row.addColumn(rs.getString(3));
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
    
    public DataTable getCreditTableValues(String sRepledgeId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, CREDITED_DATE, REASON, CREDITED_AMOUNT "
                + "FROM REPLEDGE_OTHER_CREDIT "
                + "WHERE COMPANY_ID = ? "
                + "AND REPLEDGE_ID = ?";                

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepledgeId);
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

    public DataTable getCreditTableValues(String sRepledgeId, String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, CREDITED_DATE, REASON, CREDITED_AMOUNT "
                + "FROM REPLEDGE_OTHER_CREDIT "
                + "WHERE COMPANY_ID = ? "
                + "AND REPLEDGE_ID = ? "
                + "AND CREDITED_DATE BETWEEN ? AND ? ";                

        try {
            
            java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepledgeId);
            stmt.setDate(3, sqlFromDate);
            stmt.setDate(4, sqlToDate);
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
    
    public DataTable getDebitTableValues(String sRepledgeId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, DEBITTED_DATE, REASON, DEBITTED_AMOUNT "
                + "FROM REPLEDGE_OTHER_DEBIT "
                + "WHERE COMPANY_ID = ? "
                + "AND REPLEDGE_ID = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepledgeId);
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

    public DataTable getDebitTableValues(String sRepledgeId, String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, EXPENSE_TYPE, DEBITTED_DATE, REASON, DEBITTED_AMOUNT "
                + "FROM REPLEDGE_OTHER_DEBIT "
                + "WHERE COMPANY_ID = ? "
                + "AND REPLEDGE_ID = ? "
                + "AND DEBITTED_DATE BETWEEN ? AND ? ";                

        try {
            
            java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepledgeId);
            stmt.setDate(3, sqlFromDate);
            stmt.setDate(4, sqlToDate);
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
    
}
