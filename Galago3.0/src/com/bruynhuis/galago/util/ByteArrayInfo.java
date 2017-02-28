/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author Nidebruyn
 */
public class ByteArrayInfo extends AssetInfo {
    
        private byte[] data;

        /**
         * @param manager
         * @param key
         */
        public ByteArrayInfo(AssetManager assetManager, byte[] data) {
            super(assetManager, new TextureKey("ByteArray", true));
            this.data = data;
        }

        /* (non-Javadoc)
         * @see com.jme3.asset.AssetInfo#openStream()
         */
        @Override
        public InputStream openStream() {
            return new ByteArrayInputStream(data);
        }
}
