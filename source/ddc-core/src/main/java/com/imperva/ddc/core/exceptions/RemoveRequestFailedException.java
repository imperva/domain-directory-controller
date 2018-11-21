package com.imperva.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 11/7/2016.
 */
public class RemoveRequestFailedException extends BaseException {
    private String reason = "RemoveRequestFailedException Removing AD object - Please make sure you have appropriate credentials";

    private String host;

    public RemoveRequestFailedException(String error) {
        super(error);
    }

    public RemoveRequestFailedException(String error, String host) {
        super(error);
        setHost(host);
    }

    public RemoveRequestFailedException(String error, String host, String reason) {
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
