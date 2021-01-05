package dev.theskidster.mapeditor.ui;

import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

class Rectangle {
    
    float width;
    float height;
    
    Vector2i position;
    
    Rectangle(Vector2i position, float width, float height) {
        this.position = position;
        this.width    = width;
        this.height   = height;
    }
    
}