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
 * This is a SwipeStick type control. It doesn't have any visual image but can
 * listener for the different directions when the player swipes the screen
 *
 * @author nidebruyn
 */
public class SwipeStick extends Panel {

    private TouchStickListener touchStickListener;
    private float touchDownX, touchDownY = 0;
    private ControlButton bottomControl;

    /**
     *
     * @param panel
     * @param uid
     * @param width
     * @param height
     */
    public SwipeStick(Panel panel, String uid, float width, float height) {
        super(panel, null, width, height, true);

        bottomControl = new ControlButton(this, uid + "_buttom", width, height, false);
        bottomControl.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (bottomControl.isEnabled() && isVisible()) {
                    touchDownX = touchX;
                    touchDownY = touchY;
                    fireTouchPress(touchX, touchY);


                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                fireTouchRelease(touchX, touchY);
                
                touchDownX = 0;
                touchDownY = 0;
            }

            @Override
            public void doTouchMove(float touchX, float touchY, float tpf, String name) {
                double dx = (touchDownX - touchX) / window.getScaleFactorWidth();
                double dy = (touchDownY - touchY) / window.getScaleFactorHeight();
                float distance = FastMath.sqrt((float) (dx * dx + dy * dy));
                fireTouchMove(touchX, touchY, distance);

                //Check if the stick is moving left
                if ((touchX < touchDownX)) {
                    fireTouchMoveLeft(touchX, touchY, FastMath.sqrt((float) (dx * dx + 0)));

                } else //Check if the stick is moving right
                if ((touchX > touchDownX)) {
                    fireTouchMoveRight(touchX, touchY, FastMath.sqrt((float) (dx * dx + 0)));

                } 
                //Check if the stick is moving up
                if ((touchY > touchDownY)) {
                    fireTouchMoveUp(touchX, touchY, FastMath.sqrt((float) (0 + dy * dy)));

                } else //Check if the stick is moving down
                if ((touchY < touchDownY)) {
                    fireTouchMoveDown(touchX, touchY, FastMath.sqrt((float) (0 + dy * dy)));

                }

            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                touchDownX = 0;
                touchDownY = 0;
                fireTouchRelease(touchX, touchY);

            }
        });

        panel.add(this);
    }

    public void addTouchStickListener(TouchStickListener touchStickListener1) {
        this.touchStickListener = touchStickListener1;
    }

    public void removeTouchStickListener(TouchStickListener touchStickListener1) {
        this.touchStickListener = null;
    }

    protected void fireTouchMoveLeft(float x, float y, float distance) {
        if (touchStickListener != null) {
            touchStickListener.doLeft(x, y, distance);
        }
    }

    protected void fireTouchMove(float x, float y, float distance) {
        if (touchStickListener != null) {
            touchStickListener.doMove(x, y, distance);
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
