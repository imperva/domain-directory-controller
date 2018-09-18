package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.exceptions.BaseException;
import com.imperva.opensource.ddc.core.exceptions.ChangeRequestFailedException;
import com.imperva.opensource.ddc.core.query.*;
import com.imperva.opensource.ddc.core.query.ModificationDetails;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
public class ChangeRequestExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeRequestExecutor.class.getName());
    ChangeRequest changeRequest;
    ApacheAPIConverter apacheAPIConverter= new ApacheAPIConverter();

    public ChangeRequestExecutor(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
    }

    void execute() {
            Endpoint endpoint=changeRequest.getEndpoint();
            String host = endpoint.getHost();
            try {
                LOGGER.debug("Executing request for: " + host);

                // Connection for each endpoint
                LdapConnectionResult ldapConnectionResult = driverGetInstance().connect(endpoint);

                for (ModificationDetails modificationDetails : changeRequest.getModificationDetailsList()) {
                    Modification modification = apacheAPIConverter.toModification(modificationDetails);

                    ldapConnectionResult.getConnection().modify(modificationDetails.getDn(), modification);
                }

            } catch (LdapException | BaseException e) {
                LOGGER.error("Change Execution failed for Endpoint: " + host, e);
                endpoint.setDestinationType(DestinationType.NONE);
                throw new ChangeRequestFailedException(e.getMessage(), host);
            }
    }

    DriverBase driverGetInstance() {
        return new DriverHostResolverDecorator(new DriverRobustDecorator(new Driver()));
    }
}
