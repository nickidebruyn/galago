/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.field.TextArea;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.input.Joystick;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class JoystickRawScreen extends AbstractScreen implements RawInputListener {
    
    private TouchButton clearButton;
    private TextArea info;

    @Override
    protected void init() {
        
        clearButton = new TouchButton(hudPanel, "clear button", "Clear");
        clearButton.centerTop(5, 5);
        clearButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    info.clear();                    
                }
            }
            
        });
        
        info = new TextArea(hudPanel, "output field", "Resources/textfield.png", 1280, 400);
        info.setTextColor(ColorRGBA.Green);
        info.setFontSize(18);
        info.setMaxLines(16);
        info.centerBottom(0, 50);
        
    }

    @Override
    protected void load() {
        this.inputManager.addRawInputListener(this);
        info.clear();
    }

    @Override
    protected void show() {
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks == null || joysticks.length <= 0) {
            info.append("No joystick were found");
        } else {
            info.append("Joystick found");
        }
            
    }

    @Override
    protected void exit() {
        inputManager.removeRawInputListener(this);
    }

    @Override
    protected void pause() {
        
    }
    
   public void onJoyAxisEvent(JoyAxisEvent evt) {
       if (evt.getAxis().getJoystick() != null) {
//           info.append("Axis id: " + evt.getAxis().getAxisId());
       }        

    }

    public void onJoyButtonEvent(JoyButtonEvent evt) {
        if (evt.getButton() != null) {
            info.append("Joystick Button id: " + evt.getButton().getButtonId());
        }        

    }

    public void beginInput() {
//        info.append("Begin input");
    }

    public void endInput() {
//        info.append("End input");
    }

    public void onMouseMotionEvent(MouseMotionEvent evt) {
        info.append("Motion event: " + evt.getDX());
    }

    public void onMouseButtonEvent(MouseButtonEvent evt) {
        info.append("Mouse button event: " + evt.getButtonIndex());
    }

    public void onKeyEvent(KeyInputEvent evt) {
        info.append("Key event: " + evt.getKeyCode());
    }

    public void onTouchEvent(TouchEvent evt) {
        if (!evt.isConsumed()) {
            info.append("Touched: x=" + evt.getX() + "; y=" + evt.getY() + "; keycode=" + evt.getKeyCode() + "; type=" + evt.getType() + "; dx=" + evt.getDeltaX() + "; dy=" + evt.getDeltaY());
        }        

    }
}
