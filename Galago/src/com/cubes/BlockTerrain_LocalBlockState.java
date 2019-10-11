/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

/**
 *
 * @author Carl
 */
public class BlockTerrain_LocalBlockState{

    public BlockTerrain_LocalBlockState(BlockChunkControl chunk, Vector3Int localBlockLocation){
        this.chunk = chunk;
        this.localBlockLocation = localBlockLocation;
    }
    private BlockChunkControl chunk;
    private Vector3Int localBlockLocation;

    public BlockChunkControl getChunk(){
        return chunk;
    }

    public Vector3Int getLocalBlockLocation(){
        return localBlockLocation;
    }

    public Block getBlock(){
        return chunk.getBlock(localBlockLocation);
    }
    
    public void setBlock(Block block){
        chunk.setBlock(localBlockLocation, block);
    }
    
    public void removeBlock(){
        chunk.removeBlock(localBlockLocation);
    }
}
