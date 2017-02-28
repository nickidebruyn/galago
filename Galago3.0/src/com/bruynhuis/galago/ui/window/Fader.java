/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.window;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.listener.FadeListener;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * The Fader widget.
 * This widget will be shown between screens for a few seconds.
 * This is only for INTERNAL use.
 * 
 * @author NideBruyn
 */
public class Fader extends ImageWidget {

    private static String fadeImage = "Resources/fade.png";
    protected ColorRGBA colorRGBA = ColorRGBA.Black;
    protected FadeController fadeControl;
    protected boolean fadeOut = false;
    protected boolean fadeIn = false;
    protected float inspeed = 1;
    protected float outspeed = 1;
    protected float inwait = 10f;
    protected float outwait = 10f;
    protected FadeListener fadeListener;
    protected float waitCounter = 0f;

    /**
     * 
     * @param window
     * @param colorRGBA
     * @param inspeed
     * @param outspeed
     * @param inwait
     * @param outwait 
     */
    public Fader(Window window, ColorRGBA colorRGBA, float inspeed, float outspeed, float inwait, float outwait) {
        super(window, null, fadeImage, window.getWidth(), window.getHeight(), false);
        this.colorRGBA = colorRGBA;
        this.inspeed = inspeed;
        this.outspeed = outspeed;
        this.inwait = inwait;
        this.outwait = outwait;

        picture.getMaterial().setColor("Color", colorRGBA);

        fadeControl = new FadeController();
        fadeControl.setEnabled(false);
        picture.addControl(fadeControl);

        center();
        setDepthPosition(1f);

    }

    public void addFadeListener(FadeListener fadeListener) {
        this.fadeListener = fadeListener;

    }

    protected void fireFadeListener(boolean fadeOut) {
        setVisible(false);
        if (fadeListener != null) {
            fadeListener.fadeDone(fadeOut);

        }
    }

    /**
     * Fade out of the screen. When finished you must handle the movement
     */
    public void fadeOut() {
        setVisible(true);
        fadeOut = true;
        fadeIn = false;
        colorRGBA.set(colorRGBA.r, colorRGBA.g, colorRGBA.b, 0f);
        fadeControl.setEnabled(true);
        waitCounter = 0;
    }

    /**
     * Fade into the screen. When finished you must handle the gui enabling
     */
    public void fadeIn() {
        setVisible(true);
        fadeIn = true;
        fadeOut = false;
        colorRGBA.set(colorRGBA.r, colorRGBA.g, colorRGBA.b, 1);
        fadeControl.setEnabled(true);
        waitCounter = 0;

    }

    public boolean isRunning() {
        return fadeIn || fadeOut;
    }

    @Override
    protected boolean isBatched() {
        return false;
    }

    protected class FadeController extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {

            if (fadeOut) {
//                System.out.println("Fading out = " + colorRGBA.getAlpha());
                if (waitCounter < outwait) {
                    waitCounter += (tpf * 100f);
                    
                } else {
                    if (colorRGBA.getAlpha() <= 1f) {
                        colorRGBA.set(colorRGBA.r, colorRGBA.g, colorRGBA.b, colorRGBA.getAlpha() + (tpf * outspeed));

                    } else {
                        fadeOut = false;
                        fadeIn = false;
                        setEnabled(false);
                        fireFadeListener(true);

                    }
                }

            }

            if (fadeIn) {
//                System.out.println("Fading in = " + colorRGBA.getAlpha());
                if (colorRGBA.getAlpha() >= 0f) {
                    colorRGBA.set(colorRGBA.r, colorRGBA.g, colorRGBA.b, colorRGBA.getAlpha() - (tpf * inspeed));

                } else {
                    if (waitCounter >= inwait) {
                        fadeOut = false;
                        fadeIn = false;
                        setEnabled(false);
                        fireFadeListener(false);
                    }
                    waitCounter += (tpf * 100f);
                }

            }

        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }
    }

    public void setInWait(float inwait) {
        this.inwait = inwait;
    }

    public void setOutWait(float outwait) {
        this.outwait = outwait;
    }
}
