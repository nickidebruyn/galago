/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

import java.util.HashMap;

/**
 *
 * @author nidebruyn
 */
public interface SelectionActionListener {
    
    /**
     * Send properties to the method and return a string value.
     * @param hashmap
     * @return 
     */
    public void doSelectionOption(HashMap<Integer, String> items);
    
}
