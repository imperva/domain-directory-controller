package com.imperva.ddc.core;

import com.imperva.ddc.core.commons.Utils;
import com.imperva.ddc.core.exceptions.*;
import com.imperva.ddc.core.query.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.exception.LdapAuthenticationException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapProtocolErrorException;
import org.apache.directory.api.ldap.model.message.BindRequest;
import org.apache.directory.api.ldap.model.message.BindRequestImpl;
import org.apache.directory.api.ldap.model.message.BindResponse;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gabi.beyo on 05/07/2015.
 * TODO: This class is a total mess. It should be refactored:1) to review relations btwn EndPoint and LdapConnectionResult 2) too many waterfall functions
 */
class Driver extends DriverBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class.getName());

    @Override
    LdapConnectionResult connect(Endpoint endpoint) {
        if (endpoint.getUserAccountName() == null || StringUtils.isEmpty(endpoint.getUserAccountName().trim())) {
            throw new RuntimeException("User Distinguished Name (DN) can't be empty, Anonymous connection is not supported");
        }
        if (StringUtils.isEmpty(endpoint.getPassword())) {
            throw new RuntimeException("Password can't be empty, Anonymous connection is not supported");
        }
        LdapConnectionResult ldapConnectionResult;

        ldapConnectionResult = doConnect(endpoint);
        LOGGER.debug("Connection succeeded! Host: {}, port: {}", endpoint.getResolvedHost(), endpoint.getPort());

        logResult(ldapConnectionResult);
        return ldapConnectionResult;
    }

    private LdapConnectionResult doConnect(Endpoint endpoint) {
        //ALL GOOD NO NEED TO RE CONNECT
        if (endpoint.getLdapConnection() != null && endpoint.getLdapConnection().isConnected() && endpoint.getDestinationType() != DestinationType.NONE) {
            LdapConnectionResult ldapConnectionResult;
            ldapConnectionResult = new LdapConnectionResult();
            ldapConnectionResult.setDestinationType(endpoint.getDestinationType());
            ldapConnectionResult.setConnection(endpoint.getLdapConnection());
            ldapConnectionResult.addStatus(endpoint.getDestinationType() == DestinationType.PRIMARY ? endpoint.getHost() : endpoint.getSecondaryHost(), new HoYeah());
            return ldapConnectionResult;
        }

        if (endpoint.getDestinationType() == DestinationType.NONE) {
            LdapConnectionResult ldapConnectionResult = createConnection(endpoint, DestinationType.PRIMARY);
            if (!ldapConnectionResult.hasError() || Utils.isEmpty(endpoint.getSecondaryHost()))
                return ldapConnectionResult;

            if (endpoint.getLdapConnection() != null) endpoint.close();
            LdapConnectionResult ldapConnectionResultMerged = createConnection(endpoint, DestinationType.SECONDARY);
            ldapConnectionResultMerged.addStatus(ldapConnectionResult.getStatuses());
            return ldapConnectionResultMerged;
        }
        return null;
    }

    private LdapConnectionResult createConnection(Endpoint endpoint, DestinationType destinationType) {
        LdapConnection ldapConnection;
        LdapConnectionResult ldapConnectionResult = new LdapConnectionResult();
        String host = destinationType == DestinationType.PRIMARY ? endpoint.getHost() : endpoint.getSecondaryHost();
        int port = destinationType == DestinationType.PRIMARY ? endpoint.getPort() : endpoint.getSecondaryPort();
        try {
            ldapConnection = getLdapConnection(endpoint, destinationType);
            if (ldapConnection != null && ldapConnection.isConnected()) {
                ldapConnectionResult = new LdapConnectionResult();
                ldapConnectionResult.setDestinationType(destinationType);
                ldapConnectionResult.setConnection(ldapConnection);
                ldapConnectionResult.addStatus(host, new HoYeah());
            }
        } catch (BaseException e) {
            LOGGER.debug("Connection failed! Host: {}, port: {}", host, port);
            ldapConnectionResult.addStatus(host, new Oops(e));
        } catch (Exception e) {
            LOGGER.debug("Connection failed! Host: {}, port: {}", host, port);
            throw e;
        }
        return ldapConnectionResult;
    }

    private void logResult(LdapConnectionResult ldapConnectionResult) {
        if (ldapConnectionResult == null)
            return;
        List<String> messages = new ArrayList<>();

        for (Map.Entry<String, Status> entry : ldapConnectionResult.getStatuses().entrySet()) {
            if (entry.getValue() == null) {
                messages.add("Connection to host " + entry.getKey() + " status unknown");
            } else if (entry.getValue().isError()) {
                messages.add("Connection to host " + entry.getKey() + " has failed. Reason: " + entry.getValue().getMessage());
            } else {
                messages.add("Connection to host " + entry.getKey() + " has succeeded");
            }
        }

        String result = StringUtils.join(messages, ", ");

        if (ldapConnectionResult.getConnection() == null) {
            LOGGER.error("Test connection has failed. Results: {}", result);
        } else {
            if (ldapConnectionResult.hasError()) {
                LOGGER.info("Test connection has succeeded. Results: {}", result);
            }
        }
    }

    private LdapConnection getLdapConnection(Endpoint endpoint, DestinationType destinationType) {
        LdapConnection connection = null;

        String host = destinationType == DestinationType.PRIMARY ? endpoint.getResolvedHost() : endpoint.getSecondaryResolvedHost();
        int port = destinationType == DestinationType.PRIMARY ? endpoint.getPort() : endpoint.getSecondaryPort();
        Boolean isSecuredConnection = destinationType == DestinationType.PRIMARY ? endpoint.isSecuredConnection() : endpoint.getSecuredConnectionSecondary();

        Boolean isIgnoreSSLValidations = endpoint.isIgnoreSSLValidations();
        String password = endpoint.getPassword();
        AccountNameType accountNameType = endpoint.getOsAccountNameMode();
        String userAccountName = endpoint.getUserAccountName();
        try {
            connection = ldapNetworkConnectionGetInstance(host, port, isSecuredConnection, isIgnoreSSLValidations);
            Integer timeout = Integer.parseInt(DDCProperties.getInstance().getProperty("connection.timeout"));
            if (connection instanceof LdapNetworkConnection) {
                ((LdapNetworkConnection) connection).getConfig().setTimeout(timeout);
            }
            BindRequest bindRequest = new BindRequestImpl();
            bindRequest.setCredentials(password);
            bindRequest.setSimple(true);
            if (accountNameType == AccountNameType.DOMAIN_USERNAME)
                bindRequest.setName(userAccountName);
            else if (accountNameType == AccountNameType.DN)
                bindRequest.setDn(new Dn(userAccountName));
            BindResponse bindResponse = connection.bind(bindRequest);
            if (bindResponse.getLdapResult().getResultCode() != ResultCodeEnum.SUCCESS) {
                if (bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.PROTOCOL_ERROR) {
                    throw new LdapProtocolErrorException(createLdapErrorMessage(bindResponse));
                } else if (bindResponse.getLdapResult().getResultCode() == ResultCodeEnum.INVALID_CREDENTIALS) {
                    throw new LdapAuthenticationException(createLdapErrorMessage(bindResponse));
                } else {
                    throw new LdapException(createLdapErrorMessage(bindResponse));
                }
            }

            try {
                if (LOGGER.isTraceEnabled())
                    LOGGER.trace(connection.getRootDse().toString());
            } catch (Exception e) { /*DO NOTHING*/ }


            LOGGER.debug("Ldap Connection to " + host + " succeeded.");
        } catch (LdapAuthenticationException e) {
            String error = "Ldap Connection to " + host + " failed: " + e.toString();
            LOGGER.error(error, e);
            throw new AuthenticationException(e);//* Bad credentials
        } catch (LdapProtocolErrorException e) {
            String error = "Ldap Connection to " + host + " failed: " + e.toString();
            LOGGER.error(error, e);
            throw new ProtocolException(e);//* Port inconsistency 389 + secured
        } catch (InvalidConnectionException e) {
            String error = "Ldap Connection to " + host + " failed";
            LOGGER.error(error, e);
            throw new com.imperva.ddc.core.exceptions.InvalidConnectionException(e);//* Bad Host/Port, Unreachable server
        } catch (UnresolvableHostException e) {
            LOGGER.debug("An UnresolvableHostException has occurred!");
            String error = "Ldap Connection to " + host + " failed";
            LOGGER.error(error, e);
            throw new UnresolvableHostException(e);//* Unresolvable host
        } catch (InvalidIpAddressException e) {
            LOGGER.debug("An InvalidIpAddressException has occurred!");
            String error = "Ldap Connection to " + host + " failed";
            LOGGER.error(error, e);
            throw new InvalidIpAddressException(e);//* Invalid ip
        } catch (EmptyHostException e) {
            LOGGER.debug("An EmptyHostException has occurred!");
            String error = "Ldap Connection to " + host + " failed";
            LOGGER.error(error, e);
            throw new EmptyHostException(e);//* Invalid ip
        } catch (Exception e) {
            String error = "Ldap Connection to " + host + " failed";
            LOGGER.error(error, e);
            String reason = "An unknown exception occurred. Please make sure your credentials and the IP address / Port are correct and the target Active Directory Server is reachable";
            throw new UnknownException(e.getMessage(), reason, e);
        } finally {
            endpoint.setLdapConnection(connection);
        }
        return connection;
    }

    private String createLdapErrorMessage(BindResponse bindResponse) {
        return "Error Code: " + bindResponse.getLdapResult().getResultCode() + ". Exception: " + bindResponse.getLdapResult().getDiagnosticMessage();
    }

    LdapNetworkConnection ldapNetworkConnectionGetInstance(String host, Integer port, Boolean isSecuredConnection, Boolean isIgnoreSSLValidations) {
        LdapConnectionConfig ldapConnectionConfig = new LdapConnectionConfig();
        ldapConnectionConfig.setUseSsl(isSecuredConnection);
        ldapConnectionConfig.setLdapHost(host);
        ldapConnectionConfig.setLdapPort(port);
        Boolean ignoreChainException = isIgnoreSSLValidations == null ? Boolean.parseBoolean(DDCProperties.getInstance().getProperty("ignore.ssl.cert.chain.exception")) : isIgnoreSSLValidations;
        if (isSecuredConnection && ignoreChainException) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            ldapConnectionConfig.setTrustManagers(trustAllCerts);
        }
        return new LdapNetworkConnection(ldapConnectionConfig);
    }
}
