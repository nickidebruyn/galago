/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;

/**
 * Loads a {@link TrueTypeFont} from a .ttf file stored on disk.
 * 
 * @see TrueTypeKey
 * @see TrueTypeFont
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeLoader implements AssetLoader {
    
    @Override
    public TrueTypeFont load(AssetInfo assetInfo) throws IOException {
        //TrueTypeKey key = (TrueTypeKey)assetInfo.getKey();
        
        String vendor = System.getProperty("java.vendor.url");
        boolean android = vendor != null && vendor.toLowerCase().contains("android");
        
        if (!android)
            return new TrueTypeDesktopLoader().load(assetInfo);
        
        return new TrueTypeDroidLoader().load(assetInfo);
        
        /*if (!android) {
            java.awt.Font font = null;
            try {
                font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, assetInfo.openStream());
            } catch (java.awt.FontFormatException ffe) {
                font = null;
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to load "
                        + key.getName()
                        + " using system default Sans Serif font instead.", ffe);
            } catch (IOException ioe) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to load "
                        + key.getName()
                        + " using system default Sans Serif font instead.", ioe);
            }
            
            if (font != null) {
                switch(key.getStyle()) {
                    case Plain:
                        font = font.deriveFont(java.awt.Font.PLAIN, key.getPointSize());
                        break;
                    case Bold:
                        font = font.deriveFont(java.awt.Font.BOLD, key.getPointSize());
                        break;
                    case Italic:
                        font = font.deriveFont(java.awt.Font.ITALIC, key.getPointSize());
                        break;
                    case BoldItalic:
                        font = font.deriveFont(java.awt.Font.BOLD + java.awt.Font.ITALIC, key.getPointSize());
                        break;
                    default:

                }

                return new TrueTypeAwt(assetInfo.getManager(), font,
                    key.getPointSize(), key.getOutline(), key.getScreenDensity());
            } else
                return new TrueTypeAwt(assetInfo.getManager(), null,
                    key.getPointSize(), key.getOutline(), key.getScreenDensity());
        }
        FontFactory fontFactory = FontFactory.getInstance();
        Font[] fonts = fontFactory.loadFonts(assetInfo.openStream());
        
        if (fonts.length > 0) {
            return new TrueTypeDroid(assetInfo.getManager(), fonts[0], key.getStyle(),
                    key.getPointSize(), key.getOutline(), key.getScreenDensity());
        } else
            return null;*/
    }
}
