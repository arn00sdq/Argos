/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import org.opencv.core.Core;
import org.opencv.highgui.*;
import org.opencv.core.CvType;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.List;

class CalcHist {
    

    public void run(String[] args) throws IOException {
        String filepath = "img\\carrote2.jpg";

        int clusters = 6;
       
        Mat colors= new Mat();
        Mat centers= new Mat();
        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS+TermCriteria.COUNT,10, 1.0);

        //Reading Image
        Mat img = Imgcodecs.imread(filepath);
        Mat img_clone = new Mat();

        //Making image RGB
       // 
        
        Imgproc.cvtColor(img,img_clone,Imgproc.COLOR_RGB2BGR);
        
        Mat imgKmean = img_clone.clone();
        Mat imgRgb= img_clone.clone();
        
       // HighGui.imshow("COLOR_RGB2BGR ", img_clone);
        
        //reshape into a single long line for Kmeans method
        imgKmean = img_clone.reshape (1, img.rows() * img.cols());

        //Converting to np.float32 as required by Kmeans method
        imgKmean.convertTo(imgKmean,CvType.CV_32F);

        //Executing Kmeans
       
        Core.kmeans(imgKmean,clusters,labels, criteria,10,Core.KMEANS_PP_CENTERS,colors);
        
        System.out.println("Colors : "+colors + "\n");
        String dump = colors.dump();//derverse
        System.out.println("Dump : "+dump + "\n");

        // extrait le rgb de colors -- pas important
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
                System.out.println(s);
                if(element==0) {
                    r = Math.round(Float.parseFloat(s));
                }else if(element == 1){
                    g = Math.round(Float.parseFloat(s));
                }
                else if(element == 2){
                    b = Float.parseFloat(s);
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
        
        
        
        // Visualize k-means
        Mat draw = new Mat((int)img.total(),1, CvType.CV_32FC3);
        Mat colorsK = colors.reshape(3,clusters);

       
        for (int i=0; i<clusters; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(labels, new Scalar(i), mask, Core.CMP_EQ);
            Mat colS = colorsK.row(i); // can't use the Mat directly with setTo() (see #19100)  
            double d[] = colS.get(0,0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0],d[1],d[2]), mask);
        }
       
        draw = draw.reshape(3, imgRgb.rows());
        draw.convertTo(draw, CvType.CV_8U);
        

        JFrame f = new JFrame();
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel[] panels = new JPanel[clusters+1];

        for (int i = 0; i<clusters; i++)
        {
            panels[i] = new JPanel();
            panels[i].setBounds(0,100*i,100,100);
            System.out.println("RGB : " + colorsArray[i].getRed()+colorsArray[i].getGreen()+colorsArray[i].getBlue());
            panels[i].setBackground(colorsArray[i]);
            
            panels[i].setVisible(true);
            f.add(panels[i]);
     
        }
      
        BufferedImage myPicture = ImageIO.read(new File(filepath));
        JLabel picLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(draw)));
        
        picLabel.setBounds(100,0,myPicture.getWidth(),myPicture.getHeight());
        
        int height = Math.max(myPicture.getHeight(),clusters*100);

        f.setSize(myPicture.getWidth()+100,height);
        f.setTitle("Color Tones");
        f.add(picLabel);
        
        f.setLayout(null);
        f.setVisible(true);
        
            HighGui.imshow("image de base  ", img);
        HighGui.waitKey(0);
        System.exit(0);
       
    }
}
public class CalcHistDemo {
    public static void main(String[] args) throws IOException {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new CalcHist().run(args);
    }
}
