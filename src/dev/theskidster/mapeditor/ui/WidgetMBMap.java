package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import java.util.Map;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 29, 2020
 */

public class WidgetMBMap extends Widget {

    private boolean initialFocus;
    
    private final NkRect[] nkRectangles = new NkRect[4];
    
    public WidgetMBMap() {
        super("Map");
        
        for(int r = 0; r < nkRectangles.length; r++) {
            nkRectangles[r] = NkRect.create();
        }
        
        nkRectangles[0].x(0);
        nkRectangles[0].y(MB_HEIGHT);
        nkRectangles[0].w(CONTENT_X + 20);
        nkRectangles[0].h(MB_HEIGHT * 20);
        
        nkRectangles[1].x(0);
        nkRectangles[1].y(0);
        nkRectangles[1].w(42);
        nkRectangles[1].h(MB_HEIGHT);
        
        nkRectangles[2].x(0);
        nkRectangles[2].y(MB_HEIGHT);
        nkRectangles[2].w(CONTENT_X);
        nkRectangles[2].h(MB_HEIGHT * 4);
    }
    
    @Override
    void update(NkContext nkContext, Window window, Map<String, Widget> widgets) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkColor nkGray = UI.createColor(stack, 70, 70, 70, 255);
            nk_style_push_color(nkContext, nkContext.style().window().fixed_background().data().color(), nkGray);

            if(nk_begin(nkContext, title, nkRectangles[0], NK_WINDOW_NO_SCROLLBAR)) {
                if(!initialFocus) {
                    nk_window_set_focus(nkContext, "Menu Bar");
                    initialFocus = true;
                }
                
                hovered = nk_window_is_hovered(nkContext);
                
                if(hovered) {
                    //TODO: add options
                } else {
                    if(!((WidgetMenuBar) widgets.get("Menu Bar")).getActiveMenu(3)) {
                        removeRequest = true;
                    }
                }
            }

            nk_end(nkContext);
            nk_style_pop_color(nkContext);
        }
    }

}