package com.galago.editor.ui;

import com.bruynhuis.galago.ui.button.TouchButton;

/**
 *
 * @author ndebruyn
 */
public class ButtonGroup {
    
    private TouchButton button1;
    private TouchButton button2;

    public ButtonGroup(TouchButton button1, TouchButton button2) {
        this.button1 = button1;
        this.button2 = button2;
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
    
    
}
