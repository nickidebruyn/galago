/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.ui.window.Window;

/**
 *
 * @author NideBruyn
 */
public abstract class AbstractGameDialog extends PopupDialog {
    
    protected ShowListener showListener;

    public AbstractGameDialog(Window window, String titleText) {
        super(window, "Interface/panel-medium.png", 680, 474, true);
        
        Image titleImg = new Image(this, "Interface/panel-title.png", 498*0.7f, 110*0.7f, true);
        titleImg.centerTop(0, -10);
        
//        setTitle(titleText);
//        setTitleColor(ColorRGBA.White);
//        setTitleSize(74);
        
        title.remove();
//        title.centerTop(0, 0);
//        add(title);
        
        title = new Label(this, titleText, 400, 80, new FontStyle(36));
        title.centerTop(0, -16);
        
        
    }    
    
    public void addShowListener(ShowListener showListener) {
        this.showListener = showListener;
        
    }
    
    protected void fireShowShownListener() {
        if (showListener != null) {
            showListener.shown();
        }
    }
    
    protected void fireShowHiddenListener() {
        if (showListener != null) {
            showListener.hidden();
        }
    }
    
    @Override
    public void show() {        
        super.show(); //To change body of generated methods, choose Tools | Templates.
        centerTop(0, -600);
        
        Tween.to(this, WidgetAccessor.POS_XY, 0.6f)
                .target(0f, 0f)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        fireShowShownListener();
                    }
                })
                .start(window.getApplication().getTweenManager());    
    }

    @Override
    public void hide() {
        Tween.to(this, WidgetAccessor.POS_XY, 0.5f)
                .target(0f, -window.getHeight())
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        AbstractGameDialog.super.hide();
                        fireShowHiddenListener();
                    }
                })
                .start(window.getApplication().getTweenManager());    
    }
}
