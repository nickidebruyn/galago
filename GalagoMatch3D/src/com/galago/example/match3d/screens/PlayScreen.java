/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.games.basic.BasicGameListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.SharedSystem;
import com.galago.example.match3d.MainApplication;
import com.galago.example.match3d.game.Game;
import com.galago.example.match3d.game.Player;
import com.galago.example.match3d.ui.CubeButton;
import com.galago.example.match3d.ui.PlayButton;
import com.galago.example.match3d.ui.RetryButton;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements BasicGameListener, PickListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private TouchPickListener touchPickListener;
    private Game game;
    private Player player;
    private boolean firstGame = true;

    private FilterPostProcessor fpp;
    private float cameraDistance = 13f;

    private ChaseCamera chaseCamera;

    private Label titleLabel;
    private Label scoreLabel;
    private Label bestLabel;
    private PlayButton playButton;
    private RetryButton retryButton;
    private Panel cubeButtonPanel;
    private CubeButton cubeButton1;
    private CubeButton cubeButton2;
    private CubeButton cubeButton3;
    private Image cubeButtonSelection;

    private CubeButton selectedCubeButton;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        titleLabel = new Label(hudPanel, "Match 33D", 56, 480, 50);
        titleLabel.centerTop(0, 50);

        scoreLabel = new Label(hudPanel, "0", 52);
        scoreLabel.centerAt(0, 300);

        bestLabel = new Label(hudPanel, "BEST: 0", 32);
        bestLabel.centerTop(0, 150);

        playButton = new PlayButton(hudPanel);
        playButton.centerAt(0, -250);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    firstGame = false;
                    game.start(player);
                    showInGameUI();

                }
            }

        });

        retryButton = new RetryButton(hudPanel);
        retryButton.centerAt(0, -250);
        retryButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(NAME);

                }
            }

        });

        cubeButtonPanel = new Panel(hudPanel, null, window.getWidth(), 100);
        hudPanel.add(cubeButtonPanel);

        cubeButton1 = new CubeButton(cubeButtonPanel, Game.CUBE_TYPE_1);
        cubeButton1.centerAt(-100, 0);
        cubeButton1.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    updateSelectedCubeButton(cubeButton1);
                }
            }

        });

        cubeButton2 = new CubeButton(cubeButtonPanel, Game.CUBE_TYPE_2);
        cubeButton2.centerAt(0, 0);
        cubeButton2.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    updateSelectedCubeButton(cubeButton2);
                }
            }

        });

        cubeButton3 = new CubeButton(cubeButtonPanel, Game.CUBE_TYPE_3);
        cubeButton3.centerAt(100, 0);
        cubeButton3.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    updateSelectedCubeButton(cubeButton3);
                }
            }

        });

        cubeButtonSelection = new Image(cubeButtonPanel, "Interface/button-selected.png", 70, 70, true);
        cubeButtonSelection.centerAt(0, 0);
        cubeButtonSelection.addEffect(new WobbleEffect(cubeButtonSelection, 1.05f, 0.5f));

        cubeButtonPanel.centerBottom(0, 10);

        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    private void updateSelectedCubeButton(CubeButton cubeButton) {

        if (cubeButton == null) {
            cubeButtonSelection.hide();
            selectedCubeButton = null;

        } else {
            cubeButtonSelection.show();
            cubeButtonSelection.setPosition(cubeButton.getPosition().x, cubeButton.getPosition().y);
            selectedCubeButton = cubeButton;
        }

    }

    private void reloadCubeButtons() {

        String cubeButton1Type = game.getRandomCubeType();
        ColorRGBA cubeButton1Color = game.getCubeColor(cubeButton1Type);
        cubeButton1.setScale(1f);
        cubeButton1.setBackgroundColor(cubeButton1Color);
        cubeButton1.setName(cubeButton1Type);
        cubeButton1.show();

        String cubeButton2Type = game.getRandomCubeType();
        ColorRGBA cubeButton2Color = game.getCubeColor(cubeButton2Type);
        cubeButton2.setScale(1f);
        cubeButton2.setBackgroundColor(cubeButton2Color);
        cubeButton2.setName(cubeButton2Type);
        cubeButton2.show();

        String cubeButton3Type = game.getRandomCubeType();
        ColorRGBA cubeButton3Color = game.getCubeColor(cubeButton3Type);
        cubeButton3.setScale(1f);
        cubeButton3.setBackgroundColor(cubeButton3Color);
        cubeButton3.setName(cubeButton3Type);
        cubeButton3.show();

    }

    private void checkForCubeButtonReload() {
        if (!cubeButton1.isVisible() && !cubeButton2.isVisible() && !cubeButton3.isVisible()) {
//            log("Reload cube buttons");
            reloadCubeButtons();
            cubeButtonPanel.moveFromToCenter(0, -500, 0, -340, 1f, 0f);

        }
    }

    @Override
    protected void load() {

        selectedCubeButton = null;

        game = new Game(mainApplication, rootNode);
        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);

        if (fpp == null) {

            fpp = new FilterPostProcessor(baseApplication.getAssetManager());
            baseApplication.getViewPort().addProcessor(fpp);

            FXAAFilter fXAAFilter = new FXAAFilter();
            fpp.addFilter(fXAAFilter);

        }

//        camera.setLocation(new Vector3f(-cameraDistance, cameraDistance * 0.8f, cameraDistance));
//        camera.lookAt(new Vector3f(0, cameraDistance * 0.3f, 0), Vector3f.UNIT_Y);
        if (chaseCamera == null) {

            chaseCamera = new ChaseCamera(camera, rootNode, inputManager);
            chaseCamera.setDefaultDistance(cameraDistance);
            chaseCamera.setChasingSensitivity(100);
            chaseCamera.setDefaultHorizontalRotation(135 * FastMath.DEG_TO_RAD);
            chaseCamera.setDefaultVerticalRotation(26 * FastMath.DEG_TO_RAD);
            chaseCamera.setLookAtOffset(new Vector3f(0, 1, 0));
            chaseCamera.setMinVerticalRotation(0);
            chaseCamera.setMaxVerticalRotation(0);
            chaseCamera.setHideCursorOnRotate(false);
            chaseCamera.setRotationSpeed(6);
            chaseCamera.setMinDistance(cameraDistance);
            chaseCamera.setMaxDistance(cameraDistance);
            chaseCamera.setSmoothMotion(true);
            chaseCamera.setTrailingEnabled(true);

        }

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
//        mainApplication.showStats();

        if (firstGame) {
            showStartGameUI();
        } else {
            showInGameUI();
            game.start(player);
        }
    }

    private void showStartGameUI() {
        titleLabel.show();
        titleLabel.fadeFromTo(0f, 1f, 2f, 0.2f);
        titleLabel.moveFromToCenter(0, 500, 0, 320, 1f, 0.2f);

        playButton.show();
        playButton.fadeFromTo(0, 1, 2f, 0);
        playButton.moveFromToCenter(0, -500, 0, -280, 1f, 0f);

        retryButton.hide();
        scoreLabel.hide();
        cubeButtonPanel.hide();

        bestLabel.setText("BEST: " + baseApplication.getGameSaves().getGameData().getScore());
        bestLabel.show();
        bestLabel.fadeFromTo(0f, 1f, 2f, 0f);
        bestLabel.moveFromToCenter(0, 500, 0, 240, 1f, 0f);

    }

    private void showInGameUI() {
        playButton.hide();
        retryButton.hide();
        titleLabel.hide();
//        instructionsLabel.hide();
        scoreLabel.setText("0");
        scoreLabel.show();
        bestLabel.hide();

        reloadCubeButtons();
        cubeButtonPanel.show();
        cubeButtonPanel.moveFromToCenter(0, -500, 0, -340, 1f, 0f);
        updateSelectedCubeButton(null);

        touchPickListener.registerWithInput(inputManager);

    }

    private void showGameOverUI() {
        titleLabel.hide();
        playButton.hide();
        cubeButtonPanel.hide();

        retryButton.show();
        retryButton.fadeFromTo(0, 1, 2f, 0);
        retryButton.moveFromToCenter(0, -500, 0, -250, 1f, 0f);

        scoreLabel.show();
        bestLabel.setText("BEST: " + baseApplication.getGameSaves().getGameData().getScore());
        bestLabel.show();
        bestLabel.fadeFromTo(0f, 1f, 2f, 0f);
        bestLabel.moveFromToCenter(0, 500, 0, 240, 1f, 0f);

        touchPickListener.unregisterInput();

    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {

        int score = player.getScore();
        int oldScore = baseApplication.getGameSaves().getGameData().getScore();
        if (score > oldScore) {
            baseApplication.getGameSaves().getGameData().setScore(score);
        }

        int gamesPlayed = mainApplication.getGameSaves().getGameData().getGamesPlayed();
        gamesPlayed++;
        mainApplication.getGameSaves().getGameData().setGamesPlayed(gamesPlayed);

        //Finally save the data
        baseApplication.getGameSaves().save();

        showGameOverUI();

    }

    @Override
    public void doGameCompleted() {

    }

    @Override
    public void doScoreChanged(int score) {
        scoreLabel.setText(score + "");
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            if (game.isStarted() && !game.isPaused()) {

            }

        }
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (!pickEvent.isKeyDown()) {
            if (pickEvent.getContactObject() != null && selectedCubeButton != null) {
//                log("Clicked on " + pickEvent.getContactObject().getName());
                //TODO;
                Vector3f cubeLocation = null;

                if (pickEvent.getContactObject().getName().equals(Game.PLATFORM)
                        || pickEvent.getContactObject().getName().equals(Game.CUBE)) {
                    cubeLocation = new Vector3f(pickEvent.getContactObject().getWorldTranslation().x, 0, pickEvent.getContactObject().getWorldTranslation().z);

//                    log("Location to add " + cubeLocation);
                    if (game.isPlaying()) {
                        //TODO: Play block/no play sound

                    } else {
                        String type = selectedCubeButton.getName();
                        boolean addedCube = game.addCube(type, cubeLocation.x, cubeLocation.z);

                        if (addedCube) {
                            cubeButtonSelection.hide();

                            Tween.to(selectedCubeButton, WidgetAccessor.SCALE_XY, 1.2f)
                                    .target(0f, 0f)
                                    .ease(Bounce.OUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> bt) {
                                            selectedCubeButton.hide();
                                            updateSelectedCubeButton(null);
                                            checkForCubeButtonReload();
                                        }
                                    })
                                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
                        }

                    }

                }

            }
        }

    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {

    }
}
