/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.jme3.asset.AssetInfo;
import java.io.IOException;

/**
 *
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeDroidLoader {
    public TrueTypeFont load(AssetInfo assetInfo) throws IOException {
        TrueTypeKey key = (TrueTypeKey)assetInfo.getKey();
        
        FontFactory fontFactory = FontFactory.getInstance();
        Font[] fonts = fontFactory.loadFonts(assetInfo.openStream());
        
        if (fonts.length > 0) {
            return new TrueTypeDroid(assetInfo.getManager(), fonts[0], key.getStyle(),
                    key.getPointSize(), key.getOutline(), key.getScreenDensity());
        } else
            return null;
    }
}
