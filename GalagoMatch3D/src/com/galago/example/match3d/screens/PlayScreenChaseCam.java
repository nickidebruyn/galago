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
import com.bruynhuis.galago.filters.FXAAFilter;
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
import com.galago.example.match3d.game.GameProgressListener;
import com.galago.example.match3d.game.Player;
import com.galago.example.match3d.ui.CubeButton;
import com.galago.example.match3d.ui.IconButton;
import com.galago.example.match3d.ui.PlayButton;
import com.galago.example.match3d.ui.RetryButton;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;

/**
 *
 * @author NideBruyn
 */
public class PlayScreenChaseCam extends AbstractScreen implements BasicGameListener, PickListener, GameProgressListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private TouchPickListener touchPickListener;
    private Game game;
    private Player player;
    private boolean firstGame = true;

    private FilterPostProcessor fpp;
    private float cameraDistance = 11f;

    private ChaseCamera chaseCamera;
    private CameraShaker cameraShaker;
    private int placementCount = 0;

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

    private HPanel iconsPanel;
    private IconButton likeButton;
    private IconButton soundButton;
    private IconButton shareButton;
    private IconButton leaderboardButton;

    private Image handIcon;

    private CubeButton selectedCubeButton;

    private float maxDragDistance = 50f;
    private Vector2f dragStart;

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
                    baseApplication.getSoundManager().playSoundRandomPitch("button");

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

        messageLabel = new Label(hudPanel, "Message", 26);
        messageLabel.centerAt(0, 0);
        
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

            if (placementCount < 3 && firstGame) {
                infoLabel.setText("TAP ON GRID");
                infoLabel.fadeFromTo(0f, 1f, 0.5f, 0f);
            }

            if (placementCount == 0 && firstGame) {
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

        if (placementCount == 0 && firstGame) {
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
        placementCount = 0;
        dragStart = null;

        game = new Game(mainApplication, rootNode);
        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);
        game.addGameProgressListener(this);

        touchPickListener = new TouchPickListener(camera, game.getLevelNode());
        touchPickListener.setPickListener(this);

        if (fpp == null) {
            fpp = new FilterPostProcessor(baseApplication.getAssetManager());
            baseApplication.getViewPort().addProcessor(fpp);

            FXAAFilter fXAAFilter = new FXAAFilter();
            fpp.addFilter(fXAAFilter);

        }
        
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
            chaseCamera.setDefaultVerticalRotation(40 * FastMath.DEG_TO_RAD);

//            chaseCamera.setMinVerticalRotation(20 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMaxVerticalRotation(45 * FastMath.DEG_TO_RAD);

            chaseCamera.setMinVerticalRotation(0 * FastMath.DEG_TO_RAD);
            chaseCamera.setMaxVerticalRotation(0 * FastMath.DEG_TO_RAD);

            chaseCamera.setLookAtOffset(new Vector3f(0, 0.5f, 0));

            chaseCamera.setHideCursorOnRotate(false);
//            chaseCamera.setRotationSpeed(5);
            chaseCamera.setRotationSpeed(8);
            chaseCamera.setMinDistance(cameraDistance);
            chaseCamera.setMaxDistance(cameraDistance);

            chaseCamera.setDragToRotate(true);
            chaseCamera.setRotationSensitivity(5);
        }

        cameraShaker = new CameraShaker(camera, rootNode);

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

    }

    private void showInGameUI() {
        playButton.hide();
        retryButton.hide();
        titleLabel.hide();
//        instructionsLabel.hide();
        scoreLabel.setText("0");
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

        } else {
            handIcon.hide();

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
    
    private void saveGameProgress() {
        
    }
    
    private void saveHighScore() {
        int score = player.getScore();
        int oldScore = baseApplication.getGameSaves().getGameData().getScore();
        if (score > oldScore) {
            baseApplication.getGameSaves().getGameData().setScore(score);
            showMessage("New Highscore!");
            baseApplication.getSoundManager().playSound("levelup");
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

            if (game.isStarted() && !game.isPaused()) {
                
//                if (camera.getLocation().y < 3f) {
//                    log("Danger camera below");
//                    camera.setLocation(new Vector3f(camera.getLocation().x, 3f, camera.getLocation().z));
//                }

            }

        }
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (game.isStarted() && !game.isPaused()) {
            
            if (pickEvent.isKeyDown()) {
                dragStart = inputManager.getCursorPosition().clone();
                
            } else {
                if (pickEvent.getContactObject() != null && selectedCubeButton != null && !isDragging()) {
//                log("Clicked on " + pickEvent.getContactObject().getName());
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
                            boolean addedCube = game.addCube(type, cubeLocation.x, cubeLocation.z);

                            if (addedCube) {
                                placementCount++;
                                cubeButton1.setEnabled(false);
                                cubeButton2.setEnabled(false);
                                cubeButton3.setEnabled(false);

                                cubeButtonSelection.hide();

                                if (placementCount >= 3 || !firstGame) {
                                    infoLabel.setText("SELECT A COLOR");
                                    handIcon.hide();

                                } else if (placementCount == 2) {
                                    infoLabel.setText("DRAG LEFT OR RIGHT");
                                    infoLabel.fadeFromTo(0f, 1f, 0.5f, 0f);
                                    handIcon.show();
                                    handIcon.moveFromToCenter(-100, -220, 100, -220, 1.2f, 0, Linear.INOUT, 5, true);

                                } else if (placementCount == 1) {
                                    handIcon.hide();
                                    infoLabel.setText("MATCH SAME COLOR IN ROW");
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
            showMessage("+ Double Boost");
            baseApplication.getSoundManager().setSoundPitch("booster", 1f);
            baseApplication.getSoundManager().playSound("booster");

        } else if (boosterLevel == 3) {
            showMessage("+ Triple Boost");
            baseApplication.getSoundManager().setSoundPitch("booster", 0.8f);
            baseApplication.getSoundManager().playSound("booster");

        } else if (boosterLevel == 4) {
            showMessage("+ Quadro Boost");
            baseApplication.getSoundManager().setSoundPitch("booster", 0.6f);
            baseApplication.getSoundManager().playSound("booster");
        }

    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.show();
        messageLabel.fadeFromTo(1f, 0, 1.2f, 0f);
        messageLabel.moveFromToCenter(0, 20, 0, 250, 1.2f, 0f, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                messageLabel.hide();
            }
        });

    }
    
    
}
