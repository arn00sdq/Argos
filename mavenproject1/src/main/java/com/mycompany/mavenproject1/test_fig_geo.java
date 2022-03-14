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

class Detection{
    
    private Mat srcGray = new Mat();
    private JFrame frame;
    private JLabel imgSrcLabel;
    private JLabel imgContoursLabel;
    private static final int MAX_THRESHOLD = 255;
    private int threshold = 100;
    
    private Mat src;
    private Mat srcCnt;
    
    private int i;
    
    private Mat dstThreshold = new Mat();
    
    public Detection(String[] args) {

        src = Imgcodecs.imread("img\\fig_geo.jpg");
        srcCnt = src.clone();

        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        
        // Create and set up the window.
        frame = new JFrame("Finding contours in your image demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        update();
    }
    
    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Canny threshold: "));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, threshold);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                threshold = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        
        JPanel imgPanel = new JPanel();
        // ADD img
        imgSrcLabel = new JLabel(new ImageIcon(img));
        imgPanel.add(imgSrcLabel);
        //add Gray
         imgPanel.add(new JLabel(new ImageIcon(HighGui.toBufferedImage(srcGray))));
        // ADD srcGray
        Mat blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U);
        imgContoursLabel = new JLabel(new ImageIcon((HighGui.toBufferedImage(srcCnt))));
        imgPanel.add(imgContoursLabel);
        
        // add tout
        pane.add(imgPanel);
    }
    
    private void update() {
        
        Imgproc.threshold(srcGray,this.dstThreshold,threshold, 255,Imgproc.THRESH_BINARY_INV );
        
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        
        Imgproc.findContours(this.dstThreshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];
        
        
        //Mat drawing = Mat.zeros(this.dstThreshold.size(), CvType.CV_8UC3);
        srcCnt = src.clone();
        
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(0, 255,0);
            
            Imgproc.drawContours(srcCnt, contours, i, color, 2, 1, hierarchy, 0, new Point());
            
            //Calcul nombre de points
            contoursPoly[i] = new MatOfPoint2f(contours.get(i).toArray());
            double peri = Imgproc.arcLength(contoursPoly[i], true);
            Imgproc.approxPolyDP(contoursPoly[i], contoursPoly[i], 0.02 * peri, true);  
            Point[] points = contoursPoly[i].toArray();
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            
            //Calcul l'aire
            
            double cont_area = Imgproc.contourArea(contours.get(i));
            
            Imgproc.putText(srcCnt, "Points : " + String.valueOf(points.length),new Point(boundRect[i].x, boundRect[i].y),4, 1, new Scalar(0, 0, 0),3); 
            Imgproc.putText(srcCnt, "Area : " +String.valueOf(cont_area),new Point(boundRect[i].x, boundRect[i].y + 30),4, 1, new Scalar(0, 0, 0),3); 
        }
        
        imgContoursLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(srcCnt)));
        
        frame.repaint();
    }
    
}

public class test_fig_geo {
    
    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Detection(args);
            }
        });
    }
    
}
