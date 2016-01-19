/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchStickListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.FastMath;

/**
 * This is a Joystick type control.
 * It doesn't have any visual image but can listener for the different directions.
 *
 * @author nidebruyn
 */
public class TouchStick extends Panel {
    
    private TouchStickListener touchStickListener;
    private float touchDownX, touchDownY = 0;
    private float spacing = 10f;
    private ControlButton bottomControl;
    private Image topCircle;

    /**
     * 
     * @param panel
     * @param uid
     * @param width
     * @param height 
     */
    public TouchStick(Panel panel, String uid, float width, float height) {
        super(panel, null, width, height);
        
        bottomControl = new ControlButton(this, uid + "_buttom", "Resources/button_round.png", width, height);        
        bottomControl.setTransparency(0.1f);
        bottomControl.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (bottomControl.isEnabled() && isVisible()) {                    
                    touchDownX = touchX;
                    touchDownY = touchY;
//                    touchDownX = bottomControl.getPosition().x + bottomControl.getWidth() * 0.5f;
//                    touchDownY = bottomControl.getPosition().y + bottomControl.getHeight() * 0.5f;
                    fireTouchPress(touchX, touchY);
                    topCircle.center();
                    
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                touchDownX = 0;
                touchDownY = 0;
                fireTouchRelease(touchX, touchY);
                topCircle.center();
            }

            @Override
            public void doTouchMove(float touchX, float touchY, float tpf, String name) {
                double dx = touchDownX - touchX;
                double dy = touchDownY - touchY;
                float distance = FastMath.sqrt((float) (dx * dx + dy * dy));
                
                if (distance < getWidth()*0.5f) {
                    topCircle.centerAt(-(float)dx, -(float)dy);
                }                
                
//                System.out.println("posdown = " + touchDownX +", " + touchDownY);
//                System.out.println("pos = " + touchX +", " + touchY);
                
                //Check if the stick is moving left
                if (touchX < touchDownX) {
                    fireTouchMoveLeft(touchX, touchY, FastMath.sqrt((float) (dx * dx + 0)));
                } else               
                //Check if the stick is moving right
                if (touchX > touchDownX) {
                    fireTouchMoveRight(touchX, touchY, FastMath.sqrt((float) (dx * dx + 0)));
                }
                
                //Check if the stick is moving up
                if (touchY > touchDownY) {
                    fireTouchMoveUp(touchX, touchY, FastMath.sqrt((float) (0 + dy * dy)));
                    
                } else                
                //Check if the stick is moving down
                if (touchY < touchDownY) {
                    fireTouchMoveDown(touchX, touchY, FastMath.sqrt((float) (0 + dy * dy)));
                }
                
            }            

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                touchDownX = 0;
                touchDownY = 0;
                fireTouchRelease(touchX, touchY);
                topCircle.center();
            }
            
        });

        topCircle = new Image(this, "Resources/button_circle.png", width*0.25f, height*0.25f, true);
        topCircle.center();
        
        panel.add(this);
    }    

    public float getSpacing() {
        return spacing;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
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
