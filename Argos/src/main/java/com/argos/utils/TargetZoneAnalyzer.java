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
public class TargetZoneAnalyzer {

    private List<PointOfInterest> detectedPOI = new ArrayList<>();

    private int numberOfCuts = 10;
    private int precision = 90;

    public TargetZoneAnalyzer(int cuts, int precision) {
        this.numberOfCuts = cuts;
        this.precision = precision;
    }

    public TargetZoneAnalyzer() {
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

    public void cutAndAnalyzeTargetZones(List<TargetZone> targetZones, Mat image) {

        TargetZone largestTargetZone = getLargestTargetZone(targetZones);

        targetZones.forEach(zone -> {

            Hashtable<String, Integer> materialPresencesInTargetZone = new Hashtable<>();

            if (largestTargetZone.w > largestTargetZone.h) {

                for (int i = 0; i < zone.w; i += zone.w / numberOfCuts) {

                    MaterialAnalyzer analyzer = new MaterialAnalyzer(image, precision);

                    if (analyzer.zoneContainsMaterials(zone.upper_x + i, zone.upper_y, zone.w / numberOfCuts, zone.h)) {
                        materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, analyzer.getExistingMaterials());
                    }
                }
            } else {
                for (int i = 0; i < zone.w; i += zone.w / numberOfCuts) {

                    MaterialAnalyzer analyzer = new MaterialAnalyzer(image, precision);

                    if (analyzer.zoneContainsMaterials(zone.upper_x, zone.upper_y + i, zone.w, zone.h / numberOfCuts)) {
                        materialPresencesInTargetZone = addMaterialPresences(materialPresencesInTargetZone, analyzer.getExistingMaterials());
                    }
                }
            }
            List<String> materialsList = new ArrayList<>();
            materialPresencesInTargetZone.keySet().forEach(mat -> materialsList.add(mat));
                        
            PointOfInterest POI = new PointOfInterest(materialsList, zone.upper_x, zone.upper_y, zone.w, zone.h);
            POI.setMaterialProportions(getZoneMaterialsPercentages(materialPresencesInTargetZone));
            detectedPOI.add(POI);

        });
    }

    private Hashtable<String, Integer> getZoneMaterialsPercentages(Hashtable<String, Integer> materialPresencesInTargetZone) {
        
        Hashtable<String, Integer> materialPercentages = new Hashtable<>();
        float total = materialPresencesInTargetZone.values().stream().mapToInt(Integer::intValue).sum();

        materialPresencesInTargetZone.forEach((material, count) -> {
            Float mean = count.floatValue() / total * 100;
            materialPercentages.put(material, mean.intValue());
        });

        return materialPercentages;
    }

    private Hashtable<String, Integer> addMaterialPresences(Hashtable<String, Integer> materialPresence, List<String> detectedMaterials) {

        Hashtable<String, Integer> presencesCopy = materialPresence;

        detectedMaterials.forEach(material -> {
            if (presencesCopy.get(material) == null) {
                presencesCopy.put(material, 1);
            } else {
                presencesCopy.put(material, presencesCopy.get(material) + 1);
            }
        });

        return presencesCopy;
    }

    public int getNumberOfCuts() {
        return numberOfCuts;
    }

    public void setNumberOfCuts(int numberOfCuts) {
        this.numberOfCuts = numberOfCuts;
    }

    public List<PointOfInterest> getDetectedPOI() {
        return detectedPOI;
    }

}
