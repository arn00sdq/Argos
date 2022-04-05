package com.argos.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author Ivan
 * @code This class defines a POI (Point Of Interest) in an image
 */
public class PointOfInterest {

    private final List<String> labels;
    private final Integer width;
    private final Integer height;
    private final Integer x_coord;
    private final Integer y_coord;
    private Color lineColor = new Color(255,0,0);
    private Integer lineWidth = 1;

    private Hashtable<String, Integer> materialProportions;

    /**
     *
     * @param materials Material names present in this Point Of Interest
     * @param width Width of the point of interest area
     * @param height Height of the point of interest area
     * @param x_coord X coordinate of the upper left corner
     * @param y_coord Y coordinate of the upper left corner
     * @param lineColor Color of the line that should be used to draw the
     * bounding rectangle
     * @param lineWidth Width of the line that should be used to draw the
     * bounding rectangle
     */
    public PointOfInterest(List<String> materials, Integer width, Integer height, Integer x_coord, Integer y_coord, Color lineColor, Integer lineWidth) {
        this.labels = materials;
        this.width = width;
        this.height = height;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    /**
     *
     * @param materials Material names present in this Point Of Interest
     * @param width Width of the point of interest area
     * @param height Height of the point of interest area
     * @param x_coord X coordinate of the upper left corner
     * @param y_coord Y coordinate of the upper left corner
     */
    public PointOfInterest(List<String> materials, Integer width, Integer height, Integer x_coord, Integer y_coord) {
        this.labels = materials;
        this.width = width;
        this.height = height;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }

    /**
     * Creates a list of POIs representing the zones
     *
     * @param zones List of TargetZone
     * @return List of PointOfInterest
     */
    public static List<PointOfInterest> toPOIList(List<TargetZone> zones) {

        ArrayList<PointOfInterest> poiArray = new ArrayList<>();

        zones.forEach(zone -> {
            poiArray.add(new PointOfInterest(
                    new ArrayList<>(),
                    zone.getW(),
                    zone.getH(),
                    zone.getUpper_x(),
                    zone.getUpper_y(),
                    Color.RED,
                    2));
        });

        return poiArray;

    }

    public List<String> getLabels() {
        return labels;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getX_coord() {
        return x_coord;
    }

    public Integer getY_coord() {
        return y_coord;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public Integer getLineWidth() {
        return lineWidth;
    }

    public Hashtable<String, Integer> getMaterialProportions() {
        return materialProportions;
    }

    public void setMaterialProportions(Hashtable<String, Integer> materialProportions) {
        this.materialProportions = materialProportions;
    }

    /**
     * Convert a PointOfInterest to JSON format
     *
     * @return JSON string
     */
    public String toJSON() {
        String result = new String();
        result += "{\n";
        result += "\tlabels : [\n";
        for (int i = 0; i < labels.size(); i++) {
            result += "\t\t" + labels.get(i);
            if (i != labels.size() - 1) {
                result += ",\n";
            }
        }
        result += "\n\t\t]";
        result += ",\n\tx : " + this.x_coord;
        result += ",\n\ty : " + this.y_coord;
        result += ",\n\twidth : " + this.width;
        result += ",\n\theight : " + this.height;
        result += ",\n\tlineColor : " + this.lineColor.getRed() + ", " + this.lineColor.getGreen() + ", " + this.lineColor.getBlue();
        result += ",\n\tlineWidth : " + this.lineWidth;
        result += ",\n\tpercentages : " + this.materialProportions.toString();
        result += "\n}";

        return result;
    }

}
