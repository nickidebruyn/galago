/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class ScoreHeaderPanel extends Panel {
    
    private Image scoreImage;
    private Image timeImage;
    
    private Label scoreLabel;
    private Label timeLabel;
    
    public static float scale = 0.8f;

    public ScoreHeaderPanel(Panel parent) {
        super(parent, null, parent.getWindow().getWidth(), 80);
        
        scoreImage = new Image(this, "Interface/header-score.png", 348*scale, 76*scale, true);
        scoreImage.leftCenter(10, 0);
        
        timeImage = new Image(this, "Interface/header-time.png", 354*scale, 78*scale, true);
        timeImage.rightCenter(10, 0);
        
        scoreLabel = new Label(this, "0", 42, 200, 60);
        scoreLabel.setTextColor(ColorRGBA.White);
        scoreLabel.setAlignment(TextAlign.LEFT);
        scoreLabel.setVerticalAlignment(TextAlign.CENTER);
        scoreLabel.leftCenter(50, 0);
        
        timeLabel = new Label(this, "0", 42, 200, 60);
        timeLabel.setTextColor(ColorRGBA.White);
        timeLabel.setAlignment(TextAlign.RIGHT);
        timeLabel.setVerticalAlignment(TextAlign.CENTER);
        timeLabel.rightCenter(50, 0);
        
        parent.add(this);
    }
    
    public void setScore(int score) {
        scoreLabel.setText("" + score);
        
    }
    
    public void setTime(int time) {
        if (time >= 10) {
            if (time >= 60) {
                int sec = time - 60;
                
                if (sec >= 10) {
                    timeLabel.setText("01:" + sec);
                } else {
                    timeLabel.setText("01:0" + sec);
                }                
                
            } else {
                timeLabel.setText("00:" + time);
            }
            
        } else {
            timeLabel.setText("00:0" + time);
        }
        
    }
}
