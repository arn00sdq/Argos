package com.backend;

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
     * Confidence: degree of accuracy for comparison ranging between 1 and 100
     * 100 means color values must exactly match Example: with a confidence of
     * 90 and checking against sand RGB(50,50,50), matching function will
     * compare if a new Color, for example RGB(10,34,65) has values between 
     * 50 +- 255 * (1 - confidence / 100) / 2
     */
    private float confidence = 90;
    
    private PaletteMapper paletteMapper = null;
    

    /**
     *
     * @param mapper Palette containing mapped colors for materials
     * @param confidence Degree of accuracy for comparison
     */
    public MaterialIdentifier(PaletteMapper mapper, float confidence) {
        this.paletteMapper = mapper;
        this.confidence = confidence;
    }

    /**
     *
     * @param mapper Malette containing mapped colors for materials
     */
    public MaterialIdentifier(PaletteMapper mapper) {
        this.paletteMapper = mapper;
    }

    /**
     *
     * @param colors Array of sRGB colors
     * @return List of strings representing each color mapped to a material
     * returns either the name of a material or unknown
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> getMaterialNamesFromColors(Color[] colors,int confidence) {
        List<String> materialNames = new ArrayList<>();
        for (Color color : colors) {
            materialNames.add(identifyMaterialFromColor(color, confidence));
        }
        return materialNames;
    }

    /**
     *
     * @param color Color that we want to identify
     * @return Either the name of the identified material or unknown
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String identifyMaterialFromColor(Color color, int confidence) {

        for (Entry<Color, String> entry : paletteMapper.getColorMap().entrySet()) {

            Color baseColor = entry.getKey();
            String mat = entry.getValue();

            if (colorsMatch(color, baseColor, confidence)) {
                return mat;
            }

        }
        return "unknown";
    }


    /**
     *
     * @param color Color that we want to identify
     * @param baseColor Color present in the palette that maps to a material
     * @return Whether the two colors match
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean colorsMatch(Color color, Color baseColor, int confidence){

        float decimalConfidence = confidence / 100;
        float maxIntervalrange = 256 * (1 - decimalConfidence) / 2;

        float lowerRedLimit = baseColor.red() - maxIntervalrange;
        float lowerGreenLimit = baseColor.green() - maxIntervalrange;
        float lowerBlueLimit = baseColor.blue() - maxIntervalrange;

        float higherRedLimit = baseColor.red() + maxIntervalrange;
        float higherGreenLimit = baseColor.green() + maxIntervalrange;
        float higherBlueLimit = baseColor.blue() + maxIntervalrange;

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

    public PaletteMapper getPaletteMapper() {
        return paletteMapper;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setPaletteMapper(PaletteMapper paletteMapper) {
        this.paletteMapper = paletteMapper;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
    
    
}
