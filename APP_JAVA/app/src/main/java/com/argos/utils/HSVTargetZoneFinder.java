package com.argos.utils;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Class defining an object used to find target zones based on HSV values and
 * calibrating itself
 *
 * @author Ivan
 */
public class HSVTargetZoneFinder {

    private Integer hue_value = 0;
    private Integer saturation_value = 0;
    private Integer value_value = 0;
    private Integer min_area_contour = 1750;

    private final boolean debug = true;

    private Integer targetNumber = 100;
    private int max_saturation_val = 100;

    /**
     * Gets a list of detected TargetZone from an image.
     * Uses the HSV format and subsequent thresholding on
     * masked images of the original image in order to 
     * ignore background and get only the relevant figures.
     *
     * @param src_image Mat representing an image
     * @return list of TargetZone
     */
    public List<TargetZone> getDetectedTargetZones(Mat src_image) {

        List<TargetZone> detectedZones = new ArrayList<>();
        
        Mat hsv_image = new Mat();
        Mat hsv_mask = new Mat();
        Mat hsv_mask_inverted = new Mat();
        Mat source_bitwised_and = new Mat();
        Mat hierarchy = new Mat();
        Mat srcclone = src_image.clone();

        Imgproc.cvtColor(src_image, hsv_image, Imgproc.COLOR_BGR2HSV);

        Core.inRange(hsv_image,
                new Scalar(hue_value, saturation_value, value_value),
                new Scalar(255, 255, 255),
                hsv_mask);

        Imgproc.threshold(hsv_mask, hsv_mask_inverted, 0, 255, Imgproc.THRESH_BINARY_INV);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(5, 5));
        
        Imgproc.dilate(hsv_mask_inverted, hsv_mask_inverted, kernel);
        
        Core.bitwise_and(src_image, src_image, source_bitwised_and, hsv_mask_inverted);
        
        
        
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

            if (newZone.getArea() >= min_area_contour && !newZone.existsInArray(detectedZones)) {
                detectedZones.add(newZone);
            }

        }

        return detectedZones;

    }

    /**
     * Gets a list of Contours from an image
     *
     * @param src_image Mat representing an image
     * @return list of MatOfPoint
     */
    public List<MatOfPoint> getDetectedContours(Mat src_image) {

        List<MatOfPoint> detectedContours = new ArrayList<>();

        Mat hsv_image = new Mat();
        Mat hsv_mask = new Mat();
        Mat hsv_mask_inverted = new Mat();
        Mat source_bitwised_and = new Mat();
        Mat hierarchy = new Mat();

        Imgproc.cvtColor(src_image, hsv_image, Imgproc.COLOR_BGR2HSV);

        detectedContours = new ArrayList<>();

        Core.inRange(hsv_image,
                new Scalar(hue_value, saturation_value, value_value),
                new Scalar(255, 255, 255),
                hsv_mask);

        Imgproc.threshold(hsv_mask, hsv_mask_inverted, 0, 255, Imgproc.THRESH_BINARY_INV);

        Core.bitwise_and(src_image, src_image, source_bitwised_and, hsv_mask_inverted);

        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(hsv_mask_inverted, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        return detectedContours;

    }

    /**
     * This method goes through all possible values (from 0 to
     * max_saturation_value) of the saturation value in order to automatically
     * find the most optimal value based on the number of TargetZones found by the
     * finder method (prioritizing highest number)
     *
     * @param sourceCalibration source image to base the calibration upon
     */
    public void automaticallyCalibrate(Mat sourceCalibration) {

        List<TargetZone> detectedZones = new ArrayList<>();

        saturation_value = 10;
        boolean calibration_completed = false;
        int minDiff = targetNumber;
        int optimalSaturation = saturation_value;

        while (!calibration_completed) {

            detectedZones = getDetectedTargetZones(sourceCalibration);

            if (saturation_value < max_saturation_val) {

                saturation_value += 1;

            } else {

                calibration_completed = true;
                saturation_value = optimalSaturation;
                detectedZones = getDetectedTargetZones(sourceCalibration);

            }
            
            System.out.println("Saturation: " + saturation_value + "  Zones: " + detectedZones.size());

            if (Math.abs(targetNumber - detectedZones.size() + 1) <= minDiff) {

                minDiff = Math.abs(targetNumber - detectedZones.size() + 1);
                optimalSaturation = saturation_value;

            }
        }

        saturation_value = optimalSaturation;

    }

    public Integer getHue_value() {
        return hue_value;
    }

    public void setHue_value(Integer hue_value) {
        this.hue_value = hue_value;
    }

    public Integer getSaturation_value() {
        return saturation_value;
    }

    public void setSaturation_value(Integer saturation_value) {
        this.saturation_value = saturation_value;
    }

    public Integer getValue_value() {
        return value_value;
    }

    public void setValue_value(Integer value_value) {
        this.value_value = value_value;
    }

    public Integer getMin_area_contour() {
        return min_area_contour;
    }

    public void setMin_area_contour(Integer min_area_contour) {
        this.min_area_contour = min_area_contour;
    }

    public Integer getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(Integer targetNumber) {
        this.targetNumber = targetNumber;
    }

    public int getMax_saturation_val() {
        return max_saturation_val;
    }

    public void setMax_saturation_val(int max_saturation_val) {
        this.max_saturation_val = max_saturation_val;
    }
}
