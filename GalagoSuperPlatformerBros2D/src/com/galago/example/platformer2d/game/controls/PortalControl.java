/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.controls;

import com.bruynhuis.galago.games.platform2d.Tile;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.game.Player;

/**
 *
 * @author NideBruyn
 */
public class PortalControl extends AbstractControl {

    private Game game;

    public PortalControl(Game game) {
        this.game = game;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {
            //TODO
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void doHit() {
        game.getBaseApplication().getSoundManager().playSound("portal");
        String targetName = spatial.getUserData("target");
        if (targetName != null) {
            for (int i = 0; i < game.getTileMap().getTiles().size(); i++) {
                Tile tile = (Tile) game.getTileMap().getTiles().get(i);
                if (tile.getSpatial().getUserData("type") != null && 
                        tile.getSpatial().getUserData("type").equals(targetName)) {
                    ((Player) game.getPlayer()).transportToPosition(tile.getSpatial().getWorldTranslation());
                    break;
                }
            }
        }
    }
}
