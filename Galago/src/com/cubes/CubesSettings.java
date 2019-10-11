/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;

/**
 *
 * @author Carl
 */
public class CubesSettings{
    
    public CubesSettings(Application application){
        assetManager = application.getAssetManager();
    }
    private AssetManager assetManager;
    private float blockSize = 3;
    private int chunkSizeX = 10;
    private int chunkSizeY = 10;
    private int chunkSizeZ = 10;
    private Material blockMaterial;
    private int texturesCountX = 16;
    private int texturesCountY = 16;

    public AssetManager getAssetManager(){
        return assetManager;
    }

    public float getBlockSize(){
        return blockSize;
    }

    public void setBlockSize(float blockSize){
        this.blockSize = blockSize;
    }

    public int getChunkSizeX(){
        return chunkSizeX;
    }

    public void setChunkSizeX(int chunkSizeX){
        this.chunkSizeX = chunkSizeX;
    }

    public int getChunkSizeY(){
        return chunkSizeY;
    }

    public void setChunkSizeY(int chunkSizeY){
        this.chunkSizeY = chunkSizeY;
    }

    public int getChunkSizeZ(){
        return chunkSizeZ;
    }

    public void setChunkSizeZ(int chunkSizeZ){
        this.chunkSizeZ = chunkSizeZ;
    }

    public Material getBlockMaterial(){
        return blockMaterial;
    }

    public void setDefaultBlockMaterial(String textureFilePath){
        setBlockMaterial(new BlockChunk_Material(assetManager, textureFilePath));
    }

    public void setBlockMaterial(Material blockMaterial){
        this.blockMaterial = blockMaterial;
    }

    public int getTexturesCountX(){
        return texturesCountX;
    }

    public int getTexturesCountY(){
        return texturesCountY;
    }

    public void setTexturesCount(int texturesCountX, int texturesCountY){
        this.texturesCountX = texturesCountX;
        this.texturesCountY = texturesCountY;
    }
}
