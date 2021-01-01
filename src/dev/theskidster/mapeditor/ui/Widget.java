package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import java.util.Map;
import org.lwjgl.nuklear.NkContext;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

public abstract class Widget {
    
    static final int MB_HEIGHT = 28;
    static final int PADDING   = 20;
    static final int CONTENT_X = 300;
    
    protected String title;
    
    boolean hovered;
    boolean active;
    
    public Widget(String title) {
        this.title = title;
    }
    
    abstract void update(NkContext nkContext, Window window, Map<String, Widget> widgets);
    
}