/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf.sfntly;

/**
 *
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class NullAnchorTable extends AnchorTable {
    public NullAnchorTable() {
        super(null, null);
    }
    
    @Override
    public int getNumAnchors(int glyphId) {
        return 0;
    }
    
    @Override
    public int[] getAnchor(int glyphId, int pointNumber) {
        return new int[]{0, 0};
    }
    
    @Override
    public int[] getAnchors(int glyphId) {
        return new int[]{0, 0};
    }
}
