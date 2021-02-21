package dev.theskidster.mapeditor.scene;

/**
 * @author J Hoffman
 * Created: Feb 20, 2021
 */

class Movement {
    
    String axis;
    float value;
    
    Movement() {
        axis  = "";
        value = 0;
    }
    
    Movement(String axis, float value) {
        this.axis  = axis;
        this.value = value;
    }
    
}