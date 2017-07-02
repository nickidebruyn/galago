/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.jme3.asset.AssetManager;

/**
 * A callback used to listen for changes in the font's texture atlas. Whenever the
 * texture atlas is modified the <code>mod</code> method will be called on all
 * <code>TTF_AtlasListener</code>s attached to the <code>TrueTypeFont</code>.
 * 
 * @see TrueTypeFont#addAtlasListener(truetypefont.TTF_AtlasListener) 
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public interface TTF_AtlasListener {
    public void mod(AssetManager assetManager, int oldWidth,
            int oldHeight, int newWidth, int newHeight, TrueTypeFont font);
}
