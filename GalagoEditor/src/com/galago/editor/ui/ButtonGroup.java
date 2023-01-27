package com.galago.editor.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.button.TouchButton;

/**
 *
 * @author ndebruyn
 */
public class ButtonGroup {
    
    private TouchButton button1;
    private TouchButton button2;
    private Image backImage1;
    private Image backImage2;

    public ButtonGroup(TouchButton button1, TouchButton button2) {
        this.button1 = button1;
        this.button2 = button2;
    }

    public ButtonGroup(TouchButton button1, TouchButton button2, Image backImage1, Image backImage2) {
        this.button1 = button1;
        this.button2 = button2;
        this.backImage1 = backImage1;
        this.backImage2 = backImage2;
    }

    public TouchButton getButton1() {
        return button1;
    }

    public void setButton1(TouchButton button1) {
        this.button1 = button1;
    }

    public TouchButton getButton2() {
        return button2;
    }

    public void setButton2(TouchButton button2) {
        this.button2 = button2;
    }

    public Image getBackImage1() {
        return backImage1;
    }

    public void setBackImage1(Image backImage1) {
        this.backImage1 = backImage1;
    }

    public Image getBackImage2() {
        return backImage2;
    }

    public void setBackImage2(Image backImage2) {
        this.backImage2 = backImage2;
    }
    
    
}
