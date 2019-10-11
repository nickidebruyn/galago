/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import java.util.List;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author Carl
 */
public abstract class BlockShape{
    
    private boolean isTransparent;
    protected List<Vector3f> positions;
    protected List<Short> indices;
    protected List<Float> normals;
    protected List<Vector2f> textureCoordinates;
    
    public void prepare(boolean isTransparent, List<Vector3f> positions, List<Short> indices, List<Float> normals, List<Vector2f> textureCoordinates){
        this.positions = positions;
        this.indices = indices;
        this.normals = normals;
        this.textureCoordinates = textureCoordinates;
        this.isTransparent = isTransparent;
    }
    
    public abstract void addTo(BlockChunkControl chunk, Vector3Int blockLocation);
    
    protected boolean shouldFaceBeAdded(BlockChunkControl chunk, Vector3Int blockLocation, Block.Face face){
        Block block = chunk.getBlock(blockLocation);
        BlockSkin blockSkin = block.getSkin(chunk, blockLocation, face);
        if(blockSkin.isTransparent() == isTransparent){
            Vector3Int neighborBlockLocation = BlockNavigator.getNeighborBlockLocalLocation(blockLocation, face);
            Block neighborBlock = chunk.getBlock(neighborBlockLocation);
            if(neighborBlock != null){
                BlockSkin neighborBlockSkin = neighborBlock.getSkin(chunk, blockLocation, face);
                if(blockSkin.isTransparent() != neighborBlockSkin.isTransparent()){
                    return true;
                }
                BlockShape neighborShape = neighborBlock.getShape(chunk, neighborBlockLocation);
                return (!(canBeMerged(face) && neighborShape.canBeMerged(BlockNavigator.getOppositeFace(face))));
            }
            return true;
        }
        return false;
    }
    
    protected abstract boolean canBeMerged(Block.Face face);
    
    protected Vector2f getTextureCoordinates(BlockChunkControl chunk, BlockSkin_TextureLocation textureLocation, float xUnitsToAdd, float yUnitsToAdd){
        float textureUnitX = (1f / chunk.getTerrain().getSettings().getTexturesCountX());
        float textureUnitY = (1f / chunk.getTerrain().getSettings().getTexturesCountY());
        float x = (((textureLocation.getColumn() + xUnitsToAdd) * textureUnitX));
        float y = ((((-1 * textureLocation.getRow()) + (yUnitsToAdd - 1)) * textureUnitY) + 1);
        return new Vector2f(x, y);
    }
}
