package com.imperva.ddc.core;

import com.imperva.ddc.core.query.QueryRequest;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
public class RequestExecutorFactoryTest {
    private RequestExecutorFactory requestExecutorFactory;
    private QueryRequest queryRequest;

    @Before
    public void setup() {
        requestExecutorFactory = new RequestExecutorFactory();
        queryRequest = mock(QueryRequest.class);
    }

    @Test
    public void testPagedExecutorCreated() {
        when(queryRequest.isPaged()).thenReturn(true);
        when(queryRequest.getSearchText()).thenReturn("(l=TLV)");
        QueryRequestExecutor executor = requestExecutorFactory.create(queryRequest);
        assertTrue(executor instanceof QueryRequestPagedExecutor);
    }

    @Test
    public void testNonPagedExecutorCreated() {
        when(queryRequest.isPaged()).thenReturn(false);
        when(queryRequest.getSearchText()).thenReturn("(l=TLV)");
        QueryRequestExecutor executor = requestExecutorFactory.create(queryRequest);
        assertTrue(executor instanceof QueryRequestNonPagedExecutor);
    }
}
