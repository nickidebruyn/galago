/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.screen;

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
import com.jme3.math.ColorRGBA;

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
        super(parent, "Resources/editor/panel-left.png", 300, 900);

        settingsButton = new TouchButton(this, "settings-button", "Resources/editor/icon-settings.png", 54, 54, true);
        settingsButton.setText("");
        settingsButton.rightTop(-40, 10);
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

        optionsPanel = new VFlowPanel(this, null, 280, 900);
        optionsPanel.leftTop(0, 0);
        this.add(optionsPanel);

    }

    public void addButtonListerner(TouchButtonListener buttonListener) {
        this.settingsButton.addTouchButtonListener(buttonListener);

    }

    public void openPanel() {

        if (!animating) {
            animating = true;
            this.moveFromToCenter(-930, 0, -650, 0, animationSpeed, 0, new TweenCallback() {
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
            this.moveFromToCenter(-650, 0, -930, 0, animationSpeed, 0, new TweenCallback() {
                @Override
                public void onEvent(int i, BaseTween<?> bt) {
                    animating = false;
                }
            });

            this.settingsButton.rotateFromTo(0, -360, animationSpeed, 0);

            open = false;
        }

    }

    public TouchButton addHeading(String title, ColorRGBA textColor, ColorRGBA backgroundColor) {
        TouchButton button = new TouchButton(optionsPanel, title, "Resources/editor/panel-heading.png", 280, 26);
        button.setText(title);
        button.setFontSize(16);
        button.setTextAlignment(TextAlign.LEFT);
        button.setTextVerticalAlignment(TextAlign.CENTER);
        button.setBackgroundColor(backgroundColor);
        button.setTextColor(textColor);

        optionsPanel.layout();
        return button;

    }

    public Checkbox addCheckbox(String title, ColorRGBA textColor, TouchButtonAdapter touchButtonAdapter) {
        Panel panel = new Panel(optionsPanel, null, 255, 36);
        optionsPanel.add(panel);

        Label label = new Label(panel, title, 16, 200, 36);
        label.setAlignment(TextAlign.LEFT);
        label.setTextColor(textColor);
        label.leftCenter(40, 0);

        Checkbox checkbox = new Checkbox(panel, title, 36, 36, false);
        checkbox.leftCenter(4, 0);

        if (touchButtonAdapter != null) {
            checkbox.addTouchButtonListener(touchButtonAdapter);
        }

        optionsPanel.layout();
        return checkbox;

    }

    public TouchButton addButton(String title, ColorRGBA textColor, ColorRGBA backgroundColor, TouchButtonAdapter touchButtonAdapter) {
        TouchButton button = new TouchButton(optionsPanel, title, "Resources/editor/button-shadow.png", 240, 50);
        button.setText(title);
        button.setFontSize(18);
        button.setTextAlignment(TextAlign.CENTER);
        button.setTextColor(textColor);
        button.setBackgroundColor(backgroundColor);
        button.addEffect(new TouchEffect(button));

        if (touchButtonAdapter != null) {
            button.addTouchButtonListener(touchButtonAdapter);
        }

        optionsPanel.layout();
        return button;

    }

}
