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
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;

/**
 *
 * @author nicki
 */
public class Game extends BasicGame {

    public static final String PLATFORM = "platform";
    public static final String CUBE = "cube";
    public static final String MATCHED = "matched";

    public static final String CUBE_TYPE = "cube_type";
    public static final String CUBE_TYPE_1 = "cube_type_1";
    public static final String CUBE_TYPE_2 = "cube_type_2";
    public static final String CUBE_TYPE_3 = "cube_type_3";
    public static final String CUBE_TYPE_4 = "cube_type_4";
    public static final String CUBE_TYPE_5 = "cube_type_5";
    public static final String CUBE_TYPE_6 = "cube_type_6";

    private float blocksize = 0.96f;
    private float floorsize = 0.98f;
    private float spacing = 1f;
    private Vector3f sunDirection = new Vector3f(0.2f, -0.3f, -0.4f);
    private float cubeStartHeight = spacing * 6;
    private float cubeMaxHeight = spacing * 3;

    private float BACKGROUND_SCALE = 0.02f;
    private Node cubesNode;
    private boolean playing = false;
    private boolean matchesFound = false;

    private Timer matchedCubeDisposeTimer = new Timer(120);
    private Timer gravityTimer = new Timer(120);

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

        SpatialUtils.addSkySphere(levelNode, 6, baseApplication.getCamera());

        levelNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (isStarted() && !isPaused()) {

                    //Matched cube dispose timer will determine if the matched cubes was disposed.
                    matchedCubeDisposeTimer.update(tpf);
                    if (matchedCubeDisposeTimer.finished()) {

                        //When dispose timer is done remove from parent
                        for (int i = 0; i < cubesNode.getQuantity(); i++) {
                            final Spatial cube = cubesNode.getChild(i);
                            if (cube.getUserData(MATCHED) != null) {
                                cube.removeFromParent();
                            }
                        }

                        matchesFound = false;
                        applyGravity();
                        matchedCubeDisposeTimer.stop();
                    }

                    //Gravity timer will determine if we can matched cubes again.
                    gravityTimer.update(tpf);
                    if (gravityTimer.finished()) {
                        matchCubes();
                        gravityTimer.stop();
                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

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
        Spatial platform = SpatialUtils.addBox(levelNode, floorsize / 2f, floorsize / 30f, floorsize / 2f);
        platform.setName(PLATFORM);
        SpatialUtils.addColor(platform, PLATFORM_COLOR, false);
        SpatialUtils.move(platform, x, -(blocksize / 2) - (floorsize / 30f), z);

    }

    public boolean addCube(String type, float x, float z) {

        Vector3f targetPos = getTargetYPosition(x, z);
        matchesFound = false;

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
                            matchCubes();
                        }
                    })
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

            return true;
        } else {
            return false;
        }
    }

    private Spatial getCubeOfSameTypeRelativeTo(Spatial origin, float x, float y, float z) {
        Spatial found = null;

        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            Spatial cube = cubesNode.getChild(i);
            if (!cube.equals(origin) && cube.getUserData(CUBE_TYPE).equals(origin.getUserData(CUBE_TYPE))) {

                if (cube.getWorldTranslation().x == (origin.getWorldTranslation().x + x)
                        && cube.getWorldTranslation().y == (origin.getWorldTranslation().y + y)
                        && cube.getWorldTranslation().z == (origin.getWorldTranslation().z + z)) {
                    found = cube;
//                    log("Cube found: " + cube.getUserData(CUBE_TYPE));
                    break;
                }

            }

        }

        return found;
    }
    
    private Spatial getCubeRelativeTo(Spatial origin, float x, float y, float z) {
        Spatial found = null;

        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            Spatial cube = cubesNode.getChild(i);
            if (!cube.equals(origin)) {

                if (cube.getWorldTranslation().x == (origin.getWorldTranslation().x + x)
                        && cube.getWorldTranslation().y == (origin.getWorldTranslation().y + y)
                        && cube.getWorldTranslation().z == (origin.getWorldTranslation().z + z)) {
                    found = cube;
                    break;
                }

            }

        }

        return found;
    }

    /**
     * This method will calculate if any 3 matches was found
     */
    private void matchCubes() {
        //TODO: Loop over all the cubes and check for possible matches
        //If a match occurred then mark the cubes as matched
        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            Spatial cube = cubesNode.getChild(i);
            Spatial cubeLeft = getCubeOfSameTypeRelativeTo(cube, -spacing, 0, 0);
            Spatial cubeRight = getCubeOfSameTypeRelativeTo(cube, spacing, 0, 0);
            Spatial cubeFront = getCubeOfSameTypeRelativeTo(cube, 0, 0, -spacing);
            Spatial cubeBack = getCubeOfSameTypeRelativeTo(cube, 0, 0, spacing);
            Spatial cubeUp = getCubeOfSameTypeRelativeTo(cube, 0, spacing, 0);
            Spatial cubeDown = getCubeOfSameTypeRelativeTo(cube, 0, -spacing, 0);

            if (cubeUp != null && cubeDown != null) {
                matchesFound = true;
                cube.setUserData(MATCHED, true);
                cubeUp.setUserData(MATCHED, true);
                cubeDown.setUserData(MATCHED, true);

            } else if (cubeLeft != null && cubeRight != null) {
                matchesFound = true;
                cube.setUserData(MATCHED, true);
                cubeLeft.setUserData(MATCHED, true);
                cubeRight.setUserData(MATCHED, true);

            } else if (cubeFront != null && cubeBack != null) {
                matchesFound = true;
                cube.setUserData(MATCHED, true);
                cubeFront.setUserData(MATCHED, true);
                cubeBack.setUserData(MATCHED, true);

            }
        }

        if (matchesFound) {
            animateMatchedCubesDisposal();
            matchedCubeDisposeTimer.reset();

        } else {
            playing = false;
        }

    }

    /**
     * This method will go over all the cubes and dispose the ones that has been
     * marked as matched
     */
    private void animateMatchedCubesDisposal() {
        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            final Spatial cube = cubesNode.getChild(i);
            if (cube.getUserData(MATCHED) != null) {

                Tween.to(cube, SpatialAccessor.SCALE_XYZ, 0.8f)
                        .target(0, 0, 0)
                        .ease(Bounce.OUT)
                        .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

            }
        }

    }

    /**
     * This method will apply gravity to all cubes on the bord.
     */
    private void applyGravity() {
        
        boolean gravityApplied = false;

        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            final Spatial cube = cubesNode.getChild(i);
            Vector3f targetPos = null;
            Spatial cubeDown = getCubeRelativeTo(cube, 0, -spacing, 0);
            Spatial cubeDown2 = getCubeRelativeTo(cube, 0, -spacing*2, 0);
            
            if (cubeDown == null && cubeDown2 == null) {
                targetPos = new Vector3f(cube.getWorldTranslation().x, 0, cube.getWorldTranslation().z);
                
            } else if (cubeDown == null && cubeDown2 != null) {
                targetPos = new Vector3f(cube.getWorldTranslation().x, 1, cube.getWorldTranslation().z);
                
            } else if (cubeDown != null && cubeDown2 == null) {
                targetPos = new Vector3f(cube.getWorldTranslation().x, 1, cube.getWorldTranslation().z);
                
            }

            if (targetPos != null) {
                gravityApplied = true;
                Tween.to(cube, SpatialAccessor.POS_XYZ, 1.0f)
                    .target(targetPos.x, targetPos.y, targetPos.z)
                    .ease(Bounce.OUT)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
            
            }
        }
        
        if (gravityApplied) {
            log("Apply gravity");
            gravityTimer.reset();
            
        } else {
            playing = false;
        }
    }

    public String getRandomCubeType() {
        int index = FastMath.nextRandomInt(1, 6);
        return "cube_type_" + index;
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
//                log("Found cube at: " + cube.getWorldTranslation());

                if (cube.getWorldTranslation().y >= targetPos.y) {
                    targetPos = new Vector3f(x, cube.getWorldTranslation().y + spacing, z);
//                    log("Set new target pos: " + targetPos);

                }
            }
        }

        if (targetPos.y >= cubeMaxHeight) {
            targetPos = null;
//            log("Max height reached...");
        }

        return targetPos;
    }

    public boolean isPlaying() {
        return playing;
    }

}
