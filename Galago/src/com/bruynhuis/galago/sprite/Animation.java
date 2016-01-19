/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite;

/**
 *
 * @author nidebruyn
 */
public class Animation {
    
    private String name;
    private int startIndex;
    private int endIndex;
    private float speed;

    public Animation(String name, int startIndex, int endIndex, float speed) {
        this.name = name;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Animation clone() {
        return new Animation(name, startIndex, endIndex, speed);
    }
}
