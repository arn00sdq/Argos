package com.argos.utils;

import org.opencv.core.Mat;

/**
 *
 * @author Ivan
 */
public class FrameAnalyzer {
    
    public static POI[] getPOIsFromImage(Mat img) {
        //Analyse image, extraction des POI
        POI[] pointsOfInterest = new POI[]{
            new POI(new String[]{"argile"}, 140, 220, 100, 110),
            new POI(new String[]{"sable"}, 330, 400, 50, 70)
        };
        return pointsOfInterest;
    }
}
