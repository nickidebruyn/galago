/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.effect;

import com.bruynhuis.galago.ui.Widget;

/**
 * The wobble effect will make the widget scale up and down infanitely.
 * 
 * @author nidebruyn
 */
public class WobbleEffect extends Effect {
    
    private float maxScale = 1.1f;
    private float speed = 1f;
    private boolean up = true;

    public WobbleEffect(Widget widget, float maxScale, float speed) {
        super(widget);
        this.maxScale = maxScale;
        this.speed = speed;
    }

    @Override
    protected void doShow() {
        
    }

    @Override
    protected void doHide() {
        
    }

    @Override
    protected void doTouchDown() {
        
    }

    @Override
    protected void doTouchUp() {
        
    }

    @Override
    protected void doEnabled(boolean enabled) {
        
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        if (widget != null && widget.isVisible()) {
            
            if (up) {
                widget.scale(tpf*speed);
                if (widget.getScale() >= maxScale) {
                    up = false;
                    widget.setScale(maxScale);
                }
            } else {
                widget.scale(-tpf*speed);
                if (widget.getScale() <= 1) {
                    up = true;
                    widget.setScale(1);
                }
            }
            
        }
        
    }

    @Override
    protected void doSelected() {
    }

    @Override
    protected void doUnselected() {
    }
    
}
