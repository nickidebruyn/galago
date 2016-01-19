/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.listener;

/**
 * Implements this interface when it is an android game and google play saved game services is used.
 *
 * @author nicki de Bruyn
 */
public interface SavedGameListener {
    
    public void onSavedGameError(String errorMessage);
    
    public void onSavedGameOpened(String name, String data);
    
    public void onSavedGameSaved();
    
}
