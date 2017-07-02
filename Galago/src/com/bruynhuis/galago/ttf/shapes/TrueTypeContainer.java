/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf.shapes;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import com.bruynhuis.galago.ttf.TrueTypeBitmapGlyph;
import com.bruynhuis.galago.ttf.util.StringContainer;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;

/**
 * A <code>Geometry</code> that builds a <code>Mesh</code> to display
 * text constrained by parameters set via a {@link StringContainer}
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 * 
 * @see StringContainer
 */
public class TrueTypeContainer extends Geometry {
    private StringContainer stringContainer;
    
    /**
     * Constructs a new instance of <code>TrueTypeContainer</code>.
     * 
     * @param stringContainer The {@link StringContainer} that defines
     * the parameters for the text to be rendered with this
     * <code>TrueTypeContainer</code>.
     * 
     * @see StringContainer
     */
    public TrueTypeContainer(StringContainer stringContainer) {
        super("TrueTypeBitmapGeometry", new Mesh());
        
        this.stringContainer = stringContainer;
        updateGeometry();
    }
    
    /**
     * Get the {@link StringContainer} associated with this
     * <code>TrueTypeContainer</code>.
     * 
     * @return The {@link StringContainer}
     * 
     * @see StringContainer
     */
    public StringContainer getStringContainer() {
        return stringContainer;
    }
    
    /**
     * Sets the {@link StringContainer} for this <code>TrueTypeContainer</code>
     * and calls {@link #updateGeometry()}.
     * 
     * @param container The new {@link StringContainer}
     * 
     * @see StringContainer
     * @see #updateGeometry() 
     */
    public void setStringContainer(StringContainer container) {
        stringContainer = container;
        updateGeometry(false);
    }
    
    /**
     * Updates the <code>Mesh</code> that displays the text in the
     * associated {@link StringContainer}. This method should be
     * called whenever the {@link StringContainer} is modified.
     * 
     * @see StringContainer
     * @see #setStringContainer(truetypefont.util.StringContainer) 
     */
    public void updateGeometry() {
        updateGeometry(true);
    }
    
    private void updateGeometry(boolean initialized) {
        TrueTypeBitmapGlyph[][] lines = stringContainer.getLines();
        
        if (stringContainer.getText().isEmpty() || stringContainer.getNumNonSpaceCharacters() == 0) {
            mesh = new Mesh();
            return;
        }
        
        float heightOffset = 0;
        switch(stringContainer.getVerticalAlignment()) {
            case Bottom:
                heightOffset = stringContainer.getTextBox().height
                        - stringContainer.getTextHeight();
                break;
            case Center:
                float halfBox = stringContainer.getTextBox().height / 2;
                float halfHeight = stringContainer.getTextHeight() / 2;
                heightOffset = halfBox - halfHeight;
                break;
        }
        
        FloatBuffer verts = BufferUtils.createFloatBuffer(stringContainer.getNumNonSpaceCharacters() * 12);
        FloatBuffer norms = BufferUtils.createFloatBuffer(verts.capacity());
        FloatBuffer tex1 = BufferUtils.createFloatBuffer(stringContainer.getNumNonSpaceCharacters() * 8);
        ShortBuffer indices = BufferUtils.createShortBuffer(stringContainer.getNumNonSpaceCharacters() * 6);
        
        int padding = stringContainer.getFont().padding / 2;
        float currentLineHeight = stringContainer.getFont().getActualAscender();
        short currentIndex = 0;
        int lineNum = 0;
        for (TrueTypeBitmapGlyph[] line : lines) {
            int currentX = 0;
            
            for (TrueTypeBitmapGlyph glyph : line) {
                if (glyph.codePoint == ' ') {
                    currentX += glyph.xAdvance + stringContainer.getKerning();
                    continue;
                }
                
                float widthOffset = 0;
                switch(stringContainer.getAlignment()) {
                    case Right:
                        widthOffset = stringContainer.getTextBox().width
                                - stringContainer.getLineWidths()[lineNum];
                        break;
                    case Center:
                        float halfBox = stringContainer.getTextBox().width / 2;
                        float halfWidth = stringContainer.getLineWidths()[lineNum] / 2;
                        widthOffset = halfBox - halfWidth;
                        break;
                }
                
                //Lower left
                verts.put((((currentX - padding) + glyph.xMod) * stringContainer.getFont().getScale()) + widthOffset);
                verts.put((((-glyph.hMod - padding) - currentLineHeight)
                        * stringContainer.getFont().getScale()) - heightOffset);
                verts.put(0);

                tex1.put(glyph.getLeftU());
                tex1.put(glyph.getBottomV());

                //Lower right
                verts.put(((currentX + glyph.w + glyph.xMod)
                        * stringContainer.getFont().getScale()) + widthOffset);
                verts.put((((-glyph.hMod - padding) - currentLineHeight)
                        * stringContainer.getFont().getScale()) - heightOffset);
                verts.put(0);

                tex1.put(glyph.getRightU());
                tex1.put(glyph.getBottomV());

                //Upper left
                verts.put((((currentX - padding) + glyph.xMod) * stringContainer.getFont().getScale()) + widthOffset);
                verts.put(((((glyph.h - glyph.hMod)) - currentLineHeight)
                        * stringContainer.getFont().getScale()) - heightOffset);
                verts.put(0);

                tex1.put(glyph.getLeftU());
                tex1.put(glyph.getTopV());

                //Upper right
                verts.put(((currentX + glyph.w + glyph.xMod)
                         * stringContainer.getFont().getScale()) + widthOffset);
                verts.put(((((glyph.h - glyph.hMod)) - currentLineHeight)
                         * stringContainer.getFont().getScale()) - heightOffset);
                verts.put(0);

                tex1.put(glyph.getRightU());
                tex1.put(glyph.getTopV());

                norms.put(0);
                norms.put(0);
                norms.put(1);
                norms.put(0);
                norms.put(0);
                norms.put(1);
                norms.put(0);
                norms.put(0);
                norms.put(1);
                norms.put(0);
                norms.put(0);
                norms.put(1);

                indices.put(currentIndex);
                indices.put((short)(currentIndex + 1));
                indices.put((short)(currentIndex + 2));
                indices.put((short)(currentIndex + 2));
                indices.put((short)(currentIndex + 1));
                indices.put((short)(currentIndex + 3));

                currentX += glyph.xAdvance + stringContainer.getKerning();
                currentIndex += 4;
            }
            
            currentLineHeight += stringContainer.getFont().getActualLineHeight();
            lineNum++;
        }
        
        mesh.setBuffer(VertexBuffer.Type.Position, 3, verts);
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, norms);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, tex1);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indices);
        
        mesh.updateBound();
        mesh.updateCounts();
        
    }
    
    @Override
    public TrueTypeContainer clone() {
        TrueTypeContainer ttc = (TrueTypeContainer)super.clone();
        ttc.mesh = mesh.deepClone();
        
        return ttc;
    }

    @Override
    public int collideWith(Collidable other, CollisionResults results) {
        //TODO how to fix
//        System.out.println("Collision with ");
//        return super.collideWith(other, results); //To change body of generated methods, choose Tools | Templates.
        return 0;
    }
    
    
}
