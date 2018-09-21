package com.imperva.ddc.core.query;

import org.apache.commons.lang.NotImplementedException;

public class ChangeRequestBuilderFactory {

    public static ChangeRequestBuilder create(DirectoryType directoryType){
        ChangeRequestBuilder changeRequestBuilder;
        switch (directoryType) {
            case MS_ACTIVE_DIRECTORY:
                changeRequestBuilder = new ChangeRequestBuilder();
                break;
            default:
                throw new NotImplementedException();
        }
        return changeRequestBuilder;
    }
}
