package dev.theskidster.mapeditor.ui;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

final class Color {

    static final Color WHITE       = new Color(200);
    static final Color LIGHT_GRAY  = new Color(90);
    static final Color MEDIUM_GRAY = new Color(70);
    static final Color DARK_GRAY   = new Color(52);
    static final Color BLACK       = new Color(34);
    static final Color BLUE        = new Color(24, 88, 184);
    static final Color RED         = new Color(240, 48, 12);
    
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