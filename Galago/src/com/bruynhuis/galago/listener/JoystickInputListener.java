/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.listener;

import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickConnectionListener;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import java.util.ArrayList;

/**
 * The RawInputListener can be used by a user to detect when a joystick action
 * was performed.
 *
 * @author NideBruyn
 */
public class JoystickInputListener implements RawInputListener, JoystickConnectionListener {

    private ArrayList<JoystickListener> joystickListeners = new ArrayList<>();
    private InputManager inputManager;
    private boolean enabled = true;
    private JoystickEvent joystickEvent;
    private boolean debug = true;
    private float deadZone = 0.1f;
    private boolean specialSetup = false;

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
//        log("Register joystick input");
        this.inputManager = inputManager;
        this.inputManager.addRawInputListener(this);
        this.inputManager.addJoystickConnectionListener(this);
    }

    public void unregisterInput() {
//        log("Unregister joystick input");

        if (inputManager == null) {
            return;
        }

        inputManager.removeRawInputListener(this);
        inputManager.removeJoystickConnectionListener(this);

    }

    /**
     * Log some text to the console
     *
     * @param text
     */
    protected void log(String text) {
        if (debug) {
            System.out.println(text);
        }
    }

    public void addJoystickListener(JoystickListener joystickListener1) {
//        log("Joystick Input Listener: adding listener");
        this.joystickListeners.add(joystickListener1);
    }

    public void removeJoystickListener(JoystickListener joystickListener1) {
//        log("Joystick Input Listener: remove listener");
        this.joystickListeners.remove(joystickListener1);
    }

    private void fireJoystickEvent(JoystickEvent event, float tpf) {

        if (joystickListeners != null) {
//            log("Fire joystick event in: " + joystickListeners.size());

            for (int i = 0; i < joystickListeners.size(); i++) {
                JoystickListener joystickListener = joystickListeners.get(i);
                if (joystickListener != null) {
                    joystickListener.stick(event, tpf);
                }
            }
            event.clearAxis();
            event.clearButtons();

//            if (event.isLeft() || event.isRight() || event.isUp() || event.isDown()) {
//                if (!event.isButtonDown()) {
//                    event.clearAxis();
//                }
//            }
        }
    }

    public void onJoyAxisEvent(JoyAxisEvent evt) {

//        log("joystickEvent: " + joystickEvent.isButton3());
        if (evt.getAxis().getJoystick() != null && !evt.isConsumed()) {
//            log("\n----------------------------------------------------------------");
//            log("> Start joystick axis input with id: " + evt.getJoyIndex());
//            log("> Axis id: " + evt.getAxis().getAxisId() + "; logical id: " + evt.getAxis().getLogicalId() + ";  Val: " + evt.getValue() + "; Analog: " + evt.getAxis().isAnalog() + "; Deadzone : " + evt.getAxis().getDeadZone());

//            //Exclusions
//            if (evt.getValue() == 0 && evt.getAxis().getAxisId() == 6) {
//                System.out.println("> Skip joystick axis");
//                return;
//            }
            joystickEvent.setJoyAxisEvent(evt);
//
//            //Elliminate small values
//            float val = evt.getValue();
//            if (val > -0.1f && val < 0.1f) {
//                log("> Setting val to 0");
//                val = 0f;
//            }

            //Set the analog value
            joystickEvent.setAnalogValue(evt.getValue());

            //Set the joystick index
            joystickEvent.setJoystickIndex(evt.getJoyIndex());

            if (evt.getAxis().getLogicalId().endsWith("x")) {
                if (evt.getValue() > -deadZone && evt.getValue() < deadZone) {
                    joystickEvent.setLeft(true);
                    joystickEvent.setRight(true);
                    joystickEvent.setHorizontal(true);
                    joystickEvent.setAxisDown(false);

                } else {
                    joystickEvent.setLeft(evt.getValue() <= -deadZone);
                    joystickEvent.setRight(evt.getValue() >= deadZone);
                    joystickEvent.setHorizontal(true);
                    joystickEvent.setAxisDown(true);

                }
                fireJoystickEvent(joystickEvent, 1);

            } else if (evt.getAxis().getLogicalId().endsWith("y")) {
                if (evt.getValue() > -deadZone && evt.getValue() < deadZone) {
                    joystickEvent.setUp(true);
                    joystickEvent.setDown(true);
                    joystickEvent.setVertical(true);
                    joystickEvent.setAxisDown(false);

                } else {
                    joystickEvent.setDown(evt.getValue() <= -deadZone);
                    joystickEvent.setUp(evt.getValue() >= deadZone);
                    joystickEvent.setVertical(true);
                    joystickEvent.setAxisDown(true);

                }
                fireJoystickEvent(joystickEvent, 1);

            }

//            //First check to see if button is down
//            if ((val <= 1f) && (val >= -1f) && val != 0) {
//
//                if (evt.getAxis().getLogicalId().endsWith("x")) {
//                    joystickEvent.setRight(val > 0);
//                    joystickEvent.setLeft(val < 0);
//                    joystickEvent.setHorizontal(true);
//                    joystickEvent.setVertical(false);
//                    joystickEvent.setDown(false);
//                    joystickEvent.setUp(false);
//                    joystickEvent.setAxisDown(true);
//                    fireJoystickEvent(joystickEvent, 1);
//                    log("> Do horizontal action on x");
//
//                } else if (evt.getAxis().getLogicalId().endsWith("y")) {
//                    joystickEvent.setRight(false);
//                    joystickEvent.setLeft(false);
//                    if (evt.getAxis().isAnalog()) {
//                        joystickEvent.setDown(val > 0);
//                        joystickEvent.setUp(val < 0);
//                    } else {
//                        joystickEvent.setDown(val < 0);
//                        joystickEvent.setUp(val > 0);
//                    }
//                    joystickEvent.setHorizontal(false);
//                    joystickEvent.setVertical(true);
//                    joystickEvent.setAxisDown(true);
//                    fireJoystickEvent(joystickEvent, 1);
//                    log("> Do vertical action on y");
//
//                }
//
//                return;
//
//            } else if (val == 0) {
//
//                //Now we check if a button was down and if it is now released
//                if (evt.getAxis().getLogicalId().endsWith("x")) {
//                    joystickEvent.setRight(true);
//                    joystickEvent.setLeft(true);
//                    joystickEvent.setDown(false);
//                    joystickEvent.setUp(false);
//                    joystickEvent.setHorizontal(true);
//                    joystickEvent.setVertical(false);
//                    log("> Do horizontal action on x");
//                    joystickEvent.setAxisDown(false);
//                    fireJoystickEvent(joystickEvent, 1);
//
//                } else if (evt.getAxis().getLogicalId().endsWith("y")) {
//                    joystickEvent.setRight(false);
//                    joystickEvent.setLeft(false);
//                    joystickEvent.setDown(true);
//                    joystickEvent.setUp(true);
//                    joystickEvent.setHorizontal(false);
//                    joystickEvent.setVertical(true);
//                    log("> Do vertical action on y");
//                    joystickEvent.setAxisDown(false);
//                    fireJoystickEvent(joystickEvent, 1);
//                }
//
//                return;
//            } else {
//                //Do nothing because it was already consumed
//                log("> Do nothing");
//                return;
//            }
        }

    }

    public void onJoyButtonEvent(JoyButtonEvent evt) {
        if (evt.getButton().getJoystick() != null && !evt.isConsumed()) {
//            log("\n======================= JOYSTICK BUTTON ======================");
//            System.out.println("> onJoyButtonEvent = " + evt.getButton().getButtonId() + ";   down = " + evt.isPressed());

            if (evt.getButton().getJoystick().getName() != null
                    && evt.getButton().getJoystick().getName().toLowerCase().contains("xbox")) {
                specialSetup = true;

            } else {
                specialSetup = false;
            }

            joystickEvent.setJoyButtonEvent(evt);
            joystickEvent.setJoystickIndex(evt.getJoyIndex());
            joystickEvent.setButtonDown(evt.isPressed());
            joystickEvent.setButton1(evt.getButton().getButtonId() == 0);
            joystickEvent.setButton2(evt.getButton().getButtonId() == 1);
            if (specialSetup) {
                joystickEvent.setButton3(evt.getButton().getButtonId() == 3);
                joystickEvent.setButton4(evt.getButton().getButtonId() == 2);                
                joystickEvent.setButton5(evt.getButton().getButtonId() == 5);
                joystickEvent.setButton6(evt.getButton().getButtonId() == 6);                

            } else {
                joystickEvent.setButton3(evt.getButton().getButtonId() == 2);
                joystickEvent.setButton4(evt.getButton().getButtonId() == 3);
                joystickEvent.setButton5(evt.getButton().getButtonId() == 4);
                joystickEvent.setButton6(evt.getButton().getButtonId() == 5);
                joystickEvent.setButton7(evt.getButton().getButtonId() == 6);
                joystickEvent.setButton8(evt.getButton().getButtonId() == 7);
                joystickEvent.setButton9(evt.getButton().getButtonId() == 8);
                joystickEvent.setButton10(evt.getButton().getButtonId() == 9);
            }

            fireJoystickEvent(joystickEvent, 1);

//            evt.setConsumed();
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
//            log("onTouchEvent Joystick: " + evt.getKeyCode());

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

    @Override
    public void onConnected(Joystick joystick) {
        log("Joystick connected: " + joystick.getName());

    }

    @Override
    public void onDisconnected(Joystick joystick) {
        log("Joystick disconnected: " + joystick.getName());

    }
}
