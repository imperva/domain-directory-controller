package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.Field;
import com.imperva.ddc.core.query.FieldInfo;
import com.imperva.ddc.core.query.SortKey;

import java.util.Iterator;
import java.util.List;

/**
 * Created by gabi.beyo on 18/06/2015.
 * Builder
 */
public class ActiveDirectorySearchCriteriaBuilderImpl extends SearchCriteriaBuilder {

    private List<Field> translatedRequestedFields;
    private List<SortKey> translatedRequestedSortKeys;
    private String searchFilter;

    @Override
    public void translateFields() {
        List<Field> fields = getQueryRequest().getRequestedFields();
        for (Iterator<Field> i = fields.iterator(); i.hasNext(); ) {
            Field field = i.next();
            translateField(field);
        }
        translatedRequestedFields = fields;
    }

    @Override
    public void translateSortKeys() {
        List<SortKey> sortKeys = getQueryRequest().getSortKeys();
        for (SortKey sortKey : sortKeys) {
        	translateField(sortKey);
        }
        translatedRequestedSortKeys = sortKeys;
    }

    @Override
    public void translateFilter() {

        String searchTest = getQueryRequest().getSearchText();
        if (searchTest != null && !searchTest.isEmpty()) {
            this.searchFilter = searchTest;
            return;
        }
        this.searchFilter = translateFilter(getQueryRequest().getSearchSentence());
    }

    @Override
    public String translateField(FieldInfo field) {
        if (field.getType() == null) {
            return field.getName();
        }
        String result;
        switch (field.getType()) {
            case GROUP_RECURSIVE:
                result = "member:1.2.840.113556.1.4.1941:";
                break;
            case GROUP_MEMBER_OF_RECURSIVE:
                result = "memberof:1.2.840.113556.1.4.1941:";
                break;
            case USER_ACCOUNT_CONTROL_FILTER:
                result="UserAccountControl:1.2.840.113556.1.4.803:";
                break;
            default:
                result = super.translateField(field);
        }

        field.setName(result);
        return result;
    }


    @Override
    public SearchCriteria get() {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setSearchFilter(this.searchFilter);
        searchCriteria.setRequestedFields(this.translatedRequestedFields);
        searchCriteria.setRequestedSortKeys(this.translatedRequestedSortKeys);
        return searchCriteria;
    }


}
