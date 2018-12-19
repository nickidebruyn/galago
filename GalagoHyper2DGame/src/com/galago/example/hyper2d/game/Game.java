/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper2d.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.effects.FlowControl;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nicki
 */
public class Game extends SimplePhysics2DGame {

    public static float OUTOFSCREENHEIGHT = 14f;
    public static int MAX_HEALTH = 100;
    private float BACKGROUND_SCALE = 0.025f;
    private Timer waveTimer = new Timer(200);
    private BitmapFont bitmapFont;

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {
        bitmapFont = baseApplication.getAssetManager().loadFont("Interface/Fonts/Roboto-Bold.fnt");

        loadStars("Textures/stars-close.png", -10, 0.1f);
        loadStars("Textures/stars-middle.png", -11, 0.07f);
        loadStars("Textures/stars-far.png", -12, 0.05f);

        loadBackground("Textures/background.png", -15);

        levelNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (isStarted() && !isPaused() && !isGameOver()) {

                    waveTimer.update(tpf);

                    if (waveTimer.finished()) {
                        loadRock(FastMath.nextRandomInt(-4, 4), FastMath.nextRandomInt(1, 10));
                        waveTimer.reset();
                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

    }

    private void loadStars(String texture, float zPos, float flowSpeed) {
        Sprite starsSprite = new Sprite("stars", 480 * BACKGROUND_SCALE, 800 * BACKGROUND_SCALE);
        starsSprite.setImage(texture);
        starsSprite.setQueueBucket(RenderQueue.Bucket.Transparent);

        FlowControl groundFlowControl = new FlowControl(texture, 0f, flowSpeed);
        starsSprite.addControl(groundFlowControl);
        starsSprite.flipCoords(true);
        starsSprite.flipHorizontal(false);
        SpatialUtils.move(starsSprite, 0, 0.1f, zPos);

        addVegetation(starsSprite);
        groundFlowControl.getMaterial().setFloat("AlphaDiscardThreshold", 0.0f);

    }

    private void loadBackground(String texture, float zPos) {
        Sprite starsSprite = new Sprite("background", 480 * BACKGROUND_SCALE, 800 * BACKGROUND_SCALE);
        starsSprite.setImage(texture);
//        starsSprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        SpatialUtils.move(starsSprite, 0, 0.1f, zPos);
        addVegetation(starsSprite);

    }

    private void loadRock(float xPos, int health) {

        float size = 2f;
        Sprite rock = new Sprite("rock", size, size);
        rock.setImage(getRandomRockTexture());
//        rock.setQueueBucket(RenderQueue.Bucket.Transparent);
        rock.getMaterial().setFloat("AlphaDiscardThreshold", 0.55f);        
        rock.setLocalTranslation(xPos, OUTOFSCREENHEIGHT, 0);

        CollisionShape collisionShape = new CircleCollisionShape(size * 0.5f);
        RigidBodyControl rbc = new RigidBodyControl(collisionShape, 1);
        rbc.setGravityScale(0);
        rock.addControl(rbc);

        addObstacle(rock);

        rbc.setLinearVelocity(0.1f, -(6f-(5f*((float)health/(float)MAX_HEALTH))));
        rbc.setAngularVelocity(FastMath.nextRandomFloat() * FastMath.nextRandomInt(-2, 2));
        rbc.setPhysicLocation(xPos, OUTOFSCREENHEIGHT);
        
        //Add text
        BitmapText text = new BitmapText(bitmapFont);
        text.setText(health + "");
        text.setSize(0.4f);
        text.setColor(ColorRGBA.White);
        text.setLocalTranslation(-0.2f, 0.3f, 0.2f);
        text.setQueueBucket(RenderQueue.Bucket.Transparent);
        levelNode.attachChild(text);

        rock.addControl(new ObstacleControl(this, text, health));

    }

    public BitmapFont getBitmapFont() {
        return bitmapFont;
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
        super.start(physicsPlayer); //To change body of generated methods, choose Tools | Templates.

        waveTimer.start();
    }

}
