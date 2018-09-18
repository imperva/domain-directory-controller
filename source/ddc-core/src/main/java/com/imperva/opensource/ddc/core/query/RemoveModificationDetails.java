package com.imperva.opensource.ddc.core.query;

import org.apache.directory.api.ldap.model.name.Dn;


public class RemoveModificationDetails extends ModificationDetails {

    public RemoveModificationDetails(String dn, Field attribute){
        super(dn, attribute, Operation.REMOVE);
    }
}
