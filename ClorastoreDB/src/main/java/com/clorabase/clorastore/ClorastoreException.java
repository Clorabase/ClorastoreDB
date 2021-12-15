package com.clorabase.clorastore;

public class ClorastoreException extends RuntimeException {
    private Reasons reasons;

    protected ClorastoreException(String msg,Reasons reasons){
        super(msg);
    }


    public Reasons getReasons() {
        return reasons;
    }
}
