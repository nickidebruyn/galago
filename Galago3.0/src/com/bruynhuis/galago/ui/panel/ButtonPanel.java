/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.util.Debug;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * A button panel will group TouchButtons together and allow the user to rotate
 * over all the buttons highlighting one at a time.
 *
 * @author Nidebruyn
 */
public class ButtonPanel extends Panel implements ActionListener, JoystickListener {

    private TouchButton selectedButton;

    public ButtonPanel(Widget parent) {
        super(parent);
    }

    public ButtonPanel(Widget parent, float width, float height) {
        super(parent, null, width, height);
    }

    public ButtonPanel(Widget parent, String pictureFile, float width, float height) {
        super(parent, pictureFile, width, height);
    }

    public ButtonPanel(Widget parent, String pictureFile, float width, float height, boolean lockScale) {
        super(parent, pictureFile, width, height, lockScale);
    }

    private void registerInput() {
        window.getInputManager().addMapping("keyboard_up", new KeyTrigger(KeyInput.KEY_UP));
        window.getInputManager().addMapping("keyboard_down", new KeyTrigger(KeyInput.KEY_DOWN));
        window.getInputManager().addMapping("keyboard_enter_pressed", new KeyTrigger(KeyInput.KEY_RETURN));

        window.getInputManager().addMapping("keyboard_left", new KeyTrigger(KeyInput.KEY_LEFT));
        window.getInputManager().addMapping("keyboard_right", new KeyTrigger(KeyInput.KEY_RIGHT));
        window.getInputManager().addMapping("keyboard_space_pressed", new KeyTrigger(KeyInput.KEY_SPACE));

        window.getInputManager().addListener(this, "keyboard_up", "keyboard_down", "keyboard_enter_pressed", "keyboard_left", "keyboard_right", "keyboard_space_pressed");

        window.getApplication().getJoystickInputListener().addJoystickListener(this);
    }

    private void unregisterInput() {
        window.getInputManager().deleteMapping("keyboard_up");
        window.getInputManager().deleteMapping("keyboard_down");
        window.getInputManager().deleteMapping("keyboard_enter_pressed");
        window.getInputManager().deleteMapping("keyboard_left");
        window.getInputManager().deleteMapping("keyboard_right");
        window.getInputManager().deleteMapping("keyboard_space_pressed");

        window.getInputManager().removeListener(this);
        window.getApplication().getJoystickInputListener().removeJoystickListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
//        if (isPressed) {
//            Debug.log("Key: " + name);
//        }

        if (isPressed && (name.equals("keyboard_up") || name.equals("keyboard_left"))) {
            swapDown(tpf);

        } else if (isPressed && (name.equals("keyboard_down") || name.equals("keyboard_right"))) {
            swapUp(tpf);

        } else if (name.equals("keyboard_enter_pressed") || name.equals("keyboard_space_pressed")) {

            if (selectedButton != null) {
                if (isPressed) {
                    selectedButton.fireTouchDown(0, 0, tpf);
                } else {
                    selectedButton.fireTouchUp(0, 0, tpf);
                }
            }
        }

    }

    private void swapUp(float tpf) {
//        if (selectedButton != null) {
        int index = widgets.indexOf(selectedButton);
//            Debug.log("index: " + index + "; size: " + widgets.size());
        index++;
        if (index > widgets.size() - 1) {
            index = 0;
        }
        selectedButton = (TouchButton) widgets.get(index);
        updateSelection(tpf, selectedButton);
//        }
    }

    private void swapDown(float tpf) {
//        if (selectedButton != null) {
        int index = widgets.indexOf(selectedButton);
        Debug.log("index: " + index + "; size: " + widgets.size());
        index--;

        if (index < 0) {
            index = widgets.size() - 1;
        }
        selectedButton = (TouchButton) widgets.get(index);
        updateSelection(tpf, selectedButton);
//        }
    }

    private void updateSelection(float tpf, TouchButton touchButton) {
        if (this.getWidgets() != null) {
            for (int i = 0; i < this.getWidgets().size(); i++) {
                Widget widget = this.getWidgets().get(i);
                if (widget instanceof TouchButton) {
                    ((TouchButton) widget).unselect(tpf);
                }
            }
        }

        if (touchButton != null) {
            touchButton.select(tpf);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible); //To change body of generated methods, choose Tools | Templates.

        if (visible) {
            if (!window.getInputManager().hasMapping("keyboard_up")) {
                registerInput();
            }


        } else {

            if (window.getInputManager().hasMapping("keyboard_up")) {
                unregisterInput();
            }

            selectedButton = null;
        }

    }

    @Override
    public void stick(JoystickEvent joystickEvent, float fps) {
//        Debug.log("down: " + joystickEvent.isKeyDown());

        if (joystickEvent.isKeyDown()) {
            if (joystickEvent.isUp() || joystickEvent.isLeft()) {
                swapDown(fps);
            }
            if (joystickEvent.isDown() || joystickEvent.isRight()) {
                swapUp(fps);
            }
        }


        if ((joystickEvent.isButton1() || joystickEvent.isButton3()) && selectedButton != null) {
            if (joystickEvent.isKeyDown()) {
                selectedButton.fireTouchDown(0, 0, 1f);
            } else {
                selectedButton.fireTouchUp(0, 0, 1f);
            }
        }

    }
}
