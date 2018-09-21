package com.imperva.ddc.core;

import com.imperva.ddc.core.query.*;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.message.controls.PagedResults;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by gabi.beyo on 06/07/2015.
 */
public class QueryRequestPagedExecutorTest {

    private QueryRequestPagedExecutor queryRequestPagedExecutor;
    private QueryRequest queryRequest;
    private Driver driver;
    private LdapConnection ldapConnection;
    private LdapConnectionResult ldapConnectionResult;
    @Before
    public void setup() {
        queryRequest = mock(QueryRequest.class);
        when(queryRequest.getSearchText()).thenReturn("(givenName=Gabi)");
        queryRequestPagedExecutor = spy(new QueryRequestPagedExecutor(queryRequest));

        driver = mock(Driver.class);
        ldapConnection = mock(LdapNetworkConnection.class);
        ldapConnectionResult = mock(LdapConnectionResult.class);
        ldapConnectionResult.setConnection(ldapConnection);
    }

    @Test
    public void testExecute() throws LdapException {
        ApacheAPIConverter apacheAPIConverter = mock(ApacheAPIConverter.class);
        List<Endpoint> endpoints = new ArrayList<>();
        Endpoint endpoint1 = mock(Endpoint.class);
        endpoints.add(endpoint1);
        SearchRequest searchRequest = mock(SearchRequest.class);
        RoundtripResult roundtripResult= mock(RoundtripResult.class);
        SearchCursor cursor = mock(SearchCursor.class);

        when(queryRequest.getEndpoints()).thenReturn(endpoints);

        doReturn(roundtripResult).when(queryRequestPagedExecutor).collectData(ldapConnection, searchRequest);
        doReturn(roundtripResult).when(queryRequestPagedExecutor).retrieveCookie(cursor);
        doReturn(driver).when(queryRequestPagedExecutor).driverGetInstance();
        doReturn(apacheAPIConverter).when(queryRequestPagedExecutor).parserGetInstance();
        when(apacheAPIConverter.toSearchRequest(queryRequest, endpoint1.getBaseSearchPath(), endpoint1.getCookie())).thenReturn(searchRequest);
        when(driver.connect(any(Endpoint.class))).thenReturn(ldapConnectionResult);
        when(ldapConnectionResult.connectionSucceeded()).thenReturn(true);
        when(ldapConnectionResult.getConnection()).thenReturn(ldapConnection);
        when(roundtripResult.getSearchCursor()).thenReturn(cursor);

        queryRequestPagedExecutor.execute();

        verify(driver,times(endpoints.size())).connect(any(Endpoint.class));
        verify(queryRequestPagedExecutor,times(endpoints.size())).collectData(any(LdapNetworkConnection.class),any(SearchRequest.class));
        verify(queryRequestPagedExecutor,times(1)).retrieveCookie(cursor);
        verify(endpoint1, times(1)).setDestinationType(any());
    }

    @Test
    public void testExecuteEOF() {
        List<Endpoint> endpoints = new ArrayList<>();
        Endpoint endpoint1 = mock(Endpoint.class);
        endpoints.add(endpoint1);

        when(endpoint1.hasNext()).thenReturn(CursorStatus.EOF);
        when(queryRequest.getEndpoints()).thenReturn(endpoints);
        doReturn(driver).when(queryRequestPagedExecutor).driverGetInstance();

        queryRequestPagedExecutor.execute();

        verify(endpoint1, times(0)).getBaseSearchPath();
        verify(driver,times(0)).connect(any(Endpoint.class));
        verify(queryRequestPagedExecutor,times(0)).collectData(any(LdapNetworkConnection.class),any(SearchRequest.class));
        verify(queryRequestPagedExecutor,times(0)).retrieveCookie(any(SearchCursor.class));
    }

    @Test
    public void testCollectData() {
        try {
            SearchRequest searchRequest = mock(SearchRequest.class);
            SearchCursor cursor = mock(SearchCursor.class);
            List<Entry> entries = new ArrayList<>();
            Entry entry1 = mock(Entry.class);
            entries.add(entry1);

            when(ldapConnection.search(searchRequest)).thenReturn(cursor);
            doReturn(entries).when(queryRequestPagedExecutor).run(cursor);
            RoundtripResult roundtripResult = queryRequestPagedExecutor.collectData(ldapConnection, searchRequest);

            assertEquals(roundtripResult.getData(), entries);

        } catch (LdapException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRetrieveCookie() {
        SearchCursor cursor = mock(SearchCursor.class);
        SearchResultDone searchResultDone = mock(SearchResultDone.class);
        PagedResults pagedSearchControl = mock(PagedResults.class);
        LdapResult ldapResult = mock(LdapResult.class);
        byte[] cookie = new byte[1];
        cookie[0] = 1;

        when(cursor.getSearchResultDone()).thenReturn(searchResultDone);
        when(searchResultDone.getControl(PagedResults.OID)).thenReturn(pagedSearchControl);
        when(pagedSearchControl.getCookie()).thenReturn(cookie);
        when(ldapResult.getResultCode()).thenReturn(ResultCodeEnum.BUSY);
        when(searchResultDone.getLdapResult()).thenReturn(ldapResult);

        Object cookieResult = queryRequestPagedExecutor.retrieveCookie(cursor);

        Object firstItem = ((byte[])cookieResult)[0];
        assertEquals(cookie[0],firstItem);
    }

    @Test
    public void testRun() {
        try {
            SearchCursor cursor = mock(SearchCursor.class);
            SearchResultEntry response = mock(SearchResultEntry.class);
            Entry entry = mock(Entry.class);

            when(response.getEntry()).thenReturn(entry);
            when(cursor.next()).thenReturn(true).thenReturn(false);
            when(cursor.get()).thenReturn(response);

            List<Entry> entries = queryRequestPagedExecutor.run(cursor);

            verify(cursor,times(1)).get();
            verify(cursor,times(2)).next();
            verify(response,times(1)).getEntry();
            assertTrue(entries.get(0).equals(entry));
        } catch (CursorException e) {
            e.printStackTrace();
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRunFailedLdapException() {
        try {
            SearchCursor cursor = mock(SearchCursor.class);
            SearchResultEntry response = mock(SearchResultEntry.class);
            Entry entry = mock(Entry.class);
            LdapException ldapException = mock(LdapException.class);

            when(response.getEntry()).thenReturn(entry);
            when(cursor.next()).thenReturn(true).thenReturn(false);
            when(cursor.get()).thenReturn(response);

            doThrow(ldapException).when(ldapConnection).unBind();

            doThrow(ldapException).when(cursor).next();

            queryRequestPagedExecutor.run(cursor);

            verify(ldapException, times(1)).getStackTrace();
        } catch (CursorException e) {
            e.printStackTrace();
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRunFailedCursorException() {
        try {
            SearchCursor cursor = mock(SearchCursor.class);
            SearchResultEntry response = mock(SearchResultEntry.class);
            Entry entry = mock(Entry.class);
            CursorException cursorException = mock(CursorException.class);

            when(response.getEntry()).thenReturn(entry);
            when(cursor.next()).thenReturn(true).thenReturn(false);

            doThrow(cursorException).when(cursor).next();

            queryRequestPagedExecutor.run(cursor);

            verify(cursorException, times(1)).getStackTrace();
        } catch (CursorException e) {
            e.printStackTrace();
        } catch (LdapException e) {
            e.printStackTrace();
        }
    }
}
