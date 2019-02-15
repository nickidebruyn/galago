/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.pinball.game;

import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DGame;
import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;

/**
 *
 * @author nicki
 */
public class Player extends BlenderPhysics2DPlayer implements PhysicsTickListener {

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

        rbc = new RigidBodyControl(new CircleCollisionShape(0.4f), 1f);
        playerNode.addControl(rbc);
        rbc.setPhysicLocation(-2.2f, 0);
//        rbc.setFriction(0);
//        rbc.setRestitution(0.2f);
//        rbc.setGravityScale(0.5f);

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(rbc);

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicLocation();
    }

    @Override
    public void doDie() {

    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        //Do some pre checks here

        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {
//            log("Pre check player position: " + getPosition());

            if (getPosition().y <= -16) {
                doDamage(10);

            }
        }

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
    }

}
