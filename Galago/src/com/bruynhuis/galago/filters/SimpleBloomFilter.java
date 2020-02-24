/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.filters;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class SimpleBloomFilter extends Filter {

    private static final int DEFAULT_BLOOM_TYPE = 0;
    private static final float DEFAULT_STENGTH = 0.5f;
    private static final int DEFAULT_SAMPLES = 15;
    private static final float DEFAULT_SIZE = 3.0f;

    private float strength = DEFAULT_STENGTH;
    private float size = DEFAULT_SIZE;
    private int samples = DEFAULT_SAMPLES;
    
    public SimpleBloomFilter() {
        super("simple bloom");
    }

    /**
     * Creates a bloomfilter with the specified strength, size and samples.
     *
     * @param strength Strength. Default 0.50
     * @param size Size. Default 3.0
     * @param samples Samples. Default 10
     */
    public SimpleBloomFilter(float strength, float size, int samples) {
        super("SimpleBloomFilter");
        // 
        checkFloatArgument(strength, 0, 1f, "Strength");
        checkFloatArgument(size, 0, 5f, "Size");
        checkIntArgument(samples, 1, 20, "Samples");
        //

        this.strength = strength;
        this.size = size;
        this.samples = samples;

    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/simple-bloom.j3md");
        material.setFloat("Strength", strength);
        material.setFloat("Size", size);
        material.setInt("Samples", samples);

    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    /**
     * Set the samples of the scen.
     *
     * @param The gamma of the scene.
     */
    public void setSamples(int samples) {

        checkIntArgument(samples, 0, 20, "Samples");

        if (material != null) {
            material.setInt("Samples", samples);
        }
        this.samples = samples;
    }

    /**
     * Get samples.
     *
     * @return The samples of the bloom.
     */
    public float getSamples() {
        return samples;
    }

    /**
     * Set the size of the bloom.
     *
     * @param The size of the bloom.
     */
    public void setSize(float size) {

        checkFloatArgument(size, 0, 5f, "Size");

        if (material != null) {
            material.setFloat("Size", size);
        }
        this.size = size;
    }

    /**
     * Get size.
     *
     * @return The size of the scene.
     */
    public float getSize() {
        return size;
    }

    public void setStrength(float strength) {
        checkFloatArgument(strength, 0.0f, 100.0f, "Strength");
        //
        if (material != null) {
            material.setFloat("Strength", strength);
        }
    }

    public float getStrength() {
        return strength;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(strength, "strength", DEFAULT_STENGTH);
        oc.write(samples, "samples", DEFAULT_SAMPLES);
        oc.write(size, "size", DEFAULT_SIZE);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        strength = (Float) ic.readFloat("strength", DEFAULT_STENGTH);
        samples = (Integer) ic.readInt("samples", DEFAULT_SAMPLES);
        size = (Float) ic.readFloat("size", DEFAULT_SIZE);

    }

    private void checkFloatArgument(float value, float min, float max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " was " + value + " but should be between " + min + " and " + max);
        }
    }

    private void checkIntArgument(int value, int min, int max, String name) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " was " + value + " but should be between " + min + " and " + max);
        }
    }
}
