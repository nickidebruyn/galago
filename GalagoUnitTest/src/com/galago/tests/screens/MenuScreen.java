/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
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

        Label head = new Label(hudPanel, "Menu", 46, 600, 100);
        head.setTextColor(ColorRGBA.Yellow);
        head.centerTop(0, 0);

        GridPanel gridPanel = new GridPanel(hudPanel, 800, 450);
        hudPanel.add(gridPanel);

        addSceneButton(gridPanel, "buttons", "UI Layouts");
        addSceneButton(gridPanel, "pager", "Pager Panel");
        addSceneButton(gridPanel, "batch", "Batch GUI");
        addSceneButton(gridPanel, "grid", "Grid Panel");
        addSceneButton(gridPanel, "input", "Input GUI");
        addSceneButton(gridPanel, "physics", "Physics Tests");
        addSceneButton(gridPanel, PhysicsJointScreen.NAME, "Physics Joints");
        addSceneButton(gridPanel, "joystick", "Joystick");
        addSceneButton(gridPanel, "rawjoystick", "Raw Joystick");
        addSceneButton(gridPanel, WorldEditorScreen.NAME, "World Editor");
        addSceneButton(gridPanel, "fire", "Fire Particles");
        addSceneButton(gridPanel, "watermovement", "Water Movement");
        addSceneButton(gridPanel, "waterwave", "Water Wave");
        addSceneButton(gridPanel, "postshader", "Post Shader");
        addSceneButton(gridPanel, "roadmesh", "Road Mesh");
        addSceneButton(gridPanel, "lightning", "Lightning sky");
        addSceneButton(gridPanel, "motionblur", "Motion Blur");
        addSceneButton(gridPanel, "trailrender", "Trail Render");
        addSceneButton(gridPanel, TextureMaskingScreen.NAME, "Texture Masking");
        addSceneButton(gridPanel, EggScreen.NAME, "Egg test");
        addSceneButton(gridPanel, RagdollScreen.NAME, "Ragdoll");
        addSceneButton(gridPanel, CombatScreen.NAME, "Combat");
        addSceneButton(gridPanel, SplatMarkerScreen.NAME, "Splat Mask");

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
