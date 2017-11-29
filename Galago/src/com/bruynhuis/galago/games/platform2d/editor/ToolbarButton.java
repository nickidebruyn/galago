/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.ImageSwapEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author NideBruyn
 */
public class ToolbarButton extends TouchButton {

    public ToolbarButton(Panel panel, String id, String pictureFile, String pictureSelected) {
        super(panel, id, pictureFile, 54, 54, true);
        setText("");
        addEffect(new ImageSwapEffect(pictureFile, pictureSelected, this));
    }    

    public String getId() {
        return id;
    }
    
}
