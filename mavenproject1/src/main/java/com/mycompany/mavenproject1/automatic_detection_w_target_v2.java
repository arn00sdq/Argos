/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

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

    List<ZoneCarotte> detected;
    Mat drawing;

    ArrayList<List<String>> carotteColor = new ArrayList<>();

    private JFrame frame;

    private Mat src;
    private Mat resizeImg;
    private Mat srcFinal;
    private Mat srcHsv = new Mat();
    private Mat maskHsv;
    private Mat maskHsvInv;

    private static int MAX_VALUE = 255;
    private static int MAX_VALUE_H = 360 / 2;
    private Random rng = new Random(12345);

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

    public automatic_detection(String[] args) {

        src = Imgcodecs.imread("C:\\Users\\MSI\\Documents\\NetBeansProjects\\Gestion-de-projet\\mavenproject1\\src\\main\\java\\com\\mycompany\\mavenproject1\\test2.jpg");


        resizeImg = Mat.zeros(src.size(), CvType.CV_8U);
        Size sz = new Size(400, 400);
        Imgproc.resize(src, resizeImg, sz);

        srcFinal = resizeImg.clone();

        Imgproc.cvtColor(resizeImg, srcHsv, Imgproc.COLOR_BGR2HSV);

        frame = new JFrame("Slider");
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

    private void DetectContour() {

        srcHsv = new Mat();
        maskHsv = new Mat();
        maskHsvInv = new Mat();
        srcFinal = new Mat();
        
        Mat hierarchy = new Mat();
        
        detected = new ArrayList<>();

        Imgproc.cvtColor(resizeImg, srcHsv, Imgproc.COLOR_BGR2HSV, 3);
        Core.inRange(srcHsv, new Scalar(sliderLowH.getValue(), s_val, sliderLowV.getValue()),
                new Scalar(sliderHighH.getValue(), sliderHighS.getValue(), sliderHighV.getValue()), maskHsv);
        Imgproc.threshold(maskHsv, maskHsvInv, 0, 255, Imgproc.THRESH_BINARY_INV);
        Core.bitwise_and(resizeImg, resizeImg, srcFinal, maskHsvInv);

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
            ZoneCarotte newZone = new ZoneCarotte(boundRect[i].x, boundRect[i].y, boundRect[i].x + boundRect[i].width,
                    boundRect[i].y + boundRect[i].height, boundRect[i].width, boundRect[i].height);

            if (newZone.getArea() > 1500 && !newZone.existsInArray(detected)) {
                detected.add(newZone);
            }

            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.drawContours(drawing, contours, i, color, 1, Imgproc.LINE_8, hierarchy, 0, new Point());
        }
        for (int k = 0; k < detected.size(); k++) {
            Point p1 = new Point(detected.get(k).upper_x, detected.get(k).upper_y);
            Point p2 = new Point(detected.get(k).lower_x, detected.get(k).lower_y);
            Scalar color = new Scalar(0, 0, 255);
            int thickness = 2;
            Imgproc.rectangle(srcFinal, p1, p2, color, thickness);

        }     

    }

    private void update() {

        while (!analysis_completed) {

            DetectContour();
            HighGui.imshow("HSV", drawing);
            HighGui.imshow("RectFinal", srcFinal);
            HighGui.waitKey(1);

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

        for (ZoneCarotte currentCarotte : detected) {

            for (int i = 0; i < currentCarotte.w + currentCarotte.w; i += 10) {

                KmeanDetection kDetect = new KmeanDetection(currentCarotte.upper_x + i, currentCarotte.upper_y, 2, currentCarotte.h);
                carotteColor.add(kDetect.Match());

            }

        }

        //System.out.println(carotteColor.get(0).get(0));
        HighGui.imshow("original", resizeImg);
        HighGui.imshow("HSV", srcHsv);
        HighGui.imshow("mask", maskHsv);
        HighGui.imshow("maskInv", maskHsvInv);
       
        HighGui.imshow("Final", drawing);
        HighGui.imshow("RectFinal", srcFinal);
        HighGui.waitKey(1);

    }
}

public class automatic_detection_w_target_v2 {

    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new automatic_detection(args);

    }

}
