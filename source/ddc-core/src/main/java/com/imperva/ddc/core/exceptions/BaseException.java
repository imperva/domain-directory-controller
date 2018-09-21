package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/6/2016.
 */
public class BaseException extends RuntimeException  {

    private String reason = "";

    public BaseException(String error, Throwable innerException){
        super(error,innerException);
    }

    public BaseException(Throwable innerException){
        super(innerException);
    }

    public BaseException(String error){
        super(error);
    }

    public BaseException(String error, String reason, Throwable innerException){
        super(error,innerException);
        this.setReason(reason);
    }

    public BaseException(String error, String reason){
        super(error);
        this.setReason(reason);
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
