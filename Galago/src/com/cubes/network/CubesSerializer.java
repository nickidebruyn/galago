/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.cubes.*;

/**
 *
 * @author Carl
 */
public class CubesSerializer {
    
    public static byte[][][][] writeChunksToBytes(BlockTerrainControl blockTerrain){
        BlockChunkControl[][][] chunks = blockTerrain.getChunks();
        byte[][][][] bytes = new byte[chunks.length][chunks[0].length][chunks[0][0].length][];
        for(int x=0;x<chunks.length;x++){
            for(int y=0;y<chunks[x].length;y++){
                for(int z=0;z<chunks[x][y].length;z++){
                    bytes[x][y][z] = writeToBytes(chunks[x][y][z]);
                }
            }
        }
        return bytes;
    }
    
    public static byte[] writeToBytes(BitSerializable bitSerializable){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream);
        bitSerializable.write(bitOutputStream);
        bitOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    
    public static void readFromBytes(BitSerializable bitSerializable, byte[] bytes){
        BitInputStream bitInputStream = new BitInputStream(new ByteArrayInputStream(bytes));
        try{
            bitSerializable.read(bitInputStream);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
