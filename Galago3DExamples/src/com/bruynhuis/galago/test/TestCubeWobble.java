/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test;

import com.bruynhuis.galago.control.ScaleUpDownControl;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author nidebruyn
 */
public class TestCubeWobble extends SimpleApplication {

    private int count;
    protected Geometry player;

    public static void main(String[] args) {
        TestCubeWobble test = new TestCubeWobble();
        test.start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        player = new Geometry("blue cube", b);

        count = 0;
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);

        player.addControl(new ScaleUpDownControl(1.1f, 1));
    }

}
