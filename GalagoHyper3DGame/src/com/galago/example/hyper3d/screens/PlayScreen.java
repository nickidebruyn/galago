/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper3d.screens;

import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.games.physics.PhysicsGameListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.galago.example.hyper3d.MainApplication;
import com.galago.example.hyper3d.game.Game;
import com.galago.example.hyper3d.game.Player;
import com.galago.example.hyper3d.ui.PlayButton;
import com.galago.example.hyper3d.ui.RetryButton;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Caps;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements PhysicsGameListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private Game game;
    private Player player;
    private boolean firstGame = true;
    private float cameraDistance = 20;
    private float cameraHeight = 12;
    private float cameraForward = 3f;
    private Vector3f cameraPosition;
    private Vector3f cameraLookat;

    private FilterPostProcessor fpp;
    private CartoonEdgeProcessor cartoonEdgeProcess;

    private Label titleLabel;
//    private Label instructionsLabel;
    private Label scoreLabel;
    private Label bestLabel;
    private PlayButton playButton;
    private RetryButton retryButton;
    private ControlButton jumpButton;

    private boolean playerTakeDamage = false;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        titleLabel = new Label(hudPanel, "Ball Jump", 56, 480, 50);
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

        jumpButton = new ControlButton(hudPanel, "jump-button", 480, 800);
        jumpButton.center();
        jumpButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game.isStarted() && !game.isPaused() && !game.isGameover()) {
                    player.prepareJump();
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game.isStarted() && !game.isPaused() && !game.isGameover()) {
                    player.jump();
                }
            }

        });
    }

    @Override
    protected void load() {

        playerTakeDamage = false;

        game = new Game(mainApplication, rootNode);
        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);

        if (fpp == null) {
            if (baseApplication.getRenderer().getCaps().contains(Caps.GLSL100)) {
                cartoonEdgeProcess = new CartoonEdgeProcessor();
                baseApplication.getViewPort().addProcessor(cartoonEdgeProcess);
            }
            
            fpp = new FilterPostProcessor(baseApplication.getAssetManager());
            baseApplication.getViewPort().addProcessor(fpp);

            FXAAFilter fXAAFilter = new FXAAFilter();
            fpp.addFilter(fXAAFilter);


        }

        cameraPosition = new Vector3f(-cameraDistance * 0.8f + cameraForward, cameraHeight, cameraDistance);
        camera.setLocation(cameraPosition);

        cameraLookat = new Vector3f(0 + cameraForward, cameraHeight * 0.2f, 0);
        camera.lookAt(cameraLookat, Vector3f.UNIT_Y);
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
        playButton.moveFromToCenter(0, -500, 0, -250, 1f, 0f);

        retryButton.hide();
        scoreLabel.hide();

        bestLabel.setText("BEST: " + baseApplication.getGameSaves().getGameData().getScore());
        bestLabel.show();
        bestLabel.fadeFromTo(0f, 1f, 2f, 0f);
        bestLabel.moveFromToCenter(0, 500, 0, 240, 1f, 0f);

        jumpButton.hide();

    }

    private void showInGameUI() {
        playButton.hide();
        retryButton.hide();
        titleLabel.hide();
//        instructionsLabel.hide();
        scoreLabel.setText("");
        scoreLabel.show();
        bestLabel.hide();
        jumpButton.show();

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

        jumpButton.hide();

    }

    @Override
    protected void exit() {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doScoreChanged(int score) {
        scoreLabel.setText(score + "");
    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
        player.setOnGround(true);

//        log("collision: " + collided.getName());
        //Detect if the collided object is infront of the player
        if (collided.getWorldTranslation().x > (player.getPosition().x + 0.35f)
                && player.getPosition().y < collided.getWorldTranslation().y + 0.4f
                && player.getPosition().y > collided.getWorldTranslation().y - 0.4f) {
//            log("hit wall: " + collided.getWorldTranslation());
            playerTakeDamage = true;

        }

    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            if (game.isStarted() && !game.isPaused() && !game.isGameover()) {

                cameraPosition.set(player.getPosition().x - (cameraDistance * 0.8f) + cameraForward, cameraHeight, cameraDistance);
                camera.setLocation(cameraPosition);

                cameraLookat.set(player.getPosition().x + cameraForward, cameraHeight * 0.2f, 0);
                camera.lookAt(cameraLookat, Vector3f.UNIT_Y);

                if (playerTakeDamage) {
                    player.doDamage(1);
                    playerTakeDamage = false;
                }
            }

        }
    }

}
