package com.imperva.ddc.core.query;

/**
 * Created by Shiran.Hersonsky on 19/11/2018.
 */

public class AddModificationDetails extends ModificationDetails {

    public AddModificationDetails(String dn, Field attribute){
        super(dn, attribute, Operation.ADD);
    }
}
