package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/6/2016.
 */
public class InvalidConnectionException extends BaseException  {

    private String reason = "Connection Exception is usually caused by malconfigured IP / Port address. Please make sure the IP address and Port are well configured and the Active Directory is reachable";

    public InvalidConnectionException(String error, Throwable innerException){
        super(error,innerException);
    }

    public InvalidConnectionException(Throwable innerException){
        super(innerException);
    }

    public InvalidConnectionException(String error){
        super(error);
    }

    public InvalidConnectionException(String error, String reason, Throwable innerException){
        super(error,innerException);
        this.setReason(reason);
    }

    public InvalidConnectionException(String error, String reason){
        super(error);
        this.setReason(reason);
    }

    public String getReason() {
        return reason;
    }
}
