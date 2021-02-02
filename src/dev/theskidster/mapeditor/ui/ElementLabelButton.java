package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Observable;
import dev.theskidster.mapeditor.util.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 23, 2021
 */

final class ElementLabelButton extends Element implements PropertyChangeListener {

    private final int xOffset;
    private final int yOffset;
    private final int width;
    
    private boolean prevPressed;
    private boolean currPressed;
    boolean hovered;
    boolean clicked;
    
    private final Rectangle rectangle;
    private final Rectangle aabb;
    private final Icon iconLeft;
    private final Icon iconRight;
    private Color color;
    final String text;
    private final Vector2i textPos;
    
    Observable observable = new Observable(this);
    
    ElementLabelButton(int xOffset, int yOffset, int width, String text, PropertyChangeListener observer) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width   = ((width - 30) < 0) ? 0 : width - 30;
        this.text    = text;
        
        rectangle = new Rectangle(xOffset, yOffset, this.width, 30);
        aabb      = new Rectangle(xOffset, yOffset, width, 30);
        iconLeft  = new Icon("spr_icons.png", 15, 30);
        iconRight = new Icon("spr_icons.png", 15, 30);
        
        textPos = new Vector2i();
        
        observable.properties.put("labelButtonClicked", false);
        observable.addObserver(observer);
    }
    
    @Override
    void update(Mouse mouse) {
        if(aabb.intersects(mouse.cursorPos)) {
            hovered     = true;
            prevPressed = currPressed;
            currPressed = mouse.clicked;
            
            observable.notifyObservers("labelButtonClicked", prevPressed != currPressed && !prevPressed);
            
            color = Color.BLUE;
            iconLeft.setSprite(4, 1);
            iconRight.setSprite(5, 1);
        } else {
            color = Color.MEDIUM_GRAY;
            iconLeft.setSprite(4, 0);
            iconRight.setSprite(5, 0);
        }
    }

    @Override
    void renderBackground(Background background) {
        background.drawRectangle(rectangle, color);
    }

    @Override
    void renderIcon(ShaderProgram program) {
        iconLeft.render(program);
        iconRight.render(program);
    }

    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {
        font.drawString(program, text, textPos.x, textPos.y, 1, Color.WHITE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "parentX" -> {
                int parentX = (Integer) evt.getNewValue();
                
                int textSize = TrueTypeFont.getLengthInPixels(text, 1) / 2;
                textPos.x = (parentX + xOffset + (((width + 30) / 2) - textSize));
                
                rectangle.xPos       = (parentX + xOffset + 15);
                aabb.xPos            = (parentX + xOffset);
                iconLeft.position.x  = (parentX + xOffset);
                iconRight.position.x = (parentX + xOffset + width + 15);
            }

            case "parentY" -> {
                int parentY = (Integer) evt.getNewValue();
                
                textPos.y = (parentY + yOffset + 20);
                
                rectangle.yPos       = (parentY + yOffset);
                aabb.yPos            = (parentY + yOffset);
                iconLeft.position.y  = (parentY + yOffset + 30);
                iconRight.position.y = (parentY + yOffset + 30);
            }
        }
    }

}