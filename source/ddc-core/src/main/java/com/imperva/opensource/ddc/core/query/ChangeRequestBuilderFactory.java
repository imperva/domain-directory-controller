package com.imperva.opensource.ddc.core.query;

import com.imperva.opensource.ddc.core.language.searchcriteria.ActiveDirectorySearchCriteriaBuilderImpl;
import com.imperva.opensource.ddc.core.language.searchcriteria.SearchCriteriaBuilder;
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
