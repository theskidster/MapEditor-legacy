package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import static dev.theskidster.mapeditor.ui.Widget.MB_HEIGHT;
import java.util.HashMap;
import java.util.Map;
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
    
    private boolean clicked;
    private final boolean activeMenus[];
    
    private class Item {
        final String name;
        final int width;
        
        Item(String name, int width) {
            this.name  = name;
            this.width = width;
        }
    }
    
    private final Map<Integer, Item> items;
    
    WidgetMenuBar() {
        super("Test 2");
        
        active = true;
        
        nkRectangle.x(0);
        nkRectangle.y(0);
        nkRectangle.h(MB_HEIGHT);
        
        activeMenus = new boolean[5];
        
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
                    
                    if(clicked && nk_widget_is_hovered(nkContext)) {
                        setActiveMenu(i, widgets);
                        nk_window_show(nkContext, item.name, NK_SHOWN);
                    } else {
                        nk_window_show(nkContext, item.name, NK_HIDDEN);
                    }

                    if(nk_button_label(nkContext, item.name)) {
                        clicked = true;
                    }
                }
            }
            
            nk_end(nkContext);
            
            nk_style_pop_vec2(nkContext);
            nk_style_pop_vec2(nkContext);
        }
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
    
    boolean getMenuActive(int index) {
        return activeMenus[index];
    }

}