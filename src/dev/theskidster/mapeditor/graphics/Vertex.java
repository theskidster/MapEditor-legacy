package dev.theskidster.mapeditor.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jan 25, 2021
 */

public class Vertex {
    
    public Vector3f position;
    public Vector2f texCoords;
    
    public Vertex(Vector3f position, Vector2f texCoords) {
        this.position  = position;
        this.texCoords = texCoords;
    }
    
}