package com.imperva.ddc.core.query;

/**
 * Created by Shiran.Hersonsky on 19/11/2018.
 */

public  class AddRequest extends Request {
    private String dn;
    private Endpoint endpoint;


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
}

