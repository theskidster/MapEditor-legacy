package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Observable;
import java.beans.PropertyChangeListener;

/**
 * @author J Hoffman
 * Created: Jan 17, 2021
 */

class ElementArrow extends Element {
    
    private final boolean up;
    
    private final Icon icon;
    private final Rectangle rectangle;
    private Color color;
    private final String propertyName;
    
    Observable observable = new Observable(this);
    
    ElementArrow(int xOffset, int yOffset, int rectWidth, int rectHeight, boolean up, PropertyChangeListener observer) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.up      = up;
        
        icon      = new Icon(UI.iconTexture, 20, 20);
        rectangle = new Rectangle(xOffset, yOffset, rectWidth, rectHeight);
        
        if(up) {
            icon.setSprite(0, 3);
            propertyName = "clickedUp";
        } else {
            icon.setSprite(1, 3);
            propertyName = "clickedDown";
        }
        
        observable.properties.put(propertyName, false);
        observable.addObserver(observer);
    }
    
    void setParentX(int parentX) {
        rectangle.xPos  = xOffset + parentX + 1;
        icon.position.x = xOffset + parentX + 2;
    }
    
    void setParentY(int parentY) {
        rectangle.yPos  = yOffset + parentY;
        icon.position.y = yOffset + parentY + ((up) ? 17 : 18);
    }
    
    @Override
    void update(Mouse mouse) {
        if(rectangle.intersects(mouse.cursorPos)) {
            hovered     = true;
            prevPressed = currPressed;
            currPressed = mouse.clicked;
            
            observable.notifyObservers(propertyName, prevPressed != currPressed && !prevPressed);
            
            color = (currPressed) ? Color.RGM_BLUE : Color.RGM_MEDIUM_GRAY;
        } else {
            hovered = false;
            color   = Color.RGM_DARK_GRAY;
        }
    }
    
    @Override
    void renderBackground(Background background) {
        background.drawRectangle(rectangle, color);
    }
    
    @Override
    void renderIcon(ShaderProgram program) {
        icon.render(program);
    }

    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {}
    
}