package com.imperva.ddc.core.exceptions;

/**
 * User: mors
 * Date: 7/12/2016
 */
public class UnresolvableHostException extends BaseException {
    private String reason = "Unresolvable host exception is usually caused by malconfigured DSN. Please make sure that your DNS settings are correct.";

    public UnresolvableHostException(String error, Throwable innerException){
        super(error,innerException);
    }

    public UnresolvableHostException(Throwable innerException){
        super(innerException);
    }

    public UnresolvableHostException(String error){
        super(error);
    }

    public UnresolvableHostException(String error, String reason, Throwable innerException){
        super(error,innerException);
        this.setReason(reason);
    }

    public UnresolvableHostException(String error, String reason){
        super(error);
        this.setReason(reason);
    }

    public String getReason() {
        return reason;
    }

}
