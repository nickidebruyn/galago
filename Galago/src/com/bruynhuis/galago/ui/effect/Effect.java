/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.effect;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.bruynhuis.galago.ui.Widget;

/**
 * An Abstract class for doing some nice effects on widgets.
 * Extends this when you wish to create your own effects such as slideInEffect
 * or shakeEffect, etc.
 *
 * @author nidebruyn
 */
public abstract class Effect extends AbstractControl {
        
    protected Widget widget;    
    
    /**
     * 
     * @param widget 
     */
    public Effect(Widget widget) {
        this.widget = widget;
    }
    
    /**
     * Start the show effect
     */
    public void fireShow() {
        doShow();
    }
    
    protected abstract void doShow();
    
    /**
     * Start the hide effect
     */
    public void fireHide() {
        doHide();
    }
    
    protected abstract void doHide();
    
    /**
     * Start the touch down effect
     */
    public void fireTouchDown() {
        doTouchDown();
    }
    
    protected abstract void doTouchDown();
    
    /**
     * do the touch up effect
     */
    public void fireTouchUp() {
        doTouchUp();
        
    }
    
    protected abstract void doTouchUp();
    
    /**
     * do the touch hover effect
     */
    public void fireHoverOver() {
        doHoverOver();
        
    }    
    
    protected abstract void doHoverOver();
    
    /**
     * do the hover effect
     */
    public void fireHoverOff() {
        doHoverOff();
        
    }    
    
    protected abstract void doHoverOff();    

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    public Control cloneForSpatial(Spatial spatial) {
        return this;
    }
    
    /**
     * Called when button is enabled or disabled
     */
    public void fireEnabled(boolean enabled) {
        doEnabled(enabled);
        
    }    
    
    protected abstract void doEnabled(boolean enabled);
    
    public void fireSelected() {
        doSelected();        
    }
    
    protected abstract void doSelected();
    
    
    public void fireUnselected() {
        doUnselected();        
    }
    
    protected abstract void doUnselected();
}
