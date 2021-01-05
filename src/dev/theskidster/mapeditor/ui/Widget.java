package dev.theskidster.mapeditor.ui;

/**
 * @author J Hoffman
 * Created: Jan 3, 2021
 */

public abstract class Widget {
    
    boolean hovered;
    
    abstract void update(int width, int height);
    abstract void render();
    
}