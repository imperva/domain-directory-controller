package com.imperva.ddc.core.query;

import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;

import java.util.List;


/**
 * Created by gabi.beyo on 01/07/2015.
 */
public class RoundtripResult {
    private List<Entry> data;
    private SearchCursor searchCursor;

    public List<Entry> getData() {
        return data;
    }
    public void setData(List<Entry> data) {
        this.data = data;
    }

    public SearchCursor getSearchCursor() {
        return searchCursor;
    }

    public void setSearchCursor(SearchCursor searchCursor) {
        this.searchCursor = searchCursor;
    }
}
