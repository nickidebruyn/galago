package com.galago.editor.utils;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.instancing.InstancedNode;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 * 2022-12-19: The terrain model paint tool will paint a specified grass geomertry to
 * the terrain batchnode for grass.
 *
 * @author ndebruyn
 */
public class TerrainModelTool {

    private Vector3f lastLocation = new Vector3f(0, 0, 0);
    private int count = 0;
    private CollisionResults results = new CollisionResults();
    private Ray ray = new Ray();

    public void paintModel(TerrainQuad terrain, InstancedNode instanceNode, Vector3f worldLoc, float radius, float density, float scale, Spatial model) {

        if (density > 0) {
            //Paint grass
            count = (int) (density * radius);

            if (lastLocation.distance(worldLoc) >= radius && count >= 1) {

                Vector2f posHor = new Vector2f(worldLoc.x, worldLoc.z);

                //Paint random grass
                for (int i = 0; i < count; i++) {

                    posHor.setX(worldLoc.x + FastMath.nextRandomInt(-(int) radius, (int) radius));
                    posHor.setY(worldLoc.z + FastMath.nextRandomInt(-(int) radius, (int) radius));
                    float height = terrain.getHeight(posHor);

                    Vector3f loc = new Vector3f(posHor.x, height, posHor.y);

                    results.clear();
                    ray.setOrigin(new Vector3f(loc.x, 300, loc.z));
                    ray.setDirection(new Vector3f(0, -1000, 0));

                    // 3. Collect intersections between Ray and Shootables in results list.
                    terrain.collideWith(ray, results);

                    // 5. Use the results (we mark the hit object)
                    if (results.size() > 0) {
                        CollisionResult cr = results.getClosestCollision();
                        if (cr.getContactNormal().y <= 1.0f
                                && cr.getContactNormal().y >= 0.9f) {

                            System.out.println("Painting tree: " + model.getName() + "; position: " + loc);

                            Spatial painted = model.clone(false);
                            painted.setLocalTranslation(loc);
                            painted.setLocalScale(scale + FastMath.nextRandomInt(-5, 5) * 0.1f);
                            MaterialUtils.setInstancingOnAllMaterials(painted);
                            instanceNode.attachChild(painted);

                        }
                    }
                }

                lastLocation.setX(worldLoc.x);
                lastLocation.setY(worldLoc.y);
                lastLocation.setZ(worldLoc.z);

                instanceNode.instance();
            }

        } else {
            //Remove mdoels
//            System.out.println("Removing grass");            
            instanceNode.depthFirstTraversal(new SceneGraphVisitorAdapter() {
                @Override
                public void visit(Geometry geom) {                    
                    //TODO: Need to find a solution to remove models
//                    if (!geom.getName().contains("batch") && worldLoc.distance(geom.getWorldTranslation()) < radius) {
////                        System.out.println("Removing: " + geom.getName());
//                        geom.removeFromParent();
//                    }

                }

            });
            
            instanceNode.instance();

        }

    }

}
