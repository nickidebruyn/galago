package com.galago.editor.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author ndebruyn
 */
public class TextureField extends Panel {

    private static float scale = 0.6f;

    private TouchButton textureButton;

    public TextureField(Panel parent, String id) {
        super(parent, null, 128 * scale, 128 * scale);

        textureButton = new TouchButton(this, id, null, 128 * scale, 128 * scale);
        textureButton.center();        

        parent.add(this);

    }

}
