/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens.editor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;
import com.galago.example.platformer2d.ui.ButtonClose;
import com.galago.example.platformer2d.ui.ButtonWide;
import com.galago.example.platformer2d.ui.ShowListener;

/**
 *
 * @author NideBruyn
 */
public class ConfirmDialog extends PopupDialog {
    
    protected ShowListener showListener;
    private ButtonWide okButton;
    private ButtonClose closeButton;
    private Label label;

    public ConfirmDialog(Window window, String text) {
        super(window, "Interface/panel-small.png", 680, 256, true);
        
        Image titleImg = new Image(this, "Interface/panel-title.png", 498*0.7f, 110*0.7f, true);
        titleImg.centerTop(0, -10);
        
        title.remove();

        title = new Label(this, "Confirm", 400, 80, new FontStyle(36));
        title.centerTop(0, -16);
        
        label = new Label(this, text, 28, 450, 50);
        label.centerAt(0, 10);
        label.setAlignment(TextAlign.CENTER);
        label.setTextColor(ColorRGBA.DarkGray);        
                
        okButton = new ButtonWide(this, "confirm-ok-button", "Ok");
        okButton.centerBottom(0, 32);
        
        closeButton = new ButtonClose(this, "close confirm dialog");
        closeButton.rightTop(0, 0);
        closeButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                ConfirmDialog.this.hide();
            }
            
        });
        
    }    
    
    public void setText(String text) {
        label.setText(text);
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
    
    public void addOkButtonListener(TouchButtonListener buttonListener) {
        okButton.addTouchButtonListener(buttonListener);
    }
    
    @Override
    public void show() {        
        super.show(); //To change body of generated methods, choose Tools | Templates.
        centerTop(0, -600);
        
        Tween.to(this, WidgetAccessor.POS_XY, 0.6f)
                .target(0f, 0f)
//                .ease(Linear.INOUT)
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
                        ConfirmDialog.super.hide();
                        fireShowHiddenListener();
                    }
                })
                .start(window.getApplication().getTweenManager());    
    }

}
