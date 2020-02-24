package com.galago.tests;

/**
 * test
 *
 * @author nidebruyn
 */
public class ExceptionTest {

    public static void main(String[] args) {
        
        try {
            
            System.out.println("Hello world");
            throw new RuntimeException("Throw me");
            
        } catch (Exception e) {
            System.out.println("Exception");
            return;
            
        } finally {
            System.out.println("Finally");
            
        }        

    }

}
