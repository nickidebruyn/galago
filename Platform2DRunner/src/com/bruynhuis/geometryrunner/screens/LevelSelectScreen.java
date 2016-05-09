/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.geometryrunner.MainApplication;
import com.bruynhuis.geometryrunner.game.LevelDefinition;
import com.bruynhuis.geometryrunner.ui.Button;
import com.bruynhuis.geometryrunner.ui.LevelButton;

import java.util.ArrayList;


/**
 *
 * @author nidebruyn
 */
public class LevelSelectScreen extends AbstractScreen {

    protected MainApplication mainApplication;
    protected GridPanel gridPanel;
    protected Button menuButton;
    protected Label heading;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;
        
        Image backgroundImage = new Image(hudPanel, "Textures/Backgrounds/blue_land.png", 1300, 1300, true);

        heading = new Label(hudPanel, "Emoti Dash", 60, window.getWidth(), 100);
        heading.centerTop(0, 10);

        menuButton = new Button(hudPanel, "backbutton", "Menu");
        menuButton.centerBottom(0, 15);
        menuButton.addTouchButtonListener(new TouchButtonAdapter() {
            
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String id) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("menu");
                }
            }
        });
        
        gridPanel = new GridPanel(hudPanel, window.getWidth()*0.9f, window.getHeight() * 0.7f);
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
        
        int completedLevel = mainApplication.getGameSaves().getGameData().getCompletedLevel();
        ArrayList<Widget> widgets = gridPanel.getWidgets();
        for (int i = 0; i < widgets.size(); i++) {
            Widget widget = widgets.get(i);
            LevelButton levelButton = (LevelButton) widget;
            if (levelButton.getLevelDefinition().getUid() <= completedLevel) {
                levelButton.setEnabled(true);

            } else {
                levelButton.setEnabled(false);

            }
        }

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
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
