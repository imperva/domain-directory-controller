package com.imperva.opensource.ddc.core.query;

public interface Status{
    String getMessage();
    boolean isError();
    Exception getError();
}