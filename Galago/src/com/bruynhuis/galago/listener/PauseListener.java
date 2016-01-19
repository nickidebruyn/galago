/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

/**
 *
 * @author nidebruyn
 */
public interface PauseListener {
    
    /**
     * This is called when the pause listener is fired from android.
     * @param properties
     * @return 
     */
    public void doPause(boolean pause);
    
}
