package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Mouse;
import java.awt.Component;

/**
 * @author J Hoffman
 * Created: Jan 19, 2021
 */

abstract class Element extends Component {
    
    protected int xOffset;
    protected int yOffset;
    
    protected boolean prevPressed;
    protected boolean currPressed;
    protected boolean hovered;
    protected boolean clicked;
    
    abstract void update(Mouse mouse);
    
    abstract void renderBackground(Background background);
    abstract void renderIcon(ShaderProgram program);
    abstract void renderText(ShaderProgram program, TrueTypeFont font);
    
}