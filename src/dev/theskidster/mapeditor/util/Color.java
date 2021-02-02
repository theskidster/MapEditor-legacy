package dev.theskidster.mapeditor.util;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public final class Color {
    
    public static final Color WHITE       = new Color(215, 216, 224);
    public static final Color LIGHT_GRAY  = new Color(83, 87, 116);
    public static final Color MEDIUM_GRAY = new Color(64, 68, 93);
    public static final Color DARK_GRAY   = new Color(41, 45, 62);
    public static final Color BLACK       = new Color(32, 35, 51);
    public static final Color RED         = new Color(232, 17, 35);
    public static final Color GREEN       = new Color(4, 168, 0);
    public static final Color BLUE        = new Color(51, 102, 204);
    public static final Color NAVY        = new Color(19, 22, 29);
    public static final Color PINK        = new Color(152, 96, 170);
    public static final Color PURPLE      = new Color(100, 32, 100);
    
    public final float r;
    public final float g;
    public final float b;
    
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
    }
    
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
}