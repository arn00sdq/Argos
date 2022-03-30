package com.argos.utils;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean colorsMatch(Color color, Color baseColor) {
        
        float decimalConfidence = confidence / 100;
        float maxIntervalrange = 256 * (1 - decimalConfidence);
        float halfInterval = maxIntervalrange / 2;
        
        float lowerRedLimit = baseColor.red() - halfInterval;
        float lowerGreenLimit = baseColor.green() - halfInterval;
        float lowerBlueLimit = baseColor.blue() - halfInterval;
        
        float higherRedLimit = baseColor.red() + halfInterval;
        float higherGreenLimit = baseColor.green() + halfInterval;
        float higherBlueLimit = baseColor.blue() + halfInterval;
        
        /*
        float lowerRedLimit = baseColor.red() * decimalConfidence;
        float lowerGreenLimit = baseColor.green() * decimalConfidence;
        float lowerBlueLimit = baseColor.blue() * decimalConfidence;
        
        float higherRedLimit = baseColor.red() * (1 - decimalConfidence) + baseColor.red();
        float higherGreenLimit = baseColor.green() * (1 - decimalConfidence) + baseColor.green();
        float higherBlueLimit = baseColor.blue() * (1 - decimalConfidence) + baseColor.blue();*/

        
        lowerRedLimit = lowerRedLimit < 0 ? 0 : lowerRedLimit;
        lowerGreenLimit = lowerGreenLimit < 0 ? 0 : lowerGreenLimit;
        lowerBlueLimit = lowerBlueLimit < 0 ? 0 : lowerBlueLimit;
        
        higherRedLimit = higherRedLimit > 255 ? 255 : higherRedLimit;
        higherGreenLimit = higherGreenLimit > 255 ? 255 : higherGreenLimit;
        higherBlueLimit = higherBlueLimit > 255 ? 255 : higherBlueLimit;
        
        boolean redMatches = (color.red() >= lowerRedLimit) && (color.red() <= higherRedLimit);
        boolean greenMatches = (color.green() >= lowerGreenLimit) && (color.green() <= higherGreenLimit);
        boolean blueMatches = (color.blue() >= lowerBlueLimit) && (color.blue() <= higherBlueLimit);
        
        return redMatches && greenMatches && blueMatches;
    }
}
