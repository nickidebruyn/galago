/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.resource;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.Map;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.SpatialLifeControl;
import com.bruynhuis.galago.sprite.AnimatedSprite;

/**
 * Is used for handling particle effects.
 *
 * @author nidebruyn
 */
public class EffectManager {
    
    private BaseApplication application;
    private Map<String, Spatial> effects = new HashMap<String, Spatial>();
    
    public EffectManager(BaseApplication baseApplication) {
        this.application = baseApplication;
    }
    
    public void destroy() {
        effects.clear();
    }
    
    /**
     * Load an effect using a path to a j3o model.
     * @param effect
     * @param effectPath 
     */
    public void loadEffect(String effect, String effectPath) {
        Spatial spatial = application.getAssetManager().loadModel(effectPath);
        loadEffect(effect, spatial);
    }
    
    /**
     * Load an effect using a spatial
     * @param effect
     * @param effectSpatial 
     */
    public void loadEffect(String effect, Spatial effectSpatial) {
        effects.put(effect, effectSpatial);        
        preloadParticles(effectSpatial);
    }
    
    /**
     * Should be called when you want to show a particle effect.
     * @param effect
     * @param position 
     */
    public void doEffect(String effect, Vector3f position) {
        doEffect(effect, position, 200);        
    }
    
    /**
     * Should be called when you want to show a particle effect.
     * @param effect
     * @param position 
     */
    public void doEffect(String effect, Vector3f position, float timeInMiliSec) {
        Spatial effectSpatial = (Spatial) effects.get(effect);
        doEffect(effectSpatial, position, timeInMiliSec);        
    }
    
    /**
     * This will preload the particels
     * @param spatial 
     */
    protected void preloadParticles(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            for (int i = 0; i < ((Node) spatial).getQuantity(); i++) {
                Spatial s = ((Node) spatial).getChild(i);
                if (s instanceof ParticleEmitter) {
                    ParticleEmitter emitter = (ParticleEmitter) s;
                    emitter.preload(application.getRenderManager(), application.getViewPort());
                }
            }
        }
    }

    /**
     * This will go through a spatial object and emit All particles that might
     * be located.
     *
     * @param spatial
     */
    protected static void doParticleRespawn(Spatial spatial) {
        if (spatial != null && spatial instanceof AnimatedSprite) {
            AnimatedSprite sprite = (AnimatedSprite) spatial;
            sprite.play();
            
        } else if (spatial != null && spatial instanceof Node) {
            for (int i = 0; i < ((Node) spatial).getQuantity(); i++) {
                Spatial s = ((Node) spatial).getChild(i);
                if (s instanceof ParticleEmitter) {
                    ParticleEmitter emitter = (ParticleEmitter) s;
                    emitter.emitAllParticles();
                }
            }
        }
    }

    /**
     * Helper method to prevent dupplication
     *
     * @param spatial
     * @param position
     */
    protected void doEffect(Spatial spatial, Vector3f position, float activeTimeInMilliSec) {
        Spatial sp = spatial.clone();
        sp.setCullHint(Spatial.CullHint.Never);
        sp.setLocalTranslation(position);
        SpatialLifeControl control = new SpatialLifeControl(activeTimeInMilliSec);
        sp.addControl(control);
        
        if (application.getCurrentScreen() != null) {
            application.getCurrentScreen().getRootNode().attachChild(sp);
        } else {
            application.getRootNode().attachChild(sp);
        }        

        //Call the particle respawn
        doParticleRespawn(sp);
        control.start();
    }
    
}
