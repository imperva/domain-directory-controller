package com.imperva.opensource.ddc.core.exceptions;

/**
 * Created by gabi.beyo on 11/7/2016.
 */
public class ChangeRequestFailedException extends BaseException {
    private String reason = "ChangeRequestFailedException usually caused by adding- fields that already exist, remove- fileds that are";

    private String host;

    public ChangeRequestFailedException(String error) {
        super(error);
    }

    public ChangeRequestFailedException(String error, String host) {
        super(error);
        setHost(host);
    }

    public ChangeRequestFailedException(String error, String host, String reason) {
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
