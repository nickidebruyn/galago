package com.galago.editor.utils;

import com.galago.editor.utils.TerrainRaiseTool.Meshes;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ndebruyn
 */
public class TerrainFlattenTool {

    public void modifyHeight(Terrain terrain, Vector3f level, Vector3f worldLoc, float radius, float height, boolean precision, TerrainRaiseTool.Meshes mesh) {
        if (level == null) {
            return;
        }

        float desiredHeight = level.y;

        int radiusStepsX = (int) (radius / ((Node) terrain).getLocalScale().x);
        int radiusStepsZ = (int) (radius / ((Node) terrain).getLocalScale().z);

        float xStepAmount = ((Node) terrain).getLocalScale().x;
        float zStepAmount = ((Node) terrain).getLocalScale().z;

        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();

        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = worldLoc.x + (x * xStepAmount);
                float locZ = worldLoc.z + (z * zStepAmount);

                // see if it is in the radius of the tool
                if (TerrainUtils.isInMesh(locX - worldLoc.x, locZ - worldLoc.z, radius, mesh)) {

                    Vector2f terrainLoc = new Vector2f(locX, locZ);
                    // adjust height based on radius of the tool
                    float terrainHeightAtLoc = terrain.getHeightmapHeight(terrainLoc) * ((Node) terrain).getWorldScale().y;
                    if (precision) {
                        locs.add(terrainLoc);
                        heights.add(desiredHeight / ((Node) terrain).getLocalScale().y);
//                        undoHeights.add(terrainHeightAtLoc / ((Node) terrain).getLocalScale().y);
                    } else {
                        float epsilon = 0.1f * height; // rounding error for snapping

                        float adj = 0;
                        if (terrainHeightAtLoc < desiredHeight) {
                            adj = 1;
                        } else if (terrainHeightAtLoc > desiredHeight) {
                            adj = -1;
                        }

                        adj *= height;

                        if (mesh.equals(Meshes.Sphere)) {
                            adj *= TerrainUtils.calculateRadiusPercent(radius, locX - worldLoc.x, locZ - worldLoc.z);
                        }

                        // test if adjusting too far and then cap it
                        if (adj > 0 && TerrainUtils.floatGreaterThan((terrainHeightAtLoc + adj), desiredHeight, epsilon)) {
                            adj = desiredHeight - terrainHeightAtLoc;
                        } else if (adj < 0 && TerrainUtils.floatLessThan((terrainHeightAtLoc + adj), desiredHeight, epsilon)) {
                            adj = terrainHeightAtLoc - desiredHeight;
                        }

                        if (!TerrainUtils.floatEquals(adj, 0, 0.001f)) {
                            locs.add(terrainLoc);
                            heights.add(adj);
                        }

                    }
                }
            }
        }

        // do the actual height adjustment
        if (precision) {
            terrain.setHeight(locs, heights);
        } else {
            terrain.adjustHeight(locs, heights);
        }

        ((Node) terrain).updateModelBound(); // or else we won't collide with it where we just edited

    }

}
