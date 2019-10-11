/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import java.io.IOException;
import java.util.ArrayList;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.cubes.network.*;

/**
 *
 * @author Carl
 */
public class BlockTerrainControl extends AbstractControl implements BitSerializable {

    public BlockTerrainControl(CubesSettings settings, Vector3Int chunksCount){
        this.settings = settings;
        initializeChunks(chunksCount);
    }
    private CubesSettings settings;
    private BlockChunkControl[][][] chunks;
    private ArrayList<BlockChunkListener> chunkListeners = new ArrayList<BlockChunkListener>();
    
    private void initializeChunks(Vector3Int chunksCount){
        chunks = new BlockChunkControl[chunksCount.getX()][chunksCount.getY()][chunksCount.getZ()];
        for(int x=0;x<chunks.length;x++){
            for(int y=0;y<chunks[0].length;y++){
                for(int z=0;z<chunks[0][0].length;z++){
                    BlockChunkControl chunk = new BlockChunkControl(this, x, y, z);
                    chunks[x][y][z] = chunk;
                }
            }
        }
    }

    @Override
    public void setSpatial(Spatial spatial){
        Spatial oldSpatial = this.spatial;
        super.setSpatial(spatial);
        for(int x=0;x<chunks.length;x++){
            for(int y=0;y<chunks[0].length;y++){
                for(int z=0;z<chunks[0][0].length;z++){
                    if(spatial == null){
                        oldSpatial.removeControl(chunks[x][y][z]);
                    }
                    else{
                        spatial.addControl(chunks[x][y][z]);
                    }
                }
            }
        }
    }

    @Override
    protected void controlUpdate(float lastTimePerFrame){
        updateSpatial();
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort){
        
    }

    @Override
    public Control cloneForSpatial(Spatial spatial){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Block getBlock(int x, int y, int z){
        return getBlock(new Vector3Int(x, y, z));
    }
    
    public Block getBlock(Vector3Int location){
        BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
        if(localBlockState != null){
            return localBlockState.getBlock();
        }
        return null;
    }
    
    public void setBlockArea(Vector3Int location, Vector3Int size, Block block){
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<size.getX();x++){
            for(int y=0;y<size.getY();y++){
                for(int z=0;z<size.getZ();z++){
                    tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
                    setBlock(tmpLocation, block);
                }
            }
        }
    }
    
    public void setBlock(int x, int y, int z, Block block){
        setBlock(new Vector3Int(x, y, z), block);
    }
    
    public void setBlock(Vector3Int location, Block block){
        BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
        if(localBlockState != null){
            localBlockState.setBlock(block);
        }
    }
    
    public void removeBlockArea(Vector3Int location, Vector3Int size){
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<size.getX();x++){
            for(int y=0;y<size.getY();y++){
                for(int z=0;z<size.getZ();z++){
                    tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
                    removeBlock(tmpLocation);
                }
            }
        }
    }
    
    public void removeBlock(int x, int y, int z){
        removeBlock(new Vector3Int(x, y, z));
    }
    
    public void removeBlock(Vector3Int location){
        BlockTerrain_LocalBlockState localBlockState = getLocalBlockState(location);
        if(localBlockState != null){
            localBlockState.removeBlock();
        }
    }
    
    private BlockTerrain_LocalBlockState getLocalBlockState(Vector3Int blockLocation){
        if(blockLocation.hasNegativeCoordinate()){
            return null;
        }
        BlockChunkControl chunk = getChunk(blockLocation);
        if(chunk != null){
            Vector3Int localBlockLocation = getLocalBlockLocation(blockLocation, chunk);
            return new BlockTerrain_LocalBlockState(chunk, localBlockLocation);
        }
        return null;
    }
    
    public BlockChunkControl getChunk(Vector3Int blockLocation){
        if(blockLocation.hasNegativeCoordinate()){
            return null;
        }
        Vector3Int chunkLocation = getChunkLocation(blockLocation);
        if(isValidChunkLocation(chunkLocation)){
            return chunks[chunkLocation.getX()][chunkLocation.getY()][chunkLocation.getZ()];
        }
        return null;
    }
    
    private boolean isValidChunkLocation(Vector3Int location){
        return Util.isValidIndex(chunks, location);
    }
    
    private Vector3Int getChunkLocation(Vector3Int blockLocation){
        Vector3Int chunkLocation = new Vector3Int();
        int chunkX = (blockLocation.getX() / settings.getChunkSizeX());
        int chunkY = (blockLocation.getY() / settings.getChunkSizeY());
        int chunkZ = (blockLocation.getZ() / settings.getChunkSizeZ());
        chunkLocation.set(chunkX, chunkY, chunkZ);
        return chunkLocation;
    }
    
    public static Vector3Int getLocalBlockLocation(Vector3Int blockLocation, BlockChunkControl chunk){
        Vector3Int localLocation = new Vector3Int();
        int localX = (blockLocation.getX() - chunk.getBlockLocation().getX());
        int localY = (blockLocation.getY() - chunk.getBlockLocation().getY());
        int localZ = (blockLocation.getZ() - chunk.getBlockLocation().getZ());
        localLocation.set(localX, localY, localZ);
        return localLocation;
    }
    
    public boolean updateSpatial(){
        boolean wasUpdatedNeeded = false;
        for(int x=0;x<chunks.length;x++){
            for(int y=0;y<chunks[0].length;y++){
                for(int z=0;z<chunks[0][0].length;z++){
                    BlockChunkControl chunk = chunks[x][y][z];
                    if(chunk.updateSpatial()){
                        wasUpdatedNeeded = true;
                        for(int i=0;i<chunkListeners.size();i++){
                            BlockChunkListener blockTerrainListener = chunkListeners.get(i);
                            blockTerrainListener.onSpatialUpdated(chunk);
                        }
                    }
                }
            }
        }
        return wasUpdatedNeeded;
    }
    
    public void updateBlockMaterial(){
        for(int x=0;x<chunks.length;x++){
            for(int y=0;y<chunks[0].length;y++){
                for(int z=0;z<chunks[0][0].length;z++){
                    chunks[x][y][z].updateBlockMaterial();
                }
            }
        }
    }
    
    public void addChunkListener(BlockChunkListener blockChunkListener){
        chunkListeners.add(blockChunkListener);
    }
    
    public void removeChunkListener(BlockChunkListener blockChunkListener){
        chunkListeners.remove(blockChunkListener);
    }
    
    public CubesSettings getSettings(){
        return settings;
    }

    public BlockChunkControl[][][] getChunks(){
        return chunks;
    }
    
    //Tools
    
    public void setBlocksFromHeightmap(Vector3Int location, String heightmapPath, int maximumHeight, Block block){
        try{
            Texture heightmapTexture = settings.getAssetManager().loadTexture(heightmapPath);
            ImageBasedHeightMap heightmap = new ImageBasedHeightMap(heightmapTexture.getImage(), 1f);
            heightmap.load();
            heightmap.setHeightScale(maximumHeight / 255f);
            setBlocksFromHeightmap(location, getHeightmapBlockData(heightmap.getScaledHeightMap(), heightmap.getSize()), block);
        }catch(Exception ex){
            System.out.println("Error while loading heightmap '" + heightmapPath + "'.");
        }
    }

    private static int[][] getHeightmapBlockData(float[] heightmapData, int length){
        int[][] data = new int[heightmapData.length / length][length];
        int x = 0;
        int z = 0;
        for(int i=0;i<heightmapData.length;i++){
            data[x][z] = (int) Math.round(heightmapData[i]);
            x++;
            if((x != 0) && ((x % length) == 0)){
                x = 0;
                z++;
            }
        }
        return data;
    }

    public void setBlocksFromHeightmap(Vector3Int location, int[][] heightmap, Block block){
        Vector3Int tmpLocation = new Vector3Int();
        Vector3Int tmpSize = new Vector3Int();
        for(int x=0;x<heightmap.length;x++){
            for(int z=0;z<heightmap[0].length;z++){
                tmpLocation.set(location.getX() + x, location.getY(), location.getZ() + z);
                tmpSize.set(1, heightmap[x][z], 1);
                setBlockArea(tmpLocation, tmpSize, block);
            }
        }
    }
    
    public void setBlocksFromNoise(Vector3Int location, Vector3Int size, float roughness, Block block){
        Noise noise = new Noise(null, roughness, size.getX(), size.getZ());
        noise.initialise();
        float gridMinimum = noise.getMinimum();
        float gridLargestDifference = (noise.getMaximum() - gridMinimum);
        float[][] grid = noise.getGrid();
        for(int x=0;x<grid.length;x++){
            float[] row = grid[x];
            for(int z=0;z<row.length;z++){
                /*---Calculation of block height has been summarized to minimize the java heap---
                float gridGroundHeight = (row[z] - gridMinimum);
                float blockHeightInPercents = ((gridGroundHeight * 100) / gridLargestDifference);
                int blockHeight = ((int) ((blockHeightInPercents / 100) * size.getY())) + 1;
                ---*/
                int blockHeight = (((int) (((((row[z] - gridMinimum) * 100) / gridLargestDifference) / 100) * size.getY())) + 1);
                Vector3Int tmpLocation = new Vector3Int();
                for(int y=0;y<blockHeight;y++){
                    tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
                    setBlock(tmpLocation, block);
                }
            }
        }
    }
    
    public void setBlocksForMaximumFaces(Vector3Int location, Vector3Int size, Block block){
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<size.getX();x++){
            for(int y=0;y<size.getY();y++){
                for(int z=0;z<size.getZ();z++){
                    if(((x ^ y ^ z) & 1) == 1){
                        tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
                        setBlock(tmpLocation, block);
                    }
                }
            }
        }
    }

    @Override
    public BlockTerrainControl clone(){
        BlockTerrainControl blockTerrain = new BlockTerrainControl(settings, new Vector3Int());
        blockTerrain.setBlocksFromTerrain(this);
        return blockTerrain;
    }
    
    public void setBlocksFromTerrain(BlockTerrainControl blockTerrain){
        CubesSerializer.readFromBytes(this, CubesSerializer.writeToBytes(blockTerrain));
    }

    @Override
    public void write(BitOutputStream outputStream){
        outputStream.writeInteger(chunks.length);
        outputStream.writeInteger(chunks[0].length);
        outputStream.writeInteger(chunks[0][0].length);
        for(int x=0;x<chunks.length;x++){
            for(int y=0;y<chunks[0].length;y++){
                for(int z=0;z<chunks[0][0].length;z++){
                    chunks[x][y][z].write(outputStream);
                }
            }
        }
    }

    @Override
    public void read(BitInputStream inputStream) throws IOException{
        int chunksCountX = inputStream.readInteger();
        int chunksCountY = inputStream.readInteger();
        int chunksCountZ = inputStream.readInteger();
        initializeChunks(new Vector3Int(chunksCountX, chunksCountY, chunksCountZ));
        for(int x=0;x<chunksCountX;x++){
            for(int y=0;y<chunksCountY;y++){
                for(int z=0;z<chunksCountZ;z++){
                    chunks[x][y][z].read(inputStream);
                }
            }
        }
    }
}
