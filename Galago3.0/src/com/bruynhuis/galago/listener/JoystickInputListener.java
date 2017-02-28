/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.listener;

import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The RawInputListener can be used by a user to detect when a joystick action
 * was performed.
 *
 * @author NideBruyn
 */
public class JoystickInputListener implements RawInputListener {
    
    private ArrayList<JoystickListener> joystickListeners = new ArrayList<>();
    private InputManager inputManager;
    private boolean enabled = true;
    private JoystickEvent joystickEvent;

    public JoystickInputListener() {
        joystickEvent = new JoystickEvent();

    }
    
    public boolean hasJoystick() {
        return inputManager.getJoysticks() != null && inputManager.getJoysticks().length > 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        this.inputManager.addRawInputListener(this);
    }

    public void unregisterInput() {

        if (inputManager == null) {
            return;
        }

        inputManager.removeRawInputListener(this);

    }

    /**
     * Log some text to the console
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }

    public void addJoystickListener(JoystickListener joystickListener1) {
        this.joystickListeners.add(joystickListener1);
    }
    
    public void removeJoystickListener(JoystickListener joystickListener1) {
        this.joystickListeners.remove(joystickListener1);
    }
    
    private void fireJoystickEvent(JoystickEvent event, float tpf) {
        if (joystickListeners != null) {
            for (Iterator<JoystickListener> it = joystickListeners.iterator(); it.hasNext();) {
                JoystickListener joystickListener = it.next();
                if (joystickListener != null) {
                    joystickListener.stick(event, tpf);
                }
                
            }
            
        }
    }

    public void onJoyAxisEvent(JoyAxisEvent evt) {
        
//        log("joystickEvent: " + joystickEvent.isButton3());
        
        if (evt.getAxis().getJoystick() != null && !evt.isConsumed()) {
            
//            log("Value: " + evt.getValue());
            
            //Only for info
            if (evt.getValue() != 0) {
//                log("Axis: " + evt.getAxis().getAxisId());
            }
            
            //Set the analog value
            joystickEvent.setAnalogValue(evt.getValue());
            joystickEvent.setKeyDown(evt.getValue() >= -1.0f);
            
            //Check the directions
            if (evt.getValue() == 1) {
                
                if (evt.getAxis().getAxisId() == 5 || evt.getAxis().getAxisId() == 1) {
                    joystickEvent.setRight(true);
                }  
                
                if (evt.getAxis().getAxisId() == 6) {
                    joystickEvent.setUp(true);
                }  
                
                if (evt.getAxis().getAxisId() == 0) {
                    joystickEvent.setDown(true);
                }  
                
            } else if (evt.getValue() == -1) {
                
                if (evt.getAxis().getAxisId() == 5 || evt.getAxis().getAxisId() == 1) {
                    joystickEvent.setLeft(true);
                }
                
                if (evt.getAxis().getAxisId() == 6) {
                    joystickEvent.setDown(true);
                } 
                
                if (evt.getAxis().getAxisId() == 0) {
                    joystickEvent.setUp(true);
                } 
                
            } else {
                
                if (evt.getAxis().getAxisId() == 5 || evt.getAxis().getAxisId() == 1) {
                    joystickEvent.setLeft(false);
                    joystickEvent.setRight(false);
                }
                
                if (evt.getAxis().getAxisId() == 6 || evt.getAxis().getAxisId() == 0) {
                    joystickEvent.setUp(false);
                    joystickEvent.setDown(false);
                } 
                
            }
            
            fireJoystickEvent(joystickEvent, 1);
        }

    }

    public void onJoyButtonEvent(JoyButtonEvent evt) {
        if (evt.getButton().getJoystick() != null && !evt.isConsumed()) {
            log("Button = " + evt.getButton().getButtonId() + ";   down = " + evt.isPressed());
            
            joystickEvent.setKeyDown(evt.isPressed());
            
            joystickEvent.setButton1(evt.getButton().getButtonId() == 0);
            joystickEvent.setButton2(evt.getButton().getButtonId() == 1);
            joystickEvent.setButton3(evt.getButton().getButtonId() == 2);
            joystickEvent.setButton4(evt.getButton().getButtonId() == 3);
            joystickEvent.setButton5(evt.getButton().getButtonId() == 4);
            joystickEvent.setButton6(evt.getButton().getButtonId() == 5);
            joystickEvent.setButton7(evt.getButton().getButtonId() == 6);
            joystickEvent.setButton8(evt.getButton().getButtonId() == 7);
            joystickEvent.setButton9(evt.getButton().getButtonId() == 8);
            joystickEvent.setButton10(evt.getButton().getButtonId() == 9);
                            
            fireJoystickEvent(joystickEvent, 1);
            
            if (evt.getButton().getButtonId() == 0) joystickEvent.setButton1(evt.isPressed());
            if (evt.getButton().getButtonId() == 1) joystickEvent.setButton2(evt.isPressed());
            if (evt.getButton().getButtonId() == 2) joystickEvent.setButton3(evt.isPressed());
            if (evt.getButton().getButtonId() == 3) joystickEvent.setButton4(evt.isPressed());
            if (evt.getButton().getButtonId() == 4) joystickEvent.setButton5(evt.isPressed());
            if (evt.getButton().getButtonId() == 5) joystickEvent.setButton6(evt.isPressed());
            if (evt.getButton().getButtonId() == 6) joystickEvent.setButton7(evt.isPressed());
            if (evt.getButton().getButtonId() == 7) joystickEvent.setButton8(evt.isPressed());
            if (evt.getButton().getButtonId() == 8) joystickEvent.setButton9(evt.isPressed());
            if (evt.getButton().getButtonId() == 9) joystickEvent.setButton10(evt.isPressed());
        }

    }

    public void beginInput() {
    }

    public void endInput() {
    }

    public void onMouseMotionEvent(MouseMotionEvent evt) {
    }

    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    public void onKeyEvent(KeyInputEvent evt) {
    }

    public void onTouchEvent(TouchEvent evt) {
        if (!evt.isConsumed()) {
            if (evt.getType().equals(TouchEvent.Type.KEY_DOWN)) {
                
                if (evt.getKeyCode() == 19) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setUp(true);
                }
                if (evt.getKeyCode() == 20) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setDown(true);
                }
                if (evt.getKeyCode() == 21) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setLeft(true);
                }
                if (evt.getKeyCode() == 22) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setRight(true);
                }
                //Now for buttons
                if (evt.getKeyCode() == 96) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton3(true);
                }
                if (evt.getKeyCode() == 97) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton4(true);
                }
                if (evt.getKeyCode() == 99) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton1(true);
                }
                if (evt.getKeyCode() == 100) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton2(true);
                }
                if (evt.getKeyCode() == 102) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton5(true);
                }
                if (evt.getKeyCode() == 103) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton6(true);
                }
                if (evt.getKeyCode() == 108) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton9(true);
                }
                if (evt.getKeyCode() == 109) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton10(true);
                }
                
                joystickEvent.setKeyDown(true);
                fireJoystickEvent(joystickEvent, 1);
            }
            
            if (evt.getType().equals(TouchEvent.Type.KEY_UP)) {
                
                if (evt.getKeyCode() == 19) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setUp(false);
                }
                if (evt.getKeyCode() == 20) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setDown(false);
                }
                if (evt.getKeyCode() == 21) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setLeft(false);
                }
                if (evt.getKeyCode() == 22) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setRight(false);
                }
                
                //Now for buttons
                if (evt.getKeyCode() == 96) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton3(false);
                }
                if (evt.getKeyCode() == 97) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton4(false);
                }
                if (evt.getKeyCode() == 99) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton1(false);
                }
                if (evt.getKeyCode() == 100) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton2(false);
                }
                if (evt.getKeyCode() == 102) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton5(false);
                }
                if (evt.getKeyCode() == 103) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton6(false);
                }
                if (evt.getKeyCode() == 108) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton9(false);
                }
                if (evt.getKeyCode() == 109) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton10(false);
                }
                
                joystickEvent.setKeyDown(false);
                fireJoystickEvent(joystickEvent, 1);
            }
        }
    }
}
