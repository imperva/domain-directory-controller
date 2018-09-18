package com.imperva.opensource.ddc.core.language.searchcriteria;

import com.imperva.opensource.ddc.core.query.*;

/**
 * Created by gabi.beyo on 18/06/2015.
 */
public class RequestBridgeBuilderDirectorImpl extends RequestBridgeBuilderDirector {


    @Override
    public void build(QueryRequest queryRequest) {
        this.searchCriteriaBuilder = searchCriteriaBuilderGetInstance(queryRequest.getDirectoryType());
        this.searchCriteriaBuilder.setQueryRequest(queryRequest);
        this.searchCriteriaBuilder.translateFields();
        this.searchCriteriaBuilder.translateFilter();
    }

    @Override
    public void build(ChangeRequest changeRequest) {
        this.changeRequestBuilder = changeRequestBuilderGetInstance(changeRequest.getDirectoryType());
        this.changeRequestBuilder.setChangeRequest(changeRequest);
        this.changeRequestBuilder.translateChangeFields();

    }

    @Override
    public <T> T get() {
        if (this.searchCriteriaBuilder == null) {
            if(this.changeRequestBuilder== null)
                return null;
            else
                return (T)this.changeRequestBuilder.get();
        }
        return (T)this.searchCriteriaBuilder.get();
    }



    SearchCriteriaBuilder searchCriteriaBuilderGetInstance(DirectoryType directoryType){
        return SearchCriteriaFactory.create(directoryType);
    }

    ChangeRequestBuilder changeRequestBuilderGetInstance(DirectoryType directoryType){
        return ChangeRequestBuilderFactory.create(directoryType);
    }
}
