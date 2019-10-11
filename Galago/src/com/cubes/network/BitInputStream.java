/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.network;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author pspeed, carl
 */
public class BitInputStream{
 
    public BitInputStream(InputStream in){
        this.in = in;
    }
    private InputStream in;
    private int lastByte;
    private int bits = 0;
 
    public <T extends Enum<T>> T readEnum(Class<T> enumClass) throws IOException{
        T[] enumConstants = enumClass.getEnumConstants();
        int bitsCount = BitUtil.getNeededBitsCount(enumConstants.length);
        return enumConstants[readBits(bitsCount)];
    }
    
    public String readString_UTF8(int maximumBytesCountBits) throws IOException{
        int bytesCount = readBits(maximumBytesCountBits);
        byte[] bytes = readBytes(bytesCount);
        return new String(bytes, "UTF-8");
    }
    
    public byte[] readBytes(int bytesCount) throws IOException{
        byte[] bytes = new byte[bytesCount];
        for(int i=0;i<bytes.length;i++){
            bytes[i] = (byte) readBits(8);
        }
        return bytes;
    }
    
    public float readFloat() throws IOException{
        return Float.intBitsToFloat(readBits(32));
    }
    
    public int readInteger() throws IOException{
        return readBits(32);
    }
    
    public boolean readBoolean() throws IOException{
        return (readBits(1) == 1);
    }
 
    public int readBits(int count) throws IOException{
        if(count == 0){
            throw new IllegalArgumentException("Cannot read 0 bits.");
        }
        else if(count > 32){
            throw new IllegalArgumentException("Bit count overflow: " + count);
        }
 
        int result = 0;
 
        // While we still have bits remaining…
        int remainingCount = count;
        while(remainingCount > 0){
            // See if we need to refill the current read byte
            if(bits == 0){
                int b = in.read();
                if(b < 0){
                    throw new IOException("End of stream reached.");
                }
                lastByte = b;
                bits = 8;
            }
 
            // Copy the smaller of the two: remaining bits
            // or bits left in lastByte.
            int bitsToCopy = bits < remainingCount ? bits : remainingCount;
 
            // How much do we have to shift the read byte to just
            // get the high bits we want?
            int sourceShift = bits - bitsToCopy;
 
            // And how much do we have to shift those bits to graft
            // them onto our result?
            int targetShift = remainingCount - bitsToCopy;
 
            // Copy the bits
            result |= (lastByte >> sourceShift) << targetShift;
 
            // Keep track of how many bits we have left
            remainingCount -= bitsToCopy;
            bits -= bitsToCopy;
 
            // Now we need to mask off the bits we just copied from
            // lastByte.  Just keep the bits that are left.
            lastByte = lastByte & (0xff >> (8 - bits));
        }
        return result;
    }
 
    public long readLongBits(int count) throws IOException{
        if(count == 0){
            throw new IllegalArgumentException("Cannot read 0 bits.");
        }
        else if(count > 64){
            throw new IllegalArgumentException("Bit count overflow: " + count);
        }
        
        long result = 0;
 
        // While we still have bits remaining…
        int remainingCount = count;
        while(remainingCount > 0){
            // See if we need to refill the current read byte
            if(bits == 0){
                int b = in.read();
                if(b < 0){
                    throw new IOException( "End of stream reached." );
                }
                lastByte = b;
                bits = 8;
            }
 
            // Copy the smaller of the two: remaining bits
            // or bits left in lastByte.
            int bitsToCopy = bits < remainingCount ? bits : remainingCount;
 
            // How much do we have to shift the read byte to just
            // get the high bits we want?
            int sourceShift = bits - bitsToCopy;
 
            // And how much do we have to shift those bits to graft
            // them onto our result?
            int targetShift = remainingCount - bitsToCopy;
 
            // Copy the bits
            result |= ((long) lastByte >> sourceShift) << targetShift;
 
            // Keep track of how many bits we have left
            remainingCount -= bitsToCopy;
            bits -= bitsToCopy;
 
            // Now we need to mask off the bits we just copied from
            // lastByte.  Just keep the bits that are left.
            lastByte = lastByte & (0xff >> (8 - bits));
        }
 
        return result;
    }
 
    public void close(){
        try{
            in.close();
        }catch(IOException ex){
            System.out.println("Error while closing bit stream: " + ex.toString());
        }
    }
}