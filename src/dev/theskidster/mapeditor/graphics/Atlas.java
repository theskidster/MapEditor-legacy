package dev.theskidster.mapeditor.graphics;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 10, 2021
 */

public class Atlas {
    
    public final int cellWidth;
    public final int cellHeight;
    public final int rows;
    public final int columns;
    public final int imgCount;
    
    public final float imgWidth;
    public final float imgHeight;
    
    public Vector2f texCoords = new Vector2f();
    
    public Map<Vector2i, Vector2f> imgOffsets = new HashMap<>();
    
    public Atlas(Texture texture, int cellWidth, int cellHeight) {
        this.cellWidth  = cellWidth;
        this.cellHeight = cellHeight;
        
        imgWidth  = (float) cellWidth / texture.getWidth();
        imgHeight = (float) cellHeight / texture.getHeight();
        rows      = texture.getWidth() / cellWidth;
        columns   = texture.getHeight() / cellHeight;
        imgCount  = rows * columns;
        
        for(int x = 0; x < rows; x++) {
            for(int y = 0; y < columns; y++) {
                imgOffsets.put(new Vector2i(x, y), new Vector2f(imgWidth * x, imgHeight * y));
            }
        }
    }
    
}