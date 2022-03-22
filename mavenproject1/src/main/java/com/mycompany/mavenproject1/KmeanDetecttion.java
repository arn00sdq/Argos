/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import com.mycompany.mavenproject1.PaletteMapper.*;
import com.mycompany.mavenproject1.MaterialIdentifier.*;
import org.opencv.core.Core;
import org.opencv.highgui.*;
import org.opencv.core.CvType;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;

import javax.swing.*;
import java.awt.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class ColorMatching {
    
    int clusters = 1;
    TermCriteria criteria = new TermCriteria(TermCriteria.EPS+TermCriteria.COUNT,10, 1.0);
    
    public Color[] extractRgbFromString(String dump, int clusters){
        
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
    public Mat visualizeKmean(Mat centers,Color[] colorsArray, Mat img, Mat labels){
        
        Mat draw = new Mat((int)img.total(),1, CvType.CV_32FC3);
        Mat colorsK = centers.reshape(3,clusters);

        for (int i=0; i<clusters; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(labels, new Scalar(i), mask, Core.CMP_EQ);
            Mat colS = colorsK.row(i); // can't use the Mat directly with setTo() (see #19100)  
            double d[] = colS.get(0,0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0],d[1],d[2]), mask);
        }
       
        draw = draw.reshape(3, img.rows());
        draw.convertTo(draw, CvType.CV_8U);
        
        JFrame f = new JFrame();
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel[] panels = new JPanel[clusters+1];

        for (int i = 0; i<clusters; i++)
        {
            panels[i] = new JPanel();
            panels[i].setBounds(0,100*i,100,100);
            panels[i].setBackground(colorsArray[i]);
            panels[i].setVisible(true);
            f.add(panels[i]);
     
        }
        
        f.setLayout(null);
        f.setVisible(true);
        
        return draw;
        
    }
    
    public void run(String[] args) throws IOException {
        
        String filepath = "img\\carrote2.jpg";
        PaletteMapper pm = new PaletteMapper(paletteTypes.DEFAULT_PALETTE);
        MaterialIdentifier mi = new MaterialIdentifier(pm, 50);
        Mat centers= new Mat();
        Mat labels = new Mat();
       
        Mat img = Imgcodecs.imread(filepath);
        
       /* Rect rectCrop = new Rect(20, 30 , 400, 50);
        Mat imageROI = new Mat(img,rectCrop);
        
        HighGui.imshow("ROI ", imageROI);*/
        
        Mat img_clone = new Mat();

        Imgproc.cvtColor(img,img_clone,Imgproc.COLOR_RGB2BGR);
        
        Mat imgKmean = img_clone.clone();
        
        imgKmean = img_clone.reshape (1, img.rows() * img.cols());
        imgKmean.convertTo(imgKmean,CvType.CV_32F);
        
        Core.kmeans(imgKmean,clusters,labels, criteria,10,Core.KMEANS_PP_CENTERS,centers);
        
        String dump = centers.dump();
        Color[] colorsArray = extractRgbFromString(dump, clusters);
        Mat draw = visualizeKmean(centers,colorsArray,img,labels);
        
        List<String> res = new ArrayList<>();
        res = mi.getMaterialNamesFromColors(colorsArray);
        System.out.println(res);
        
        HighGui.imshow("img avec kmeans  ", draw);
        HighGui.imshow("image de base  ", img);
        HighGui.waitKey(0);
        System.exit(0);
       
    }
}
public class KmeanDetecttion {
    
    public static void main(String[] args) throws IOException {
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        new ColorMatching().run(args);
        
    }
}
