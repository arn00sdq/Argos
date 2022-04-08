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

    private TargetZoneFinderMethod targetZoneFinderMethod = TargetZoneFinderMethod.HSV;

    public HSVTargetZoneFinder HSVTargetZoneFinder = new HSVTargetZoneFinder();
    public KmeansTargetZoneFinder KmeansTargetZoneFinder = new KmeansTargetZoneFinder();

    public TargetZoneMaterialsExtractor targetZoneMaterialsExtractor = new TargetZoneMaterialsExtractor();

    public static enum TargetZoneFinderMethod {
        HSV, K_MEANS
    }

    /**
     * Gets a list of detected target zones on an image
     *
     * @param img The image to analyze
     * @return A list of Target Zones
     */
    public List<TargetZone> getTargetZonesFromImage(Mat img) {
        switch (this.targetZoneFinderMethod) {
            case HSV:
                return HSVTargetZoneFinder.getDetectedTargetZones(img);
            case K_MEANS:
                return KmeansTargetZoneFinder.getDetectedTargetZones(img);
        }
        return null;
    }

    /**
     * Gets a list of POIs containing data of the presence of materials
     *
     * @param img The image to analyze
     * @return A list of Points of Interest
     */
    public List<PointOfInterest> getDetailedPOIsFromImage(Mat img) {
        return targetZoneMaterialsExtractor.getPOIFromTargetZonesMaterials(getTargetZonesFromImage(img), img);
    }

    public TargetZoneFinderMethod getTargetZoneFinderMethod() {
        return targetZoneFinderMethod;
    }

    public void setTargetZoneFinderMethod(TargetZoneFinderMethod TargetZoneFinderMethod) {
        this.targetZoneFinderMethod = TargetZoneFinderMethod;
    }

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat img = Imgcodecs.imread("C:\\Users\\Ivan\\Google Drive\\FAC\\M1\\S2\\Gestion de Projets\\Gestion-de-projet\\Images\\test2.jpg");

        /*EXEMPLE D'UTILISATION DE LA CLASSE FRAME ANALYZER POUR EXTRAIRE DES POI 
        CONTENANT DES DONNES CONCERNANT DES MATERIAUX A L'INTERIEUR DES CAROTTES
         */
        FrameAnalyzer analyzer = new FrameAnalyzer();

        analyzer.HSVTargetZoneFinder.setSaturation_value(45);
        analyzer.HSVTargetZoneFinder.setHue_value(45);
        analyzer.HSVTargetZoneFinder.setValue_value(45);
        analyzer.HSVTargetZoneFinder.setMin_area_contour(1000);
        /*ET/OU*/
        analyzer.HSVTargetZoneFinder.setAutomaticallyCalibratedS(img);

        analyzer.targetZoneMaterialsExtractor.setConfidence(90);
        analyzer.targetZoneMaterialsExtractor.setLengthOfCut(5);
        analyzer.targetZoneMaterialsExtractor.setNumberOfClusters(4);

        List<PointOfInterest> poiList = analyzer.getDetailedPOIsFromImage(img);

    }
}
