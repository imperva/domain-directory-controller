package com.imperva.ddc.core;

import com.imperva.ddc.core.exceptions.BaseException;
import com.imperva.ddc.core.exceptions.ChangeRequestFailedException;
import com.imperva.ddc.core.exceptions.RemoveRequestFailedException;
import com.imperva.ddc.core.query.*;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Shiran.Hersonsky on 05/07/2015.
 */
public class RemoveRequestExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRequestExecutor.class.getName());
    RemoveRequest removeRequest;

    public RemoveRequestExecutor(RemoveRequest removeRequest) {
        this.removeRequest = removeRequest;
    }

    void execute() {
        Endpoint endpoint = removeRequest.getEndpoint();
        String host = endpoint.getHost();
        try {
            LOGGER.debug("Executing request for: " + host);

            // Connection for each endpoint
            LdapConnectionResult ldapConnectionResult = driverGetInstance().connect(endpoint);

            ldapConnectionResult.getConnection().delete(removeRequest.getDn());

        } catch (LdapException | BaseException e) {
            LOGGER.error("Remove Execution failed for Endpoint: " + host, e);
            endpoint.setDestinationType(DestinationType.NONE);
            throw new RemoveRequestFailedException(e.getMessage(), host);
        }
    }

    DriverBase driverGetInstance() {
        return new DriverHostResolverDecorator(new DriverRobustDecorator(new Driver()));
    }
}
