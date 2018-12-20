/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.enemies;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;

/**
 *
 * @author NideBruyn
 */
public class EnemyControl extends AbstractControl {

    private Game game;
    private AnimatedSprite animatedSprite;
    private RigidBodyControl rigidBodyControl;
    private float moveSpeed;
    private boolean forward = true;
    private Timer walkTimer = new Timer(300);
    private int health = 1;
    private Timer recoverTimer = new Timer(25);
    private boolean recovered = true;
    private Timer directionChangeTimer = new Timer(50);
    private Timer walkSoundTimer = new Timer(1000);
    private boolean changeDirection = false;
    private boolean destroy = false;

    public EnemyControl(Game game, AnimatedSprite animatedSprite, float speed) {
        this.game = game;
        this.animatedSprite = animatedSprite;
        this.moveSpeed = speed;
    }

    public Vector3f getLocation() {
        return rigidBodyControl.getPhysicLocation();
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isPaused()) {

            //First we need to get this
            if (rigidBodyControl == null) {
                rigidBodyControl = animatedSprite.getControl(RigidBodyControl.class);
                animatedSprite.play("idle", true, false, false);
                walkTimer.start();
                directionChangeTimer.start();
                walkSoundTimer.start();
//                game.getBaseApplication().getSoundManager().playSound("zombiewalk");
            }

            //Now we can execute the movement logic
            if (rigidBodyControl != null) {

                walkTimer.update(tpf);
                if (walkTimer.finished()) {
                    doChangeDirection();
                    walkTimer.reset();
                }

                if (forward) {
                    rigidBodyControl.move(tpf * moveSpeed, 0);
                    animatedSprite.play("move", true, false, false);
                } else {
                    rigidBodyControl.move(-tpf * moveSpeed, 0);
                    animatedSprite.play("move", true, false, false);
                }

                //Play walk sound
                walkSoundTimer.update(tpf);

                if (walkSoundTimer.finished()) {
//                    game.getBaseApplication().getSoundManager().playSound("zombiewalk");
                    walkSoundTimer.reset();
                }


                if (rigidBodyControl.getPhysicLocation().y < -10f) {
                    doDie();

                }

                //Recover the zombie to be hitable
                recoverTimer.update(tpf);
                if (recoverTimer.finished()) {
                    recovered = true;
                    recoverTimer.stop();
                }

                directionChangeTimer.update(tpf);

                //Check to change direction
                if (changeDirection) {
                    if (directionChangeTimer.finished()) {
                        forward = !forward; // change the direction

                        if (forward) {
                            animatedSprite.flipHorizontal(false);
                        } else {
                            animatedSprite.flipHorizontal(true);
                        }

                        directionChangeTimer.reset();
                    }
                    changeDirection = false;
                }
            }
        }

        if (rigidBodyControl != null) {
            //Correct the rotation so that the player don;t twist
            rigidBodyControl.getBody().setAngularVelocity(0);
            rigidBodyControl.getBody().setAngularDamping(0);
            rigidBodyControl.getBody().getTransform().setRotation(0);


            if (destroy) {
                game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);                
                destroy = false;
                Tween.to(animatedSprite, SpatialAccessor.SCALE_XYZ, 0.4f)
                        .target(0, 0, 1)
                        .ease(Bounce.OUT)
                        .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        animatedSprite.removeFromParent();
                    }
                }).start(game.getBaseApplication().getTweenManager());
            }
        }

    }

    public void doChangeDirection() {
        this.changeDirection = true;

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void doDie() {
        System.out.println(" ********* Removing enemy");
        animatedSprite.play("die", true, false, false);
        destroy = true;


    }

    public void doHit() {

        if (recovered) {
            health--;
            if (health <= 0) {
                doDie();
//                game.addKill();
            }

            recoverTimer.reset();
            recovered = false;
//            game.getBaseApplication().getSoundManager().playSound("zombiehit");
//            game.getBaseApplication().getEffectManager().doEffect("zombiehit", rigidBodyControl.getPhysicLocation().clone().addLocal(0, 0.8f, 0f), 200);
        }

    }

    public boolean isAlive() {
        return health > 0;
    }

    public void doEat() {
        animatedSprite.play("eat", true, false, false);
    }
}
