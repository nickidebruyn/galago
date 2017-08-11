/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.app;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.control.tween.Rigidbody2DAccessor;
import com.bruynhuis.galago.sprite.physics.Dyn4jAppState;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.ThreadingType;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;

/**
 * If you plan to create a 2D game you must extends this class.
 * Together with this class you need to tell the game if you intend to use physics.
 * The physics engine used will be Dyn4J.
 * 
 * @author nidebruyn
 */
public abstract class Base2DApplication extends BaseApplication implements PhysicsTickListener {
    
    protected Dyn4jAppState dyn4jAppState;
    protected float frustumSize = 10f;

    public Base2DApplication(String title, float width, float height, String gameSaveFileName, String gameFont, String splashImage, boolean resizable, float widthSample, float heightSample) {
        super(title, width, height, gameSaveFileName, gameFont, splashImage, resizable, widthSample, heightSample);
    }

    public Base2DApplication(String title, float width, float height, String gameSaveFileName, String gameFont, String splashImage, boolean resizable) {
        super(title, width, height, gameSaveFileName, gameFont, splashImage, resizable);
    }
    
    @Override
    public void simpleInitApp() {
        Tween.registerAccessor(com.bruynhuis.galago.sprite.physics.RigidBodyControl.class, new Rigidbody2DAccessor());
        super.simpleInitApp(); 
    } 
    
    @Override
    protected void initPhysics() {        
        //Don't load if it already exist
        if (dyn4jAppState != null) {
            return;
        }
        /**
         * Set up Physics
         */
        dyn4jAppState = new Dyn4jAppState(ThreadingType.SEQUENTIAL);
        stateManager.attach(dyn4jAppState);
        dyn4jAppState.getPhysicsSpace().setGravity(0, -20.0f);
        dyn4jAppState.getPhysicsSpace().addPhysicsTickListener(this);
        
    }
    
    /**
     * Can be called from the code to get the physicsSpace.
     * @return Dyn4jAppState
     */
    public Dyn4jAppState getDyn4jAppState() {
        return dyn4jAppState;
    }

    @Override
    public void showDebuging() {

    }

    @Override
    protected void initCamera() {
        super.initCamera();
        cam.setParallelProjection(true);
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustum(-500, 500, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        cam.setLocation(new Vector3f(0, 0, 0));
    }
    
    public void setCameraDistanceFrustrum(float frustrum) {
        this.frustumSize = frustrum;
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
    }
    
    public float getDepthScale() {
        float aspect = 0.25f;
        return frustumSize/100f*aspect;
    }
    
    
    public void fixFlatTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        getTweenManagerPhysics().update(tpf);
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
    }
}
