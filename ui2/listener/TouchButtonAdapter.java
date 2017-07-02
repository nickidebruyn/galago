/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.listener;

/**
 * An adapter class for the TouchButtonListener interface.
 * 
 * @author nidebruyn
 */
public abstract class TouchButtonAdapter implements TouchButtonListener {

    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
        
    }

    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        
    }

    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
        
    }

    @Override
    public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
        
    }
    
}
