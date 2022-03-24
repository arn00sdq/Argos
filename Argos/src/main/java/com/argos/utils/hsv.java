package com.argos.utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

class Detection_w_hsv {

   
    private JFrame frame;

    private JPanel imgPanel = new JPanel();
    
    private Mat src;
    private Mat resizeImg;
    private Mat srcFinal;
    private Mat srcHsv = new Mat();
    private Mat maskHsv;
    private Mat maskHsvInv;
    
    private static int MAX_VALUE = 255;
    private static int MAX_VALUE_H = 360/2;
    
    private static final String LOW_H_NAME = "Low H";
    private static final String LOW_S_NAME = "Low S";
    private static final String LOW_V_NAME = "Low V";
    private static final String HIGH_H_NAME = "High H";
    private static final String HIGH_S_NAME = "High S";
    private static final String HIGH_V_NAME = "High V";
    
    private JSlider sliderLowH;
    private JSlider sliderHighH;
    private JSlider sliderLowS;
    private JSlider sliderHighS;
    private JSlider sliderLowV;
    private JSlider sliderHighV;
    
    public Detection_w_hsv(String[] args) {

        src = Imgcodecs.imread("C:\\Users\\MSI\\Documents\\NetBeansProjects\\Gestion-de-projet\\mavenproject1\\src\\main\\java\\com\\mycompany\\mavenproject1\\test2.jpg");
        
        resizeImg = Mat.zeros(src.size(), CvType.CV_8U);
        Size sz = new Size(400,400);
        Imgproc.resize( src, resizeImg, sz );

        Imgproc.cvtColor(resizeImg, srcHsv, Imgproc.COLOR_BGR2HSV); 
        
        frame = new JFrame("Sliders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        
        window_initialisation(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
        update();
    }

    private void window_initialisation(Container pane) {
       
        JPanel sliderPanel = new JPanel();
        
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel(LOW_H_NAME));
        
        sliderLowH = new JSlider(0, MAX_VALUE_H, 0);
        sliderPanel.add(sliderLowH);
        sliderPanel.add(new JLabel(HIGH_H_NAME));
        
        sliderHighH = new JSlider(0, MAX_VALUE_H, MAX_VALUE_H);
        sliderPanel.add(sliderHighH);
        
        sliderPanel.add(new JLabel(LOW_S_NAME));
        sliderLowS = new JSlider(0, MAX_VALUE, 0);
        sliderPanel.add(sliderLowS);
        sliderPanel.add(new JLabel(HIGH_S_NAME));
        
        sliderHighS = new JSlider(0, MAX_VALUE, MAX_VALUE);
        sliderPanel.add(sliderHighS);
        sliderPanel.add(new JLabel(LOW_V_NAME));
        
        sliderLowV = new JSlider(0, MAX_VALUE, 0);
        sliderPanel.add(sliderLowV);
        sliderPanel.add(new JLabel(HIGH_V_NAME));
        
        sliderHighV = new JSlider(0, MAX_VALUE, MAX_VALUE);
        sliderPanel.add(sliderHighV);
        
        pane.add(sliderPanel, BorderLayout.PAGE_START); 

    }

    private void update() {
        
        while (true) {
            
            Mat srcHsv = new Mat();
            maskHsv = new Mat();
            maskHsvInv = new Mat();
            srcFinal = new Mat();

            Imgproc.cvtColor(resizeImg, srcHsv, Imgproc.COLOR_BGR2HSV,3);  

            Core.inRange(srcHsv, new Scalar(sliderLowH.getValue(), sliderLowS.getValue(), sliderLowV.getValue()),
            new Scalar(sliderHighH.getValue(), sliderHighS.getValue(), sliderHighV.getValue()), maskHsv);
            Imgproc.threshold(maskHsv,maskHsvInv,0, 255,Imgproc.THRESH_BINARY_INV );
            Core.bitwise_and(resizeImg, resizeImg,srcFinal, maskHsvInv);
            
            HighGui.imshow("original",resizeImg);
            HighGui.imshow("HSV",srcHsv);
            HighGui.imshow("mask", maskHsv);
            HighGui.imshow("maskInv", maskHsvInv);
            HighGui.imshow("Final", srcFinal);
            HighGui.waitKey(1);
            
        }
      
    }
    
}

public class hsv {

    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new Detection_w_hsv(args);

    }

}
