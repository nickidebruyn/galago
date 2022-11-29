package com.galago.editor.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.FocusListener;
import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class LongField extends Panel {

    private static float scale = 0.6f;

    private InputField inputField;
    private Image focusImage;

    public LongField(Panel parent, String id) {
        super(parent, null, 260 * scale, 48 * scale);

        inputField = new InputField(this, id);
        inputField.center();
        
        focusImage = new Image(this, "Interface/field-focus.png", 260 * scale, 48 * scale);
        focusImage.setBackgroundColor(EditorUtils.theme.getSelectionColor());
        focusImage.center();
        focusImage.setTransparency(0);
        
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void doFocus(String id) {
                focusImage.setTransparency(1);
            }

            @Override
            public void doBlur(String id) {
                focusImage.setTransparency(0);
            }
            
            
        });

        parent.add(this);

    }

    public void addKeyboardListener(KeyboardListener keyboardListener) {
        inputField.addKeyboardListener(keyboardListener);

    }

    public void setValue(long val) {
        inputField.setText(String.valueOf(val));

    }

    public long getValue() {
        
        if (inputField.getText().isBlank()) {
            return 0;
        }

        return Long.parseLong(inputField.getText());

    }

}
