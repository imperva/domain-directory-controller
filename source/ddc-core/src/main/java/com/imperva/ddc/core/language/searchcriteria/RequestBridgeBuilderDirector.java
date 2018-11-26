package com.imperva.ddc.core.language.searchcriteria;

import com.imperva.ddc.core.query.*;

/**
 * Created by gabi.beyo on 18/06/2015.
 */
public abstract class RequestBridgeBuilderDirector {
    protected SearchCriteriaBuilder searchCriteriaBuilder;
    protected ChangeRequestBuilder changeRequestBuilder;
    protected RemoveRequestBuilder removeRequestBuilder;
    protected AddRequestBuilder addRequestBuilder;

    public abstract void build(QueryRequest queryRequest);

    public abstract void build(ChangeRequest changeRequest);

    public abstract void build(RemoveRequest addRequest);

    public abstract void build(AddRequest addRequest);

    public abstract <T> T get();
}
