package dev.theskidster.mapeditor.world;

/**
 * @author J Hoffman
 * Created: Feb 6, 2021
 */

final class Face {
    
    int[] vp = new int[3];
    int[] tc = new int[3];
    int[] n  = new int[3];
        
    Face(int[] vp, int[] tc, int[] n) {
        this.vp = vp;
        this.tc = tc;
        this.n  = n;
    }
    
}