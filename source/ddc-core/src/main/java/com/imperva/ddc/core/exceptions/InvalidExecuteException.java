package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 3/6/2016.
 */
public class InvalidExecuteException extends BaseException  {

    private String reason = "InvalidExecuteException usually happens when the Request object in the connector class doesn't match with the execute method"
            +"\n for queryRequest- call execute() \n for changeRequest- call executeChangeRequest()";

    public InvalidExecuteException(String error, Throwable innerException){
        super(error,innerException);
    }

    public InvalidExecuteException(Throwable innerException){
        super(innerException);
    }

    public InvalidExecuteException(String error){
        super(error);
    }

    public InvalidExecuteException(String error, String reason, Throwable innerException){
        super(error,innerException);
        this.setReason(reason);
    }

    public InvalidExecuteException(String error, String reason){
        super(error);
        this.setReason(reason);
    }

    public String getReason() {
        return reason;
    }
}
