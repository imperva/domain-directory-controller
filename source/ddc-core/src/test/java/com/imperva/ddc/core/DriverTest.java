package com.imperva.ddc.core;

import com.imperva.ddc.core.query.LdapConnectionResult;
import com.imperva.ddc.core.query.Endpoint;
import com.imperva.ddc.core.query.DestinationType;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by gabi.beyo on 07/07/2015.
 */
public class DriverTest {

    Driver driver;
    @Before
    public void setup() {
        driver = spy(new Driver());
    }

    @Test(expected = RuntimeException.class)
    public void testConnectionWithEmptyInfo_Failure() {

        Endpoint endpoint = mock(Endpoint.class);
        LdapConnection connection = mock(LdapNetworkConnection.class);

        when(endpoint.getUserAccountName()).thenReturn("");
        when(endpoint.getPassword()).thenReturn("");
        doReturn(connection).when(driver).ldapNetworkConnectionGetInstance(any(String.class), any(Integer.class), any(Boolean.class), any(Boolean.class));

        LdapConnection connectionResult = driver.connect(endpoint).getConnection();

        assertEquals(connectionResult, connection);
    }

    @Test
    public void testConnectPrimaryOnly() {
        String host = "10.1.1.1";
        Endpoint endpoint = createEndpoint(host, null, "mmm\\mmm", "p", DestinationType.NONE);

        LdapConnection connection = mock(LdapNetworkConnection.class);
        doReturn(connection).when(driver).ldapNetworkConnectionGetInstance(any(String.class), any(Integer.class), any(Boolean.class), any(Boolean.class));
        LdapConnectionResult ldapConnectionResult = driver.connect(endpoint);
        assertTrue(ldapConnectionResult.getStatuses().containsKey(host));
        assertNotNull(ldapConnectionResult.getStatuses().get(host));
    }

    @Test
    public void testConnectionPrimaryAndSecondary() {
        String host = "10.1.1.1";
        String secondaryHost = "10.2.2.2";
        Endpoint endpoint = createEndpoint(host, secondaryHost, "naaa\\naaa", "p", DestinationType.NONE);

        LdapConnection connection = mock(LdapNetworkConnection.class);
        doReturn(connection).when(driver).ldapNetworkConnectionGetInstance(any(String.class), any(Integer.class), any(Boolean.class), any(Boolean.class));

        LdapConnectionResult ldapConnectionResult = driver.connect(endpoint);
        assertTrue(ldapConnectionResult.getStatuses().containsKey(host));
        assertTrue(ldapConnectionResult.getStatuses().containsKey(secondaryHost));
        assertNotNull(ldapConnectionResult.getStatuses().get(host));
        assertNotNull(ldapConnectionResult.getStatuses().get(secondaryHost));
    }

    @Test
    @Ignore
    public void testConnectionWithDestinationType() {
        String host = "10.1.1.1";
        String secondaryHost = "10.2.2.2";
        Endpoint endpoint = createEndpoint(host, secondaryHost, "naa\\aaa","p", DestinationType.PRIMARY);

        LdapConnection connection = mock(LdapNetworkConnection.class);
        doReturn(connection).when(driver).ldapNetworkConnectionGetInstance(any(String.class), any(Integer.class), any(Boolean.class), any(Boolean.class));

        LdapConnectionResult ldapConnectionResult = driver.connect(endpoint);
        assertTrue(ldapConnectionResult.getStatuses().containsKey(host));
        assertFalse(ldapConnectionResult.getStatuses().containsKey(secondaryHost));
    }

    @Test
    @Ignore
    public void testConnectionWithDestinationType2() {
        String host = "10.1.1.1";
        String secondaryHost = "10.2.2.2";
        Endpoint endpoint = createEndpoint(host, secondaryHost, "naa\\aaa","p", DestinationType.SECONDARY);

        LdapConnection connection = mock(LdapNetworkConnection.class);
        doReturn(connection).when(driver).ldapNetworkConnectionGetInstance(any(String.class), any(Integer.class), any(Boolean.class), any(Boolean.class));

        LdapConnectionResult ldapConnectionResult = driver.connect(endpoint);
        assertFalse(ldapConnectionResult.getStatuses().containsKey(host));
        assertTrue(ldapConnectionResult.getStatuses().containsKey(secondaryHost));
    }

    private Endpoint createEndpoint(String host, String secondaryHost, String userAccountName, String password, DestinationType destinationType) {
        Endpoint endpoint = new Endpoint();
        endpoint.setHost(host);
        endpoint.setSecondaryHost(secondaryHost);
        endpoint.setUserAccountName(userAccountName);
        endpoint.setPassword(password);
        endpoint.setDestinationType(destinationType);
        endpoint.setLdapConnection(new LdapNetworkConnection());
        return endpoint;
    }

    @Test (expected = RuntimeException.class)
    public void testConnectFailed() {
        try {
            LdapException ldapException = mock(LdapException.class);
            Endpoint endpoint = mock(Endpoint.class);
            LdapConnection connection = mock(LdapNetworkConnection.class);

            when(endpoint.getUserAccountName()).thenReturn("");
            when(endpoint.getPassword()).thenReturn("");
            doReturn(connection).when(driver).ldapNetworkConnectionGetInstance(endpoint.getHost(), endpoint.getPort(),endpoint.isSecuredConnection(), endpoint.isIgnoreSSLValidations());

            doThrow(ldapException).when(connection).bind(anyString(),anyString());

            driver.connect(endpoint);

            verify(ldapException, times(1)).getStackTrace();
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }
}
