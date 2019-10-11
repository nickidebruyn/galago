/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.network;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author pspeed, carl
 */
public class BitOutputStream{
 
    public BitOutputStream(OutputStream out){
        this.out = out;
    }
    private OutputStream out;
    private int currentByte = 0;
    private int bits = 8;
 
    public <T extends Enum<T>> void writeEnum(T value){
        T[] enumConstants = value.getDeclaringClass().getEnumConstants();
        int bitsCount = BitUtil.getNeededBitsCount(enumConstants.length);
        writeBits(value.ordinal(), bitsCount);
    }
 
    public void writeString_UTF8(String string, int maximumBytesCountBits){
        try{
            byte[] bytes = string.getBytes("UTF-8");
            writeBits(bytes.length, maximumBytesCountBits);
            writeBytes(bytes, bytes.length);
        }catch(UnsupportedEncodingException ex){
            System.out.println("Error while encoding string: " + ex.toString());
        }
    }
 
    public void writeBytes(byte[] bytes, int count){
        for(int i=0;i<count;i++){
            byte value = ((i < bytes.length)?bytes[i]:0);
            writeBits(value, 8);
        }
    }
 
    public void writeFloat(float value){
        writeBits(Float.floatToIntBits(value), 32);
    }
 
    public void writeInteger(int value){
        writeBits(value, 32);
    }
 
    public void writeBoolean(boolean value){
        writeBits((value?1:0), 1);
    }
 
    public void writeBits(int value, int count){
        if(count == 0){
            throw new IllegalArgumentException("Cannot write 0 bits.");
        }
        // Make sure the value is clean of extra high bits
        value = value & (0xffffffff >>> (32 - count));
 
        int remaining = count;
        while( remaining > 0 ) {
            int bitsToCopy = bits < remaining ? bits : remaining;
 
            int sourceShift = remaining - bitsToCopy;
            int targetShift = bits - bitsToCopy;
 
            currentByte |= (value >>> sourceShift) << targetShift;
 
            remaining -= bitsToCopy;
            bits -= bitsToCopy;                      
 
            value = value & (0xffffffff >>> (32 - remaining));
 
            // If there are no more bits left to write to in our
            // working byte then write it out and clear it.
            if(bits == 0){
                flush();
            }
        }
    }
 
    public void writeLongBits(long value, int count){
        if(count == 0){
            throw new IllegalArgumentException("Cannot write 0 bits.");
        }
 
        // Make sure the value is clean of extra high bits
        value = value & (0xffffffffffffffffL >>> (64 - count));
 
        int remaining = count;
        while(remaining > 0){
            int bitsToCopy = bits < remaining ? bits : remaining;
 
            int sourceShift = remaining - bitsToCopy;
            int targetShift = bits - bitsToCopy;
 
            currentByte |= (value >>> sourceShift) << targetShift;
 
            remaining -= bitsToCopy;
            bits -= bitsToCopy;                      
 
            value = value & (0xffffffffffffffffL >>> (64 - remaining));
 
            // If there are no more bits left to write to in our
            // working byte then write it out and clear it.
            if(bits == 0){
                flush();
            }
        }
    }
 
    protected void flush(){
        try{
            out.write(currentByte);
            bits = 8;
            currentByte = 0;
        }catch(IOException ex){
            System.out.println("Error while flushing bit stream: " + ex.toString());
        }
    }
 
    public void close(){
        flush();
        try{
            out.close();
        }catch(IOException ex){
            System.out.println("Error while closing bit stream: " + ex.toString());
        }
    }
 
    public int getPendingBits(){
        return bits;
    }
}