package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import java.util.Map;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

class WidgetBlocks extends Widget {

    WidgetBlocks() {
        super("Blocks");
    }
    
    @Override
    public void update(NkContext nkContext, Window window, Map<String, Widget> widgets) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            rect.x(window.width - (CONTENT_X + PADDING));
            rect.y((window.height / 2) + MB_HEIGHT);
            rect.w(CONTENT_X);
            rect.h((window.height / 2) - (PADDING + MB_HEIGHT));
            
            if(nk_begin(nkContext, title, rect, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
                nk_layout_row_dynamic(nkContext, 20, 1);
                nk_label(nkContext, "test text", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);
            }
            
            nk_end(nkContext);
        }
    }
    
}