/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class ConfirmDialog extends PopupDialog {

    private ButtonWide okButton;
    private ButtonClose closeButton;
    private Label label;

    public ConfirmDialog(Window window, String text) {
        super(window, "Resources/panel.png", window.getWidth()*0.9f, window.getHeight()*0.6f, true);
        
        title.remove();

        title = new Label(this, "Confirm", 400, 30, new FontStyle(30));
        title.setTextColor(ColorRGBA.DarkGray);
        title.centerTop(0, 20);
        
        label = new Label(this, text, 20, window.getWidth()*0.8f, 100);
        label.centerAt(0, 30);
        label.setAlignment(TextAlign.CENTER);
        label.setTextColor(ColorRGBA.Gray);        
                
        okButton = new ButtonWide(this, "confirm-ok-button", "Ok");
        okButton.centerBottom(0, 15);
        
        closeButton = new ButtonClose(this, "close confirm dialog");
        closeButton.rightTop(15, 15);
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
    
    public void addOkButtonListener(TouchButtonListener buttonListener) {
        okButton.addTouchButtonListener(buttonListener);
    }
    
//    @Override
//    public void show() {        
//        super.show(); //To change body of generated methods, choose Tools | Templates.
//        centerTop(0, -600);
//        
//        Tween.to(this, WidgetAccessor.POS_XY, 1f)
//                .target(0f, 0f)
//                .setCallback(new TweenCallback() {
//                    public void onEvent(int i, BaseTween<?> bt) {
//
//                    }
//                })
//                .start(window.getApplication().getTweenManager());    
//    }
//
//    @Override
//    public void hide() {
//        Tween.to(this, WidgetAccessor.POS_XY, 0.5f)
//                .target(0f, -window.getHeight())
//                .setCallback(new TweenCallback() {
//                    public void onEvent(int i, BaseTween<?> bt) {
//                        ConfirmDialog.super.hide();
//
//                    }
//                })
//                .start(window.getApplication().getTweenManager());    
//    }

}
