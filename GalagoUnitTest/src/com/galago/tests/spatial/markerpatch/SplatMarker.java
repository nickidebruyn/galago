/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.spatial.markerpatch;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NideBruyn
 */
public class SplatMarker {

    public static Geometry createTargetMarker(Mesh mesh, Vector3f markerPosition, AssetManager assetManager, String texture) {
        Mesh markerMesh = new Mesh();
        float maxDistance = 1f;
        List<Vector3f> position = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < mesh.getTriangleCount(); i++) {
            Triangle tri = new Triangle();
            mesh.getTriangle(i, tri);
            boolean isClose = tri.get1().distance(markerPosition) < maxDistance
                    || tri.get2().distance(markerPosition) < maxDistance
                    || tri.get3().distance(markerPosition) < maxDistance;
            if (isClose) {
                for (int j = 0; j < 3; j++) {
                    Vector3f v = tri.get(j);
                    int index = 0;
                    if (position.contains(v)) {
                        index = position.indexOf(v);
                    } else {
                        index = position.size();
                        position.add(v);
                        normals.add(tri.getNormal());
                    }
                    indices.add(index);
                }
            }
        }

        Vector3f normal = normals.stream().reduce(new Vector3f(), (total, next) -> total.addLocal(next)).normalize();
        // Find a vector 90 degrees from both the normal and the world up vector
        Vector3f left = normal.cross(Vector3f.UNIT_Y);
        // Find a vector 90 degrees from both the normal and the left vector
        Vector3f up = normal.cross(left);
        float uMin = Float.MAX_VALUE, uMax = -Float.MAX_VALUE, vMin = Float.MAX_VALUE, vMax = -Float.MAX_VALUE;
        List<Vector2f> uvCoords = new ArrayList<>();
        for (Vector3f p : position) {
            float u = left.dot(p);
            float v = up.dot(p);
            uvCoords.add(new Vector2f(u, v));
            uMin = Math.min(uMin, u);
            uMax = Math.max(uMax, u);
            vMin = Math.min(vMin, v);
            vMax = Math.max(vMax, v);
        }
        // Normalize UV coords
        for (Vector2f textCoord : uvCoords) {
            float u = textCoord.x, v = textCoord.y;
            textCoord.x = (u - uMin) / (uMax - uMin);
            textCoord.y = (v - vMin) / (vMax - vMin);
        }

        markerMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(position.toArray(new Vector3f[]{})));
        markerMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals.toArray(new Vector3f[]{})));
        markerMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices.stream().mapToInt(Integer::intValue).toArray()));
        markerMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvCoords.toArray(new Vector2f[]{})));

        Geometry geo = new Geometry("Marker", markerMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture2D tex = (Texture2D) assetManager.loadTexture(texture);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setPolyOffset(-1f, -1f);
        geo.setQueueBucket(Bucket.Transparent);
        geo.setMaterial(mat);
        markerMesh.updateBound();
        return geo;
    }

}
