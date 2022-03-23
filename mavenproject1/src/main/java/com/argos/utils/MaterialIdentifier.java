package com.argos.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Ivan
 * @code This class is used to identify the identity of materials from a color
 *       and a specific PaletteMapper
 */
public class MaterialIdentifier {
    
    /**
     * confidence: degree of accuracy for comparison
     * number between 1 and 100
     * 100 means color values must exactly match
     */
    private PaletteMapper paletteMapper = null;
    private float confidence = 90;
    /**
     * 
     * @param mapper palette containing mapped colors for materials
     * @param confidence degree of accuracy for comparison
     */
    public MaterialIdentifier(PaletteMapper mapper, float confidence) {
        this.paletteMapper = mapper;
        this.confidence = confidence;
    }
    /**
     * 
     * @param mapper palette containing mapped colors for materials
     */
    public MaterialIdentifier(PaletteMapper mapper) {
        this.paletteMapper = mapper;
    }
    /**
     * 
     * @param colors Array of sRGB colors
     * @return List of strings representing each color mapped to a material
     *         returns either the name of a material or unknown
     */
    public List<String> getMaterialNamesFromColors(Color[] colors){
        List<String> result = new ArrayList<>();
        for (Color color : colors) {
            result.add(identifyMaterialFromColor(color));
        }
        return result;
    }
    /**
     * 
     * @param color Color that we want to identify
     * @return Either the name of the identified material or unknown
     */
    private String identifyMaterialFromColor(Color color){
        
        for (Entry<Color, String> entry : paletteMapper.getColorMap().entrySet()) {
            
            Color baseColor = entry.getKey();
            String mat = entry.getValue();
            
            if (colorsMatch(color, baseColor)) return mat;
        }
        return "unknown";
    }
    /**
     * 
     * @param color color that we want to identify
     * @param baseColor color present in the palette that maps to a material
     * @return wether the two colors match
     */
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
