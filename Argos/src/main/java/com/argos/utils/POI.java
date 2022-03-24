package com.argos.utils;

import java.awt.Color;

/**
 *
 * @author Ivan
 * @code This class defines a POI (Point Of Interest) in an image 
 */
public class POI {

    private final String[] labels;
    private final Integer width;
    private final Integer height;
    private final Integer x_coord;
    private final Integer y_coord;
    private Color lineColor = Color.CYAN;
    private Integer lineWidth = 1;

    /**
     * 
     * @param labels names corresponding to this Point Of Interest
     * @param width width of the point of intereset area
     * @param height height of the point of intereset area
     * @param x_coord x coordinate of the upper left corner
     * @param y_coord y coordinate of the upper left corner
     * @param lineColor color of the line that should be used to draw the bounding rectangle
     * @param lineWidth width of the line that should be used to draw the bounding rectangle
     */
    public POI(String[] labels, Integer width, Integer height, Integer x_coord, Integer y_coord, Color lineColor, Integer lineWidth) {
        this.labels = labels;
        this.width = width;
        this.height = height;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public POI(String[] labels, Integer width, Integer height, Integer x_coord, Integer y_coord) {
        this.labels = labels;
        this.width = width;
        this.height = height;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }

    

    public String[] getLabels() {
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

    public String toJSON() {
        String result = new String();
        result += "{\n";
        result += "\tlabels : [\n";
        for (int i = 0; i < labels.length; i++) {
            result += "\t\t" + labels[i];
            if (i != labels.length - 1) {
                result += ",\n";
            }
        }
        result += "\n\t]";
        result += ",\n\tx : " + this.x_coord;
        result += ",\n\ty : " + this.y_coord;
        result += ",\n\twidth : " + this.width;
        result += ",\n\theight : " + this.height;
        result += ",\n\tlineColor : " + this.lineColor.getRed() + ", " + this.lineColor.getGreen()+ ", " + this.lineColor.getBlue();
        result += ",\n\tlineWidth : " + this.lineWidth;
        result += "\n}";
        
        return result;
    }

}
