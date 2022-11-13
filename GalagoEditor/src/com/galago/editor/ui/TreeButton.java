package com.galago.editor.ui;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;
import com.jme3.scene.Spatial;

/**
 *
 * @author ndebruyn
 */
public class TreeButton extends TouchButton {
    
    private Spatial item;

    public TreeButton(Panel panel, String id, Spatial item) {
        super(panel, id, "Interface/tree-item.png", EditorUtils.HIERARCHYBAR_WIDTH, 18, new FontStyle(14));
        this.item = item;
        this.setTextAlignment(TextAlign.LEFT);
        this.setTextVerticalAlignment(TextAlign.CENTER);
        
    }

    public Spatial getItem() {
        return item;
    }

    public void setItem(Spatial item) {
        this.item = item;
    }
    
    

}
