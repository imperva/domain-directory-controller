package com.imperva.opensource.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/27/2017.
 */
public class ParsingException extends BaseException {
    public ParsingException(String error, Throwable innerException) {
        super(error, innerException);
    }

    public ParsingException(Throwable innerException) {
        super(innerException);
    }

    public ParsingException(String error) {
        super(error);
    }

    public ParsingException(String error, String reason, Throwable innerException) {
        super(error, reason, innerException);
    }

    public ParsingException(String error, String reason) {
        super(error, reason);
    }
}
