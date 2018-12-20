/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.platform2d.Platform2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.util.SpriteUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class SuperBrosPlayer extends Platform2DPlayer implements PhysicsCollisionListener, PhysicsTickListener {

    private Sprite body;
    private Sprite face;
    private RigidBodyControl rigidBodyControl;
    private float jumpForce = 8.0f;
    private boolean onGround = false;
    private boolean left, right = false;
    private float moveSpeed = 0.6f;
    private float moverSpeed = 4f;
    private float scale = 1f;
    private Timer jumpDelayTimer = new Timer(0.5f);
    private boolean moverActive;
    private Vector3f moverDirection;
    private float gravity = 3f;

    public SuperBrosPlayer(Platform2DGame platform2DGame) {
        super(platform2DGame);
    }

    @Override
    protected void init() {
        lives = 0;

        body = new Sprite(Platform2DGame.TYPE_PLAYER, Game.TILE_SIZE * 0.9f, Game.TILE_SIZE * 0.9f);
        SpriteUtils.addColor(body, ColorRGBA.Orange, true);
        body.setQueueBucket(RenderQueue.Bucket.Transparent);
        body.move(0, 0, 1f);

        rigidBodyControl = new RigidBodyControl(new CircleCollisionShape(body.getWidth() * 0.5f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        playerNode.addControl(rigidBodyControl);
        playerNode.attachChild(body);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        rigidBodyControl.setPhysicLocation(new Vector3f(startPosition.x, startPosition.y, 0f));
        rigidBodyControl.setGravityScale(gravity);

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                jumpDelayTimer.update(tpf);

                if (rigidBodyControl != null) {

                    if (scale < 1f) {
                        scale += (tpf * 2f);
                        body.setLocalScale(1, scale, 1);

                        if (scale > 1f) {
                            scale = 1f;
                        }
                    }

                    if (getPosition().y < -10f) {
                        doDamage(10);
                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        face = new Sprite(Platform2DGame.TYPE_PLAYER, body.getWidth() * 0.95f, body.getHeight() * 0.95f);
        face.setImage("Textures/player/face1.png");
        face.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
        face.setQueueBucket(RenderQueue.Bucket.Transparent);
        face.move(0.1f, 0, 0.1f);
        body.attachChild(face);

        //Add the collision listener
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

    }

    @Override
    public Vector3f getPosition() {
        return rigidBodyControl.getPhysicLocation().clone();
    }

    @Override
    public void doDie() {
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(playerNode);
        game.getBaseApplication().getEffectManager().doEffect("die", getPosition());

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsCollisionListener(this);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);

        Tween.to(body, SpatialAccessor.SCALE_XYZ, 0.15f)
                .target(0, 0, 0)
                .delay(0.005f)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                    }
                })
                .start(game.getBaseApplication().getTweenManager());

        rigidBodyControl.clearForces();

    }

    public void jump(float extrajumpForcePer) {

        if (onGround && !game.isGameOver() && !moverActive) {
            scale = 0.8f;
            rigidBodyControl.clearForces();
            float jumpAmount = jumpForce * extrajumpForcePer;
            rigidBodyControl.applyImpulse(0, jumpAmount);
            game.getBaseApplication().getSoundManager().setSoundPitch("jump", 1f + FastMath.nextRandomInt(1, 100) * 0.01f);
            game.getBaseApplication().getSoundManager().playSound("jump");
//            doJumpFace();
            onGround = false;
            jumpDelayTimer.reset();
        }

    }

    public void bounce() {
        onGround = true;
        jump(0.6f);
    }

    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f point) {
        float dist = getPosition().distance(point.multLocal(1, 1, 0));
//        log("Point: " + point + ";  Player: " + getPosition());
//        log("On Gound dist: " + dist);
        if (point.y <= getPosition().y - dist) {

            if (jumpDelayTimer.finished()) {
                onGround = true;
            }

        }

    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isGoingDown() {
        return rigidBodyControl.getLinearVelocity().y < 0.1f && rigidBodyControl.getLinearVelocity().y > -0.1f;
    }

    @Override
    public void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        jumpDelayTimer.start();

    }

    public boolean isFacingForward() {
        return !body.isHorizontalFlipped();
    }

    public int getLives() {
        return lives;
    }

    @Override
    protected float getSize() {
        return 0.45f;
    }

    public void moveLeft(float moveSpeed) {
        left = true;
        right = false;
        this.moveSpeed = moveSpeed;

        if (this.left && !right) {
//            sprite.flipHorizontal(true);
            face.setLocalTranslation(-0.1f, 0, 0.1f);
        }
    }

    public void moveRight(float moveSpeed) {
        left = false;
        right = true;
        this.moveSpeed = moveSpeed;
        if (!this.left && right) {
//            sprite.flipHorizontal(false);
            face.setLocalTranslation(0.1f, 0, 0.1f);
        }
    }

    public void moveCancel() {
        left = false;
        right = false;
        moveSpeed = 0;
    }

    public void doLevelCompleteAction() {
//        doSmileFace();
//        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(playerNode);
        rigidBodyControl.clearForces();

        Tween.to(body, SpatialAccessor.SCALE_XYZ, 0.5f)
                .target(0, 0, 0)
                .delay(0.05f)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                    }
                })
                .start(game.getBaseApplication().getTweenManager());
    }

    public void transportToPosition(Vector3f pos) {
        rigidBodyControl.clearForces();
        rigidBodyControl.setPhysicLocation(pos.x, pos.y);
    }

    public void setMoverActive(Vector3f direction) {
        this.moverActive = true;
        this.moverDirection = direction;
        this.rigidBodyControl.setGravityScale(0);
        rigidBodyControl.setAngularVelocity(0);
        rigidBodyControl.setLinearVelocity(0, 0);

    }

    public void setMoverInactive() {
        this.moverActive = false;
        this.moverDirection = new Vector3f(0, 0, 0);
        this.rigidBodyControl.setGravityScale(gravity);

    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {

    }

    public void physicsTick(PhysicsSpace space, float tpf) {

        if (moverActive) {
            rigidBodyControl.move(moverDirection.x * moverSpeed * tpf, moverDirection.y * moverSpeed * tpf);

        } else if (left && !right) {
            rigidBodyControl.move(-tpf * moveSpeed, 0);

        } else if (!left && right) {
            rigidBodyControl.move(tpf * moveSpeed, 0);

        }

        //Correct the rotation so that the player don;t twist
        rigidBodyControl.getBody().setAngularVelocity(0);
        rigidBodyControl.getBody().setAngularDamping(0);
        rigidBodyControl.getBody().getTransform().setRotation(0);

    }
}
