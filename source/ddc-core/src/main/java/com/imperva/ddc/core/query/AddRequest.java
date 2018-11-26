package com.imperva.ddc.core.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shiran.Hersonsky on 19/11/2018.
 */

public  class AddRequest extends Request {
    private String dn;
    private Endpoint endpoint;
    private List<Field> fields = new ArrayList<>();


    public AddRequest(String dn) {
        this.dn = dn;
    }

    @Override
    public void close() {
        if (endpoint != null) {
            endpoint.close();
            endpoint.setLdapConnection(null);
            endpoint.setDestinationType(DestinationType.NONE);
        }
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public AddRequest addField(Field field){
        fields.add(field);
        return this;
    }
}

