package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.commons.Utils;
import com.imperva.opensource.ddc.core.exceptions.EmptyHostException;
import com.imperva.opensource.ddc.core.exceptions.InvalidIpAddressException;
import com.imperva.opensource.ddc.core.exceptions.UnresolvableHostException;
import com.imperva.opensource.ddc.core.query.Endpoint;
import com.imperva.opensource.ddc.core.query.LdapConnectionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DriverHostResolverDecorator extends DriverBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverHostResolverDecorator.class.getName());
    private static final Pattern ipStructurePattern = Pattern.compile(("\\b(?:\\d+\\.){3}\\d+\\b"));
    private static final Pattern ipAddressPattern = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    private DriverBase driverBase;

    DriverHostResolverDecorator(DriverBase driverBase) {
        this.driverBase = driverBase;
    }

    @Override
    LdapConnectionResult connect(Endpoint endpoint) {
        validateHost(endpoint.getHost());
        String resolvedHost = resolveHost(endpoint.getHost());
        endpoint.setResolvedHost(resolvedHost);

        if(!Utils.isEmpty(endpoint.getSecondaryHost())) {
            validateHost(endpoint.getSecondaryHost());
            String resolvedSecondaryHost = resolveHost(endpoint.getSecondaryHost());
            endpoint.setSecondaryResolvedHost(resolvedSecondaryHost);
        }

        return driverBase.connect(endpoint);
    }

    private void validateHost(String host) {
        if (Utils.isEmpty(host)) {
            throw new EmptyHostException("Host/IP field cannot be empty");
        }

        Matcher ipStructureMatcher = ipStructurePattern.matcher(host);
        if (ipStructureMatcher.matches()) {
            Matcher validIpStructureMatcher = ipAddressPattern.matcher(host);
            if (!validIpStructureMatcher.matches()) {
                throw new InvalidIpAddressException("IP " + host + " is invalid");
            }
        }
    }

    private String resolveHost(String hostOrIp) {
        Matcher ipStructureMatcher = ipStructurePattern.matcher(hostOrIp);
        if (ipStructureMatcher.matches()) {
            LOGGER.debug("{} is recognize as IP", hostOrIp);
            return hostOrIp;
        } else {
            // host
            LOGGER.debug("{} is recognize as host. Resolving host to ip", hostOrIp);
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getByName(hostOrIp);
                String ipAddress = inetAddress.getHostAddress();
                LOGGER.debug("Host resolving result: {}", ipAddress);
                return ipAddress;
            } catch (UnknownHostException e) {
                LOGGER.error("An error occurred while trying to resolve host {}. Error: {}", hostOrIp, e.getMessage());
                LOGGER.debug("Exception: ", e);
                return hostOrIp;
                //throw new UnresolvableHostException(e);
            }
        }
    }
}
