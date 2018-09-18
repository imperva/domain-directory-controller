package com.imperva.opensource.ddc.core.query;

import com.imperva.opensource.ddc.core.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 01/07/2015.
 */
public class Cursor {
    private List<Endpoint> endpoints = new ArrayList<Endpoint>();
    private Connector connector;

    public Cursor(Connector connector) {
        this.connector = connector;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    public void forgetPosition() {
        for (Endpoint endpoint : this.endpoints) {
            endpoint.hasNext(CursorStatus.STARTING);
        }
    }

    public QueryResponse next() {
        return this.connector.execute();
    }

    public boolean hasNext() {
        boolean hasNext = false;
        for (Endpoint endpoint : this.endpoints) {
            if (endpoint.hasNext() != CursorStatus.EOF) {
                return true;
            }
        }
        return hasNext;
    }
}
