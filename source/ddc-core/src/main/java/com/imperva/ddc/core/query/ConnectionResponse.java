package com.imperva.ddc.core.query;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mors
 * Date: 7/20/2016
 */
public class ConnectionResponse {

    Map<String, Status> statuses;

    /**
     * @return A key-value list of connection's results, where the key is the endpoint's IP and the value results to
     * true if connection succeeded otherwise false
     */
    public Map<String, Boolean> getConnectionResultByHost() {
        Map<String, Boolean> connectionResultByHost = new HashMap<>();
        for(Map.Entry<String, Status> entry : statuses.entrySet()) {
            if(entry.getValue().isError()) {
                connectionResultByHost.put(entry.getKey(), false);
            } else {
                connectionResultByHost.put(entry.getKey(), true);
            }
        }
        return connectionResultByHost;
    }

    /**
     * @return A key-value list of connection's results, where the key is the endpoint's IP and the value results to
     * null if connection succeeded otherwise the thrown Exception {@link Exception}
     */
    public Map<String, Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(Map<String, Status> statuses) {
        this.statuses = statuses;
    }

    /**
     * @return True if connection result contains at least one Exception {@link Exception}, otherwise null
     */
    public boolean hasError() {
        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
            if (entry.getValue().isError()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return True if connection's results contains at least one succeeded connection, otherwise false
     */
    public boolean isError() {
        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
            if (entry.getValue().isError()) {
                return false;
            }
        }
        return true;
    }
}
