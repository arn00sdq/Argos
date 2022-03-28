/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argos.utils;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;

/**
 *
 * @author MSI
 */
public class PointOfInterestFinder {
    
    private List<PointOfInterest> detectedPOI;
    private ArrayList<List<String>> carotteColor = new ArrayList<>();
    private TargetZone largestCarotte;
      
    int maxZone = 0;
                
    private void GetCarotteLargestArea(List<TargetZone> detectedCarotte){
        
        for (TargetZone currentCarotte : detectedCarotte) {
            
            if(currentCarotte.getArea() > maxZone){
                    
                    maxZone = currentCarotte.getArea();
                    largestCarotte = currentCarotte;
                    
            }
            
        }
               
    }
    
    private void SetPOIValues(TargetZone carotte){
        
        
        
    }
    
    public List<PointOfInterest> GetPointOfInterest(List<TargetZone> detectedCarotte,Mat image_bitwised){
          
        detectedCarotte.forEach(currentCarotte -> {
            
            if(largestCarotte.w > largestCarotte.h) {
                
                for (int i = 0; i < currentCarotte.w ; i += currentCarotte.w/10) {
                
                    KmeansMaterialRecognition kDetect = new KmeansMaterialRecognition(currentCarotte.upper_x + i , currentCarotte.upper_y, currentCarotte.w/10, currentCarotte.h);
                    if(kDetect.Match(image_bitwised)){
                        
                        detectedPOI.add(new PointOfInterest(kDetect.materials,kDetect.w,kDetect.h,kDetect.upper_x,kDetect.upper_y));
                        
                    };
                }
                
            }else{
                
                for (int i = 0; i < currentCarotte.h ; i += currentCarotte.h/10) {
                
                    KmeansMaterialRecognition kDetect = new KmeansMaterialRecognition(currentCarotte.upper_x , currentCarotte.upper_y + i, currentCarotte.w, currentCarotte.h/10);
                    if(kDetect.Match(image_bitwised)){
                        
                        detectedPOI.add(new PointOfInterest(kDetect.materials,kDetect.w,kDetect.h,kDetect.upper_x,kDetect.upper_y));
                        
                    };
                }
            }
        });
        
        return  detectedPOI;
    }
    
}
