/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.messages;

/**
 *
 * @author Nidebruyn
 */
public interface MessageListener {
    
    /**
     * This interface can be implemented and added to the messageManager which will fire an event.
     * 
     * @param message
     * @param object 
     */
    public void messageReceived(String message, Object object);

    
}
