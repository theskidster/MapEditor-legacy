package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.Window;
import static dev.theskidster.mapeditor.ui.Widget.*;
import dev.theskidster.mapeditor.util.Event;
import static dev.theskidster.mapeditor.util.Event.*;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

public class WidgetMenuBar extends Widget {

    private int prevMenuIndex = -1;
    private int currMenuIndex = -1;
    
    boolean blockWindowReset;
    private final boolean activeMenus[];
    
    private final NkRect nkRectangle = NkRect.create();
    
    private class Option {
        final String name;
        final int width;
        
        Option(String name, int width) {
            this.name  = name;
            this.width = width;
        }
    }
    
    private final Map<Integer, Option> options;
    
    WidgetMenuBar() {
        super("Menu Bar");
        
        nkRectangle.x(0);
        nkRectangle.y(0);
        nkRectangle.h(MB_HEIGHT);
        
        activeMenus = new boolean[5];
        
        options = new HashMap<Integer, Option>() {{
            put(0, new Option("File", 42));
            put(1, new Option("Edit", 47));
            put(2, new Option("View", 51));
            put(3, new Option("Map", 47));
            put(4, new Option("Layer", 55));
        }};
    }
    
    @Override
    void update(NkContext nkContext, Window window, Map<String, Widget> widgets) {
        nkRectangle.w(window.width);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkVec2 nkPaddingBuf = NkVec2.mallocStack(stack);
            NkVec2 nkSpacingBuf = NkVec2.mallocStack(stack);
            
            nkPaddingBuf.set(0, 0);
            nkSpacingBuf.set(0, 0);
                    
            nk_style_push_vec2(nkContext, nkContext.style().window().padding(), nkPaddingBuf);
            nk_style_push_vec2(nkContext, nkContext.style().window().spacing(), nkSpacingBuf);
            
            if(getAnyActiveMenu()) {
                NkColor blue = UI.createColor(stack, 24, 88, 184, 255);
                nk_style_push_color(nkContext, nkContext.style().button().hover().data().color(), blue);
            }
            
            if(nk_begin(nkContext, title, nkRectangle, NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_begin(nkContext, NK_STATIC, MB_HEIGHT, 5);
                
                for(int m = 0; m < activeMenus.length; m++) {
                    Option option = options.get(m);
                    
                    nk_layout_row_push(nkContext, option.width);
                    
                    if(activeMenus[m]) {
                        NkColor blue = UI.createColor(stack, 24, 88, 184, 255);
                        nk_style_push_color(nkContext, nkContext.style().button().normal().data().color(), blue);
                    } else {
                        nk_style_push_color(nkContext, nkContext.style().button().normal().data().color(), UI.nkGray2);
                    }
                    
                    if(nk_widget_is_hovered(nkContext)) {
                        if(getAnyActiveMenu()) setActiveMenu(m);
                        
                        if(currMenuIndex == m && widgets.containsKey(option.name)) {
                            blockWindowReset = currMenuIndex == prevMenuIndex;
                        }
                    } else {
                        if(currMenuIndex == m && widgets.containsKey(option.name)) {
                            blockWindowReset = currMenuIndex == prevMenuIndex && widgets.get(option.name).hovered;
                        }
                    }
                    
                    if(nk_button_label(nkContext, option.name)) {
                        if(currMenuIndex == prevMenuIndex && currMenuIndex != -1) {
                            resetState();
                        } else {
                            setActiveMenu(m);
                        }
                    }
                    
                    nk_style_pop_color(nkContext);
                }
                
                if(prevMenuIndex != currMenuIndex) {
                    switch(currMenuIndex) {
                        case 0: App.addEvent(new Event(WIDGET_FILE, null));  break;
                        case 1: App.addEvent(new Event(WIDGET_EDIT, null));  break;
                        case 2: App.addEvent(new Event(WIDGET_VIEW, null));  break;
                        case 3: App.addEvent(new Event(WIDGET_MAP, null));   break;
                        case 4: App.addEvent(new Event(WIDGET_LAYER, null)); break;
                    }
                }
            }
            
            nk_end(nkContext);
            
            nk_style_pop_vec2(nkContext);
            nk_style_pop_vec2(nkContext);
            nk_style_pop_color(nkContext);
        }
    }
    
    private void setActiveMenu(int index) {
        prevMenuIndex = currMenuIndex;
        currMenuIndex = index;
        
        for(int m = 0; m < activeMenus.length; m++) {
            activeMenus[m] = (m == index);
        }
    }
    
    boolean getAnyActiveMenu() {
        for(int m = 0; m < activeMenus.length; m++) {
            if(activeMenus[m]) return true;
        }
        
        return false;
    }
    
    boolean getActiveMenu(int index) {
        return activeMenus[index];
    }
    
    void resetState() {
        prevMenuIndex    = -1;
        currMenuIndex    = -1;
        blockWindowReset = false;
        for(int m = 0; m < activeMenus.length; m++) activeMenus[m] = false;
    }

}