package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import java.util.Map;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_window_is_hovered;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 1, 2021
 */

public class WidgetTest2 extends Widget {
    
    private final NkRect nkRectangle = NkRect.create();
    
    WidgetTest2() {
        super("Edit");
        
        nkRectangle.x(0);
        nkRectangle.y(MB_HEIGHT);
        nkRectangle.w(350);
        nkRectangle.h(200);
    }
    
    @Override
    void update(NkContext nkContext, Window window, Map<String, Widget> widgets) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            if(nk_begin(nkContext, title, nkRectangle, NK_WINDOW_NO_SCROLLBAR)) {
                if(nk_window_is_hovered(nkContext)) {
                    System.out.println("cursor over edit options");
                }
            }
            
            nk_end(nkContext);
        }
    }
    
}