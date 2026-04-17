/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.backup;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.io.File;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author Tiru
 */
public class DataBaseBackup {
    
    public void doBackupOperation(String fromMailId, String fromMailPass, String toMailId,
            String subject, String filePath, 
            String backupFileName, String encrFileName) throws IOException {
        
        /*File temp = new File(CommonConstants.TEMP_LOCATION);
        temp.mkdirs();
        temp.deleteOnExit();
        
        if(takeDBBackup(temp.getAbsolutePath(), backupFileName)) {
            
            Thread thrd = new Thread(() -> {
                    try {
                        
                        File f = new File(temp.getAbsoluteFile() + File.separator + backupFileName);
                        
                        while(!f.exists()) {
                            
                            if(f.exists()) {
                                //String contentOfFile = FileUtil.getContentOfFile(temp.getAbsolutePath(), backupFileName);
                                //String hashedString = convertToMD5(contentOfFile);
                                //System.out.println(unHash(hashedString));
                                //if(FileUtil.writeContentOfFile(temp.getAbsolutePath(), encrFileName, hashedString)) {
                                    boolean sent = FileUtil.sendMail(fromMailId, 
                                                    fromMailPass, 
                                                    toMailId, 
                                                    subject, 
                                                    "Backup files ", 
                                                    temp.getAbsoluteFile() + File.separator + backupFileName);
                                    if(sent) {
                                        f.delete();
                                    }
                                    break;
                                //}
                            }
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
            });
            thrd.start();
        }*/
    }
    
    public void doBackupOperation(String backupFilePath, String backupFileName) throws IOException {
        
        File temp = new File(backupFilePath);
        temp.mkdirs();
        
        if(takeDBBackup(temp.getAbsolutePath(), backupFileName)) {
            PopupUtil.showInfoAlert("This date data was backedup successfully.");
        } else {
            PopupUtil.showInfoAlert("Cannot backup the datas.");
        }
    }
    
    private boolean takeDBBackup(String filePath, String fileName) throws IOException {

        final ArrayList<String> cmd = new ArrayList<>();
        cmd.add("C:/Program Files/PostgreSQL/14/bin\\pg_dump");
        cmd.add("-h");
        cmd.add(CommonConstants.IP);
        cmd.add("-p");
        cmd.add(CommonConstants.PORT);
        cmd.add("-U");
        cmd.add("\"" + CommonConstants.DB_USERNAME + "\"");
        cmd.add("-c");
        cmd.add("-F");
        cmd.add("t");
        cmd.add("-f");
        cmd.add(filePath + File.separator + fileName);
        cmd.add("\""+ CommonConstants.SCHEMA +"\"");
        final ProcessBuilder pb = new ProcessBuilder(cmd);
        final Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", CommonConstants.DB_PASSWORD);
        pb.redirectErrorStream(true);
        Process p = pb.start();         
        return true;
    }
    
    /*private String convertToMD5(String md5) throws NoSuchAlgorithmException {
        java.security.MessageDigest md 
                = java.security.MessageDigest.getInstance("MD5");
        //md.update(CommonConstants.MD5_PASSWORD.getBytes());
        byte[] array = md.digest(md5.getBytes());
        return new String(Hex.encodeHex(array));  
    }        

    public String unHash(String hashedPassword) throws NoSuchAlgorithmException, DecoderException {  
        MessageDigest md = MessageDigest.getInstance("MD5");  
        //md.update(CommonConstants.MD5_PASSWORD.getBytes());
        //md.update(hashedPassword.getBytes());  
        char[] raw = Arrays.toString(md.digest(hashedPassword.getBytes())).toCharArray();  
        return new String(Hex.decodeHex(raw));  
    }  */
 
    
}
