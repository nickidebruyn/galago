/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.Widget;

/**
 * The PagerPanel is similar to the Horizontal Panel except it has some nice scrolling depth effects.
 * This is almost the same as a cateye/carousel widget.
 * 
 * @author nidebruyn
 */
public class VScrollPanel extends VPanel implements TouchButtonListener {
    
    private ControlButton controlButton;
    private float touchedDownY = 0;
    private float touchedUpY = 0;
    private float touchDown;
    private float centerOffset = 1f;
    private float tempOffset = 0;
    private float maxCenterOffset = 0;
    private float centerOffsetPercentage = 0;
    
    /**
     * 
     * @param parent
     * @param width
     * @param height 
     */
    public VScrollPanel(Widget parent, float width, float height) {
        super(parent.getWindow(), parent, null, width, height);
        
        controlButton = new ControlButton((Panel)parent, "pager-panel-controlbutton", width, height);
        controlButton.addTouchButtonListener(this);
        
        maxCenterOffset = this.getWidth()*0.5f;

    }

    @Override
    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
        if (this.isVisible()) {
            touchedDownY = touchY;
            touchedUpY = touchY;

            tempOffset = centerOffset;
            touchDown = -(window.getWidth() * 0.5f) + touchY;
        }
        
    }

    @Override
    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        if (this.isVisible()) {
            touchDown = 0;

            touchedUpY = touchY;
        }
        
    }

    @Override
    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
        if (this.isVisible()) {
            touchedUpY = touchY;
            
            float diff = (-(window.getHeight()* 0.5f) + touchY) - touchDown;
            centerOffset = tempOffset + diff;
            
            //Fix that the position doesn't go higher than the min and max
            if (centerOffset < this.getHeight()*-0.5f) {
                centerOffset = this.getHeight()*-0.5f;
                
            } else if (centerOffset > this.getHeight()*0.5f) {
                centerOffset = this.getHeight()*0.5f;
                
            }

            
        }        
    }
    
    @Override
    protected boolean isBatched() {
        return true;
    }

    @Override
    public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
        touchDown = 0;

    }
    

}
