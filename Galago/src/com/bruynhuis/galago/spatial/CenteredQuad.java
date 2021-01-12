/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author NideBruyn
 */
public class CenteredQuad extends Mesh {

    public CenteredQuad(float width, float height) {
        super();

        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        Vector3f[] verts = { // bl, br, tl, tr
            new Vector3f(-halfWidth, -halfHeight, 0),
            new Vector3f(halfWidth, -halfHeight, 0),
            new Vector3f(-halfWidth, halfHeight, 0),
            new Vector3f(halfWidth, halfHeight, 0)
        };

        int[] indices = {
            0, 1, 2,
            2, 1, 3
        };

        FloatBuffer vb = BufferUtils.createFloatBuffer(verts);
        setBuffer(VertexBuffer.Type.Position, 3, vb);

        IntBuffer ib = BufferUtils.createIntBuffer(indices);
        setBuffer(VertexBuffer.Type.Index, 3, ib);

        updateBound();
    }

}
