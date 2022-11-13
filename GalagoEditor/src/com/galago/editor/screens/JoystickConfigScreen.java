/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.galago.editor.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import java.io.File;
import markil3.controller.CalibrateInputScreen;

/**
 *
 * @author ndebruyn
 */
public class JoystickConfigScreen extends AbstractScreen {

    public static final String NAME = "JoystickConfigScreen";

    private CalibrateInputScreen calibrateInputScreen;

    @Override
    protected void init() {
        calibrateInputScreen = new CalibrateInputScreen(new File("input-mappings.properties"));
    }

    @Override
    protected void load() {
        baseApplication.getStateManager().attach(calibrateInputScreen);
    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {
        baseApplication.getStateManager().detach(calibrateInputScreen);
    }

    @Override
    protected void pause() {
    }

}
