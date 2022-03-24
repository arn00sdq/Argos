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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Ivan
 */
public class HSVCalibration {

    private Integer hue_value_min = 0;
    private Integer saturation_value_min = 0;
    private Integer value_value_min = 0;

    private Integer hue_value_max = 254;
    private Integer saturation_value_max = 150;
    private Integer value_value_max = 254;

    private Integer min_area_contour = 1500;
    private Integer target = 100;
    
    private boolean debug = true;

    Mat hsv_image = new Mat();
    Mat hsv_image_resize = new Mat();
    Mat hsv_resize = new Mat();
    Mat hsv_mask = new Mat();
    Mat hsv_mask_inverted = new Mat();
    Mat source_bitwised_and = new Mat();
    Mat hierarchy = new Mat();

    int s_val = 1;
    boolean calibration_completed = false;
    int minDiff = target;
    int optimalSaturation = 1;

    int maxZone = 0;
    ZoneCarotte zoneMaxAire;

    List<ZoneCarotte> detectedZones;
    ArrayList<List<String>> carotteColor = new ArrayList<>();

    public HSVCalibration(Mat sourceCalibration) {

        hsv_image_resize = Mat.zeros(sourceCalibration.size(), CvType.CV_8U);
        Size sz = new Size(400, 400);
        Imgproc.resize(sourceCalibration, hsv_image_resize, sz);

        source_bitwised_and = sourceCalibration.clone();

        updateCalibration();

    }

    public void calibrate() {

        hsv_image = new Mat();
        hsv_mask = new Mat();
        hsv_mask_inverted = new Mat();
        source_bitwised_and = new Mat();
        hierarchy = new Mat();

        Imgproc.cvtColor(hsv_image_resize, hsv_image, Imgproc.COLOR_BGR2HSV);

        detectedZones = new ArrayList<>();

        Core.inRange(hsv_image,
                new Scalar(0, s_val, 0),
                new Scalar(255, 255, 255),
                hsv_mask);

        Imgproc.threshold(hsv_mask, hsv_mask_inverted, 0, 255, Imgproc.THRESH_BINARY_INV);

        Core.bitwise_and(hsv_image_resize, hsv_image_resize, source_bitwised_and, hsv_mask_inverted);
        HighGui.imshow("RectFinal", hsv_mask);
            HighGui.waitKey(1);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(hsv_mask_inverted, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        System.out.println(contours.size());

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        for (int i = 0; i < contours.size(); i++) {

            contoursPoly[i] = new MatOfPoint2f(contours.get(i).toArray());
            double peri = Imgproc.arcLength(contoursPoly[i], true);
            Imgproc.approxPolyDP(contoursPoly[i], contoursPoly[i], 0.02 * peri, true);

            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            ZoneCarotte newZone = new ZoneCarotte(boundRect[i].x, boundRect[i].y, boundRect[i].x + boundRect[i].width,
                    boundRect[i].y + boundRect[i].height, boundRect[i].width, boundRect[i].height);

           if (newZone.getArea() > 1500 && !newZone.existsInArray(detectedZones)) {
                
                if(newZone.getArea() > this.maxZone){
                    
                    this.maxZone = newZone.getArea();
                    zoneMaxAire = newZone;
                    
                }
                detectedZones.add(newZone);
            }

        }

    }

    private void updateCalibration() {

        while (!calibration_completed) {

            calibrate();

            if (this.s_val < 150) {

                this.s_val += 2;

            } else {

                calibration_completed = true;
                this.s_val = optimalSaturation;
                calibrate();

            }

            if (Math.abs(target - detectedZones.size() + 1) < minDiff) {

                minDiff = Math.abs(target - detectedZones.size() + 1);
                optimalSaturation = this.s_val;

            }

        }

        if (this.debug){
            
            System.out.println("optimal saturation" + optimalSaturation);

            HighGui.imshow("original", hsv_image_resize);
            HighGui.imshow("HSV", hsv_image);
            HighGui.imshow("mask", hsv_mask);
            HighGui.imshow("maskInv", hsv_mask_inverted);

            HighGui.imshow("RectFinal", source_bitwised_and);
            HighGui.waitKey(1);
            
        }
        

    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        HSVCalibration cal = new HSVCalibration(Imgcodecs.imread("C:\\Users\\MSI\\Documents\\NetBeansProjects\\Gestion-de-projet\\mavenproject1\\src\\main\\java\\com\\argos\\utils\\carrote2.jpg"));
    }

    public Integer getHue_value_min() {
        return hue_value_min;
    }

    public void setHue_value_min(Integer hue_value_min) {
        this.hue_value_min = hue_value_min;
    }

    public Integer getSaturation_value_min() {
        return saturation_value_min;
    }

    public void setSaturation_value_min(Integer saturation_value_min) {
        this.saturation_value_min = saturation_value_min;
    }

    public Integer getValue_value_min() {
        return value_value_min;
    }

    public void setValue_value_min(Integer value_value_min) {
        this.value_value_min = value_value_min;
    }

    public Integer getHue_value_max() {
        return hue_value_max;
    }

    public void setHue_value_max(Integer hue_value_max) {
        this.hue_value_max = hue_value_max;
    }

    public Integer getSaturation_value_max() {
        return saturation_value_max;
    }

    public void setSaturation_value_max(Integer saturation_value_max) {
        this.saturation_value_max = saturation_value_max;
    }

    public Integer getValue_value_max() {
        return value_value_max;
    }

    public void setValue_value_max(Integer value_value_max) {
        this.value_value_max = value_value_max;
    }

    public Integer getMin_area_contour() {
        return min_area_contour;
    }

    public void setMin_area_contour(Integer min_area_contour) {
        this.min_area_contour = min_area_contour;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public List<ZoneCarotte> getDetectedZones() {
        return detectedZones;
    }

}
