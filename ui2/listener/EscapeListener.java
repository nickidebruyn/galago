/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.listener;

/**
 * Listens for escape or back actions.
 * Internal use only.
 * 
 * @author NideBruyn
 */
public interface EscapeListener {
    
    /**
     * This method will be called on every view state that implements this method.
     */
    public void doEscape(boolean touchEvent);
    
}
