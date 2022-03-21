/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Ivan
 */
public class MaterialIdentifier {
    
    /**
     * confidence: number between 1 and 100
     * 100 means colors must exactly match
     */
    private PaletteMapper paletteMapper = null;
    private float confidence = 90;
    
    
    public MaterialIdentifier(PaletteMapper mapper, float confidence) {
        this.paletteMapper = mapper;
        this.confidence = confidence;
    }
    
    public MaterialIdentifier(PaletteMapper mapper) {
        this.paletteMapper = mapper;
    }
    
    public List<String> getMaterialNamesFromColors(Color[] colors){
        List<String> result = new ArrayList<>();
        for (Color color : colors) {
            result.add(identifyMaterialFromColor(color));
        }
        return result;
    }
    
    private String identifyMaterialFromColor(Color color){
        
        for (Entry<Color, String> entry : paletteMapper.getColorMap().entrySet()) {
            Color baseColor = entry.getKey();
            String mat = entry.getValue();
            
            if (colorsMatch(color, baseColor)) return mat;
        }
        return "unknown";
    }
    
    private boolean colorsMatch(Color color, Color baseColor) {
        
        float decimalConfidence = confidence / 100;
        
        float lowerRedLimit = baseColor.getRed() * decimalConfidence;
        float lowerGreenLimit = baseColor.getGreen() * decimalConfidence;
        float lowerBlueLimit = baseColor.getBlue() * decimalConfidence;
        
        float higherRedLimit = baseColor.getRed() * (1 - decimalConfidence) + baseColor.getRed();
        float higherGreenLimit = baseColor.getGreen() * (1 - decimalConfidence) + baseColor.getGreen();
        float higherBlueLimit = baseColor.getBlue() * (1 - decimalConfidence) + baseColor.getBlue();

        higherRedLimit = higherRedLimit > 255 ? 255 : higherRedLimit;
        higherGreenLimit = higherGreenLimit > 255 ? 255 : higherGreenLimit;
        higherBlueLimit = higherBlueLimit > 255 ? 255 : higherBlueLimit;
        
        boolean redMatches = (color.getRed() >= lowerRedLimit) && (color.getRed() <= higherRedLimit);
        boolean greenMatches = (color.getGreen() >= lowerGreenLimit) && (color.getGreen() <= higherGreenLimit);
        boolean blueMatches = (color.getBlue() >= lowerBlueLimit) && (color.getBlue() <= higherBlueLimit);
        
        return redMatches && greenMatches && blueMatches;
    }
}