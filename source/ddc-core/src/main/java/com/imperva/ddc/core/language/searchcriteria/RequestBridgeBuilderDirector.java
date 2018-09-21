package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.ChangeRequest;
import com.imperva.ddc.core.query.ChangeRequestBuilder;
import com.imperva.ddc.core.query.QueryRequest;

/**
 * Created by gabi.beyo on 18/06/2015.
 */
public abstract class RequestBridgeBuilderDirector {
    protected SearchCriteriaBuilder searchCriteriaBuilder;
    protected ChangeRequestBuilder changeRequestBuilder;

    public abstract void build(QueryRequest queryRequest);

    public abstract void build(ChangeRequest changeRequest);

    public abstract <T> T get();
}
