package dev.theskidster.mapeditor.world;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Feb 7, 2021
 */

final class Vertex {
    
    Point point;
    Vector2f texCoords;
    Vector3f normal;
    
    Vertex(float xTex, float yTex, float xNrml, float yNrml, float zNrml) {
        texCoords  = new Vector2f(xTex, yTex);
        normal     = new Vector3f(xNrml, yNrml, zNrml);
    }
    
}