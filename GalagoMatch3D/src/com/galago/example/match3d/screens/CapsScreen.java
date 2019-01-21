/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.field.TextArea;
import com.jme3.renderer.Caps;
import java.util.EnumSet;
import java.util.Iterator;

/**
 *
 * @author NideBruyn
 */
public class CapsScreen extends AbstractScreen {
    
    private TextArea textArea;

    @Override
    protected void init() {
        
        textArea = new TextArea(hudPanel, 420, 800);
        textArea.center();

    }

    @Override
    protected void load() {

        EnumSet<Caps> caps = baseApplication.getRenderer().getCaps();
        if (caps != null) {
            for (Iterator<Caps> iterator = caps.iterator(); iterator.hasNext();) {
                Caps cap = iterator.next();
                textArea.addText(cap.name() + "\n");
            }

        }
        
    }

    @Override
    protected void show() {

    }

    @Override
    protected void exit() {

    }

    @Override
    protected void pause() {

    }
    
    
}
