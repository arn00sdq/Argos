/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.argos.utils.PaletteMapper.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MaterialAnalyzer {

    private final MaterialIdentifier materialIdentifier = new MaterialIdentifier(new PaletteMapper(paletteTypes.DEFAULT_PALETTE));

    /**
     * This method uses the k-means algorithm in order to extract, from an area
     * of an image, a number of predominant materials matching the found colors
     * equal to the number of clusters
     *
     * @param analyzedImage Image to analyze
     * @param upper_x Upper X coordinate of the zone
     * @param upper_y Upper Y coordinate of the zone
     * @param w Width of the zone
     * @param h Height of the zone
     * @param clustersNumber Number of clusters for the k-means material
     * analysis
     * @param confidence confidence of the comparison between colors
     * @return A list of names of materials
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> getMaterialsInsideZoneOfImage(Mat analyzedImage, int upper_x, int upper_y, int w, int h, int clustersNumber, int attemptNumber, int confidence) {

        Mat centers = new Mat();
        Mat labels = new Mat();

        Rect rectZone = new Rect(upper_x, upper_y, w, h);
        Mat imageZone = new Mat(analyzedImage, rectZone);

        Mat img_clone = new Mat();
        Imgproc.cvtColor(imageZone, img_clone, Imgproc.COLOR_RGB2BGR);

        Mat imgKmean = img_clone.clone();

        imgKmean = img_clone.reshape(1, imageZone.rows() * imageZone.cols());
        imgKmean.convertTo(imgKmean, CvType.CV_32F);

        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, attemptNumber, 1.0);

        Core.kmeans(imgKmean, clustersNumber, labels, criteria, attemptNumber, Core.KMEANS_PP_CENTERS, centers);

        String dump = centers.dump();
        Color[] colorsArray = extractRgbFromString(dump, clustersNumber);

        return materialIdentifier.getMaterialNamesFromColors(colorsArray, confidence);

    }

    public Mat getKmeanMask(Mat src_image, int clustersNumber, int attemptNumber) {

        Mat srcclone = src_image.clone();
        Mat srctemp = new Mat();
        Imgproc.cvtColor(src_image,srctemp,Imgproc.COLOR_RGBA2RGB);

        Mat bestLabels = new Mat();
        TermCriteria criteria = new TermCriteria();
        int flags = Core.KMEANS_PP_CENTERS;
        Mat centers = new Mat();

        srctemp.convertTo(srctemp, CvType.CV_32F);
        Mat data = srctemp.reshape(1, (int)srctemp.total());

        Core.kmeans(data, clustersNumber, bestLabels, criteria, attemptNumber, flags, centers);

        Mat draw = new Mat((int) srctemp.total(), 1, CvType.CV_32FC3);
        Mat colors = centers.reshape(3, clustersNumber);
        for (int i = 0; i < clustersNumber; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
            Mat col = colors.row(i); // can't use the Mat directly with setTo() (see #19100)
            double d[] = col.get(0, 0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0], d[1], d[2]), mask);
        }

        draw = draw.reshape(3, srctemp.rows());
        draw.convertTo(draw, CvType.CV_8U);
        Imgproc.cvtColor(draw,draw,Imgproc.COLOR_RGB2RGBA);

        return draw;

    }

    /**
     * Gets the Color value of the most present material in a hash table of
     * material proportions
     *
     * @param materialProportions Hash table defining the proportions of each
     * material
     * @return The color of the predominant material
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Color getColorOfPredominantMaterial(Hashtable<String, Integer> materialProportions) {

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
     *
     * @param dump String containing Color data
     * @param clusters Number of clusters to extract
     * @return An array of colors of size equal to clusters
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
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

                colorsArray[row] = Color.valueOf((int) r, (int) g, (int) b, 255);

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
