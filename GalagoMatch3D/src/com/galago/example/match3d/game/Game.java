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
import aurelienribon.tweenengine.equations.Elastic;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.games.basic.BasicPlayer;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

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

    public static final int PLAYER_LEVEL_2_RANGE = 50;
    public static final int PLAYER_LEVEL_3_RANGE = 150;

    private GameProgressListener gameProgressListener;

    private float blocksize = 0.94f;
    private float floorsize = 0.98f;
    private float spacing = 1f;
    private Vector3f sunDirection = new Vector3f(0.2f, -0.3f, -0.4f);
    private float cubeStartHeight = spacing * 6;
    private float cubeMaxHeight = spacing * 3;

    private float BACKGROUND_SCALE = 0.02f;
    private Node cubesNode;
    private Spatial borderMarker;
    private Spatial sky;
    private Material skyMaterial;
    private boolean playing = false;
    private boolean matchesFound = false;
    private boolean rotating = false;
    private float angle = 0;
    private int boosterCount = 0;
    private int playerLevel = 1;
    private int cubePlacementCount = 0;
    private int savedScore = 0;

    private Timer matchedCubeDisposeTimer = new Timer(80);
    private Timer gravityTimer = new Timer(100);

    private ColorRGBA PLATFORM_COLOR = ColorUtils.rgb(100, 100, 100);
//    private ColorRGBA CUBE_COLOR1 = ColorRGBA.Red;
//    private ColorRGBA CUBE_COLOR2 = ColorRGBA.Green;
//    private ColorRGBA CUBE_COLOR3 = ColorRGBA.Blue;
//    private ColorRGBA CUBE_COLOR4 = ColorRGBA.White;
//    private ColorRGBA CUBE_COLOR5 = ColorRGBA.Yellow;
//    private ColorRGBA CUBE_COLOR6 = ColorRGBA.Magenta;

    //American
    private ColorRGBA CUBE_COLOR1 = ColorUtils.rgb(214, 48, 49); //ColorRGBA.Red;
    private ColorRGBA CUBE_COLOR2 = ColorUtils.rgb(0, 184, 148);//ColorRGBA.Green;
    private ColorRGBA CUBE_COLOR3 = ColorUtils.rgb(9, 132, 227);//ColorRGBA.Blue;
    private ColorRGBA CUBE_COLOR4 = ColorUtils.rgb(253, 203, 110);//ColorRGBA.Yellow;
    private ColorRGBA CUBE_COLOR5 = ColorUtils.rgb(155, 89, 182);//ColorRGBA.Purple;
    private ColorRGBA CUBE_COLOR6 = ColorUtils.rgb(255, 255, 255);//ColorRGBA.white;
    private Spatial skyParticles;

    public Game(BaseApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    public void addGameProgressListener(GameProgressListener gameProgressListener) {
        this.gameProgressListener = gameProgressListener;
    }

    private void fireGameProgressLevelUp(int level) {
        if (this.gameProgressListener != null) {
            this.gameProgressListener.doLevelUp(level);
        }
    }

    private void fireGameProgressScoreBooster(int score) {
        if (this.gameProgressListener != null) {
            this.gameProgressListener.doScoreBooster(score);
        }
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

        //Load border marker
//        borderMarker = SpatialUtils.addBox(rootNode, spacing*1.5f, spacing*1.5f, spacing*1.5f);
//        borderMarker.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/marker.j3m"));
//        borderMarker.move(0, spacing*1.5f-(spacing*0.5f), 0);
        initLight(ColorRGBA.Gray, ColorRGBA.LightGray, sunDirection);

        ColorRGBA colorRGBA = ColorUtils.hsv(0.0f, 0.75f, .9f);
        sky = SpatialUtils.addSkySphere(levelNode, colorRGBA, PLATFORM_COLOR, baseApplication.getCamera());
        skyMaterial = ((Geometry) sky).getMaterial();
        refreshSkyColor();

        //Load the sky particles
        skyParticles = baseApplication.getAssetManager().loadModel("Models/Effects/sky-particles.j3o");
        skyParticles.move(0, -8, 0);
        levelNode.attachChild(skyParticles);

//        SpatialUtils.addSkySphere(levelNode, 6, baseApplication.getCamera());
        levelNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (isStarted() && !isPaused()) {

                    //Matched cube dispose timer will determine if the matched cubes was disposed.
                    matchedCubeDisposeTimer.update(tpf);
                    if (matchedCubeDisposeTimer.finished()) {

//                        log("############  SEARCHING MATCHED CUBES ###########");
                        //When dispose timer is done remove from parent
                        ArrayList<Spatial> cubestoRemove = new ArrayList<>();
                        for (int i = 0; i < cubesNode.getQuantity(); i++) {
                            final Spatial cube = cubesNode.getChild(i);
//                            log("Cube matched indicator " + cube.getUserData(MATCHED));
                            if (cube.getUserData(MATCHED) != null) {
//                                log("Remove cube " + cube.getUserData(CUBE_TYPE));
                                cubestoRemove.add(cube);
                            }
                        }

                        //Now we remove the cubes
                        if (cubestoRemove.size() > 0) {
                            for (int i = 0; i < cubestoRemove.size(); i++) {
                                Spatial cube = cubestoRemove.get(i);
                                cube.removeFromParent();
                            }

                            fireGameProgressScoreBooster(boosterCount);

                            //Calculate scores
                            if (cubestoRemove.size() == 3) {
                                player.addScore(1);

                            } else if (cubestoRemove.size() >= 5 && cubestoRemove.size() < 9) {
                                player.addScore(2);

                            } else if (cubestoRemove.size() >= 9) {
                                player.addScore(3);

                            }

                            //Fire a booster score of 5 if the player cleared the board.
                            log("Cubes left = " + cubesNode.getQuantity());
                            if (boosterCount >= 3 && cubesNode.getQuantity() == 0) {
                                fireGameProgressScoreBooster(5);
                            }

                            refreshSkyColor();

                            updatePlayerLevel();

                        }

//                        log("Cubes left on board: " + cubesNode.getQuantity());
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

    private void updatePlayerLevel() {
        if (playerLevel == 1 && cubePlacementCount >= PLAYER_LEVEL_2_RANGE) {
            playerLevel++;
            fireGameProgressLevelUp(playerLevel);

        } else if (playerLevel == 2 && cubePlacementCount >= PLAYER_LEVEL_3_RANGE) {
            playerLevel++;
            fireGameProgressLevelUp(playerLevel);
        }
    }

    private void refreshSkyColor() {
        float val = 0;
        if (player != null) {
            val = (float) player.getScore();
        }
        if (val >= 180f) {
            val = 180f;
        }

        float scaler = 1f - (0.1f + (float) val / 200f);
        ColorRGBA colorRGBA = ColorUtils.hsv(scaler, 0.5f, .9f);
        baseApplication.getViewPort().setBackgroundColor(colorRGBA);
        skyMaterial.setColor("EndColor", colorRGBA);
    }

    public int getPlayerLevel() {
//        int level = 1;
//        if (player != null) {
//            if (player.getScore() >= PLAYER_LEVEL_2_RANGE) {
//                level = 2;
//            }
//
//            if (player.getScore() >= PLAYER_LEVEL_3_RANGE) {
//                level = 3;
//            }
//        }
//        return level;
        return playerLevel;
    }

    private void addPlatform(float x, float z) {
        Spatial platform = SpatialUtils.addBox(levelNode, floorsize / 2f, floorsize / 30f, floorsize / 2f);
        platform.setName(PLATFORM);
        SpatialUtils.addColor(platform, PLATFORM_COLOR, false);
        SpatialUtils.move(platform, x, -(blocksize / 2) - (floorsize / 30f), z);

    }

    
    public Spatial loadCube(String type, ColorRGBA colorRGBA, float x, float y, float z) {
        Spatial cube = SpatialUtils.addBox(cubesNode, blocksize / 2f, blocksize / 2f, blocksize / 2f);
        cube.setName(CUBE);
        cube.setUserData(CUBE_TYPE, type);
        
        Material m = baseApplication.getAssetManager().loadMaterial("Materials/cube.j3m");
        m.setColor("Ambient", colorRGBA);
        m.setColor("Diffuse", colorRGBA);
        cube.setMaterial(m);

        cube.setLocalTranslation(x, y, z);

        return cube;
    }

    public boolean addCube(String type, float x, float z) {

        //Check the range
        if (x > 1 || x < -1 || z > 1 || z < -1) {
            return false;
        }

        //Only continue if above is true
        final Vector3f targetPos = getTargetYPosition(x, z);
        matchesFound = false;
        boosterCount = 0;

        if (targetPos != null) {
            playing = true;
            final ColorRGBA cubeColor = getCubeColor(type);
            final Spatial cube = loadCube(type, cubeColor, x, cubeStartHeight, z);
            
            baseApplication.getSoundManager().playSound("drop");
            cubePlacementCount++;

            //Create wobble effect
            final float wobbleAmount = 0.2f;
            final float fallTime = 1f;
            Tween.to(cube, SpatialAccessor.SCALE_XYZ, fallTime * 0.35f)
                    .target(1f - wobbleAmount, 1.0f + wobbleAmount, 1f - wobbleAmount)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> bt) {

                            //Do collide effect
                            //TODO: Sound
                            baseApplication.getSoundManager().playSoundRandomPitch("place");

                            baseApplication.getEffectManager().prepareColor(cubeColor, cubeColor);
                            baseApplication.getEffectManager().doEffect("cube-place", targetPos.subtract(0, spacing / 2, 0));

                            //Do wobble wide
                            Tween.to(cube, SpatialAccessor.SCALE_XYZ, fallTime * 0.2f)
                                    .target(1f + wobbleAmount, 1.0f - wobbleAmount, 1f + wobbleAmount)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> bt) {

                                            //Reset to default
                                            Tween.to(cube, SpatialAccessor.SCALE_XYZ, fallTime * 0.2f)
                                                    .target(1f, 1f, 1f)
                                                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
                                        }
                                    })
                                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

                        }
                    })
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

            Tween.to(cube, SpatialAccessor.POS_XYZ, fallTime)
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

        //Loop over all the cubes and check for possible matches
        //If a match occurred then mark the cubes as matched
        for (int i = 0; i < cubesNode.getQuantity(); i++) {
            Spatial cube = cubesNode.getChild(i);
//            if (cube.getUserData(MATCHED) == null) {
            Spatial cubeLeft = getCubeOfSameTypeRelativeTo(cube, -spacing, 0, 0);
            Spatial cubeRight = getCubeOfSameTypeRelativeTo(cube, spacing, 0, 0);
            Spatial cubeFront = getCubeOfSameTypeRelativeTo(cube, 0, 0, -spacing);
            Spatial cubeBack = getCubeOfSameTypeRelativeTo(cube, 0, 0, spacing);
            Spatial cubeUp = getCubeOfSameTypeRelativeTo(cube, 0, spacing, 0);
            Spatial cubeDown = getCubeOfSameTypeRelativeTo(cube, 0, -spacing, 0);

//                if (cubeUp != null && cubeDown != null) {
//                    matchesFound = true;
//                    cube.setUserData(MATCHED, "true");
//                    cubeUp.setUserData(MATCHED, "true");
//                    cubeDown.setUserData(MATCHED, "true");
//
//                } else 
            if (cubeLeft != null && cubeRight != null) {
                matchesFound = true;
                cube.setUserData(MATCHED, "true");
                cubeLeft.setUserData(MATCHED, "true");
                cubeRight.setUserData(MATCHED, "true");

            } else if (cubeFront != null && cubeBack != null) {
                matchesFound = true;
                cube.setUserData(MATCHED, "true");
                cubeFront.setUserData(MATCHED, "true");
                cubeBack.setUserData(MATCHED, "true");

            }
//            }

        }

        if (matchesFound) {
            boosterCount++;
            animateMatchedCubesDisposal();
            matchedCubeDisposeTimer.reset();

        } else {
            checkIfGameOver();
            playing = false;
        }

    }

    private void checkIfGameOver() {
        if (cubesNode.getQuantity() >= 27) {
            doGameOver();
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

                baseApplication.getSoundManager().playSoundRandomPitch("pop");

                Tween.to(cube, SpatialAccessor.SCALE_XYZ, 0.4f)
                        .target(0, 0, 0)
                        .ease(Elastic.OUT)
                        .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

                ColorRGBA colorRGBA = getCubeColor((String) cube.getUserData(CUBE_TYPE));

                baseApplication.getEffectManager().prepareMaterialColor(colorRGBA);
                baseApplication.getEffectManager().doEffect("cube-destroy", cube.getWorldTranslation().clone());

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
            Spatial cubeDown2 = getCubeRelativeTo(cube, 0, -spacing * 2, 0);

            if (cubeDown == null && cubeDown2 == null) {
                targetPos = new Vector3f(cube.getWorldTranslation().x, 0, cube.getWorldTranslation().z);

            } else if (cubeDown == null && cubeDown2 != null) {
                targetPos = new Vector3f(cube.getWorldTranslation().x, 1, cube.getWorldTranslation().z);

            } else if (cubeDown != null && cubeDown2 == null) {
                targetPos = new Vector3f(cube.getWorldTranslation().x, 1, cube.getWorldTranslation().z);

            }

            if (targetPos != null) {
                gravityApplied = true;
                Tween.to(cube, SpatialAccessor.POS_XYZ, 0.6f)
                        .target(targetPos.x, targetPos.y, targetPos.z)
                        .ease(Bounce.OUT)
                        .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

            }
        }

        if (gravityApplied) {
            gravityTimer.reset();

        } else {
            checkIfGameOver();
            playing = false;
        }
    }

    public String getRandomCubeType() {

        int max = 4;
        int playerlevel = getPlayerLevel();
        if (playerlevel == 2) {
            max = 5;
        } else if (playerlevel >= 3) {
            max = 6;
        }

        int index = FastMath.nextRandomInt(1, max);
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

    @Override
    public void start(BasicPlayer physicsPlayer) {
        super.start(physicsPlayer);
    
        refreshSkyColor();
    }

    public void refreshGame() {        
        refreshSkyColor();
        
    }

    public Node getCubesNode() {
        return cubesNode;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public int getCubePlacementCount() {
        return cubePlacementCount;
    }

    public void setCubePlacementCount(int cubePlacementCount) {
        this.cubePlacementCount = cubePlacementCount;
    }

}
