package dev.theskidster.mapeditor.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

/**
 * Keeps a chronological record of significant events occurring within the application and writes the output to a text file.
 */
public final class Logger {
    
    private static Exception ex;
    private static PrintWriter writer;
    
    static {
        try {
            FileWriter logFile = new FileWriter("log.txt");
            writer = new PrintWriter(logFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the file stream used by the static class and releases any resources it may have allocated.
     */
    static void close() {
        writer.close();
    }
    
    /**
     * Outputs information about the system that the application is currently running on.
     */
    static void printSystemInfo() {
        log(LogLevel.INFO, "--------------------------------------------------------------------------------");
        log(LogLevel.INFO, "OS NAME:\t\t" + System.getProperty("os.name"));
        log(LogLevel.INFO, "JAVA VERSION:\t" + System.getProperty("java.version"));
        log(LogLevel.INFO, "GLFW VERSION:\t" + glfwGetVersionString());
        log(LogLevel.INFO, "OPENGL VERSION:\t" + glGetString(GL_VERSION));
        log(LogLevel.INFO, "APP VERSION:\t" + App.VERSION);
        log(LogLevel.INFO, "--------------------------------------------------------------------------------");
        
        System.out.println();
        writer.println();
    }
    
    /**
     * Supplies the logger with a stack trace that can be used by subsequent calls to the {@linkplain log(LogLevel, String) log()} method provided its 
     * {@linkplain LogLevel level} is either {@link LogLevel#WARNING WARNING} or {@link LogLevel#SEVERE SEVERE}. Ideally this should be used in conjunction 
     * with a try-catch statement.
     * 
     * @param e the exception we want to output
     */
    public static void setStackTrace(Exception e) {
        ex = e;
    }
    
    /**
     * Writes a new message to the log file that will be generated after the application ceases execution.
     * 
     * @param level the precedence of the message being displayed
     * @param desc  the message that will be written to the log file
     */
    public static void log(LogLevel level, String desc) {
        String message;
        String timestamp = new SimpleDateFormat("MM-dd-yyyy h:mma").format(new Date());
        
        switch(level) {
            case INFO -> {
                message = "INFO: " + desc;
                System.out.println(message);
                writer.println(message);
            }
                
            case WARNING -> {
                message = "WARNING: " + desc;
                System.out.println(System.lineSeparator() + timestamp);
                System.out.println(message + System.lineSeparator());
                writer.println();
                writer.println(timestamp);
                writer.println(message);
                writer.println();
                if(ex != null) {
                    var stackTrace = ex.getStackTrace();
                    
                    System.out.println(ex.toString());
                    for(StackTraceElement element : stackTrace) System.out.println("\t" + element.toString());
                    System.out.println();
                    
                    ex.printStackTrace(writer);
                    writer.println();
                    ex = null;
                }
            }
                
            case SEVERE -> {
                message = "ERROR: " + desc;
                System.err.println(System.lineSeparator() + timestamp);
                System.err.println(message + System.lineSeparator());
                writer.println();
                writer.println(timestamp);
                writer.println(message);
                writer.println();
                if(ex != null) {
                    ex.printStackTrace(writer);
                } else {
                    setStackTrace(new RuntimeException());
                    ex.printStackTrace(writer);
                }
                writer.close();
                throw new RuntimeException();
            }
        }
    }
    
}
