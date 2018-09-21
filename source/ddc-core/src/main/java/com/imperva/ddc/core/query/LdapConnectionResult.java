package com.imperva.ddc.core.query;

import com.imperva.ddc.core.commons.Utils;
import org.apache.directory.ldap.client.api.LdapConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mors
 * Date: 7/19/2016
 */
public class LdapConnectionResult {
    LdapConnection connection;
    DestinationType destinationType;

    /**
     * Key: host or IP
     * Value: connection exception
     */
    Map<String, Status> statuses;

    public LdapConnectionResult() {
        statuses = new HashMap<>();
    }

    public void setDestinationType(DestinationType destinationType){
        this.destinationType = destinationType;
    }

    public DestinationType getDestinationType(){
        return this.destinationType;
    }

    public boolean hasError() {
        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
            if (entry.getValue().isError()) {
                return true;
            }
        }

        return false;
    }

    public LdapConnection getConnection() {
        return connection;
    }

    public void setConnection(LdapConnection connection) {
        this.connection = connection;
    }

    public Map<String, Status> getStatuses() {
        return statuses;
    }

    public void addStatus(String host, Status status) {
        if (Utils.isEmpty(host) || status == null)
            throw new RuntimeException("Host and Status can't be empty");
        this.statuses.put(host, status);
    }

    public void setStatuses(Map<String, Status> statuses) {
        this.statuses = statuses;
    }

    public boolean connectionSucceeded() {
        return connection != null && connection.isConnected();
    }

    public void addStatus(Map<String, Status> statuses) {
        this.statuses.putAll(statuses);
    }
}
