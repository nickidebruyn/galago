/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.test2d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.galago.test2d.spritebatching.BatchingScreen;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class MenuScreen extends AbstractScreen {

    public static final String NAME = "MenuScreen";

    private TouchButton statsButton;

    protected void addSceneButton(GridPanel gridPanel, final String screenName, String label) {
        final TouchButton sceneButton = new TouchButton(gridPanel, screenName, label);
        sceneButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(screenName);
                }
            }
        });

    }

    @Override
    protected void init() {

        Label head = new Label(hudPanel, "Galago 2D Examples", 46, 600, 100);
        head.setTextColor(ColorRGBA.Yellow);
        head.centerTop(0, 0);

        GridPanel gridPanel = new GridPanel(hudPanel, 800, 450);
        hudPanel.add(gridPanel);

        addSceneButton(gridPanel, BatchingScreen.NAME, "Sprite Batching");

        gridPanel.layout(8, 3);

        statsButton = new TouchButton(hudPanel, "statsButton", "Stats");
        statsButton.rightBottom(2, 2);
        statsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.showStats();
                }
            }
        });

        gridPanel.center();
    }

    @Override
    protected void load() {
        
    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {

    }

    @Override
    protected void pause() {

    }
}
