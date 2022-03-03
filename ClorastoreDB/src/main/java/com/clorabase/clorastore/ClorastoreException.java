package com.clorabase.clorastore;

/**
 * An exception which is thrown when any database operation get failed.
 */
public class ClorastoreException extends RuntimeException {
    private Reasons reasons;

    protected ClorastoreException(String msg,Reasons reasons){
        super(msg);
    }


    public Reasons getReasons() {
        return reasons;
    }
}
