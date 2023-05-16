package com.galago.tests.screens;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author ndebruyn
 */
public class RoadGenerator {

    private static final float ROAD_WIDTH = 5f;
    private static final float ROAD_HEIGHT = 0.1f;
    private static final ColorRGBA ROAD_COLOR = new ColorRGBA(0.2f, 0.2f, 0.2f, 1f);

    public static Mesh generateRoad(int numVertices, Vector3f[] vertices) {
        Mesh mesh = new Mesh();

        Vector3f[] normals = new Vector3f[numVertices];
        Vector3f[] tangents = new Vector3f[numVertices];
        Vector3f[] binormals = new Vector3f[numVertices];
        Vector3f[] uvs = new Vector3f[numVertices];

        for (int i = 0; i < numVertices; i++) {
            normals[i] = Vector3f.UNIT_Y;
            tangents[i] = Vector3f.UNIT_X;
            binormals[i] = Vector3f.UNIT_Z;
            uvs[i] = new Vector3f(i / (float) numVertices, 0f, 0f);
        }

        int numTriangles = numVertices - 1;
        int numVerticesPerTriangle = 6;
        int numIndices = numTriangles * numVerticesPerTriangle;
        int[] indices = new int[numIndices];

        int index = 0;
        for (int i = 0; i < numTriangles; i++) {
            int vertexIndex = i * 2;
            indices[index++] = vertexIndex;
            indices[index++] = vertexIndex + 1;
            indices[index++] = vertexIndex + 2;
            indices[index++] = vertexIndex + 2;
            indices[index++] = vertexIndex + 1;
            indices[index++] = vertexIndex + 3;
        }

        mesh.setBuffer(Type.Position, 3, createFloatBuffer(vertices));
        mesh.setBuffer(Type.Normal, 3, createFloatBuffer(normals));
        mesh.setBuffer(Type.Tangent, 3, createFloatBuffer(tangents));
        mesh.setBuffer(Type.Binormal, 3, createFloatBuffer(binormals));
        mesh.setBuffer(Type.TexCoord, 3, createFloatBuffer(uvs));
        mesh.setBuffer(Type.Index, 3, createIntBuffer(indices));
        mesh.updateBound();
        mesh.setStatic();

        return mesh;
    }

    private static FloatBuffer createFloatBuffer(Vector3f[] array) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length * 3);
        for (Vector3f v : array) {
            buffer.put(v.x).put(v.y).put(v.z);
        }
        buffer.flip();
        return buffer;
    }

    private static IntBuffer createIntBuffer(int[] array) {
        IntBuffer buffer = BufferUtils.createIntBuffer(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

}
