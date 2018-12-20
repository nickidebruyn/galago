/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.jme3.math.Vector3f;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.ui.EditButton;
import com.galago.example.platformer2d.ui.InfoButton;
import com.galago.example.platformer2d.ui.LikeButton;
import com.galago.example.platformer2d.ui.PlayButton;
import com.galago.example.platformer2d.ui.ScoreButton;
import com.galago.example.platformer2d.ui.SettingsButton;
import com.galago.example.platformer2d.ui.ShareButton;

/**
 *
 * @author Nidebruyn
 */
public class MenuScreen extends AbstractScreen {

    private Label heading;
    private Image headingMenuImage;
    private PlayButton playButton;
    private SettingsButton settingsButton;
    private ShareButton shareButton;
    private LikeButton likeButton;
    private ScoreButton scoreButton;
    private EditButton editButton;
    private InfoButton infoButton;
    private ButtonPanel buttonPanel;

    @Override
    protected void init() {

        heading = new Label(hudPanel, "Bounce", 74, window.getWidth(), 100);
        heading.centerTop(0, 10);

        headingMenuImage = new Image(hudPanel, "Interface/header-menu.png", 578, 138, true);
        headingMenuImage.centerAt(0, -100);

        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);

        playButton = new PlayButton(buttonPanel);
        playButton.centerAt(0, 0);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("levels");
                }
            }
        });

        settingsButton = new SettingsButton(buttonPanel);
        settingsButton.centerAt(-280, -232);
        settingsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("options");
                }
            }
        });

        infoButton = new InfoButton(buttonPanel);
        infoButton.centerAt(-140, -226);
        infoButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("about");
                }
            }
        });

        scoreButton = new ScoreButton(buttonPanel);
        scoreButton.centerAt(0, -220);
        scoreButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    int score = baseApplication.getGameSaves().getGameData().getScore();
                    baseApplication.doShowHighscores(MainApplication.LEADERBOARD_UID, score);
                }
            }
        });

        likeButton = new LikeButton(buttonPanel);
        likeButton.centerAt(140, -226);
        likeButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doRateApplication();
                }
            }
        });

        shareButton = new ShareButton(buttonPanel);
        shareButton.centerAt(280, -232);
        shareButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doShareApplication();
                }
            }
        });

        if (!baseApplication.isMobileApp()) {
            editButton = new EditButton(buttonPanel);
            editButton.rightBottom(10, 10);
            editButton.addTouchButtonListener(new TouchButtonAdapter() {
                @Override
                public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                    if (isActive()) {
                        baseApplication.getSoundManager().playSound("button");
                        showScreen("edit");
                    }
                }
            });
        }

    }

    @Override
    protected void load() {

        baseApplication.getSoundManager().playMusic("music");
        camera.setLocation(new Vector3f(0, 0, 10));

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        buttonPanel.show();
//        image.show();
    }

    @Override
    protected void exit() {
        baseApplication.getSoundManager().stopMusic("music");
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }
}
