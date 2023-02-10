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
    private TouchButton removeButton1;
    private TouchButton removeButton2;
    private TouchButton horizontalFlipButton1;
    private TouchButton horizontalFlipButton2;
    private TouchButton verticalFlipButton1;
    private TouchButton verticalFlipButton2;

//    public ButtonGroup(TouchButton button1, TouchButton button2) {
//        this.button1 = button1;
//        this.button2 = button2;
//    }
//
//    public ButtonGroup(TouchButton button1, TouchButton button2, Image backImage1, Image backImage2) {
//        this.button1 = button1;
//        this.button2 = button2;
//        this.backImage1 = backImage1;
//        this.backImage2 = backImage2;
//    }

    public ButtonGroup(TouchButton button1, TouchButton button2, Image backImage1, Image backImage2, TouchButton removeButton1, TouchButton removeButton2) {
        this.button1 = button1;
        this.button2 = button2;
        this.backImage1 = backImage1;
        this.backImage2 = backImage2;
        this.removeButton1 = removeButton1;
        this.removeButton2 = removeButton2;
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
    
    public void setLeftButtonVisible(boolean visible) {
        this.button1.setVisible(visible);
        this.backImage1.setVisible(visible);
        this.removeButton1.setVisible(visible);
        
        if (horizontalFlipButton1 != null) {
            horizontalFlipButton1.setVisible(visible);            
        }
        if (verticalFlipButton1 != null) {
            verticalFlipButton1.setVisible(visible);
        }
        
    }    
    
    public void setRightButtonVisible(boolean visible) {
        this.button2.setVisible(visible);
        this.backImage2.setVisible(visible);
        this.removeButton2.setVisible(visible);
        
        if (horizontalFlipButton2 != null) {
            horizontalFlipButton2.setVisible(visible);            
        }
        if (verticalFlipButton2 != null) {
            verticalFlipButton2.setVisible(visible);
        }        
        
    }    

    public TouchButton getRemoveButton1() {
        return removeButton1;
    }

    public void setRemoveButton1(TouchButton removeButton1) {
        this.removeButton1 = removeButton1;
    }

    public TouchButton getRemoveButton2() {
        return removeButton2;
    }

    public void setRemoveButton2(TouchButton removeButton2) {
        this.removeButton2 = removeButton2;
    }

    public TouchButton getHorizontalFlipButton1() {
        return horizontalFlipButton1;
    }

    public void setHorizontalFlipButton1(TouchButton horizontalFlipButton1) {
        this.horizontalFlipButton1 = horizontalFlipButton1;
    }

    public TouchButton getHorizontalFlipButton2() {
        return horizontalFlipButton2;
    }

    public void setHorizontalFlipButton2(TouchButton horizontalFlipButton2) {
        this.horizontalFlipButton2 = horizontalFlipButton2;
    }

    public TouchButton getVerticalFlipButton1() {
        return verticalFlipButton1;
    }

    public void setVerticalFlipButton1(TouchButton verticalFlipButton1) {
        this.verticalFlipButton1 = verticalFlipButton1;
    }

    public TouchButton getVerticalFlipButton2() {
        return verticalFlipButton2;
    }

    public void setVerticalFlipButton2(TouchButton verticalFlipButton2) {
        this.verticalFlipButton2 = verticalFlipButton2;
    }
    
}
