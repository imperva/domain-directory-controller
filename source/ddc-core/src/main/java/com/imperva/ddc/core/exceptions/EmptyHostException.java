package com.imperva.ddc.core.exceptions;

/**
 * User: mors
 * Date: 7/14/2016
 */
public class EmptyHostException extends BaseException{

    public EmptyHostException(Throwable innerException){
        super(innerException);
    }

    public EmptyHostException(String error) {
        super(error);
    }
}
