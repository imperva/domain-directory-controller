package com.imperva.opensource.ddc.core.language.searchcriteria;

import com.imperva.opensource.ddc.core.query.DirectoryType;
import com.imperva.opensource.ddc.core.query.QueryRequest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by gabi.beyo on 06/07/2015.
 */
public class SearchCriteriaBridgeBuilderDirectorImplTest {

    private RequestBridgeBuilderDirectorImpl searchCriteriaBridgeBuilderDirector;
    private QueryRequest queryRequest;
    private ActiveDirectorySearchCriteriaBuilderImpl searchCriteriaBuilder;

    @Before
    public void setup() {
        queryRequest = mock(QueryRequest.class);
        searchCriteriaBuilder = mock(ActiveDirectorySearchCriteriaBuilderImpl.class);

        when(queryRequest.getDirectoryType()).thenReturn(DirectoryType.MS_ACTIVE_DIRECTORY);
        searchCriteriaBridgeBuilderDirector = spy(new RequestBridgeBuilderDirectorImpl());

    }

    @Test
    public void testBuild() {
        doReturn(searchCriteriaBuilder).when(searchCriteriaBridgeBuilderDirector).searchCriteriaBuilderGetInstance(DirectoryType.MS_ACTIVE_DIRECTORY);
        searchCriteriaBridgeBuilderDirector.build(queryRequest);
        verify(searchCriteriaBuilder, times(1)).translateFields();
        verify(searchCriteriaBuilder, times(1)).translateFields();
    }

    @Test
    public void testGet() {
        doReturn(searchCriteriaBuilder).when(searchCriteriaBridgeBuilderDirector).searchCriteriaBuilderGetInstance(DirectoryType.MS_ACTIVE_DIRECTORY);
        searchCriteriaBridgeBuilderDirector.build(queryRequest);
        searchCriteriaBridgeBuilderDirector.get();
        verify(searchCriteriaBuilder, times(1)).get();
    }

}
