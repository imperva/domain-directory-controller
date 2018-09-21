package com.imperva.ddc.core.query;

public class HoYeah implements Status {

    @Override
    public String getMessage() {
        return "Rock'n Roll";
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public Exception getError() {
        return null;
    }
}