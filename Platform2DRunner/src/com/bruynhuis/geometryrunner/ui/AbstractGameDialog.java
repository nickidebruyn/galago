/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Elastic;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public abstract class AbstractGameDialog extends PopupDialog {
    
    private static final float scale = 2f;

    public AbstractGameDialog(Window window, String titleText) {
        super(window, "Interface/panel.png", 300*scale, 200*scale);
//        super(window, null, 700, 460);
//        setTransparency(0.5f);
        
        setTitle(titleText);
        setTitleColor(ColorRGBA.Brown);
        setTitleSize(24);
        title.centerTop(0, -2);
        
    }    
    
    
    @Override
    public void show() {        
        super.show(); //To change body of generated methods, choose Tools | Templates.
        window.getApplication().getSoundManager().playSound("menu");
        centerTop(0, -400);
        
        Tween.to(this, WidgetAccessor.POS_XY, 0.5f)
                .target(0f, 0f)
                .ease(Elastic.OUT)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        
                    }
                })
                .start(window.getApplication().getTweenManager());    
    }

    @Override
    public void hide() {
        Tween.to(this, WidgetAccessor.POS_XY, 0.5f)
                .target(0f, -window.getHeight())
                .ease(Elastic.OUT)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        AbstractGameDialog.super.hide();
                    }
                })
                .start(window.getApplication().getTweenManager());    
    }
}
