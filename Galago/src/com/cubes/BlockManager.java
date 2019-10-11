/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import java.util.HashMap;

/**
 *
 * @author Carl
 */
public class BlockManager{
    
    private static HashMap<Block, Byte> BLOCK_TYPES = new HashMap<Block, Byte>();
    private static Block[] TYPES_BLOCKS = new Block[256];
    private static byte nextBlockType = 1;
    
    public static void register(Block block){
        BLOCK_TYPES.put(block, nextBlockType);
        TYPES_BLOCKS[nextBlockType] = block;
        nextBlockType++;
    }
    
    public static byte getType(Block block){
        return BLOCK_TYPES.get(block);
    }
    
    public static Block getBlock(byte type){
        return TYPES_BLOCKS[type];
    }
}
