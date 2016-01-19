/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

/**
 * The sensor listener is there for listening to android device sensor actions, for example (tilt, orientation)
 * For you to listen in code to the android sensor set the (useSensor = true;) in the MainActivity class
 * 
 * and 
 * 
 * You have to implement the SensorListener in one of your classes and add it to the BaseApplication
 * 
 * @author nidebruyn
 */
public interface SensorListener {
    
    /**
     * 
     * @param fisting (is for fisting)
     * @param tilting (is for tilting up and down)
     * @param twisting (is for twisting (holding controller))
     */
    public void doSensorAction(float fisting, float tilting, float twisting);
    
}
