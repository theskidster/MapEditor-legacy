package dev.theskidster.mapeditor.ui;

import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

class Rectangle {
    
    int xPos;
    int yPos;
    
    float width;
    float height;
    
    Rectangle() {
        xPos   = 0;
        yPos   = 0;
        width  = 0;
        height = 0;
    }
    
    Rectangle(int xPos, int yPos, float width, float height) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
    }
    
    boolean intersects(Vector2i point) {
        return (point.x > xPos && point.x < xPos + width) && 
               (point.y > yPos && point.y < yPos + height);
    }
    
}