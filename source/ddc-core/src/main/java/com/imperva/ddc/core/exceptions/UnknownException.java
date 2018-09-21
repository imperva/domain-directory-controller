package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/6/2016.
 */
public class UnknownException extends BaseException  {

    public UnknownException(String error, Throwable innerException){
        super(error,innerException);
    }

    public UnknownException(Throwable innerException){
        super(innerException);
    }

    public UnknownException(String error){
        super(error);
    }

    public UnknownException(String error, String reason, Throwable innerException){
        super(error,innerException);
        this.setReason(reason);
    }

    public UnknownException(String error, String reason){
        super(error);
        this.setReason(reason);
    }
}
