package com.imperva.ddc.core.query;

import java.util.List;

/**
 * Created by gabi.beyo on 28/06/2015.
 */
public class AddCriteria {
    private List<Field> fields;
    private String translatedDN;


    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }


    public String getTranslatedDN() {
        return translatedDN;
    }

    public void setTranslatedDN(String translatedDN) {
        this.translatedDN = translatedDN;
    }
}
