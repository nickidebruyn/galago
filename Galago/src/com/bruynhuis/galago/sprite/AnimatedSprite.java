/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite;

import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.util.Timer;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author nidebruyn
 */
public class AnimatedSprite extends Sprite implements Savable {

    private int currentFrame = 0;
    protected boolean loop = false;
    protected boolean reverse = false;
    protected boolean cycle = true;
    private Timer frameTimer = new Timer(50);
    private HashMap<String, Animation> animationMap = new HashMap<String, Animation>();
    protected Animation currentAnimation;
    private String DEFAULT_ANIMATION = "default";
    private float defaultSpeed;
    private ArrayList<AnimationListener> animationListeners = new ArrayList<AnimationListener>();

    public AnimatedSprite() {
    }
    
    public AnimatedSprite(String name, float width, float height, int columns, int rows, float speed, boolean loop, boolean reverse, boolean cycle) {        
        this(name, width, height, columns, rows, speed);
        this.loop = loop;
        this.reverse = reverse;
        this.cycle = cycle;
    }

    public AnimatedSprite(String name, float width, float height, int columns, int rows, float speed) {
        super(name, width, height, rows, columns, 0, 0);
        this.defaultSpeed = speed;
        initialize();
        addAnimation(new Animation(DEFAULT_ANIMATION, 0, (rows * columns) - 1, speed));
    }
    
    public void addAnimationListener(AnimationListener animationListener) {
        this.animationListeners.add(animationListener);
    }
    
    public void removeAnimationListener(AnimationListener animationListener) {
        this.animationListeners.add(animationListener);
    }
    
    public void fireAnimationStarted() {
        for (int i = 0; i < animationListeners.size(); i++) {
            AnimationListener animationListener = animationListeners.get(i);
            animationListener.animationStart(currentAnimation);
        }
    }
    
    public void fireAnimationDone() {
        for (int i = 0; i < animationListeners.size(); i++) {
            AnimationListener animationListener = animationListeners.get(i);
            animationListener.animationDone(currentAnimation);
        }
    }

    public void addAnimation(Animation animation) {
        animationMap.put(animation.getName(), animation);
    }

    public void removeAnimation(Animation animation) {
        animationMap.remove(animation.getName());
    }

    private AbstractControl createControl() {
        return new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isEnabled()) {
                    frameTimer.update(tpf);
                    if (frameTimer.finished()) {

                        if (AnimatedSprite.this.reverse) {
                            currentFrame--;
                            if (currentFrame < currentAnimation.getStartIndex()) {
                                fireAnimationDone();
                                
                                if (cycle) {
                                    currentFrame = currentAnimation.getEndIndex();
                                } else {
                                    reverse = false;
                                    currentFrame = currentAnimation.getStartIndex();
                                }                                
                                
                                if (!AnimatedSprite.this.loop) {
                                    stop();
                                    return;
                                }

                                fireAnimationStarted();
                            }

                        } else {
                            currentFrame++;
                            if (currentFrame > currentAnimation.getEndIndex()) {
                                fireAnimationDone();
                                
                                if (cycle) {
                                    currentFrame = currentAnimation.getStartIndex();
                                } else {
                                    reverse = true;
                                    currentFrame = currentAnimation.getEndIndex();
                                }                                
                                
                                
                                if (!AnimatedSprite.this.loop) {
                                    stop();
                                    return;
                                }

                                fireAnimationStarted();
                            }

                        }

                        setCullHint(Spatial.CullHint.Never);
                        showIndex(currentFrame % AnimatedSprite.this.columns, currentFrame / AnimatedSprite.this.columns);
                        frameTimer.reset();
                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        };

    }

    private void initialize() {
        addControl(createControl());
        setCullHint(Spatial.CullHint.Always);
    }

    /**
     * This method will play the sequence of frames from start frame to end
     * frame
     *
     * @param startFrame
     * @param endFrame
     */
    public void play(String animation, boolean loop, boolean reverse, boolean cycle) {
        this.loop = loop;
        this.reverse = reverse;
        this.cycle = cycle;
        
        
        if (currentAnimation != null && currentAnimation.getName().equals(animation)) {
            return;
        }

        currentAnimation = animationMap.get(animation);
        if (reverse) {
            this.currentFrame = currentAnimation.getEndIndex();
        } else {
            this.currentFrame = currentAnimation.getStartIndex();
        }

        setCullHint(Spatial.CullHint.Never);
        frameTimer.setMaxTime(currentAnimation.getSpeed());
        
        if (!loop && !reverse && !cycle) {
            frameTimer.stop();
        } else {
            frameTimer.reset();
        }        
        
        showIndex(currentFrame % AnimatedSprite.this.columns, currentFrame / AnimatedSprite.this.columns);
        fireAnimationStarted();
    }

    /**
     * This method will play the default animation. It will not loop and it will
     * not be in reverse
     */
    public void play() {
        play(DEFAULT_ANIMATION, loop, reverse, cycle);

    }

    public void stop() {
        frameTimer.stop();
//        setCullHint(Spatial.CullHint.Always);
    }

    public void pause() {
        frameTimer.pause(true);

    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e); //To change body of generated methods, choose Tools | Templates.
        OutputCapsule capsule = e.getCapsule(this);

    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im); //To change body of generated methods, choose Tools | Templates.
        InputCapsule capsule = im.getCapsule(this);

        this.baseApplication = SharedSystem.getInstance().getBaseApplication();

        initialize();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public Node clone(boolean cloneMaterials) {
//        Material mat = material.clone();
        AnimatedSprite clone = new AnimatedSprite(name, width, height, columns, rows, defaultSpeed);
        clone.setMaterial(material);

        for (Iterator<Animation> it = getAnimationMap().values().iterator(); it.hasNext();) {
            Animation animation = it.next();
            clone.addAnimation(animation.clone());
        }

        return clone;
    }

    public HashMap<String, Animation> getAnimationMap() {
        return animationMap;
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }
    
    
}
