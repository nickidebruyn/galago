/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 *
 * @author nidebruyn
 */
public class Disk extends Mesh {
    
    private int segments;
    private float radius;

    private short[] vertexIndices;
    private Vector3f[] vertexPositions;
    private Vector2f[] vertexTexCoordinates;

    private final Vector2f offsetVector = new Vector2f(0.5f, 0.5f);

    public Disk(int segments, float radius) {
        this.segments = segments;
        this.radius = radius;
        //1 for each segment + 1 for the center
        vertexPositions = new Vector3f[segments + 1];
        vertexTexCoordinates = new Vector2f[segments + 1];
        this.vertexIndices = new short[segments * 3]; //One triangle for each segment
        this.generateVertices();
        this.generateIndices();
        this.buildMesh();

    }

    private void buildMesh() {
        setMode(Mode.Triangles);
        setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer(vertexIndices));
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertexPositions));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(vertexTexCoordinates));
        setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(vertexPositions));
        updateCounts();
        updateBound();
    }

    private void generateIndices() {
        for (int i = 0; i < segments; i++) {
            vertexIndices[3 * i] = 0;
            vertexIndices[3 * i + 2] = (short)(i + 1);
            vertexIndices[3 * i + 1] = (short)(((i + 1) % segments) + 1);
        }
    }

    private void generateVertices() {
        vertexPositions[0] = new Vector3f(); //Center
        vertexTexCoordinates[0] = new Vector2f(0.5f, 0.5f);
        float step = (2 * FastMath.PI) / segments;
        for (int i = 0; i < segments; i++) {
            //Normalized Disk
            vertexPositions[i + 1] = new Vector3f((float) FastMath.sin(step * i), (float) FastMath.cos(step * i), 0f);
            //Tex Coords
            vertexTexCoordinates[i + 1] = new Vector2f(vertexPositions[i + 1].x, vertexPositions[i + 1].y);
            vertexTexCoordinates[i + 1].multLocal(0.5f).addLocal(offsetVector);
            //Add radius to the positions
            vertexPositions[i + 1].multLocal(radius);
        }
    }
}