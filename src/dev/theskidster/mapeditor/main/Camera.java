package dev.theskidster.mapeditor.main;

import org.joml.FrustumRayBuilder;
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
    final Vector3f ray       = new Vector3f();
    
    private final Vector3f tempVec1 = new Vector3f();
    private final Vector3f tempVec2 = new Vector3f();
    private final Vector4f tempVec3 = new Vector4f();
    
    private final Matrix4f view    = new Matrix4f();
    private final Matrix4f proj    = new Matrix4f();
    private final Matrix4f tempMat = new Matrix4f();
    
    void update(int width, int height) {
        //TODO: control FOV with prefrences
        proj.setPerspective((float) Math.toRadians(90), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
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
            yaw   += getChangeIntensity(xPos, prevX, 0.25f) * 2;
            pitch += getChangeIntensity(yPos, prevY, 0.25f) * 2;
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
            float speedX = getChangeIntensity(-xPos, -prevX, 0.38f);
            float speedY = getChangeIntensity(-yPos, -prevY, 0.38f);
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
        position.add(direction.mul(speed * 18, tempVec1));
    }
    
    private FrustumRayBuilder rb = new FrustumRayBuilder();
    
    public void castRay(float x, float y) {
        
        tempVec3.set(x, y, -1f, 1f);
            
        proj.invert(tempMat);
        tempVec3.set(tempVec3.mul(tempMat).x, tempVec3.mul(tempMat).y, -1f, 0);
        view.invert(tempMat);

        ray.set(tempVec3.mul(tempMat).x, tempVec3.mul(tempMat).y, tempVec3.mul(tempMat).z);
        ray.normalize();
        
        /*
        tempMat.set(view);
        tempVec1.set(position);
        
        rb.set(tempMat);
        rb.origin(tempVec1);
        rb.dir(x, y, ray);*/
    }
    
}