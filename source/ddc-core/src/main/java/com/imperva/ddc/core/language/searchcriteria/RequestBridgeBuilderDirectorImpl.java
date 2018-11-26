package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public void build(RemoveRequest removeRequest) {
        this.removeRequestBuilder = removeRequestBuilderGetInstance(removeRequest.getDirectoryType());
        this.removeRequestBuilder.setRemoveRequest(removeRequest);
        this.removeRequestBuilder.translateFields();
    }

    @Override
    public void build(AddRequest addRequest) {
        this.addRequestBuilder = addRequestBuilderGetInstance(addRequest.getDirectoryType());
        this.addRequestBuilder.setAddRequest(addRequest);
        this.addRequestBuilder.translateFields();
    }

    @Override
    public <T> T get() {
        if (this.searchCriteriaBuilder != null) {
            return (T)this.searchCriteriaBuilder.get();
        }


        if (this.changeRequestBuilder != null) {
            return (T)this.changeRequestBuilder.get();
        }

        if (this.addRequestBuilder != null) {
            return (T)this.addRequestBuilder.get();
        }

        if (this.removeRequestBuilder != null) {
            return (T)this.removeRequestBuilder.get();
        }
        throw new NotImplementedException();
    }



    //todo consider AbstractFactory here instead of all this factories
    SearchCriteriaBuilder searchCriteriaBuilderGetInstance(DirectoryType directoryType){
        return SearchCriteriaFactory.create(directoryType);
    }

    ChangeRequestBuilder changeRequestBuilderGetInstance(DirectoryType directoryType){
        return ChangeRequestBuilderFactory.create(directoryType);
    }

    AddRequestBuilder addRequestBuilderGetInstance(DirectoryType directoryType){
        return AddRequestBuilderFactory.create(directoryType);
    }

    RemoveRequestBuilder removeRequestBuilderGetInstance(DirectoryType directoryType){
        return RemoveRequestBuilderFactory.create(directoryType);
    }
}
