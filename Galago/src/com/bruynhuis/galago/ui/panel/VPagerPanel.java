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
 * The PagerPanel is similar to the Horizontal Panel except it has some nice
 * scrolling depth effects. This is almost the same as a cateye/carousel widget.
 *
 * @author nidebruyn
 */
public class VPagerPanel extends Panel implements TouchButtonListener {

    private ControlButton controlButton;
    private ArrayList<Widget> pages = new ArrayList<Widget>();
    private float touchDown;
    private float centerOffset = 1f;
    private float tempOffset = 0;
    private float maxCenterOffset = 0;
    private float centerOffsetPercentage = 0;
    private float scaleFactor = 0;
    private float depthFactor = 0;
    private float touchedDownY = 0;
    private float touchedUpY = 0;
    private float centerOffsetLeft = 0;
    private boolean applyScale = false;
    private boolean applyTransparency = false;

    /**
     *
     * @param parent
     * @param width
     * @param height
     */
    public VPagerPanel(Widget parent, float width, float height) {
        super(parent.getWindow(), parent, null, width, height);

        controlButton = new ControlButton((Panel) parent, "pager-panel-controlbutton", width, height);
        controlButton.addTouchButtonListener(this);

        maxCenterOffset = this.getHeight() * 0.5f;

    }

    @Override
    public void add(Widget widget) {
        if (!widget.equals(controlButton)) {
            pages.add(widget);
        } else {
            window.log("Control found");
        }

        super.add(widget); //To change body of generated methods, choose Tools | Templates.
    }

    public void layoutTop(float offsetY) {
        centerOffset = -maxCenterOffset + offsetY;
        layout();
    }

    public void layout(float offsetX, float offsetY) {
        centerOffset = -maxCenterOffset + offsetY;
        centerOffsetLeft = offsetX;
        layout();
    }

    /**
     * This method will handle the way we layout the
     */
    public void layout() {
        float sectionSize = getHeight() / widgets.size();
        float position = -getHeight() * 0.5f + sectionSize * 0.5f;
        float distanceFromCenter = 0;

//        window.log("************ Layouts **************");
        for (int i = pages.size() - 1; i >= 0; i--) {
            Widget widget = pages.get(i);
            widget.setPosition(centerOffsetLeft, position + centerOffset);
            position += sectionSize;

            distanceFromCenter = FastMath.abs(-this.getPosition().y + widget.getWidgetNode().getWorldTranslation().y - (getHeight() * 0.5f));
            centerOffsetPercentage = distanceFromCenter / maxCenterOffset;
            if (centerOffsetPercentage > 1f) {
                centerOffsetPercentage = 1f;
            }
            scaleFactor = 1f - centerOffsetPercentage;
            depthFactor = -1f + scaleFactor;
            if (depthFactor > -0.05f) {
                depthFactor = 0f;
            }
//            window.log(i + " depthFactor=" + depthFactor);
            if (applyScale) {
                widget.setScale(scaleFactor);
            }

            widget.setDepthPosition(depthFactor);

            if (applyTransparency) {
                widget.setTransparency(scaleFactor > 0.98f ? scaleFactor : scaleFactor*0.5f);
//                window.log("dis = " + distanceFromCenter);
                if (distanceFromCenter > this.getWidth()) {
                    widget.setVisible(false);
                } else {
                    widget.setVisible(true);
                }
            }

        }
//        window.log("*********** Layouts End ************");
//        optimize();
    }

    private boolean canMove() {
        return ((getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen())
                || (getParent().getParent() != null && (getParent().getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen())
                || (getParent().getParent() != null && getParent().getParent().getParent() != null && (getParent().getParent().getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen())
                || (!(getParent() instanceof PopupDialog) && this.isVisible() && !window.isDialogOpen());
    }

    @Override
    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
        if (canMove()) {
            touchedDownY = touchY;
            touchedUpY = touchY;

            tempOffset = centerOffset;
            touchDown = -(window.getHeight() * 0.5f) + touchY;
        }

    }

    @Override
    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        if (canMove()) {
            touchDown = 0;

            touchedUpY = touchY;
        }

    }

    @Override
    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
        if (canMove()) {

            touchedUpY = touchY;

            float diff = (-(window.getHeight() * 0.5f) + touchY) - touchDown;
            centerOffset = tempOffset + diff;

            //Fix that the position doesn't go higher than the min and max
            if (centerOffset < this.getHeight() * -0.5f) {
                centerOffset = this.getHeight() * -0.5f;

            } else if (centerOffset > this.getHeight() * 0.5f) {
                centerOffset = this.getHeight() * 0.5f;

            }

            layout();

        }
    }

    public float getCenterOffset() {
        return centerOffset;
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
        Debug.log("Moved distance of page: " + FastMath.abs(touchedUpY - touchedDownY));
        return FastMath.abs(touchedUpY - touchedDownY) > 10f;
    }

    @Override
    public void doHoverOver(float touchX, float touchY, float tpf, String uid) {

    }

    @Override
    public void doHoverOff(float touchX, float touchY, float tpf, String uid) {

    }

    public boolean isApplyScale() {
        return applyScale;
    }

    public void setApplyScale(boolean applyScale) {
        this.applyScale = applyScale;
    }

    public boolean isApplyTransparency() {
        return applyTransparency;
    }

    public void setApplyTransparency(boolean applyTransparency) {
        this.applyTransparency = applyTransparency;
    }

}
