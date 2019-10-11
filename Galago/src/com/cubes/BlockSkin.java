/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

/**
 *
 * @author Carl
 */
public class BlockSkin{
    
    public BlockSkin(BlockSkin_TextureLocation textureLocation, boolean isTransparent){
        this.textureLocation = textureLocation;
        this.isTransparent = isTransparent;
    }
    private BlockSkin_TextureLocation textureLocation;
    private boolean isTransparent;

    public BlockSkin_TextureLocation getTextureLocation(){
        return textureLocation;
    }

    public boolean isTransparent(){
        return isTransparent;
    }
}
