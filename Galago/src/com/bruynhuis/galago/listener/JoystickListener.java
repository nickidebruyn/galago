/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

/**
 *
 * @author nidebruyn
 */
public interface JoystickListener {
    
    public void stick(JoystickEvent joystickEvent, float fps);
    
}
