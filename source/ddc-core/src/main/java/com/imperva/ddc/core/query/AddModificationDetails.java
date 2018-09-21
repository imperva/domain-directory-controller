package com.imperva.ddc.core.query;

import org.apache.directory.api.ldap.model.name.Dn;


public class AddModificationDetails extends ModificationDetails {

    private String value;

    public AddModificationDetails(String dn, Field attribute, String value) {
        super(dn, attribute, Operation.ADD);
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
