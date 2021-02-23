package dev.theskidster.mapeditor.util;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public class Event {

    public static final int WIDGET_NEW_MAP = 0;
    
    public final int type;
    
    public boolean resolved;
    
    public Object data;
    
    public Event(int type, Object data) {
        this.type = type;
        this.data = data;
    }
    
}