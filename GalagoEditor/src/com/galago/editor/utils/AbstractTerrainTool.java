package com.galago.editor.utils;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;

/**
 *
 * @author nicki
 */
public abstract class AbstractTerrainTool {

    private Terrain terrain;
    private Node terrainNode;

    protected Terrain getTerrain(Spatial root) {

        if (terrain != null) {
            return terrain;
        }

        // is this the terrain?
        if (root instanceof Terrain && root instanceof Node) {
            terrain = (Terrain) root;
            terrainNode = (Node) root;
            return terrain;
        }

        if (root instanceof Node) {
            Node n = (Node) root;
            for (Spatial c : n.getChildren()) {
                if (c instanceof Node) {
                    Terrain res = getTerrain(c);
                    if (res != null) {
                        return res;
                    }
                }
            }
        }

        return null;
    }

    protected Node getTerrainNode(Spatial root) {

        if (terrainNode != null) {
            return terrainNode;
        }

        // is this the terrain?
        if (root instanceof Terrain && root instanceof Node) {
            terrainNode = (Node) root;
            terrain = (Terrain) root;
            return terrainNode;
        }

        if (root instanceof Node) {
            Node n = (Node) root;
            for (Spatial c : n.getChildren()) {
                if (c instanceof Node) {
                    Node res = getTerrainNode(c);
                    if (res != null) {
                        return res;
                    }
                }
            }
        }

        return null;
    }

}
