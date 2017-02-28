/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * This class will contain pick data when a user touches the screen and drag the screen.
 * 
 * @author nidebruyn
 */
public class PickEvent {
    
    private Geometry contactObject;
    private Vector3f contactPoint;
    private Vector2f cursorPosition;
    private boolean keyDown = false;
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private boolean zoomUp = false;
    private boolean zoomDown = false;
    private float analogValue;
    private boolean leftButton = false;
    private boolean rightButton = false;

    public Geometry getContactObject() {
        return contactObject;
    }

    public void setContactObject(Geometry contactObject) {
        this.contactObject = contactObject;
    }

    public Vector3f getContactPoint() {
        return contactPoint;
    }

    public float getAnalogValue() {
        return analogValue;
    }

    public boolean isLeftButton() {
        return leftButton;
    }

    public void setLeftButton(boolean leftButton) {
        this.leftButton = leftButton;
    }

    public boolean isRightButton() {
        return rightButton;
    }

    public void setRightButton(boolean rightButton) {
        this.rightButton = rightButton;
    }

    public void setAnalogValue(float analogValue) {
        this.analogValue = analogValue;
    }

    public void setContactPoint(Vector3f contactPoint) {
        this.contactPoint = contactPoint;
    }

    public Vector2f getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(Vector2f cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public boolean isKeyDown() {
        return keyDown;
    }

    public void setKeyDown(boolean keyDown) {
        this.keyDown = keyDown;
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

    public boolean isZoomUp() {
        return zoomUp;
    }

    public void setZoomUp(boolean zoomUp) {
        this.zoomUp = zoomUp;
    }

    public boolean isZoomDown() {
        return zoomDown;
    }

    public void setZoomDown(boolean zoomDown) {
        this.zoomDown = zoomDown;
    }

}
