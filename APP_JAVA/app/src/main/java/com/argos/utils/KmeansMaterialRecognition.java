/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import com.argos.utils.PaletteMapper.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;


public class KmeansMaterialRecognition {
    
    int clusters = 4;
    int upper_x;
    int upper_y;
    int w;
    int h;
    List<String> materials = new ArrayList<>();
    TermCriteria criteria = new TermCriteria(TermCriteria.EPS+TermCriteria.COUNT,10, 1.0);
    
    public KmeansMaterialRecognition(int upper_x,int upper_y,int w,int h){
        
        this.upper_x = upper_x;
        this.upper_y = upper_y;
        this.w = w;
        this.h = h;
        
    }
    
    public KmeansMaterialRecognition(int upper_x,int upper_y,int w,int h, int cluster){
        
        this.upper_x = upper_x;
        this.upper_y = upper_y;
        this.w = w;
        this.h = h;
        this.clusters = cluster;
        
    }
    
    private Color[] extractRgbFromString(String dump, int clusters){
        
        int row = 0;
        int col = 0;
        int element = 0;

        float r = 0,g = 0,b = 0;

        char[] charList = new char[100];

        Color[] colorsArray = new Color[clusters];
       
        for(int i = 0; i<dump.length(); i++){
            if(dump.charAt(i)=='['){
                continue;
            }

            if(dump.charAt(i) == ',')
            {
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
            if(dump.charAt(i) == ';' || dump.charAt(i) == ']' ){
                if(element == 2){
                    String s = new String(charList).trim();
                    b = Math.round(Float.parseFloat(s));
                }
                
                colorsArray[row] = new Color((int)r,(int)g,(int)b,255);

                row++;
                element = 0;
                charList = new char[100];
                col = 0;
                continue;
            }

            charList[col]=dump.charAt(i);

            col++;
        }
        
        return colorsArray;
        
    }
    
    public boolean Match(Mat kImg) {
             
        PaletteMapper pm = new PaletteMapper(paletteTypes.DEFAULT_PALETTE);
        MaterialIdentifier mi = new MaterialIdentifier(pm, 50);
        Mat centers= new Mat();
        Mat labels = new Mat();
                       
        boolean materialDetected = false;
        
        Size sz = kImg.size();
        
        if(this.upper_x +  this.w >= sz.width){
            
            this.upper_x = (int)(sz.width - 1) - this.w ;
        }
         if(this.upper_y +  this.h >= sz.height){
            
            this.upper_y = (int)(sz.height - 1) - this.h ;
        }
        
        Rect rectCrop = new Rect(this.upper_x, this.upper_y , this.w, this.h);
        Mat imageROI = new Mat(kImg,rectCrop);
        
        Mat img_clone = new Mat();

        Imgproc.cvtColor(imageROI,img_clone,Imgproc.COLOR_RGB2BGR);
        
        Mat imgKmean = img_clone.clone();
        
        imgKmean = img_clone.reshape (1, imageROI.rows() * imageROI.cols());
        imgKmean.convertTo(imgKmean,CvType.CV_32F);
        
        Core.kmeans(imgKmean,clusters,labels, criteria,10,Core.KMEANS_PP_CENTERS,centers);
        
        String dump = centers.dump();
        Color[] colorsArray = extractRgbFromString(dump, clusters);
        
        materials = mi.getMaterialNamesFromColors(colorsArray);
        
        materials.removeIf(x -> x.contains("unknown"));

        if(materials.size() > 0) materialDetected = true;
            
        return(materialDetected);
       
    }
}

