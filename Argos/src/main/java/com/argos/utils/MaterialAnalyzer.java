/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import com.argos.utils.PaletteMapper.*;
import java.awt.Color;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;

public class MaterialAnalyzer {

    private MaterialIdentifier materialIdentifier = new MaterialIdentifier(new PaletteMapper(paletteTypes.DEFAULT_PALETTE));

    /**
     * This method uses the k-means algorithm in order to extract, from an area of
     * an image, a number of predominant materials matching the found colors equal 
     * to the number of clusters
     * @param analyzedImage Image to analyze
     * @param upper_x Upper X coordinate of the zone
     * @param upper_y Upper Y coordinate of the zone
     * @param w Width of the zone
     * @param h Height of the zone
     * @return A list of names of materials
     */
    public List<String> getMaterialsInsideZoneOfImage(Mat analyzedImage, int upper_x, int upper_y, int w, int h, int clustersNumber) {

        Mat centers = new Mat();
        Mat labels = new Mat();

        
        Size sz = analyzedImage.size();

        while (upper_x + w >= sz.width)
            --w;
        while (upper_y + h >= sz.height)
            --h;   

        Rect rectCrop = new Rect(upper_x, upper_y, w, h);
        Mat imageROI = new Mat(analyzedImage, rectCrop);

        Mat img_clone = new Mat();

        Imgproc.cvtColor(imageROI, img_clone, Imgproc.COLOR_RGB2BGR);

        Mat imgKmean = img_clone.clone();

        imgKmean = img_clone.reshape(1, imageROI.rows() * imageROI.cols());
        imgKmean.convertTo(imgKmean, CvType.CV_32F);

        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, 10, 1.0);

        Core.kmeans(imgKmean, clustersNumber, labels, criteria, 10, Core.KMEANS_PP_CENTERS, centers);

        String dump = centers.dump();
        Color[] colorsArray = extractRgbFromString(dump, clustersNumber);

        return materialIdentifier.getMaterialNamesFromColors(colorsArray);

    }
    
    /**
     * Gets the Color value of the most present material in a hash table of material proportions
     * @param materialProportions Hash table defining the proportions of each material
     * @return The color of the predominant material
     */
    public Color getColorOfPredominantMaterial(Hashtable<String, Integer> materialProportions){
        
        Integer predominantMaterialMaxPresence = 0;
        String predominantMaterial = "unknown";
        
        for (Map.Entry<String, Integer> entry : materialProportions.entrySet()) {
            String material = entry.getKey();
            Integer proportion = entry.getValue();
            
            if (proportion > predominantMaterialMaxPresence) {
                predominantMaterialMaxPresence = proportion;
                predominantMaterial = material;
            }
        }
        
        return this.materialIdentifier.getPaletteMapper().getColorFromMaterial(predominantMaterial);
    }

    /**
     * Converts color data in string format to Color format
     * @param dump String containing Color data
     * @param clusters Number of clusters to extract
     * @return An array of colors of size equal to clusters
     */
    private Color[] extractRgbFromString(String dump, int clusters) {

        int row = 0;
        int col = 0;
        int element = 0;

        float r = 0, g = 0, b = 0;

        char[] charList = new char[100];

        Color[] colorsArray = new Color[clusters];

        for (int i = 0; i < dump.length(); i++) {
            if (dump.charAt(i) == '[') {
                continue;
            }

            if (dump.charAt(i) == ',') {
                String s = new String(charList).trim();
                switch (element) {
                    case 0:
                        r = Math.round(Float.parseFloat(s));
                        break;
                    case 1:
                        g = Math.round(Float.parseFloat(s));
                        break;
                    case 2:
                        b = Float.parseFloat(s);
                        break;
                    default:
                        break;
                }
                charList = new char[100];
                col = 0;
                element++;
                continue;
            }
            if (dump.charAt(i) == ';' || dump.charAt(i) == ']') {
                if (element == 2) {
                    String s = new String(charList).trim();
                    b = Math.round(Float.parseFloat(s));
                }

                colorsArray[row] = new Color((int) r, (int) g, (int) b, 255);

                row++;
                element = 0;
                charList = new char[100];
                col = 0;
                continue;
            }

            charList[col] = dump.charAt(i);

            col++;
        }

        return colorsArray;

    }

    public float getConfidence() {
        return materialIdentifier.getConfidence();
    }
    
    public void setConfidence(float confidence) {
        materialIdentifier.setConfidence(confidence);
    }
}
