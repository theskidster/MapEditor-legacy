package dev.theskidster.mapeditor.main;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Dec 17, 2020
 */

/**
 * Represents a camera that can be used to move throughout the {@link dev.theskidster.mapeditor.scene.Scene Scene}.
 */
final class Camera {
    
    private float pitch;
    private float yaw = -90f;
    
    double prevX;
    double prevY;
    
    private final Vector3f position;
    private final Vector3f direction;
    private final Vector3f up;
    private final Vector3f tempFront;
    private final Vector3f tempDirec;
    private final Vector3f tempRight;
    private final Vector3f tempUp;
    
    private final Matrix4f view;
    private final Matrix4f proj;
    
    Camera(float width, float height) {
        position  = new Vector3f();
        direction = new Vector3f(0, 0, -1);
        up        = new Vector3f(0, 1, 0);
        tempFront = new Vector3f();
        tempDirec = new Vector3f();
        tempRight = new Vector3f();
        tempUp    = new Vector3f();
        
        view = new Matrix4f();
        proj = new Matrix4f();
    }
    
    void update(int width, int height) {
        //TODO: control FOV with prefrences
        proj.setPerspective((float) Math.toRadians(90), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
    void render(ShaderProgram program) {
        view.setLookAt(position, position.add(direction, tempFront), up);
        
        program.setUniform("uView", false, view);
        program.setUniform("uProjection", false, proj);
    }
    
    private float getChangeIntensity(double currValue, double prevValue, float sensitivity) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    public void setDirection(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            yaw   += getChangeIntensity(xPos, prevX, 0.2f) * 2;
            pitch += getChangeIntensity(yPos, prevY, 0.2f) * 2;
            //TODO: import sensitivity from prefrences file
            
            if(pitch > 89f)  pitch = 89;
            if(pitch < -89f) pitch = -89;
            
            direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
            direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
    public void setPosition(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            float speedX = getChangeIntensity(-xPos, -prevX, 0.35f);
            float speedY = getChangeIntensity(-yPos, -prevY, 0.35f);
            //TODO: import inverted controls from prefrences file
            
            position.add(direction.cross(up, tempRight).normalize().mul(speedX));
            
            tempRight.set(
                    (float) (Math.cos(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch))), 
                    0, 
                    (float) (Math.sin(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch))));
            
            position.add(0, direction.cross(tempRight, tempUp).normalize().mul(speedY).y, 0);
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
    public void dolly(float speed) {
        position.add(direction.mul(speed * 18, tempDirec));
    }
    
}