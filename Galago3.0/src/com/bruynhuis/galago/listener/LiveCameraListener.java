/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

import com.jme3.texture.Texture;

/**
 *
 * Use this listener if you want to capture the image coming from the devices camera.
 * 
 * @author Nidebruyn
 */
public interface LiveCameraListener {
    
    /**
     * This method will be called infanately by the android system if active
     * @param image 
     */
    public void setTexture(Texture texture);
    
}
