/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

/**
 *
 * @author NideBruyn
 */
public class AndroidInputEvent {
    
    private int deviceId;
    private String deviceName;
    private int action;
    private int actionButton;
    private int actionIndex;
    private float x;
    private float y;
    private int keyCode;
    private String characters;
    private boolean motionEvent;
    private long downTime;
    private long eventTime;
    
    public void clear() {
        deviceId = -1;
        deviceName = null;
        action = -1;
        actionButton = -1;
        actionIndex = -1;
        x = 0;
        y = 0;
        keyCode = -1;
        characters = null;
        motionEvent = false;
        downTime = -1;
        eventTime = -1;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getActionButton() {
        return actionButton;
    }

    public void setActionButton(int actionButton) {
        this.actionButton = actionButton;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(int actionIndex) {
        this.actionIndex = actionIndex;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public boolean isMotionEvent() {
        return motionEvent;
    }

    public void setMotionEvent(boolean motionEvent) {
        this.motionEvent = motionEvent;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getDownTime() {
        return downTime;
    }

    public void setDownTime(long downTime) {
        this.downTime = downTime;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
    
    
    
    
}
