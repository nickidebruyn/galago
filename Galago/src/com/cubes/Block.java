/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import com.cubes.shapes.BlockShape_Cube;

/**
 *
 * @author Carl
 */
public class Block{

    public Block(BlockSkin... skins){
        this.skins = skins;
    }
    public static enum Face{
        Top, Bottom, Left, Right, Front, Back
    };
    private BlockShape[] shapes = new BlockShape[]{new BlockShape_Cube()};
    private BlockSkin[] skins;

    protected void setShapes(BlockShape... shapes){
        this.shapes = shapes;
    }
    
    public BlockShape getShape(BlockChunkControl chunk, Vector3Int location){
        return shapes[getShapeIndex(chunk, location)];
    }
    
    protected int getShapeIndex(BlockChunkControl chunk, Vector3Int location){
        return 0;
    }
    
    public BlockSkin getSkin(BlockChunkControl chunk, Vector3Int location, Face face){
        return skins[getSkinIndex(chunk, location, face)];
    }
    
    protected int getSkinIndex(BlockChunkControl chunk, Vector3Int location, Face face){
        if(skins.length == 6){
            return face.ordinal();
        }
        return 0;
    }
}
