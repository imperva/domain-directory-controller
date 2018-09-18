package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.query.LdapConnectionResult;
import com.imperva.opensource.ddc.core.query.RoundtripResult;
import com.imperva.opensource.ddc.core.query.Endpoint;
import com.imperva.opensource.ddc.core.query.QueryRequest;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by gabi.beyo on 06/07/2015.
 */
public class QueryRequestNonPagedExecutorTest {
    private QueryRequestNonPagedExecutor queryRequestNonPagedExecutor;
    private QueryRequest queryRequest;
    private Driver driver;
    private LdapConnection ldapConnection;

    @Before
    public void setup() {
        queryRequest = mock(QueryRequest.class);
        when(queryRequest.getSearchText()).thenReturn("(givenName=Gabi)");
        queryRequestNonPagedExecutor = spy(new QueryRequestNonPagedExecutor(queryRequest));
        driver = mock(Driver.class);
        ldapConnection = mock(LdapNetworkConnection.class);
    }

    @Test
    @Ignore
    public void testExecute() {
        List<Endpoint> endpoints = new ArrayList<>();
        SearchRequest searchRequest = mock(SearchRequest.class);
        Endpoint endpoint1 = mock(Endpoint.class);
        Endpoint endpoint2 = mock(Endpoint.class);
        endpoints.add(endpoint1);
        endpoints.add(endpoint2);
        LdapConnectionResult ldapConnectionResult = mock(LdapConnectionResult.class);
        LdapConnection ldapConnection = mock(LdapNetworkConnection.class);
        ldapConnectionResult.setConnection(ldapConnection);
        RoundtripResult roundtripResult = mock(RoundtripResult.class);

        when(queryRequest.getEndpoints()).thenReturn(endpoints);

        doReturn(driver).when(queryRequestNonPagedExecutor).driverGetInstance();
        doReturn(roundtripResult).when(queryRequestNonPagedExecutor).collectData(ldapConnection, searchRequest);
        when(driver.connect(any(Endpoint.class))).thenReturn(ldapConnectionResult);
        when(ldapConnectionResult.getConnection()).thenReturn(ldapConnection);
        when(ldapConnectionResult.connectionSucceeded()).thenReturn(true);

        queryRequestNonPagedExecutor.execute();

        verify(endpoint1, times(2)).getBaseSearchPath();
        verify(endpoint2, times(2)).getBaseSearchPath();
        verify(driver, times(endpoints.size())).connect(any(Endpoint.class));
        verify(queryRequestNonPagedExecutor, times(endpoints.size())).collectData(any(LdapNetworkConnection.class), any(SearchRequest.class));
        verify(endpoint1, times(1)).setDestinationType(any());
    }

    @Test(expected = LdapException.class)
    public void testExecuteFailedInvalidDnException() throws LdapException {
        Endpoint endpoint1 = mock(Endpoint.class);
        when(endpoint1.getBaseSearchPath()).thenReturn("---");
        ApacheAPIConverter apacheAPIConverter = new ApacheAPIConverter();
        apacheAPIConverter.toSearchRequest(queryRequest, endpoint1.getBaseSearchPath());
    }

    @Test
    public void testCollectData() {
        try {
            when(ldapConnection.search(any(SearchRequest.class))).thenReturn(mock(SearchCursor.class));
            doReturn(null).when(queryRequestNonPagedExecutor).run(any(SearchCursor.class));
            RoundtripResult roundtripResult = queryRequestNonPagedExecutor.collectData(ldapConnection, mock(SearchRequest.class));
            assertNotNull(roundtripResult);
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCollectDataFailed() {
        try {
            LdapException ldapException = mock(LdapException.class);
            SearchRequest searchRequest = new SearchRequestImpl();
            doThrow(ldapException).when(ldapConnection).search(searchRequest);

            queryRequestNonPagedExecutor.collectData(ldapConnection, searchRequest);
            verify(ldapException, times(1)).getStackTrace();
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }
}
