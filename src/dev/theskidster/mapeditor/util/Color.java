package dev.theskidster.mapeditor.util;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public final class Color {
    
    //XJGE color table
    public static final Color WHITE      = new Color(1);
    public static final Color SILVER     = new Color(0.75f);
    public static final Color GRAY       = new Color(0.5f);
    public static final Color BLACK      = new Color();
    public static final Color RED        = new Color(255, 0, 0);
    public static final Color ORANGE     = new Color(255, 153, 0);
    public static final Color YELLOW     = new Color(255, 255, 0);
    public static final Color GREEN      = new Color(0, 255, 0);
    public static final Color CYAN       = new Color(0, 255, 255);
    public static final Color BLUE       = new Color(0, 0, 255);
    public static final Color PURPLE     = new Color(136, 0, 152);
    public static final Color PINK       = new Color(255, 0, 255);
    public static final Color BROWN      = new Color(70, 45, 10);
    public static final Color NAVY       = new Color(0, 0, 128);
    public static final Color SOFT_BLUE  = new Color(92, 148, 252);
    public static final Color TEAL       = new Color(0, 128, 128);
    public static final Color SLATE_GRAY = new Color(16, 21, 36);
    public static final Color PERIWINKLE = new Color(141, 97, 156);
    
    //RGM editor color table
    public static final Color RGM_WHITE       = new Color(215, 216, 224);
    public static final Color RGM_LIGHT_GRAY  = new Color(83, 87, 116);
    public static final Color RGM_MEDIUM_GRAY = new Color(64, 68, 93);
    public static final Color RGM_DARK_GRAY   = new Color(41, 45, 62);
    public static final Color RGM_BLACK       = new Color(32, 35, 51);
    public static final Color RGM_RED         = new Color(232, 17, 35);
    public static final Color RGM_GREEN       = new Color(4, 168, 0);
    public static final Color RGM_BLUE        = new Color(51, 102, 204);
    public static final Color RGM_NAVY        = new Color(19, 22, 29);
    public static final Color RGM_PINK        = new Color(130, 83, 140);
    public static final Color RGM_PURPLE      = new Color(83, 51, 89);
    
    public final float r;
    public final float g;
    public final float b;
    
    private Color() {
        r = g = b = 0;
    }
    
    private Color(float scalar) {
        r = g = b = scalar;
    }
    
    private Color(int r, int g, int b) {
        this.r = (r / 255f);
        this.g = (g / 255f);
        this.b = (b / 255f);
    }
    
    public static Color create(int r, int g, int b) {
        return new Color(r, g, b);
    }
    
    public static Vector3f convert(Color color) {
        return new Vector3f(color.r, color.g, color.b);
    }
    
}