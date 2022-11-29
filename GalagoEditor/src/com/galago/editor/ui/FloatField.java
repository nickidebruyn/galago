package com.galago.editor.ui;

import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author ndebruyn
 */
public class FloatField extends Panel {
    
    private static float scale = 0.6f;
    
    private InputField inputField;
    
    public FloatField(Panel parent, String id) {
        super(parent, null, 260 * scale, 48 * scale);
        
        inputField = new InputField(this, id);
        inputField.center();        
        
        parent.add(this);
        
    }
    
    public void addKeyboardListener(KeyboardListener keyboardListener) {
        inputField.addKeyboardListener(keyboardListener);
        
    }
    
    public void setValue(float val) {
        inputField.setText(String.valueOf(val));
        
    }
    
    public float getValue() {
        
        if (inputField.getText().isBlank()) {
            return 0;
        }
        
        return Float.parseFloat(inputField.getText());
    }
    
}
