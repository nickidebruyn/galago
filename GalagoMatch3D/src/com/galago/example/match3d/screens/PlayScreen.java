/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.screens;

import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.games.basic.BasicGameListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.galago.example.match3d.MainApplication;
import com.galago.example.match3d.game.Game;
import com.galago.example.match3d.game.Player;
import com.galago.example.match3d.ui.PlayButton;
import com.galago.example.match3d.ui.RetryButton;
import com.jme3.input.ChaseCamera;
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
    private float cameraDistance = 14f;

    private ChaseCamera chaseCamera;

    private Label titleLabel;
    private Label scoreLabel;
    private Label bestLabel;
    private PlayButton playButton;
    private RetryButton retryButton;

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

        
        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    @Override
    protected void load() {

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
        playButton.moveFromToCenter(0, -500, 0, -260, 1f, 0f);

        retryButton.hide();
        scoreLabel.hide();

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
        touchPickListener.registerWithInput(inputManager);

    }

    private void showGameOverUI() {
        titleLabel.hide();
        playButton.hide();

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
            if (pickEvent.getContactObject() != null) {
                log("Clicked on " + pickEvent.getContactObject().getName());
                //TODO;
                Vector3f cubeLocation = null;
                
                if (pickEvent.getContactObject().getName().equals(Game.PLATFORM) ||
                        pickEvent.getContactObject().getName().equals(Game.CUBE)) {
                    cubeLocation = new Vector3f(pickEvent.getContactObject().getWorldTranslation().x, 0, pickEvent.getContactObject().getWorldTranslation().z);
                    
                }

                log("Location to add " + cubeLocation);
                if (game.isPlaying()) {
                    //TODO: Play block/no play sound
                    
                } else {
                    game.addCube(Game.CUBE_TYPE_3, cubeLocation.x, cubeLocation.z);
                    
                }
                

            }
        }

    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {


    }
}
