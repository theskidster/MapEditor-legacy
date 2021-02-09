package dev.theskidster.mapeditor.graphics;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 8, 2021
 */

public class LightSource {
    
    public boolean enabled = true;
    
    private Light light;
    private Graphics g;
    private Texture texture;
    private Atlas atlas;
    
    public LightSource(Light light) {
        this.light = light;
        
        g       = new Graphics();
        texture = new Texture("spr_icons.png");
        atlas   = new Atlas(texture, 30, 30);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            float size = 0.5f;
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-size).put(-size).put(0) .put(atlas.imgWidth * 3).put(atlas.imgHeight * 3);
            g.vertices.put( size).put(-size).put(0) .put(atlas.imgWidth * 4).put(atlas.imgHeight * 3);
            g.vertices.put( size).put( size).put(0) .put(atlas.imgWidth * 4).put(atlas.imgHeight * 2);
            g.vertices.put(-size).put( size).put(0) .put(atlas.imgWidth * 3).put(atlas.imgHeight * 2);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    public void update() {
        g.modelMatrix.translation(light.position);
    }
    
    public void render(ShaderProgram program, Vector3f camPos, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(light.position, camPos, camUp);
        
        glBindVertexArray(g.vao);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        program.setUniform("uType", 3);
        program.setUniform("uModel", false, g.modelMatrix);
        program.setUniform("uColor", light.ambient);

        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);

        App.checkGLError();
    }
    
    public float getBrightness()      { return light.brightness; }
    public float getContrast()        { return light.contrast; }
    public Vector3f getPosition()     { return light.position; }
    public Vector3f getAmbientColor() { return light.ambient; }
    public Vector3f getDiffuseColor() { return light.diffuse; }
    
    public void setBrightness(float brightness) {
        light.brightness = brightness;
    }
    
    public void setContrast(float contrast) {
        light.contrast = contrast;
    }
    
    public void setPosition(float x, float y, float z) {
        light.position.set(x, y, z);
    }
    
    public void setAmbientColor(Color color) {
        light.ambientColor = color;
        light.ambient      = Color.convert(color);
    }
    
    public void setDiffuseColor(Color color) {
        light.diffuseColor = color;
        light.diffuse      = Color.convert(color);
    }
    
}