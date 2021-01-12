/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

/**
 * All widgets that wants a touchable event must implement this.
 *
 * @author nidebruyn
 */
public interface Touchable {

    public void fireTouchDown(float x, float y, float tpf);

    public void fireTouchUp(float x, float y, float tpf);

    public void fireTouchMove(float x, float y, float tpf);

    public void fireTouchCancel(float x, float y, float tpf);

    public void fireHoverOver(float x, float y, float tpf);
    
    public void fireHoverOff(float x, float y, float tpf);

    public boolean isTouched();

    public boolean isHovered();

}
