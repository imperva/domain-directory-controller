package com.imperva.ddc.core.query;


import org.apache.commons.lang3.NotImplementedException;

public class AddRequestBuilderFactory {

    public static AddCriteriaBuilder create(DirectoryType directoryType){
        AddCriteriaBuilder addCriteriaBuilder;
        switch (directoryType) {
            case MS_ACTIVE_DIRECTORY:
                addCriteriaBuilder = new AddCriteriaBuilder();
                break;
            default:
                throw new NotImplementedException("Not implemented yet");
        }
        return addCriteriaBuilder;
    }
}
