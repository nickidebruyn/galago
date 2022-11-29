package com.galago.editor.terrain;

import com.jme3.terrain.heightmap.AbstractHeightMap;

/**
 *
 * @author ndebruyn
 */
public class FlatHeightmap extends AbstractHeightMap {

    private int size;
    private float[] heightmapData;

    public FlatHeightmap(int size) {
        this.size = size;
    }

    @Override
    public boolean load() {
        heightmapData = new float[size * size];
        return true;
    }

    @Override
    public float[] getHeightMap() {
        return heightmapData;
    }

}
