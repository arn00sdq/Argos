package com.mycompany.mavenproject1;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

class Detection_w_hsv {

   
    private JFrame frame;

    private JPanel imgPanel = new JPanel();

    private JLabel container_img;
    private JLabel imgContours;
    private JLabel detectionMask;
    private JLabel detectionMaskInv;
    
    private Mat src;
    private Mat resizeImg;
    private Mat srcFinal;
    private Mat srcHsv = new Mat();
    private Mat maskHsv;
    private Mat maskHsvInv;
    
    private static int MAX_VALUE = 255;
    private static int MAX_VALUE_H = 360/2;
    
    private static final String WINDOW_NAME = "Thresholding Operations using inRange demo";
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
    
    private Scalar lower_range = new Scalar(5, 5, 5);
    private Scalar upper_range = new Scalar(255, 255, 255);

    private int i;

    public Detection_w_hsv(String[] args) {

        src = Imgcodecs.imread("img\\carrote2.jpg");
        
        resizeImg = Mat.zeros(src.size(), CvType.CV_8U);
        Size sz = new Size(400,400);
        Imgproc.resize( src, resizeImg, sz );
        
        srcFinal = resizeImg.clone();
        Image srcImg = HighGui.toBufferedImage(resizeImg);

        frame = new JFrame("Finding contours in your image demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        
        Imgproc.cvtColor(resizeImg, srcHsv, Imgproc.COLOR_BGR2HSV);  
        
        window_initialisation(frame.getContentPane(), srcImg);

        frame.pack();
        frame.setVisible(true);
        update();
    }

    private void window_initialisation(Container pane, Image srcImg) {

        //Slider init
        JPanel sliderPanel = new JPanel();
        
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel(LOW_H_NAME));
        
        sliderLowH = new JSlider(0, MAX_VALUE_H, 0);
        sliderLowH.setMajorTickSpacing(50);
        sliderLowH.setMinorTickSpacing(10);
        sliderLowH.setPaintTicks(true);
        sliderLowH.setPaintLabels(true);
        sliderPanel.add(sliderLowH);
        sliderPanel.add(new JLabel(HIGH_H_NAME));
        
        sliderHighH = new JSlider(0, MAX_VALUE_H, MAX_VALUE_H);
        sliderHighH.setMajorTickSpacing(50);
        sliderHighH.setMinorTickSpacing(10);
        sliderHighH.setPaintTicks(true);
        sliderHighH.setPaintLabels(true);
        sliderPanel.add(sliderHighH);
        
        sliderPanel.add(new JLabel(LOW_S_NAME));
        sliderLowS = new JSlider(0, MAX_VALUE, 0);
        sliderLowS.setMajorTickSpacing(50);
        sliderLowS.setMinorTickSpacing(10);
        sliderLowS.setPaintTicks(true);
        sliderLowS.setPaintLabels(true);
        sliderPanel.add(sliderLowS);
        sliderPanel.add(new JLabel(HIGH_S_NAME));
        
        sliderHighS = new JSlider(0, MAX_VALUE, MAX_VALUE);
        sliderHighS.setMajorTickSpacing(50);
        sliderHighS.setMinorTickSpacing(10);
        sliderHighS.setPaintTicks(true);
        sliderHighS.setPaintLabels(true);
        sliderPanel.add(sliderHighS);
        sliderPanel.add(new JLabel(LOW_V_NAME));
        
        sliderLowV = new JSlider(0, MAX_VALUE, 0);
        sliderLowV.setMajorTickSpacing(50);
        sliderLowV.setMinorTickSpacing(10);
        sliderLowV.setPaintTicks(true);
        sliderLowV.setPaintLabels(true);
        sliderPanel.add(sliderLowV);
        sliderPanel.add(new JLabel(HIGH_V_NAME));
        
        sliderHighV = new JSlider(0, MAX_VALUE, MAX_VALUE);
        sliderHighV.setMajorTickSpacing(50);
        sliderHighV.setMinorTickSpacing(10);
        sliderHighV.setPaintTicks(true);
        sliderHighV.setPaintLabels(true);
        sliderPanel.add(sliderHighV);
        
        sliderLowH.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valH = Math.min(sliderHighH.getValue()-1, source.getValue());
                sliderLowH.setValue(valH);
                update();
            }
        });
        sliderHighH.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valH = Math.max(source.getValue(), sliderLowH.getValue()+1);
                sliderHighH.setValue(valH);
                update();
            }
        });
        sliderLowS.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valS = Math.min(sliderHighS.getValue()-1, source.getValue());
                sliderLowS.setValue(valS);
                update();
            }
        });
        sliderHighS.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valS = Math.max(source.getValue(), sliderLowS.getValue()+1);
                sliderHighS.setValue(valS);
                update();
            }
        });
        sliderLowV.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valV = Math.min(sliderHighV.getValue()-1, source.getValue());
                sliderLowV.setValue(valV);
                update();
            }
        });
        sliderHighV.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valV = Math.max(source.getValue(), sliderLowV.getValue()+1);
                sliderHighV.setValue(valV);
                update();
            }
        });
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        //Image Container

        imgPanel.add(new JLabel(new ImageIcon(srcImg)));
        imgPanel.add(new JLabel(new ImageIcon(HighGui.toBufferedImage(srcHsv))));// pas besoin de Jlabel
        
        detectionMask = new JLabel(new ImageIcon(srcImg));
        detectionMaskInv = new JLabel(new ImageIcon(srcImg));
        imgContours = new JLabel(new ImageIcon(srcImg));
        
        imgPanel.add(detectionMask);
        imgPanel.add(detectionMaskInv);
        imgPanel.add(imgContours);

        pane.add(imgPanel);
        
        update();

    }

    private void update() {
        
        Mat srcHsv = new Mat();
        maskHsv = new Mat();
        maskHsvInv = new Mat();
        srcFinal = new Mat();
        
        Imgproc.cvtColor(resizeImg, srcHsv, Imgproc.COLOR_BGR2HSV,3);  
        
        Core.inRange(srcHsv, new Scalar(sliderLowH.getValue(), sliderLowS.getValue(), sliderLowV.getValue()),
                    new Scalar(sliderHighH.getValue(), sliderHighS.getValue(), sliderHighV.getValue()), maskHsv);
        Imgproc.threshold(maskHsv,maskHsvInv,0, 255,Imgproc.THRESH_BINARY_INV );
        Core.bitwise_and(resizeImg, resizeImg,srcFinal, maskHsvInv);
        
        detectionMask.setIcon(new ImageIcon(HighGui.toBufferedImage(maskHsv)));
        detectionMaskInv.setIcon(new ImageIcon(HighGui.toBufferedImage(maskHsvInv)));
        imgContours.setIcon(new ImageIcon(HighGui.toBufferedImage(srcFinal)));
        
       // srcFinal = src.clone();
    }

}

public class hsv {

    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Detection_w_hsv(args);
            }
        });
    }

}
