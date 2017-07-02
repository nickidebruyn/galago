/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.window.Window;

/**
 * A GridPanel can be used when you wish to display widgets in the form of a grid.
 * This is normally something like level selection screens.
 * The layout() method must be called after all widgets was added.
 *
 * @author nidebruyn
 */
public class GridPanel extends Panel {

    /**
     * 
     * @param window
     * @param pictureFile 
     */
    public GridPanel(Window window, String pictureFile) {
        super(window, pictureFile);
    }

    /**
     * 
     * @param window
     * @param pictureFile
     * @param width
     * @param height 
     */
    public GridPanel(Window window, String pictureFile, float width, float height) {
        super(window, pictureFile, width, height);
    }

    /**
     * 
     * @param parent
     * @param pictureFile
     * @param width
     * @param height 
     */
    public GridPanel(Widget parent, String pictureFile, float width, float height) {
        super(parent.getWindow(), parent, pictureFile, width, height);
    }

    /**
     * 
     * @param parent
     * @param width
     * @param height 
     */
    public GridPanel(Widget parent, float width, float height) {
        super(parent.getWindow(), parent, null, width, height);
    }

    /**
     * Call this method after all widgets was added to the gridpanel.
     * 
     * @param rows
     * @param cols 
     */
    public void layout(int rows, int cols) {
        float sectionSizeX = getWidth()/cols;
        float sectionSizeY = getHeight()/rows;
        
        float positionX = -getWidth()*0.5f + sectionSizeX*0.5f;
        float positionY = getHeight()*0.5f - sectionSizeY*0.5f;
        int i = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (i < widgets.size()) {
                    Widget widget = widgets.get(i);
                    if (widget != null) {
                        widget.setPosition(positionX, positionY);
                        positionX += sectionSizeX;
                    }
                }

                i++;
            }
            positionY -= sectionSizeY;
            positionX = -getWidth()*0.5f + sectionSizeX*0.5f;

        }
//        optimize();
    }
    
    @Override
    protected boolean isBatched() {
        return true;
    }
}
