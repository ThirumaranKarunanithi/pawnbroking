/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author Tiru
 */
public class NoticeUtil {
    
    public final String FILE_NAME = "FILE_NAME";
    public final String PARAMAETERS = "PARAMAETERS";

    private static void showViewer(JasperPrint print) {
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            viewer.revalidate();
            viewer.repaint();
        });
    }

    public void generateNoticeOperation(String fileName, List<Map<String, Object>> ParamList) throws JRException, SQLException {
            
        File reportFile = new File(fileName);
        JasperPrint print = null;     
        Map<String, Object> firstPageParam = ParamList.get(0);
        print = JasperFillManager.fillReport(reportFile.getPath(), firstPageParam, new JREmptyDataSource());

        if(ParamList.size()>1){
            for(int i=1; i < ParamList.size(); i++)
            {
                JasperPrint jasperPrint_next = JasperFillManager.fillReport(reportFile.getPath(), ParamList.get(i), new JREmptyDataSource());
                 List pages = jasperPrint_next.getPages();
                 for (int j = 0; j < pages.size(); j++) {
                   JRPrintPage object = (JRPrintPage) pages.get(0);
                   print.addPage(object);
                 }
            }
        }
        JasperPrint finalPrint = print;
        SwingUtilities.invokeLater(() -> showViewer(finalPrint));
    }    

    public void generateSinglePageNoticeOperation(String fileName, List<Map<String, Object>> ParamList) throws JRException, SQLException {
            
        File reportFile = new File(fileName);
        JasperPrint print = null;     
        Map<String, Object> firstPageParam = ParamList.get(0);
        print = JasperFillManager.fillReport(reportFile.getPath(), firstPageParam, new JREmptyDataSource());

        if(ParamList.size()>1){
            for(int i=1; i < ParamList.size(); i++)
            {
                JasperPrint jasperPrint_next = JasperFillManager.fillReport(reportFile.getPath(), ParamList.get(i), new JREmptyDataSource());
                List pages = jasperPrint_next.getPages();
                JRPrintPage object = (JRPrintPage) pages.get(0);
                print.addPage(object);
            }
        }
        JasperPrint finalPrint = print;
        SwingUtilities.invokeLater(() -> showViewer(finalPrint));
    }    
    
    public void generateNoticeOperation(String fileName, Map<String, Object> ParamList) throws JRException, SQLException {
            
        File reportFile = new File(fileName);
        JasperPrint print = null;     
        print = JasperFillManager.fillReport(reportFile.getPath(), ParamList, new JREmptyDataSource());
        JasperPrint finalPrint = print;
        SwingUtilities.invokeLater(() -> showViewer(finalPrint));
    }    

    public void mergeAndGenerateNoticeOperation(String fileName1, String fileName2, Map<String, Object> ParamList) throws JRException, SQLException {
            
        File reportFile1 = new File(fileName1);
        File reportFile2 = new File(fileName2);
        JasperPrint print1 = JasperFillManager.fillReport(reportFile1.getPath(), ParamList, new JREmptyDataSource());
        JasperPrint print2 = JasperFillManager.fillReport(reportFile2.getPath(), ParamList, new JREmptyDataSource());
        print1.addPage(print2.getPages().get(0));
        JasperPrint finalPrint1 = print1;
        SwingUtilities.invokeLater(() -> showViewer(finalPrint1));
    }    

    public JasperPrint[] mergeaAndGenerateNoticeOperation(String title, 
            List<Map<String, Object>> paramList, 
            String...fileNames) throws JRException, SQLException {

        List<String> fileNameLst = new ArrayList();
        for (String fileName : fileNames) {
            if (fileName != null) {
                fileNameLst.add(fileName);
            }
        }

        File[] reportFiles = new File[fileNameLst.size()];        
        for(int i=0; i<reportFiles.length; i++) {
            if(fileNameLst.get(i) != null) {
                reportFiles[i] = new File(fileNameLst.get(i));
            }
        }
        
        JasperPrint[] jPrints = new JasperPrint[reportFiles.length];
        for(int i=0; i<jPrints.length; i++) {
            if(reportFiles[i] != null) {
                jPrints[i] = JasperFillManager.fillReport(reportFiles[i].getPath(), 
                        paramList.get(i), new JREmptyDataSource());
                if(i>0) {
                    jPrints[0].addPage(jPrints[i].getPages().get(0));
                }
            }
        }
        if(jPrints.length > 0) {
            //PrintReportToPrinter(jPrints[0]);
            JasperPrint p0 = jPrints[0];
            SwingUtilities.invokeLater(() -> showViewer(p0));            
            //JasperPrintManager.printReport(jPrints[0], false);            
            /*JasperViewer viewer = new JasperViewer(jPrints[0], true);
            Container contentPane = viewer.getContentPane();
            JRViewerToolbar toolBar = (JRViewerToolbar)((JRViewer)
                    ((JPanel)contentPane.getComponents()[0]).getComponent(0)).getComponent(0);
            
            JButton saveBtn = (JButton) toolBar.getComponent(0);
            saveBtn.setEnabled(false);

            JButton printBtn = (JButton) toolBar.getComponent(1);
            printBtn.setEnabled(false);
            
            viewer.setTitle(CommonConstants.ACTIVE_COMPANY_NAME + " - " + title);
            viewer.setVisible(true);*/
        } else {
            //PopupUtil.showInfoAlert("Sorry No Report to print.");
        }
        return jPrints;
    }    

    /*private void PrintReportToPrinter(JasperPrint print, String printerName) throws JRException {

        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            int selectedService = 0;
            for(int i = 0; i < services.length;i++){
                if(services[i].getName().toUpperCase().contains(printerName.toUpperCase())){
                    selectedService = i;
                }
            }
            job.setPrintService(services[selectedService]);
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            MediaSizeName mediaSizeName = MediaSize.findMedia(4,4,MediaPrintableArea.INCH);
            printRequestAttributeSet.add(mediaSizeName);
            printRequestAttributeSet.add(new Copies(1));
            
            JRPrintServiceExporter exporter;
            exporter = new JRPrintServiceExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);            
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE,
                    services[selectedService]);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
                    services[selectedService].getAttributes());
            //exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET,
            //      printRequestAttributeSet);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG,
                    Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
                    Boolean.FALSE);           
            exporter.exportReport();
            exporter.setConfiguration(configuration);
        } catch (PrinterException ex) {
            Logger.getLogger(NoticeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    */

    private void PrintReportToPrinter(JasperPrint print, String printerName) throws JRException {

        //Get the printers names
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        String selectedPrinter = "";
        for(int i = 0; i < services.length;i++){
            if(services[i].getName().toUpperCase().contains(printerName.toUpperCase())){
                selectedPrinter = services[i].getName();
            }
        }

        //Lets set the printer name based on the registered printers driver name (you can see the printer names in the services variable at debugging) 
        //String selectedPrinter = "Microsoft XPS Document Writer";   
        // String selectedPrinter = "\\\\S-BPPRINT\\HP Color LaserJet 4700"; // examlpe to network shared printer

        System.out.println("Number of print services: " + services.length);
        System.out.println("printer: " + selectedPrinter);
        PrintService selectedService = null;

        //Set the printing settings
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        //printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        printRequestAttributeSet.add(new Copies(1));
        /*if (print.getOrientationValue() == net.sf.jasperreports.engine.type.OrientationEnum.LANDSCAPE) { 
          printRequestAttributeSet.add(OrientationRequested.LANDSCAPE); 
        } else { 
          printRequestAttributeSet.add(OrientationRequested.PORTRAIT); 
        } */
        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(selectedPrinter, null));

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
        configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);

        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setConfiguration(configuration);

        //Iterate through available printer, and once matched with our <selectedPrinter>, go ahead and print!
        if(services != null && services.length != 0){
          for(PrintService service : services){
              String existingPrinter = service.getName();
              if(existingPrinter.equals(selectedPrinter))
              {
                  selectedService = service;
                  break;
              }
          }
        }
        if(selectedService != null)
        {   
          try{
              //Lets the printer do its magic!
              exporter.exportReport();
          }catch(Exception e){
        System.out.println("JasperReport Error: "+e.getMessage());
          }
        }else{
          System.out.println("JasperReport Error: Printer not found!");
        }
    }

    public void mergeaAndGenerateNoticeIndividual(String title, 
            Map<String, Object> paramList, 
            String...fileNames) throws JRException, SQLException {

        List<String> fileNameLst = new ArrayList();
        for (String fileName : fileNames) {
            if (fileName != null) {
                fileNameLst.add(fileName);
            }
        }

        File[] reportFiles = new File[fileNameLst.size()];        
        for(int i=0; i<reportFiles.length; i++) {
            if(fileNameLst.get(i) != null) {
                reportFiles[i] = new File(fileNameLst.get(i));
            }
        }
        
        JasperPrint[] jPrints = new JasperPrint[reportFiles.length];
        for(int i=0; i<jPrints.length; i++) {
            if(reportFiles[i] != null) {
                jPrints[i] = JasperFillManager.fillReport(reportFiles[i].getPath(), 
                        paramList, new JREmptyDataSource());
                if(i>0) {
                    jPrints[0].addPage(jPrints[i].getPages().get(0));
                }
            }
        }
        if(jPrints.length > 0) {
            JasperPrint p0 = jPrints[0];
            SwingUtilities.invokeLater(() -> showViewer(p0));
        } else {
            PopupUtil.showInfoAlert("Sorry No Report to print.");
        }
    }    
    
    public void mergeaAndGenerateNoticeOperationDirectPrint(boolean directPrint, 
            String printerName, 
            Map<String, Object> ParamList, 
            String...fileNames) throws JRException, SQLException {

        List<String> fileNameLst = new ArrayList();
        for (String fileName : fileNames) {
            if (fileName != null) {
                fileNameLst.add(fileName);
            }
        }

        File[] reportFiles = new File[fileNameLst.size()];
        for(int i=0; i<reportFiles.length; i++) {
            if(fileNameLst.get(i) != null) {
                reportFiles[i] = new File(fileNameLst.get(i));
            }
        }
        
        JasperPrint[] jPrints = new JasperPrint[reportFiles.length];
        for(int i=0; i<jPrints.length; i++) {
            if(reportFiles[i] != null) {
                jPrints[i] = JasperFillManager.fillReport(reportFiles[i].getPath(), ParamList, new JREmptyDataSource());
                if(i>0) {
                    jPrints[0].addPage(jPrints[i].getPages().get(0));
                }
            }
        }
        if(jPrints.length > 0) {
            if(!directPrint) {
                JasperPrintManager.printReport(jPrints[0], false);      
            } else {
                PrintReportToPrinter(jPrints[0], printerName);            
            }
        } else {
            //PopupUtil.showInfoAlert("Sorry No Report to print.");
        }
    }    
    
    public JasperPrint[] mergeaAndGenerateNoticeOperation(List<Map<String, Object>> allPages) throws JRException, SQLException {
            
        File[] reportFiles = new File[allPages.size()];
        for(int i=0; i<reportFiles.length; i++) {
            reportFiles[i] = new File(allPages.get(i).get(FILE_NAME).toString());
        }
        
        JasperPrint[] jPrints = new JasperPrint[allPages.size()];
        for(int i=0; i<jPrints.length; i++) {
            jPrints[i] = JasperFillManager.fillReport(reportFiles[i].getPath(), 
                    (Map<String, Object>) allPages.get(i).get(PARAMAETERS), new JREmptyDataSource());
            if(i>0) {
                for(int j=0; j<jPrints[i].getPages().size(); j++) {
                    jPrints[0].addPage(jPrints[i].getPages().get(j));
                }
            }
        }
        JasperPrint p0 = jPrints[0];
        SwingUtilities.invokeLater(() -> showViewer(p0));
        return jPrints;
    }    
    
    public void mergeaAndGenerateNoticeOperationAndToPDF(JasperPrint jPrints,
               String locationPath, String fileName) throws JRException, SQLException, FileNotFoundException {
            
        //JasperViewer.viewReport(jPrints[0], false);
        //File pdf = new File("E:\\todaysaccOU.pdf");
        //JasperExportManager.exportReportToPdfStream(jPrints[0], new FileOutputStream(pdf)); 
        
        //JasperExportManager.exportReportToPdfFile(jPrints,
          //     locationPath + "\\" + fileName + ".pdf");
        
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setExporterInput(new SimpleExporterInput(jPrints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(locationPath + "\\" + fileName + ".pdf"));
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        exporter.setConfiguration(configuration);

        exporter.exportReport();        
    }        
        
    public void compileToFile(String srcFile, String destinationFile) throws JRException {
    
        JasperDesign jasperDesign = JRXmlLoader.load(srcFile);
        JasperCompileManager.compileReportToFile(jasperDesign, destinationFile);
    }
    
    public void compileAllReportFiles() {        
        try {
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_company.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_company.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_customer.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_customer.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_backside.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_backside.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_company_blank.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_company_blank.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_customer_blank.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_customer_blank.jasper");
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\todays_account.jrxml", "D:\\Pawnbroking\\reportcompiler\\todays_account.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\noticegeneration.jrxml", "D:\\Pawnbroking\\reportcompiler\\noticegeneration.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billcalcrep.jrxml", "D:\\Pawnbroking\\reportcompiler\\billcalcrep.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\companybilldebits.jrxml", "D:\\Pawnbroking\\reportcompiler\\companybilldebits.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\companybillcredits.jrxml", "D:\\Pawnbroking\\reportcompiler\\companybillcredits.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\repledgebillcredits.jrxml", "D:\\Pawnbroking\\reportcompiler\\repledgebillcredits.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\repledgebilldebits.jrxml", "D:\\Pawnbroking\\reportcompiler\\repledgebilldebits.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\advanceamount.jrxml", "D:\\Pawnbroking\\reportcompiler\\advanceamount.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\expensesandincome.jrxml", "D:\\Pawnbroking\\reportcompiler\\expensesandincome.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\customerdetails.jrxml", "D:\\Pawnbroking\\reportcompiler\\customerdetails.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\todays_account_denomination.jrxml", "D:\\Pawnbroking\\reportcompiler\\todays_account_denomination.jasper");             
            compileToFile("D:\\Pawnbroking\\reportcompiler\\ledger.jrxml", "D:\\Pawnbroking\\reportcompiler\\ledger.jasper");     
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\cardlostbond.jrxml", "D:\\Pawnbroking\\reportcompiler\\cardlostbond.jasper");     
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_company_silver.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_company_silver.jasper");     
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_customer_silver.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_customer_silver.jasper"); 
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billopening_small_card.jrxml", "D:\\Pawnbroking\\reportcompiler\\billopening_small_card.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\file_sticker_for_bills.jrxml", "D:\\Pawnbroking\\reportcompiler\\file_sticker_for_bills.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\locker_sticker_for_bills.jrxml", "D:\\Pawnbroking\\reportcompiler\\locker_sticker_for_bills.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\8020report.jrxml", "D:\\Pawnbroking\\reportcompiler\\8020report.jasper"); 
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\monthlynoticepanel.jrxml", "D:\\Pawnbroking\\reportcompiler\\monthlynoticepanel.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\companybillcreditsindex.jrxml", "D:\\Pawnbroking\\reportcompiler\\companybillcreditsindex.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\notice_acknowledge_address.jrxml", "D:\\Pawnbroking\\reportcompiler\\notice_acknowledge_address.jasper"); 
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\repledgePlannedButNotDone.jrxml", "D:\\Pawnbroking\\reportcompiler\\repledgePlannedButNotDone.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\lockerStock.jrxml", "D:\\Pawnbroking\\reportcompiler\\lockerStock.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\repledgeStock.jrxml", "D:\\Pawnbroking\\reportcompiler\\repledgeStock.jasper");  
            
            compileToFile("D:\\Pawnbroking\\reportcompiler\\Billclosing_RecievedAmt.jrxml"
                    , "D:\\Pawnbroking\\reportcompiler\\Billclosing_RecievedAmt.jasper");
            compileToFile("D:\\Pawnbroking\\reportcompiler\\BillOpening_RecievedAmt.jrxml"
                    , "D:\\Pawnbroking\\reportcompiler\\BillOpening_RecievedAmt.jasper"); 
            compileToFile("D:\\Pawnbroking\\reportcompiler\\billcalccomp.jrxml"
                    , "D:\\Pawnbroking\\reportcompiler\\billcalccomp.jasper"); 
            
            
            //compileToFile("D:\\Pawnbroking\\reportcompiler\\receiptvoucher.jrxml"
              //      , "D:\\Pawnbroking\\reportcompiler\\receiptvoucher.jasper"); 
            
        } catch (JRException ex) {
            Logger.getLogger(NoticeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
