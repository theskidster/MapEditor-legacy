package dev.theskidster.mapeditor.main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkDrawNullTexture;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkUserFont;
import org.lwjgl.nuklear.NkVec2;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

final class Window {

    int width;
    int height;
    
    int viewWidth;
    int viewHeight;
    
    private int vao;
    private int vbo;
    private int ibo;
    private int program;
    private int vertHandle;
    private int fragHandle;
    private int uniformTex;
    private int uniformProj;
    
    final long handle;
    
    String title;
    Vector2i position;
    
    private NkContext nkContext;
    private static final NkAllocator NK_ALLOC;
    private NkBuffer commandBuf;
    private NkUserFont nkFont;
    private NkDrawNullTexture nkNullTex;
    
    static {
        NK_ALLOC = NkAllocator.create()
                .alloc((hdl, old, size) -> nmemAllocChecked(size))
                .mfree((hdl, ptr) -> nmemFree(ptr));
    }
    
    Window(String title, Monitor monitor) {
        //TODO: pull these values in from prefrences file
        width  = 1480;
        height = 960;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xStartBuf = stack.mallocInt(1);
            IntBuffer yStartBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xStartBuf, yStartBuf);
            
            position = new Vector2i(
                    Math.round((monitor.width - width) / 2) + xStartBuf.get(), 
                    Math.round((monitor.height - height) / 2) + yStartBuf.get());
        }
        
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
    }
    
    /**
     * Sets the icon image of the window. Images should be at least 32x32 pixels large, but no larger than 64x64.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    private void setWindowIcon(String filename) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = App.class.getResourceAsStream("/dev/theskidster/mapeditor/assets/" + filename);
            byte[] data      = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(data.length).put(data).flip(),
                    widthBuf,
                    heightBuf,
                    channelBuf,
                    STBI_rgb_alpha);
            
            glfwSetWindowIcon(handle, GLFWImage.mallocStack(1, stack)
                    .width(widthBuf.get())
                    .height(heightBuf.get())
                    .pixels(icon));
            
            stbi_image_free(icon);
            
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to set window icon: \"" + filename + "\"");
        }
    }
    
    private NkContext setCallbacks() {
        glfwSetWindowSizeCallback(handle, (window, w, h) -> {
            System.out.println(w + " " + h);
        });
        
        glfwSetScrollCallback(handle, (window, xOffset, yOffset) -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                NkVec2 scroll = NkVec2.mallocStack(stack)
                        .x((float) xOffset)
                        .y((float) yOffset);
                
                nk_input_scroll(nkContext, scroll);
            }
        });
        
        glfwSetCharCallback(handle, (window, codepoint) -> nk_input_unicode(nkContext, codepoint));
        
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            boolean press = action == GLFW_PRESS;
            
            switch(key) {                    
                case GLFW_KEY_DELETE:
                    nk_input_key(nkContext, NK_KEY_DEL, press);
                    break;
                    
                case GLFW_KEY_ENTER:
                    nk_input_key(nkContext, NK_KEY_ENTER, press);
                    break;
                    
                case GLFW_KEY_TAB:
                    nk_input_key(nkContext, NK_KEY_TAB, press);
                    break;
                    
                case GLFW_KEY_BACKSPACE:
                    nk_input_key(nkContext, NK_KEY_BACKSPACE, press);
                    break;
                    
                case GLFW_KEY_UP:
                    nk_input_key(nkContext, NK_KEY_UP, press);
                    break;
                    
                case GLFW_KEY_DOWN:
                    nk_input_key(nkContext, NK_KEY_DOWN, press);
                    break;
                    
                case GLFW_KEY_LEFT_SHIFT:
                case GLFW_KEY_RIGHT_SHIFT:
                    nk_input_key(nkContext, NK_KEY_SHIFT, press);
                    break;
                    
                case GLFW_KEY_LEFT_CONTROL:
                case GLFW_KEY_RIGHT_CONTROL:
                    if(press) {
                        nk_input_key(nkContext, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                    } else {
                        nk_input_key(nkContext, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_COPY, false);
                        nk_input_key(nkContext, NK_KEY_PASTE, false);
                        nk_input_key(nkContext, NK_KEY_CUT, false);
                        nk_input_key(nkContext, NK_KEY_SHIFT, false);
                    }
                    break;
            }
        });
        
        glfwSetCursorPosCallback(handle, (window, xPos, yPos) -> nk_input_motion(nkContext, (int) xPos, (int) yPos));
        
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                DoubleBuffer xPosBuf = stack.mallocDouble(1);
                DoubleBuffer yPosBuf = stack.mallocDouble(1);
                
                glfwGetCursorPos(window, xPosBuf, yPosBuf);
                
                int nkButton;
                
                switch(button) {
                    case GLFW_MOUSE_BUTTON_RIGHT:  nkButton = NK_BUTTON_RIGHT;  break;
                    case GLFW_MOUSE_BUTTON_MIDDLE: nkButton = NK_BUTTON_MIDDLE; break;
                    default:                       nkButton = NK_BUTTON_LEFT;
                }
                
                nk_input_button(nkContext, nkButton, (int) xPosBuf.get(0), (int) yPosBuf.get(0), action == GLFW_PRESS);
            }
        });
        
        nk_init(nkContext, NK_ALLOC, null);
        
        nkContext.clip()
                .copy((hdl, text, len) -> {
                    if(len == 0) return;
                    
                    try(MemoryStack stack = MemoryStack.stackPush()) {
                        ByteBuffer stringBuf = stack.malloc(len + 1);
                        MemoryUtil.memCopy(text, MemoryUtil.memAddress(stringBuf), len);;
                        stringBuf.put(len, (byte) 0);
                        
                        glfwSetClipboardString(handle, stringBuf);
                    }
                })
                .paste((hdl, edit) -> {
                    long text = nglfwGetClipboardString(handle);
                    if(text != NULL) nnk_textedit_paste(edit, text, nnk_strlen(text));
                });
        
        String NK_SHADER_VERSION = "#version 300 es\n";
        
        String vertCode = 
                NK_SHADER_VERSION + 
                "uniform mat4 ProjMtx;\n" +
                "in vec2 Position;\n" +
                "in vec2 TexCoord;\n" +
                "in vec4 Color;\n" +
                "out vec2 Frag_UV;\n" +
                "out vec4 Frag_Color;\n" +
                "void main() {\n" +
                "   Frag_UV = TexCoord;\n" +
                "   Frag_Color = Color;\n" +
                "   gl_Position = ProjMtx * vec4(Position.xy, 0, 1);\n" +
                "}\n";
        
        String fragCode = 
                NK_SHADER_VERSION +
                "precision mediump float;\n" +
                "uniform sampler2D Texture;\n" +
                "in vec2 Frag_UV;\n" +
                "in vec4 Frag_Color;\n" +
                "out vec4 Out_Color;\n" +
                "void main(){\n" +
                "   Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n" +
                "}\n";
        
        commandBuf = NkBuffer.create();
        nk_buffer_init(commandBuf, NK_ALLOC, Float.BYTES * 1024);
        
        program    = glCreateProgram();
        vertHandle = glCreateShader(GL_VERTEX_SHADER);
        fragHandle = glCreateShader(GL_FRAGMENT_SHADER);
        
        glShaderSource(vertHandle, vertCode);
        glShaderSource(fragHandle, fragCode);
        glCompileShader(vertHandle);
        glCompileShader(fragHandle);
        
        App.checkShaderError(vertHandle, "internal vertex shader");
        App.checkShaderError(fragHandle, "internal fragment shader");
        
        glAttachShader(program, vertHandle);
        glAttachShader(program, fragHandle);
        glLinkProgram(program);
        
        if(glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
            Logger.log(LogLevel.SEVERE, "Failed to link interal shader program");
        }
        
        uniformTex    = glGetUniformLocation(program, "Texture");
        uniformProj   = glGetUniformLocation(program, "ProjMtx");
        int attribPos = glGetAttribLocation(program, "Position");
        int attribUV  = glGetAttribLocation(program, "TexCoord");
        int attribCol = glGetAttribLocation(program, "Color");
        
        {
            vao = glGenVertexArrays();
            vbo = glGenBuffers();
            ibo = glGenBuffers();

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

            glEnableVertexAttribArray(attribPos);
            glEnableVertexAttribArray(attribUV);
            glEnableVertexAttribArray(attribCol);

            glVertexAttribPointer(attribPos, 2, GL_FLOAT, false, 20, 0);
            glVertexAttribPointer(attribUV, 2, GL_FLOAT, false, 20, 8);
            glVertexAttribPointer(attribCol, 4, GL_UNSIGNED_BYTE, true, 20, 16);
        }
        
        {
            int nullTexID = glGenTextures();

            nkNullTex = NkDrawNullTexture.create();
            
            nkNullTex.texture().id(nullTexID);
            nkNullTex.uv().set(0.5f, 0.5f);

            glBindTexture(GL_TEXTURE_2D, nullTexID);
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
            }
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        }
        
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        
        return nkContext;
    }
    
    /**
     * Displays the window and establishes its input callback events.
     * 
     * @param monitor the monitor to display this window on
     */
    void show(Monitor monitor) {
        setWindowIcon("img_logo.png");
        
        glfwSetWindowMonitor(handle, NULL, position.x, position.y, width, height, monitor.refreshRate);
        glfwSetWindowPos(handle, position.x, position.y);
        glfwSwapInterval(1);
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwShowWindow(handle);
        
        nkContext = NkContext.create();
        nkContext = setCallbacks();
    }
    
    void pollInput() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuf  = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            
            glfwGetWindowSize(handle, widthBuf, heightBuf);
            
            width  = widthBuf.get(0);
            height = heightBuf.get(0);
            
            glfwGetFramebufferSize(handle, widthBuf, heightBuf);
            
            viewWidth  = widthBuf.get(0);
            viewHeight = heightBuf.get(0);
        }
        
        nk_input_begin(nkContext);
        glfwPollEvents();
        
        NkMouse mouse = nkContext.input().mouse();
        
        if(mouse.grab()) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else if(mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            
            glfwSetCursorPos(handle, prevX, prevY);
            
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
        } else if(mouse.ungrab()) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        
        nk_input_end(nkContext);
    }
    
    public void textTest() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            
            //TODO: investigate nullpointer
            if(nk_begin(nkContext, title, nk_rect(50, 50, 300, 200, rect), NK_WINDOW_TITLE | NK_WINDOW_BORDER | NK_WINDOW_MINIMIZABLE)) {
                float rowHeight = 50;
                int itemsPerRow = 1;
                
                nk_layout_row_dynamic(nkContext, rowHeight, itemsPerRow);
            }
            
            nk_end(nkContext);
        }
    }
    
    public void useProgram() {
        glUseProgram(program);
    }
    
}
