package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * @author J Hoffman
 * Created: Jan 16, 2021
 */

final class FolderButton extends Component implements PropertyChangeListener {

    private final int xOffset;
    private final int yOffset;

    private boolean clicked;

    private final Rectangle rectangle;
    private final Icon icon;
    private TextArea textArea;
    
    FolderButton(int xOffset, int yOffset, TextArea textArea) {
        this.xOffset  = xOffset;
        this.yOffset  = yOffset;
        this.textArea = textArea;

        rectangle = new Rectangle(0, 0, 20, 20);
        icon      = new Icon("spr_icons.png", 20, 20);

        icon.setSprite(1, 0);
    }

    public void update(Mouse mouse) {
        if(rectangle.intersects(mouse.cursorPos)) {
            if(mouse.clicked) {
                if(!clicked) {
                    JFileChooser fc = new JFileChooser();
                    
                    if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        
                        textArea.setText(file.getAbsolutePath());
                        textArea.focus();
                        
                        //TODO: store file object to be used during map creation
                        
                        Logger.log(LogLevel.INFO, "Opening file: \"" + file.getName() + "\"");
                    }
                    
                    clicked = true;
                }
            } else {
                clicked = false;
            }
        }
    }

    public void render(ShaderProgram program) {
        icon.render(program);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "parentX":
                int parentX = (Integer) evt.getNewValue();

                rectangle.xPos  = (parentX + xOffset);
                icon.position.x = (parentX + xOffset);
                break;

            case "parentY":
                int parentY = (Integer) evt.getNewValue();

                rectangle.yPos  = (parentY + yOffset - 20);
                icon.position.y = (parentY + yOffset);
                break;
        }
    }
}