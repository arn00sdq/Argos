/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author MSI
 */
public class FindContourKmean {

    private Integer target = 100;
    
    private boolean debug = true;

    Mat kmean_mask = new Mat();
    Mat hsv_image_resize = new Mat();
    Mat hsv_resize = new Mat();
    Mat hsv_mask = new Mat();
    Mat hsv_mask_inverted = new Mat();
    Mat result_kmean = new Mat();
    Mat hierarchy = new Mat();

    int s_val = 1;
    boolean calibration_completed = false;
    int minDiff = target;
    int maxZone = 0;
    TargetZone zoneMaxAire;

    List<TargetZone> detectedZones;
    ArrayList<List<String>> carotteColor = new ArrayList<>();

    public FindContourKmean(Mat sourceCalibration) {

        hsv_image_resize = Mat.zeros(sourceCalibration.size(), CvType.CV_8U);
        Size sz = new Size(400, 400);
        Imgproc.resize(sourceCalibration, hsv_image_resize, sz);

        calibrate();

    }

    public void calibrate() {

        kmean_mask = new Mat();
        hsv_mask = new Mat();
        hsv_mask_inverted = new Mat();
        hierarchy = new Mat();
        
        Mat source_bitwised_and = new Mat();
        detectedZones = new ArrayList<>();
        
        int K=5;
        Mat bestLabels = new Mat();
        TermCriteria criteria = new TermCriteria();
        int attempts=5;
        int flags = Core.KMEANS_PP_CENTERS;
        Mat centers=new Mat();

        //Imgproc.cvtColor(imageROI,img_clone,Imgproc.COLOR_RGB2BGR);      
        
        Mat data = hsv_image_resize.reshape (1, hsv_image_resize.rows() * hsv_image_resize.cols());
        data.convertTo(data,CvType.CV_32F);
                  
        Core.kmeans​(data, K, bestLabels, criteria, attempts, flags, centers);
        
        Mat draw = new Mat((int)hsv_image_resize.total(),1, CvType.CV_32FC3);
        Mat colors = centers.reshape(3,K);
        for (int i=0; i<K; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
            Mat col = colors.row(i); // can't use the Mat directly with setTo() (see #19100)  
            double d[] = col.get(0,0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0],d[1],d[2]), mask);
        }
        
        draw = draw.reshape(3, hsv_image_resize.rows());
        draw.convertTo(draw, CvType.CV_8U);
        
        Imgproc.cvtColor(draw, kmean_mask, Imgproc.COLOR_BGR2GRAY);       
        Imgproc.threshold(kmean_mask, hsv_mask_inverted, 150, 255, Imgproc.THRESH_BINARY);
        Core.bitwise_and(hsv_image_resize, hsv_image_resize, source_bitwised_and, hsv_mask_inverted);
       
        
        List<MatOfPoint> contours = new ArrayList<>();
        
        Imgproc.findContours(hsv_mask_inverted, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        for (int i = 0; i < contours.size(); i++) {
                      
            contoursPoly[i] = new MatOfPoint2f(contours.get(i).toArray());
            double peri = Imgproc.arcLength(contoursPoly[i], true);
            Imgproc.approxPolyDP(contoursPoly[i], contoursPoly[i], 0.02 * peri, true);
            
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            
            TargetZone newZone = new TargetZone(boundRect[i].x, boundRect[i].y, boundRect[i].x + boundRect[i].width,
                    boundRect[i].y + boundRect[i].height, boundRect[i].width, boundRect[i].height);

           if (newZone.getArea() > 1500 && !newZone.existsInArray(detectedZones)) {
                
               /* if(newZone.getArea() > this.maxZone){
                    
                    this.maxZone = newZone.getArea();
                    zoneMaxAire = newZone;
                    
                }*/
               
                detectedZones.add(newZone);
            }

        }
        System.out.println(detectedZones.size());
        
         for (int k = 0; k < detectedZones.size(); k++) {
            Point p1 = new Point(detectedZones.get(k).upper_x, detectedZones.get(k).upper_y);
            Point p2 = new Point(detectedZones.get(k).lower_x, detectedZones.get(k).lower_y);
            Scalar color = new Scalar(0, 0, 255);
            int thickness = 2;
            Imgproc.rectangle(source_bitwised_and, p1, p2, color, thickness);

        }
         
        if (this.debug){
            
            HighGui.imshow("image_bitwised", source_bitwised_and);  
            HighGui.imshow("2GRAY", kmean_mask);  
            HighGui.imshow("resize_original", hsv_image_resize);   
            HighGui.imshow("KmeanImg", draw);
            HighGui.imshow("inv_mask", hsv_mask_inverted);

            HighGui.waitKey(1);
            
        }

    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FindContourKmean cal = new FindContourKmean(Imgcodecs.imread("C:\\Users\\MSI\\Documents\\NetBeansProjects\\Gestion-de-projet\\Argos\\src\\main\\java\\com\\argos\\utils\\carrote2.jpg"));
    }

}
