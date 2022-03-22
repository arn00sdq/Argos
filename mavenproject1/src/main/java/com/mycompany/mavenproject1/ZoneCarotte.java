/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.util.List;

/**
 *
 * @author MSI
 */
public class ZoneCarotte {
    
    int upper_x;
    int upper_y;
    int lower_x;
    int lower_y;
    int w;
    int h;
    
    public ZoneCarotte(int upper_x,int upper_y,int lower_x,int lower_y,int w,int h){
        
        this.upper_x = upper_x;
        this.upper_y = upper_y;
        this.lower_x = lower_x;
        this.lower_y = lower_y;
        this.w = w;
        this.h = h;
   
    }
    
    public int getArea(){
        
        return this.w * this.h;
        
    }
    
    public boolean equals(ZoneCarotte other){

        if(this.upper_x == other.upper_x){
            
            return true;
            
        }else{
        
            return false;
            
        }
    }
    
    public boolean existsInArray(List<ZoneCarotte> array){
        
        boolean exists = false;

        for(int i = 0; i < array.size(); i++ ){
            
            if(this.equals(array.get(i))){
                
                exists = true;
            }
            
        }
        
        return exists;
        
    }
    
    
}
