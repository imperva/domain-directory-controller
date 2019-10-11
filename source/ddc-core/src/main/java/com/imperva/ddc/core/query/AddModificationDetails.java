package com.imperva.ddc.core.query;

public class AddModificationDetails extends ModificationDetails {

    private String value;

    public AddModificationDetails(String dn, Field attribute, String value) {
        super(dn, attribute, Operation.ADD);
        this.value=value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
