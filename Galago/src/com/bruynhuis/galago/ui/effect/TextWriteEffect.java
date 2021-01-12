/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.effect;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.Timer;

/**
 * The wobble effect will make the widget scale up and down infanitely.
 *
 * @author nidebruyn
 */
public class TextWriteEffect extends Effect {

    private String text;
    private Timer letterTimer;
    private Label label;
    private int letterPosition = 0;

    public TextWriteEffect(Label label, float timePerLetter) {
        super(label);
        this.label = label;
        this.letterTimer = new Timer(timePerLetter);

    }

    @Override
    protected void doShow() {

        text = label.getText();
        this.letterTimer.start();
        label.setText(" ");
        letterPosition = 0;

    }

    @Override
    protected void doHide() {
    }

    @Override
    protected void doTouchDown() {
    }

    @Override
    protected void doTouchUp() {
    }

    @Override
    protected void doEnabled(boolean enabled) {
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (widget != null && widget.isVisible()) {

            letterTimer.update(tpf);

            if (letterTimer.finished()) {
                letterPosition++;
                label.setText(pad(text.substring(0, letterPosition)));

                if (letterPosition < text.length()) {
                    letterTimer.reset();
                } else {
                    letterTimer.stop();
                }

            }
        }
    }

    protected String pad(String str) {
        if (text != null) {
            if (str.length() < text.length()) {
                for (int i = str.length(); i < text.length(); i++) {
                    str = str + " ";

                }
            }
        }

        return str;
    }

    @Override
    protected void doSelected() {
    }

    @Override
    protected void doUnselected() {
    }
    
    @Override
    protected void doHoverOver() {
        
    }

    @Override
    protected void doHoverOff() {
        
    }
    
}
