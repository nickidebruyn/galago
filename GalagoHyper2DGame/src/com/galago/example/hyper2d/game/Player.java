/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper2d.game;

import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.PyramidCollisionShape;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.effect.ParticleEmitter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

/**
 *
 * @author nicki
 */
public class Player extends SimplePhysics2DPlayer implements PhysicsTickListener {

    private Sprite model;
    private RigidBodyControl rbc;
    private float moveSpeed = 0.1f;
    private PlayerShootControl playerShootControl;
    private BitmapText text;
    private ParticleEmitter thrusters;
    private float size = 1.8f;
    private ColorRGBA color;

    public Player(SimplePhysics2DGame physicsGame) {
        super(physicsGame);
    }

    @Override
    protected void init() {
        lives = 10;
        color = ColorUtils.rgb(0, 168, 255);

        model = SpriteUtils.addSprite(playerNode, size, size);
        model.setImage("Textures/ship1.png");
        model.getMaterial().setFloat("AlphaDiscardThreshold", 0.1f);
        model.getMaterial().setColor("Color", color);

        rbc = new RigidBodyControl(new PyramidCollisionShape(size*0.6f, size), 1);
        playerNode.addControl(rbc);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

        playerShootControl = new PlayerShootControl(this);
        playerNode.addControl(playerShootControl);
        
        //Add text
        text = ((Game)game).getBitmapFont().createLabel("" + lives);
        text.setBox(new Rectangle(-size * 0.5f, size * 0.5f, size, size *0.5f));
        text.setAlignment(BitmapFont.Align.Center);
        text.setVerticalAlignment(BitmapFont.VAlign.Bottom);
        text.setSize(0.4f);
        text.setColor(ColorRGBA.White);
        text.setLocalTranslation(0, 0, 0.1f);
        text.setQueueBucket(RenderQueue.Bucket.Transparent);
        playerNode.attachChild(text);
        
        //Load the thrusters
        Node thrustersNode = (Node)game.getBaseApplication().getAssetManager().loadModel("Models/player/ship1-thrusters.j3o");
        thrusters = (ParticleEmitter)thrustersNode.getChild(0);
        playerNode.attachChild(thrustersNode);
        thrustersNode.move(0, -0.7f, 0);
        thrusters.setStartColor(new ColorRGBA(color.r, color.g, color.b, 0.8f));
        thrusters.setEndColor(new ColorRGBA(color.r, color.g, color.b, 0.1f));
        
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicLocation().clone();
    }

    @Override
    public void doDie() {
        //TODO
        game.getBaseApplication().getEffectManager().prepareColor(new ColorRGBA(color.r, color.g, color.b, 1f), 
                new ColorRGBA(color.r, color.g, color.b, 0.1f));
        game.getBaseApplication().getEffectManager().doEffect("player-kill", rbc.getPhysicLocation().clone());
        playerNode.removeFromParent();
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);
    }

    @Override
    public void doDamage(int hits) {
        super.doDamage(hits);
        text.setText("" + lives);
    }

    @Override
    public void start() {
        rbc.setGravityScale(0);
        rbc.setFriction(0.0f);
        rbc.setRestitution(0.0f);

    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
//        Debug.log("Update: " + tpf);
        rbc.setPhysicRotation(0);
        rbc.setAngularVelocity(0);

    }

    public void movePlayerToTarget(Vector3f targetPosition) {
        rbc.setPhysicLocation(rbc.getPhysicLocation().interpolateLocal(targetPosition, moveSpeed));
        
    }

    public void shoot(boolean shoot) {
        playerShootControl.setShoot(shoot);
        
    }
}
