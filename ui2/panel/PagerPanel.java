/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.util.Debug;
import com.jme3.math.FastMath;
import java.util.ArrayList;

/**
 * The PagerPanel is similar to the Horizontal Panel except it has some nice scrolling depth effects.
 * This is almost the same as a cateye/carousel widget.
 * 
 * @author nidebruyn
 */
public class PagerPanel extends Panel implements TouchButtonListener {
    
    private ControlButton controlButton;
    private ArrayList<Widget> pages = new ArrayList<Widget>();
    private float touchDown;
    private float centerOffset = 1f;
    private float tempOffset = 0;
    private float maxCenterOffset = 0;
    private float centerOffsetPercentage = 0;
    private float scaleFactor = 0;
    private float depthFactor = 0;
    private float touchedDownX = 0;
    private float touchedUpX = 0;
    
    /**
     * 
     * @param parent
     * @param width
     * @param height 
     */
    public PagerPanel(Widget parent, float width, float height) {
        super(parent.getWindow(), parent, null, width, height);
        
        controlButton = new ControlButton((Panel)parent, "pager-panel-controlbutton", width, height);
        controlButton.addTouchButtonListener(this);
        
        maxCenterOffset = this.getWidth()*0.5f;

    }

    @Override
    public void add(Widget widget) {
        if (!widget.equals(controlButton)) {
            pages.add(widget);
        } else {
//            window.log("Control found");
        }
        
        super.add(widget); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This method will handle the way we layout the 
     */
    public void layout() {
        float sectionSize = getWidth()/widgets.size();
        float position = -getWidth()*0.5f + sectionSize*0.5f;
        float distanceFromCenter = 0;
        
//        window.log("************ Layouts **************");
        
        for (int i = 0; i < pages.size(); i++) {            
            Widget widget = pages.get(i);
            widget.setPosition(position + centerOffset, 0);
            position += sectionSize;
            
            distanceFromCenter = FastMath.abs(-this.getPosition().x + widget.getWidgetNode().getWorldTranslation().x - (getWidth()*0.5f));
            centerOffsetPercentage = distanceFromCenter/maxCenterOffset;
            if (centerOffsetPercentage > 1f) centerOffsetPercentage = 1f;
            scaleFactor = 1f-centerOffsetPercentage;
            depthFactor = -1f + scaleFactor;
            if (depthFactor > -0.05f) {
                depthFactor = 0f;
            }
//            window.log(i + " depthFactor=" + depthFactor);
            widget.setScale(scaleFactor);
            widget.setDepthPosition(depthFactor);
//            widget.setTransparency(scaleFactor);
//            window.log("dis = " + distanceFromCenter);
//            if (distanceFromCenter > this.getWidth()*0.35f) {
//                widget.setVisible(false);
//            } else {
//                widget.setVisible(true);
//            }
            
        }
//        window.log("*********** Layouts End ************");
//        optimize();
    }

    @Override
    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
        if (this.isVisible()) {
            touchedDownX = touchX;
            touchedUpX = touchX;
            
            tempOffset = centerOffset;
            touchDown = -(window.getWidth() * 0.5f) + touchX;
        }
        
    }

    @Override
    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        if (this.isVisible()) {
            touchDown = 0;

            touchedUpX = touchX;
        }
        
    }

    @Override
    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
        if (this.isVisible()) {
            
            touchedUpX = touchX;
            
            float diff = (-(window.getWidth() * 0.5f) + touchX) - touchDown;
            centerOffset = tempOffset + diff;
            
            //Fix that the position doesn't go higher than the min and max
            if (centerOffset < this.getWidth()*-0.5f) {
                centerOffset = this.getWidth()*-0.5f;
                
            } else if (centerOffset > this.getWidth()*0.5f) {
                centerOffset = this.getWidth()*0.5f;
                
            }

            layout();
            
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
    
    public boolean isPageMoved() {
//        Debug.log("Moved distance of page: " + FastMath.abs(touchedUpX-touchedDownX));
        return FastMath.abs(touchedUpX-touchedDownX) > 10f;
    }

}
