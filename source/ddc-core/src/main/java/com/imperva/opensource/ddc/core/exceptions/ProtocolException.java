package com.imperva.opensource.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/6/2016.
 */
public class ProtocolException extends BaseException  {

    private String reason = "Protocol Exception can be caused by malconfigured port address. Usually Port 389 is used for non-secured connections and Port 636 for secured connections";

    public ProtocolException(String error, Throwable innerException){
        super(error,innerException);
    }

    public ProtocolException(Throwable innerException){
        super(innerException);
    }

    public ProtocolException(String error){
        super(error);
    }

    public ProtocolException(String error, String reason, Throwable innerException){
        super(error,innerException);
        this.setReason(reason);
    }

    public ProtocolException(String error, String reason){
        super(error);
        this.setReason(reason);
    }

    public String getReason() {
        return reason;
    }
}
