package dev.theskidster.mapeditor.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Dec 17, 2020
 */

public class Graphics {

    public final int vao = glGenVertexArrays();
    public final int vbo = glGenBuffers();
    public final int ibo = glGenBuffers();
    
    public FloatBuffer vertices;
    public IntBuffer indices;
    
    public Matrix4f modelMatrix = new Matrix4f();
    
}