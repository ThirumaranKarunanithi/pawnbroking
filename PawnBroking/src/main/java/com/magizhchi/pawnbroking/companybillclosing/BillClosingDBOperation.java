/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillclosing;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
public class BillClosingDBOperation {
    
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
    private File CLOSEcustTemp = new File(tempFile, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
    private File CLOSEjewelTemp = new File(tempFile, CommonConstants.CLOSE_JEWEL_IMAGE_NAME);
    private File CLOSEuserTemp = new File(tempFile, CommonConstants.CLOSE_USER_IMAGE_NAME);
    
    public BillClosingDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(BillClosingDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
           
    public HashMap<String, String> getAllBillingValuesToClose(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(INTEREST, 0), COALESCE(DOCUMENT_CHARGE, 0), "
                + "STATUS, NOTE, COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), REPLEDGE_BILL_ID, REBILLED_TO, REBILLED_FROM, "
                + "accepted_closing_date, nominee_name, customer_copy, id_proof_type, id_proof_number, physical_location, "
                + "CREATED_USER_ID, to_char(created_date, 'dd-MM-YY / HH24:MI:ss') "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
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
                headerValues.put("PURITY", rs.getString(14));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(20)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(21));
                headerValues.put("REBILLED_TO", rs.getString(22));
                headerValues.put("REBILLED_FROM", rs.getString(23));
                if(rs.getDate(24) != null) {
                    headerValues.put("ACCEPTED_CLOSING_DATE", format.format(rs.getDate(24).toLocalDate()));
                }
                headerValues.put("NOMINEE_NAME", rs.getString(25));
                headerValues.put("CUSTOMER_COPY", rs.getString(26));
                headerValues.put("ID_TYPE", rs.getString(27));
                headerValues.put("ID_NUMBER", rs.getString(28));
                headerValues.put("physical_location", rs.getString(29));
                headerValues.put("CREATED_USER_ID", rs.getString(30)); 
                headerValues.put("CREATED_TIME", rs.getString(31)); 
                headerValues.put("CLOSED_USER_ID", "NOT YET CLOSED"); 
                headerValues.put("CLOSED_TIME", "NOT YET CLOSED"); 
                                
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

    public HashMap<String, String> getAllClosedBillingValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), "
                + "COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(INTEREST, 0), COALESCE(DOCUMENT_CHARGE, 0), "
                + "STATUS, NOTE, CLOSING_DATE, COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), "
                + "REPLEDGE_BILL_ID, REBILLED_TO, REBILLED_FROM, GOT_AMOUNT, discount_amount, "
                + "accepted_closing_date, NOMINEE_NAME, customer_copy, id_proof_type, id_proof_number, physical_location, "
                + "CREATED_USER_ID, to_char(created_date, 'dd-MM-YY / HH24:MI:ss'), "
                + "closed_user_id, to_char(closed_date, 'dd-MM-YY / HH24:MI:ss') "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') "
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
                headerValues.put("GROSS_WEIGHT", rs.getString(12));
                headerValues.put("NET_WEIGHT", rs.getString(13));
                headerValues.put("PURITY", rs.getString(14));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(20).toLocalDate()));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(21)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(22));
                headerValues.put("REBILLED_TO", rs.getString(23));
                headerValues.put("REBILLED_FROM", rs.getString(24));
                headerValues.put("GOT_AMOUNT", Double.toString(rs.getDouble(25)));
                headerValues.put("DISCOUNT_AMOUNT", Double.toString(rs.getDouble(26)));
                if(rs.getDate(27) != null) {
                    headerValues.put("ACCEPTED_CLOSING_DATE", format.format(rs.getDate(27).toLocalDate()));
                }
                headerValues.put("NOMINEE_NAME", rs.getString(28));
                headerValues.put("CUSTOMER_COPY", rs.getString(29));
                headerValues.put("ID_TYPE", rs.getString(30));
                headerValues.put("ID_NUMBER", rs.getString(31));
                headerValues.put("physical_location", rs.getString(32));
                headerValues.put("CREATED_USER_ID", rs.getString(33)); 
                headerValues.put("CREATED_TIME", rs.getString(34)); 
                headerValues.put("CLOSED_USER_ID", rs.getString(35)); 
                headerValues.put("CLOSED_TIME", rs.getString(36)); 
                                
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
    
    public HashMap<String, Image> getOpenImages(String sBillNumber, String sMaterialType, boolean isToClose) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, Image> headerValues = new HashMap<>();
        
        String sql = "SELECT open_customer_image, open_jewel_image, open_user_image "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? ";
        
        if(isToClose) {
            sql = sql + "AND STATUS IN ('OPENED', 'LOCKED', 'CANCELED') ";
        } else {
            sql = sql + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') ";
        }                


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
                        GoldBillClosingController.isOpenCustomerImgAvailable = true;
                    } else {
                        SilverBillClosingController.isOpenCustomerImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isOpenCustomerImgAvailable = false;
                    } else {
                        SilverBillClosingController.isOpenCustomerImgAvailable = false;
                    }
                }

                byte jewelBuf[] = rs.getBytes(2);
                if(jewelBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(jewelBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("JEWEL_IMG", SwingFXUtils.toFXImage(bImage, null));

                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isOpenJewelImgAvailable = true;
                    } else {
                        SilverBillClosingController.isOpenJewelImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isOpenJewelImgAvailable = false;
                    } else {
                        SilverBillClosingController.isOpenJewelImgAvailable = false;
                    }
                }

                byte userBuf[] = rs.getBytes(3);
                if(userBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(userBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("USER_IMG", SwingFXUtils.toFXImage(bImage, null));

                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isOpenUserImgAvailable = true;
                    } else {
                        SilverBillClosingController.isOpenUserImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isOpenUserImgAvailable = false;
                    } else {
                        SilverBillClosingController.isOpenUserImgAvailable = false;
                    }
                }                
                return headerValues;		       
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BillClosingDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BillClosingDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return null;
    }

    public HashMap<String, Image> getClosedImages(String sBillNumber, String sMaterialType, boolean isToClose) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, Image> headerValues = new HashMap<>();
        
        String sql = "SELECT close_customer_image, close_jewel_image, close_user_image "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? ";

        if(isToClose) {
            sql = sql + "AND STATUS IN ('OPENED', 'LOCKED', 'CANCELED') ";
        } else {
            sql = sql + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') ";
        }                


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
                        GoldBillClosingController.isCloseCustomerImgAvailable = true;
                    } else {
                        SilverBillClosingController.isCloseCustomerImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isCloseCustomerImgAvailable = false;
                    } else {
                        SilverBillClosingController.isCloseCustomerImgAvailable = false;
                    }
                }

                byte jewelBuf[] = rs.getBytes(2);
                if(jewelBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(jewelBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("JEWEL_IMG", SwingFXUtils.toFXImage(bImage, null));

                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isCloseJewelImgAvailable = true;
                    } else {
                        SilverBillClosingController.isCloseJewelImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isCloseJewelImgAvailable = false;
                    } else {
                        SilverBillClosingController.isCloseJewelImgAvailable = false;
                    }
                }

                byte userBuf[] = rs.getBytes(3);
                if(userBuf != null) {   
                    ByteArrayInputStream bis = new ByteArrayInputStream(userBuf);
                    BufferedImage bImage = ImageIO.read(bis);   
                    headerValues.put("USER_IMG", SwingFXUtils.toFXImage(bImage, null));

                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isCloseUserImgAvailable = true;
                    } else {
                        SilverBillClosingController.isCloseUserImgAvailable = true;
                    }
                } else {
                    if(sMaterialType.equals("GOLD")) {
                        GoldBillClosingController.isCloseUserImgAvailable = false;
                    } else {
                        SilverBillClosingController.isCloseUserImgAvailable = false;
                    }
                }                
                return headerValues;		       
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BillClosingDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BillClosingDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return null;
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
    
    public String getInterestType() throws SQLException
    {

        connectDB();
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
            roleMasterConn.close();
        }        
        return null;
    }

    public String[] getReduceOrMinimumDaysOrMonths(String sMaterialType, String sType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[2];
        
        String sql ="SELECT COALESCE(DAYS_OR_MONTHS, 0), REDUCTION_TYPE "
                + "FROM COMPANY_REDUCE_MONTHS_OR_DAYS "
                + "WHERE COMPANY_ID = ? " 
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
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

    public String getCardLostCharge() throws SQLException
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
            roleMasterConn.close();
        } 

        return sFilePath;	
    }
    
    public String[] getFineCharges(double month, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[5];
        
        String sql ="SELECT interest_type, month_days_from, month_days_to, charged_interest, calculation_method "
                + "FROM fine_charges "
                + "WHERE company_id = ? "
                + "AND ? BETWEEN month_days_from AND month_days_to "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDouble(2, month);      
            stmt.setString(3, sMaterialType);      
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                data[0] = rs.getString(1);		                   
                data[1] = Double.toString(rs.getDouble(2));
                data[2] = Double.toString(rs.getDouble(3));
                data[3] = Double.toString(rs.getDouble(4));
                data[4] = rs.getString(5);		                   
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

    public String getFineStartingMonth(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT month_days_from "
                + "FROM fine_charges "
                + "WHERE company_id = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "ORDER BY jewel_material_type, month_days_from";
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
            if(rs!=null && !rs.isClosed())
                rs.close();
            if(!stmt.isClosed())
                stmt.close();
            roleMasterConn.close();
        }        
        return null;
    }
    
    public double getRemainingDaysAsMonths(String sDate, double iRemainingDays, String sMaterialType) throws SQLException
    {

        connectDB();
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
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return 0;
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
            roleMasterConn.close();
        }        
        return "0";
    }
    
    public boolean closeBill(String sBillNumber, String sReBilledTo, String sInterestType, String sBillCosingDate, 
                            String sTotalDaysOrMonths, int iMinimumDaysOrMonths, int iReduceDaysOrMonths, 
                            double dTakenDaysOrMonths, double dCloseTakenAmount, double dToGetAmount, 
                            double dGotAmount, String sStatus, String sNote, String sReduceType, String sMinimumType, 
                            String sMaterialType, double dNoticeCharge, double dFineInterest, double dFineCharge,
                            double dOtherCharges, double dDiscount,
                            String sCustomerCopy, String sIdType, String sIdNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        boolean val = false;
                
        java.sql.Date sqlDateBillClosing = java.sql.Date.valueOf(LocalDate.parse(sBillCosingDate, CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "INTEREST_TYPE = ?::INTEREST_TYPE, "
                    + "CLOSING_DATE = ?, "
                    + "TOTAL_DAYS_OR_MONTHS = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS = ?, "
                    + "REDUCE_DAYS_OR_MONTHS = ?, "
                    + "TAKEN_DAYS_OR_MONTHS = ?, "
                    + "CLOSE_TAKEN_AMOUNT = ?, "
                    + "TOGET_AMOUNT = ?, "
                    + "GOT_AMOUNT = ?, "
                    + "STATUS = ?::COMPANY_BILL_STATUS, "
                    + "NOTE = ?, "
                    + "REDUCE_DAYS_OR_MONTHS_TYPE = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS_TYPE = ?, "
                    + "CLOSED_USER_ID = ?, "
                    + "CLOSED_DATE = NOW(), "
                    + "REBILLED_TO = ?, "
                    + "notice_charge_amount = ?, "
                    + "fine_interest_taken = ?, "
                    + "fine_charge_amount = ?, "
                    + "total_other_charges = ?, "
                    + "discount_amount = ?, "
                    + "customer_copy = ?, "
                    + "id_proof_type = ?, "
                    + "id_proof_number = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('OPENED', 'LOCKED') "
                    + "AND BILL_NUMBER = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sInterestType);
            stmt.setDate(2, sqlDateBillClosing);
            stmt.setString(3, sTotalDaysOrMonths);
            stmt.setInt(4, iMinimumDaysOrMonths);
            stmt.setInt(5, iReduceDaysOrMonths);
            stmt.setDouble(6, dTakenDaysOrMonths);
            stmt.setDouble(7, dCloseTakenAmount);
            stmt.setDouble(8, dToGetAmount);
            stmt.setDouble(9, dGotAmount);
            stmt.setString(10, sStatus);
            stmt.setString(11, sNote);
            stmt.setString(12, sReduceType);
            stmt.setString(13, sMinimumType);
            stmt.setString(14, CommonConstants.USERID);
            stmt.setString(15, sReBilledTo); 
            stmt.setDouble(16, dNoticeCharge); 
            stmt.setDouble(17, dFineInterest); 
            stmt.setDouble(18, dFineCharge); 
            stmt.setDouble(19, dOtherCharges); 
            stmt.setDouble(20, dDiscount); 
            stmt.setString(21, sCustomerCopy);
            stmt.setString(22, sIdType);
            stmt.setString(23, sIdNumber);            
            stmt.setString(24, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(25, sMaterialType); 
            stmt.setString(26, sBillNumber);            
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

    public boolean updateBill(String sBillNumber, String sInterestType, String sBillCosingDate, 
                            String sTotalDaysOrMonths, int iMinimumDaysOrMonths, int iReduceDaysOrMonths, 
                            double dTakenDaysOrMonths, double dCloseTakenAmount, double dToGetAmount, 
                            double dGotAmount, String sStatus, String sNote, String sReduceType, String sMinimumType,
                            String sMaterialType, double dNoticeCharge, double dFineInterest, double dFineCharge,
                            double dOtherCharges, double dDiscount,
                            String sCustomerCopy, String sIdType, String sIdNumber) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        boolean val = false;               
        java.sql.Date sqlDateBillClosing = java.sql.Date.valueOf(LocalDate.parse(sBillCosingDate, CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "INTEREST_TYPE = ?::INTEREST_TYPE, "
                    + "CLOSING_DATE = ?, "
                    + "TOTAL_DAYS_OR_MONTHS = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS = ?, "
                    + "REDUCE_DAYS_OR_MONTHS = ?, "
                    + "TAKEN_DAYS_OR_MONTHS = ?, "
                    + "CLOSE_TAKEN_AMOUNT = ?, "
                    + "TOGET_AMOUNT = ?, "
                    + "GOT_AMOUNT = ?, "
                    + "STATUS = ?::COMPANY_BILL_STATUS, "
                    + "NOTE = ?, "
                    + "REDUCE_DAYS_OR_MONTHS_TYPE = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS_TYPE = ?, "                
                    + "CLOSED_USER_ID = ?, "
                    + "CLOSED_DATE = NOW(), "
                    + "notice_charge_amount = ?, "
                    + "fine_interest_taken = ?, "
                    + "fine_charge_amount = ?, "
                    + "total_other_charges = ?, "
                    + "discount_amount = ?, "  
                    + "customer_copy = ?, "  
                    + "id_proof_type = ?, " 
                    + "id_proof_number = ? "                                    
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') "
                    + "AND BILL_NUMBER = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sInterestType);
            stmt.setDate(2, sqlDateBillClosing);
            stmt.setString(3, sTotalDaysOrMonths);
            stmt.setInt(4, iMinimumDaysOrMonths);
            stmt.setInt(5, iReduceDaysOrMonths);
            stmt.setDouble(6, dTakenDaysOrMonths);
            stmt.setDouble(7, dCloseTakenAmount);
            stmt.setDouble(8, dToGetAmount);
            stmt.setDouble(9, dGotAmount);
            stmt.setString(10, sStatus);
            stmt.setString(11, sNote);
            stmt.setString(12, sReduceType);
            stmt.setString(13, sMinimumType);
            stmt.setString(14, CommonConstants.USERID);
            stmt.setDouble(15, dNoticeCharge); 
            stmt.setDouble(16, dFineInterest); 
            stmt.setDouble(17, dFineCharge); 
            stmt.setDouble(18, dOtherCharges); 
            stmt.setDouble(19, dDiscount);             
            stmt.setString(20, sCustomerCopy); 
            stmt.setString(21, sIdType); 
            stmt.setString(22, sIdNumber); 
            stmt.setString(23, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(24, sMaterialType); 
            stmt.setString(25, sBillNumber);
            val =  stmt.executeUpdate() >= 1;
            return val;
        } catch (SQLException e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean unCloseBill(String sBillNumber, String sInterestType, String sBillCosingDate, 
                            String sTotalDaysOrMonths, int iMinimumDaysOrMonths, int iReduceDaysOrMonths, 
                            double dTakenDaysOrMonths, double dCloseTakenAmount, double dToGetAmount, 
                            double dGotAmount, String sStatus, String sReduceType, String sMinimumType,
                            String sMaterialType, double dNoticeCharge, double dFineInterest, double dFineCharge,
                            double dOtherCharges, double dDiscount) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "INTEREST_TYPE = ?::INTEREST_TYPE, "
                    + "CLOSING_DATE = ?, "
                    + "TOTAL_DAYS_OR_MONTHS = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS = ?, "
                    + "REDUCE_DAYS_OR_MONTHS = ?, "
                    + "TAKEN_DAYS_OR_MONTHS = ?, "
                    + "CLOSE_TAKEN_AMOUNT = ?, "
                    + "TOGET_AMOUNT = ?, "
                    + "GOT_AMOUNT = ?, "
                    + "STATUS = ?::COMPANY_BILL_STATUS, "
                    + "REDUCE_DAYS_OR_MONTHS_TYPE = ?, "
                    + "MINIMUM_DAYS_OR_MONTHS_TYPE = ?, "                
                    + "CLOSED_USER_ID = ?, "
                    + "CLOSED_DATE = NOW(), "
                    + "notice_charge_amount = ?, "
                    + "fine_interest_taken = ?, "
                    + "fine_charge_amount = ?, "
                    + "total_other_charges = ?, "
                    + "discount_amount = ?, "
                    + "rebilled_to = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') "
                    + "AND BILL_NUMBER = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sInterestType);
            stmt.setDate(2, null);
            stmt.setString(3, sTotalDaysOrMonths);
            stmt.setInt(4, iMinimumDaysOrMonths);
            stmt.setInt(5, iReduceDaysOrMonths);
            stmt.setDouble(6, dTakenDaysOrMonths);
            stmt.setDouble(7, dCloseTakenAmount);
            stmt.setDouble(8, dToGetAmount);
            stmt.setDouble(9, dGotAmount);
            stmt.setString(10, sStatus);
            stmt.setString(11, sReduceType);
            stmt.setString(12, sMinimumType);
            stmt.setString(13, CommonConstants.USERID);
            stmt.setDouble(14, dNoticeCharge); 
            stmt.setDouble(15, dFineInterest); 
            stmt.setDouble(16, dFineCharge); 
            stmt.setDouble(17, dOtherCharges); 
            stmt.setDouble(18, dDiscount);     
            stmt.setString(19, null);
            stmt.setString(20, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(21, sMaterialType); 
            stmt.setString(22, sBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAllDetailsValues(
            String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        //String[] billRowAndNumber = getGoldCurrentBillNumber();
            
        
        String sql = "SELECT BILL_NUMBER, CLOSING_DATE, AMOUNT, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "STREET, AREA, MOBILE_NUMBER, ITEMS, GROSS_WEIGHT, NET_WEIGHT, PURITY, "
                + "STATUS, NOTE "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
                //+ "TO_NUMBER(REGEXP_REPLACE("
                //+ "BILL_NUMBER, 'D', '', 'g'), '99G999D9S') BILL  "

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += "ORDER BY CLOSING_DATE DESC ";

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);

            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("GENDER =")) {
                        stmt.setString(i+3, sVals[i]);
                    } else if(sFilterScript.contains("STATUS =")) {
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
    
    public DataTable getOtherSettingsValues(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ALLOW_TO_CHANGE_BILL_CLOSING_DATE, allow_to_change_bill_closing_received_amount, "
                + "customer_camera_name, jewel_camera_name, user_camera_name, verify_gbc_copies "
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
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getBoolean(6));
                
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
    
    public boolean isReadyToDeliver(String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        
        String sql = "SELECT RB.REPLEDGE_NAME, RB.STATUS " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.STATUS IN ('RECEIVED') " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND RB.REPLEDGE_BILL_ID = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeBillId);
            rs = stmt.executeQuery();
            
            if(rs.next() || sRepledgeBillId == null)
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
    
    public String getLastDuePaidDate(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT PAID_DATE " +
                    "FROM COMPANY_ADVANCE_AMOUNT " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND BILL_NUMBER = ? " +
                    "ORDER BY PAID_DATE DESC " +
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

    public boolean deleteCompanyBillDebitTable(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "DELETE FROM company_bill_debit " +
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

    public boolean deleteCompanyBillCreditTable(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "DELETE FROM company_bill_credit " +
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

    public boolean deleteCompanyBillingTable(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "DELETE FROM company_billing " +
                    "WHERE jewel_material_type = ?::MATERIAL_TYPE "
                + "AND bill_number = ?";
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
    
    public boolean updateAADate(String sBillNumber, String sMaterialType, String sDate, double dTotalPaidAmount) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE company_advance_amount SET paid_date=? "
                + "WHERE company_id = ? "
                + "and bill_number = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "and total_amount = ?";
        
        try {
            
            java.sql.Date sqlDateBillClosing = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setDate(1, sqlDateBillClosing);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sBillNumber);
            stmt.setString(4, sMaterialType);
            stmt.setDouble(5, dTotalPaidAmount);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getCreditTableValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, CREDITED_DATE, TO_BE_CREDITED_AMOUNT, CREDITED_AMOUNT "
                + "FROM COMPANY_BILL_CREDIT "
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

    public DataTable getDebitTableValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "ID, DEBITTED_DATE, TO_BE_DEBITTED_AMOUNT, DEBITTED_AMOUNT "
                + "FROM COMPANY_BILL_DEBIT "
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
                + "WHERE CB.COMPANY_ID = ? "
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
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, headerValues.get("CUSTOMER_NAME"));
            stmt.setString(3, headerValues.get("GENDER"));
            stmt.setString(4, headerValues.get("SPOUSE_TYPE"));
            stmt.setString(5, headerValues.get("SPOUSE_NAME"));
            stmt.setString(6, headerValues.get("DOOR_NUMBER"));
            stmt.setString(7, headerValues.get("STREET"));
            stmt.setString(8, headerValues.get("AREA"));
            stmt.setString(9, headerValues.get("CITY"));
            stmt.setString(10, headerValues.get("MOBILE_NUMBER"));
            stmt.setString(11, headerValues.get("BILL_NUMBER"));
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
    
    public String[] getDenominationMultiBillNumbers(String sOperation, String sBillNumber, String sMaterialType) throws SQLException
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
    
    public double getRebilledToVals(String sMaterialType, String sBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        
        String sql = "select amount "
                + "from company_billing "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE "
                + "AND bill_number = ? ";
        
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);

            rs = stmt.executeQuery();

            while(rs.next())
            {	
                return rs.getDouble(1);
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
    
    public DataTable getAllCompanyNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CONCAT(c.id, ' - ',  c.name), c.status, c.id, a.todays_date, c.name, c.type "
                + "FROM company c, company_todays_account a "
                + "where a.company_id = c.id "
                + "AND REF_MARK = 'L' ";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
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

    public boolean updateAllToRestStatus() throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update company set status = ?::COMPANY_STATUS";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);             
            stmt.setString(1, CommonConstants.REST);
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateActiveCompany(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update company set status = ?::COMPANY_STATUS where id = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE);
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
    
    public HashMap<String, String> getRebilledFromVals(String sMaterialType, String sBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "select SUM(total_advance_amount_paid), SUM(close_taken_amount), SUM(amount), "
                + "SUM(total_other_charges), SUM(discount_amount), SUM(got_amount), SUM(GROSS_WEIGHT) "
                + "from company_billing "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE "
                + "AND rebilled_to = ?";
                
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                headerValues.put("total_advance_amount_paid", Double.toString(rs.getDouble(1)));
                headerValues.put("close_taken_amount", Double.toString(rs.getDouble(2)));
                headerValues.put("amount", Double.toString(rs.getDouble(3)));
                headerValues.put("total_other_charges", Double.toString(rs.getDouble(4)));
                headerValues.put("discount_amount", Double.toString(rs.getDouble(5)));
                headerValues.put("got_amount", Double.toString(rs.getDouble(6)));
                headerValues.put("gross_weight", Double.toString(rs.getDouble(7)));
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

    public String getRebilledFromAmt(String sMaterialType, String sBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        String sql = "select AMOUNT "
                + "from company_billing "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE "
                + "AND bill_number = ?";
                
        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                return Double.toString(rs.getDouble(1));
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

        return "0";	
    }
    
    public String getOpenFormula(String sDate, double dAmount, String sMaterialType) throws SQLException
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
    
    public String getBillStatus(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null; 
        String sFilePath = "";
        
        String sql = "SELECT STATUS "
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
    
    public boolean[] getAddViewUpdate(String sRoleId, String sTabName, String sScreenName) throws SQLException
    {

        boolean[] addViewUpdate = new boolean[3];
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_add, allow_view, allow_update " +
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
                addViewUpdate[0] = rs.getBoolean(1);
                addViewUpdate[1] = rs.getBoolean(2);
                addViewUpdate[2] = rs.getBoolean(3);
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

    public String getBillClosedDate(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT CLOSING_DATE "
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

    public boolean saveVerifications(boolean isCustVerified, boolean isCompVerified, 
            boolean isPackVerified, boolean isCardLost, 
            String sClosedBy, String sRelationToClosedBy, String sMaterialType, String sBillNumber) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "cust_copy_verifed = ?, "
                    + "comp_copy_verifed = ?, "
                    + "pack_copy_verifed = ?, "
                    + "is_card_lost_bond_printed = ?, "
                    + "closed_by = ?, "
                    + "relation_to_closed_by = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', "
                + "'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE')  "
                    + "AND BILL_NUMBER = ? ";
        
        try {
            
            PreparedStatement stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setBoolean(1, isCustVerified);
            stmt.setBoolean(2, isCompVerified);
            stmt.setBoolean(3, isPackVerified);
            stmt.setBoolean(4, isCardLost);
            stmt.setString(5, sClosedBy);
            stmt.setString(6, sRelationToClosedBy);
            stmt.setString(7, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(8, sMaterialType);
            stmt.setString(9, sBillNumber);
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

    public DataTable getVerificationValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DataTable dataTable = new DataTable();
        
        String sql = "SELECT cust_copy_verifed, comp_copy_verifed, pack_copy_verifed, "
                + "is_card_lost_bond_printed, closed_by, relation_to_closed_by, "
                + "id_proof_type, id_proof_number "
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

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getBoolean(1));
                row.addColumn(rs.getBoolean(2));
                row.addColumn(rs.getBoolean(3));
                row.addColumn(rs.getBoolean(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                dataTable.add(row);                	       
            }		                
            
        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return dataTable;
    }

    public boolean updateStatus(String sBillNumber, String sStatus, 
            String sPhysicalLoc, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "STATUS = ?::COMPANY_BILL_STATUS, "
                    + "physical_location = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND BILL_NUMBER = ? ";
        
        try {
            
            PreparedStatement stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sStatus);
            stmt.setString(2, sPhysicalLoc);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, sMaterialType);
            stmt.setString(5, sBillNumber);
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
    
    public boolean updateCompanyBillPhysicalLocation(String sBillNumber, 
            String sMaterialType, String sPhysicalLocation) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_BILLING SET "
                + "physical_location = ? "                
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND bill_number = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sPhysicalLocation);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);
            stmt.setString(4, sBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }    
}
