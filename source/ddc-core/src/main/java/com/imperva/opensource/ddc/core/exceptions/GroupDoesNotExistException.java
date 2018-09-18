package com.imperva.opensource.ddc.core.exceptions;

/**
 * User: mors
 * Date: 8/31/2016
 */
public class GroupDoesNotExistException extends BaseException {
    public GroupDoesNotExistException(Throwable innerException){
        super(innerException);
    }

    public GroupDoesNotExistException(String error) {
        super(error);
    }
}
