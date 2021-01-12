/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class LinkLevelDialog extends PopupDialog {

    private ButtonWide okButton;
    private ButtonClose closeButton;
    private Label label;
    private NameField keyField;
    private NameField nameField;

    public LinkLevelDialog(Window window) {
        super(window, "Resources/panel.png", window.getWidth()*0.9f, window.getHeight()*0.6f, true);
        
        title.remove();

        title = new Label(this, "Tile Property", 400, 30, new FontStyle(30));
        title.setTextColor(ColorRGBA.DarkGray);
        title.centerTop(0, 20);
        
        label = new Label(this, "Key: ", 26, 400, 50);
        label.centerAt(0, 90);
        label.setTextColor(ColorRGBA.Gray);        
        
        keyField = new NameField(this);
        keyField.centerAt(0, 30);
        
        label = new Label(this, "Value: ", 26, 400, 50);
        label.centerAt(0, -30);
        label.setTextColor(ColorRGBA.Gray);        
        
        nameField = new NameField(this);
        nameField.centerAt(0, -90);
        
        okButton = new ButtonWide(this, "link-edit-ok-button", "Ok");
        okButton.centerBottom(0, 15);
        
        closeButton = new ButtonClose(this, "Close link file button");
        closeButton.rightTop(15, 15);
        closeButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                LinkLevelDialog.this.hide();
            }
            
        });
        
    }    
    
    public void addOkButtonListener(TouchButtonListener buttonListener) {
        okButton.addTouchButtonListener(buttonListener);
    }
    
    public String getValue() {
        return nameField.getText();
    }
    
    public String getKey() {
        return keyField.getText();
    }
}
