/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.screens;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.platform2d.Platform2DEditor;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.geometryrunner.game.Game;

/**
 *
 * @author nidebruyn
 */
public class EditScreen extends Platform2DEditor {

    @Override
    protected String getEditFile() {
        return "geometryrun-edit.map";
    }

    @Override
    protected void doTestAction() {
        PlayScreen playScreen = (PlayScreen) baseApplication.getScreenManager().getScreen("play");
        playScreen.setTest(true);
        playScreen.setEditFile(getEditFile());
        showScreen("play");
    }

    @Override
    protected Platform2DGame initGame() {
        return new Game((Base2DApplication) baseApplication, rootNode);

    }

    @Override
    protected void show() {
        setPreviousScreen("menu");
    }

    @Override
    protected void pause() {
    }

    @Override
    protected void doClearAction() {
//        int index = FastMath.nextRandomInt(58, 62);
//        String item = "vegetation," + index;
//        for (int c = 0; c < 40; c++) {
//            for (int r = 0; r < 20; r++) {
//                Vector3f pos = new Vector3f(c, r, 0);
//                Sprite sprite = game.getItem(item);
//                Tile tile = new Tile(pos.x, pos.y, sprite.getWorldTranslation().z, item);
//                tile.setSpatial(sprite);
//                game.addTile(tile);
//            }
//        }

    }
    
    @Override
    public void update(float tpf) {
        /**
         * We override the update loop so that we can move the camera position.
         */
        if (isActive()) {
            camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(targetLookAtPoint.x).setY(PlayScreen.camHeight), 0.1f));

        }
    }
}
