/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import java.util.Iterator;
import java.util.LinkedList;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 *
 * @author Carl
 */
public class BlockChunk_MeshOptimizer{

    private static Vector3f[] positions;
    private static short[] indices;
    private static Vector2f[] textureCoordinates;
    private static float[] normals;

    public static Mesh generateOptimizedMesh(BlockChunkControl blockChunk, boolean isTransparent){
        LinkedList<Vector3f> positionsList = new LinkedList<Vector3f>();
        LinkedList<Short> indicesList = new LinkedList<Short>();
        LinkedList<Float> normalsList = new LinkedList<Float>();
        LinkedList<Vector2f> textureCoordinatesList = new LinkedList<Vector2f>();
        BlockTerrainControl blockTerrain = blockChunk.getTerrain();
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<blockTerrain.getSettings().getChunkSizeX();x++){
            for(int y=0;y<blockTerrain.getSettings().getChunkSizeY();y++){
                for(int z=0;z<blockTerrain.getSettings().getChunkSizeZ();z++){
                    tmpLocation.set(x, y, z);
                    Block block = blockChunk.getBlock(tmpLocation);
                    if(block != null){
                        BlockShape blockShape = block.getShape(blockChunk, tmpLocation);
                        blockShape.prepare(isTransparent, positionsList, indicesList, normalsList, textureCoordinatesList);
                        blockShape.addTo(blockChunk, tmpLocation);
                    }
                }
            }
        }
        positions = new Vector3f[positionsList.size()];
        Iterator<Vector3f> positionsIterator = positionsList.iterator();
        for(int i=0;positionsIterator.hasNext();i++){
            positions[i] = positionsIterator.next().mult(blockTerrain.getSettings().getBlockSize());
        }
        indices = new short[indicesList.size()];
        Iterator<Short> indicesIterator = indicesList.iterator();
        for(int i=0;indicesIterator.hasNext();i++){
            indices[i] = indicesIterator.next();
        }
        textureCoordinates = textureCoordinatesList.toArray(new Vector2f[0]);
        normals = new float[normalsList.size()];
        Iterator<Float> normalsIterator = normalsList.iterator();
        for(int i=0;normalsIterator.hasNext();i++){
            normals[i] = normalsIterator.next();
        }
        return generateMesh();
    }

    private static Mesh generateMesh(){
        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        mesh.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indices));
        mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoordinates));
        mesh.updateBound();
        return mesh;
    }
}
