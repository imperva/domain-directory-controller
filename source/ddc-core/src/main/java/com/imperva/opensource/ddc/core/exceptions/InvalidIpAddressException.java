package com.imperva.opensource.ddc.core.exceptions;

/**
 * User: mors
 * Date: 7/14/2016
 */
public class InvalidIpAddressException extends BaseException {

    public InvalidIpAddressException(Throwable innerException){
        super(innerException);
    }

    public InvalidIpAddressException(String error) {
        super(error);
    }
}
