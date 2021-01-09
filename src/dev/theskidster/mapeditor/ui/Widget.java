package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;

/**
 * @author J Hoffman
 * Created: Jan 3, 2021
 */

public abstract class Widget {
    
    boolean hovered;
    boolean removeRequest;
    
    abstract void update(int width, int height, Mouse mouse);
    abstract void render(ShaderProgram program, TrueTypeFont font);
    
}