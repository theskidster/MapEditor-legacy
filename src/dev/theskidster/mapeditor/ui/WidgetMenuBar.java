package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import static dev.theskidster.mapeditor.ui.UI.createColor;
import static dev.theskidster.mapeditor.ui.Widget.MB_HEIGHT;
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
 * Created: Dec 30, 2020
 */

class WidgetMenuBar extends Widget {
    
    private final NkRect nkRectangle = NkRect.create();
    
    private final boolean activeMenus[];
    private final boolean hoveredItems[];
    
    class Item {
        final String name;
        final int width;
        
        Item(String name, int width) {
            this.name  = name;
            this.width = width;
        }
    }
    
    final Map<Integer, Item> items;
    
    WidgetMenuBar() {
        super("Test 2");
        
        active = true;
        
        nkRectangle.x(0);
        nkRectangle.y(0);
        nkRectangle.h(MB_HEIGHT);
        
        activeMenus  = new boolean[5];
        hoveredItems = new boolean[5];
        
        items = new HashMap<Integer, Item>() {{
            put(0, new Item("File", 42));
            put(1, new Item("Edit", 47));
            //put(2, new Item("View", 51));
            //put(3, new Item("Map", 47));
            //put(4, new Item("Layer", 55));
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
            
            if(nk_begin(nkContext, title, nkRectangle, NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_begin(nkContext, NK_STATIC, MB_HEIGHT, 5);
                
                for(int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    
                    nk_layout_row_push(nkContext, item.width);
                    
                    if(activeMenus[i] && clicked) {
                        NkColor nkBlue = UI.createColor(stack, 24, 88, 184, 255);
                        nk_style_push_color(nkContext, nkContext.style().button().normal().data().color(), nkBlue);
                        nk_style_push_color(nkContext, nkContext.style().button().hover().data().color(), nkBlue);
                    } else {
                        NkColor nkGray2 = UI.createColor(stack, 52, 52, 52, 255);
                        nk_style_push_color(nkContext, nkContext.style().button().normal().data().color(), nkGray2);
                        nk_style_push_color(nkContext, nkContext.style().button().hover().data().color(), nkGray2);
                    }
                    
                    if(!widgets.get("File").hovered && 
                       !widgets.get("Edit").hovered) {
                        nk_window_set_focus(nkContext, title);
                    }
                    
                    if(!clicked) {
                        nk_window_show(nkContext, item.name, NK_HIDDEN);
                    } else {
                        if(nk_widget_is_hovered(nkContext)) {
                            setActiveMenu(i, widgets);
                            nk_window_show(nkContext, item.name, NK_SHOWN);
                            
                            hoveredItems[i] = true;
                        } else {
                            if(!widgets.get(item.name).active) {
                                nk_window_show(nkContext, item.name, NK_HIDDEN);
                            }
                            
                            hoveredItems[i] = false;
                        }
                    }
                    
                    if(nk_button_label(nkContext, item.name)) clicked = !clicked;
                    
                    nk_style_pop_color(nkContext);
                }
            }
            
            nk_end(nkContext);
            
            nk_style_pop_vec2(nkContext);
            nk_style_pop_vec2(nkContext);
        }
    }
    
    private boolean getAnyItemHovered() {
        for(int m = 0; m < hoveredItems.length; m++) {
            if(hoveredItems[m]) return true;
        }
        
        return false;
    }
    
    private void setActiveMenu(int index, Map<String, Widget> widgets) {
        for(int m = 0; m < activeMenus.length; m++) {
            activeMenus[m] = (m == index);
            
            switch(m) {
                case 0: widgets.get("File").active = (m == index); break;
                case 1: widgets.get("Edit").active = (m == index); break;
                //case 0: widgets.get("File").active = (m == index); break;
                //case 0: widgets.get("File").active = (m == index); break;
                //case 0: widgets.get("File").active = (m == index); break;
            }
        }
    }
    
    public void resetState(NkContext nkContext) {
        clicked = getAnyItemHovered();
        
        for(int i = 0; i < items.size(); i++) {
            activeMenus[i]  = false;
            hoveredItems[i] = false;
        }
        
        //Causes a visible flash on the menu bar, required to make it function
        nk_window_close(nkContext, title);
    }

}