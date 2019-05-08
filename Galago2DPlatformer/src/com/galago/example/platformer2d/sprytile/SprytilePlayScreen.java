/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.sprytile;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.games.blender2d.Blender2DGameListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.galago.example.platformer2d.MainApplication;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class SprytilePlayScreen extends AbstractScreen implements Blender2DGameListener, KeyboardControlListener {

    public static final String NAME = "SprytilePlayScreen";
    private MainApplication mainApplication;
    private Label title;

    private SprytileGame game;
    private SprytilePlayer player;
    private KeyboardControlInputListener keyboardControlInputListener;

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;

        title = new Label(hudPanel, "READY PLAYERS!", 32, 500, 50);
        title.centerTop(0, 20);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);

    }

    @Override
    protected void load() {

        game = new SprytileGame(mainApplication, rootNode, "Scenes/sprytilelevel.j3o");
        game.load();

        player = new SprytilePlayer(game);
        player.load();

        game.addGameListener(this);
    }

    @Override
    protected void show() {

        title.show();
        title.moveFromToCenter(0, 500, 0, 300, 1, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                game.start(player);
                keyboardControlInputListener.registerWithInput(inputManager);
                title.fadeFromTo(1, 0, 1, 1);
            }
        });

    }

    @Override
    protected void exit() {
        keyboardControlInputListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {
    }

    @Override
    public void doGameCompleted() {
    }

    @Override
    public void doScoreChanged(int score) {
    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {

        player.setOnGround();

    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
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
    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
//        player.setLeft(keyboardControlEvent.isLeft() && keyboardControlEvent.isKeyDown());
//        player.setRight(keyboardControlEvent.isRight() && keyboardControlEvent.isKeyDown());

        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isRight()) {
            player.setRight(true);
        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isRight()) {
            player.setRight(false);
        }

        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isLeft()) {
            player.setLeft(true);
        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isLeft()) {
            player.setLeft(false);
        }

        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isUp()) {
            player.setAimUp(true);
        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isUp()) {
            player.setAimUp(false);
        }

        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isDown()) {
            player.setAimDown(true);
        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isDown()) {
            player.setAimDown(false);
        }

        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isButton4()) {
            player.jump();

        }

        if (keyboardControlEvent.isButton2()) {
            player.shoot(keyboardControlEvent.isKeyDown());

        }
    }

}
