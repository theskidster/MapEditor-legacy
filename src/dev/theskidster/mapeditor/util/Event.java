package dev.theskidster.mapeditor.util;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

public final class Event {

    public final int type;
    
    public boolean resolved;
    
    public final Object data;
    
    public static final int WIDGET_FILE  = 0;
    public static final int WIDGET_EDIT  = 1;
    public static final int WIDGET_VIEW  = 2;
    public static final int WIDGET_MAP   = 3;
    public static final int WIDGET_LAYER = 4;
    
    public Event(int type, Object data) {
        this.type = type;
        this.data = data;
    }
    
}