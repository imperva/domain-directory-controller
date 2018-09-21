package com.imperva.ddc.core;

import com.imperva.ddc.core.query.LdapConnectionResult;
import com.imperva.ddc.core.query.QueryRequest;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
public class ExecutorTest {
    private Driver driver;
    private Executor executor;
    private LdapConnectionResult ldapConnectionResult;
    private LdapNetworkConnection ldapNetworkConnection;
    private QueryRequest queryRequest;
    private QueryRequestPagedExecutor queryRequestPagedExecutor;

    @Before
    public void setup() {
        executor = spy(new Executor());

        driver = mock(Driver.class);
        ldapNetworkConnection = mock(LdapNetworkConnection.class);
        ldapConnectionResult = mock(LdapConnectionResult.class);
        ldapConnectionResult.setConnection(ldapNetworkConnection);
        queryRequest = mock(QueryRequest.class);
        queryRequestPagedExecutor = mock(QueryRequestPagedExecutor.class);
    }

    @Test
    public void testExecuteIsExecuted() {
        RequestExecutorFactory requestExecutorFactory = mock(RequestExecutorFactory.class);
        when(requestExecutorFactory.create(queryRequest)).thenReturn(queryRequestPagedExecutor);
        doReturn(requestExecutorFactory).when(executor).queryRequestExecutorFactoryGetInstance();

        executor.execute(queryRequest);

        verify(queryRequestPagedExecutor, times(1)).execute();
    }

}
