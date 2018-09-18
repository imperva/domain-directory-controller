package com.imperva.opensource.ddc.core.language.searchcriteria;

import com.imperva.opensource.ddc.core.query.Field;
import com.imperva.opensource.ddc.core.query.ModificationDetails;

import java.util.Iterator;
import java.util.List;

/**
 * Created by gabi.beyo on 18/06/2015.
 * Builder
 */
public class ActiveDirectorySearchCriteriaBuilderImpl extends SearchCriteriaBuilder {

    private List<Field> translatedRequestedFields;
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
    public void translateFilter() {

        String searchTest = getQueryRequest().getSearchText();
        if (searchTest != null && !searchTest.isEmpty()) {
            this.searchFilter = searchTest;
            return;
        }
        this.searchFilter = translateFilter(getQueryRequest().getSearchSentence());
    }

    @Override
    public String translateField(Field field) {
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
            case USER_ACCOUNT_CONTROL:
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
        return searchCriteria;
    }


}
