package com.imperva.ddc.core.query;

public class RemoveModificationDetails extends ModificationDetails {

    public RemoveModificationDetails(String dn, Field attribute){
        super(dn, attribute, Operation.REMOVE);
    }
}
