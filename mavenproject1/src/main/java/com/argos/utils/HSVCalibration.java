/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Ivan
 */
public class HSVCalibration {

    private Integer hue_value_min = 1;
    private Integer saturation_value_min = 1;
    private Integer value_value_min = 1;

    private Integer hue_value_max = 254;
    private Integer saturation_value_max = 254;
    private Integer value_value_max = 254;

    private Integer min_area_contour = 1500;
    private Integer target = 50;

    List<ZoneCarotte> detectedZones;

    public void calibrate(Mat sourceCalibration) {

        boolean calibration_completed = false;
        int minDiff = target;
        int optimalSaturation = 1;
        int s_val = 1;

        Mat hsv_image = new Mat();
        Mat hsv_mask = new Mat();
        Mat hsv_mask_inverted = new Mat();
        Mat source_bitwised_and = new Mat();
        Mat hierarchy = new Mat();

        Imgproc.cvtColor(sourceCalibration, hsv_image, Imgproc.COLOR_BGR2HSV);

        while (!calibration_completed) {

            detectedZones = new ArrayList<>();

            Core.inRange(hsv_image,
                    new Scalar(hue_value_min, s_val, value_value_min),
                    new Scalar(hue_value_max, saturation_value_max, value_value_max),
                    hsv_mask);

            Imgproc.threshold(hsv_mask, hsv_mask_inverted, 0, 255, Imgproc.THRESH_BINARY_INV);

            Core.bitwise_and(sourceCalibration, sourceCalibration, source_bitwised_and, hsv_mask_inverted);

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

                if (newZone.getArea() > this.min_area_contour && !newZone.existsInArray(detectedZones)) {
                    detectedZones.add(newZone);
                }
            }

            if (s_val < saturation_value_max) {

                s_val += 2;

            } else {

                calibration_completed = true;
                s_val = optimalSaturation;

            }

            if (Math.abs(target - detectedZones.size() + 1) < minDiff) {

                minDiff = Math.abs(target - detectedZones.size() + 1);
                optimalSaturation = s_val;

            }
            
            System.out.println("optimal saturation" + optimalSaturation);

        }

    }

    
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        HSVCalibration cal = new HSVCalibration();
        cal.calibrate(Imgcodecs.imread("C:\\Users\\Ivan\\Google Drive\\FAC\\M1\\S2\\Gestion de Projets\\Gestion-de-projet\\mavenproject1\\src\\main\\java\\com\\argos\\utils\\test2.jpg"));
        System.out.println(cal.getDetectedZones().size() + " zones detected");
        for (ZoneCarotte detectedZone : cal.getDetectedZones()) {
            System.out.println(detectedZone.toString());
        }
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
