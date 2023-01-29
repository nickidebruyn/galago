package com.galago.editor.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author ndebruyn
 */
public class ColorButton extends TouchButton {

    private static float scale = 0.6f;

    public ColorButton(Panel panel, String id) {
        super(panel, id, "Interface/button.png", 260 * scale, 48 * scale);

        setBackgroundColor(ColorRGBA.White);
        setFontSize(14);
        setTextColor(EditorUtils.theme.getButtonTextColor());
        addEffect(new TouchEffect(this));
        setText("Color");

    }

    public void setColor(ColorRGBA color) {
        if (color != null) {
            setBackgroundColor(color);
        }

    }
}
