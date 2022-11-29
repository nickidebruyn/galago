package com.galago.editor.ui;

import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.field.TextField;
import com.bruynhuis.galago.ui.listener.FocusListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class InputField extends TextField {

    private static float scale = 0.6f;

    public InputField(Panel panel, String id) {
        super(panel, id, "Interface/field.png", 260 * scale, 48 * scale);

        setTextAlignment(TextAlign.LEFT);
        setTextVerticalAlignment(TextAlign.CENTER);

        setBackgroundColor(EditorUtils.theme.getFieldColor());
        setFontSize(14);
        setTextColor(EditorUtils.theme.getFieldTextColor());
        

    }

}
