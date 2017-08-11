/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

/**
 * This class will contain pick data when a user touches the screen and drag the
 * screen.
 *
 * @author nidebruyn
 */
public class JoystickEvent {

    private boolean axisDown = false;
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private float analogValue;
    
    private boolean buttonDown = false;
    private boolean button1 = false;
    private boolean button2 = false;
    private boolean button3 = false;
    private boolean button4 = false;
    private boolean button5 = false;
    private boolean button6 = false;
    private boolean button7 = false;
    private boolean button8 = false;
    private boolean button9 = false;
    private boolean button10 = false;

    public float getAnalogValue() {
        return analogValue;
    }

    public void setAnalogValue(float analogValue) {
        this.analogValue = analogValue;
    }

    public void setAxisDown(boolean axisDown) {
        this.axisDown = axisDown;
    }

    public boolean isButtonDown() {
        return buttonDown;
    }

    public void setButtonDown(boolean keyDown) {
        this.buttonDown = keyDown;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isButton1() {
        return button1;
    }

    public void setButton1(boolean button1) {
        this.button1 = button1;
    }

    public boolean isButton2() {
        return button2;
    }

    public void setButton2(boolean button2) {
        this.button2 = button2;
    }

    public boolean isButton3() {
        return button3;
    }

    public void setButton3(boolean button3) {
        this.button3 = button3;
    }

    public boolean isButton4() {
        return button4;
    }

    public void setButton4(boolean button4) {
        this.button4 = button4;
    }

    public boolean isButton5() {
        return button5;
    }

    public void setButton5(boolean button5) {
        this.button5 = button5;
    }

    public boolean isButton6() {
        return button6;
    }

    public void setButton6(boolean button6) {
        this.button6 = button6;
    }

    public boolean isButton7() {
        return button7;
    }

    public void setButton7(boolean button7) {
        this.button7 = button7;
    }

    public boolean isButton8() {
        return button8;
    }

    public void setButton8(boolean button8) {
        this.button8 = button8;
    }

    public boolean isButton9() {
        return button9;
    }

    public void setButton9(boolean button9) {
        this.button9 = button9;
    }

    public boolean isButton10() {
        return button10;
    }

    public void setButton10(boolean button10) {
        this.button10 = button10;
    }
//
//    public void clearAll() {
//        buttonDown = false;
//        left = false;
//        right = false;
//        up = false;
//        down = false;
//        analogValue = 0;
//        button1 = false;
//        button2 = false;
//        button3 = false;
//        button4 = false;
//        button5 = false;
//        button6 = false;
//        button7 = false;
//        button8 = false;
//        button9 = false;
//        button10 = false;
//    }
    
    public void clearButtons() {
        button1 = false;
        button2 = false;
        button3 = false;
        button4 = false;
        button5 = false;
        button6 = false;
        button7 = false;
        button8 = false;
        button9 = false;
        button10 = false;
    }
    
    public void clearAxis() {
        left = false;
        right = false;
        up = false;
        down = false;
        analogValue = 0;
    }
    
    public boolean isAxisDown() {
        return axisDown;
//        return isLeft() || isRight() || isUp() || isDown();
    }

    @Override
    public String toString() {
        return "JoystickEvent{" + "axisDown=" + axisDown + ", left=" + left + ", right=" + right + ", up=" + up + ", down=" + down + ", analogValue=" + analogValue + ", buttonDown=" + buttonDown + ", button1=" + button1 + ", button2=" + button2 + ", button3=" + button3 + ", button4=" + button4 + ", button5=" + button5 + ", button6=" + button6 + ", button7=" + button7 + ", button8=" + button8 + ", button9=" + button9 + ", button10=" + button10 + '}';
    }
    
    
}
