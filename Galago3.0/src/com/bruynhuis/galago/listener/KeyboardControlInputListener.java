/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.listener;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The KeyboardControlInputListener can be used by a user to detect basic
 * keyboard events actions was performed.
 *
 * @author NideBruyn
 */
public class KeyboardControlInputListener implements ActionListener {

    private static final String KEYBOARD_ENTER = "KEY-ENTER";
    private static final String KEYBOARD_SPACE = "KEY-SPACE";
    private static final String KEYBOARD_LEFT = "KEY-LEFT";
    private static final String KEYBOARD_RIGHT = "KEY-RIGHT";
    private static final String KEYBOARD_UP = "KEY-UP";
    private static final String KEYBOARD_DOWN = "KEY-DOWN";
    private static final String KEYBOARD_LEFT_CTRL = "KEYBOARD_LEFT_CTRL";
    private static final String KEYBOARD_RIGHT_CTRL = "KEYBOARD_RIGHT_CTRL";
    private static final String KEYBOARD_TAB = "KEYBOARD_TAB";
    private static final String KEYBOARD_DELETE = "KEYBOARD_DELETE";

    private ArrayList<KeyboardControlListener> keyboardControlListeners = new ArrayList<KeyboardControlListener>();
    private InputManager inputManager;
    private boolean enabled = true;
    private KeyboardControlEvent keyboardControlEvent;

    public KeyboardControlInputListener() {
        keyboardControlEvent = new KeyboardControlEvent();

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        if (!inputManager.hasMapping(KEYBOARD_ENTER)) {

            inputManager.addMapping(KEYBOARD_ENTER, new KeyTrigger(KeyInput.KEY_RETURN));
            inputManager.addMapping(KEYBOARD_SPACE, new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping(KEYBOARD_LEFT, new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(KEYBOARD_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping(KEYBOARD_UP, new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping(KEYBOARD_DOWN, new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping(KEYBOARD_LEFT_CTRL, new KeyTrigger(KeyInput.KEY_LCONTROL));
            inputManager.addMapping(KEYBOARD_RIGHT_CTRL, new KeyTrigger(KeyInput.KEY_RCONTROL));
            inputManager.addMapping(KEYBOARD_TAB, new KeyTrigger(KeyInput.KEY_TAB));
            inputManager.addMapping(KEYBOARD_DELETE, new KeyTrigger(KeyInput.KEY_DELETE));

            inputManager.addListener(this, new String[]{KEYBOARD_ENTER, KEYBOARD_SPACE,
                KEYBOARD_LEFT, KEYBOARD_RIGHT, KEYBOARD_UP, KEYBOARD_DOWN,
                KEYBOARD_LEFT_CTRL, KEYBOARD_RIGHT_CTRL, KEYBOARD_TAB, KEYBOARD_DELETE});
        }
    }

    public void unregisterInput() {

        if (inputManager == null) {
            return;
        }

        inputManager.deleteMapping(KEYBOARD_ENTER);
        inputManager.deleteMapping(KEYBOARD_SPACE);
        inputManager.deleteMapping(KEYBOARD_LEFT);
        inputManager.deleteMapping(KEYBOARD_RIGHT);
        inputManager.deleteMapping(KEYBOARD_UP);
        inputManager.deleteMapping(KEYBOARD_DOWN);
        inputManager.deleteMapping(KEYBOARD_LEFT_CTRL);
        inputManager.deleteMapping(KEYBOARD_RIGHT_CTRL);
        inputManager.deleteMapping(KEYBOARD_TAB);
        inputManager.deleteMapping(KEYBOARD_DELETE);

        inputManager.removeListener(this);

    }

    /**
     * Log some text to the console
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }

    public void addKeyboardControlListener(KeyboardControlListener keyboardControlListener) {
        this.keyboardControlListeners.add(keyboardControlListener);
    }

    public void removeKeyboardControlListener(KeyboardControlListener keyboardControlListener) {
        this.keyboardControlListeners.remove(keyboardControlListener);
    }

    private void fireKeyboardControlEvent(KeyboardControlEvent event, float tpf) {
        if (keyboardControlListeners != null) {
            for (Iterator<KeyboardControlListener> it = keyboardControlListeners.iterator(); it.hasNext();) {
                KeyboardControlListener keyboardControlListener = it.next();
                if (keyboardControlListener != null) {
                    keyboardControlListener.onKey(event, tpf);
                }

            }

        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name != null) {
            
            keyboardControlEvent.setKeyDown(isPressed);
            keyboardControlEvent.setUp(KEYBOARD_UP.equals(name));
            keyboardControlEvent.setDown(KEYBOARD_DOWN.equals(name));
            keyboardControlEvent.setLeft(KEYBOARD_LEFT.equals(name));
            keyboardControlEvent.setRight(KEYBOARD_RIGHT.equals(name));
            keyboardControlEvent.setButton1(KEYBOARD_ENTER.equals(name));
            keyboardControlEvent.setButton2(KEYBOARD_SPACE.equals(name));
            keyboardControlEvent.setButton3(KEYBOARD_TAB.equals(name));
            keyboardControlEvent.setButton4(KEYBOARD_LEFT_CTRL.equals(name));
            keyboardControlEvent.setButton5(KEYBOARD_RIGHT_CTRL.equals(name));
            keyboardControlEvent.setDelete(KEYBOARD_DELETE.equals(name));

            fireKeyboardControlEvent(keyboardControlEvent, tpf);
        }
    }
}
