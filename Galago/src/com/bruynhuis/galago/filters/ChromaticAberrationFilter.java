/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.filters;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import com.bruynhuis.galago.control.tween.Vector3fAccessor;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.Texture2D;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class ChromaticAberrationFilter extends Filter {

    private Texture2D noiseTesture;
    private Vector3f distortionOffsets = Vector3f.ZERO;
    private float distortionTime = 0.1f;
    private float distortionFrequency = 0.1f;

    /**
     * creates a ChromaticAberrationFilter
     */
    public ChromaticAberrationFilter() {
        super("ChromaticAberrationFilter");

    }

    @Override
    protected Material getMaterial() {
//        material.setColor("Color", color);
        material.setTexture("NoiseTexture", noiseTesture);
        material.setVector3("DistortionOffsets", distortionOffsets);
        material.setFloat("DistortionTime", distortionTime);
        material.setFloat("DistortionFrequency", distortionFrequency);
        return material;
    }

    @Override
    protected void preFrame(float tpf) {
        if (material != null) {
            material.setVector3("DistortionOffsets", distortionOffsets);
            //SharedSystem.getInstance().getBaseApplication().log("distortionOffsets: " + distortionOffsets);
        }
    }

    /**
     *
     * @param count
     */
    public void doEffect(final float speed, Vector3f offsets) {

        Tween.to(distortionOffsets, Vector3fAccessor.VECTOR3F, speed)
                .target(offsets.x, offsets.y, offsets.z)
                .ease(Bounce.OUT)
                .setCallback(new TweenCallback() {
                            @Override
                                public void onEvent(int i, BaseTween<?> bt) {
                                    Tween.to(distortionOffsets, Vector3fAccessor.VECTOR3F, speed*5f)
                                        .target(0, 0, 0)
                                        .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
                                }
                                })
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        noiseTesture = (Texture2D) manager.loadTexture("Resources/filters/Perlin_noise.jpg");

        material = new Material(manager, "Resources/filters/chromatic-aberration.j3md");
    }

    public float getDistortionTime() {
        return distortionTime;
    }

    public void setDistortionTime(float distortionTime) {
        this.distortionTime = distortionTime;
        if (material != null) {
            material.setFloat("DistortionTime", distortionTime);
        }
    }

    public float getDistortionFrequency() {
        return distortionFrequency;
    }

    public void setDistortionFrequency(float distortionFrequency) {
        this.distortionFrequency = distortionFrequency;
        if (material != null) {
            material.setFloat("DistortionFrequency", distortionFrequency);
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(distortionOffsets, "distortionOffsets", Vector3f.ZERO);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        distortionOffsets = (Vector3f) ic.readSavable("distortionOffsets", Vector3f.ZERO);
    }
}
