package com.imperva.ddc.core.exceptions;

/**
 * Created by Shiran.Hersonsky on 11/7/2016.
 */
public class AddRequestFailedException extends BaseException {
    private String reason = "AddRequestFailedException: Adding AD object - Please make sure you have appropriate credentials";

    private String host;

    public AddRequestFailedException(String error) {
        super(error);
    }

    public AddRequestFailedException(String error, String host) {
        super(error);
        setHost(host);
    }

    public AddRequestFailedException(String error, String host, String reason) {
        super(error);
        setHost(host);
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
