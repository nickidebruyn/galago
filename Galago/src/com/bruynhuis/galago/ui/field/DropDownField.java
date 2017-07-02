/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.field;

import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author NideBruyn
 */
public class DropDownField extends TouchButton {
    
    private HashMap<Integer, String> items = new LinkedHashMap<Integer, String>();
    private boolean focus = false;
    private int selectedIndex = 0;

    public DropDownField(Panel panel, String id, String image, float width, float height, boolean lock) {
        super(panel, id, image, width, height, lock);
        setText("");
        setTextColor(ColorRGBA.DarkGray);
        setTextAlignment(TextAlign.LEFT);
        setFontSize(30);
        setMargins(25, 0, 0, 0);

    }

    public HashMap<Integer, String> getItems() {
        return items;
    }

    public void setItems(HashMap<Integer, String> items) {
        this.items = items;
        this.selectedIndex = 0;
        refreshOptions();
    }

    @Override
    public void fireTouchUp(float x, float y, float tpf) {        
        
        window.removeFocusFromDropdown();
        focus = true;
        window.getApplication().doShowSelection(items);
        
        super.fireTouchUp(x, y, tpf);

    }
    
    public void blur() {
        focus = false;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        refreshOptions();
    }

    public boolean isFocus() {
        return focus;
    }
    
    protected void refreshOptions() {
        if (items != null && items.size() > 0 && selectedIndex < items.size()) {
            String text = new ArrayList<String>(items.values()).get(selectedIndex);
            setText(text);
        } else {
            setText(" ");
        }
    }
    
    public Integer getValue() {
        Integer val = null;
        if (items != null && items.size() > 0) {
            val = new ArrayList<Integer>(items.keySet()).get(selectedIndex);
        }
        return val;
    }
    
    public void setValue(Integer val) {
        int i = 0;
        if (items != null && items.size() > 0) {
            for (Iterator<Integer> it = items.keySet().iterator(); it.hasNext();) {
                Integer integer = it.next();
                if (integer.equals(val)) {
                    setSelectedIndex(i);
                    return;
                }
                i++;
            }
        }
    }
}
