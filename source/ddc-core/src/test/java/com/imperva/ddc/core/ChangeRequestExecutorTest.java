package com.imperva.ddc.core;

import com.imperva.ddc.core.query.*;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

/**
 * Created by gabi.beyo on 06/07/2015.
 */
public class ChangeRequestExecutorTest {
    private ChangeRequestExecutor changeRequestExecutor;
    private ChangeRequest changeRequest;
    private Driver driver;
    private LdapConnection ldapConnection;
    private ApacheAPIConverter apacheAPIConverter;

    @Before
    public void setup() {
        changeRequest = mock(ChangeRequest.class);
        when(changeRequest.getDn()).thenReturn("CN=John,OU=Users,DC=Group,DC=Company,DC=com");
        changeRequestExecutor = spy(new ChangeRequestExecutor(changeRequest));
        driver = mock(Driver.class);
        ldapConnection = mock(LdapNetworkConnection.class);
    }

    @Test
    @Ignore
    public void testExecute() {
        SearchRequest searchRequest = mock(SearchRequest.class);
        Endpoint endpoint = mock(Endpoint.class);
        ModificationDetails modificationDetails= mock(ModificationDetails.class);
        List<ModificationDetails> modificationDetailsList= new ArrayList<ModificationDetails>();
        modificationDetailsList.add(modificationDetails);

        Modification modification= new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, "givenName", "ChangedTest1");
        LdapConnectionResult ldapConnectionResult = mock(LdapConnectionResult.class);
        LdapConnection ldapConnection = mock(LdapNetworkConnection.class);
        ldapConnectionResult.setConnection(ldapConnection);
        RoundtripResult roundtripResult = mock(RoundtripResult.class);


        when(changeRequest.getEndpoint()).thenReturn(endpoint);

        doReturn(driver).when(changeRequestExecutor).driverGetInstance();
        when(driver.connect(any(Endpoint.class))).thenReturn(ldapConnectionResult);
        when(changeRequest.getModificationDetailsList()).thenReturn(modificationDetailsList);

        /*when(changeRequestExecutor.apacheAPIConverter.toModification(modificationDetails)).thenReturn(modification);*/
        when(ldapConnectionResult.getConnection()).thenReturn(ldapConnection);
        when(ldapConnectionResult.connectionSucceeded()).thenReturn(true);

        changeRequestExecutor.execute();

        verify(driver, times(1)).connect(any(Endpoint.class));
/*        verify(queryRequestNonPagedExecutor, times(endpoints.size())).collectData(any(LdapNetworkConnection.class), any(SearchRequest.class));
        verify(endpoint1, times(1)).setDestinationType(any());*/
    }
/*
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
    }*/
}
