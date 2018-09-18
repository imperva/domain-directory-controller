package com.imperva.opensource.ddc.core.exceptions;

/**
 * User: mors
 * Date: 12/13/2016
 */
public class InvalidAuthenticationInfoException extends BaseException {
    private String reason = "Authentication Exception is usually caused by malconfigured credentials. Please make sure the Distinguished Name (DN) or Domain\\UserName and Password are correct";

    public InvalidAuthenticationInfoException(String error, Throwable innerException) {
        super(error, innerException);
    }

    public InvalidAuthenticationInfoException(Throwable innerException) {
        super(innerException);
    }

    public InvalidAuthenticationInfoException(String error) {
        super(error);
    }

    public InvalidAuthenticationInfoException(String error, String reason, Throwable innerException) {
        super(error, innerException);
        this.setReason(reason);
    }

    public InvalidAuthenticationInfoException(String error, String reason) {
        super(error);
        this.setReason(reason);
    }


    public String getReason() {
        return reason;
    }
}
