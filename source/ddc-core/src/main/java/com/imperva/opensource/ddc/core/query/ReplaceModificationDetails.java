package com.imperva.opensource.ddc.core.query;

import org.apache.directory.api.ldap.model.name.Dn;


public class ReplaceModificationDetails extends ModificationDetails {

    private String value;

    public ReplaceModificationDetails(String dn, Field attribute, String value){
        super(dn, attribute, Operation.REPLACE);
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
