package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author J Hoffman
 * Created: Feb 22, 2021
 */

class ElementScrollBar extends Element implements PropertyChangeListener {

    private final int size;
    
    private final boolean vertical;
    
    private final ElementArrow[] arrows  = new ElementArrow[2];
    private final Rectangle[] rectangles = new Rectangle[3];
    private final Icon[] icons           = new Icon[2];
    
    ElementScrollBar(int xOffset, int yOffset, boolean vertical, int size) {
        this.xOffset  = xOffset;
        this.yOffset  = yOffset;
        this.vertical = vertical;
        this.size     = size;
        
        icons[0] = new Icon(UI.iconTexture, 24, 24);
        icons[1] = new Icon(UI.iconTexture, 24, 24);
        
        if(vertical) {
            icons[0].setSprite(5, 0);
            icons[1].setSprite(5, 1);
            
            icons[0].position.set(xOffset, yOffset + 25);
            icons[1].position.set(xOffset, yOffset + (size + 47));
            
            rectangles[0] = new Rectangle(xOffset,     yOffset + 24, 24, size);
            rectangles[1] = new Rectangle(xOffset + 1, yOffset + 25, 22, size - 2);
        } else {
            icons[0].setSprite(5, 0);
            icons[1].setSprite(5, 2);
            
            rectangles[0] = new Rectangle(xOffset,     yOffset,     size,     24);
            rectangles[1] = new Rectangle(xOffset + 1, yOffset + 1, size - 2, 24);
        }
    }
    
    @Override
    void update(Mouse mouse) {
        
    }

    @Override
    void renderBackground(Background background) {
        background.drawRectangle(rectangles[0], Color.RGM_LIGHT_GRAY);
        background.drawRectangle(rectangles[1], Color.RGM_DARK_GRAY);
        //background.drawRectangle(rectangles[2], Color.RGM_WHITE);
    }

    @Override
    void renderIcon(ShaderProgram program) {
        for (Icon icon : icons) icon.render(program);
    }

    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "parentX" -> {
                int parentX = (Integer) evt.getNewValue();
                
                if(vertical) {
                    rectangles[0].xPos = (parentX + xOffset);
                    rectangles[1].xPos = (parentX + xOffset) + 1;

                    icons[0].position.x = (parentX + xOffset);
                    icons[1].position.x = (parentX + xOffset);
                } else {
                    
                }
            }

            case "parentY" -> {
                int parentY = (Integer) evt.getNewValue();
                
                if(vertical) {
                    rectangles[0].yPos = (parentY + yOffset) + 24;
                    rectangles[1].yPos = (parentY + yOffset) + 25;

                    icons[0].position.y = (parentY + yOffset) + 25;
                    icons[1].position.y = (parentY + yOffset) + (size + 47);
                } else {
                    
                }
            }
        }
    }
    
}