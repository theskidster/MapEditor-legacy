package dev.theskidster.mapeditor.world;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Feb 7, 2021
 */

final class Vertex {
    
    Vector3f position;
    Vector2f texCoords;

    Vertex(float xPos, float yPos, float zPos, float xTex, float yTex) {
        position  = new Vector3f(xPos, yPos, zPos);
        texCoords = new Vector2f(xTex, yTex);
    }
    
}