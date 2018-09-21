package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 11/7/2016.
 */
public class InvalidDnException extends BaseException {
    private String reason = "Caused by Invalid DN, Bad format";

    public InvalidDnException(String error) {
        super(error);
    }


    public InvalidDnException(String error, String host, String reason) {
        super(error);
        setReason(reason);
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }


}
