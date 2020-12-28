package dev.theskidster.mapeditor.main;

import static dev.theskidster.mapeditor.main.Widget.*;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

public class WidgetMenuBar extends Widget {

    WidgetMenuBar() {
        super("menu bar");
    }
    
    @Override
    void update(NkContext nkContext, Window window) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            rect.x(0);
            rect.y(0);
            rect.w(window.width);
            rect.h(MB_HEIGHT);
            
            if(nk_begin(nkContext, title, rect, NK_WINDOW_BORDER | NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_dynamic(nkContext, 20, 1);
            }
            
            nk_end(nkContext);
        }
    }

}