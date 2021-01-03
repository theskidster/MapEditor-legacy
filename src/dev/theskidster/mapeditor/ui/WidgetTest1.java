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
        super("File");
        
        nkRectangle.x(0);
        nkRectangle.y(MB_HEIGHT);
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
            if(nk_begin(nkContext, title, nkRectangle, NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_begin(nkContext, NK_STATIC, MB_HEIGHT, 1);
                nk_layout_row_push(nkContext, 300);
                if(nk_widget_is_hovered(nkContext)) {
                    System.out.println("new hovered");
                }
                if(nk_button_label(nkContext, "New")) {
                    System.out.println("new pressed");
                }
                nk_layout_row_end(nkContext);

                nk_layout_row_begin(nkContext, NK_STATIC, MB_HEIGHT, 1);
                nk_layout_row_push(nkContext, 300);
                if(nk_button_label(nkContext, "Open")) {
                    System.out.println("open pressed");
                }
                nk_layout_row_end(nkContext);
                
                hovered = nk_window_is_hovered(nkContext);
            }
            
            nk_end(nkContext);
        }
    }

}