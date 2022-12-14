package com.galago.editor.ui.actions;

import com.jme3.texture.Texture;
import java.io.File;

/**
 *
 * @author ndebruyn
 */
public class TerrainAction {

    public static final int TYPE_FLAT = 0; //FLAT TERRAIN
    public static final int TYPE_ISLAND = 1; //ISLANG TERRAIN
    public static final int TYPE_MIDPOINT = 2; //MIDPOINT TERRAIN
    public static final int TYPE_IMAGE = 3; //IMAGE TERRAIN
    
    public static final int TOOL_PAINT = 0; //PAINT TERRAIN
    public static final int TOOL_RAISE = 1; //RAISE TERRAIN
    public static final int TOOL_FLATTEN = 2; //FLATTEN TERRAIN
    public static final int TOOL_SMOOTH = 3; //SMOOTH TERRAIN
    public static final int TOOL_GRASS1 = 4; //PAINT GRASS1
    public static final int TOOL_GRASS2 = 5; //PAINT GRASS2
    public static final int TOOL_GRASS3 = 6; //PAINT GRASS3
    
    public static final int MATERIAL_PAINTABLE = 0; //TERRAIN Material Paintable
    public static final int MATERIAL_HEIGHT_BASED = 1; //TERRAIN Material Height based
    public static final int MATERIAL_PBR = 2; //TERRAIN Material PBR Paintable
    
    public static final String BATCH_GRASS1 = "BATCH-GRASS1"; //GRASS1 BATCH
    public static final String BATCH_GRASS2 = "BATCH-GRASS2"; //GRASS2 BATCH
    public static final String BATCH_GRASS3 = "BATCH-GRASS3"; //GRASS3 BATCH

    private int type = TYPE_FLAT;
    private int terrainSize = 256;
    private int terrainMaterial = MATERIAL_PAINTABLE;
    private int iterations = 100;
    private float minRadius = 10;
    private float maxRadius = 50;
    private float range = 0;
    private float persistence = 0.5f;
    private long seed = 1;
    private Texture heightMapTexture;
    private int tool = TOOL_PAINT;
    
    private float autoPaintMin = 0f;
    private float autoPaintMax = 1f;
    private float autoStartHeight = 0f;
    private float scaleFactor = 1f;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTerrainSize() {
        return terrainSize;
    }

    public void setTerrainSize(int terrainSize) {
        this.terrainSize = terrainSize;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public float getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getPersistence() {
        return persistence;
    }

    public void setPersistence(float persistence) {
        this.persistence = persistence;
    }

    public Texture getHeightMapTexture() {
        return heightMapTexture;
    }

    public void setHeightMapTexture(Texture heightMapTexture) {
        this.heightMapTexture = heightMapTexture;
    }

    public int getTerrainMaterial() {
        return terrainMaterial;
    }

    public void setTerrainMaterial(int terrainMaterial) {
        this.terrainMaterial = terrainMaterial;
    }

    public float getAutoPaintMin() {
        return autoPaintMin;
    }

    public void setAutoPaintMin(float autoPaintMin) {
        this.autoPaintMin = autoPaintMin;
    }

    public float getAutoPaintMax() {
        return autoPaintMax;
    }

    public void setAutoPaintMax(float autoPaintMax) {
        this.autoPaintMax = autoPaintMax;
    }

    public float getAutoStartHeight() {
        return autoStartHeight;
    }

    public void setAutoStartHeight(float autoStartHeight) {
        this.autoStartHeight = autoStartHeight;
    }

    public int getTool() {
        return tool;
    }

    public void setTool(int tool) {
        this.tool = tool;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
}
