/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

/**
 *
 * @author Tiru
 */
public class FileUtil {
    
    /*public static String getContentOfFile(String filePath, String fileName) throws FileNotFoundException, IOException {
        
        File file = new File(filePath + File.separator + fileName);
        FileInputStream fis = null;
        String sContent = "";
        
        try {
            fis = new FileInputStream(file);
            int content;
            while ((content = fis.read()) != -1) {
                sContent = sContent + (char) content;
            }
            return sContent;
        } finally {
            try {
                if (fis != null)
                fis.close();
            } catch (IOException ex) {
            }
        }    
    }
    
    public static boolean writeContentOfFile(String filePath, String fileName, String content) throws FileNotFoundException, IOException {
        
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(filePath + File.separator + fileName);
            bw = new BufferedWriter(fw);
            bw.write(content);
            return true;
        } finally {
            try {
                if (bw != null)
                        bw.close();
                if (fw != null)
                        fw.close();
            } catch (IOException ex) {
            }
        }    
    }
    
    public static boolean sendMail(final String from, 
            final String password, 
            String to, 
            String subject, 
            String content,
            String fileName) throws MessagingException {
        
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
        new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(from, password);//change accordingly
        }
        });

        System.out.println("Sent message started....");
        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
	      
        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
           InternetAddress.parse(to));

        // Set Subject: header field
        message.setSubject("Testing Subject");

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        messageBodyPart.setText("This is message body");

        // Create a multipar message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        File file = new File(fileName);
        DataSource source = new FileDataSource(file);
        
        messageBodyPart.setDataHandler(new DataHandler(source));
        try {
            messageBodyPart.setFileName(MimeUtility.encodeText(file.getName()));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);

        // Send message
        Transport.send(message);

        System.out.println("Sent message successfully....");
        return true;	 
    }*/
    
}
