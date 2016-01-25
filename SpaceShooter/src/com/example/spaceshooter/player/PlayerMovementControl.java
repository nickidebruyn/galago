/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.player;

import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.example.spaceshooter.MainApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class PlayerMovementControl extends AbstractControl implements ActionListener, JoystickListener {

    private MainApplication mainApplication;
    private float verticalSpeed = 8f;
    private float horizontalSpeed = 14f;
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;

    public PlayerMovementControl(MainApplication mainApplication1) {
        this.mainApplication = mainApplication1;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (up && spatial.getControl(RigidBodyControl.class).getPhysicLocation().y < mainApplication.getLevelHeight()) {
            spatial.getControl(RigidBodyControl.class).move(0, verticalSpeed * tpf);
        }

        if (down && spatial.getControl(RigidBodyControl.class).getPhysicLocation().y > -mainApplication.getLevelHeight()) {
            spatial.getControl(RigidBodyControl.class).move(0, -verticalSpeed * tpf);
        }

        if (left && spatial.getControl(RigidBodyControl.class).getPhysicLocation().x > -mainApplication.getLevelWidth()) {
            spatial.getControl(RigidBodyControl.class).move(-horizontalSpeed * tpf, 0);
        }

        if (right && spatial.getControl(RigidBodyControl.class).getPhysicLocation().x < mainApplication.getLevelWidth()) {
            spatial.getControl(RigidBodyControl.class).move(horizontalSpeed * tpf, 0);
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("moveup")) {
            up = isPressed;
        }

        if (name.equals("movedown")) {
            down = isPressed;
        }

        if (name.equals("moveleft")) {
            left = isPressed;
        }

        if (name.equals("moveright")) {
            right = isPressed;
        }
    }

    public void stick(JoystickEvent joystickEvent, float fps) {        
        up = joystickEvent.isUp();
        down = joystickEvent.isDown();
        left = joystickEvent.isLeft();
        right = joystickEvent.isRight();
        
    }
}
