/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.rocketman.screens;

import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGameListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.HorizontalTouchStick;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchStickAdapter;
import com.galago.example.rocketman.MainApplication;
import com.galago.example.rocketman.game.Game;
import com.galago.example.rocketman.game.Player;
import com.galago.example.rocketman.ui.PlayButton;
import com.galago.example.rocketman.ui.RetryButton;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements SimplePhysics2DGameListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private Game game;
    private Player player;
    private boolean firstGame = true;
    private boolean movePlayer = false;
    private Vector3f targetPosition;

    private Label titleLabel;
    private Label scoreLabel;
    private Label bestLabel;
    private PlayButton playButton;
    private RetryButton retryButton;
    private HorizontalTouchStick controlbutton;

    private boolean playerTakeDamage = false;
    private int playerDamageAmount = 0;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        titleLabel = new Label(hudPanel, "Rocket Man", 56, 480, 50);
        titleLabel.centerTop(0, 50);

        scoreLabel = new Label(hudPanel, "0", 52);
        scoreLabel.centerAt(0, 200);

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

        controlbutton = new HorizontalTouchStick(hudPanel, "controlbutton", "Resources/blank.png", window.getWidth(), window.getHeight());
        controlbutton.addTouchStickListener(new TouchStickAdapter() {
            @Override
            public void doRelease(float x, float y) {
//                log("release");
                player.flyStop();
            }

            @Override
            public void doRight(float x, float y, float distance) {
//                log("right");
                player.flyRight();
            }

            @Override
            public void doLeft(float x, float y, float distance) {
//                log("left");
                player.flyLeft();
            }
            
        });
    }

    @Override
    protected void load() {

        playerTakeDamage = false;
        playerDamageAmount = 0;
        movePlayer = false;

        game = new Game(mainApplication, rootNode);
        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);

        camera.setLocation(new Vector3f(0, 0, 10));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

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
        titleLabel.moveFromToCenter(0, 300, 0, 200, 1f, 0.2f);

        playButton.show();
        playButton.fadeFromTo(0, 1, 2f, 0);
        playButton.moveFromToCenter(0, -300, 0, -250, 1f, 0f);

        retryButton.hide();
        scoreLabel.hide();

        bestLabel.setText("BEST: " + baseApplication.getGameSaves().getGameData().getScore());
        bestLabel.show();
        bestLabel.fadeFromTo(0f, 1f, 2f, 0f);
        bestLabel.moveFromToCenter(0, 300, 0, 140, 1f, 0f);

        controlbutton.hide();

    }

    private void showInGameUI() {
        playButton.hide();
        retryButton.hide();
        titleLabel.hide();
//        instructionsLabel.hide();
        scoreLabel.setText("0");
        scoreLabel.show();
        bestLabel.hide();
        controlbutton.show();

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
        bestLabel.moveFromToCenter(0, 500, 0, 140, 1f, 0f);

        controlbutton.hide();

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

    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
//        log("collided = " + collided.getName());
//        log("collider = " + collider.getName());
//

    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
//        log("collided = " + collided.getName());
//        log("collider = " + collider.getName());
//
//        if (collided.getControl(ObstacleControl.class) != null && collided.getControl(ObstacleControl.class).isAlive()) {
//            int health = collided.getControl(ObstacleControl.class).getHealth();
//            collided.getControl(ObstacleControl.class).doDamage(health);
//
//            log("Player Damage: " + health);
            playerDamageAmount = 1;
            playerTakeDamage = true;
//
//        }

    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionPlayerWithPlayer(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {

    }

    @Override
    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {

    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

                if (playerTakeDamage) {
                    player.doDamage(playerDamageAmount);
                    playerDamageAmount = 0;
                    playerTakeDamage = false;
                }

            }

        }
    }

}
