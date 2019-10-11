/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.cubes;

import com.cubes.Block;

/**
 *
 * @author NideBruyn
 */
public class CubeDefinition {
    
    private String name;
    private Block block;
    private int indexX;
    private int indexY;
    private boolean background;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getIndexX() {
        return indexX;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }
    
    
    
}
