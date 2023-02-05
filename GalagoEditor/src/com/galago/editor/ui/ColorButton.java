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
        setTextColor(ColorRGBA.DarkGray);
        addEffect(new TouchEffect(this));
        setText("SELECT");

    }

    public void setColor(ColorRGBA color) {
        if (color != null) {
            setBackgroundColor(color);
            
            //This code will set the text color to white if the button color is dark
            //and set the text color to dark if the button is white.
            if (color.r > 0.5f && color.b > 0.5f && color.g > 0.5f) {
                setTextColor(ColorRGBA.DarkGray);
                
            } else {
                setTextColor(ColorRGBA.White);
                
            }
            
//            setText(color.r + ", " + color.g + ", " + color.b);
            
        }

    }
}
