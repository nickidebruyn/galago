/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper2d.game;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.Debug;
import static com.galago.example.hyper2d.game.Game.MAX_HEALTH;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class ObstacleControl extends AbstractControl {

    private Game game;
    private int health;
    private BitmapText text;
    private RigidBodyControl rbc;
    private Sprite sprite;
    private boolean takeDamage = false;

    public ObstacleControl(Game game, BitmapText text, int health) {
        this.game = game;
        this.text = text;
        this.health = health;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

            if (rbc == null) {
                rbc = spatial.getControl(RigidBodyControl.class);
                sprite = (Sprite) spatial;

                updateObstacle();

            } else if (takeDamage) {
                if (isAlive()) {
                    updateObstacle();

                } else {
                    destroyObstacle();

                }
                takeDamage = false;
            }

            if (rbc != null) {
                text.setLocalTranslation(-0.2f + rbc.getPhysicLocation().x, 0.3f + rbc.getPhysicLocation().y, text.getLocalTranslation().z);
                text.setLocalScale(sprite.getLocalScale().x);
            }
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    protected void updateObstacle() {
        float scale = 1f + ((float) health / (float) Game.MAX_HEALTH);
        sprite.setLocalScale(scale);

        float radius = sprite.getWidth() * scale * 0.5f;
        rbc.setCollisionShape(new CircleCollisionShape(radius));

        ColorRGBA color = ColorUtils.hsv(0.1f + (float) health / MAX_HEALTH, 0.75f, .9f);
        sprite.getMaterial().setColor("Color", color);
        
        text.setText("" + health);
//        Debug.log("Update text: " + text.getText());

    }

    protected void destroyObstacle() {

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rbc);
        spatial.removeFromParent();
        text.removeFromParent();

    }

    public void doDamage(int damage) {

        health = health - damage;
        takeDamage = true;

//        Debug.log("Health = " + health);

    }

    public boolean isAlive() {
        return health > 0;
    }
}
