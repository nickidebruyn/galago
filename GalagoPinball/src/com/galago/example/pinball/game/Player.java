/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.pinball.game;

import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DGame;
import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;

/**
 *
 * @author nicki
 */
public class Player extends BlenderPhysics2DPlayer {
    
    private RigidBodyControl rbc;
    private Sprite sprite;

    public Player(BlenderPhysics2DGame physicsGame) {
        super(physicsGame);
    }

    @Override
    protected void init() {
        
        sprite = new Sprite("ball", 0.8f, 0.8f);
        sprite.setImage("Textures/ball.png");
        sprite.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
        sprite.move(0, 0, 1);
        playerNode.attachChild(sprite);
        
        
    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public Vector3f getPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doDie() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
