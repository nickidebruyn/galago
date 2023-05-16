/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.List;

/**
 * This is a road mesh. It can be used for creation of a road like surface.
 *
 * @author NideBruyn
 */
public class Road extends Mesh {

    private float width;
    private List<Spatial> controlPoints;

    private int[] vertexIndices;
    private Vector3f[] vertexPositions;
    private Vector3f[] normalPositions;
    private Vector2f[] vertexTexCoordinates;

    /**
     * Serialization only. Do not use.
     */
    public Road() {
    }

    /**
     * Create a quad with the given width and height. The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param vertices
     */
    public Road(float width, List<Spatial> points) {
        System.out.println("Test");

        this.width = width;
        this.controlPoints = points;
        this.generateVertices();
        this.generateIndices();
        this.buildMesh();

    }

    private void buildMesh() {
        setMode(Mode.Triangles);
        setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(vertexIndices));
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertexPositions));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(vertexTexCoordinates));
        setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normalPositions));
        updateCounts();
        updateBound();
        setStatic();
    }

    private void generateIndices() {
        // Set the index buffer for the mesh (each pair of vertices defines a quad)
        vertexIndices = new int[(controlPoints.size() - 1) * 6];
        for (int i = 0; i < controlPoints.size() - 1; i++) {
            vertexIndices[i * 6] = i * 2;
            vertexIndices[i * 6 + 1] = i * 2 + 1;
            vertexIndices[i * 6 + 2] = i * 2 + 2;
            vertexIndices[i * 6 + 3] = i * 2 + 1;
            vertexIndices[i * 6 + 4] = i * 2 + 3;
            vertexIndices[i * 6 + 5] = i * 2 + 2;
        }

    }

    private void generateVertices() {

        this.vertexPositions = new Vector3f[controlPoints.size() * 2];
        this.normalPositions = new Vector3f[controlPoints.size() * 2];
        this.vertexTexCoordinates = new Vector2f[controlPoints.size() * 2];

        Spatial current = null;
        Vector3f left = null;
        Vector3f right = null;

        for (int i = 0; i < controlPoints.size(); i++) {
            // Calculate the position of the point along the road            
            current = controlPoints.get(i);
            width = current.getLocalScale().x;
            
            left = new Vector3f(-width, 0, 0);
            left = current.getWorldRotation().mult(left);            
            left = current.getWorldTranslation().add(left);
            
            right = new Vector3f(width, 0, 0);
            right = current.getWorldRotation().mult(right);
            right = current.getWorldTranslation().add(right);

            vertexPositions[i * 2] = new Vector3f(left.x,left.y, left.z);
            vertexPositions[i * 2 + 1] = new Vector3f(right.x, right.y, right.z);

//            // Calculate the UV coordinates for the left and right vertices
            if (i % 2 == 0) {
                System.out.println("mod: " + i);
                vertexTexCoordinates[i * 2] = new Vector2f(0, 0);
                vertexTexCoordinates[i * 2 + 1] = new Vector2f(1, 0);

            } else {
                System.out.println("not: " + i);
                vertexTexCoordinates[i * 2] = new Vector2f(0, 1);
                vertexTexCoordinates[i * 2 + 1] = new Vector2f(1, 1);                

            }
            
            normalPositions[i * 2] = new Vector3f(0, 1, 0);
            normalPositions[i * 2 + 1] = new Vector3f(0, 1, 0);
            

//            int index = i * 6;
//            vertexPositions[index] = vertex.x;
//            verticesArray[index + 1] = vertex.y;
//            verticesArray[index + 2] = vertex.z;
//            verticesArray[index + 3] = 0;
//            verticesArray[index + 4] = 1;
//            verticesArray[index + 5] = 0;
//
//            // Add the left and right vertices to the arrays
//            vertexPositions[i * 2] = point.add(new Vector3f(0, 0, - / 2f));
//            vertexPositions[i * 2 + 1] = point.add(new Vector3f(0, 0, roadWidth / 2f));
//
//            // Calculate the UV coordinates for the left and right vertices
//            texCoord[i * 2] = new Vector2f(t, 0);
//            texCoord[i * 2 + 1] = new Vector2f(t, 1);
        }

    }
}
