/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author MSI
 */
class automatic_detection {

    int target = 100;
    int minDiff = target;
    int optimalSaturation = 1;
    boolean analysis_completed = false;
    int s_val = 1;

    List<TargetZone> detected;
    int maxZone = 0;
    TargetZone zoneMaxAire;
    Mat drawing;

    ArrayList<List<String>> carotteColor = new ArrayList<>();

    private Mat src;
    private Mat srcFinal;
    private Mat srcHsv = new Mat();
    private Mat maskHsv;
    private Mat maskHsvInv;

    private static int MAX_VALUE = 255;
    private static int MAX_VALUE_H = 360 / 2;
    private Random rng = new Random(12345);

    public automatic_detection(String[] args) {

        src = Imgcodecs.imread("C:\\Users\\MSI\\Desktop\\master\\Gestion-de-projet\\Argos\\src\\main\\java\\com\\argos\\utils\\test.png");


        srcFinal = src.clone();

        update();
    }

    private void DetectContour() {

        srcHsv = new Mat();
        maskHsv = new Mat();
        maskHsvInv = new Mat();
        srcFinal = src.clone();
        
        Mat hierarchy = new Mat();
        
        detected = new ArrayList<>();

        Imgproc.cvtColor(src, srcHsv, Imgproc.COLOR_BGR2HSV, 3);
        Core.inRange(srcHsv, new Scalar(0, s_val, 0),
                new Scalar(255, 255, 255), maskHsv);
        Imgproc.threshold(maskHsv, maskHsvInv, 0, 255, Imgproc.THRESH_BINARY_INV);
        //src,src,dest,mask
        Core.bitwise_and(src, src, srcFinal, maskHsvInv);
        
        HighGui.imshow("RectFinal", srcFinal);
        HighGui.waitKey(1);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(maskHsvInv, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];

        drawing = Mat.zeros(maskHsvInv.size(), CvType.CV_8UC3);
        
        for (int i = 0; i < contours.size(); i++) {

            contoursPoly[i] = new MatOfPoint2f(contours.get(i).toArray());
            double peri = Imgproc.arcLength(contoursPoly[i], true);
            Imgproc.approxPolyDP(contoursPoly[i], contoursPoly[i], 0.02 * peri, true);

            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            TargetZone newZone = new TargetZone(boundRect[i].x, boundRect[i].y, boundRect[i].x + boundRect[i].width,
                    boundRect[i].y + boundRect[i].height, boundRect[i].width, boundRect[i].height);
            
            
            if (newZone.getArea() > 1500 && !newZone.existsInArray(detected)) {
                
                detected.add(newZone);
            }
            
        } 

    }

    private void update() {

        while (!analysis_completed) {

            DetectContour();


            if (this.s_val < 150) {

                this.s_val += 2;

            } else {

                analysis_completed = true;
                this.s_val = optimalSaturation;
                DetectContour();

            }

            if (Math.abs(target - detected.size() + 1) < minDiff) {

                minDiff = Math.abs(target - detected.size() + 1);
                optimalSaturation = this.s_val;

            }
            
        }
        
        for (int k = 0; k < detected.size(); k++) {
                Point p1 = new Point(detected.get(k).upper_x, detected.get(k).upper_y);
                Point p2 = new Point(detected.get(k).lower_x, detected.get(k).lower_y);
                Scalar color = new Scalar(0, 255, 0);
                int thickness = 2;
                Imgproc.rectangle(srcFinal, p1, p2, color, thickness);

            }
            
        
        System.out.println("optimal saturation" + optimalSaturation);
        
        PointOfInterestFinder PoIFinder = new PointOfInterestFinder();
        PoIFinder.GetPointOfInterest(detected,srcFinal);
       

        HighGui.imshow("original", src);
        HighGui.imshow("HSV", srcHsv);
        HighGui.imshow("mask", maskHsv);
        HighGui.imshow("maskInv", maskHsvInv);
       
        HighGui.imshow("RectFinal", srcFinal);
        HighGui.waitKey(1);

    }
}

public class automatic_detection_w_target_v2 {

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new automatic_detection(args);

    }

}
