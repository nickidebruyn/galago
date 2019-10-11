/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.models;

import java.util.HashMap;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.CollisionResult;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Spatial;
import com.cubes.*;

/**
 *
 * @author Carl
 */
public class BlockModel{

    public BlockModel(String modelPath, Block[] blocks){
        this.modelPath = modelPath;
        this.blocks = blocks;
    }
    private String modelPath;
    private Block[] blocks;
    private int nextMaterialIndex = 0;
    private HashMap<Material, Block> materialBlocks = new HashMap<Material, Block>();
    
    public void addToBlockTerrain(BlockTerrainControl blockTerrain, Vector3Int location, Vector3Int size){
        Spatial spatial = blockTerrain.getSettings().getAssetManager().loadModel(modelPath);
        Vector3f bounds = getBounds(spatial);
        Vector3f relativeBlockSize = new Vector3f((bounds.getX() / size.getX()), (bounds.getY() / size.getY()), (bounds.getZ() / size.getZ()));
        Geometry testBlockBox = new Geometry("", new Box(relativeBlockSize.divide(2), relativeBlockSize.getX(), relativeBlockSize.getY(), relativeBlockSize.getZ()));
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<size.getX();x++){
            for(int y=0;y<size.getY();y++){
                for(int z=0;z<size.getZ();z++){
                    testBlockBox.setLocalTranslation(
                        (relativeBlockSize.getX() * x) - (bounds.getX() / 2),
                        (relativeBlockSize.getY() * y),
                        (relativeBlockSize.getZ() * z) - (bounds.getZ() / 2)
                    );
                    CollisionResults collisionResults = new CollisionResults();
                    spatial.collideWith(testBlockBox.getWorldBound(), collisionResults);
                    CollisionResult collisionResult = collisionResults.getClosestCollision();
                    if(collisionResult != null){
                        tmpLocation.set(location).addLocal(x, y, z);
                        Block block = getMaterialBlock(collisionResult.getGeometry().getMaterial());
                        blockTerrain.setBlock(tmpLocation, block);
                    }
                }
            }
        }
    }
    
    private Block getMaterialBlock(Material material){
        Block block = materialBlocks.get(material);
        if(block == null){
            block = blocks[nextMaterialIndex];
            if(nextMaterialIndex < (blocks.length - 1)){
                nextMaterialIndex++;
            }
            materialBlocks.put(material, block);
        }
        return block;
    }
    
    private static Vector3f getBounds(Spatial spatial){
        if(spatial.getWorldBound() instanceof BoundingBox){
            BoundingBox boundingBox = (BoundingBox) spatial.getWorldBound();
            return new Vector3f(2 * boundingBox.getXExtent(), 2 * boundingBox.getYExtent(), 2 * boundingBox.getZExtent());
        }
        return new Vector3f(0, 0, 0);
    }
}
