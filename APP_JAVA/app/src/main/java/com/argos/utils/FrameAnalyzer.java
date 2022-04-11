package com.argos.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Ivan
 */
public class FrameAnalyzer {

    public TargetZoneFinderMethod targetZoneFinderMethod = TargetZoneFinderMethod.HSV;
    
    public HSVTargetZoneFinder HSVTargetZoneFinder = new HSVTargetZoneFinder();
    public KmeansTargetZoneFinder KmeansTargetZoneFinder = new KmeansTargetZoneFinder();
    
    public TargetZoneMaterialsExtractor targetZoneMaterialsExtractor = new TargetZoneMaterialsExtractor();
    
    public static enum TargetZoneFinderMethod {
        HSV, K_MEANS
    }
    
    
    /**
     * Gets a list of detected target zones on an image
     * @param img The image to analyze
     * @return A list of Target Zones
     */
    public List<TargetZone> getTargetZonesFromImage(Mat img) {
        switch (this.targetZoneFinderMethod){
            case HSV:
                return HSVTargetZoneFinder.getDetectedTargetZones(img);
            case K_MEANS:
                return KmeansTargetZoneFinder.getDetectedTargetZones(img);
        }
        return null;
    }
    /**
     * Gets a list of POIs containing data of the presence of materials
     * @param img The image to analyze
     * @return A list of Points of Interest
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<PointOfInterest> getDetailedPOIsFromImage(Mat img) {
        return targetZoneMaterialsExtractor.getPOIFromTargetZonesMaterials(getTargetZonesFromImage(img), img);
    }

    public TargetZoneFinderMethod getTargetZoneFinderMethod() {
        return targetZoneFinderMethod;
    }

    public void setTargetZoneFinderMethod(TargetZoneFinderMethod TargetZoneFinderMethod) {
        this.targetZoneFinderMethod = TargetZoneFinderMethod;
    }
    
    
    // function for java only
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        FrameAnalyzer analyzer = new FrameAnalyzer();
        Mat img = Imgcodecs.imread("C:\\Users\\Ivan\\Google Drive\\FAC\\M1\\S2\\Gestion de Projets\\Gestion-de-projet\\Images\\test2.jpg");
        
        analyzer.HSVTargetZoneFinder.setSaturation_value(45);
        //analyzer.HSVTargetZoneFinder.automaticallyCalibrate(img);
        analyzer.targetZoneMaterialsExtractor.setComparisonConfidence(50);
        analyzer.targetZoneMaterialsExtractor.setNumberOfCuts(20);
        analyzer.getDetailedPOIsFromImage(img).forEach(POI -> System.out.println(POI.toJSON()));
        
    }*/
}
