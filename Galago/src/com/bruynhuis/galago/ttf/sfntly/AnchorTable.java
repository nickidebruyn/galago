/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf.sfntly;

import com.google.typography.font.sfntly.data.FontData;
import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.table.Header;
import com.google.typography.font.sfntly.table.Table;

/**
 * Reads the 'ankr' table from a TrueType Font file.
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class AnchorTable extends Table {
    public final int version;
    public final int flags;
    public final int lookupOffset;
    public final int glyphDataOffset;
    
    public AnchorTable(Table table) {
        this(table.header(), table.readFontData());
    }
    
    public AnchorTable(Header header, ReadableFontData data) {
        super(header, data);
        if (header != null && data != null) {
            version = this.data.readUShort(0);
            flags = this.data.readUShort(FontData.DataSize.USHORT.size());
            lookupOffset = this.data.readULongAsInt(FontData.DataSize.USHORT.size() * 2);
            glyphDataOffset = this.data.readULongAsInt((FontData.DataSize.USHORT.size() * 2)
                    + FontData.DataSize.ULONG.size());
        } else {
            version = 0;
            flags = 0;
            lookupOffset = 0;
            glyphDataOffset = 0;
        }
    }
    
    /**
     * Retrieves the number of anchor points for a given glyph.
     * 
     * @param glyphId The glyph id to lookup.
     * @return The number of anchor points for this glyph.
     */
    public int getNumAnchors(int glyphId) {
        if (glyphId < 0)
            return 0;
        
        int index = (2 * glyphId) + lookupOffset;
        if (index > glyphDataOffset)
            return 0;
        
        index = this.data.readUShort(index);
        
        return this.data.readULongAsInt(index + glyphDataOffset);
    }
    
    /**
     * Gets a specific anchor point from a glyph.
     * 
     * @param glyphId The glyph id to lookup.
     * @param pointNumber The anchor point number for the glyph
     * to retrieve.
     * @return A size two integer array where the first value
     * represents the x-axis and the second the y-axis. Returns
     * {0, 0} if the glyph does not contain the requested point.
     */
    public int[] getAnchor(int glyphId, int pointNumber) {
        if (pointNumber < 0 || glyphId < 0)
            return new int[]{0, 0};
        
        int index = (2 * glyphId) + lookupOffset;
        if (index > glyphDataOffset)
            return new int[]{0, 0};
        
        index = this.data.readUShort(index) + glyphDataOffset;
        
        int numPoints = this.data.readULongAsInt(index);
        index += FontData.DataSize.ULONG.size() + (FontData.DataSize.LONG.size() * pointNumber);
        if (pointNumber >= numPoints)
            return new int[]{0, 0};
        
        int x = this.data.readShort(index);
        int y = this.data.readShort(index + FontData.DataSize.SHORT.size());
        
        return new int[]{x, y};
    }
    
    /**
     * Gets all the anchors for a glyph.
     * 
     * @param glyphId The glyph to lookup.
     * @return An integer array containing all the x/y-axis value
     * pairs for each point. The first item in the array corresponds
     * to the x-axis of the first anchor while the second item is
     * the y-axis of the first anchor and so on. A particular
     * point will be located at point * 2 in the array.
     */
    public int[] getAnchors(int glyphId) {
        if (glyphId < 0) 
            return new int[]{0, 0};
        
        int index = (2 * glyphId) + lookupOffset;
        if (index > glyphDataOffset)
            return new int[]{0, 0};
        
        index = this.data.readUShort(index) + glyphDataOffset;
        int[] points = new int[this.data.readULongAsInt(index) * 2];
        
        if (points.length == 0)
            return new int[]{0, 0};
        
        index += FontData.DataSize.ULONG.size();
        
        int i = 0;
        while (i < points.length) {
            points[i++] = this.data.readShort(index);
            index += FontData.DataSize.SHORT.size();
            points[i++] = this.data.readShort(index);
            index += FontData.DataSize.SHORT.size();
        }
        
        return points;
    }
}
