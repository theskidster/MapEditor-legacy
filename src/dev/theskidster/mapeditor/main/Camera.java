package dev.theskidster.mapeditor.main;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author J Hoffman
 * Created: Dec 17, 2020
 */

/**
 * Represents a camera that can be used to move throughout the {@link dev.theskidster.mapeditor.scene.Scene Scene}.
 */
public final class Camera {
    
    private float pitch;
    private float yaw = -90f;
    float fov         = 60f;
    
    double prevX;
    double prevY;
    
    final Vector3f position  = new Vector3f();
    final Vector3f direction = new Vector3f(0, 0, -1);
    final Vector3f up        = new Vector3f(0, 1, 0);
    Vector3f ray             = new Vector3f();
    
    private final Vector3f tempVec1 = new Vector3f();
    private final Vector3f tempVec2 = new Vector3f();
    private final Vector4f tempVec3 = new Vector4f();
    
    private final Matrix4f view    = new Matrix4f();
    private final Matrix4f proj    = new Matrix4f();
    private final Matrix4f tempMat = new Matrix4f();
    
    void update(int width, int height) {
        //TODO: control FOV with prefrences
        proj.setPerspective((float) Math.toRadians(fov), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
    void render(ShaderProgram program) {
        view.setLookAt(position, position.add(direction, tempVec1), up);
        
        program.setUniform("uView", false, view);
        program.setUniform("uProjection", false, proj);
    }
    
    private float getChangeIntensity(double currValue, double prevValue, float sensitivity) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    public void setPosition(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            float speedX = getChangeIntensity(-xPos, -prevX, 0.017f);
            float speedY = getChangeIntensity(-yPos, -prevY, 0.017f);
            //TODO: import inverted controls from prefrences file
            
            position.add(direction.cross(up, tempVec1).normalize().mul(speedX));
            
            tempVec1.set(
                    (float) (Math.cos(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch))), 
                    0, 
                    (float) (Math.sin(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch))));
            
            position.add(0, direction.cross(tempVec1, tempVec2).normalize().mul(speedY).y, 0);
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
    public void setDirection(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            yaw   += getChangeIntensity(xPos, prevX, 0.35f);
            pitch += getChangeIntensity(yPos, prevY, 0.35f);
            //TODO: import sensitivity from prefrences file
            
            if(pitch > 89f)  pitch = 89f;
            if(pitch < -89f) pitch = -89f;
            
            direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
            direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
    public void dolly(float speed) {
        position.add(direction.mul(speed, tempVec1));
    }
    
    public void castRay(float x, float y) {
        tempVec3.set(x, y, -1f, 1f);
        
        proj.invert(tempMat);
        tempMat.transform(tempVec3);
        
        tempVec3.z = -1f;
        tempVec3.w = 0;
        
        view.invert(tempMat);
        tempMat.transform(tempVec3);
        
        ray.set(tempVec3.x, tempVec3.y, tempVec3.z);
    }
    
}