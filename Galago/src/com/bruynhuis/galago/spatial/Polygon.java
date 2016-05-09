/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 *
 * @author Nidebruyn
 */
public class Polygon extends Mesh {

    /**
     * Number of sides on the polygon.
     */
    private int sides;
    /**
     * Thickness of the border.
     */
    private float thickness;
    /**
     * Outer radius of the polygon.
     */
    private float outerRadius;

    /**
     * Constructor.
     *
     * @param sides Number of sides on the polygon
     * @param outerRadius Outer radius of the polygon
     * @param thickness Thickness of the border
     */
    public Polygon(int sides, float outerRadius, float thickness) {
        this.sides = sides;
        this.thickness = thickness;
        this.outerRadius = outerRadius;
        buildMesh();
    }

    /**
     * Builds the mesh structure.
     */
    private void buildMesh() {
        // Quaternion used to rotate the corner points in order to get the next one
        Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.TWO_PI / (float) sides, Vector3f.UNIT_Z);
        // The angle on the inside of the polygon
        float innerAngle = (float) (sides - 2) * FastMath.PI / (float) sides;
        // The distance between an inner and an outer corner along the radius
        float cornerDistance = thickness / FastMath.sin(innerAngle / 2f);
        // The inner radius
        float innerRadius = outerRadius - cornerDistance;

        // vertices (CCW, first outer then inner)
        Vector3f[] vertices = new Vector3f[sides * 2];
        Vector2f[] texCoord = new Vector2f[sides * 2];
        int[] indices = new int[sides * 2 * 3];

        Vector3f inner = Vector3f.UNIT_Y.mult(innerRadius);
        Vector3f outer = Vector3f.UNIT_Y.mult(outerRadius);
        int index = 0;
        for (int i = 0; i < sides; i++) {
            // Set vertices
            vertices[i * 2] = outer;
            vertices[i * 2 + 1] = inner;

            // Set texture coordinates
            texCoord[i * 2] = new Vector2f(outer.x, outer.y);
            texCoord[i * 2 + 1] = new Vector2f(inner.x, inner.y);

            // Set indices
            int thisOuter = i * 2;
            int thisInner = i * 2 + 1;
            int nextOuter = i * 2 + 2;
            int nextInner = i * 2 + 3;
            if (i == sides - 1) {
                nextOuter = 0;
                nextInner = 1;
            }
            indices[index++] = thisOuter;
            indices[index++] = nextInner;
            indices[index++] = thisInner;
            indices[index++] = thisOuter;
            indices[index++] = nextOuter;
            indices[index++] = nextInner;

            // Calculate next location
            inner = rotation.mult(inner);
            outer = rotation.mult(outer);
        }

        // Create actual mesh
        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        updateBound();
    }

    public int getSides() {
        return sides;
    }

    public void setSides(int sides) {
        this.sides = sides;
        buildMesh();
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
        buildMesh();
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
        buildMesh();
    }
}
