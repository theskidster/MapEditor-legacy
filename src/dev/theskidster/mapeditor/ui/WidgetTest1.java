package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import java.util.Map;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 30, 2020
 */

class WidgetTest1 extends Widget {

    private final NkRect nkRectangle = NkRect.create();
    
    WidgetTest1() {
        super("Test 1");
        
        nkRectangle.x(200);
        nkRectangle.y(200);
        nkRectangle.w(300);
        nkRectangle.h(250);
        
        /*
        TODO: 
        I've provided these two test classes temporarily to get a better 
        understanding of the nuklear API through experimentation. I'll continue 
        adding features once I feel confortable with it.
        */
    }
    
    @Override
    void update(NkContext nkContext, Window window, Map<String, Widget> widgets) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            if(nk_begin(nkContext, title, nkRectangle, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR)) {
                
            }
            
            nk_end(nkContext);
        }
    }

}