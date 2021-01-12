/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.camera;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class ChaseCamera2DAppState extends BaseAppState {

    private Spatial target;
    private float followInterpolationAmount = 0.2f;
    private float cameraDistanceFrustum = 10f;
    private Vector2f targetOffset = new Vector2f(0, 0);
    private Vector2f minimumClipping;
    private Vector2f maxClipping;

    private Vector3f targetPosition = new Vector3f(0, 0, 0);
    private Camera camera;

    public ChaseCamera2DAppState(Spatial target, float cameraDistanceFrustum, float followInterpolationAmount) {
        this.target = target;
        this.cameraDistanceFrustum = cameraDistanceFrustum;
        this.followInterpolationAmount = followInterpolationAmount;
    }

    @Override
    protected void initialize(Application app) {
        camera = app.getCamera();

        //First we need to remove the default fly camera
        if (app instanceof SimpleApplication) {
            SimpleApplication simpleApplication = (SimpleApplication) app;
            simpleApplication.getFlyByCamera().setEnabled(false);
            simpleApplication.getFlyByCamera().unregisterInput();
            System.out.println("PlatformerCameraState is removing default fly camera");
        }

        //Next we need to change the camera projection
        camera.setParallelProjection(true);
        float aspect = (float) camera.getWidth() / camera.getHeight();
        camera.setFrustum(-500, 500, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
        camera.setLocation(new Vector3f(0, 0, 0));

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        //Update the camera target position
        targetPosition.setZ(cameraDistanceFrustum);
        if (minimumClipping != null && maxClipping != null) {
            //Apply clipping to the camera position
            targetPosition.setX(FastMath.clamp((target.getLocalTranslation().x + targetOffset.x), minimumClipping.x, maxClipping.x));
            targetPosition.setY(FastMath.clamp((target.getLocalTranslation().y + targetOffset.y), minimumClipping.y, maxClipping.y));

        } else {
            //Do not apply clipping
            targetPosition.setX(target.getLocalTranslation().x + targetOffset.x);
            targetPosition.setY(target.getLocalTranslation().y + targetOffset.y);
        }

        //Here we need to control the camera to follow the target.
        camera.setLocation(camera.getLocation().interpolateLocal(targetPosition, followInterpolationAmount));

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public void setCameraDistanceFrustrum(float frustrum) {
        this.cameraDistanceFrustum = frustrum;
        float aspect = (float) camera.getWidth() / camera.getHeight();
        camera.setFrustum(-1000, 1000, -aspect * cameraDistanceFrustum, aspect * cameraDistanceFrustum, cameraDistanceFrustum, -cameraDistanceFrustum);
    }

    public Spatial getTarget() {
        return target;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    public float getFollowInterpolationAmount() {
        return followInterpolationAmount;
    }

    public void setFollowInterpolationAmount(float followInterpolationAmount) {
        this.followInterpolationAmount = followInterpolationAmount;
    }

    public Vector2f getTargetOffset() {
        return targetOffset;
    }

    public void setTargetOffset(Vector2f targetOffset) {
        this.targetOffset = targetOffset;
    }

    public void setCameraClipping(Vector2f minimumClipping, Vector2f maxClipping) {
        this.minimumClipping = minimumClipping;
        this.maxClipping = maxClipping;
    }

}
