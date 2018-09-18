package com.imperva.opensource.ddc.core.exceptions;

/**
 * User: mors
 * Date: 12/12/2016
 */
public class UserDisabledException extends BaseException{
    private String reason = "UserDisabled Exception is caused by disabled user in Active Directory. Please make sure the user you are using is enabled";

    public UserDisabledException(String error, Throwable innerException) {
        super(error, innerException);
    }

    public UserDisabledException(Throwable innerException) {
        super(innerException);
    }

    public UserDisabledException(String error) {
        super(error);
    }

    public UserDisabledException(String error, String reason, Throwable innerException) {
        super(error, innerException);
        this.setReason(reason);
    }

    public UserDisabledException(String error, String reason) {
        super(error);
        this.setReason(reason);
    }


    public String getReason() {
        return reason;
    }
}
