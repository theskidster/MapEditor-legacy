package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import org.lwjgl.nuklear.NkContext;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

abstract class Widget {
    
    static final int MB_HEIGHT = 28;
    static final int PADDING   = 20;
    static final int CONTENT_X = 300;
    
    protected String title;
    
    boolean removeRequest;
    
    Widget(String title) {
        this.title = title;
    }
    
    abstract void update(NkContext nkContext, Window window);
    
}