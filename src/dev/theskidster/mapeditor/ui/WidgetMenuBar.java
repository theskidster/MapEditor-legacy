package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.Window;
import static dev.theskidster.mapeditor.ui.Widget.*;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author J Hoffman
 * Created: Dec 27, 2020
 */

public class WidgetMenuBar extends Widget {

    private NkRect nkRectangle = NkRect.create();
    private NkVec2 nkVec2      = NkVec2.create();
    
    WidgetMenuBar() {
        super("menu bar");
    }
    
    @Override
    void update(NkContext nkContext, Window window) {
        nkRectangle.x(0);
        nkRectangle.y(0);
        nkRectangle.w(window.width);
        nkRectangle.h(MB_HEIGHT);

        nkVec2.set(0, 0);
        nkContext.style().window().padding(nkVec2);
        
        if(nk_begin(nkContext, title, nkRectangle, NK_WINDOW_NO_SCROLLBAR)) {
            nk_layout_row_begin(nkContext, NK_STATIC, MB_HEIGHT, 5);
            
            nk_layout_row_push(nkContext, 42);
            if(nk_button_label(nkContext, "File")) {
                System.out.println("file clicked");
                
            }
            
            nk_layout_row_push(nkContext, 47);
            if(nk_button_label(nkContext, "Edit")) {
                System.out.println("edit clicked");
            }
            
            nk_layout_row_push(nkContext, 51);
            if(nk_button_label(nkContext, "View")) {
                System.out.println("view clicked");
            }
            
            nk_layout_row_push(nkContext, 47);
            if(nk_button_label(nkContext, "Map")) {
                System.out.println("map clicked");
            }
            
            nk_layout_row_push(nkContext, 55);
            if(nk_button_label(nkContext, "Layer")) {
                System.out.println("layer clicked");
            }
            
            nk_layout_row_end(nkContext);
        }
        
        nkVec2.set(4, 4);
        nkContext.style().window().padding(nkVec2);

        nk_end(nkContext);
    }

}