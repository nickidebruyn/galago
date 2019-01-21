/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;

/**
 *
 * @author nidebruyn
 */
public class GridPanelScreen extends AbstractScreen {
    
    private GridPanel gridPanel;
    private Widget firstButton;

    @Override
    protected void init() {
        
        gridPanel = new GridPanel(hudPanel, 1100, 600);
        hudPanel.add(gridPanel);
                
        for (int i = 0; i <150; i++) {
            addItem(gridPanel, "Item " + i);
        }
        
        gridPanel.layout(10, 15);
        gridPanel.center();

        
    }
    
    protected void addItem(Panel panel, String title) {
        Image i = new Image(panel, "Interface/girl.png", 54, 54);
        i.addEffect(new WobbleEffect(i, 1.5f, FastMath.nextRandomFloat()));
    }

    @Override
    protected void load() {
//        baseApplication.getViewPort().setBackgroundColor(new ColorRGBA(46f/255f, 204f/255f, 113f/255, 1));
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
    }

    @Override
    protected void show() {
        
    }

    @Override
    protected void exit() {
        
    }
    
    @Override
    protected void pause() {
        
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
//            log("Depth = " + firstButton.getWidgetNode().getWorldTranslation().z);
        }
    }
       
    
}
