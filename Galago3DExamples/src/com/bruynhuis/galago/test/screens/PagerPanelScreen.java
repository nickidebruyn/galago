/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.panel.PagerPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.Widget;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class PagerPanelScreen extends AbstractScreen {
    
    private PagerPanel pagerPanel;
    private Widget firstButton;
    private TouchButtonAdapter adapter;

    @Override
    protected void init() {
        
        adapter = new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showPreviousScreen();
                }
            }
            
        };
        
        pagerPanel = new PagerPanel(hudPanel, 1280, 600);
        hudPanel.add(pagerPanel);
                
        for (int i = 0; i < 7; i++) {
            addPage(pagerPanel, "Panel " + i);
        }
        
        pagerPanel.layout();
        
//        pagerPanel.optimize();
        pagerPanel.center();
//        pagerPanel.setDepthPosition(1);
        
    }
    
    protected void addPage(Panel panel, String title) {
        
        Panel page = new Panel(panel);
        page.setBackgroundColor(ColorRGBA.LightGray);
        panel.add(page);
//        Image image = new Image(panel, "Interface/panel.png", 414, 514);
        Label heading = new Label(page, title, 26);
        heading.setTextColor(ColorRGBA.DarkGray);
        heading.centerTop(0, 10);
        
        Label some = new Label(page, "This paragraph is some info on what a real text panel should look like.", 18, 300, 400);
        some.setTextColor(ColorRGBA.Orange);
        some.center();
        
        TouchButton button = new TouchButton(page, title+"-button", "Back");
        button.centerBottom(0, 20);
        button.addTouchButtonListener(adapter);

        if (firstButton == null) {
            firstButton = page;
        }
    }

    @Override
    protected void load() {
//        baseApplication.getViewPort().setBackgroundColor(new ColorRGBA(46f/255f, 204f/255f, 113f/255, 1));
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Orange);
        
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
