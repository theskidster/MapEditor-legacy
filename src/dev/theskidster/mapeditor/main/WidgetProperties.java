package dev.theskidster.mapeditor.main;

import static dev.theskidster.mapeditor.main.Widget.CONTENT_X;
import static dev.theskidster.mapeditor.main.Widget.MB_HEIGHT;
import static dev.theskidster.mapeditor.main.Widget.PADDING;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_BOTTOM;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_BORDER;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_TITLE;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_label;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

public class WidgetProperties extends Widget {
    
    WidgetProperties() {
        super("Properties");
    }
    
    @Override
    void update(NkContext nkContext, Window window) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            rect.x(window.width - (CONTENT_X + PADDING));
            rect.y(PADDING + MB_HEIGHT);
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