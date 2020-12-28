package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

class WidgetLayers extends Widget {

    WidgetLayers() {
        super("Layers");
    }
    
    @Override
    public void update(NkContext nkContext, Window window) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            rect.x(PADDING);
            rect.y(PADDING + MB_HEIGHT);
            rect.w(CONTENT_X);
            rect.h(window.height - ((PADDING * 2) + MB_HEIGHT));
            
            if(nk_begin(nkContext, title, rect, NK_WINDOW_TITLE)) {
                nk_layout_row_dynamic(nkContext, 20, 1);
                nk_label(nkContext, "test text", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);
                nk_layout_row_dynamic(nkContext, 20, 2);
                nk_label(nkContext, "test text", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);
            }
            
            nk_end(nkContext);
        }
    }

}