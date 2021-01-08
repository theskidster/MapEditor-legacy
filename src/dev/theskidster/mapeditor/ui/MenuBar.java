package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
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
    
    private final int MB_HEIGHT = 28;
    private int currMenuIndex;
    
    boolean openSubMenus;
    private final boolean[] activeMenu;
    private final boolean[] hoveredMenu;
    
    private final Rectangle rectangle;
    private final Background background;
    
    private final List<LabelButton> buttons;
    private final Map<Integer, SubMenu> subMenus = new HashMap<>();
    
    public MenuBar() {
        rectangle   = new Rectangle(0, 0, 0, MB_HEIGHT);
        background  = new Background(6);
        activeMenu  = new boolean[5];
        hoveredMenu = new boolean[5];
        
        //Initialize menubar buttons
        {
            Rectangle[] rectangles = {
                new Rectangle(0,   0, 46, MB_HEIGHT),
                new Rectangle(45,  0, 50, MB_HEIGHT),
                new Rectangle(94,  0, 54, MB_HEIGHT),
                new Rectangle(147, 0, 52, MB_HEIGHT),
                new Rectangle(198, 0, 59, MB_HEIGHT)
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
                add(new Rectangle(1, MB_HEIGHT + 1,       298, MB_HEIGHT));
                add(new Rectangle(1, MB_HEIGHT * 2,       298, MB_HEIGHT));
                add(new Rectangle(1, (MB_HEIGHT * 3) - 1, 298, MB_HEIGHT));
                add(new Rectangle(1, MB_HEIGHT * 4,       298, MB_HEIGHT));
                add(new Rectangle(1, (MB_HEIGHT * 5) - 1, 298, MB_HEIGHT));
                add(new Rectangle(1, MB_HEIGHT * 6,       298, MB_HEIGHT));
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
            
            rectangles.clear();
            
            rectangles.add(new Rectangle(0, MB_HEIGHT, 300, (MB_HEIGHT * 6) + 1));
            
            subMenus.put(0, new SubMenu(subMenuButtons, rectangles));
        }
        
        //TODO: add Edit submenu options
        {
            List<Rectangle> rectangles = new ArrayList<>() {{
                add(new Rectangle(45, MB_HEIGHT, 280, 200));
            }};
            
            subMenus.put(1, new SubMenu(new ArrayList<>(), rectangles));
        }
        
        //TODO: add View submenu options
        {
            List<Rectangle> rectangles = new ArrayList<>() {{
                add(new Rectangle(94, MB_HEIGHT, 315, 213));
            }};
            
            subMenus.put(2, new SubMenu(new ArrayList<>(), rectangles));
        }
        
        //TODO: add Map submenu options
        {
            List<Rectangle> rectangles = new ArrayList<>() {{
                add(new Rectangle(147, MB_HEIGHT, 300, 312));
            }};
            
            subMenus.put(3, new SubMenu(new ArrayList<>(), rectangles));
        }
        
        //TODO: add Layer submenu options
        {
            List<Rectangle> rectangles = new ArrayList<>() {{
                add(new Rectangle(198, MB_HEIGHT, 280, 350));
            }};
            
            subMenus.put(4, new SubMenu(new ArrayList<>(), rectangles));
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
        
        if(openSubMenus) subMenus.get(currMenuIndex).update(mouse);
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
            if(hoveredMenu[m]) return true;
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