package com.imperva.ddc.core;

import com.imperva.ddc.core.exceptions.AddRequestFailedException;
import com.imperva.ddc.core.exceptions.BaseException;
import com.imperva.ddc.core.exceptions.RemoveRequestFailedException;
import com.imperva.ddc.core.query.*;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Shiran.Hersonsky on 05/07/2015.
 */
public class AddRequestExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddRequestExecutor.class.getName());
    AddRequest addRequest;

    public AddRequestExecutor(AddRequest addRequest) {
        this.addRequest = addRequest;
    }

    void execute() {
        Endpoint endpoint = addRequest.getEndpoint();
        String host = endpoint.getHost();
        try {
            LOGGER.debug("Executing request for: " + host);

            // Connection for each endpoint
            LdapConnectionResult ldapConnectionResult = driverGetInstance().connect(endpoint);

          //TODO  ? ldapConnectionResult.getConnection().add();

        } catch (LdapException | BaseException e) {
            LOGGER.error("Add Execution failed for Endpoint: " + host, e);
            endpoint.setDestinationType(DestinationType.NONE);
            throw new AddRequestFailedException(e.getMessage(), host);
        }
    }

    DriverBase driverGetInstance() {
        return new DriverHostResolverDecorator(new DriverRobustDecorator(new Driver()));
    }
}
