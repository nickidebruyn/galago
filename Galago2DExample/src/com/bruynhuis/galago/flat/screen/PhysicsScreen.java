/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

/**
 *
 * @author nidebruyn
 */
public class PhysicsScreen extends AbstractSpriteScreen {

    @Override
    protected void init() {
        super.init();        
        
    }

    @Override
    protected String getHeading() {
        return "Sprite Physics Test";
    }

    @Override
    protected String getInstructions() {
        return "Use the toolbar icons and buttons to add or remove object in the 2D environment.";
    }
    
}
