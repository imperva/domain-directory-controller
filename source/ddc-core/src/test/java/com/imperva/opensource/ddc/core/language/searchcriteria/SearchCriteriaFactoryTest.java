package com.imperva.opensource.ddc.core.language.searchcriteria;

import com.imperva.opensource.ddc.core.query.DirectoryType;
import com.imperva.opensource.ddc.core.language.searchcriteria.ActiveDirectorySearchCriteriaBuilderImpl;
import com.imperva.opensource.ddc.core.language.searchcriteria.SearchCriteriaBuilder;
import com.imperva.opensource.ddc.core.language.searchcriteria.SearchCriteriaFactory;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by gabi.beyo on 06/07/2015.
 */
public class SearchCriteriaFactoryTest {

    @Before
    public void setup() {

    }

    @Test
    public void testCreate() {
        SearchCriteriaBuilder SearchCriteriaBuilder = SearchCriteriaFactory.create(DirectoryType.MS_ACTIVE_DIRECTORY);
        assertTrue(SearchCriteriaBuilder instanceof ActiveDirectorySearchCriteriaBuilderImpl);
    }
}
