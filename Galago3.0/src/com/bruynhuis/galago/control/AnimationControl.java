/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.bruynhuis.galago.listener.AnimationListener;

/**
 *
 * @author NideBruyn
 */
public class AnimationControl extends AbstractControl implements AnimEventListener {

    protected String ANIMATION = "";
    protected AnimChannel channel;
    protected AnimControl control;
    protected float speed = 1.0f;
    protected AnimationListener animationListener;

    public AnimationControl() {
    }
    
    public void addAnimationListener(AnimationListener animationListener1) {
        this.animationListener = animationListener1;
        
    }
    
    public void fireAnimationDoneListener(String animation) {
        if (this.animationListener != null) {
            animationListener.doAnimationDone(animation);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (control == null) {
            //Load the animation control
            control = spatial.getControl(AnimControl.class);
        }

        if (control != null && channel == null) {
            control.addListener(this);
            channel = control.createChannel();
            channel.setSpeed(speed);
            channel.setLoopMode(LoopMode.DontLoop);
            control.setEnabled(false);
        }

    }

    /**
     * Helper method for playing an animation.
     *
     * @param animation
     * @param loop
     * @param rewind
     * @param speed
     */
    public boolean play(String animation, boolean loop, boolean rewind, float speed) {
        
        //First we must get out if that animation is already playing
        if (channel != null && channel.getAnimationName() != null && channel.getAnimationName().equals(animation) && loop) {
            return false;
        }
        
        //Now we can move on with playing the animation
        if (control != null && channel != null) {
            
            if (control.getAnim(animation) == null) {
                return false;
            }
            
            ANIMATION = animation;
            channel.setAnim(animation);
            channel.setSpeed(speed);
            if (loop) {
                channel.setLoopMode(LoopMode.Loop);
            } else {
                channel.setLoopMode(LoopMode.DontLoop);
            }
//        channel.reset(rewind);
            control.setEnabled(true);
            return true;

        }
        
        return false;

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        fireAnimationDoneListener(animName);
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Control cloneForSpatial(Spatial spatial) {
        AnimationControl control = new AnimationControl();
        //TODO: copy parameters to new Control
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }

    public AnimChannel getChannel() {
        return channel;
    }

    public AnimControl getControl() {
        return control;
    }
}
