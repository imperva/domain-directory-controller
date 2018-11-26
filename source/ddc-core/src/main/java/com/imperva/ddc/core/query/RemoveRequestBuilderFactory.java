package com.imperva.ddc.core.query;

import org.apache.commons.lang.NotImplementedException;

public class RemoveRequestBuilderFactory {

    public static RemoveRequestBuilder create(DirectoryType directoryType){
        RemoveRequestBuilder removeRequestBuilder;
        switch (directoryType) {
            case MS_ACTIVE_DIRECTORY:
                removeRequestBuilder = new RemoveRequestBuilder();
                break;
            default:
                throw new NotImplementedException();
        }
        return removeRequestBuilder;
    }
}
