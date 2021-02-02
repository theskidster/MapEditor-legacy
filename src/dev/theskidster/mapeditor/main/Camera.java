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
    
    double prevX;
    double prevY;
    
    final Vector3f position  = new Vector3f();
    final Vector3f direction = new Vector3f(0, 0, -1);
    final Vector3f up        = new Vector3f(0, 1, 0);
    Vector3f ray       = new Vector3f();
    
    private final Vector3f tempVec1 = new Vector3f();
    private final Vector3f tempVec2 = new Vector3f();
    
    private final Matrix4f view    = new Matrix4f();
    private final Matrix4f proj    = new Matrix4f();
    private final Matrix4f tempMat = new Matrix4f();
    
    void update(int width, int height) {
        //TODO: control FOV with prefrences
        proj.setPerspective((float) Math.toRadians(65), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
    void render(ShaderProgram program) {
        view.setLookAt(position, position.add(direction, tempVec1), up);
        
        program.setUniform("uView", false, view);
        program.setUniform("uProjection", false, proj);
    }
    
    private float getChangeIntensity(double currValue, double prevValue, float sensitivity) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    public void setDirection(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            yaw   += getChangeIntensity(xPos, prevX, 0.35f);
            pitch += getChangeIntensity(yPos, prevY, 0.35f);
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
    
    public void dolly(float speed) {
        position.add(direction.mul(speed, tempVec1));
    }
    
    public void castRay(float x, float y) {
        Vector4f eyeCoords = toEyeCoords(new Vector4f(x, y, -1f, 1f));
        ray = toWorldCoords(eyeCoords);
    }
    
    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Vector4f eyeCoords = new Vector4f();
        
        proj.invert(tempMat);
        tempMat.transform(clipCoords, eyeCoords);
        
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }
    
    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Vector4f worldCoords = new Vector4f();
        
        view.invert(tempMat);
        tempMat.transform(eyeCoords, worldCoords);
        
        Vector3f result = new Vector3f(worldCoords.x, worldCoords.y, worldCoords.z);
        
        return result.normalize();
    }
    
}