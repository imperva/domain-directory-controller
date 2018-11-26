package com.imperva.ddc.core.query;

import java.util.Iterator;
import java.util.List;

public class AddRequestBuilder extends RequestBuilder {

    private AddRequest addRequest;

    List<Field> translatedFields;
    String translatedDN = null;

    public AddRequest getAddRequest() {
        return addRequest;
    }

    public void setAddRequest(AddRequest addRequest) {
        this.addRequest = addRequest;
    }

    public void translateFields() {
        List<Field> fields = getAddRequest().getFields();
        for (Iterator<Field> i = fields.iterator(); i.hasNext(); ) {
            Field field = i.next();
            translateField(field);
        }
        //todo Fix all Builders" fields are already altered inside translatedField method. field param should be immutable
        translatedFields = fields;

        Field field = new Field();
        field.setType(FieldType.DISTINGUISHED_NAME);
        //todo what values must we escape?
        translatedDN = escapeSpecialChars(getAddRequest().getDn(),field);
    }


    public AddCriteria get() {
        AddCriteria addCriteria = new AddCriteria();
        addCriteria.setFields(translatedFields);
        addCriteria.setTranslatedDN(translatedDN);
        return addCriteria;
    }

    @Override
    public void translateFilter() {
         throw new UnsupportedOperationException();
    }
}
