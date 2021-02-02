package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.util.Color;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Feb 1, 2021
 */

class Cell {
    
    boolean hovered;
    
    Vector3f position;
    Vector3f color;
    Vector3f max;
    
    Cell(Vector3f position, Color color) {
        this.position = position;
        this.color    = new Vector3f(color.r, color.g, color.b);
        
        max = new Vector3f(position.x + CELL_SIZE, position.y, position.z + CELL_SIZE);
    }
    
}