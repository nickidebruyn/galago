package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author ndebruyn
 */
public class ParticleEditor extends AbstractScreen {

    public static final String NAME = "ParticleEditor";
    
    private ChaseCamera chaseCamera;

    @Override
    protected void init() {
    }

    @Override
    protected void load() {
        loadDefaultParticles();
        loadChaseCamera(10);
    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {
    }

    private void loadDefaultParticles() {
        /**
         * Uses Texture from jme3-test-data library!
         */
        ParticleEmitter fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        //fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setImagesX(1);
        fireEffect.setImagesY(1); // 2x2 texture animation        
        fireEffect.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fireEffect.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fireEffect.setStartSize(0.6f);
        fireEffect.setEndSize(0.1f);
        fireEffect.setGravity(0f, 0f, 0f);
        fireEffect.setLowLife(0.5f);
        fireEffect.setHighLife(3f);
        fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        rootNode.attachChild(fireEffect);
        fireEffect.emitAllParticles();

    }

    private void loadChaseCamera(float cameraDistance) {
        //Load the camera
        chaseCamera = new ChaseCamera(camera, rootNode, inputManager);
        chaseCamera.setDefaultDistance(cameraDistance);
        chaseCamera.setChasingSensitivity(60);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setTrailingEnabled(false);

        chaseCamera.setDefaultHorizontalRotation(90 * FastMath.DEG_TO_RAD);
        chaseCamera.setDefaultVerticalRotation(40 * FastMath.DEG_TO_RAD);

        chaseCamera.setMinVerticalRotation(2 * FastMath.DEG_TO_RAD);
        chaseCamera.setMaxVerticalRotation(60 * FastMath.DEG_TO_RAD);

//            chaseCamera.setMinVerticalRotation(0 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMaxVerticalRotation(0 * FastMath.DEG_TO_RAD);
        chaseCamera.setLookAtOffset(new Vector3f(0, 0f, 0));

        chaseCamera.setHideCursorOnRotate(false);
//            chaseCamera.setRotationSpeed(5);
        chaseCamera.setRotationSpeed(10);
        chaseCamera.setMinDistance(cameraDistance / 2f);
        chaseCamera.setMaxDistance(cameraDistance * 2f);

        chaseCamera.setDragToRotate(true);
        chaseCamera.setRotationSensitivity(5);
        chaseCamera.setEnabled(false);

    }
}
