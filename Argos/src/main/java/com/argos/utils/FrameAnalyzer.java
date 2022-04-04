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

    public TargetZoneFinderMethod targetZoneFinderMethod = TargetZoneFinderMethod.HSV;
    
    public HSVTargetZoneFinder HSVTargetZoneFinder = new HSVTargetZoneFinder();
    public KmeansTargetZoneFinder KmeansTargetZoneFinder = new KmeansTargetZoneFinder();
    
    public static enum TargetZoneFinderMethod {
        HSV, K_MEANS
    }
    
    
    public List<TargetZone> getTargetZonesFromImage(Mat img) {
        switch (this.targetZoneFinderMethod){
            case HSV:
                return HSVTargetZoneFinder.getDetectedTargetZones(img);
            case K_MEANS:
                return KmeansTargetZoneFinder.getDetectedTargetZones(img);
        }
        return null;
    }

    public TargetZoneFinderMethod getTargetZoneFinderMethod() {
        return targetZoneFinderMethod;
    }

    public void setTargetZoneFinderMethod(TargetZoneFinderMethod TargetZoneFinderMethod) {
        this.targetZoneFinderMethod = TargetZoneFinderMethod;
    }
    
    
    
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        FrameAnalyzer analyzer = new FrameAnalyzer();
        Mat img = Imgcodecs.imread("C:\\Users\\Ivan\\Google Drive\\FAC\\M1\\S2\\Gestion de Projets\\Gestion-de-projet\\Images\\test2.jpg");
        analyzer.HSVTargetZoneFinder.automaticallyCalibrate(img);
        
        List<TargetZone> zones = analyzer.getTargetZonesFromImage(img);
        
        TargetZoneAnalyzer tga = new TargetZoneAnalyzer(10, 90);
        
        tga.cutAndAnalyzeTargetZones(zones, img);
        tga.getDetectedPOI().forEach(poi -> 
                System.out.println(poi.toJSON())
        );
        
    }
}
