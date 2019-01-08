/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.game;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;

/**
 *
 * @author nicki
 */
public class Game extends BasicGame {

    public static final String PLATFORM = "platform";
    public static final String CUBE = "cube";

    public static final String CUBE_TYPE = "cube_type";
    public static final String CUBE_TYPE_1 = "cube_type_1";
    public static final String CUBE_TYPE_2 = "cube_type_2";
    public static final String CUBE_TYPE_3 = "cube_type_3";
    public static final String CUBE_TYPE_4 = "cube_type_4";
    public static final String CUBE_TYPE_5 = "cube_type_5";
    public static final String CUBE_TYPE_6 = "cube_type_6";

    private float blocksize = 0.94f;
    private float floorsize = 0.98f;
    private float spacing = 1f;
    private Vector3f sunDirection = new Vector3f(0.2f, -0.3f, -0.4f);
    private float cubeStartHeight = spacing * 6;
    private float cubeMaxHeight = spacing * 3;

    private float BACKGROUND_SCALE = 0.02f;
    private Node cubesNode;
    private boolean playing = false;

    private ColorRGBA PLATFORM_COLOR = ColorUtils.rgb(149, 175, 192);
    private ColorRGBA CUBE_COLOR1 = ColorUtils.rgb(249, 202, 36);
    private ColorRGBA CUBE_COLOR2 = ColorUtils.rgb(235, 77, 75);
    private ColorRGBA CUBE_COLOR3 = ColorUtils.rgb(106, 176, 76);
    private ColorRGBA CUBE_COLOR4 = ColorUtils.rgb(190, 46, 221);
    private ColorRGBA CUBE_COLOR5 = ColorUtils.rgb(72, 52, 212);
    private ColorRGBA CUBE_COLOR6 = ColorUtils.rgb(19, 15, 64);

    public Game(BaseApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {

//        loadBackground("Textures/background.png", -15);
        cubesNode = new Node("cubenode");
        levelNode.attachChild(cubesNode);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                addPlatform(x * spacing, z * spacing);
            }
        }

        initLight(ColorRGBA.DarkGray, ColorRGBA.LightGray, sunDirection);

    }

    private void loadBackground(String texture, float zPos) {
        Sprite backgroundSprite = new Sprite("background", 480 * BACKGROUND_SCALE, 800 * BACKGROUND_SCALE);
        backgroundSprite.setImage(texture);
//        SpatialUtils.rotate(backgroundSprite, 0, -45f, 0);
        backgroundSprite.setQueueBucket(RenderQueue.Bucket.Sky);
        SpatialUtils.move(backgroundSprite, 0f, backgroundSprite.getHeight() / 4f, 0f);
        BillboardControl bc = new BillboardControl();
        backgroundSprite.addControl(bc);
        levelNode.attachChild(backgroundSprite);

    }

    private void addPlatform(float x, float z) {
        Spatial platform = SpatialUtils.addBox(levelNode, floorsize / 2f, floorsize / 20f, floorsize / 2f);
        platform.setName(PLATFORM);
        SpatialUtils.addColor(platform, PLATFORM_COLOR, false);
        SpatialUtils.move(platform, x, -(blocksize / 2) - (floorsize / 20f), z);

    }

    public void addCube(String type, float x, float z) {
        
        Vector3f targetPos = getTargetYPosition(x, z);

        if (targetPos != null) {
            playing = true;
            Spatial cube = SpatialUtils.addBox(cubesNode, blocksize / 2f, blocksize / 2f, blocksize / 2f);
            cube.setName(CUBE);
            cube.setUserData(CUBE_TYPE, type);
            SpatialUtils.addColor(cube, getCubeColor(type), false);
            SpatialUtils.move(cube, x, cubeStartHeight, z);

            Tween.to(cube, SpatialAccessor.POS_XYZ, 1.2f)
                    .target(targetPos.x, targetPos.y, targetPos.z)
                    .ease(Bounce.OUT)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> bt) {
                            playing = false;
                        }
                    })
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    public ColorRGBA getCubeColor(String type) {
        ColorRGBA color = ColorRGBA.Black;

        if (type != null) {

            if (type.equals(CUBE_TYPE_1)) {
                color = CUBE_COLOR1;

            } else if (type.equals(CUBE_TYPE_2)) {
                color = CUBE_COLOR2;

            } else if (type.equals(CUBE_TYPE_3)) {
                color = CUBE_COLOR3;

            } else if (type.equals(CUBE_TYPE_4)) {
                color = CUBE_COLOR4;

            } else if (type.equals(CUBE_TYPE_5)) {
                color = CUBE_COLOR5;

            } else if (type.equals(CUBE_TYPE_6)) {
                color = CUBE_COLOR6;

            }

        }

        return color;
    }

    public Vector3f getTargetYPosition(float x, float z) {
        Vector3f targetPos = new Vector3f(x, 0, z);

        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            Spatial cube = cubesNode.getChild(i);

            if (cube.getWorldTranslation().x == x
                    && cube.getWorldTranslation().z == z
                    && cube.getWorldTranslation().y < cubeMaxHeight) {

//                targetPos = new Vector3f(x, 0, z);
                log("Found cube at: " + cube.getWorldTranslation());

                if (cube.getWorldTranslation().y >= targetPos.y) {
                    targetPos = new Vector3f(x, cube.getWorldTranslation().y + spacing, z);
                    log("Set new target pos: " + targetPos);

                }
            }
        }

        if (targetPos.y >= cubeMaxHeight) {
            targetPos = null;
            log("Max height reached...");
        }

        return targetPos;
    }

    public boolean isPlaying() {
        return playing;
    }

}
