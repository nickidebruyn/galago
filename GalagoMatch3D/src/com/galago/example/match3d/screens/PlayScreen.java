/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import com.bruynhuis.galago.control.camera.CameraShaker;
import com.bruynhuis.galago.games.basic.BasicGameListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.HPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.SharedSystem;
import com.galago.example.match3d.MainApplication;
import com.galago.example.match3d.game.Game;
import static com.galago.example.match3d.game.Game.CUBE_TYPE;
import com.galago.example.match3d.game.GameProgressListener;
import com.galago.example.match3d.game.Player;
import com.galago.example.match3d.ui.CubeButton;
import com.galago.example.match3d.ui.ExitDialog;
import com.galago.example.match3d.ui.IconButton;
import com.galago.example.match3d.ui.PlayButton;
import com.galago.example.match3d.ui.RetryButton;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements BasicGameListener, PickListener, GameProgressListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private TouchPickListener touchPickListener;
    private Game game;
    private Player player;
    private boolean firstGame = true;

//    private FilterPostProcessor fpp;
    private float cameraDistance = 11f;

    private ChaseCamera chaseCamera;
    private CameraShaker cameraShaker;

    private Label gameoverLabel;
    private Label titleLabel;
    private Label scoreLabel;
    private Label bestLabel;

    private Label c, o, l, o2, r;

    private PlayButton playButton;
    private RetryButton retryButton;
    private Panel cubeButtonPanel;
    private Label infoLabel;
    private Label messageLabel;
    private CubeButton cubeButton1;
    private CubeButton cubeButton2;
    private CubeButton cubeButton3;
    private Image cubeButtonSelection;
//    private Image sunbeams;

    private HPanel iconsPanel;
    private IconButton likeButton;
    private IconButton soundButton;
    private IconButton shareButton;
    private IconButton leaderboardButton;
//    private VSlider tiltSlider;

    private Image handIcon;

    private CubeButton selectedCubeButton;

    private float maxDragDistance = 30f;
    private float cameraTiltAngle = 35;
    private Vector2f dragStart;
    private boolean highscoreBeaten = false;
    private String savedTool1Type, savedTool2Type, savedTool3Type;

    private ExitDialog exitDialog;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        FontStyle font = new FontStyle(70, 4);

        c = new Label(hudPanel, "C", 50, 50, font);
        c.setOutlineColor(ColorRGBA.White);
        c.centerTop(-100, 30);
        o = new Label(hudPanel, "O", 50, 50, font);
        o.setOutlineColor(ColorRGBA.White);
        o.centerTop(-50, 30);
        l = new Label(hudPanel, "L", 50, 50, font);
        l.setOutlineColor(ColorRGBA.White);
        l.centerTop(0, 30);
        o2 = new Label(hudPanel, "O", 50, 50, font);
        o2.setOutlineColor(ColorRGBA.White);
        o2.centerTop(50, 30);
        r = new Label(hudPanel, "R", 50, 50, font);
        r.setOutlineColor(ColorRGBA.White);
        r.centerTop(100, 30);

        gameoverLabel = new Label(hudPanel, "GAME OVER", 54, 480, 50);
        gameoverLabel.centerAt(0, 330);

        titleLabel = new Label(hudPanel, "CUBE", 50, 480, 50);
        titleLabel.centerAt(0, 260);

        scoreLabel = new Label(hudPanel, "0", 54);
        scoreLabel.centerAt(0, 290);

        bestLabel = new Label(hudPanel, "BEST: 0", 30);
        bestLabel.centerTop(0, 150);

        playButton = new PlayButton(hudPanel);
        playButton.centerAt(0, -250);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
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
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
                    firstGame = false;
                    showScreen(NAME);

                }
            }

        });

        iconsPanel = new HPanel(hudPanel, window.getWidth() * 0.8f, 60);
        hudPanel.add(iconsPanel);
        iconsPanel.centerAt(0, -320);

        likeButton = new IconButton(iconsPanel, "like", "Interface/icon-like.png");
        likeButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
                    baseApplication.doRateApplication();

                }
            }

        });

        soundButton = new IconButton(iconsPanel, "sound", "Interface/icon-sound-on.png");
        soundButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {                    

                    if (baseApplication.getGameSaves().getGameData().isSoundOn()) {
                        baseApplication.getGameSaves().getGameData().setSoundOn(false);
                        baseApplication.doAnalyticsAction("playscreen", "sound_action", "Sound off.");
                    } else {
                        baseApplication.getGameSaves().getGameData().setSoundOn(true);
                        baseApplication.doAnalyticsAction("playscreen", "sound_action", "Sound on.");

                    }
                    baseApplication.getGameSaves().save();

                    updateSoundIcon();

                }
            }

        });

        shareButton = new IconButton(iconsPanel, "share", "Interface/icon-share.png");
        shareButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
                    baseApplication.doShareApplication();

                }
            }

        });

        leaderboardButton = new IconButton(iconsPanel, "leaderboard", "Interface/icon-leaderboard.png");
        leaderboardButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
                    baseApplication.doAlert("Leaderboard not implemented yet!");
//                    showScreen("caps");

                }
            }

        });

        iconsPanel.layout();

        infoLabel = new Label(hudPanel, "SELECT A COLOR", 20);
        infoLabel.centerAt(0, -280);

        cubeButtonPanel = new Panel(hudPanel, null, window.getWidth(), 100);
        hudPanel.add(cubeButtonPanel);

        cubeButton1 = new CubeButton(cubeButtonPanel, Game.CUBE_TYPE_1);
        cubeButton1.centerAt(-100, 0);
        cubeButton1.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
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
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
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
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
                    updateSelectedCubeButton(cubeButton3);

                }
            }

        });

        cubeButtonSelection = new Image(cubeButtonPanel, "Interface/button-selected.png", 64, 64, true);
        cubeButtonSelection.centerAt(0, 0);
        cubeButtonSelection.addEffect(new WobbleEffect(cubeButtonSelection, 1.04f, 0.3f));

        cubeButtonPanel.centerBottom(0, 10);

        handIcon = new Image(hudPanel, "Interface/icon-hand.png", 50, 50, true);
        handIcon.centerAt(0, -220);

//        sunbeams = new Image(hudPanel, "Interface/sunbeams.png", 800, 800, true);
        messageLabel = new Label(hudPanel, "Message", 26);
        messageLabel.centerAt(0, 0);

//        tiltSlider = new VSlider(hudPanel, "Resources/blank.png", "Resources/blank.png", 100, 300);
//        tiltSlider.setMaxValue(55);
//        tiltSlider.setMinValue(15);
//        tiltSlider.setIncrementValue(0.5f);
//        tiltSlider.rightCenter(0, 0);
//        tiltSlider.getLabel().setVisible(false);
//        tiltSlider.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void doValueChange(float value) {
//                cameraTiltAngle = value;
//            }
//        });
        exitDialog = new ExitDialog(window);
        exitDialog.addExitButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (isActive()) {
                    PlayScreen.super.doEscape(false);
                }

            }

        });

        exitDialog.addCancelButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (isActive()) {
                    exitDialog.hide();
                }

            }

        });

        exitDialog.addRestartButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (isActive()) {
                    clearSavedFiles();
                    exitDialog.hide();
                    baseApplication.getSoundManager().playSoundRandomPitch("button");
                    firstGame = false;
                    showScreen(NAME);
                }

            }

        });

    }

    protected void updateSoundIcon() {

        if (baseApplication.getGameSaves().getGameData().isSoundOn()) {
            soundButton.updatePicture("Interface/icon-sound-on.png");
        } else {
            soundButton.updatePicture("Interface/icon-sound-off.png");
        }

        baseApplication.getSoundManager().muteAll(!baseApplication.getGameSaves().getGameData().isSoundOn());

    }

    private void updateSelectedCubeButton(CubeButton cubeButton) {

        if (cubeButton == null) {
            cubeButtonSelection.hide();
            selectedCubeButton = null;

        } else {

            if (game.getCubePlacementCount() < 3 && firstGame) {
                infoLabel.setText("TAP ON GRID");
                infoLabel.fadeFromTo(0f, 1f, 0.5f, 0f);
            }

            if (game.getCubePlacementCount() == 0 && firstGame) {
                handIcon.show();
                handIcon.moveFromToCenter(100, -370, 0, -80, 1.0f, 0.2f);

            }

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

        //Force the system to the same colors if it is the first time playing
        if (game.getCubePlacementCount() == 0 && firstGame) {
            String cubeButtonType = game.getRandomCubeType();
            ColorRGBA cubeButtonColor = game.getCubeColor(cubeButtonType);

            cubeButton1.setBackgroundColor(cubeButtonColor);
            cubeButton1.setName(cubeButtonType);

            cubeButton2.setBackgroundColor(cubeButtonColor);
            cubeButton2.setName(cubeButtonType);

            cubeButton3.setBackgroundColor(cubeButtonColor);
            cubeButton3.setName(cubeButtonType);

        }
    }

    private void checkForCubeButtonReload() {
        if (!cubeButton1.isVisible() && !cubeButton2.isVisible() && !cubeButton3.isVisible()) {
//            log("Reload cube buttons");
            reloadCubeButtons();
            cubeButtonPanel.moveFromToCenter(0, -500, 0, -340, 1f, 0f);
//            infoLabel.setText("<< SWIPE LEFT OR RIGHT >>");

        }
    }

    @Override
    protected void load() {

        updateSoundIcon();

        selectedCubeButton = null;
        dragStart = null;
        savedTool1Type = null;
        savedTool2Type = null;
        savedTool3Type = null;

        game = new Game(mainApplication, rootNode);
        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);
        game.addGameProgressListener(this);

        touchPickListener = new TouchPickListener(camera, game.getLevelNode());
        touchPickListener.setPickListener(this);

//        if (fpp == null) {
//            fpp = new FilterPostProcessor(baseApplication.getAssetManager());
//            baseApplication.getViewPort().addProcessor(fpp);
//
//            FXAAFilter fXAAFilter = new FXAAFilter();
//            fpp.addFilter(fXAAFilter);
//
//        }
//        cameraDistance = 7;
//        camera.setLocation(new Vector3f(-cameraDistance, cameraDistance*0.8f, cameraDistance));
//        camera.lookAt(new Vector3f(0, cameraDistance * 0.02f, 0), Vector3f.UNIT_Y);
        if (chaseCamera == null) {

            chaseCamera = new ChaseCamera(camera, rootNode, inputManager);
            chaseCamera.setDefaultDistance(cameraDistance);
            chaseCamera.setChasingSensitivity(60);
            chaseCamera.setSmoothMotion(true);
            chaseCamera.setTrailingEnabled(false);

            chaseCamera.setDefaultHorizontalRotation(135 * FastMath.DEG_TO_RAD);
            chaseCamera.setDefaultVerticalRotation(cameraTiltAngle * FastMath.DEG_TO_RAD);

//            chaseCamera.setMinVerticalRotation(20 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMaxVerticalRotation(55 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMinVerticalRotation(0 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMaxVerticalRotation(0 * FastMath.DEG_TO_RAD);
            chaseCamera.setLookAtOffset(new Vector3f(0, 0.7f, 0));

            chaseCamera.setHideCursorOnRotate(false);
//            chaseCamera.setRotationSpeed(5);
            chaseCamera.setRotationSpeed(8);
            chaseCamera.setMinDistance(cameraDistance);
            chaseCamera.setMaxDistance(cameraDistance);

            chaseCamera.setDragToRotate(true);
            chaseCamera.setRotationSensitivity(8);
        }

        cameraShaker = new CameraShaker(camera, rootNode);

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
//        mainApplication.showStats();

        highscoreBeaten = false;

        if (firstGame) {
            loadProgress();
            showStartGameUI();
            
        } else {
            showInGameUI();
            game.start(player);
            
        }
    }

    private void showStartGameUI() {

//        c.setTextColor(game.getCubeColor(Game.CUBE_TYPE_1));
//        o.setTextColor(game.getCubeColor(Game.CUBE_TYPE_2));
//        l.setTextColor(game.getCubeColor(Game.CUBE_TYPE_3));
//        o2.setTextColor(game.getCubeColor(Game.CUBE_TYPE_4));
//        r.setTextColor(game.getCubeColor(Game.CUBE_TYPE_5));
        TweenEquation colorEq = Bounce.OUT;
        float speed = 1f;

        c.moveFromToCenter(-100, 500, -100, 340, speed, 0f, colorEq, 0, false);
        o.moveFromToCenter(-50, 500, -50, 340, speed, 0.2f, colorEq, 0, false);
        l.moveFromToCenter(0, 500, 0, 340, speed, 0.4f, colorEq, 0, false);
        o2.moveFromToCenter(50, 500, 50, 340, speed, 0.6f, colorEq, 0, false);
        r.moveFromToCenter(100, 500, 100, 340, speed, 0.8f, colorEq, 0, false);

        titleLabel.setTextColor(game.getCubeColor(Game.CUBE_TYPE_6));
        titleLabel.show();
        titleLabel.moveFromToCenter(0, 500, 0, 270, 0.7f, 0.2f);

        playButton.show();
        playButton.fadeFromTo(0, 1, 2f, 0);
        playButton.moveFromToCenter(0, -500, 0, -220, 1f, 0f);

        retryButton.hide();
        scoreLabel.hide();
        cubeButtonPanel.hide();
        infoLabel.hide();
        handIcon.hide();
        gameoverLabel.hide();
        messageLabel.hide();

        bestLabel.setText("BEST " + baseApplication.getGameSaves().getGameData().getScore());
        bestLabel.show();
        bestLabel.fadeFromTo(0f, 1f, 2f, 0f);
        bestLabel.moveFromToCenter(0, 500, 0, 180, 0.5f, 0f);

        iconsPanel.moveFromToCenter(0, -500, 0, -350, 1f, 1.f);

//        tiltSlider.hide();
    }

    private void showInGameUI() {
        playButton.hide();
        retryButton.hide();
        titleLabel.hide();
//        instructionsLabel.hide();
        scoreLabel.setText(player.getScore() + "");
        scoreLabel.show();
        bestLabel.hide();
        gameoverLabel.hide();

        infoLabel.setText("SELECT A COLOR");
        infoLabel.show();

        messageLabel.hide();

        if (firstGame) {
            handIcon.show();
            handIcon.fadeFromTo(0, 1, 1.5f, 0);
            handIcon.moveFromToCenter(-100, -370, 100, -370, 1.0f, 1, Linear.INOUT, 2, true);
//            tiltSlider.hide();

        } else {
            handIcon.hide();
//            tiltSlider.show();
//            tiltSlider.getLabel().setVisible(false);

        }

        c.hide();
        o.hide();
        l.hide();
        o2.hide();
        r.hide();

        infoLabel.fadeFromTo(0, 1, 1f, 0);

        iconsPanel.moveFromToCenter(0, -350, 0, -500, 0.8f, 0f);

        reloadCubeButtons();
        cubeButtonPanel.show();
        cubeButtonPanel.moveFromToCenter(0, -500, 0, -340, 1f, 0f);
        updateSelectedCubeButton(null);
        updateSavedToolButtons();

        touchPickListener.registerWithInput(inputManager);

    }

    private void showGameOverUI() {
        titleLabel.hide();
        playButton.hide();
        cubeButtonPanel.hide();
        infoLabel.hide();
        handIcon.hide();
        messageLabel.hide();

        gameoverLabel.show();
        gameoverLabel.fadeFromTo(0f, 1f, 1f, 0f);
        gameoverLabel.moveFromToCenter(0, 500, 0, 340, 0.8f, 0f);

        retryButton.show();
        retryButton.fadeFromTo(0, 1, 2f, 0);
        retryButton.moveFromToCenter(0, -500, 0, -220, 1f, 0f);

        scoreLabel.show();
        bestLabel.setText("BEST " + baseApplication.getGameSaves().getGameData().getScore());
        bestLabel.show();
        bestLabel.fadeFromTo(0f, 1f, 2f, 0f);
        bestLabel.moveFromToCenter(0, 500, 0, 220, 1f, 0f);

        iconsPanel.moveFromToCenter(0, -500, 0, -350, 1f, 1.f);

        touchPickListener.unregisterInput();
//        tiltSlider.hide();

    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
        game.close();
//        baseApplication.getViewPort().removeProcessor(fpp);
    }

    @Override
    protected void pause() {
    }

    private void saveHighScore() {
        int score = player.getScore();
        int oldScore = baseApplication.getGameSaves().getGameData().getScore();
        if (score > oldScore) {
            baseApplication.getGameSaves().getGameData().setScore(score);
            if (!highscoreBeaten) {
                showMessage("New Highscore!");
                baseApplication.getSoundManager().playSound("levelup");
                highscoreBeaten = true;
            }

        }

        //Finally save the data
        baseApplication.getGameSaves().save();
    }

    @Override
    public void doGameOver() {

        baseApplication.getSoundManager().playSound("gameover");
        cameraShaker.shake(CameraShaker.LARGE_AMOUNT, 50);
        baseApplication.doVibrate();

        int score = player.getScore();
        int oldScore = baseApplication.getGameSaves().getGameData().getScore();
        if (score > oldScore) {
            baseApplication.getGameSaves().getGameData().setScore(score);
        }

        int gamesPlayed = mainApplication.getGameSaves().getGameData().getGamesPlayed();
        gamesPlayed++;
        mainApplication.getGameSaves().getGameData().setGamesPlayed(gamesPlayed);
        
        //Clear the saved data
        mainApplication.getGameSaves().getGameData().setProperties(new Properties());

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

        if (score == 1) {
            showMessage("Nicely done!");
        }

        saveHighScore();

    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

//            if (game.isStarted() && !game.isPaused()) {
//                if (camera.getLocation().y < 5f) {
//                    log("Danger camera below: ");
            chaseCamera.setDefaultVerticalRotation(cameraTiltAngle * FastMath.DEG_TO_RAD);
//                    
//                }
//            }

        }
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (game.isStarted() && !game.isPaused()) {

            if (pickEvent.isKeyDown()) {
                dragStart = inputManager.getCursorPosition().clone();

            } else if (pickEvent.getContactObject() != null && selectedCubeButton != null && !isDragging()) {

                //TODO;
                Vector3f cubeLocation = null;
                dragStart = null;

                if (pickEvent.getContactObject().getName().equals(Game.PLATFORM)
                        || pickEvent.getContactObject().getName().equals(Game.CUBE)) {
                    cubeLocation = new Vector3f(pickEvent.getContactObject().getWorldTranslation().x, 0, pickEvent.getContactObject().getWorldTranslation().z);

//                    log("Location to add " + cubeLocation);
                    if (game.isPlaying()) {
                        //TODO: Play block/no play sound

                    } else {
                        String type = selectedCubeButton.getName();
                        Vector3f normal = pickEvent.getContactNormal();
//                        log("Normal: " + normal);
//                        log("Pos   : " + cubeLocation);

                        float xPos = (float) Math.round(cubeLocation.x + (normal.x));
                        float zPos = (float) Math.round(cubeLocation.z + (normal.z));

//                        log("Newpos: " + xPos + ", " + zPos);

                        boolean addedCube = game.addCube(type, xPos, zPos);

                        if (addedCube) {
                            cubeButton1.setEnabled(false);
                            cubeButton2.setEnabled(false);
                            cubeButton3.setEnabled(false);

                            cubeButtonSelection.hide();

                            if (game.getCubePlacementCount() >= 4 || !firstGame) {
                                infoLabel.setText("SELECT A COLOR");
                                handIcon.hide();

                            } else if (game.getCubePlacementCount() == 3) {
                                infoLabel.setText("DRAG LEFT OR RIGHT");
                                infoLabel.fadeFromTo(0f, 1f, 0.5f, 0f);
                                handIcon.show();
                                handIcon.moveFromToCenter(-100, -220, 100, -220, 1.2f, 0, Linear.INOUT, 5, true);
//                                tiltSlider.show();
//                                tiltSlider.getLabel().setVisible(false);

                            } else if (game.getCubePlacementCount() == 2) {
                                handIcon.hide();
                                infoLabel.setText("MATCH HORIZONTALLY");
                                infoLabel.fadeFromTo(0f, 1f, 0.5f, 0f);

                            } else if (game.getCubePlacementCount() == 1) {
                                handIcon.hide();
                                infoLabel.setText("MATCH 3 SAME COLOR IN ROW");
                                infoLabel.fadeFromTo(0f, 1f, 0.5f, 0f);
                            }

                            Tween.to(selectedCubeButton, WidgetAccessor.SCALE_XY, 0.6f)
                                    .target(0f, 0f)
                                    .ease(Expo.OUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> bt) {
                                            selectedCubeButton.hide();
                                            updateSelectedCubeButton(null);
                                            checkForCubeButtonReload();
                                            cubeButton1.setEnabled(true);
                                            cubeButton2.setEnabled(true);
                                            cubeButton3.setEnabled(true);

                                        }
                                    })
                                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
                        }

                    }

                }

            }
        }

    }

    private boolean isDragging() {
        return dragStart != null && dragStart.distance(inputManager.getCursorPosition()) > maxDragDistance;
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {

    }

    @Override
    public void doLevelUp(int level) {
        showMessage("Level Upgrade!");
        baseApplication.getSoundManager().setSoundPitch("levelup", 0.8f);
        baseApplication.getSoundManager().playSound("levelup");

    }

    @Override
    public void doScoreBooster(int boosterLevel) {
        log("booster = " + boosterLevel);

        if (boosterLevel == 2) {
            player.addScore(10);
            showMessage("+10 Super Boost");
            baseApplication.getSoundManager().setSoundPitch("booster", 1f);
            baseApplication.getSoundManager().playSound("booster");

        } else if (boosterLevel == 3) {
            player.addScore(20);
            showMessage("+20 Mega Bonus");
            baseApplication.getSoundManager().setSoundPitch("booster", 0.8f);
            baseApplication.getSoundManager().playSound("booster");

        } else if (boosterLevel == 4) {
            player.addScore(50);
            showMessage("+50 Ultra Bonus");
            baseApplication.getSoundManager().setSoundPitch("booster", 0.6f);
            baseApplication.getSoundManager().playSound("booster");

        } else if (boosterLevel == 5) {
            player.addScore(10);
            showMessage("+10 Clean Sweep Bonus");
            baseApplication.getSoundManager().setSoundPitch("booster", 0.5f);
            baseApplication.getSoundManager().playSound("booster");
        }

    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.show();
        messageLabel.scaleFromTo(0, 0, 1.2f, 1.2f, 0.5f, 0f, Bounce.OUT);
        messageLabel.fadeFromTo(1f, 0, 0.6f, 1f);
        messageLabel.moveFromToCenter(0, 20, 0, 350, 1.2f, 0.5f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                messageLabel.hide();
            }
        });

    }

    @Override
    public void doEscape(boolean touchEvent) {

        if (exitDialog.isVisible()) {
//            exitDialog.hide();
        } else {
            if (game != null && game.isStarted()) {
                saveProgress();
            }
            exitDialog.show();
        }

    }
    
    public void clearSavedFiles() {
        baseApplication.getGameSaves().getGameData().setProperties(new Properties());
        baseApplication.getGameSaves().save();
        
    }

    /**
     * This method will save the current players progress.
     */
    public void saveProgress() {
        //TODO: Save the cube positions and colors        
        Properties properties = baseApplication.getGameSaves().getGameData().getProperties();
        properties.clear();
        properties.put("level", game.getPlayerLevel());
        properties.put("score", player.getScore());
        properties.put("cubeplacementcount", game.getCubePlacementCount());

        if (cubeButton1.isVisible()) {
            properties.put("tool1", cubeButton1.getName());
        }

        if (cubeButton2.isVisible()) {
            properties.put("tool2", cubeButton2.getName());
        }

        if (cubeButton3.isVisible()) {
            properties.put("tool3", cubeButton3.getName());
        }

        for (int i = 0; i < game.getCubesNode().getQuantity(); i++) {
            Spatial cube = game.getCubesNode().getChild(i);
            if (cube != null) {
                String name = "cube" + i;
                String typeKey = name + "_type";
                String type = cube.getUserData(CUBE_TYPE);
                String posKey = name + "_pos";
                String pos = cube.getLocalTranslation().x + "," + cube.getLocalTranslation().y + "," + cube.getLocalTranslation().z;

//                log("SAVING CUBE DATA");
//                log("\t" + typeKey + ": " + type);
//                log("\t" + posKey + ": " + pos);
                properties.put(typeKey, type);
                properties.put(posKey, pos);

            }
        }

        baseApplication.getGameSaves().getGameData().setProperties(properties);
        baseApplication.getGameSaves().save();

    }

    /**
     * This method will load the current players progress.
     */
    public void loadProgress() {

        savedTool1Type = null;
        savedTool2Type = null;
        savedTool3Type = null;

        baseApplication.getGameSaves().read();
        Properties properties = baseApplication.getGameSaves().getGameData().getProperties();
//        log("properties: " + properties);

        if (properties != null) {

            Set<Object> keys = properties.keySet();
            for (Iterator<Object> iterator = keys.iterator(); iterator.hasNext();) {
                Object key = iterator.next();
                Object val = properties.get(key);
                log("Key: " + key + "; val: " + val);

                if (key.equals("score")) {
                    int savedScore = (int) val;
                    player.addScore(savedScore);

                } else if (key.equals("level")) {
                    int playerLevel = (int) val;
                    game.setPlayerLevel(playerLevel);

                } else if (key.equals("cubeplacementcount")) {
                    int cubeplacementcount = (int) val;
                    game.setCubePlacementCount(cubeplacementcount);

                } else if (key.equals("tool1")) {
                    savedTool1Type = val.toString();

                } else if (key.equals("tool2")) {
                    savedTool2Type = val.toString();

                } else if (key.equals("tool3")) {
                    savedTool3Type = val.toString();

                } else if (key.toString().startsWith("cube") && key.toString().endsWith("type")) {
                    String type = val.toString();
//                    log("Found cube type, " + type);
                    String posStr = properties.getProperty(key.toString().replace("type", "pos"));
//                    log("Found cube pos, " + posStr);
                    String[] posArr = posStr.split(",");
                    if (posArr.length == 3) {
                        float x = Float.parseFloat(posArr[0]);
                        float y = Float.parseFloat(posArr[1]);
                        float z = Float.parseFloat(posArr[2]);

//                        log("Adding cube "+type+" from saved data at (" + x + ", " + y +", " + z + ")");
                        ColorRGBA color = game.getCubeColor(type);
                        game.loadCube(type, color, x, y, z);

                    }

                }

            }
        }
        
        
        if (player.getScore() > 0) {
            firstGame = false;
            game.refreshGame();
            
        } else {
            firstGame = true;
            savedTool1Type = null;
            savedTool2Type = null;
            savedTool3Type = null;
            game.setPlayerLevel(1);
            game.setCubePlacementCount(0);
            game.getCubesNode().detachAllChildren();
        }

        
    }

    private void updateSavedToolButtons() {

        if (savedTool1Type != null || savedTool2Type != null || savedTool3Type != null) {
            
            if (savedTool1Type != null) {
                ColorRGBA color = game.getCubeColor(savedTool1Type);
                cubeButton1.setScale(1f);
                cubeButton1.setBackgroundColor(color);
                cubeButton1.setName(savedTool1Type);
                cubeButton1.show();
            } else {
                cubeButton1.hide();
            }

            if (savedTool2Type != null) {
                ColorRGBA color = game.getCubeColor(savedTool2Type);
                cubeButton2.setScale(1f);
                cubeButton2.setBackgroundColor(color);
                cubeButton2.setName(savedTool2Type);
                cubeButton2.show();
            } else {
                cubeButton2.hide();
            }

            if (savedTool3Type != null) {
                ColorRGBA color = game.getCubeColor(savedTool3Type);
                cubeButton3.setScale(1f);
                cubeButton3.setBackgroundColor(color);
                cubeButton3.setName(savedTool3Type);
                cubeButton3.show();
            } else {
                cubeButton3.hide();
            }
        }

    }
}
