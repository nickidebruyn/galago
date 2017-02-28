/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.field.TextField;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class DescriptionDialog extends PopupDialog {
    
    private TextField textField;
    private TouchButton touchButton;

    public DescriptionDialog(Window window) {
        super(window, "Resources/panel.png", 512, 256);
        
        setTitle("Description");
        setTitleColor(ColorRGBA.DarkGray);
        setTitleSize(22);
        
        textField = new TextField(this, "descr_field_edit", "Resources/textfield.png", 500, 40);
        textField.setMaxLength(50);
        textField.center();
        
        touchButton = new TouchButton(this, "save_button", "Save");
        touchButton.addEffect(new TouchEffect(touchButton));
        touchButton.centerBottom(0, 10);
        
    }    
    
    public String getDescription() {
        return textField.getText();
    }

    public void show(String description) {
        textField.setText(description);
        super.show();
    }
    
    public void addSaveButtonListener(TouchButtonAdapter touchButtonAdapter) {
        touchButton.addTouchButtonListener(touchButtonAdapter);
    }
    
}
