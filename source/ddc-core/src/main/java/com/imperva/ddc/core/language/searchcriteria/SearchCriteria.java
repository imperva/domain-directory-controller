package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.Field;

import java.util.List;

/**
 * Created by gabi.beyo on 28/06/2015.
 */
public class SearchCriteria {
    private List<Field> requestedFields;
    private String searchFilter;

    public List<Field> getRequestedFields() { return requestedFields; }
    public void setRequestedFields(List<Field> requestedFields) { this.requestedFields = requestedFields; }

    public String getSearchFilter() { return searchFilter; }
    public void setSearchFilter(String searchFilter) { this.searchFilter = searchFilter; }

    @Override
    public String toString(){
        return searchFilter;
    }
}
