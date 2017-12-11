/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.control.FlickerControl;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.FastMath;

/**
 *
 * @author NideBruyn
 */
public class Toolbar extends Panel implements TouchButtonListener {
    
    private float yVal = 7;
    private float xVal = 130;
    private boolean shifted = false;
    private String selectedItem = "erase";
    private Image selectedImage;

    public Toolbar(Panel parent) {
        super(parent, "Resources/editor/side-panel-left.png", 124, 800);
        
        addToolTopButton("erase", "Resources/editor/icon-erase.png", 0);
        
        
        selectedImage = new Image(this, "Resources/editor/toolbutton-highlight.png", 54, 54, true);
        selectedImage.leftTop(130, 7);
        selectedImage.getWidgetNode().addControl(new FlickerControl(3f));
                
        parent.add(this);
    }    
    
    public void addToolButton(String id, String image, float angle) {
        float xxVal = shifted ? 54 : 0;
        
        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Resources/editor/toolbutton.png", "Resources/editor/toolbutton-on.png");
        toolbarButton.leftTop(xxVal, yVal);
        toolbarButton.addTouchButtonListener(this);
        
        Image img = new Image(this, image, 32, 32, true);
        img.leftTop(xxVal + 11, yVal + 11);
        img.rotate(angle* FastMath.DEG_TO_RAD);
        
        if (shifted) {
            yVal += 54;
            shifted = false;
        } else {
            shifted = true;
            
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
        img.leftTop(xVal + 11, 7+11);
        img.rotate(angle* FastMath.DEG_TO_RAD);
        
        xVal += 54;
        
        
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
    }

    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        this.selectedItem = uid;
//        window.log("Selected: " + this.selectedItem);
        for (int i = 0; i < this.getWidgets().size(); i++) {
            Widget widget = this.getWidgets().get(i);
            if (widget instanceof ToolbarButton && ((ToolbarButton)widget).getId().equals(uid)) {
                selectedImage.setPosition(widget.getPosition().x, widget.getPosition().y);
            }
        }
    }

    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
    }

    public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
    }
    
}
