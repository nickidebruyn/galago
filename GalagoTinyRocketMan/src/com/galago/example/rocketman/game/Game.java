/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.rocketman.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nicki
 */
public class Game extends SimplePhysics2DGame {

    public static float OUTOFSCREENHEIGHT = -5f;
    public static int MAX_HEALTH = 100;
    private float BACKGROUND_SCALE = 0.025f;
    private Timer waveTimer = new Timer(100);

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {

        levelNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (isStarted() && !isPaused() && !isGameOver()) {

                    waveTimer.update(tpf);

                    if (waveTimer.finished()) {
                        loadRock(FastMath.nextRandomInt(-8, 8));
                        waveTimer.reset();
                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

    }

    private void loadRock(float xPos) {

        float size = 1f;
        Sprite rock = new Sprite("rock", size, size);
        rock.setImage(getRandomRockTexture());
        rock.getMaterial().setFloat("AlphaDiscardThreshold", 0.55f);
        rock.setLocalTranslation(xPos, OUTOFSCREENHEIGHT, 0);

        CollisionShape collisionShape = new CircleCollisionShape(size * 0.5f);
        RigidBodyControl rbc = new RigidBodyControl(collisionShape, 1);
        rbc.setGravityScale(1);
        rock.addControl(rbc);

        addObstacle(rock);

        rbc.setLinearVelocity((float)FastMath.nextRandomInt(-2, 2), 15f);
        rbc.setAngularVelocity(FastMath.nextRandomFloat() * FastMath.nextRandomInt(-2, 2));
        rbc.setPhysicLocation(xPos, OUTOFSCREENHEIGHT);

    }

    protected String getRandomRockTexture() {
        int index = FastMath.nextRandomInt(0, 1);
        String img = null;
        if (index == 0) {
            img = "Textures/rock1.png";
        } else {
            img = "Textures/rock2.png";

        }
        return img;
    }

    @Override
    public void start(SimplePhysics2DPlayer physicsPlayer) {
        super.start(physicsPlayer);
        waveTimer.start();
    }

}
