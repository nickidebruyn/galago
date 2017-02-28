/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchStickListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.math.FastMath;

/**
 * A DirectionalTouchStick is a screen control widget this is has 9 directional
 * buttons.
 *
 * @author nidebruyn
 */
public class DirectionalTouchStick extends Panel {

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
    public DirectionalTouchStick(Panel panel, String uid, float width, float height) {
        super(panel, null, width, height, true);

        bottomControl = new ControlButton(this, uid + "_buttom", "Resources/stick.png", width, height, true);
        bottomControl.setTransparency(0.5f);
        bottomControl.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (bottomControl.isEnabled() && isVisible()) {
                    
                    touchDownX = getWidth()/2f;
                    touchDownY = getHeight()/2f;

                    fireTouchPress(touchX, touchY);

                    double dx = (touchDownX - touchX) / window.getScaleFactorWidth();
                    double dy = (touchDownY - touchY) / window.getScaleFactorHeight();

                    float distance = FastMath.sqrt((float) (dx * dx + dy * dy));
                    float maxDistance = getWidth() * 0.5f;
                    float minDistance = getWidth() * 0.2f;

                    if (distance < maxDistance) {
                        topCircle.centerAt(-(float) dx, -(float) dy);
                    }

//                System.out.println("posdown = " + touchDownX +", " + touchDownY);
                System.out.println("pos = " + touchX +", " + touchY);

                    //Check if the stick is moving left
                    if (touchX < touchDownX && (touchDownX-touchX) > minDistance) {
                        fireTouchMoveLeft(touchX, touchY, FastMath.sqrt((float) (dx * dx + 0)));
                        
                    } else //Check if the stick is moving right
                    if (touchX > touchDownX && (touchX-touchDownX) > minDistance) {
                        fireTouchMoveRight(touchX, touchY, FastMath.sqrt((float) (dx * dx + 0)));
                    }

                    //Check if the stick is moving up
                    if (touchY > touchDownY && (touchY-touchDownY) > minDistance) {
                        fireTouchMoveUp(touchX, touchY, FastMath.sqrt((float) (0 + dy * dy)));

                    } else //Check if the stick is moving down
                    if (touchY < touchDownY && (touchDownY-touchY) > minDistance) {
                        fireTouchMoveDown(touchX, touchY, FastMath.sqrt((float) (0 + dy * dy)));
                    }

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
            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                touchDownX = 0;
                touchDownY = 0;
                fireTouchRelease(touchX, touchY);
                topCircle.center();
            }
        });

        topCircle = new Image(this, "Resources/button_circle.png", width * 0.25f, height * 0.25f, true);
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
