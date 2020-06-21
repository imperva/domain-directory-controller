package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.*;


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
        this.searchCriteriaBuilder.translateSortKeys();
    }

    @Override
    public void build(ChangeRequest changeRequest) {
        this.changeCriteriaBuilder = changeRequestBuilderGetInstance(changeRequest.getDirectoryType());
        this.changeCriteriaBuilder.setChangeRequest(changeRequest);
        this.changeCriteriaBuilder.translateChangeFields();
    }

    @Override
    public void build(RemoveRequest removeRequest) {
        this.removeCriteriaBuilder = removeRequestBuilderGetInstance(removeRequest.getDirectoryType());
        this.removeCriteriaBuilder.setRemoveRequest(removeRequest);
        this.removeCriteriaBuilder.translateFields();
    }

    @Override
    public void build(AddRequest addRequest) {
        this.addCriteriaBuilder = addRequestBuilderGetInstance(addRequest.getDirectoryType());
        this.addCriteriaBuilder.setAddRequest(addRequest);
        this.addCriteriaBuilder.translateFields();
    }

    @Override
    public <T> T get() {
        if (this.searchCriteriaBuilder != null) {
            return (T)this.searchCriteriaBuilder.get();
        }


        if (this.changeCriteriaBuilder != null) {
            return (T)this.changeCriteriaBuilder.get();
        }

        if (this.addCriteriaBuilder != null) {
            return (T)this.addCriteriaBuilder.get();
        }

        if (this.removeCriteriaBuilder != null) {
            return (T)this.removeCriteriaBuilder.get();
        }
        throw new UnsupportedOperationException();
    }



    //todo consider AbstractFactory here instead of all this factories
    SearchCriteriaBuilder searchCriteriaBuilderGetInstance(DirectoryType directoryType){
        return SearchCriteriaFactory.create(directoryType);
    }

    ChangeCriteriaBuilder changeRequestBuilderGetInstance(DirectoryType directoryType){
        return ChangeRequestBuilderFactory.create(directoryType);
    }

    AddCriteriaBuilder addRequestBuilderGetInstance(DirectoryType directoryType){
        return AddRequestBuilderFactory.create(directoryType);
    }

    RemoveCriteriaBuilder removeRequestBuilderGetInstance(DirectoryType directoryType){
        return RemoveRequestBuilderFactory.create(directoryType);
    }
}
