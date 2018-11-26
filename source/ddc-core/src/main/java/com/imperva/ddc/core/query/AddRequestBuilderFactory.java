package com.imperva.ddc.core.query;

import org.apache.commons.lang.NotImplementedException;

public class AddRequestBuilderFactory {

    public static AddRequestBuilder create(DirectoryType directoryType){
        AddRequestBuilder addRequestBuilder;
        switch (directoryType) {
            case MS_ACTIVE_DIRECTORY:
                addRequestBuilder = new AddRequestBuilder();
                break;
            default:
                throw new NotImplementedException();
        }
        return addRequestBuilder;
    }
}
