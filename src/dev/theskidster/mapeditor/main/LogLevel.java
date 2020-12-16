package dev.theskidster.mapeditor.main;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

/**
 * Values contained in this enum are used to indicate the importance of messages 
 * displayed by the {@link Logger}.
 */
public enum LogLevel {
    /**
     * The application has made some significant change to its state 
     * successfully. 
     */
    INFO,
    
    /**
     * The application may have entered an invalid state, but has not crashed.
     */
    WARNING,
    
    /**
     * The application has encountered a fatal issue that will require it to 
     * cease execution.
     */
    SEVERE
}