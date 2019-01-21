/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.bruynhuis.galago.ui.effect.Effect;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A Widget is the most abstract type class for the gui engine.
 *
 * @author nidebruyn
 */
public abstract class Widget implements Savable {

    protected boolean animated = false;
    protected Window window;
    protected Widget parent;
    protected AppSettings appSettings;
    protected float width;
    protected float height;
    protected Node widgetNode;
    protected ArrayList<Effect> effects = new ArrayList<Effect>();
    protected boolean lockScaling = false;

    /**
     *
     * @param window
     * @param parent
     * @param width
     * @param height
     * @param lockscaling
     */
    public Widget(Window window, Widget parent, float width, float height, boolean lockscaling) {
        this.window = window;
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.lockScaling = lockscaling;

        if (lockscaling) {
            this.width = width * window.getScaleFactorHeight();
        } else {
            this.width = width * window.getScaleFactorWidth();
        }

        this.height = height * window.getScaleFactorHeight();

        this.appSettings = window.getAppSettings();

        if (isBatched()) {
            widgetNode = new BatchNode("WidgetNode");
        } else {
            widgetNode = new Node("WidgetNode");
        }

    }

    protected abstract boolean isBatched();

    public boolean isAnimated() {
        return animated;
    }

    /**
     *
     * @param animated
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setName(String name) {
        widgetNode.setName(name);
    }

    public String getName() {
        return widgetNode.getName();
    }

    public Widget getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Window getWindow() {
        return window;
    }

    public void add(Node parent) {
        parent.attachChild(widgetNode);
    }

    public void remove() {
        widgetNode.removeFromParent();
    }

    public Node getWidgetNode() {
        return widgetNode;
    }

    /**
     * Call this method only when you want to show a widget with animations
     */
    public void show() {
        if (!isVisible()) {
            this.setVisible(true);
            if (animated) {
                for (Effect effect : effects) {
                    effect.fireShow();
                }
            }
        }

    }

    /**
     * This method must only be called if a widget must be shown with effcts.
     */
    public void hide() {
        if (isVisible()) {
            if (animated) {
                for (Effect effect : effects) {
                    effect.fireHide();
                }
            } else {
                this.setVisible(false);
            }
        }

    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (visible && widgetNode.getCullHint().equals(Spatial.CullHint.Always)) {
            widgetNode.setCullHint(Spatial.CullHint.Never);

        } else if (!visible && widgetNode.getCullHint().equals(Spatial.CullHint.Never)) {
            widgetNode.setCullHint(Spatial.CullHint.Always);

        }
    }

    public boolean isVisible() {
        return widgetNode.getCullHint().equals(Spatial.CullHint.Never);
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        widgetNode.setLocalTranslation(x, y, widgetNode.getLocalTranslation().z);
    }

    /**
     *
     * @param z
     */
    public void setDepthPosition(float z) {
        widgetNode.setLocalTranslation(widgetNode.getLocalTranslation().x, widgetNode.getLocalTranslation().y, z);
    }

    public Vector3f getPosition() {
        return widgetNode.getLocalTranslation();
    }

    public Vector3f getScreenPosition() {
//        return new Vector3f(-(window.getWidth()*0.5f*window.getScaleFactorWidth()) + getPosition().x, width, getPosition().z);
        return new Vector3f(getPosition().x + (window.getWidth() * 0.5f * window.getScaleFactorWidth()), getPosition().y + (window.getHeight() * 0.5f * window.getScaleFactorHeight()), getPosition().z);
    }

    /**
     *
     * @param x
     * @param y
     */
    public void move(float x, float y) {
        widgetNode.setLocalTranslation(getPosition().x + x, getPosition().y + y, widgetNode.getLocalTranslation().z);

    }

    protected float getParentWidth() {
        if (hasParent()) {
            return parent.getWidth();
        } else {
            return window.getWidth() * window.getScaleFactorWidth();
        }
    }

    protected float getParentHeight() {
        if (hasParent()) {
            return parent.getHeight();
        } else {
            return window.getHeight() * window.getScaleFactorHeight();
        }
    }

    /**
     * Center the widget to the screen.
     */
    public void center() {
        float xPos = 0;
        float yPos = 0;
        setPosition(xPos, yPos);
    }

    /**
     * Center the widget to the Top of the screen.
     *
     * @param offsetX
     * @param offsetY
     */
    public void centerTop(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = offsetX * window.getScaleFactorHeight();
            float yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        } else {
            float xPos = offsetX * window.getScaleFactorWidth();
            float yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void centerBottom(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = offsetX * window.getScaleFactorHeight();
            float yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        } else {
            float xPos = offsetX * window.getScaleFactorWidth();
            float yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void rightBottom(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * window.getScaleFactorHeight());
            float yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        } else {
            float xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * window.getScaleFactorWidth());
            float yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void rightTop(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * window.getScaleFactorHeight());
            float yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        } else {
            float xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * window.getScaleFactorWidth());
            float yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void rightCenter(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * window.getScaleFactorHeight());
            float yPos = (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        } else {
            float xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * window.getScaleFactorWidth());
            float yPos = (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void leftCenter(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * window.getScaleFactorHeight();
            float yPos = (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);

        } else {
            float xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * window.getScaleFactorWidth();
            float yPos = (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);

        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void leftBottom(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * window.getScaleFactorHeight();
            float yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);

        } else {
            float xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * window.getScaleFactorWidth();
            float yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void leftTop(float offsetX, float offsetY) {
        if (hasParent() && getParent().isLockScaling()) {
            float xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * window.getScaleFactorHeight();
            float yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        } else {
            float xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * window.getScaleFactorWidth();
            float yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    /**
     *
     * @param offsetX
     * @param offsetY
     */
    public void centerAt(float offsetX, float offsetY) {

        if (hasParent() && getParent().isLockScaling()) {
            float xPos = (offsetX * window.getScaleFactorHeight());
            float yPos = (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);

        } else {
            float xPos = (offsetX * window.getScaleFactorWidth());
            float yPos = (offsetY * window.getScaleFactorHeight());
            setPosition(xPos, yPos);
        }

    }

    public float getWidth() {
        return width;

    }

    public boolean isLockScaling() {
        return lockScaling;
    }

    public float getHeight() {
        return height;

    }

    /**
     *
     * @param scale
     */
    public void scale(float scale) {
        setScale(getScale() + scale);
    }

    /**
     *
     * @param scale
     */
    public void setScale(float scale) {
        widgetNode.setLocalScale(scale);
    }

    public float getScale() {
        return widgetNode.getLocalScale().x;
    }

    public Vector3f getScales() {
        return widgetNode.getLocalScale();
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setScales(float x, float y) {
        widgetNode.setLocalScale(new Vector3f(x, y, 1));
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void rotate(float x, float y, float z) {
        widgetNode.rotate(x, y, z);
    }

    /**
     *
     * @param rotationAmountZ
     */
    public void rotate(float rotationAmountZ) {
        widgetNode.rotate(0, 0, rotationAmountZ);
    }

    /**
     * Rotation in degrees
     *
     * @return
     */
    public float getRotation() {
        return widgetNode.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG;
    }

    /**
     * Rotation in degrees
     *
     * @return
     */
    public float getRotationY() {
        return widgetNode.getLocalRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG;
    }

    /**
     * Sets the rotation of a widget in degrees
     *
     * @param angle
     */
    public void setRotation(float angle) {
        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        widgetNode.setLocalRotation(q);
    }

    /**
     * Sets the rotation of a widget in degrees
     *
     * @param angle
     */
    public void setRotationY(float angle) {
        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
        widgetNode.setLocalRotation(q);
    }

    /**
     *
     * @param effect
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
        widgetNode.addControl(effect);
    }

    /**
     *
     * @param effect
     */
    public void removeEffect(Effect effect) {
        effects.remove(effect);
        widgetNode.removeControl(effect);
    }

    /**
     *
     * @param alpha
     */
    public abstract void setTransparency(float alpha);

    /**
     *
     * @return
     */
    public abstract float getTransparency();

    @Override
    public void write(JmeExporter ex) throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(JmeImporter im) throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void fadeFromTo(float from, float to, float duration, float delay) {
        setTransparency(from);
        Tween.to(this, WidgetAccessor.OPACITY, duration)
                .target(to)
                .delay(delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public void fadeFromTo(float from, float to, float duration, float delay, TweenEquation tweenEquation, int count, boolean yoyo) {
        setTransparency(from);

        if (yoyo) {
            Tween.to(this, WidgetAccessor.OPACITY, duration)
                    .target(to)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(count, 0)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        } else {
            Tween.to(this, WidgetAccessor.OPACITY, duration)
                    .target(to)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeat(count, 0)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    public void moveFromToCenter(float fromX, float fromY, float toX, float toY, float duration, float delay) {
        centerAt(toX, toY);
        Vector3f target = getPosition().clone();
        centerAt(fromX, fromY);

        Tween.to(this, WidgetAccessor.POS_XY, duration)
                .target(target.x, target.y)
                .delay(delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public void moveFromToCenter(float fromX, float fromY, float toX, float toY, float duration, float delay, TweenCallback callback) {
        centerAt(toX, toY);
        Vector3f target = getPosition().clone();
        centerAt(fromX, fromY);

        Tween.to(this, WidgetAccessor.POS_XY, duration)
                .target(target.x, target.y)
                .delay(delay)
                .setCallback(callback)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public void moveFromToCenter(float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation, int count, boolean yoyo) {
        centerAt(toX, toY);
        Vector3f target = getPosition().clone();
        centerAt(fromX, fromY);

        if (yoyo) {
            Tween.to(this, WidgetAccessor.POS_XY, duration)
                    .target(target.x, target.y)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(count, 0)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        } else {
            Tween.to(this, WidgetAccessor.POS_XY, duration)
                    .target(target.x, target.y)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeat(count, 0)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    public void scaleFromTo(float fromX, float fromY, float toX, float toY, float duration, float delay) {
        setScales(fromX, fromY);

        Tween.to(this, WidgetAccessor.SCALE_XY, duration)
                .target(toX, toY)
                .delay(delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public void scaleFromTo(float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation) {
        setScales(fromX, fromY);

        Tween.to(this, WidgetAccessor.SCALE_XY, duration)
                .target(toX, toY)
                .delay(delay)
                .ease(tweenEquation)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
    }
    
    public void rotateFromTo(float fromAngle, float toAngle, float duration, float delay) {
        setRotation(fromAngle);

        Tween.to(this, WidgetAccessor.ROTATION, duration)
                .target(toAngle)
                .delay(delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public void rotateFromTo(float fromAngle, float toAngle, float duration, float delay, TweenEquation tweenEquation) {
        setRotation(fromAngle);

        Tween.to(this, WidgetAccessor.ROTATION, duration)
                .target(toAngle)
                .delay(delay)
                .ease(tweenEquation)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
    }
    
    public void rotateFromTo(float fromAngle, float toAngle, float duration, float delay, TweenEquation tweenEquation, TweenCallback callback) {
        setRotation(fromAngle);

        Tween.to(this, WidgetAccessor.ROTATION, duration)
                .target(toAngle)
                .delay(delay)
                .ease(tweenEquation)
                .setCallback(callback)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
    }
}
