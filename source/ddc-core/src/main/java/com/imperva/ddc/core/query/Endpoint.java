package com.imperva.ddc.core.query;

import com.imperva.ddc.core.commons.Utils;
import org.apache.directory.ldap.client.api.LdapConnection;

import java.io.IOException;

/**
 * Created by gabi.beyo on 18/06/2015.
 * Represent the Directory endpoint data required for connectivity
 */
public class Endpoint implements AutoCloseable {

    private AccountNameType osAccountNameMode = AccountNameType.DOMAIN_USERNAME;
    private String host;
    private String resolvedHost;
    private String secondaryHost;
    private String secondaryResolvedHost;
    private Integer port = 389;
    private Integer secondaryPort = 389;
    private String userAccountName;
    private String osUserName;
    private String domain;
    private String password;
    private String base;
    private Boolean securedConnection = false;
    private Boolean securedConnectionSecondary = false;
    private Boolean ignoreSSLValidations;

    //* Not included in copy-constructor
    private Object cookie;
    private CursorStatus hasNext = CursorStatus.STARTING;
    private DestinationType destinationType = DestinationType.NONE;
    private LdapConnection ldapConnection;

    public Endpoint(){}

    public Endpoint(Endpoint endpoint){
        this.setBaseSearchPath(endpoint.getBaseSearchPath());
        this.domain = endpoint.getDomain();
        this.osUserName= endpoint.getOsUserName();
        this.setUserAccountName(endpoint.getUserAccountName());

        this.setPort(endpoint.getPort());
        this.setHost(endpoint.getHost());
        this.setSecuredConnection(endpoint.isSecuredConnection());
        this.setPassword(endpoint.getPassword());

        this.setSecondaryHost(endpoint.getSecondaryHost());
        this.setSecuredConnectionSecondary(endpoint.getSecuredConnectionSecondary());
        this.setSecondaryPort(endpoint.getSecondaryPort());

        this.setResolvedHost(endpoint.getResolvedHost());
        this.setSecondaryResolvedHost(endpoint.getSecondaryResolvedHost());
    }

    /**
     * @return Endpoint's IP
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host Endpoint's IP
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return Endpoint's secondary IP
     */
    public String getSecondaryHost() {
        return secondaryHost;
    }

    /**
     * @param secondaryHost Endpoint's secondary IP
     */
    public void setSecondaryHost(String secondaryHost) {
        this.secondaryHost = secondaryHost;
    }

    /**
     * @return Endpoint's port, the default value is set to 389
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port Endpoint's port, the default value is set to 389
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return Endpoint's secondary port, the default value is set to 389
     */
    public Integer getSecondaryPort() {
        return secondaryPort;
    }

    /**
     * @param secondaryPort Endpoint's secondary port, the default value is set to 389
     */
    public void setSecondaryPort(Integer secondaryPort) {
        this.secondaryPort = secondaryPort;
    }

    /**
     * @return User Distinguished Name or Domain\UserName is a unique name that identifies a specific user
     *
     */
    public String getUserAccountName() {
        return userAccountName;
    }

    /**
     * @param userAccountName User Distinguished Name, Domain\UserName or UserName@FullDomain is a unique name that identifies a specific user
     */
    public void setUserAccountName(String userAccountName) {
        if(Utils.isEmpty(userAccountName)) {
            this.userAccountName = null;
            this.osAccountNameMode = AccountNameType.NONE;
            return;
        }
        boolean isDistinguishedName =  Utils.isDistinguishName(userAccountName);
        if(isDistinguishedName){
            this.osAccountNameMode = AccountNameType.DN;
            this.domain = null;
            this.osUserName = null;
        } else {
            this.osAccountNameMode = AccountNameType.DOMAIN_USERNAME;
            if (Utils.isUserNameFullDomain(userAccountName)) {
                String[] splittedAccount = userAccountName.split("@");
                this.domain = splittedAccount[1];
                this.osUserName = splittedAccount[0];
            } else {
                String[] splittedAccount = userAccountName.split("\\\\");
                this.domain = splittedAccount[0];
                this.osUserName = splittedAccount[1];
            }
        }
        this.userAccountName = userAccountName;
    }

    /**
     * @return User's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password User's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Optional parameter which indicates the search starting point. If not set will be automatically guessed
     */
    public String getBaseSearchPath() {
        return base;
    }

    /**
     * @param base Optional parameter which indicates the search starting point. If not set will be automatically guessed
     */
    public void setBaseSearchPath(String base) {
        this.base = base;
    }


    /**
     * @return <code>true</code> indicates a secured connection, <code>false</code> an unsecured one. Default is <code>false</code>
     */
    public Boolean isSecuredConnection() {
        return securedConnection;
    }

    /**
     * @param securedConnection <code>true</code> indicates a secured connection, <code>false</code> an unsecured one. Default is <code>false</code>
     */
    public void setSecuredConnection(Boolean securedConnection) {
        this.securedConnection = securedConnection;
    }

    /**
     * @return <code>true</code> indicates a secured connection of secondary host, <code>false</code> an unsecured one. Default is <code>false</code>
     */
    public Boolean getSecuredConnectionSecondary() {
        return securedConnectionSecondary;
    }

    /**
     * @param securedConnectionSecondary <code>true</code> indicates a secured connection of secondary host, <code>false</code> an unsecured one. Default is <code>false</code>
     */
    public void setSecuredConnectionSecondary(Boolean securedConnectionSecondary) {
        this.securedConnectionSecondary = securedConnectionSecondary;
    }

    /**
     * @return Used when a search request is Paged, in order to maintain a pointer to the next chunk result
     */
    public Object getCookie() {
        return cookie;
    }

    /**
     * @param cookie Used when a search request is Paged, in order to maintain a pointer to the next chunk result
     */
    public void setCookie(Object cookie) {
        this.cookie = cookie;
    }

    /**
     * @return A {@link CursorStatus} enumerator, used when a search request is Paged, which indicates the specific endpoint's status
     */
    public CursorStatus hasNext() {
        return hasNext;
    }

    /**
     * @param hasNext A {@link CursorStatus} enumerator, used when a search request is Paged, which indicates the specific endpoint's status
     */
    public void hasNext(CursorStatus hasNext) {
        this.hasNext = hasNext;
    }

    /**
     * @return AD Server Domain
     */
    private String getDomain() {
        return domain;
    }

    /**
     * @return User Name
     */
    public String getOsUserName() {
        return osUserName;
    }

    /**
     * @return Account Name Mode
     */
    public AccountNameType getOsAccountNameMode() {
        return osAccountNameMode;
    }

    /**
     * @return Destination Type
     */
    public DestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    /**
     * @return Ignore SSL Validations. If set to null default FALSE value will be used
     */
    public Boolean isIgnoreSSLValidations() {
        return ignoreSSLValidations;
    }


    public void setIgnoreSSLValidations(boolean ignoreSSLValidations) {
        this.ignoreSSLValidations = ignoreSSLValidations;
    }

    /**
     * @return Underling Connection Object
     */
    public LdapConnection getLdapConnection() {
        return ldapConnection;
    }

    public void setLdapConnection(LdapConnection ldapConnection) {
        this.ldapConnection = ldapConnection;
    }

    public boolean isConnectionSucceeded(){
        return this.ldapConnection != null && this.ldapConnection.isConnected();
    }

    public boolean isValid() {
        if (Utils.isEmpty(host) || Utils.isEmpty(port) || Utils.isEmpty(password))
            return false;
        if (!Utils.isEmpty(secondaryHost) && (Utils.isEmpty(secondaryPort)))
            return false;
        return true;
    }

    @Override
    public void close() {
        if(ldapConnection != null && ldapConnection.isConnected()) {
            try {
                ldapConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getResolvedHost() {
        return resolvedHost;
    }

    public void setResolvedHost(String resolvedHost) {
        this.resolvedHost = resolvedHost;
    }

    public String getSecondaryResolvedHost() {
        return secondaryResolvedHost;
    }

    public void setSecondaryResolvedHost(String secondaryResolvedHost) {
        this.secondaryResolvedHost = secondaryResolvedHost;
    }
}
