/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

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

    private int numberOfCuts = 10;
    private int numberOfClusters = 4;

    /**
     * Cuts each target zone in numberOfCuts pieces and extracts material data
     * from it
     * @param targetZones List of Target Zones to analyze
     * @param image Image in which the target zones are present
     * @return A list of PointsOfInterest containing data about the materials
     * present in the zone
     */
    protected List<PointOfInterest> getPOIFromTargetZonesMaterials(List<TargetZone> targetZones, Mat image) {

        List<PointOfInterest> analyzedPOI = new ArrayList<>();

        TargetZone largestTargetZone = getLargestTargetZone(targetZones);

        targetZones.forEach(zone -> {

            Hashtable<String, Integer> materialPresencesInTargetZone = new Hashtable<>();

            if (largestTargetZone.w > largestTargetZone.h) {

                for (int i = 0; i < zone.w; i += zone.w / numberOfCuts) {

                    List<String> presentMaterialsInZone = materialAnalyzer.getMaterialsInsideZoneOfImage(image, zone.upper_x + i, zone.upper_y, zone.w / numberOfCuts, zone.h, numberOfClusters);

                    materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, presentMaterialsInZone);
                }
            } else {
                for (int i = 0; i < zone.h; i += zone.h / numberOfCuts) {

                    List<String> presentMaterialsInZone = materialAnalyzer.getMaterialsInsideZoneOfImage(image, zone.upper_x, zone.upper_y + i, zone.w, zone.h / numberOfCuts, numberOfClusters);

                    materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, presentMaterialsInZone);
                }
            }
            List<String> materialsList = new ArrayList<>();
            materialPresencesInTargetZone.keySet().forEach(mat -> materialsList.add(mat));

            PointOfInterest POI = new PointOfInterest(materialsList, zone.upper_x, zone.upper_y, zone.w, zone.h);
            POI.setMaterialProportions(getZoneMaterialsPercentages(materialPresencesInTargetZone));
            POI.setLineColor(materialAnalyzer.getColorOfPredominantMaterial(materialPresencesInTargetZone));
            analyzedPOI.add(POI);

        });

        return analyzedPOI;
    }

    /**
     * Calculates the percentages of presence of each material
     * @param materialPresencesInTargetZone HashTable where presence of each material has been counted
     * @return HashTable containing percentages instead of the count
     */
    private Hashtable<String, Integer> getZoneMaterialsPercentages(Hashtable<String, Integer> materialPresencesInTargetZone) {

        Hashtable<String, Integer> materialPercentages = new Hashtable<>();
        float total = materialPresencesInTargetZone.values().stream().mapToInt(Integer::intValue).sum();

        materialPresencesInTargetZone.forEach((material, count) -> {
            Float mean = count.floatValue() / total * 100;
            materialPercentages.put(material, mean.intValue());
        });

        return materialPercentages;
    }

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

    public int getNumberOfCuts() {
        return numberOfCuts;
    }

    public void setNumberOfCuts(int numberOfCuts) {
        this.numberOfCuts = numberOfCuts;
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public void setNumberOfClusters(int numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    public float getConfidence() {
        return materialAnalyzer.getConfidence();
    }

    public void setComparisonConfidence(int confidence) {
        materialAnalyzer.setConfidence(confidence);
    }

}
