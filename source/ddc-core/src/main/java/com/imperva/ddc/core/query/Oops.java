package com.imperva.ddc.core.query;

public class Oops implements Status {

    private String message;
    private Exception exception;

    public Oops(Exception e){
        this.message = e.toString();
        this.exception = e;
    }
    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public Exception getError() {
        return exception;
    }
}