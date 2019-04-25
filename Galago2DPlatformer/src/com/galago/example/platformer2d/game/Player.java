/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CapsuleCollisionShape;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class Player extends SimplePhysics2DPlayer implements PhysicsTickListener {

    private Sprite sprite;
    private RigidBodyControl rbc;
    private PlayerShootControl playerShootControl;
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private float accel = 45f;
    private float deccel = 30f;
    private float moveSpeed = 0;
    private float jumpForce = 18;
    private float width = 1.4f;
    private float height = 1.4f;
    private float MAXSPEED = 600;
    private int bounceCount = 0;
//    private int direction = 1;
    private Tween idleTween;
    private Vector3f aimDirection;
    private Sprite weapon;
    private Node weaponNode;

    public Player(SimplePhysics2DGame physicsGame) {
        super(physicsGame);
    }

    @Override
    protected void init() {

        sprite = SpriteUtils.addSprite(playerNode, "Textures/player1.png", width, height);

        rbc = new RigidBodyControl(new CapsuleCollisionShape(width * 0.7f, height * 0.9f), 1);
        rbc.setFriction(0f);
        rbc.setRestitution(0.2f);
        rbc.setGravityScale(3);
        playerNode.addControl(rbc);

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(rbc);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

        playerShootControl = new PlayerShootControl(this);
        playerNode.addControl(playerShootControl);

        rbc.setPhysicLocation(0, -5.1f);

        weaponNode = new Node("weapon-node");
        playerNode.attachChild(weaponNode);
        weapon = SpriteUtils.addSprite(weaponNode, "Textures/gun.png", 1.2f, 0.6f, 0.9f, 0f, 1.1f);

        idleTween = Tween.to(sprite, SpatialAccessor.SCALE_XYZ, 0.22f)
                .target(1.05f, 0.95f, 1)
                .repeatYoyo(Tween.INFINITY, 0)
                .start(game.getBaseApplication().getTweenManager());

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

                    if (up) {
                        SpatialUtils.rotate(weaponNode, 0, 0, tpf * 100);
                    }
                    if (down) {
                        SpatialUtils.rotate(weaponNode, 0, 0, -tpf * 100);
                    }

                    playerShootControl.setDirection(weaponNode.getWorldRotation().getRotationColumn(0).normalize());
                    playerShootControl.setSpawnPoint(weapon.getWorldTranslation());
                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

    }

    @Override
    protected float getSize() {
        return 1f;
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicLocation();
    }

//    public int getDirection() {
//        return direction;
//    }
    @Override
    public void doDie() {

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);
    }

    public void setLeft(boolean left) {
        this.left = left;
        this.right = false;
        this.sprite.flipHorizontal(false);
        SpatialUtils.rotateTo(weaponNode, 0, 180, weaponNode.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG);
    }

    public void setRight(boolean right) {
        this.right = right;
        this.left = false;
        this.sprite.flipHorizontal(true);
        SpatialUtils.rotateTo(weaponNode, 0, 0, weaponNode.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG);
    }

    public void jump() {
        if (bounceCount < 2) {
            rbc.setLinearVelocity(rbc.getLinearVelocity().x, jumpForce);
            bounceCount++;
        }
    }

    public void setAimUp(boolean up) {
        this.up = up;
        this.down = false;
    }

    public void setAimDown(boolean down) {
        this.down = down;
        this.up = false;
    }

    public void shoot(boolean shoot) {
        playerShootControl.setDirection(weaponNode.getWorldRotation().getRotationColumn(0).normalize());
        playerShootControl.setSpawnPoint(weapon.getWorldTranslation());
        playerShootControl.setShoot(shoot);
    }

    public boolean isOnGround() {
        return bounceCount < 2;
    }

    public void setOnGround() {
        bounceCount = 0;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        rbc.setAngularVelocity(0);
        rbc.setPhysicRotation(0);
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

        if (left && !right) {
            moveSpeed -= (accel);

            if (moveSpeed < -MAXSPEED) {
                moveSpeed = -MAXSPEED;
            }
        } else if (right && !left) {
            moveSpeed += (accel);

            if (moveSpeed > MAXSPEED) {
                moveSpeed = MAXSPEED;
            }

        } else //Decelarate code below            
        {
            if (FastMath.abs(moveSpeed) < accel) {
                moveSpeed = 0;

            } else if (moveSpeed > 0) {
                moveSpeed -= (deccel);

            } else if (moveSpeed < 0) {
                moveSpeed += (deccel);

            }
        }
        log("Movespeed = " + moveSpeed * tpf);
        rbc.setLinearVelocity(moveSpeed * tpf, rbc.getLinearVelocity().y);

        rbc.setAngularVelocity(0);
        rbc.setPhysicRotation(0);

    }

}
