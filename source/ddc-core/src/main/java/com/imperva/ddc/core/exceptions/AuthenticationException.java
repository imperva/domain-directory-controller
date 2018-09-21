package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/6/2016.
 */
public class AuthenticationException extends BaseException {

    private String reason = "Authentication Exception is usually caused by malconfigured credentials. Please make sure the Distinguished Name (DN) or Domain\\UserName and Password are correct";

    public AuthenticationException(String error, Throwable innerException) {
        super(error, innerException);
    }

    public AuthenticationException(Throwable innerException) {
        super(innerException);
    }

    public AuthenticationException(String error) {
        super(error);
    }

    public AuthenticationException(String error, String reason, Throwable innerException) {
        super(error, innerException);
        this.setReason(reason);
    }

    public AuthenticationException(String error, String reason) {
        super(error);
        this.setReason(reason);
    }


    public String getReason() {
        return reason;
    }
}
