package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Observable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public final class FrameNewMap extends Frame implements PropertyChangeListener {
    
    private final FocusableTextArea textArea;
    private final ElementFolderButton fButton;
    private final FocusableSpinBox spinBox1;
    private final FocusableSpinBox spinBox2;
    private final FocusableSpinBox spinBox3;
    private final ElementLabelButton lButton1;
    private final ElementLabelButton lButton2;
    
    private final Background background;
    
    private final Observable observable = new Observable(this);
    
    //TODO: parse files using location provided from text areas
    private File skybox;
    
    public FrameNewMap(int xPos, int yPos) {
        super(new Icon("spr_icons.png", 20, 20), "New Map", xPos, yPos, 488, 320, true);
        
        background = new Background(23);
        
        textArea = new FocusableTextArea(126, 52, 300);
        fButton  = new ElementFolderButton(440, 77, textArea);
        spinBox1 = new FocusableSpinBox(126, 162, 186, "blocks", 64);
        spinBox2 = new FocusableSpinBox(126, 205, 186, "blocks", 32);
        spinBox3 = new FocusableSpinBox(126, 248, 186, "blocks", 64);
        lButton1 = new ElementLabelButton(368, 219, 96, "Save As...", this);
        lButton2 = new ElementLabelButton(368, 262, 96, "Cancel", this);
        
        observable.properties.put("parentX", xPos);
        observable.properties.put("parentY", yPos);
        observable.addObserver(textArea);
        observable.addObserver(fButton);
        observable.addObserver(spinBox1);
        observable.addObserver(spinBox2);
        observable.addObserver(spinBox3);
        observable.addObserver(lButton1);
        observable.addObserver(lButton2);
        
        icon.setSprite(2, 2);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        center(width, height);
        observable.notifyObservers("parentX", xPos);
        observable.notifyObservers("parentY", yPos);
        
        closeButton.update(mouse);
        textArea.update(mouse);
        fButton.update(mouse);
        spinBox1.update(mouse);
        spinBox2.update(mouse);
        spinBox3.update(mouse);
        lButton1.update(mouse);
        lButton2.update(mouse);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(titleBar, Color.BLACK);
            background.drawRectangle(content, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 38, 462, 58, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 39, 460, 56, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 148, 337, 144, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 149, 335, 142, Color.DARK_GRAY);
            closeButton.renderBackground(background);
            textArea.renderBackground(background);
            spinBox1.renderBackground(background);
            spinBox2.renderBackground(background);
            spinBox3.renderBackground(background);
            lButton1.renderBackground(background);
            lButton2.renderBackground(background);
        background.batchEnd(program);
        
        icon.render(program);
        closeButton.renderIcon(program);
        textArea.renderIcon(program);
        fButton.renderIcon(program);
        spinBox1.renderIcon(program);
        spinBox2.renderIcon(program);
        spinBox3.renderIcon(program);
        lButton1.renderIcon(program);
        lButton2.renderIcon(program);
        
        font.drawString(program, "New Map", xPos + 45, yPos - (TITLE_BAR_HEIGHT / 3), 1, Color.WHITE);
        font.drawString(program, "Skybox:", xPos + 12, yPos + 24, 1, Color.WHITE);
        font.drawString(program, "Source:", xPos + 26, yPos + 73, 1, Color.WHITE);
        font.drawString(program, "Dimensions:", xPos + 12, yPos + 134, 1, Color.WHITE);
        font.drawString(program, "Width:", xPos + 26, yPos + 183, 1, Color.WHITE);
        font.drawString(program, "Height:", xPos + 26, yPos + 226, 1, Color.WHITE);
        font.drawString(program, "Depth:", xPos + 26, yPos + 269, 1, Color.WHITE);
        textArea.renderText(program, font);
        spinBox1.renderText(program, font);
        spinBox2.renderText(program, font);
        spinBox3.renderText(program, font);
        lButton1.renderText(program, font);
        lButton2.renderText(program, font);
    }

    @Override
    void close() {
        textArea.unfocus();
        spinBox1.unfocus();
        spinBox2.unfocus();
        spinBox3.unfocus();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "labelButtonClicked":
                switch(((ElementLabelButton) evt.getSource()).text) {
                    case "Save As...":
                        if((Boolean) evt.getNewValue()) {
                            //TODO: create map using data parsed from elements
                        }
                        break;
                        
                    case "Cancel":
                        if((Boolean) evt.getNewValue()) {
                            removeRequest = true;
                            close();
                        }
                        break;
                }
                break;
        }
    }
    
}