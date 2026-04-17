/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;


import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import org.bytedeco.opencv.opencv_core.Mat;
/**
 *
 * @author Tiru
 */
public class WebCamWork {
    
    public void captureImageForDefaultCam() {
        Webcam webcam = null;
        try {
            // get default webcam and open it
            webcam = Webcam.getDefault();
            System.out.println("DEF-" + webcam.getName());
            webcam.open();
            
            // get image
            BufferedImage image = webcam.getImage();
            /*for(Dimension dim : webcam.getViewSizes()) {
                System.out.println(dim.getSize());
            }*/
            if (!webcam.isOpen()) {
                webcam.setViewSize(WebcamResolution.VGA.getSize());
            }
            // save image to PNG file
        String sCustomerImage = "E:\\images\\bills\\text.png";
            File customerImgFile = new File(sCustomerImage);
            customerImgFile.setWritable(true);
            ImageIO.write(image, "PNG", customerImgFile);            
        } catch (IOException ex) {
            Logger.getLogger(WebCamWork.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            webcam.close();
        }
    }

    public void startToShowCamera(String webCamName, ImageView imageView) {
        Webcam webcam = null;
        webcam = Webcam.getWebcamByName(webCamName);
        webcam.open();
        for (int i = 0; i < 100; i++) {
            imageView.setImage(SwingFXUtils.toFXImage(webcam.getImage(), null));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
        if (webcam.isOpen()) {
            webcam.close();
            webcam.removeWebcamListener(null);
        }
    }

    public static Image captureImageFrom(String webCamName) {

        String strDeviceNum = webCamName.charAt(webCamName.length()-1)+"";
        int deviceNum = Integer.parseInt(strDeviceNum);

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(deviceNum);
        try {
            grabber.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame = grabber.grab();
            grabber.stop();            
            if (frame.imageHeight> 0 && frame.imageWidth> 0) {
                BufferedImage image = converter.getBufferedImage(frame);
                //iplImage.asCvMat();//copyTo(image);
                System.out.println("hiiii");
                //Stylesheet.convertToBinary(source, destination);
                return SwingFXUtils.toFXImage(image, null );
            }            
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(WebCamWork.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return null;
    }
    
    public static void captureImageFrom(String webCamName, String fileName) throws FrameGrabber.Exception {
        String strDeviceNum = webCamName.charAt(webCamName.length()-1)+"";
        int deviceNum = Integer.parseInt(strDeviceNum);
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(deviceNum);
        grabber.start();
        Frame frame = grabber.grab();
        OpenCVFrameConverter.ToMat converter 
                = new OpenCVFrameConverter.ToMat();
        Mat mat = converter.convert(frame);
        if(frame != null){
            imwrite(fileName    , mat);
        }
        grabber.stop();
    }

        //System.out.println(webCamName + " - " + deviceNum);
        /*for(int i=0; i<videoInputLib.videoInput.listDevices() ;i++) {
            System.out.println(videoInputLib.videoInput.getDeviceName(i));
        }*/
    
    public Image getCapturedImageFrom(String webCamName) {
        Webcam webcam = null;
        webcam = Webcam.getWebcamByName(webCamName);
        if (!webcam.isOpen()) {
            Dimension dim = webcam.getViewSizes()[webcam.getViewSizes().length-1];
            webcam.setViewSize(dim);
        }
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();
        return SwingFXUtils.toFXImage(image, null);        
    }
    
    public Image captureAndGetImage(String webCamName) {
        Webcam webcam = null;
        webcam = Webcam.getWebcamByName(webCamName);
        webcam.open();
        BufferedImage image = webcam.getImage();
        webcam.close();
        webcam.removeWebcamListener(null);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }                    
        return SwingFXUtils.toFXImage(image, null);
    }
    
    public static List<Webcam> getWebCamLists() {
        return Webcam.getWebcams();
    }
}
