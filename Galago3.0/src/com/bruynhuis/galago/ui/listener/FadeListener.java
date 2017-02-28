/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.listener;

/**
 * For internal use only.
 * 
 * @author NideBruyn
 */
public interface FadeListener {
    
    /**
     * It will be true if it was a fade out else it was a fade in.
     * @param fadeOut 
     */
    public void fadeDone(boolean fadeOut);
    
}
