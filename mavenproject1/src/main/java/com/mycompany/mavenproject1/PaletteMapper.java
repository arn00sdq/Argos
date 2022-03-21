/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.awt.Color;
import java.util.Map;

/**
 *
 * @author Ivan
 */
public class PaletteMapper {
    
    private Map<Color, String> colorMapping = null;
    
    public enum paletteTypes {
        DEFAULT_PALETTE
    }

    public PaletteMapper(paletteTypes type) {
        switch (type) {
            case DEFAULT_PALETTE:
                setDefaultPaletteMapping();
        } 
    }
    
    public Map<Color, String> getColorMap(){
        return colorMapping;
    }
    private void setDefaultPaletteMapping() {
        colorMapping = Map.ofEntries(
                Map.entry(new Color(176,130,67), "sand"),
                Map.entry(new Color(73,54,33), "clay")
        );       
    }
    
}
