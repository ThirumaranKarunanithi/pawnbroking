/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.io.File;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;

/**
 *
 * @author Tiru
 */
public class CommonConstants {
    
    public static final String SOFTWARE_ID = "1";
    public static final String ENGLISH = "ENGLISH";
    public static final String TAMIL = "TAMIL";
    public static final String FONT = "Arial";
    
    public static final MachineDetails tiruNewMachine = 
            new MachineDetails("TIRU", "E0-70-EA-C4-98-FA", 
                    "02-04-2100", 3, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails tiruNewMachine1 = 
            new MachineDetails("TIRU", "DC-21-48-80-84-FB", 
                    "02-04-2100", 3, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");    
    public static final MachineDetails tiruMachine = 
            new MachineDetails("ALWARPURAM", "B0-5A-DA-E6-95-25", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails karumbalaiMachine = 
            new MachineDetails("KARUMBALAI", "98-28-A6-45-A2-89", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails aiyanarpuramMachine = 
            new MachineDetails("AIYANARPURAM", "E6-AA-EA-E3-1F-1F", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails iravathanallurMachine = 
            new MachineDetails("IRAVATHANALLUR", "74-04-F1-01-61-C1", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails annanagarMachine = 
            new MachineDetails("ANNANAGAR", "92-E8-68-EE-A7-AD", 
                    "02-04-2100", 3, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails neelPersonalMachine = 
            new MachineDetails("ANNANAGAR", "F8-16-54-4A-3D-9C", 
                    "02-04-2100", 3, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails neelOfficeMachine = 
            new MachineDetails("ANNANAGAR", "8C-47-BE-50-6C-18", 
                    "02-04-2100", 3, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails annanagarOfficeLap = 
            new MachineDetails("ANNANAGAR", "00-FF-45-EE-EA-E5", 
                    "02-04-2100", 3, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails subashiniMachine = 
            new MachineDetails("SUBASHINI", "30-24-A9-40-41-3A", 
                    "02-04-2100", 2, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails manonmaniMachine = 
            new MachineDetails("MANONMANI", "00-FF-80-9E-99-E2", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails manonmaniMachine2 = 
            new MachineDetails("MANONMANI", "D0-DF-9A-E5-92-39", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    public static final MachineDetails pandiSalaigramam = 
            new MachineDetails("SALAIGRAMAM", "A0-E7-0B-BF-2F-94", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");   
    /*public static final MachineDetails dinakaranEllisNagar = 
            new MachineDetails("DINAKARAN", "B0-83-FE-70-90-A4", 
                    "02-04-2022", 1, Boolean.FALSE, 
                    ENGLISH, "Arial");// PANDI ANNA machine*/
    public static final MachineDetails annaduraiKRCMachine = 
            new MachineDetails("ANNADURAIKRC", "00-E0-1C-29-A3-48", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");     
    public static final MachineDetails govindMachine = 
            new MachineDetails("GOVIND", "00-FF-9A-1C-FF-16", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");     
    public static final MachineDetails venkateshAiyanarMachine = 
            new MachineDetails("VENKAT", "00-FF-2F-1E-53-10", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");     
    public static final MachineDetails maruthuSathyaNagar = 
            new MachineDetails("MARUTHUSATHYANAGAR", "00-FF-1A-D9-79-16", 
                    "02-04-2025", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");     
    public static final MachineDetails bagavathyfinance = 
            new MachineDetails("BAGAVATHY", "00-FF-F7-FC-15-31", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");     
    public static final MachineDetails maniDubaiLap = 
            new MachineDetails("ANNANAGAR", "C8-CB-9E-79-E0-3D", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy");     
    public static final MachineDetails senbagapriyaPalanganatham = 
            new MachineDetails("ANGALAPARAMESWARI", "1C-A0-B8-74-21-9B", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
    public static final MachineDetails lakshmiNarayanan = 
            new MachineDetails("lakshmi", "", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
    public static final MachineDetails suganya = 
            new MachineDetails("suganya", "00-FF-AF-4F-89-E5", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
    public static final MachineDetails balamurugan = 
            new MachineDetails("BALAMURUGAN", "D2-39-57-A8-FB-6D", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
    public static final MachineDetails pandiNathan = 
            new MachineDetails("PANDINATHAN", "3A-8D-3D-7A-D7-88", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
    public static final MachineDetails pandiNathan1 = 
            new MachineDetails("PANDINATHAN", "38-8D-3D-7A-D7-88", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
   public static final MachineDetails baskaran = 
            new MachineDetails("BASKARAN", "3A-8D-3D-50-A1-7A", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
   public static final MachineDetails baskaran1 = 
            new MachineDetails("BASKARAN", "38-8D-3D-50-A1-7A", 
                    "02-04-2100", 1, Boolean.FALSE, 
                    ENGLISH, FONT, "happy"); 
    
    public static final MachineDetails[] softwareOnMachines = {tiruNewMachine, tiruNewMachine1, tiruMachine,  
        karumbalaiMachine, annanagarMachine, aiyanarpuramMachine, iravathanallurMachine, 
        neelPersonalMachine, neelOfficeMachine, 
        subashiniMachine, manonmaniMachine, manonmaniMachine2, pandiSalaigramam, 
        annaduraiKRCMachine, annanagarOfficeLap, govindMachine, 
        venkateshAiyanarMachine, maruthuSathyaNagar, bagavathyfinance, maniDubaiLap, 
        senbagapriyaPalanganatham, lakshmiNarayanan, suganya, 
        balamurugan, pandiNathan, pandiNathan1, baskaran, baskaran1};
    
    private static final Rectangle2D BOUNDS = Screen.getPrimary().getVisualBounds();
    
    public static final double SCREEN_WIDTH = 1366;
    public static final double SCREEN_HEIGHT = 768;
    public static final double SCREEN_X = (BOUNDS.getWidth() - SCREEN_WIDTH) / 2;
    public static final double SCREEN_Y = (BOUNDS.getHeight() - SCREEN_HEIGHT) / 2;
    
    public static final String DB = "postgresql";
    public static final String IP = "localhost";
    public static final String PORT = "5432";
    public static final String SCHEMA = "pawnbroking";
    public static final String DB_USERNAME = "postgres";
    public static final String DB_PASSWORD = "happy";    
    
    public static final String TIRU = "TIRU";
    public static final String TIRU_USERNAME = "TIRU";
    public static final String TIRU_PASSWORD = Util.getShortPassWordFor("HAPPY@", "ALWAYS");
    
    public static final String TEMP_LOCATION = System.getProperty("java.io.tmpdir") + File.separator + "pawn";
    public static final String TEMP_FILE_SUFFIX = DateTimeFormatter.ofPattern("yyMMdd").format(LocalDate.now());
    public static final String MD5_PASSWORD = "tiru";
    public static final String ALWARPURAM_FROM_MAIL_ID = "alwarpuramrbmachine@gmail.com";
    public static final String ALWARPURAM_FROM_MAIL_PASSWORD = "9787315187";
    public static final String MANONMANI_FROM_MAIL_ID = "manonmanipbmachine@gmail.com";
    public static final String MANONMANI_FROM_MAIL_PASSWORD = "9787315187";
    public static final String AYIRAVATHANALLUR_FROM_MAIL_ID = "alwarpuramrbmachine@gmail.com";
    public static final String AYIRAVATHANALLUR_FROM_MAIL_PASSWORD = "9787315187";
    
    public static String USERID;
    public static String EMPID;
    public static String EMP_NAME;
    public static String ROLEID;
    public static String ACTIVE_COMPANY_ID;
    public static String ACTIVE_COMPANY_NAME;
    public static String ACTIVE_COMPANY_TYPE;
    public static String ACTIVE_COMPANY_ACC_STARTING_DATE;
    public static String ACTIVE_COMPANY_ACC_LAST_DATE;
    public static MachineDetails ACTIVE_MACHINE;
    
    public static final String FIlE_DATE_FORMAT = "ddMMyyyy";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String SHORT_DATE_FORMAT = "dd-MM-yy";
    public static final String SHORT_MONTH_FORMAT = "MM";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_WITH_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter MONTHFORMATTER = DateTimeFormatter.ofPattern(CommonConstants.SHORT_MONTH_FORMAT);  
    public static final DateTimeFormatter DATETIMEFORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT); 
    public static final DateTimeFormatter DATEWITHTIMEFORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DATE_WITH_TIME_FORMAT); 
    public static final DateTimeFormatter DBDATETIMEFORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT);    
    public static final DateTimeFormatter FILEDATETIMEFORMATTER = DateTimeFormatter.ofPattern(CommonConstants.FIlE_DATE_FORMAT);    
    
    public static final String CMP_ID_PREFIX = "CMP";
    public static final String REP_ID_PREFIX = "REP";
    public static final String REP_BILL_ID_PREFIX = "REPBILL";
    public static final String EMP_ID_PREFIX = "EMP";
    public static final String ROL_ID_PREFIX = "ROL";
    public static final String USR_ID_PREFIX = "USR";
    
    public static final String EMP_DEBIT_DA_PREFIX = "DEBDA";
    public static final String EMP_DEBIT_AA_PREFIX = "DEBAA";
    public static final String EMP_DEBIT_SA_PREFIX = "DEBSA";
    public static final String EMP_DEBIT_OA_PREFIX = "DEBOA";
    
    public static final String COMP_DEBIT_CB_PREFIX = "DEBCB";
    public static final String COMP_DEBIT_CO_PREFIX = "DEBCO";

    public static final String REP_DEBIT_RB_PREFIX = "DEBRB";
    public static final String REP_DEBIT_RO_PREFIX = "DEBRO";
    
    public static final String EMP_CREDIT_AA_PREFIX = "CREAA";
    public static final String EMP_CREDIT_OA_PREFIX = "CREOA";

    public static final String COMP_CREDIT_CB_PREFIX = "CRECB";
    public static final String COMP_CREDIT_CO_PREFIX = "CRECO";    

    public static final String REP_CREDIT_RB_PREFIX = "CRERB";
    public static final String REP_CREDIT_RO_PREFIX = "CRERO";
    
    public static final String CUSTOMER_ID_PREFIX = "CUST";
    
    public static final String MASTER_TAB = "MASTER";
    public static final String OPERATION_TAB = "OPERATION";
    public static final String SPECIAL_OPTIONS_TAB = "SPECIAL OPTIONS";
    public static final String REPORTS_TAB = "REPORTS";
    
    public static final String COMPANY_MODULE_SCREEN = "COMPANY MODULE";
    public static final String REPLEDGE_MODULE_SCREEN = "RE-PLEDGE MODULE";
    public static final String EMPLOYEE_MODULE_SCREEN = "EMPLOYEE MODULE";
    public static final String JEWEL_ITEM_MODULE_SCREEN = "JEWEL ITEM MODULE";
    public static final String BILL_EDIT_OPERATION_SCREEN = "BILL EDIT OPERATION";
    public static final String ROLE_MODULE_SCREEN = "ROLE MODULE";
    public static final String USER_MODULE_SCREEN = "USER MODULE";
    
    public static final String TODAYS_ACCOUNT_SCREEN = "TODAYS ACCOUNT";
    public static final String REPLEDGE_BILL_OPENING_SCREEN = "RE-PLEDGE BILL OPENING";
    public static final String REPLEDGE_BILL_CLOSING_SCREEN = "RE-PLEDGE BILL CLOSING";
    public static final String SILVER_BILL_OPENING_SCREEN = "SILVER BILL OPENING";
    public static final String SILVER_BILL_CLOSING_SCREEN = "SILVER BILL CLOSING";
    public static final String SILVER_ADVANCE_AMOUNT_SCREEN = "SILVER ADVANCE AMOUNT RECEIPT";
    public static final String GOLD_BILL_OPENING_SCREEN = "GOLD BILL OPENING";
    public static final String GOLD_BILL_CLOSING_SCREEN = "GOLD BILL CLOSING";    
    public static final String GOLD_ADVANCE_AMOUNT_SCREEN = "GOLD ADVANCE AMOUNT RECEIPT";
    public static final String EMPLOYEE_EXPENSES_SCREEN = "EMPLOYEE EXPENSES";
    public static final String COMPANY_EXPENSES_SCREEN = "COMPANY EXPENSES";
    public static final String REPLEDGE_EXPENSES_SCREEN = "RE-PLEDGE EXPENSES";
    public static final String EMPLOYEE_INCOME_SCREEN = "EMPLOYEE INCOME";
    public static final String COMPANY_INCOME_SCREEN = "COMPANY INCOME";
    public static final String REPLEDGE_INCOME_SCREEN = "RE-PLEDGE INCOME";
    public static final String BILL_CALCULATOR = "BILL CALCULATOR";  
    public static final String JEWEL_ACCOUNT_TAB = "JEWEL ACCOUNT TAB";
    public static final String PF_TC_FD = "PF TC FD";  
    public static final String TA_S_BO_LOCKER = "TA S BO LOCKER";  
    public static final String TA_G_BO_LOCKER = "TA G BO LOCKER";  
    public static final String TA_S_BC_DELIVERED = "TA S BC DELIVERED";  
    public static final String TA_G_BC_DELIVERED = "TA G BC DELIVERED";  
    
    public static final String STOCK_DETAILS_SCREEN = "STOCK DETAILS";      
    public static final String GOLD_REBILL_MAPPER_SCREEN = "GOLD REBILL MAPPER";
    public static final String SILVER_REBILL_MAPPER_SCREEN = "SILVER REBILL MAPPER";
    public static final String CUSTOMER_DETAILS = "CUSTOMER DETAILS";
    public static final String ALL_CUSTOMER_DETAILS = "ALL CUSTOMER DETAILS";
    public static final String MIS_REPORTS = "MIS REPORTS";
    
    public static final String PAWN = "PAWN";
    public static final String RE = "RE+";
    
    public static final String ACTIVE = "ACTIVE";
    public static final String REST = "REST";

    public static final String G_BILL_OPENING_OPERATION = "GOLDBILLOPENING";
    public static final String S_BILL_OPENING_OPERATION = "SILVERBILLOPENING";    
    public static final String G_BILL_CLOSING_OPERATION = "GOLDBILLCLOSING";
    public static final String S_BILL_CLOSING_OPERATION = "SILVERBILLCLOSING";
    public static final String G_REBILL_CLOSE_OPERATION = "GOLDREBILLCLOSE";
    public static final String G_REBILL_OPEN_OPERATION = "GOLDREBILLOPEN";
    public static final String S_REBILL_CLOSE_OPERATION = "SILVERREBILLCLOSE";
    public static final String S_REBILL_OPEN_OPERATION = "SILVERREBILLOPEN";
    public static final String G_ADVANCE_AMOUNT_OPERATION = "GOLDADVANCEAMOUNT";
    public static final String S_ADVANCE_AMOUNT_OPERATION = "SILVERADVANCEAMOUNT";
    public static final String MULTI_CLOSING_OPERATION = "MULTICLOSE";
    public static final String MULTI_OPERATION_OPERATION = "MULTIOPERATION";
    

    public static final String REPORT_LOCATION = "C:\\Program Files\\PawnBroking\\app\\reports";
    
    public static final String COMPANY_EXPENSES = "COMPANY EXPENSES";
    public static final String COMPANY_INCOMES = "COMPANY INCOMES";
    public static String CURRENT_OPERATION = null;
    
    public static String TEMP_FILE_LOCATION;
    public static boolean IS_LOGIN_USER_IMAGE_AVAILABLE = false;
    public static final String LOGIN_USER_IMAGE_NAME = "login_user.png";
    public static final String CUSTOMER_IMAGE_NAME = "customer_image.png";
    public static final String OPEN_CUSTOMER_IMAGE_NAME = "open_customer.png";
    public static final String OPEN_JEWEL_IMAGE_NAME = "open_jewel.png";
    public static final String OPEN_USER_IMAGE_NAME = "open_user.png";
    public static final String CLOSE_CUSTOMER_IMAGE_NAME = "close_customer.png";
    public static final String CLOSE_JEWEL_IMAGE_NAME = "close_jewel.png";
    public static final String CLOSE_USER_IMAGE_NAME = "close_user.png";

    public static boolean IRAIVA_SOUND_ON = true;
    
    public static File tempFile;
    public static File custTemp;
    public static File jewelTemp;
    public static File userTemp;
    public static File CLOSEcustTemp;
    public static File CLOSEjewelTemp;
    public static File CLOSEuserTemp;
    
    public static Image noImage;
    public static Image loadingImage;
    
    public static final String WEIGHT_MACHINE = "WEIGHT MACHINE";
    public static final String CASH_DRAWER = "CASH DRAWER";
    public static final String SHOP_LOCKER = "SHOP LOCKER";
    public static final String REPLEDGE_LOCKER = "REPLEDGE LOCKER";
    public static final String REPLEDGE_DRAWER = "REPLEDGE DRAWER";
    public static final String DELIVERED = "DELIVERED";
    public static final String NONE = "NONE";
    public static final String REBILLED = "REBILLED";
    
    public static final String NORMAL = "NORMAL";
    public static final String EMI = "EMI";
        
}
