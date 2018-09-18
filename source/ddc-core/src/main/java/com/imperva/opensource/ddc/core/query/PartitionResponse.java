package com.imperva.opensource.ddc.core.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gabi.beyo on 6/8/2016.
 * LDAP queries can be fetched from different endpoints.
 * The PartitionResponse class encapsulates the results for each endpoint
 */
public class PartitionResponse {
    private Map<String, Status> status;
    private Endpoint endpoint;
    private List<EntityResponse> data = new ArrayList<EntityResponse>();

    public PartitionResponse() {
        status = new HashMap<>();
    }

    /**
     * @return The total value size
     */
    public Integer getSize() {
        return this.data.size();
    }

    /**
     * @return A list of {@link EntityResponse} objects, which contains a kye-value representation of the query result
     */
    public List<EntityResponse> getData() {
        return data;
    }

    /**
     * @param data A list of {@link EntityResponse} objects, which contains a kye-value representation of the query result
     */
    public void setData(List<EntityResponse> data) {
        this.data = data;
    }

    public void addData(EntityResponse entResponse) {
        this.data.add(entResponse);
    }


    /**
     * @return The queried endpoint of a specific partition partition {@link Endpoint}
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Extracts a key-value list containing exceptions accrued during the LDAP query
     * The key value is the actual endpoint's IP and the value is the Actual Exception object {@link Exception} or null in case nu Exception was raised
     *
     * @return A key-value list containing exceptions accrued during the LDAP query
     */
    public Map<String, Status> getStatus() {
        return status;
    }

    /**
     * Iterates over the Exception list and returns the one with the matching endpoint's IP
     *
     * @param host The IP of the requested endpoint
     * @return The Exception {@link Exception} object or null in case no Exception was raised
     */
    public Status getStatus(String host) {
        for (Map.Entry<String, Status> entry : status.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(host) && entry.getValue() != null) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void addStatus(String host, Status status) {
        this.status.put(host, status);
    }

    public void setStatus(Map<String, Status> status) {
        this.status = status;
    }

    /**
     * Iterates over the Exception list and returns true if primary and/or secondary have failures
     *
     * @return true if both primary and secondary have failures
     */
    public boolean hasError() {
        for (Map.Entry<String, Status> entry : status.entrySet()) {
            if (entry.getValue().isError()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Iterates over the Exception list and returns true if both primary and secondary have failures
     *
     * @return true if both primary and secondary have failures
     */
    public boolean isAllError() {
        for (Map.Entry<String, Status> entry : status.entrySet()) {
            if (! entry.getValue().isError()) {
                return false;
            }
        }
        return true;
    }
}
