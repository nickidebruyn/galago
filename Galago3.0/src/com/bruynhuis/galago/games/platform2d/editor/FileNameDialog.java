/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class FileNameDialog extends PopupDialog {

    private ButtonWide okButton;
    private ButtonClose closeButton;
    private Label label;
    private NameField nameField;

    public FileNameDialog(Window window) {
        super(window, "Resources/panel.png", 680, 256, true);
        
        title.remove();

        title = new Label(this, "New Level", 400, 80, new FontStyle(36));
        title.centerTop(0, -16);
        
        label = new Label(this, "Level Name: ", 32, 250, 50);
        label.centerAt(-160, 10);
        label.setTextColor(ColorRGBA.DarkGray);        
        
        nameField = new NameField(this);
        nameField.centerAt(100, 10);
        
        okButton = new ButtonWide(this, "file-edit-ok-button", "Ok");
        okButton.centerBottom(0, 32);
        
        closeButton = new ButtonClose(this, "Close new file button");
        closeButton.rightTop(0, 0);
        closeButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                FileNameDialog.this.hide();
            }
            
        });
        
    }    
    
    public void addOkButtonListener(TouchButtonListener buttonListener) {
        okButton.addTouchButtonListener(buttonListener);
    }
    
    @Override
    public void show() {        
        super.show(); //To change body of generated methods, choose Tools | Templates.
        centerTop(0, -600);
        nameField.setText("");
        
        Tween.to(this, WidgetAccessor.POS_XY, 1f)
                .target(0f, 0f)
                .ease(Bounce.OUT)
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
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        FileNameDialog.super.hide();

                    }
                })
                .start(window.getApplication().getTweenManager());    
    }

    public String getFileName() {
        return nameField.getText();
    }
}
