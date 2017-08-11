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
//            log("Fire event in: " + joystickListeners.size());
            for (Iterator<JoystickListener> it = joystickListeners.iterator(); it.hasNext();) {
                JoystickListener joystickListener = it.next();
                if (joystickListener != null) {
                    joystickListener.stick(event, tpf);
                }
            }

//            if (event.isLeft() || event.isRight() || event.isUp() || event.isDown()) {
//                if (!event.isKeyDown()) {
//                    event.clearAxis();
//                }
//            }

        }
    }

    public void onJoyAxisEvent(JoyAxisEvent evt) {

//        log("joystickEvent: " + joystickEvent.isButton3());

        if (evt.getAxis().getJoystick() != null && !evt.isConsumed()) {
            
//            log("Logical Axis ID: " + evt.getAxis().getLogicalId() + "   Val: " + evt.getValue() + "     Analog: " + evt.getAxis().isAnalog());
            
            //Exclusions
            if (evt.getValue() == 0 && evt.getAxis().getAxisId() == 6) {
                return;
            }

            //Elliminate small values
            float val = evt.getValue();
            if (val > -0.1f && val < 0.1f) {
                val = 0f;
            }
            
            //Set the analog value
            joystickEvent.setAnalogValue(val);

            //First check to see if button is down
            if ((val <= 1f) && (val >= -1f) && val != 0) {             
                
//                if (evt.getAxis().getAxisId() == 5 || evt.getAxis().getAxisId() == 1 || evt.getAxis().getAxisId() == 3) {
                if (evt.getAxis().getLogicalId().endsWith("x")) {
                    joystickEvent.setRight(val > 0);
                    joystickEvent.setLeft(val < 0);
                    joystickEvent.setDown(false);
                    joystickEvent.setUp(false);
                    
//                } else if (evt.getAxis().getAxisId() == 0 || evt.getAxis().getAxisId() == 6) {
                } else if (evt.getAxis().getLogicalId().endsWith("y")) {
                    joystickEvent.setRight(false);
                    joystickEvent.setLeft(false);
                    if (evt.getAxis().isAnalog()) {
                        joystickEvent.setDown(val > 0);
                        joystickEvent.setUp(val < 0);
                    } else {
                        joystickEvent.setDown(val < 0);
                        joystickEvent.setUp(val > 0);
                    }
                    
                }
//                else if (evt.getAxis().getAxisId() == 2) {
//                    //Reverse if joystick
//                    joystickEvent.setRight(false);
//                    joystickEvent.setLeft(false);
//                    joystickEvent.setDown(val > 0);
//                    joystickEvent.setUp(val < 0);
//                    
//                }

                joystickEvent.setAxisDown(true);
                fireJoystickEvent(joystickEvent, 1);
                return;
                
            } else if (joystickEvent.isLeft() || joystickEvent.isRight() || joystickEvent.isUp() || joystickEvent.isDown()) {
                //Now we check if a button was down and if it is now released
                joystickEvent.setAxisDown(false);
                fireJoystickEvent(joystickEvent, 1);
                joystickEvent.setRight(false);
                joystickEvent.setLeft(false);
                joystickEvent.setDown(false);
                joystickEvent.setUp(false);
                return;
            } else {
                //Do nothing because it was already consumed
                return;
            }


//            if (evt.getAxis().getAxisId() == 5 || evt.getAxis().getAxisId() == 1 || evt.getAxis().getAxisId() == 3) {
//                
//                if ((val <= 1f) && (val >= -1f) && val != 0) {
//                    joystickEvent.setRight(val > 0);
//                    joystickEvent.setLeft(val < 0);
//                    joystickEvent.setAxisDown(true);
//                    fireJoystickEvent(joystickEvent, 1);
//                    return;
//                } else if (joystickEvent.isLeft() || joystickEvent.isRight()) {
//                    joystickEvent.setAxisDown(false);
//                    fireJoystickEvent(joystickEvent, 1);
//                    joystickEvent.setRight(false);
//                    joystickEvent.setLeft(false);
//                    return;
//                } else {
//                    //Do nothing because it was already consumed
//                    return;
//                }
//
//            }
//
//            if (evt.getAxis().getAxisId() == 0 || evt.getAxis().getAxisId() == 2 || evt.getAxis().getAxisId() == 6) {
//                if ((val <= 1f) && (val >= -1f) && val != 0) {
//                    joystickEvent.setDown(val < 0);
//                    joystickEvent.setUp(val > 0);
//                    joystickEvent.setAxisDown(true);
//                    fireJoystickEvent(joystickEvent, 1);
//                    return;
//                } else if (joystickEvent.isUp() || joystickEvent.isDown()) {
//                    joystickEvent.setAxisDown(false);
//                    fireJoystickEvent(joystickEvent, 1);
//                    joystickEvent.setDown(false);
//                    joystickEvent.setUp(false);
//                    return;
//                } else {
//                    //Do nothing because it was already consumed
//                    return;
//                }
//            }

        }

    }

    public void onJoyButtonEvent(JoyButtonEvent evt) {
        if (evt.getButton().getJoystick() != null && !evt.isConsumed()) {
            log("Button = " + evt.getButton().getButtonId() + ";   down = " + evt.isPressed());
//            joystickEvent.clearAll();

            joystickEvent.setButtonDown(evt.isPressed());

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

            if (evt.getButton().getButtonId() == 0) {
                joystickEvent.setButton1(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 1) {
                joystickEvent.setButton2(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 2) {
                joystickEvent.setButton3(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 3) {
                joystickEvent.setButton4(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 4) {
                joystickEvent.setButton5(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 5) {
                joystickEvent.setButton6(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 6) {
                joystickEvent.setButton7(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 7) {
                joystickEvent.setButton8(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 8) {
                joystickEvent.setButton9(evt.isPressed());
            }
            if (evt.getButton().getButtonId() == 9) {
                joystickEvent.setButton10(evt.isPressed());
            }
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

//            joystickEvent.clearAll();

            if (evt.getType().equals(TouchEvent.Type.KEY_DOWN)) {

                if (evt.getKeyCode() == 19 && !joystickEvent.isUp()) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setUp(true);
                    joystickEvent.setAxisDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 20 && !joystickEvent.isDown()) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setDown(true);
                    joystickEvent.setAxisDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 21 && !joystickEvent.isLeft()) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setLeft(true);
                    joystickEvent.setAxisDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 22 && !joystickEvent.isRight()) {
                    joystickEvent.setAnalogValue(1);
                    joystickEvent.setRight(true);
                    joystickEvent.setAxisDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }

                //Now for buttons
                if (evt.getKeyCode() == 96 && !joystickEvent.isButton3()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton3(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 97 && !joystickEvent.isButton4()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton4(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 99 && !joystickEvent.isButton1()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton1(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 100 && !joystickEvent.isButton2()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton2(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 102 && !joystickEvent.isButton5()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton5(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 103 && !joystickEvent.isButton6()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton6(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 108 && !joystickEvent.isButton9()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton9(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }
                if (evt.getKeyCode() == 109 && !joystickEvent.isButton10()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setButton10(true);
                    joystickEvent.setButtonDown(true);
                    fireJoystickEvent(joystickEvent, 1);
                    return;
                }

            }

            if (evt.getType().equals(TouchEvent.Type.KEY_UP)) {

                if ((evt.getKeyCode() == 19 || evt.getKeyCode() == 20 || evt.getKeyCode() == 21 || evt.getKeyCode() == 22)
                        && joystickEvent.isAxisDown()) {
                    joystickEvent.setAnalogValue(0);
                    joystickEvent.setAxisDown(false);
                    fireJoystickEvent(joystickEvent, 1);
                    joystickEvent.clearAxis();
                    return;

                } else if (joystickEvent.isButtonDown()) {
                    joystickEvent.setButtonDown(false);
                    joystickEvent.setAnalogValue(0);
                    fireJoystickEvent(joystickEvent, 1);
                    joystickEvent.clearButtons();
                    return;
                }

            }
        }
    }
}
