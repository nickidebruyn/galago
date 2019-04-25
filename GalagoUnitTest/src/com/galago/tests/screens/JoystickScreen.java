/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickInputListener;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class JoystickScreen extends AbstractScreen implements JoystickListener {
    
    private JoystickInputListener joystickInputListener;
    private Image image;
    private boolean left, right, up, down = false;
    private float speed = 100;
    
    @Override
    protected void init() {
        
        image = new Image(hudPanel, "Interface/smile.png", 128, 128, true);
        image.center();

        
    }

    @Override
    protected void load() {         
        joystickInputListener = new JoystickInputListener();
        joystickInputListener.addJoystickListener(this);
        joystickInputListener.registerWithInput(inputManager);
    }

    @Override
    protected void show() {
        
    }

    @Override
    protected void exit() {
       joystickInputListener.unregisterInput();
    }

    @Override
    protected void pause() {
        
    }

    public void stick(JoystickEvent joystickEvent, float fps) {
        
        log("Stick index pressed");
        log("" + joystickEvent.toString());
        
        left = joystickEvent.isLeft() && joystickEvent.isAxisDown();
        right = joystickEvent.isRight() && joystickEvent.isAxisDown();
        up = joystickEvent.isUp() && joystickEvent.isAxisDown();
        down = joystickEvent.isDown() && joystickEvent.isAxisDown();
        
        speed = joystickEvent.getAnalogValue()*500;
        
        if (joystickEvent.isButton1() && joystickEvent.isButtonDown()) {
            baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Red);
        }
        if (joystickEvent.isButton2() && joystickEvent.isButtonDown()) {
            baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Blue);
        }
        if (joystickEvent.isButton3() && joystickEvent.isButtonDown()) {
            baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Green);
        }
        if (joystickEvent.isButton4() && joystickEvent.isButtonDown()) {
            baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Pink);
        }
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            
            if (left) {
                image.move(tpf*speed, 0);
                
            }
            if (right) {
                image.move(tpf*speed, 0);
                
            }
            if (up) {
                image.move(0, -tpf*speed);
                
            }
            if (down) {
                image.move(0, -tpf*speed);
                
            }   
            
        }
    }

    
    
}
