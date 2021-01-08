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
    
    private class LabelButton {
        
        private boolean prevPressed;
        private boolean currPressed;
        boolean hovered;
        
        private final String text;
        private final Rectangle rectangle;
        private final Vector2i padding;
        private Color color;
        
        LabelButton(String text, Rectangle rectangle, Vector2i padding) {
            this.text      = text;
            this.rectangle = rectangle;
            this.padding   = padding;
        }
        
        void update(Mouse mouse, MenuBar menubar, boolean active) {
            if(rectangle.intersects(mouse.cursorPos)) {
                hovered     = true;
                prevPressed = currPressed;
                currPressed = mouse.clicked;
                
                if(prevPressed != currPressed && !prevPressed) {
                    menubar.openSubMenus = !menubar.openSubMenus;
                    if(!menubar.openSubMenus) menubar.resetState();
                }
                
                color = (currPressed || menubar.openSubMenus) ? Color.BLUE : Color.SILVER;
            } else {
                hovered = false;
                color   = (active) ? Color.BLUE : Color.GRAY;
            }
        }
        
        void update(Mouse mouse) {
            hovered = rectangle.intersects(mouse.cursorPos);
            color   = (hovered) ? Color.BLUE : Color.GRAY;
        }
        
        void renderBackground(Background background) {
            background.drawRectangle(rectangle, color);
        }
        
        void renderText(ShaderProgram program, TrueTypeFont font) {
            int xOffset = rectangle.xPos + padding.x;
            int yOffset = rectangle.yPos + padding.y + TrueTypeFont.FONT_HEIGHT;
            
            font.drawString(program, text, xOffset, yOffset, 1, Color.WHITE);
        }
        
    }
    
    private class SubMenu {
        
        boolean hovered;
        
        private final Background background;
        
        private final List<LabelButton> buttons;
        private final List<Rectangle> rectangles;
        
        SubMenu(List<LabelButton> buttons, List<Rectangle> rectangles) {
            this.buttons    = buttons;
            this.rectangles = rectangles;
            
            background = new Background(buttons.size() + rectangles.size());
        }
        
        void update(Mouse mouse) {
            hovered = rectangle.intersects(mouse.cursorPos);
            
            buttons.forEach(button -> button.update(mouse));
        }
        
        void render(ShaderProgram program, TrueTypeFont text) {
            background.batchStart();
                rectangles.forEach(rect -> background.drawRectangle(rect, Color.SILVER));
                buttons.forEach(button -> button.renderBackground(background));
            background.batchEnd(program);
            
            buttons.forEach(button -> button.renderText(program, text));
        }
        
    }
    
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
        
        Vector2i padding;
        
        //Initialize buttons that will appear on the menubar
        {
            Rectangle[] rectangles = {
                new Rectangle(0,   0, 46, MB_HEIGHT),
                new Rectangle(45,  0, 50, MB_HEIGHT),
                new Rectangle(94,  0, 54, MB_HEIGHT),
                new Rectangle(147, 0, 52, MB_HEIGHT),
                new Rectangle(198, 0, 59, MB_HEIGHT)
            };

            padding = new Vector2i(8, 2);

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
                add(new Rectangle(0, MB_HEIGHT,     300, MB_HEIGHT));
                add(new Rectangle(0, MB_HEIGHT * 2, 300, MB_HEIGHT));
            }};
            
            List<LabelButton> subMenuButtons = new ArrayList<>() {{
                add(new LabelButton("New Map...",       rectangles.get(0), padding));
                add(new LabelButton("New Blockset...",  rectangles.get(1), padding));
            }};
            
            rectangles.clear();
            
            rectangles.add(new Rectangle(0, MB_HEIGHT, 300, 300));
            
            subMenus.put(0, new SubMenu(subMenuButtons, rectangles));
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
            background.drawRectangle(rectangle, Color.GRAY);
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