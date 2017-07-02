/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchStickListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.FastMath;

/**
 * This is a Joystick type control.
 * It requires an image and only handles left and right control actions.
 *
 * @author nidebruyn
 */
public class HorizontalTouchStick extends ControlButton {
    
    private TouchStickListener touchStickListener;
    private float centerX, centerY = 0;

    /**
     * 
     * @param panel
     * @param uid
     * @param width
     * @param height 
     */
    public HorizontalTouchStick(Panel panel, String uid, float scale) {
        super(panel, uid, "Resources/horizontal_arrows.png", 360f*scale, 120*scale);
    
        initListener();
    }    

    public HorizontalTouchStick(Panel panel, String uid, String image, float width, float height) {
        super(panel, uid, image, width, height);
        
        initListener();
    }
        
    private void initListener() {
        addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isEnabled() && isVisible()) {
                    centerX = getScreenPosition().x;
                    centerY = getScreenPosition().y;
                    fireTouchPress(touchX, touchY);
                    
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                System.out.println("Up");
                centerX = 0;
                centerY = 0;
                fireTouchRelease(touchX, touchY);
            }

            @Override
            public void doTouchMove(float touchX, float touchY, float tpf, String name) {
                double dx = centerX - touchX;
                double dy = centerY - touchY;
                float distanceSquared = (float) (dx * dx + dy * dy);                
                float distance = FastMath.sqrt(distanceSquared);
                
                //Check if the stick is moving left
                if (touchX < centerX) {
                    fireTouchMoveLeft(touchX, touchY, distance);
                }
                
                //Check if the stick is moving right
                if (touchX > centerX) {
                    fireTouchMoveRight(touchX, touchY, distance);
                }
                
            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                centerX = 0;
                centerY = 0;
                fireTouchRelease(touchX, touchY);
            }
            
            
        });

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
