/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

/**
 *
 * @author nidebruyn
 */
public class TouchKeyNames {
    
private static final String[] KEY_NAMES = new String[0xFF];
    
    static {
        KEY_NAMES[7] = "0";
        KEY_NAMES[8] = "1";
        KEY_NAMES[9] = "2";
        KEY_NAMES[10] = "3";
        KEY_NAMES[11] = "4";
        KEY_NAMES[12] = "5";
        KEY_NAMES[13] = "6";
        KEY_NAMES[14] = "7";
        KEY_NAMES[15] = "8";
        KEY_NAMES[16] = "9";
        
        KEY_NAMES[45] = "Q";
        KEY_NAMES[51] = "W";
        KEY_NAMES[33] = "E";
        KEY_NAMES[46] = "R";
        KEY_NAMES[48] = "T";
        KEY_NAMES[53] = "Y";
        KEY_NAMES[49] = "U";
        KEY_NAMES[37] = "I";
        KEY_NAMES[43] = "O";
        KEY_NAMES[44] = "P";
        KEY_NAMES[29] = "A";
        KEY_NAMES[47] = "S";
        KEY_NAMES[32] = "D";
        KEY_NAMES[34] = "F";
        KEY_NAMES[35] = "G";
        KEY_NAMES[36] = "H";
        KEY_NAMES[38] = "J";
        KEY_NAMES[39] = "K";
        KEY_NAMES[40] = "L";
        KEY_NAMES[54] = "Z";
        KEY_NAMES[52] = "X";
        KEY_NAMES[31] = "C";
        KEY_NAMES[50] = "V";
        KEY_NAMES[30] = "B";
        KEY_NAMES[42] = "N";
        KEY_NAMES[41] = "M";
        
        
        KEY_NAMES[67] = "Backspace";
        KEY_NAMES[62] = "Space";
        KEY_NAMES[59] = "Shift";
        KEY_NAMES[66] = "Enter";
        
        KEY_NAMES[69] = "-";
        KEY_NAMES[70] = "=";
        KEY_NAMES[71] = "[";
        KEY_NAMES[72] = "]";
        KEY_NAMES[74] = ";";
        KEY_NAMES[75] = "'";
        KEY_NAMES[68] = "`";
        KEY_NAMES[73] = "\\";
        KEY_NAMES[55] = ",";
        KEY_NAMES[56] = ".";
        KEY_NAMES[76] = "/";
//        KEY_NAMES[15] = "*";
        KEY_NAMES[70] = "+";
        KEY_NAMES[74] = ":";
        KEY_NAMES[69] = "_";
//        KEY_NAMES[9] = "@";

    }
    
    public String getName(int keyId){
        return KEY_NAMES[keyId];
    }
    
}
