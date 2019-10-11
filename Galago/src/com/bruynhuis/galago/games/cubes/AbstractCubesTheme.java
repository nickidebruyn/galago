/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.cubes;

import com.cubes.Block;
import com.cubes.BlockManager;
import com.cubes.CubesSettings;
import com.jme3.app.Application;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author NideBruyn
 */
public abstract class AbstractCubesTheme {
    
    protected Application application;
    private String tilesetFile;
    private int tilesetColumns;
    private int tilesetRows;
    private boolean vertical = true;
    private CubesSettings cubesSettings;
    private Map<String, CubeDefinition> blockRegistry = new HashMap<>();
    private int chunkCountX = 1;
    private int chunkCountY = 1;
    private int chunkCountZ = 1;

    public AbstractCubesTheme(Application application, String tilesetFile, int tilesetColumns, int tilesetRows) {
        this.application = application;
        this.tilesetFile = tilesetFile;
        this.tilesetColumns = tilesetColumns;
        this.tilesetRows = tilesetRows;
    }
    
    public void load() {        
        cubesSettings = new CubesSettings(application);
        cubesSettings.setBlockSize(1);
//        cubesSettings.setChunkSizeX(10);
//        cubesSettings.setChunkSizeY(7);
//        cubesSettings.setChunkSizeZ(3);
        cubesSettings.setTexturesCount(tilesetColumns, tilesetRows);
        cubesSettings.setDefaultBlockMaterial(tilesetFile);
        
        registerSettings(cubesSettings);
        registerBlocks();
    }

    public String getTilesetFile() {
        return tilesetFile;
    }
    
    public Map<String, CubeDefinition> getBlockRegistry() {
        return blockRegistry;
    }
    
    public CubeDefinition getBlockDefinition(String name) {
        return blockRegistry.get(name);
    }

    public CubesSettings getCubesSettings() {
        return cubesSettings;
    }

    public int getTilesetColumns() {
        return tilesetColumns;
    }

    public int getTilesetRows() {
        return tilesetRows;
    }
    
    protected abstract void registerSettings(CubesSettings cubesSettings);
    
    protected void registerBlock(String name, Block block, int indexX, int indexY, boolean background) {
        CubeDefinition blockDefinition = new CubeDefinition();
        blockDefinition.setBlock(block);
        blockDefinition.setName(name);
        blockDefinition.setIndexX(indexX);
        blockDefinition.setIndexY(indexY);
        blockDefinition.setBackground(background);        
        blockRegistry.put(name, blockDefinition);        
        BlockManager.register(block);
        
    }
    
    protected abstract void registerBlocks();

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public int getChunkCountX() {
        return chunkCountX;
    }

    public void setChunkCountX(int chunkCountX) {
        this.chunkCountX = chunkCountX;
    }

    public int getChunkCountY() {
        return chunkCountY;
    }

    public void setChunkCountY(int chunkCountY) {
        this.chunkCountY = chunkCountY;
    }

    public int getChunkCountZ() {
        return chunkCountZ;
    }

    public void setChunkCountZ(int chunkCountZ) {
        this.chunkCountZ = chunkCountZ;
    }
    
}
