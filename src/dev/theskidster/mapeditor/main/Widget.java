package dev.theskidster.mapeditor.main;

import org.lwjgl.nuklear.NkContext;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

abstract class Widget {
    
    static final int MB_HEIGHT = 25;
    static final int PADDING   = 20;
    static final int CONTENT_X = 300;
    
    protected String title;
    
    Widget(String title) {
        this.title = title;
    }
    
    abstract void update(NkContext nkContext, Window window);
    
}