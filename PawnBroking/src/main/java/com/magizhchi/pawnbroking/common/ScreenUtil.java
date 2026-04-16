/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;

/**
 *
 * @author tiruk
 */
public class ScreenUtil {
    
    public static JFrame customerWindow = new JFrame();
    
    public static void showOnScreen(int screen, JFrame frame )
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        GraphicsDevice graphicsDevice;
        if( screen > -1 && screen < gd.length ) {
            graphicsDevice = gd[screen];
        } else if( gd.length > 0 ) {
            graphicsDevice = gd[0];
        } else {
            throw new RuntimeException( "No Screens Found" );
        }
        Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
        int screenWidth = graphicsDevice.getDisplayMode().getWidth();
        int screenHeight = graphicsDevice.getDisplayMode().getHeight();
        frame.setLocation(bounds.x ,
              bounds.y);
        frame.setVisible(true);
    }

}
