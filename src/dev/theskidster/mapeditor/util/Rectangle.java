package dev.theskidster.mapeditor.util;

import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public class Rectangle {
    
    public int xPos;
    public int yPos;
    
    public float width;
    public float height;
    
    public Rectangle() {
        xPos   = 0;
        yPos   = 0;
        width  = 0;
        height = 0;
    }
    
    public Rectangle(int xPos, int yPos, float width, float height) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
    }
    
    public boolean intersects(Vector2i point) {
        return (point.x > xPos && point.x < xPos + width) && 
               (point.y > yPos && point.y < yPos + height);
    }
    
}