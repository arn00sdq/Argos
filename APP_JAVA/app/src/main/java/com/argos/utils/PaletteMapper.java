package com.argos.utils;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Map;

/**
 *
 * @author Ivan
 */
public class PaletteMapper {

    /**
     * Map each color to a material
     */
    private Map<Color, String> colorMapping = null;
    /**
     * Different kinds of palettes should go here
     * Create a Palette Mapping setter for each kind
     * of palette
     */
    public enum paletteTypes {
        DEFAULT_PALETTE
    }
    /**
     * 
     * @param type Type of the palette
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public PaletteMapper(paletteTypes type) {
        switch (type) {
            case DEFAULT_PALETTE:
                setDefaultPaletteMapping();
        } 
    }
    /**
     * Palette Mapping getter
     * @return 
     */
    public Map<Color, String> getColorMap(){
        return colorMapping;
    }
    
    /**
     *
     * @param material The material name of which we want the Color
     * @return The color that matches the corresponding material, or black if
     * material is unknown
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Color getColorFromMaterial(String material) {

        for (Map.Entry<Color, String> entry : colorMapping.entrySet()) {

            Color color = entry.getKey();
            String mat = entry.getValue();

            if (mat.equalsIgnoreCase(material)) {
                return color;
            }

        }
        return Color.valueOf(0f, 0f, 0f);
    }
    /**
     * setter for the default Palette Mapping
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDefaultPaletteMapping() {
        colorMapping = Map.ofEntries(
                Map.entry(Color.valueOf(177,186,181), "Sable massif"),
                Map.entry(Color.valueOf(169,174,164), "Sable massif"),
                Map.entry(Color.valueOf(30,44,46), "Argile"),
                Map.entry(Color.valueOf(58,80,87), "Argile"),
                Map.entry(Color.valueOf(196,211,208), "Conglomerat"),
                Map.entry(Color.valueOf(170,180,170), "Conglomerat")
        );       
    }

}