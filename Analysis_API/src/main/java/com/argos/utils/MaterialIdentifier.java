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

    private PaletteMapper paletteMapper = null;
    
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
     * @param confidence
     * @return List of strings representing each color mapped to a material
     * returns either the name of a material or unknown
     */
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
    private boolean colorsMatch(Color color, Color baseColor, int confidence) {

        float decimalConfidence = confidence / 100;
        float maxIntervalrange = 256 * (1 - decimalConfidence) / 2;

        float lowerRedLimit = baseColor.getRed() - maxIntervalrange;
        float lowerGreenLimit = baseColor.getGreen() - maxIntervalrange;
        float lowerBlueLimit = baseColor.getBlue() - maxIntervalrange;

        float higherRedLimit = baseColor.getRed() + maxIntervalrange;
        float higherGreenLimit = baseColor.getGreen() + maxIntervalrange;
        float higherBlueLimit = baseColor.getBlue() + maxIntervalrange;

        lowerRedLimit = lowerRedLimit < 0 ? 0 : lowerRedLimit;
        lowerGreenLimit = lowerGreenLimit < 0 ? 0 : lowerGreenLimit;
        lowerBlueLimit = lowerBlueLimit < 0 ? 0 : lowerBlueLimit;

        higherRedLimit = higherRedLimit > 255 ? 255 : higherRedLimit;
        higherGreenLimit = higherGreenLimit > 255 ? 255 : higherGreenLimit;
        higherBlueLimit = higherBlueLimit > 255 ? 255 : higherBlueLimit;

        boolean redMatches = (color.getRed() >= lowerRedLimit) && (color.getRed() <= higherRedLimit);
        boolean greenMatches = (color.getGreen() >= lowerGreenLimit) && (color.getGreen() <= higherGreenLimit);
        boolean blueMatches = (color.getBlue() >= lowerBlueLimit) && (color.getBlue() <= higherBlueLimit);

        return redMatches && greenMatches && blueMatches;
    }

    public PaletteMapper getPaletteMapper() {
        return paletteMapper;
    }

    public void setPaletteMapper(PaletteMapper paletteMapper) {
        this.paletteMapper = paletteMapper;
    }
}
