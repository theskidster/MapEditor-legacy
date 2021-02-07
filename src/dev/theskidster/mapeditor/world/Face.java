package dev.theskidster.mapeditor.world;

/**
 * @author J Hoffman
 * Created: Feb 6, 2021
 */

final class Face {
    
    int[] indices = new int[3];

    Face(int vert1, int vert2, int vert3) {
        indices[0] = vert1;
        indices[1] = vert2;
        indices[2] = vert3;
    }
    
}