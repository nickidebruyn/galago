/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.scene.BatchNode;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A Panel will have a set of widgets attached to it.
 *
 * @author nidebruyn
 */
public class Panel extends ImageWidget {

    protected ArrayList<Widget> widgets = new ArrayList<Widget>();
    
    /**
     * 
     * @param parent 
     */
    public Panel(Widget parent) {
        this(parent.getWindow(), parent, "Resources/panel.png", 600, 400);
    }

    /**
     * 
     * @param window
     * @param pictureFile 
     */
    public Panel(Window window, String pictureFile) {
        this(window, pictureFile, window.getWidth(), window.getHeight());
    }
    
    public Panel(Window window, String pictureFile, boolean lockScale) {
        this(window, pictureFile, window.getWidth(), window.getHeight(), lockScale);
    }

    /**
     * 
     * @param window
     * @param pictureFile
     * @param width
     * @param height 
     */
    public Panel(Window window, String pictureFile, float width, float height) {
        super(window, null, pictureFile, width, height, false);
    }
    
    public Panel(Window window, String pictureFile, float width, float height, boolean lockScale) {
        super(window, null, pictureFile, width, height, lockScale);
    }

    /**
     * 
     * @param window
     * @param parent
     * @param pictureFile
     * @param width
     * @param height 
     */
    public Panel(Window window, Widget parent, String pictureFile, float width, float height) {
        super(window, parent, pictureFile, width, height, false);

    }

    /**
     * 
     * @param parent
     * @param pictureFile
     * @param width
     * @param height 
     */
    public Panel(Widget parent, String pictureFile, float width, float height) {
        super(parent.getWindow(), parent, pictureFile, width, height, false);

    }
    
    public Panel(Widget parent, String pictureFile, float width, float height, boolean lockScale) {
        super(parent.getWindow(), parent, pictureFile, width, height, lockScale);

    }
    
    public void add(Widget widget) {
        widgets.add(widget);
        widget.add(widgetNode);
    }

    public ArrayList<Widget> getWidgets() {
        return widgets;
    }

    /**
     * remove all widgets.
     */
    public void clear() {
        for (int i = 0; i < widgets.size(); i++) {
            Widget widget = widgets.get(i);
            widget.remove();
        }
        widgets.clear();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        for (Iterator<Widget> it = widgets.iterator(); it.hasNext();) {
            Widget widget = it.next();
            widget.setVisible(visible);
        }
    }

    @Override
    public void setTransparency(float alpha) {
        //the widgets
        if (widgets != null) {
            for (Iterator<Widget> it = widgets.iterator(); it.hasNext();) {
                Widget widget = it.next();
                widget.setTransparency(alpha);
            }
        }

        super.setTransparency(alpha);

    }
    
    @Override
    protected boolean isBatched() {
        return false;
    }
    
    /**
     * This method will try to optimize the node and all its children.
     */
    public void optimize() {
        if (widgetNode instanceof BatchNode) {
            ((BatchNode)widgetNode).batch();
        }        
    }
}
