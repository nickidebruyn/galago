package com.galago.editor.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class Button extends TouchButton {
    
    private static float scale = 0.8f;
    
    public Button(Panel panel, String id, String text) {
        super(panel, id, "Interface/button.png", 220*scale, 48*scale, true);
        
        setBackgroundColor(EditorUtils.theme.getButtonColor());
        setText(text);
        setFontSize(14);
        setTextColor(EditorUtils.theme.getButtonTextColor());
        addEffect(new TouchEffect(this));
    }
    
    
    
}
