package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.query.Endpoint;
import com.imperva.opensource.ddc.core.query.LdapConnectionResult;
import com.imperva.opensource.ddc.core.query.Status;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class DriverRobustDecorator extends DriverBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverRobustDecorator.class.getName());
    private DriverBase driverBase;

    DriverRobustDecorator(DriverBase driverBase) {
        this.driverBase = driverBase;
    }

    @Override
    LdapConnectionResult connect(Endpoint endpoint) {
        LdapConnectionResult ldapConnectionResult = driverBase.connect(endpoint);

        boolean connectionSucceeded = endpoint.isConnectionSucceeded();
        //*TODO: For now, use this method instead of isConnectionSucceeded.isConnectionSucceeded(). LdapConnectionResult flow should be fixed

        if (connectionSucceeded)
            return ldapConnectionResult;
        else
            return retry(ldapConnectionResult, endpoint);
    }

    private LdapConnectionResult retry(LdapConnectionResult ldapConnectionResult, Endpoint endpoint) {
        int numOfRetries = 3;
        int retryCounter = new Integer(numOfRetries);
        Map<String, Status> status = ldapConnectionResult.getStatuses();
        Status primaryStatus = status.get(endpoint.getHost());
        Status secondaryStatus = status.get(endpoint.getSecondaryHost());

        Exception toCheck = primaryStatus != null && primaryStatus.isError() && primaryStatus.getError() instanceof com.imperva.opensource.ddc.core.exceptions.InvalidConnectionException ?  primaryStatus.getError() : secondaryStatus != null && secondaryStatus.isError() && secondaryStatus.getError() instanceof com.imperva.opensource.ddc.core.exceptions.InvalidConnectionException ? secondaryStatus.getError() : null;

        boolean isStatusInvalidConnection = toCheck != null;
        while (!endpoint.isConnectionSucceeded() && isStatusInvalidConnection && retryCounter > 0) {
            --retryCounter;
            LOGGER.debug("Retry connecting " + (numOfRetries - retryCounter) + " / " + numOfRetries);
            try {
                Thread.sleep(5000);
            } catch (Exception c) {
            }

            ldapConnectionResult = driverBase.connect(endpoint);
        }
        return ldapConnectionResult;
    }
}
