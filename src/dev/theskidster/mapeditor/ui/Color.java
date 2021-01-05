package dev.theskidster.mapeditor.ui;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

final class Color {

    static final Color WHITE  = new Color(255);
    static final Color SILVER = new Color(70);
    static final Color GRAY   = new Color(52);
    static final Color BLUE   = new Color(24, 88, 184);
    
    final float r;
    final float g;
    final float b;
    
    private Color(float scalar) {
        r = g = b = (scalar / 255f);
    }
    
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
    }
    
    static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
}