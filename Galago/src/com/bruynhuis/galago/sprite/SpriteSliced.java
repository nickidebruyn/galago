/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 *
 * @author nidebruyn
 */
public class SpriteSliced extends Mesh {

    private float width;
    private float height;
    private float textureWidth = 512;
    private float textureHeight = 512;
    private float xStart = 0;
    private float yStart = 0;
    private float xEnd = 0;
    private float yEnd = 0;
    private boolean flipCoords = false;

    /**
     * Serialization only. Do not use.
     */
    public SpriteSliced() {
    }

    public SpriteSliced(int textureWidth, int textureHeight, int xStart, int yStart, int xEnd, int yEnd, boolean flipped) {


        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.xStart = (float)xStart;
        this.yStart = (float)yStart;
        this.xEnd = (float)xEnd;
        this.yEnd = (float)yEnd;
        
        this.width = (float)xEnd-(float)xStart;
        this.height = (float)yEnd-(float)yStart;        

        this.flipCoords = flipped;

        initializeMesh();

    }

    private void initializeMesh() {
        // Vertex positions in space
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-width * 0.5f, -height * 0.5f, 0f);
        vertices[1] = new Vector3f(width * 0.5f, -height * 0.5f, 0f);
        vertices[2] = new Vector3f(-width * 0.5f, height * 0.5f, 0f);
        vertices[3] = new Vector3f(width * 0.5f, height * 0.5f, 0f);

        // Texture coordinates
        Vector2f[] texCoord = new Vector2f[4];
        if (flipCoords) {
            texCoord[0] = new Vector2f(xEnd / textureWidth, yStart / textureHeight);
            texCoord[1] = new Vector2f(xStart / textureWidth, yStart / textureHeight);
            texCoord[2] = new Vector2f(xEnd / textureWidth, yEnd / textureHeight);
            texCoord[3] = new Vector2f(xStart / textureWidth, yEnd / textureHeight);

        } else {
            System.out.println("xEnd / textureWidth = " + (xEnd / textureWidth));
            texCoord[0] = new Vector2f(xStart / textureWidth, yEnd / textureHeight);
            texCoord[1] = new Vector2f(xEnd / textureWidth, yEnd / textureHeight);
            texCoord[2] = new Vector2f(xStart / textureWidth, yStart / textureHeight);
            texCoord[3] = new Vector2f(xEnd / textureWidth, yStart / textureHeight);
        }

        // Indexes. We define the order in which mesh should be constructed
        short[] indexes = {2, 0, 1, 1, 3, 2};

        // Setting buffers
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(VertexBuffer.Type.Normal, 3, new float[]{0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1});
        setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        updateBound();

    }

//    /**
//     * This method can be called if the user wants to flip the coordinates.
//     * @param flip 
//     */
//    public void flipCoords(boolean flip) {
//        this.flipCoords = flip;
//        updateTextureCoords(colPosition, rowPosition);
//    }
//    public void updateTextureCoords(int colPosition, int rowPosition) {
//        this.colPosition = colPosition;
//        this.rowPosition = rowPosition;
//        
//        float colSize = 1f/(float)columns;
//        float rowSize = 1f/(float)rows;
//
//        // Texture coordinates
//        Vector2f [] texCoord = new Vector2f[4];
//        
//        if (flipCoords) {
//            texCoord[0] = new Vector2f(colSize*colPosition+colSize - uvSpacing, rowSize*rowPosition + uvSpacing);
//            texCoord[1] = new Vector2f(colSize*colPosition + uvSpacing, rowSize*rowPosition + uvSpacing);
//            texCoord[2] = new Vector2f(colSize*colPosition+colSize - uvSpacing, rowSize*rowPosition+rowSize - uvSpacing);
//            texCoord[3] = new Vector2f(colSize*colPosition + uvSpacing, rowSize*rowPosition+rowSize - uvSpacing);
//            
//        } else {
//            texCoord[0] = new Vector2f(colSize*colPosition + uvSpacing, rowSize*rowPosition+rowSize - uvSpacing);
//            texCoord[1] = new Vector2f(colSize*colPosition+colSize - uvSpacing, rowSize*rowPosition+rowSize - uvSpacing);
//            texCoord[2] = new Vector2f(colSize*colPosition + uvSpacing, rowSize*rowPosition + uvSpacing);
//            texCoord[3] = new Vector2f(colSize*colPosition+colSize - uvSpacing, rowSize*rowPosition + uvSpacing);
//        }
//        
//        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
//    }
    public boolean isFlipCoords() {
        return flipCoords;
    }

}
