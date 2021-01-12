/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

/**
 *
 * @author NideBruyn
 */
public class JmeMesh extends Mesh {

    public JmeMesh() {
        super();
    }

    public void set(VertexBuffer.Type type, Collection<?> inputValues) {
        set(type, inputValues.toArray());
    }

    public void set(VertexBuffer.Type type, Object[] inputValues) {
        set(type, null, inputValues);
    }

    public void set(VertexBuffer.Type type, Integer components, Object[] inputValues) {

        if (inputValues == null || inputValues.length == 0) {
            clearBuffer(type);
            return;
        }

        Class<?> componentType = inputValues[0].getClass();

        if (componentType == Float.class) {

            FloatBuffer fb = BufferUtils.createFloatBuffer(inputValues.length);

            for (Object inputValue : inputValues) {
                fb.put((float) inputValue);
            }

            int c = (components == null ? 1 : components);
            setBuffer(type, c, fb);
        } else if (componentType == Integer.class) {

            IntBuffer ib = BufferUtils.createIntBuffer(inputValues.length);

            for (Object inputValue : inputValues) {
                ib.put((int) inputValue);
            }

            int c = type == VertexBuffer.Type.Index
                    ? 3
                    : (components == null ? 3 : components);

            setBuffer(type, c, ib);
        } else if (componentType == Vector2f.class) {

            FloatBuffer fb = BufferUtils.createFloatBuffer(inputValues.length * 2);

            for (Object inputValue : inputValues) {
                fb.put(((Vector2f) inputValue).x);
                fb.put(((Vector2f) inputValue).y);
            }

            setBuffer(type, 2, fb);
        } else if (componentType == Vector3f.class) {

            FloatBuffer fb = BufferUtils.createFloatBuffer(inputValues.length * 3);

            for (Object inputValue : inputValues) {
                fb.put(((Vector3f) inputValue).x);
                fb.put(((Vector3f) inputValue).y);
                fb.put(((Vector3f) inputValue).z);
            }

            setBuffer(type, 3, fb);

        } else if (componentType == Vector4f.class) {

            FloatBuffer fb = BufferUtils.createFloatBuffer(inputValues.length * 4);

            for (Object inputValue : inputValues) {
                fb.put(((Vector4f) inputValue).x);
                fb.put(((Vector4f) inputValue).y);
                fb.put(((Vector4f) inputValue).z);
                fb.put(((Vector4f) inputValue).w);
            }

            setBuffer(type, 4, fb);
        }

        // if we update the positions of the vertices, we also need to update the bounds of the mesh.
        if (type == VertexBuffer.Type.Position) {
            updateBound();
        }

    }

    public void moveVertices(float x, float y, float z) {

        Vector3f[] verts = BufferUtils.getVector3Array(getFloatBuffer(VertexBuffer.Type.Position));
        FloatBuffer floatBuffer = getFloatBuffer(VertexBuffer.Type.Position);

        for (int i = 0; i < verts.length; i++) {
            BufferUtils.setInBuffer(verts[i].add(x, y, z), floatBuffer, i);
        }

        updateBound(); // ?
    }

}
