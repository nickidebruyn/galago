package com.galago.editor.utils;

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
public class TerrainSmoothTool {

    public void modifyHeight(Terrain terrain, Vector3f worldLoc, float radius, float height, TerrainRaiseTool.Meshes mesh) {

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
                    float center = terrain.getHeightmapHeight(terrainLoc);
                    float left = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x - 1, terrainLoc.y));
                    float right = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x + 1, terrainLoc.y));
                    float up = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x, terrainLoc.y + 1));
                    float down = terrain.getHeightmapHeight(new Vector2f(terrainLoc.x, terrainLoc.y - 1));
                    int count = 1;
                    float amount = center;
                    if (!isNaN(left)) {
                        amount += left;
                        count++;
                    }
                    if (!isNaN(right)) {
                        amount += right;
                        count++;
                    }
                    if (!isNaN(up)) {
                        amount += up;
                        count++;
                    }
                    if (!isNaN(down)) {
                        amount += down;
                        count++;
                    }

                    amount /= count; // take average

                    // weigh it
                    float diff = amount - center;
                    diff *= height;

                    locs.add(terrainLoc);
                    heights.add(diff);
                }
            }
        }

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);

        ((Node) terrain).updateModelBound(); // or else we won't collide with it where we just edited
    }

    private boolean isNaN(float val) {
        return val != val;
    }

    private void resetHeight(Terrain terrain, List<Vector2f> undoLocs, List<Float> undoHeights) {
        List<Float> neg = new ArrayList<Float>();
        for (Float f : undoHeights) {
            neg.add(f * -1f);
        }

        terrain.adjustHeight(undoLocs, neg);
        ((Node) terrain).updateModelBound();
    }

}
