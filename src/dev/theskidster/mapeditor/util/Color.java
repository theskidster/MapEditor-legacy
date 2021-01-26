package dev.theskidster.mapeditor.util;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public final class Color {

    public static final Color WHITE       = new Color(215, 216, 224);
    public static final Color SILVER      = new Color(161, 162, 179);
    public static final Color SLATE       = new Color(95, 95, 100);
    public static final Color LIGHT_GRAY  = new Color(83, 87, 116);
    public static final Color MEDIUM_GRAY = new Color(64, 68, 93);
    public static final Color DARK_GRAY   = new Color(41, 45, 62);
    public static final Color BLACK       = new Color(26, 23, 33);
    public static final Color BLUE        = new Color(51, 102, 204);
    public static final Color RED         = new Color(248, 40, 12);
    
    public final float r;
    public final float g;
    public final float b;
    
    private Color(float scalar) {
        r = g = b = (scalar / 255f);
    }
    
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
    }
    
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
}