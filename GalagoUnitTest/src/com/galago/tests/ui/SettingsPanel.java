/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.Checkbox;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VFlowPanel;

/**
 *
 * @author NideBruyn
 */
public class SettingsPanel extends Panel {

    private TouchButton settingsButton;
    private boolean open = true;
    private boolean animating = false;
    private VFlowPanel optionsPanel;
    private float animationSpeed = 0.5f;

    public SettingsPanel(Panel parent) {
        super(parent, "Interface/settings-panel.png", 300, 890);

        settingsButton = new TouchButton(this, "settings-button", "Interface/gear.png", 32, 32, true);
        settingsButton.setText("");
        settingsButton.rightTop(8, 22);
        settingsButton.addEffect(new TouchEffect(settingsButton));
        settingsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (open) {
                    closePanel();
                } else {
                    openPanel();
                }

            }

        });
        this.centerAt(-650, 0);
        parent.add(this);

        optionsPanel = new VFlowPanel(this, null, 255, 850);
        optionsPanel.leftTop(0, 12);
        this.add(optionsPanel);

    }
    
    public void addButtonListerner(TouchButtonListener buttonListener) {
        this.settingsButton.addTouchButtonListener(buttonListener);
        
    }

    public void openPanel() {

        if (!animating) {
            animating = true;
            this.moveFromToCenter(-910, 0, -650, 0, animationSpeed, 0, new TweenCallback() {
                @Override
                public void onEvent(int i, BaseTween<?> bt) {
                    animating = false;
                }
            });

            this.settingsButton.rotateFromTo(0, 360, animationSpeed, 0);
            open = true;
        }

    }

    public void closePanel() {

        if (!animating) {
            animating = true;
            this.moveFromToCenter(-650, 0, -910, 0, animationSpeed, 0, new TweenCallback() {
                @Override
                public void onEvent(int i, BaseTween<?> bt) {
                    animating = false;
                }
            });

            this.settingsButton.rotateFromTo(0, -360, animationSpeed, 0);

            open = false;
        }

    }

    public TouchButton addHeading(String title) {
        TouchButton button = new TouchButton(optionsPanel, title, "Interface/panel-heading.png", 255, 30);
        button.setText(title);
        button.setFontSize(16);
        button.setTextAlignment(TextAlign.LEFT);
        
        optionsPanel.layout();
        return button;

    }

    public Checkbox addCheckbox(String title, TouchButtonAdapter touchButtonAdapter) {
        Panel panel = new Panel(optionsPanel, null, 255, 30);
        optionsPanel.add(panel);

        Label label = new Label(panel, title, 16, 200, 30);
        label.setAlignment(TextAlign.LEFT);
        label.leftCenter(40, 0);

        Checkbox checkbox = new Checkbox(panel, title, 28, 28, false);
        checkbox.leftCenter(4, 0);
        
        if (touchButtonAdapter != null) {
            checkbox.addTouchButtonListener(touchButtonAdapter);
        }        

        optionsPanel.layout();
        return checkbox;

    }
    
    public TouchButton addButton(String title, TouchButtonAdapter touchButtonAdapter) {
        TouchButton button = new TouchButton(optionsPanel, title, "Interface/button.png", 240, 30);
        button.setText(title);
        button.setFontSize(16);
        button.setTextAlignment(TextAlign.LEFT);
        button.addEffect(new TouchEffect(button));

        if (touchButtonAdapter != null) {
            button.addTouchButtonListener(touchButtonAdapter);
        }             
        
        optionsPanel.layout();
        return button;

    }

}
