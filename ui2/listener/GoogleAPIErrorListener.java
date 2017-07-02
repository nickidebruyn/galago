/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.listener;

/**
 * Implements this interface when it is an android game and google play services is used.
 *
 * @author nicki de Bruyn
 */
public interface GoogleAPIErrorListener {
    
    public void onGoogleAPIError(String errorMessage);
    
    public void onGoogleAPIConnected(String message);
    
    public void onGoogleAPIDisconnected(String message);
    
}
