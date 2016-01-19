/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

/**
 * A timer class is a very powerful class and it will be used very often.
 * It helps you with creating times in the game.
 * For example, if you need some spatial to jump or rotate every 200 miliseconds.
 *
 * @author nidebruyn
 */
public class Timer {
    
    private float maxTime = 10f;
    private float counter = 0;
    private boolean paused = true;
    
    /**
     * 
     * @param maxTime time in milli seconds
     */
    public Timer(float maxTime) {
        this.maxTime = maxTime;
        counter = 0;        
    }
    
    /**
     * Start the timer.
     */
    public void start() {
        counter = 0;
        paused = false;
    }
    
    /**
     * Pause the timer
     * @param pause 
     */
    public void pause(boolean pause) {
        paused = pause;
        
    }
    
    /**
     * Restart the timer.
     */
    public void reset() {
        paused = false;
        counter = 0;
    }
    
    /**
     * Stop the timer
     */
    public void stop() {
        paused = true;
        counter = 0;
    }
    
    /**
     * This method must be called every game loop. In a control or in the update method.
     * @param tpf 
     */
    public void update(float tpf) {
        
        if (!paused) {
            if (counter < maxTime) {
                counter += (tpf*100f);
            }
        }
        
    }
    
    /**
     * Check if the timer if finished.
     * @return 
     */
    public boolean finished() {
        if (paused) {
            return false;
        }
        return counter >= maxTime;
    }
    
    /**
     * Force the timer to finish.
     */
    public void forceFinished() {
        counter = maxTime;
    }
    
    public boolean isPaused() {
        return paused;
    }

    /**
     * 
     * @param maxTime 
     */
    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
        reset();
    }
    
    public float getTimeLeft() {
        return (maxTime - counter);
    }

    /**
     * 
     * @param counter 
     */
    public void setCounterTo(float counter) {
        this.counter = counter;
    }    

    public float getCounter() {
        return counter;
    }

    public float getMaxTime() {
        return maxTime;
    }
    
}
