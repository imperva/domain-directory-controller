package com.imperva.ddc.core.query;

import org.apache.commons.lang.NotImplementedException;

public class RemoveRequestBuilderFactory {

    public static RemoveCriteriaBuilder create(DirectoryType directoryType){
        RemoveCriteriaBuilder removeCriteriaBuilder;
        switch (directoryType) {
            case MS_ACTIVE_DIRECTORY:
                removeCriteriaBuilder = new RemoveCriteriaBuilder();
                break;
            default:
                throw new NotImplementedException();
        }
        return removeCriteriaBuilder;
    }
}
