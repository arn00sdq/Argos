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
    @RequiresApi(api = Build.VERSION_CODES.R)
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
     * setter for the default Palette Mapping
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setDefaultPaletteMapping() {
        colorMapping = Map.ofEntries(
                Map.entry(Color.valueOf(176,130,67), "sand"),
                Map.entry(Color.valueOf(73,54,33), "clay")
        );       
    }

}