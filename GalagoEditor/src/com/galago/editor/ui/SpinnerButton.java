package com.galago.editor.ui;

import com.bruynhuis.galago.ui.button.Spinner;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class SpinnerButton extends Spinner {

    private static float scale = 0.6f;

    public SpinnerButton(Panel panel, String id, String[] options) {
        super(panel, id, "Interface/button-spinner.png", 260 * scale, 48 * scale, options);

        setBackgroundColor(EditorUtils.theme.getButtonColor());
        setFontSize(14);
        setTextColor(EditorUtils.theme.getButtonTextColor());
        addEffect(new TouchEffect(this));
        
    }

}
