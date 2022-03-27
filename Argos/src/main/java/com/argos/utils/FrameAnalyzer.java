package com.argos.utils;

import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Ivan
 */
public class FrameAnalyzer {

    public ContourFindingMethods ContourFindingMethod = ContourFindingMethods.HSV;
    public HSVTargetZoneFinder HSVTargetZoneFinder = new HSVTargetZoneFinder();
    
    public static enum ContourFindingMethods {
        HSV, K_MEANS
}
    
    
    public List<TargetZone> getTargetZonesFromImage(Mat img) {
        //Analyse image, extraction des POI
        switch (this.ContourFindingMethod){
            case HSV:
                return HSVTargetZoneFinder.getDetectedTargetZones(img);
        }
        return null;
    }
    
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        FrameAnalyzer analyzer = new FrameAnalyzer();
        List<TargetZone> zones = analyzer.getTargetZonesFromImage
        (Imgcodecs.imread("C:\\Users\\Ivan\\Google Drive\\FAC\\M1\\S2\\Gestion de Projets\\Gestion-de-projet\\mavenproject1\\src\\main\\java\\com\\argos\\utils\\test2.jpg"));
        zones.forEach(zone -> {
            System.out.println(zone.toString());
        });
        
    }
}
