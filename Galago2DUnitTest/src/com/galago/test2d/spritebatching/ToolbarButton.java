/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.test2d.spritebatching;

import com.bruynhuis.galago.ui.SpriteWidget;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author NideBruyn
 */
public class ToolbarButton extends Panel {
    
    private ControlButton button;
    private SpriteWidget spriteWidget;
    private int columnIndex;
    private int rowIndex;
    private float borderSize = 2;
    private String uid;    
    private WobbleEffect wobbleEffect;

    public ToolbarButton(Panel parent, String uid, String pictureFile, float size, float borderSize, int cols, int rows, int colIndex, int rowIndex) {
        super(parent, "Interface/button-toolbar.png", size, size, true);
        this.columnIndex = colIndex;
        this.rowIndex = rowIndex;
        this.uid = uid;
        this.borderSize = borderSize;
        
        spriteWidget = new SpriteWidget(this, pictureFile, (size-borderSize*2), (size-borderSize*2), cols, rows, colIndex, rowIndex, true);
        spriteWidget.center();
        
        button = new ControlButton(this, uid, size, size, true);
        button.addEffect(new TouchEffect(this));
        
        wobbleEffect = new WobbleEffect(this, 1.1f, 0.6f);
        wobbleEffect.setEnabled(false);
        this.addEffect(wobbleEffect);
        
        parent.add(this);
    }
    
    public void select() {
        updatePicture("Interface/button-toolbar-selected.png");
        wobbleEffect.setEnabled(true);
    }
    
    public void unselect() {
        updatePicture("Interface/button-toolbar.png");
        wobbleEffect.setEnabled(false);
        this.setScale(1);
    }
    
    public void addTouchButtonListener(TouchButtonListener listener) {
        button.addTouchButtonListener(listener);
        
    }

    public String getUid() {
        return uid;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }
    
}
