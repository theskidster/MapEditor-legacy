package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Event;
import static dev.theskidster.mapeditor.util.Event.WIDGET_NEW_MAP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public class MenuBar extends Widget {
    
    private final int HEIGHT = 28;
    private int currMenuIndex;
    
    boolean openSubMenus;
    private final boolean[] activeMenu;
    private final boolean[] hoveredMenu;
    
    private final Rectangle rectangle;
    private final Background background;
    
    private final List<LabelButton> buttons;
    private final Map<Integer, SubMenu> subMenus = new HashMap<>();
    
    public MenuBar() {
        rectangle   = new Rectangle(0, 0, 0, HEIGHT);
        background  = new Background(7); //Setting this to 6 causes the Quit option to flash for whatever reason
        activeMenu  = new boolean[5];
        hoveredMenu = new boolean[5];
        
        //Initialize menubar buttons
        {
            Rectangle[] rectangles = {
                new Rectangle(0,   0, 46, HEIGHT),
                new Rectangle(45,  0, 50, HEIGHT),
                new Rectangle(94,  0, 54, HEIGHT),
                new Rectangle(147, 0, 52, HEIGHT),
                new Rectangle(198, 0, 59, HEIGHT)
            };

            Vector2i padding = new Vector2i(8, 2);

            buttons = new ArrayList<>() {{
                add(new LabelButton("File",  rectangles[0], padding));
                add(new LabelButton("Edit",  rectangles[1], padding));
                add(new LabelButton("View",  rectangles[2], padding));
                add(new LabelButton("Map",   rectangles[3], padding));
                add(new LabelButton("Layer", rectangles[4], padding));
            }};
        }
        
        //Initialize File submenu options
        {
            List<Rectangle> rectangles = new ArrayList<>() {{
                add(new Rectangle(1, HEIGHT + 1,       298, HEIGHT));
                add(new Rectangle(1, HEIGHT * 2,       298, HEIGHT));
                add(new Rectangle(1, (HEIGHT * 3) - 1, 298, HEIGHT));
                add(new Rectangle(1, HEIGHT * 4,       298, HEIGHT));
                add(new Rectangle(1, (HEIGHT * 5) - 1, 298, HEIGHT));
                add(new Rectangle(1, HEIGHT * 6,       298, HEIGHT));
            }};
            
            Vector2i padding = new Vector2i(32, 2);
            
            List<LabelButton> subMenuButtons = new ArrayList<>() {{
                add(new LabelButton("New Map...",       rectangles.get(0), padding));
                add(new LabelButton("New Blockset...",  rectangles.get(1), padding));
                add(new LabelButton("Open...",          rectangles.get(2), padding));
                add(new LabelButton("Save",             rectangles.get(3), padding));
                add(new LabelButton("Save As...",       rectangles.get(4), padding));
                add(new LabelButton("Quit",             rectangles.get(5), padding));
            }};
            
            subMenus.put(0, new SubMenu(subMenuButtons, new Rectangle(0, HEIGHT, 300, (HEIGHT * 6) + 1)));
        }
        
        //TODO: add Edit submenu options
        {
            subMenus.put(1, new SubMenu(new ArrayList<>(), new Rectangle(45, HEIGHT, 280, 200)));
        }
        
        //TODO: add View submenu options
        {
            subMenus.put(2, new SubMenu(new ArrayList<>(), new Rectangle(94, HEIGHT, 315, 213)));
        }
        
        //TODO: add Map submenu options
        {
            subMenus.put(3, new SubMenu(new ArrayList<>(), new Rectangle(147, HEIGHT, 300, 312)));
        }
        
        //TODO: add Layer submenu options
        {
            subMenus.put(4, new SubMenu(new ArrayList<>(), new Rectangle(198, HEIGHT, 280, 350)));
        }
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        hovered = rectangle.intersects(mouse.cursorPos);
        rectangle.width = width;
        
        for(int m = 0; m < buttons.size(); m++) {
            buttons.get(m).update(mouse, this, activeMenu[m]);
            
            hoveredMenu[m] = buttons.get(m).hovered;
            if(openSubMenus && buttons.get(m).hovered) setActiveMenu(m);
        }
        
        if(openSubMenus) {
            subMenus.get(currMenuIndex).update(mouse);
            
            subMenus.get(currMenuIndex).buttons.forEach((button -> {
                if(button.clicked) {
                    switch(button.text) {
                        case "New Map...": 
                            App.addEvent(new Event(WIDGET_NEW_MAP)); 
                            break;
                            
                        case "Quit":
                            App.end();
                            break;
                    }
                    
                    openSubMenus = false;
                    resetState();
                }
            }));
        }
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {        
        background.batchStart();
            background.drawRectangle(rectangle, Color.DARK_GRAY);
            buttons.forEach(button -> button.renderBackground(background));
        background.batchEnd(program);
        
        if(openSubMenus) subMenus.get(currMenuIndex).render(program, font);
        buttons.forEach(button -> button.renderText(program, font));
    }
    
    private void setActiveMenu(int index) {
        for(int m = 0; m < buttons.size(); m++) {
            activeMenu[m] = (m == index);
            if(m == index) currMenuIndex = index;
        }
    }
    
    private boolean getAnyMenuHovered() {
        for(int m = 0; m < buttons.size(); m++) {
            if(hoveredMenu[m] || subMenus.get(currMenuIndex).hovered) {
                return true;
            }
        }
        
        return false;
    }
    
    boolean getMenuBarActive() {
        return openSubMenus && getAnyMenuHovered();
    }
    
    void resetState() {
        openSubMenus = false;
        Arrays.fill(activeMenu, false);
    }
    
}