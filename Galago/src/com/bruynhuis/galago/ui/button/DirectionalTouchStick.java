/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.listener.TouchStickListener;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.effect.TouchEffect;

/**
 * A DirectionalTouchStick is a screen control widget this is has 9 directional buttons.
 *
 * @author nidebruyn
 */
public class DirectionalTouchStick extends GridPanel {

    private TouchStickListener touchStickListener;

    /*
     *  123
     *  456
     *  789
     * 
     * 
     */
    

    /**
     * 
     * @param parent
     * @param width
     * @param height 
     */
    public DirectionalTouchStick(Widget parent, float width, float height) {
        super(parent, "Resources/stick.png", width, height);
        
        ((Panel)parent).add(this);
        
        for (int i = 0; i < 9; i++) {
            final TouchButton touchButton = new TouchButton(this, "button" + (i+1), "Resources/button.png", width/3f, height/3f);
            this.add(touchButton);
            touchButton.addEffect(new TouchEffect(touchButton));
            touchButton.addTouchButtonListener(new TouchButtonListener() {

                @Override
                public void doTouchDown(float touchX, float touchY, float tpf, String uid) {

                    fireTouchPress(touchX, touchY);
                    
                    if (uid.endsWith("2")) {
                        fireTouchMoveUp(touchX, touchY, 0);
                        
                    } else if (uid.endsWith("8")) {
                        fireTouchMoveDown(touchX, touchY, 0);
                        
                    } else if (uid.endsWith("4")) {
                        fireTouchMoveLeft(touchX, touchY, 0);
                        
                    } else if (uid.endsWith("6")) {
                        fireTouchMoveRight(touchX, touchY, 0);
                        
                    }

                }

                @Override
                public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                    fireTouchRelease(touchX, touchY);
                    

                }

                @Override
                public void doTouchMove(float touchX, float touchY, float tpf, String name) {

                }

                @Override
                public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                    
                }
                
                
            });
        }
        
        layout(3, 3);
                
    }

    public void addTouchStickListener(TouchStickListener touchStickListener1) {
        this.touchStickListener = touchStickListener1;
    }

    protected void fireTouchMoveLeft(float x, float y, float distance) {
        if (touchStickListener != null) {
            touchStickListener.doLeft(x, y, distance);
        }
    }

    protected void fireTouchMoveRight(float x, float y, float distance) {
        if (touchStickListener != null) {
            touchStickListener.doRight(x, y, distance);
        }
    }

    protected void fireTouchMoveUp(float x, float y, float distance) {
        if (touchStickListener != null) {
            touchStickListener.doUp(x, y, distance);
        }
    }

    protected void fireTouchMoveDown(float x, float y, float distance) {
        if (touchStickListener != null) {
            touchStickListener.doDown(x, y, distance);
        }
    }

    protected void fireTouchRelease(float x, float y) {
        if (touchStickListener != null) {
            touchStickListener.doRelease(x, y);
        }
    }

    protected void fireTouchPress(float x, float y) {
        if (touchStickListener != null) {
            touchStickListener.doPress(x, y);
        }
    }
}
