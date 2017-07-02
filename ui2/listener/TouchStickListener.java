/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.listener;

/**
 * The touchStick listener interface.
 * @author nidebruyn
 */
public interface TouchStickListener {
    
    public void doMove(float x, float y, float distance);
    
    public void doLeft(float x, float y, float distance);
    
    public void doRight(float x, float y, float distance);
    
    public void doUp(float x, float y, float distance);
    
    public void doDown(float x, float y, float distance);
    
    public void doRelease(float x, float y);
    
    public void doPress(float x, float y);
    
}
