/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.network;

import java.io.IOException;

/**
 *
 * @author Carl
 */
public interface BitSerializable{
    
    public abstract void write(BitOutputStream outputStream);
    
    public abstract void read(BitInputStream inputStream) throws IOException;
}
