/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class ButtonLayoutsScreen extends AbstractScreen {
    
    private TouchButtonAdapter buttonListener;
    
    private TouchButton leftTopButton;
    private TouchButton leftCenterButton;
    private TouchButton leftBottomButton;

    private TouchButton rightTopButton;
    private TouchButton rightCenterButton;
    private TouchButton rightBottomButton;    
    
    private TouchButton centerTopButton;
    private TouchButton centerCenterButton;
    private TouchButton centerBottomButton;    

    @Override
    protected void init() {
        buttonListener = new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getViewPort().setBackgroundColor(ColorRGBA.randomColor());
                }
            }
            
        };
        
        
        //Left
        leftTopButton = new TouchButton(hudPanel, "leftTopButton", "LeftTop");
        leftTopButton.leftTop(2, 2);
        leftTopButton.setTextAlignment(TextAlign.LEFT);
        leftTopButton.setTextVerticalAlignment(TextAlign.TOP);
        leftTopButton.addTouchButtonListener(buttonListener);
        
        leftCenterButton = new TouchButton(hudPanel, "leftCenterButton", "LeftCenter");
        leftCenterButton.leftCenter(2, 2);
        leftCenterButton.setTextAlignment(TextAlign.LEFT);
        leftCenterButton.setTextVerticalAlignment(TextAlign.CENTER);
        leftCenterButton.addTouchButtonListener(buttonListener);
        
        leftBottomButton = new TouchButton(hudPanel, "leftBottomButton", "LeftBottom");
        leftBottomButton.leftBottom(2, 10);
        leftBottomButton.setTextAlignment(TextAlign.LEFT);
        leftBottomButton.setTextVerticalAlignment(TextAlign.BOTTOM);
        leftBottomButton.addTouchButtonListener(buttonListener);
        
        
        //right
        rightTopButton = new TouchButton(hudPanel, "rightTopButton", "RightTop");
        rightTopButton.rightTop(2, 2);
        rightTopButton.setTextAlignment(TextAlign.RIGHT);
        rightTopButton.setTextVerticalAlignment(TextAlign.TOP);
        rightTopButton.addTouchButtonListener(buttonListener);
        
        rightCenterButton = new TouchButton(hudPanel, "rightCenterButton", "RightCenter");
        rightCenterButton.rightCenter(2, 2);
        rightCenterButton.setTextAlignment(TextAlign.RIGHT);
        rightCenterButton.setTextVerticalAlignment(TextAlign.CENTER);
        rightCenterButton.addTouchButtonListener(buttonListener);
        
        rightBottomButton = new TouchButton(hudPanel, "rightBottomButton", "RightBottom");
        rightBottomButton.rightBottom(2, 10);
        rightBottomButton.setTextAlignment(TextAlign.RIGHT);
        rightBottomButton.setTextVerticalAlignment(TextAlign.BOTTOM);
        rightBottomButton.addTouchButtonListener(buttonListener);
        
        //center
        centerTopButton = new TouchButton(hudPanel, "centerTopButton", "CenterTop");
        centerTopButton.centerTop(0, 2);
        centerTopButton.setTextAlignment(TextAlign.CENTER);
        centerTopButton.setTextVerticalAlignment(TextAlign.TOP);
        centerTopButton.addTouchButtonListener(buttonListener);
        
        centerCenterButton = new TouchButton(hudPanel, "rightCenterButton", "RightCenter");
        centerCenterButton.centerAt(0, 0);
        centerCenterButton.setTextAlignment(TextAlign.CENTER);
        centerCenterButton.setTextVerticalAlignment(TextAlign.CENTER);
        centerCenterButton.addTouchButtonListener(buttonListener);
        
        centerBottomButton = new TouchButton(hudPanel, "centerBottomButton", "CenterBottom");
        centerBottomButton.centerBottom(0, 10);
        centerBottomButton.setTextAlignment(TextAlign.CENTER);
        centerBottomButton.setTextVerticalAlignment(TextAlign.BOTTOM);
        centerBottomButton.addTouchButtonListener(buttonListener);
                
//        hudPanel.optimize();
    }

    @Override
    protected void load() {
        
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
    
}
