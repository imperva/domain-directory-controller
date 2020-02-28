package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.Field;
import com.imperva.ddc.core.query.SortKey;

import java.util.List;

/**
 * Created by gabi.beyo on 28/06/2015.
 */
public class SearchCriteria {
    private List<Field> requestedFields;
    private List<SortKey> requestedSortKeys;
    private String searchFilter;

    public List<Field> getRequestedFields() { return requestedFields; }
    public void setRequestedFields(List<Field> requestedFields) { this.requestedFields = requestedFields; }

    public List<SortKey> getRequestedSortKeys() { return requestedSortKeys; }
    public void setRequestedSortKeys(List<SortKey> requestedSortKeys) { this.requestedSortKeys = requestedSortKeys; }

    public String getSearchFilter() { return searchFilter; }
    public void setSearchFilter(String searchFilter) { this.searchFilter = searchFilter; }

    @Override
    public String toString(){
        return searchFilter;
    }
}
