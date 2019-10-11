/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.shapes;

import java.util.List;
import com.cubes.*;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author Carl
 */
public class BlockShape_Cuboid extends BlockShape{

    public BlockShape_Cuboid(float[] extents){
        this.extents = extents;
    }
    //{top, bottom, left, right, front, back}
    private float[] extents;

    @Override
    public void addTo(BlockChunkControl chunk, Vector3Int blockLocation){
        Block block = chunk.getBlock(blockLocation);
        Vector3f blockLocation3f = new Vector3f(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        Vector3f faceLoc_Bottom_TopLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f - extents[1]), (0.5f - extents[5])));
        Vector3f faceLoc_Bottom_TopRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f - extents[1]), (0.5f - extents[5])));
        Vector3f faceLoc_Bottom_BottomLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f - extents[1]), (0.5f + extents[4])));
        Vector3f faceLoc_Bottom_BottomRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f - extents[1]), (0.5f + extents[4])));
        Vector3f faceLoc_Top_TopLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f + extents[0]), (0.5f - extents[5])));
        Vector3f faceLoc_Top_TopRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f + extents[0]), (0.5f - extents[5])));
        Vector3f faceLoc_Top_BottomLeft = blockLocation3f.add(new Vector3f((0.5f - extents[2]), (0.5f + extents[0]), (0.5f + extents[4])));
        Vector3f faceLoc_Top_BottomRight = blockLocation3f.add(new Vector3f((0.5f + extents[3]), (0.5f + extents[0]), (0.5f + extents[4])));
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Top)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Top_BottomLeft);
            positions.add(faceLoc_Top_BottomRight);
            positions.add(faceLoc_Top_TopLeft);
            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 0, 1, 0);
            
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Top).getTextureLocation());
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Bottom)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
            addSquareNormals(normals, 0, -1, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Bottom).getTextureLocation());
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Left)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_TopLeft);
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Top_TopLeft);
            positions.add(faceLoc_Top_BottomLeft);
            addSquareNormals(normals, -1, 0, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Left).getTextureLocation());
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Right)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Top_BottomRight);
            positions.add(faceLoc_Top_TopRight);
            addSquareNormals(normals, 1, 0, 0);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Right).getTextureLocation());
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Front)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_BottomLeft);
            positions.add(faceLoc_Bottom_BottomRight);
            positions.add(faceLoc_Top_BottomLeft);
            positions.add(faceLoc_Top_BottomRight);
            addSquareNormals(normals, 0, 0, 1);
            addTextureCoordinates(chunk, textureCoordinates,block.getSkin(chunk, blockLocation, Block.Face.Front).getTextureLocation());
        }
        if(shouldFaceBeAdded(chunk, blockLocation, Block.Face.Back)){
            addFaceIndices(indices, positions.size());
            positions.add(faceLoc_Bottom_TopRight);
            positions.add(faceLoc_Bottom_TopLeft);
            positions.add(faceLoc_Top_TopRight);
            positions.add(faceLoc_Top_TopLeft);
            addSquareNormals(normals, 0, 0, -1);
            addTextureCoordinates(chunk, textureCoordinates, block.getSkin(chunk, blockLocation, Block.Face.Back).getTextureLocation());
        }
    }

    private void addFaceIndices(List<Short> indices, int offset){
        indices.add((short) (offset + 2));
        indices.add((short) (offset + 0));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 1));
        indices.add((short) (offset + 3));
        indices.add((short) (offset + 2));
    }
    
    private void addSquareNormals(List<Float> normals, float normalX, float normalY, float normalZ){
        for(int i=0;i<4;i++){
            normals.add(normalX);
            normals.add(normalY);
            normals.add(normalZ);
        }
    }

    private void addTextureCoordinates(BlockChunkControl chunk, List<Vector2f> textureCoordinates, BlockSkin_TextureLocation textureLocation){
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 0));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 0, 1));
        textureCoordinates.add(getTextureCoordinates(chunk, textureLocation, 1, 1));
    }

    @Override
    protected boolean canBeMerged(Block.Face face){
        boolean isAllowed = true;
        Block.Face oppositeFace = BlockNavigator.getOppositeFace(face);
        for(int i=0;i<extents.length;i++){
            if((i != oppositeFace.ordinal()) && (extents[i] != 0.5f)){
                isAllowed = false;
                break;
            }
        }
        return isAllowed;
    }
}
