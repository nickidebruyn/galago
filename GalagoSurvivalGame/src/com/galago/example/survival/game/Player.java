/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.survival.game;

import com.bruynhuis.galago.games.blender3d.Blender3DGame;
import com.bruynhuis.galago.games.blender3d.Blender3DPlayer;
import com.jme3.math.Vector3f;

/**
 *
 * @author nicki
 */
public class Player extends Blender3DPlayer {

    public Player(Blender3DGame physicsGame) {
        super(physicsGame);
    }
    
    @Override
    protected void init() {

    }

    @Override
    protected float getSize() {
        return 1f;

    }

    @Override
    public Vector3f getPosition() {
        return playerNode.getWorldTranslation();
    }

    @Override
    public void doDie() {

    }
    
}
