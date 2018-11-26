package com.imperva.ddc.core.query;

import org.apache.commons.lang.NotImplementedException;

public class ChangeRequestBuilderFactory {

    public static ChangeCriteriaBuilder create(DirectoryType directoryType){
        ChangeCriteriaBuilder changeCriteriaBuilder;
        switch (directoryType) {
            case MS_ACTIVE_DIRECTORY:
                changeCriteriaBuilder = new ChangeCriteriaBuilder();
                break;
            default:
                throw new NotImplementedException();
        }
        return changeCriteriaBuilder;
    }
}
