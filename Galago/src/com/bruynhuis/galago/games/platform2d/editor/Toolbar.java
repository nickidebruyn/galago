/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.control.FlickerControl;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.SpriteWidget;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.FastMath;

/**
 *
 * @author NideBruyn
 */
public class Toolbar extends Panel implements TouchButtonListener {

    private float yVal = 70;
    private float xVal = 7;
    private float x2Val = 7;
    private boolean shifted = false;
    private String selectedItem = "erase";
    private Image selectedImage;
    private boolean doubleSideBar = true;
    private TouchButtonListener touchButtonListener;

    public Toolbar(Panel parent) {
        super(parent, null, parent.getWindow().getWidth(), parent.getWindow().getHeight());

        addToolTopButton("erase", "Resources/editor/icon-erase.png", 0);
        addToolTopButton("rotate", "Resources/editor/icon-rotate.png", 0);
        addToolTopButton("select", null, 0);

        selectedImage = new Image(this, "Resources/editor/toolbutton-highlight.png", 54, 54, true);
        selectedImage.leftTop(7, 7);
        selectedImage.getWidgetNode().addControl(new FlickerControl(3f));

        parent.add(this);
    }

    public void addToolButton(String id, String image, float angle) {
        this.addToolButton(id, image, angle, 1, 1, -1, -1);

    }

    public void addToolButton(String id, String image, float angle, int cols, int rows, int indexX, int indexY) {
        float xxVal = shifted ? 54 : 0;

        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Resources/editor/toolbutton.png", "Resources/editor/toolbutton-on.png");
        toolbarButton.leftTop(xxVal, yVal);
        toolbarButton.addTouchButtonListener(this);

        if (indexX < 0 || indexY < 0) {
            Image img = new Image(this, image, 32, 32, true);
            img.leftTop(xxVal + 11, yVal + 11);
            img.rotate(angle * FastMath.DEG_TO_RAD);
        } else {
            SpriteWidget img = new SpriteWidget(this, image, 32, 32, cols, rows, indexX, indexY, true);
            img.leftTop(xxVal + 11, yVal + 11);
            img.rotate(angle * FastMath.DEG_TO_RAD);
        }

        if (doubleSideBar) {
            if (shifted) {
                yVal += 54;
                shifted = false;
            } else {
                shifted = true;

            }
        } else {
            yVal += 54;
        }

    }

    public void addSpace() {
        if (shifted) {
            yVal += 66;
            shifted = false;
        } else {
            yVal += 12;
        }

    }

    public void addToolTopButton(String id, String image, float angle) {

        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Resources/editor/toolbutton.png", "Resources/editor/toolbutton-on.png");
        toolbarButton.leftTop(xVal, 7);
        toolbarButton.addTouchButtonListener(this);

        Image img = new Image(this, image, 32, 32, true);
        img.leftTop(xVal + 11, 7 + 11);
        img.rotate(angle * FastMath.DEG_TO_RAD);

        xVal += 54;

    }

    public void addToolBottomButton(String id, String image, float angle) {

        this.addToolBottomButton(id, image, angle, 1, 1, -1, -1);

    }

    public void addToolBottomButton(String id, String image, float angle, int cols, int rows, int indexX, int indexY) {

        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Resources/editor/toolbutton.png", "Resources/editor/toolbutton-on.png");
        toolbarButton.rightBottom(x2Val, 7);
        toolbarButton.addTouchButtonListener(this);

        if (indexX < 0 || indexY < 0) {
            Image img = new Image(this, image, 32, 32, true);
            img.rightBottom(x2Val + 11, 7 + 11);
            img.rotate(angle * FastMath.DEG_TO_RAD);

        } else {
            SpriteWidget img = new SpriteWidget(this, image, 32, 32, cols, rows, indexX, indexY, true);
            img.rightBottom(x2Val + 11, 7 + 11);
            img.rotate(angle * FastMath.DEG_TO_RAD);
        }

        x2Val += 54;

    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
        if (touchButtonListener != null) {
            touchButtonListener.doTouchDown(touchX, touchY, tpf, uid);
        }
        
    }

    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        this.selectedItem = uid;
//        window.log("Selected: " + this.selectedItem);
        for (int i = 0; i < this.getWidgets().size(); i++) {
            Widget widget = this.getWidgets().get(i);
            if (widget instanceof ToolbarButton && ((ToolbarButton) widget).getId().equals(uid)) {
                selectedImage.setPosition(widget.getPosition().x, widget.getPosition().y);
            }
        }
        
        if (touchButtonListener != null) {
            touchButtonListener.doTouchUp(touchX, touchY, tpf, uid);
        }
    }

    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
    }

    public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
    }

    public boolean isDoubleSideBar() {
        return doubleSideBar;
    }

    public void setDoubleSideBar(boolean doubleSideBar) {
        this.doubleSideBar = doubleSideBar;
    }

    public void setTouchButtonListener(TouchButtonListener touchButtonListener) {
        this.touchButtonListener = touchButtonListener;
    }

    @Override
    public void doHoverOver(float touchX, float touchY, float tpf, String uid) {
     
    }

    @Override
    public void doHoverOff(float touchX, float touchY, float tpf, String uid) {
        
    }
    
    

}
