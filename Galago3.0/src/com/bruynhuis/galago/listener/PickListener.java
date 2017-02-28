/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.listener;

/**
 *
 * @author NideBruyn
 */
public interface PickListener {
    
    public void picked(PickEvent pickEvent, float tpf);
    
    public void drag(PickEvent pickEvent, float tpf);
    
}
