/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.rpg2d.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.games.basic.BasicGameListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.galago.example.rpg2d.MainApplication;
import com.galago.example.rpg2d.game.Game;
import com.galago.example.rpg2d.game.Player;
import com.jme3.math.Vector3f;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements BasicGameListener, KeyboardControlListener, PickListener {

    public static final String NAME = "PlayScreen";
    private MainApplication mainApplication;
    private Label title;

    private Game game;
    private Player player;
    private KeyboardControlInputListener keyboardControlInputListener;
    private TouchPickListener touchPickListener;

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;

        title = new Label(hudPanel, "RPG", 32, 500, 50);
        title.centerTop(0, 20);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
        
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
        
        camera.setLocation(new Vector3f(0, 0, 10));
    }

    @Override
    protected void show() {

        title.show();
        title.moveFromToCenter(0, 500, 0, 300, 0.5f, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                game.start(player);
                keyboardControlInputListener.registerWithInput(inputManager);
                touchPickListener.registerWithInput(inputManager);
                title.fadeFromTo(1, 0, 1, 0.3f);
            }
        });

    }

    @Override
    protected void exit() {
        keyboardControlInputListener.unregisterInput();
        touchPickListener.unregisterInput();
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
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
//        player.setLeft(keyboardControlEvent.isLeft() && keyboardControlEvent.isKeyDown());
//        player.setRight(keyboardControlEvent.isRight() && keyboardControlEvent.isKeyDown());

//        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isRight()) {
//            player.setRight(true);
//        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isRight()) {
//            player.setRight(false);
//        }
//
//        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isLeft()) {
//            player.setLeft(true);
//        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isLeft()) {
//            player.setLeft(false);
//        }
//
//        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isUp()) {
//            player.setAimUp(true);
//        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isUp()) {
//            player.setAimUp(false);
//        }
//
//        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isDown()) {
//            player.setAimDown(true);
//        } else if (!keyboardControlEvent.isKeyDown() && keyboardControlEvent.isDown()) {
//            player.setAimDown(false);
//        }
//
//        if (keyboardControlEvent.isKeyDown() && keyboardControlEvent.isButton4()) {
//            player.jump();
//
//        }
//
//        if (keyboardControlEvent.isButton2()) {
//            player.shoot(keyboardControlEvent.isKeyDown());
//
//        }
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {
        
        if (isActive()) {            
            player.setTargetPosition(pickEvent.getContactPoint());
            
        }
        
    }

}
