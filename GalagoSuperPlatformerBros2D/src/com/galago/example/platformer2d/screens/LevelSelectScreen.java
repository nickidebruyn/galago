/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.game.LevelDefinition;
import com.galago.example.platformer2d.ui.HomeButton;
import com.galago.example.platformer2d.ui.LevelButton;

/**
 *
 * @author Nidebruyn
 */
public class LevelSelectScreen extends AbstractScreen {

    protected MainApplication mainApplication;
    protected GridPanel gridPanel;
    protected HomeButton menuButton;
    protected Label heading;
    private Game game;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        heading = new Label(hudPanel, "Levels", 64, window.getWidth(), 100);
        heading.centerTop(0, 10);

        menuButton = new HomeButton(hudPanel);
        menuButton.leftBottom(10, 10);
        menuButton.addTouchButtonListener(new TouchButtonAdapter() {
            
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String id) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("menu");
                }
            }
        });
        
        gridPanel = new GridPanel(hudPanel, window.getWidth()*0.6f, window.getHeight() * 0.6f);
        hudPanel.add(gridPanel);
        for (int j = 0; j < mainApplication.getLevelManager().getLevels().size(); j++) {
            LevelDefinition ld = mainApplication.getLevelManager().getLevels().get(j);
            final LevelButton button = new LevelButton(gridPanel, ld);
            button.addTouchButtonListener(new TouchButtonAdapter() {
                @Override
                public void doTouchUp(float touchX, float touchY, float tpf, String id) {
                    if (isActive()) {                       
                        baseApplication.getSoundManager().playSound("button");
                        mainApplication.getPlayScreen().setTest(false);
                        mainApplication.getPlayScreen().setLevelDefinition(button.getLevelDefinition());
                        showScreen("play");
                    }
                }
            });

        }
        gridPanel.layout(3, 3);

    }

    @Override
    protected void load() {

        game = new Game((MainApplication)baseApplication, rootNode);
        rootNode.attachChild(game.getSky("sky-autumn1"));
        camera.setLocation(new Vector3f(0, 0, -10));

        ArrayList<Widget> widgets = gridPanel.getWidgets();
        for (int i = 0; i < widgets.size(); i++) {
            Widget widget = widgets.get(i);
            LevelButton levelButton = (LevelButton) widget;
            levelButton.refreshDisplay();

        }

    }

    @Override
    protected void show() {
        setPreviousScreen("menu");
        gridPanel.show();
    }

    @Override
    protected void exit() {
    }

    @Override
    public void doEscape(boolean touchEvent) {
        if (isActive() && isEnabled() && isInitialized()) {
            showScreen("menu");

        }

    }

    @Override
    protected void pause() {
        
    }
}
