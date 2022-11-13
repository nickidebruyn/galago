package com.galago.editor.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class ToolbarButton extends TouchButton {

    public ToolbarButton(Panel panel, String id, String pictureFile) {
        super(panel, id, pictureFile, EditorUtils.TOOLBAR_BUTTON_SIZE, EditorUtils.TOOLBAR_BUTTON_SIZE);
        
    }    
    

}
