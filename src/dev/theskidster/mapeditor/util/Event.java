package dev.theskidster.mapeditor.util;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public class Event {

    public static final int WIDGET_NEW_MAP = 0;
    
    public final int type;
    
    public boolean resolved;
    
    public Event(int type) {
        this.type = type;
    }
    
}