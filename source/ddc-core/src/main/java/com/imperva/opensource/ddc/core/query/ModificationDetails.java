package com.imperva.opensource.ddc.core.query;

public class ModificationDetails {

    protected String dn;
    protected Field attribute;
    protected Operation operation;

    public ModificationDetails(String dn, Field attribute, Operation operation){
        this.dn=dn;
        this.attribute=attribute;
        this.operation=operation;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public Field getAttribute() {
        return attribute;
    }

    public void setAttribute(Field attribute) {
        this.attribute = attribute;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setAttributeName(String name){
        attribute.setName(name);
    }
}
