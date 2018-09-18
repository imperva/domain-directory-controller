package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.query.ChangeRequest;
import com.imperva.opensource.ddc.core.query.QueryRequest;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
class RequestExecutorFactory {
     QueryRequestExecutor create(QueryRequest queryRequest){
        QueryRequestExecutor executor;
        if (!queryRequest.isPaged()) {
            executor = new QueryRequestNonPagedExecutor(queryRequest);
        } else {
            executor = new QueryRequestPagedExecutor(queryRequest);
        }
        return executor;
    }



    ChangeRequestExecutor create(ChangeRequest changeRequest){
        return new ChangeRequestExecutor(changeRequest);
    }
}
