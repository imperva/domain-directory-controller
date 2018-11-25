package com.imperva.ddc.core.query;

import com.imperva.ddc.core.exceptions.ParsingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shiran.Hersonsky on 19/11/2018.
 */

public  class RemoveRequest extends Request {
    private String dn;
    private Endpoint endpoint;


    public RemoveRequest(String dn) {
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
}

