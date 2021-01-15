package dev.theskidster.mapeditor.util;

import dev.theskidster.mapeditor.main.App;
import java.beans.PropertyChangeListener;

/**
 * @author J Hoffman
 * Created: Jan 15, 2021
 */

public class Timer {

    public int time;
    public int speed;
    private final int initialTime;
    
    private boolean finished;
    private boolean start;
    
    private Observable observable = new Observable(this);
    
    public Timer(int time, int speed) {
        this.time   = time;
        this.speed  = speed;
        initialTime = time;
    }
    
    public Timer(int time, int speed, PropertyChangeListener observer) {
        this.time   = time;
        this.speed  = speed;
        initialTime = time;
        
        observable.properties.put("finished", finished);
        observable.addObserver(observer);
    }
    
    public void start() { start = true; };
    
    public void stop()  { start = false; };
    
    public void reset() { time = initialTime; }
    
    public void update() {
        if(start) {
            if(time != 0) {
                if(App.tick(speed)) time--;
            } else {
                finished = true;
                observable.notifyObservers("finished", finished);
            }
        }
    }
    
    public void restart() {
        finished = false;
        start    = true;
        
        observable.notifyObservers("finished", finished);
        reset();
    }
    
}