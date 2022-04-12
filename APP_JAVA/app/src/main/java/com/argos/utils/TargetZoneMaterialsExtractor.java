/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.opencv.core.Mat;

/**
 *
 * @author MSI
 */
public class TargetZoneMaterialsExtractor {

    MaterialAnalyzer materialAnalyzer = new MaterialAnalyzer();

    private int lengthOfCut = 5;
    private int numberOfClusters = 3;
    private int numberOfIterations = 3;
    /**
     * @value Confidence: degree of accuracy for comparison ranging between 1 and 100
     * 100 means color values must exactly match Example: with a confidence of
     * 90 and checking against sand RGB(50,50,50), matching function will
     * compare if a new Color, for example RGB(10,34,65) has values between
     * 50 +- 255 * (1 - confidence / 100) / 2
     */
    private int confidence = 90;

    /**
     * Cuts each target zone in lengthOfCut pieces and extracts material data
     * from it
     *
     * @param targetZones List of Target Zones to analyze
     * @param image Image in which the target zones are present
     * @return A list of PointsOfInterest containing data about the materials
     * present in the zone
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected List<PointOfInterest> getPOIFromTargetZonesMaterials(List<TargetZone> targetZones, Mat image) {

        List<PointOfInterest> analyzedPOI = new ArrayList<>();

        if (targetZones.isEmpty()) {
            return analyzedPOI;
        }

        TargetZone largestTargetZone = getLargestTargetZone(targetZones);

        targetZones.forEach(zone -> {

            Hashtable<String, Integer> materialPresencesInTargetZone = new Hashtable<>();

            int analyzed_upper_x_zone = zone.upper_x,
                    analyzed_upper_y_zone = zone.upper_y,
                    analyzed_width_zone = zone.w,
                    analyzed_height_zone = zone.h;

            if (largestTargetZone.w > largestTargetZone.h) {
                /*Width is greater than height, we assume the cores are positionned horizontally*/

                while (analyzed_width_zone > lengthOfCut) {

                    List<String> presentMaterialsInZone = materialAnalyzer.
                            getMaterialsInsideZoneOfImage(image, analyzed_upper_x_zone, analyzed_upper_y_zone, lengthOfCut, analyzed_height_zone, numberOfClusters,numberOfIterations, confidence);
                    materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, presentMaterialsInZone);

                    analyzed_upper_x_zone += lengthOfCut;
                    analyzed_width_zone -= lengthOfCut;

                }

                List<String> presentMaterialsInZone = materialAnalyzer.
                        getMaterialsInsideZoneOfImage(image, analyzed_upper_x_zone, analyzed_upper_y_zone, analyzed_width_zone, analyzed_height_zone, numberOfClusters,numberOfIterations, confidence);
                materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, presentMaterialsInZone);

            } else {
                /*Width is smaller than height, we assume the cores are positionned vertically*/
                while (analyzed_height_zone > lengthOfCut) {

                    List<String> presentMaterialsInZone = materialAnalyzer.
                            getMaterialsInsideZoneOfImage(image, analyzed_upper_x_zone, analyzed_upper_y_zone, analyzed_width_zone, lengthOfCut, numberOfClusters,numberOfIterations, confidence);
                    materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, presentMaterialsInZone);

                    analyzed_upper_y_zone += lengthOfCut;
                    analyzed_height_zone -= lengthOfCut;

                }

                List<String> presentMaterialsInZone = materialAnalyzer.
                        getMaterialsInsideZoneOfImage(image, analyzed_upper_x_zone, analyzed_upper_y_zone, analyzed_width_zone, analyzed_height_zone, numberOfClusters,numberOfIterations, confidence);
                materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, presentMaterialsInZone);

            }

            List<String> materialsList = new ArrayList<>();
            materialPresencesInTargetZone.keySet().forEach(mat -> materialsList.add(mat));

            PointOfInterest POI = new PointOfInterest(materialsList, zone.w, zone.h, zone.upper_x, zone.upper_y);
            POI.setMaterialProportions(getZoneMaterialsPercentages(materialPresencesInTargetZone));
            POI.setLineColor(materialAnalyzer.getColorOfPredominantMaterial(materialPresencesInTargetZone));
            analyzedPOI.add(POI);
        });
        return analyzedPOI;
    }

    /**
     * Calculates the percentages of presence of each material
     *
     * @param materialPresencesInTargetZone HashTable where presence of each
     * material has been counted
     * @return HashTable containing percentages instead of the count
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Hashtable<String, Integer> getZoneMaterialsPercentages(Hashtable<String, Integer> materialPresencesInTargetZone) {

        Hashtable<String, Integer> materialPercentages = new Hashtable<>();
        float total = materialPresencesInTargetZone.values().stream().mapToInt(Integer::intValue).sum();

        materialPresencesInTargetZone.forEach((material, count) -> {
            Float mean = count.floatValue() / total * 100;
            materialPercentages.put(material, mean.intValue());
        });

        return materialPercentages;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Hashtable<String, Integer> addMaterialPresences(Hashtable<String, Integer> materialPresenceCount, List<String> detectedMaterials) {

        Hashtable<String, Integer> presencesCopy = materialPresenceCount;

        detectedMaterials.forEach(material -> {
            if (presencesCopy.get(material) == null) {
                presencesCopy.put(material, 1);
            } else {
                presencesCopy.put(material, presencesCopy.get(material) + 1);
            }
        });

        return presencesCopy;
    }

    private TargetZone getLargestTargetZone(List<TargetZone> detectedTargetZones) {

        TargetZone largestTargetZone = detectedTargetZones.get(0);

        for (TargetZone currentCarotte : detectedTargetZones) {

            if (currentCarotte.getArea() > largestTargetZone.getArea()) {
                largestTargetZone = currentCarotte;
            }
        }

        return largestTargetZone;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Mat getKmeanMask(Mat img) {
        return materialAnalyzer.getKmeanMask(img, numberOfClusters, numberOfIterations);
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public void setNumberOfClusters(int numberOfClusters) { this.numberOfClusters = numberOfClusters;}

    public void setNumberOfIterations(int numberOfIterations) { this.numberOfIterations = numberOfIterations;}

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public int getLengthOfCut() {
        return lengthOfCut;
    }


    public void setLengthOfCut(int lengthOfCut) {
        this.lengthOfCut = lengthOfCut;
    }

}
