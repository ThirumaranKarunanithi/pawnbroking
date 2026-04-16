/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.customerdetails;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.Customer;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class CustomerDetailsDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public CustomerDetailsDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(CustomerDetailsDBOperation.class.getName()).log(Level.SEVERE, null, ex);
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
        
    public DataTable getAllDetailsValues() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT COALESCE(OQ.TOT_STOCK_BILLS, 0), COUNT(CB.BILL_NUMBER) TOT_OPEND_BILLS, " +
                        "CB.customer_name, CB.gender, CB.spouse_type,  " +
                        "CB.spouse_name, CB.door_number, CB.street, CB.area, CB.city, CB.mobile_number, CB.CUSTOMER_ID  " +
                        "FROM COMPANY_BILLING CB LEFT OUTER JOIN  " +
                        " " +
                        "(SELECT DISTINCT COUNT(BILL_NUMBER) TOT_STOCK_BILLS,  " +
                        "customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number, CUSTOMER_ID  " +
                        "FROM COMPANY_BILLING " +
                        "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ") " +
                        "AND STATUS IN ('OPENED', 'LOCKED') " +
                        "GROUP BY customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number, CUSTOMER_ID  " +
                        "ORDER BY customer_name, spouse_name) OQ " +
                        " " +
                        "ON CB.CUSTOMER_NAME = OQ.CUSTOMER_NAME " +
                        "AND CB.gender = OQ.gender " +
                        "AND CB.spouse_type = OQ.spouse_type " +
                        "AND CB.spouse_name = OQ.spouse_name " +
                        "AND CB.door_number = OQ.door_number " +
                        "AND CB.street = OQ.street " +
                        "AND CB.area = OQ.area " +
                        "AND CB.city = OQ.city " +
                        "AND CB.mobile_number = OQ.mobile_number " +
                        "AND CB.CUSTOMER_ID = OQ.CUSTOMER_ID " +
                        "WHERE CB.COMPANY_ID in (" + getCompIdsToShareCustomers() + ")  " +
                        "GROUP BY OQ.TOT_STOCK_BILLS, CB.customer_name, CB.gender, CB.spouse_type,  " +
                        "CB.spouse_name, CB.door_number, CB.street, CB.area, CB.city, CB.mobile_number, CB.CUSTOMER_ID  " +
                        "ORDER BY CB.customer_name, CB.spouse_name ";

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

    public DataTable getActiveValues() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT COALESCE(OQ.TOT_STOCK_BILLS, 0), COUNT(CB.BILL_NUMBER) TOT_OPEND_BILLS, " +
                        "CB.customer_name, CB.gender, CB.spouse_type,  " +
                        "CB.spouse_name, CB.door_number, CB.street, CB.area, CB.city, CB.mobile_number, CB.CUSTOMER_ID  " +
                        "FROM COMPANY_BILLING CB INNER JOIN  " +
                        " " +
                        "(SELECT DISTINCT COUNT(BILL_NUMBER) TOT_STOCK_BILLS,  " +
                        "customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number, CUSTOMER_ID  " +
                        "FROM COMPANY_BILLING " +
                        "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ") " +
                        "AND STATUS IN ('OPENED', 'LOCKED') " +
                        "GROUP BY customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number, CUSTOMER_ID  " +
                        "ORDER BY customer_name, spouse_name) OQ " +
                        " " +
                        "ON CB.CUSTOMER_NAME = OQ.CUSTOMER_NAME " +
                        "AND CB.gender = OQ.gender " +
                        "AND CB.spouse_type = OQ.spouse_type " +
                        "AND CB.spouse_name = OQ.spouse_name " +
                        "AND CB.door_number = OQ.door_number " +
                        "AND CB.street = OQ.street " +
                        "AND CB.area = OQ.area " +
                        "AND CB.city = OQ.city " +
                        "AND CB.mobile_number = OQ.mobile_number " +
                        "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ") " +
                        "GROUP BY OQ.TOT_STOCK_BILLS, CB.customer_name, CB.gender, CB.spouse_type,  " +
                        "CB.spouse_name, CB.door_number, CB.street, CB.area, CB.city, CB.mobile_number, CB.CUSTOMER_ID  " +
                        "ORDER BY CB.customer_name, CB.spouse_name ";

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

    public DataTable getInActiveValues() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT COALESCE(OQ.TOT_STOCK_BILLS, 0), COUNT(CB.BILL_NUMBER) TOT_OPEND_BILLS, " +
                        "CB.customer_name, CB.gender, CB.spouse_type,  " +
                        "CB.spouse_name, CB.door_number, CB.street, CB.area, CB.city, CB.mobile_number, CB.CUSTOMER_ID  " +
                        "FROM COMPANY_BILLING CB LEFT JOIN  " +
                        " " +
                        "(SELECT DISTINCT COUNT(BILL_NUMBER) TOT_STOCK_BILLS,  " +
                        "customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number, CUSTOMER_ID  " +
                        "FROM COMPANY_BILLING " +
                        "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ")  " +
                        "AND STATUS IN ('OPENED', 'LOCKED') " +
                        "GROUP BY customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number, CUSTOMER_ID  " +
                        "ORDER BY customer_name, spouse_name) OQ " +
                        " " +
                        "ON CB.CUSTOMER_NAME = OQ.CUSTOMER_NAME " +
                        "AND CB.gender = OQ.gender " +
                        "AND CB.spouse_type = OQ.spouse_type " +
                        "AND CB.spouse_name = OQ.spouse_name " +
                        "AND CB.door_number = OQ.door_number " +
                        "AND CB.street = OQ.street " +
                        "AND CB.area = OQ.area " +
                        "AND CB.city = OQ.city " +
                        "AND CB.mobile_number = OQ.mobile_number " +
                        "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ")  " +
                        "AND OQ.CUSTOMER_NAME  IS NULL " +
                        "GROUP BY OQ.TOT_STOCK_BILLS, CB.customer_name, CB.gender, CB.spouse_type,  " +
                        "CB.spouse_name, CB.door_number, CB.street, CB.area, CB.city, CB.mobile_number, CB.CUSTOMER_ID  " +
                        "ORDER BY CB.customer_name, CB.spouse_name ";

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
        
        String sql = "SELECT DISTINCT CONCAT(customer_name, ' ', spouse_type, ' ',  spouse_name, "
                + "' \n\t ', door_number, ', ', street, ' ',  area, ' \n\t ',  mobile_number), "
                + "customer_name, gender, spouse_type, "
                + "spouse_name, door_number, street, area, city, "
                + "mobile_number, COALESCE(customer_status,''), "
                + "mobile_number_2, cust_id_proof_type, cust_id_proof_number, refered_by_name "
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
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
                row.addColumn(rs.getString(15));
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

    public DataTable getCustomerDetails(String customerName, String gender, String spouseType,
            String spouseName, String doorNumber, String street, String area, String city,
            String mobileNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT DISTINCT CONCAT('\t', customer_name, ' ', spouse_type, ' ',  spouse_name, "
                + "' \n\t ', door_number, ', ', street, ' ',  area,"
                + "' \n\t ', city, ', ', mobile_number), "
                + "customer_name, gender, spouse_type, "
                + "spouse_name, door_number, street, area, city, mobile_number, COALESCE(customer_status,''), "
                + "mobile_number_2, cust_id_proof_type, cust_id_proof_number, refered_by_name, customer_id, customer_occupation "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "and customer_name = ? "
                + "and gender = ?::GENDER_TYPE "
                + "and spouse_type = ? "
                + "and spouse_name = ? "
                + "and door_number = ? "
                + "and street = ? "
                + "and area = ? "
                + "and city = ? "
                + "and mobile_number = ? ";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, customerName);
            stmt.setString(3, gender);
            stmt.setString(4, spouseType);
            stmt.setString(5, spouseName);
            stmt.setString(6, doorNumber);
            stmt.setString(7, street);
            stmt.setString(8, area);
            stmt.setString(9, city);
            stmt.setString(10, mobileNumber);
            rs = stmt.executeQuery();

            if(rs.next())
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
    
    public int updateRecord(DataRow fromRow, DataRow toRow) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        String sql = "UPDATE COMPANY_BILLING SET "
                + "CUSTOMER_NAME = ?, "
                + "GENDER = ?::GENDER_TYPE, "
                + "SPOUSE_TYPE = ?, "
                + "SPOUSE_NAME = ?, "
                + "DOOR_NUMBER = ?, "
                + "STREET = ?, "
                + "AREA = ?, "
                + "CITY = ?, "
                + "MOBILE_NUMBER = ?, "
                + "customer_status = ?, "
                + "mobile_number_2 = ?, "
                + "cust_id_proof_type = ?, "
                + "cust_id_proof_number = ?, "
                + "refered_by_name = ?, "
                + "customer_id = ?, "                
                + "customer_occupation = ? "                
                + "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ") "
                + "AND CUSTOMER_NAME = ? "
                + "AND GENDER = ?::GENDER_TYPE "
                + "AND SPOUSE_TYPE = ? "
                + "AND SPOUSE_NAME = ? "
                + "AND DOOR_NUMBER = ? "
                + "AND STREET = ? "
                + "AND AREA = ? "
                + "AND CITY = ? "
                + "AND MOBILE_NUMBER = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);                 
            stmt.setString(1, toRow.getColumn(0).toString());
            stmt.setString(2, toRow.getColumn(1).toString());
            stmt.setString(3, toRow.getColumn(2).toString());
            stmt.setString(4, toRow.getColumn(3).toString());
            stmt.setString(5, toRow.getColumn(4).toString());
            stmt.setString(6, toRow.getColumn(5).toString());
            stmt.setString(7, toRow.getColumn(6).toString());
            stmt.setString(8, toRow.getColumn(7).toString());
            stmt.setString(9, toRow.getColumn(8).toString());
            stmt.setString(10, toRow.getColumn(9).toString());
            stmt.setString(11, toRow.getColumn(10).toString());
            stmt.setString(12, toRow.getColumn(11).toString());
            stmt.setString(13, toRow.getColumn(12).toString());
            stmt.setString(14, toRow.getColumn(13).toString());
            stmt.setString(15, toRow.getColumn(14).toString());
            stmt.setString(16, toRow.getColumn(15).toString());
            
            stmt.setString(17, fromRow.getColumn(1).toString());
            stmt.setString(18, fromRow.getColumn(2).toString());
            stmt.setString(19, fromRow.getColumn(3).toString());
            stmt.setString(20, fromRow.getColumn(4).toString());
            stmt.setString(21, fromRow.getColumn(5).toString());
            stmt.setString(22, fromRow.getColumn(6).toString());
            stmt.setString(23, fromRow.getColumn(7).toString());
            stmt.setString(24, fromRow.getColumn(8).toString());
            stmt.setString(25, fromRow.getColumn(9).toString());

            
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
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

    public int updateReRecord(DataRow fromRow, DataRow toRow) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        String sql = "UPDATE customer_details SET "
                + "CUSTOMER_NAME = ?, "
                + "GENDER = ?::GENDER_TYPE, "
                + "SPOUSE_TYPE = ?, "
                + "SPOUSE_NAME = ?, "
                + "DOOR_NUMBER = ?, "
                + "STREET = ?, "
                + "AREA = ?, "
                + "CITY = ?, "
                + "MOBILE_NUMBER = ?, "
                + "customer_status = ? "
                + "WHERE COMPANY_ID in (" + getCompIdsToShareCustomers() + ") "
                + "AND CUSTOMER_NAME = ? "
                + "AND GENDER = ?::GENDER_TYPE "
                + "AND SPOUSE_TYPE = ? "
                + "AND SPOUSE_NAME = ? "
                + "AND DOOR_NUMBER = ? "
                + "AND STREET = ? "
                + "AND AREA = ? "
                + "AND CITY = ? "
                + "AND MOBILE_NUMBER = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);                 
            stmt.setString(1, toRow.getColumn(0).toString());
            stmt.setString(2, toRow.getColumn(1).toString());
            stmt.setString(3, toRow.getColumn(2).toString());
            stmt.setString(4, toRow.getColumn(3).toString());
            stmt.setString(5, toRow.getColumn(4).toString());
            stmt.setString(6, toRow.getColumn(5).toString());
            stmt.setString(7, toRow.getColumn(6).toString());
            stmt.setString(8, toRow.getColumn(7).toString());
            stmt.setString(9, toRow.getColumn(8).toString());
            stmt.setString(10, toRow.getColumn(9).toString());
            stmt.setString(11, fromRow.getColumn(1).toString());
            stmt.setString(12, fromRow.getColumn(2).toString());
            stmt.setString(13, fromRow.getColumn(3).toString());
            stmt.setString(14, fromRow.getColumn(4).toString());
            stmt.setString(15, fromRow.getColumn(5).toString());
            stmt.setString(16, fromRow.getColumn(6).toString());
            stmt.setString(17, fromRow.getColumn(7).toString());
            stmt.setString(18, fromRow.getColumn(8).toString());
            stmt.setString(19, fromRow.getColumn(9).toString());

            
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    @SuppressWarnings("null")
    public String getId(String screenName, String operationName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, screenName);
            stmt.setString(2, operationName);
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
    
    public boolean saveRecord(String sCustomerId, String sName, 
                                String sGender, 
                                String sSpouseType, String sSpouseName, String sDoorNo, 
                                String sStreetName, String sArea, String sCity, 
                                String sMobileNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO customer_details(" +
"            company_id, customer_id, customer_name, gender, spouse_type, " +
"            spouse_name, door_number, street, area, city, mobile_number)" +
"    VALUES (?, ?, ?, ?::GENDER_TYPE, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sCustomerId);
            stmt.setString(3, sName);
            stmt.setString(4, sGender);
            stmt.setString(5, sSpouseType);
            stmt.setString(6, sSpouseName);
            stmt.setString(7, sDoorNo);
            stmt.setString(8, sStreetName);
            stmt.setString(9, sArea);
            stmt.setString(10, sCity);
            stmt.setString(11, sMobileNumber);
            
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
        
        String sql = "UPDATE OPERATION_ID_GENERATOR "
                + "SET OPERATION_NEXT_ID = ?  "
                + "WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, sNextIdWithPrefix);
            stmt.setString(2, screenName);
            stmt.setString(3, "REPLEDGE");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
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
    
    @SuppressWarnings("null")
    public String getCustomerId() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, "CUSTOMER_DETAILS");
            stmt.setString(2, "CUSTOMER_ID");
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

    public String getCustomerIdFor(String sIdProof, String sIdNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT customer_id FROM company_billing WHERE cust_id_proof_type = ? AND cust_id_proof_number = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sIdProof);
            stmt.setString(2, sIdNumber);
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

    public Customer getCustomerNameBy(String sCustomerId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Customer customer = new Customer();
        
        String sql ="SELECT distinct customer_id, customer_name, gender, spouse_type,  " +
                        "spouse_name, door_number, street, area, city, mobile_number,"
                + "mobile_number_2, cust_id_proof_type, cust_id_proof_number, "
                + "refered_by_name, refered_by_customer_id "
                + "FROM company_billing "
                + "WHERE customer_id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sCustomerId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                customer.setId(rs.getString(1));
                customer.setName(rs.getString(2));
                customer.setGender(rs.getString(3));
                customer.setSpouseType(rs.getString(4));
                customer.setSpouseName(rs.getString(5));
                customer.setDoorNo(rs.getString(6));
                customer.setStreet(rs.getString(7));
                customer.setArea(rs.getString(8));
                customer.setCity(rs.getString(9));
                customer.setMobileNumber(rs.getString(10));
                customer.setMobileNumber2(rs.getString(11));
                customer.setIdProof(rs.getString(12));
                customer.setIdNumber(rs.getString(13));
                customer.setReferredById(rs.getString(14));
                customer.setReferredByName(rs.getString(15));
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return customer;
    }
    
    public boolean setNextCustomerId(String sNextId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR "
                + "SET OPERATION_NEXT_ID = ?  "
                + "WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, sNextId);
            stmt.setString(2, "CUSTOMER_DETAILS");
            stmt.setString(3, "CUSTOMER_ID");
            
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
