/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.path;

/**
 *
 * @author nidebruyn
 */
public interface PathListener {
    
    public void moveStarted();
    public void moveBusy();
    public void moveDone();
    
}
