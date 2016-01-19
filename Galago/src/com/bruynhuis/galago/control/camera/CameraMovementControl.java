/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.camera;

import com.jme3.collision.MotionAllowedListener;
import com.jme3.input.FlyByCamera;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nidebruyn
 */
public class CameraMovementControl extends AbstractControl {
    
    protected boolean left, right, up, down;
    protected boolean strafeLeft, strafeRight, forward, backward;
    protected boolean invert;

    protected Camera cam;
    protected Vector3f initialUpVec;
    protected float rotationSpeed = 1f;
    protected float lookSpeed = 0.1f;
    protected float moveSpeed = 3f;
    protected float strafeSpeed = 1f;
    protected float zoomSpeed = 1f;
    protected MotionAllowedListener motionAllowed = null;
    protected boolean dragToRotate = false;
    protected boolean canRotate = false;
    protected boolean invertY = false;

    public CameraMovementControl(Camera cam){
        this.cam = cam;
        initialUpVec = cam.getUp().clone();
    }
    
    /**
     * Sets the up vector that should be used for the camera.
     * @param upVec
     */
    public void setUpVector(Vector3f upVec) {
       initialUpVec.set(upVec);
    }

    public void setMotionAllowedListener(MotionAllowedListener listener){
        this.motionAllowed = listener;
    }

    /**
     * Sets the move speed. The speed is given in world units per second.
     * @param moveSpeed
     */
    public void setMoveSpeed(float moveSpeed){
        this.moveSpeed = moveSpeed;
    }
    
    /**
     * Gets the move speed. The speed is given in world units per second.
     * @return moveSpeed
     */
    public float getMoveSpeed(){
        return moveSpeed;
    }

    /**
     * Sets the rotation speed.
     * @param rotationSpeed
     */
    public void setRotationSpeed(float rotationSpeed){
        this.rotationSpeed = rotationSpeed;
    }
    
    /**
     * Gets the move speed. The speed is given in world units per second.
     * @return rotationSpeed
     */
    public float getRotationSpeed(){
        return rotationSpeed;
    }
    
    /**
     * Sets the zoom speed.
     * @param zoomSpeed 
     */
    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }
    
    /**
     * Gets the zoom speed.  The speed is a multiplier to increase/decrease
     * the zoom rate.
     * @return zoomSpeed
     */
    public float getZoomSpeed() {
        return zoomSpeed;
    }

    public float getStrafeSpeed() {
        return strafeSpeed;
    }

    public void setStrafeSpeed(float strafeSpeed) {
        this.strafeSpeed = strafeSpeed;
    }

    /**
     * @return If drag to rotate feature is enabled.
     *
     * @see FlyByCamera#setDragToRotate(boolean) 
     */
    public boolean isDragToRotate() {
        return dragToRotate;
    }

    /**
     * Set if drag to rotate mode is enabled.
     * 
     * When true, the user must hold the mouse button
     * and drag over the screen to rotate the camera, and the cursor is
     * visible until dragged. Otherwise, the cursor is invisible at all times
     * and holding the mouse button is not needed to rotate the camera.
     * This feature is disabled by default.
     * 
     * @param dragToRotate True if drag to rotate mode is enabled.
     */
    public void setDragToRotate(boolean dragToRotate) {
        this.dragToRotate = dragToRotate;

    }

    public float getLookSpeed() {
        return lookSpeed;
    }

    public void setLookSpeed(float lookSpeed) {
        this.lookSpeed = lookSpeed;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isStrafeLeft() {
        return strafeLeft;
    }

    public void setStrafeLeft(boolean strafeLeft) {
        this.strafeLeft = strafeLeft;
    }

    public boolean isStrafeRight() {
        return strafeRight;
    }

    public void setStrafeRight(boolean strafeRight) {
        this.strafeRight = strafeRight;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    public boolean isInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public MotionAllowedListener getMotionAllowed() {
        return motionAllowed;
    }

    public void setMotionAllowed(MotionAllowedListener motionAllowed) {
        this.motionAllowed = motionAllowed;
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    public boolean isInvertY() {
        return invertY;
    }

    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
    }

    protected void rotateCamera(float value, Vector3f axis){
        if (dragToRotate){
            if (canRotate){
//                value = -value;
            }else{
                return;
            }
        }

        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
    }

    protected void zoomCamera(float value){
        // derive fovY value
        float h = cam.getFrustumTop();
        float w = cam.getFrustumRight();
        float aspect = w / h;

        float near = cam.getFrustumNear();

        float fovY = FastMath.atan(h / near)
                  / (FastMath.DEG_TO_RAD * .5f);
        float newFovY = fovY + value * 0.1f * zoomSpeed;
        if (newFovY > 0f) {
            // Don't let the FOV go zero or negative.
            fovY = newFovY;
        }

        h = FastMath.tan( fovY * FastMath.DEG_TO_RAD * .5f) * near;
        w = h * aspect;

        cam.setFrustumTop(h);
        cam.setFrustumBottom(-h);
        cam.setFrustumLeft(-w);
        cam.setFrustumRight(w);
    }

    protected void riseCamera(float value){
        Vector3f vel = new Vector3f(0, value * moveSpeed, 0);
        Vector3f pos = cam.getLocation().clone();

        if (motionAllowed != null)
            motionAllowed.checkMotionAllowed(pos, vel);
        else
            pos.addLocal(vel);

        cam.setLocation(pos);
    }

    protected void moveCamera(float value, boolean sideways){
        Vector3f vel = new Vector3f();
        Vector3f pos = cam.getLocation().clone();

        if (sideways){
            cam.getLeft(vel);
        }else{
            cam.getDirection(vel);
        }
        vel.multLocal(value);

        if (motionAllowed != null)
            motionAllowed.checkMotionAllowed(pos, vel);
        else
            pos.addLocal(vel);

        cam.setLocation(pos);
    }
    
    @Override
    protected void controlUpdate(float tpf) {

        if (left){
            rotateCamera(rotationSpeed * tpf, initialUpVec);
        }
        if (right){
            rotateCamera(-rotationSpeed * tpf, initialUpVec);
        }
        if (up){
            rotateCamera(-tpf * lookSpeed * (invertY ? -1 : 1), cam.getLeft());
        }
        if (down){
            rotateCamera(tpf * lookSpeed * (invertY ? -1 : 1), cam.getLeft());
        }
        
        if (forward){
            moveCamera(tpf * moveSpeed, false);
        }
        if (backward){
            moveCamera(-tpf * moveSpeed, false);
        }
        if (strafeLeft){
            moveCamera(tpf * strafeSpeed, true);
        }
        if (strafeRight){
            moveCamera(-tpf * strafeSpeed, true);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    public void clearMovement() {
        forward = false;
        backward = false;
        strafeLeft = false;
        strafeRight = false;
    }
    
    public void clearDirection() {
        up = false;
        down = false;
        left = false;
        right = false;
    }

}
