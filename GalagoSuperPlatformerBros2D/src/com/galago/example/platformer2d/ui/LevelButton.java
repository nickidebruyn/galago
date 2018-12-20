/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;
import com.galago.example.platformer2d.game.LevelDefinition;

/**
 *
 * @author Nidebruyn
 */
public class LevelButton extends TouchButton {

    private LevelDefinition levelDefinition;
    private static float scale = 0.5f;

    public LevelButton(Panel panel, LevelDefinition levelDefinition) {
        super(panel, "level-button-" + levelDefinition.getUid(), "Interface/button-level-off.png", 256 * scale, 256 * scale, true);
        this.addEffect(new TouchEffect(this));
        this.setTextColor(ColorRGBA.White);
        this.setFontSize(42);
        this.setText("" + levelDefinition.getUid());
        this.levelDefinition = levelDefinition;
    }

    public LevelDefinition getLevelDefinition() {
        return levelDefinition;
    }

    public void refreshDisplay() {
        int completedLevel = window.getApplication().getGameSaves().getGameData().getCompletedLevel();
        
        if (levelDefinition.getUid() <= completedLevel) {
            updatePicture("Interface/button-level-1stars.png");
            setEnabled(true);
        } else {
            updatePicture("Interface/button-level-off.png");
            setEnabled(false);
        }

    }
}
