package com.imperva.ddc.core.query;

import java.util.List;

public class RemoveCriteriaBuilder extends RequestBuilder {

    private RemoveRequest removeRequest;

    List<Field> translatedFields;
    String translatedDN = null;

    public RemoveRequest getRemovedRequest() {
        return removeRequest;
    }

    public void setRemoveRequest(RemoveRequest removeRequest) {
        this.removeRequest = removeRequest;
    }

    public void translateFields() {
        //todo here or in translateFilter?
        Field field = new Field();
        field.setType(FieldType.DISTINGUISHED_NAME);
        translatedDN = escapeSpecialChars(getRemovedRequest().getDn(),field);
    }


    public RemoveCriteria get() {
        RemoveCriteria removeCriteria = new RemoveCriteria();
        removeCriteria.setTranslatedDN(translatedDN);
        return removeCriteria;
    }

    @Override
    public void translateFilter() {
         throw new UnsupportedOperationException();
    }
}
