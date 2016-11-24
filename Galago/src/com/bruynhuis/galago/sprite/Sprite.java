/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * @author nidebruyn
 */
public class Sprite extends Node {

    protected BaseApplication baseApplication;
    protected SpriteQuad quad;
    protected Material material;
    protected Texture texture;
    protected float width;
    protected float height;
    protected int rows = 1;
    protected int columns = 1;
    protected int currentRow = 0;
    protected int currentColumn = 0;
    protected Geometry geometry;
    protected Quaternion quaternion = new Quaternion();
    protected boolean horizontalFlipped = false;

    public Sprite() {
    }

    public Sprite(String name, float width, float height) {
        this(name, width, height, 1, 1, 0, 0);

    }
    
    public Sprite(String name, float width, float height, int columns, int rows, int index) {
        this(name, width, height, columns, rows, index % columns, index / columns);
    }

    public Sprite(String name, float width, float height, int columns, int rows, int currentColumn, int currentRow) {
        super(name);
        this.baseApplication = SharedSystem.getInstance().getBaseApplication();
        this.width = width;
        this.height = height;

        this.columns = columns;
        this.rows = rows;
        this.currentColumn = currentColumn;
        this.currentRow = currentRow;

        initializeSprite();
    }

    protected void initializeSprite() {
        quad = new SpriteQuad(width, height, columns, rows, currentColumn, currentRow);
        geometry = new Geometry(name + "_geom", quad);
//        geometry.setQueueBucket(RenderQueue.Bucket.Gui);
        attachChild(geometry);
    }
    
    /**
     * Scale the vector texture
     * @param scaleVector 
     */
    public void scaleTextureCoords(Vector2f scaleVector) {
        if (quad != null) {
            quad.scaleTextureCoordinates(scaleVector);
        }
    }
    
    /**
     * This method can be called if the user wants to flip the coordinates.
     * @param flip 
     */
    public void flipCoords(boolean flip) {
        quad.flipCoords(flip);
    }
    
    public boolean isFlipCoords() {
        return quad.isFlipCoords();
    }
    
    public void flipHorizontal(boolean flip) {
        this.horizontalFlipped = flip;
        if (flip) {
            quaternion.fromAngleAxis(FastMath.DEG_TO_RAD*180f, Vector3f.UNIT_Y);
        } else {
            quaternion.fromAngleAxis(FastMath.DEG_TO_RAD*0f, Vector3f.UNIT_Y);
        }
        geometry.setLocalRotation(quaternion);
    }
    
    public void showIndex(int index) {
        showIndex(index % columns, index / columns);        
    }

    public void showIndex(int colPosition, int rowPosition) {
        this.currentColumn = colPosition;
        this.currentRow = rowPosition;
        quad.updateTextureCoords(colPosition, rowPosition);
        setCullHint(Spatial.CullHint.Never);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public void setMaterial(Material material) {
        super.setMaterial(material);
        this.material = material;
    }

    public void setImage(String imagePath) {
        Texture texture = baseApplication.getAssetManager().loadTexture(imagePath);
        texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        texture.setWrap(Texture.WrapMode.Repeat);

        Material material = new Material(baseApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        material.setColor("Color", ColorRGBA.White);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        material.setTexture("ColorMap", texture);
        setMaterial(material);
        
        flipCoords(true);
        flipHorizontal(true);
    }
//
//    @Override
//    public void write(JmeExporter e) throws IOException {
//        super.write(e); //To change body of generated methods, choose Tools | Templates.
//        OutputCapsule capsule = e.getCapsule(this);
//        capsule.write(width, "width", 10);
//        capsule.write(height, "height", 10);
//        capsule.write(rows, "rows", 1);
//        capsule.write(columns, "columns", 1);
//
//    }
//
//    @Override
//    public void read(JmeImporter im) throws IOException {
//        //Read sprite parameters        
//        super.read(im);
//        InputCapsule capsule = im.getCapsule(this);
//        this.width = capsule.readFloat("width", 10.0f);
//        this.height = capsule.readFloat("height", 10.0f);
//        this.rows = capsule.readInt("rows", 1);
//        this.columns = capsule.readInt("columns", 1);
//
//        this.baseApplication = SharedSystem.getInstance().getBaseApplication();
//
//        initializeSprite();
//    }
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public boolean isHorizontalFlipped() {
        return horizontalFlipped;
    }
    
}
