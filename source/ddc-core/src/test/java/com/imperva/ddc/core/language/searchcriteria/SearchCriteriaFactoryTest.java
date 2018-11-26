package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.DirectoryType;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


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
