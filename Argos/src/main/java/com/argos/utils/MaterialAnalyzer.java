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

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;

public class MaterialAnalyzer {

    private Mat analyzedImage;

    private int clusters = 4;
    private PaletteMapper paletteMapper;
    private MaterialIdentifier materialIdentifier;

    private List<String> existingMaterials = new ArrayList<>();

    public MaterialAnalyzer(Mat image) {
        analyzedImage = image;
        paletteMapper = new PaletteMapper(paletteTypes.DEFAULT_PALETTE);
        materialIdentifier = new MaterialIdentifier(paletteMapper);
    }

    public MaterialAnalyzer(Mat image, int precision) {
        analyzedImage = image;
        paletteMapper = new PaletteMapper(paletteTypes.DEFAULT_PALETTE);
        materialIdentifier = new MaterialIdentifier(paletteMapper, precision);
    }

    public MaterialAnalyzer(Mat image, int precision, paletteTypes paletteType) {
        analyzedImage = image;
        paletteMapper = new PaletteMapper(paletteType);
        materialIdentifier = new MaterialIdentifier(paletteMapper, precision);
    }

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

    public boolean zoneContainsMaterials(int upper_x, int upper_y, int w, int h) {

        Mat centers = new Mat();
        Mat labels = new Mat();

        Size sz = analyzedImage.size();

        if (upper_x + w >= sz.width) {

            upper_x = (int) (sz.width - 1) - w;
        }
        if (upper_y + h >= sz.height) {

            upper_y = (int) (sz.height - 1) - h;
        }

        Rect rectCrop = new Rect(upper_x, upper_y, w, h);
        Mat imageROI = new Mat(analyzedImage, rectCrop);

        Mat img_clone = new Mat();

        Imgproc.cvtColor(imageROI, img_clone, Imgproc.COLOR_RGB2BGR);

        Mat imgKmean = img_clone.clone();

        imgKmean = img_clone.reshape(1, imageROI.rows() * imageROI.cols());
        imgKmean.convertTo(imgKmean, CvType.CV_32F);

        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, 10, 1.0);

        Core.kmeans(imgKmean, clusters, labels, criteria, 10, Core.KMEANS_PP_CENTERS, centers);

        String dump = centers.dump();
        Color[] colorsArray = extractRgbFromString(dump, clusters);

        existingMaterials = materialIdentifier.getMaterialNamesFromColors(colorsArray);
        
        //existingMaterials.removeIf(x -> x.contains("unknown"));

        return (existingMaterials.size() > 0);

    }

    public List<String> getExistingMaterials() {
        return existingMaterials;
    }

}
