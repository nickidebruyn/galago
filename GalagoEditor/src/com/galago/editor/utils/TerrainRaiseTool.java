package com.galago.editor.utils;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ndebruyn
 */
public class TerrainRaiseTool extends AbstractTerrainTool {

    public static enum Meshes {
        Box, Sphere
    }

    public void modifyHeight(TerrainQuad terrain, Vector3f worldLoc, float radius, float heightDir, Meshes mesh) {

        int radiusStepsX = (int) (radius / ((Node) terrain).getWorldScale().x);
        int radiusStepsZ = (int) (radius / ((Node) terrain).getWorldScale().z);

        float xStepAmount = ((Node) terrain).getWorldScale().x;
        float zStepAmount = ((Node) terrain).getWorldScale().z;

        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();

        for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
            for (int x = -radiusStepsX; x < radiusStepsX; x++) {

                float locX = worldLoc.x + (x * xStepAmount);
                float locZ = worldLoc.z + (z * zStepAmount);

                // see if it is in the radius of the tool
                if (TerrainUtils.isInMesh(locX - worldLoc.x, locZ - worldLoc.z, radius, mesh)) {
                    // adjust height based on radius of the tool
                    float h = TerrainUtils.calculateHeight(radius, heightDir, locX - worldLoc.x, locZ - worldLoc.z);
                    // increase the height
                    locs.add(new Vector2f(locX, locZ));
                    heights.add(h);
                }
            }
        }

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);

        ((Node) terrain).updateModelBound(); // or else we won't collide with it where we just edited

        TerrainUtils.updateVegetationBatches(terrain, worldLoc, radius);
    }

}
