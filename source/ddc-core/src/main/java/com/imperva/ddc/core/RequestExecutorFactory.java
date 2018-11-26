package com.imperva.ddc.core;

import com.imperva.ddc.core.query.AddRequest;
import com.imperva.ddc.core.query.ChangeRequest;
import com.imperva.ddc.core.query.QueryRequest;
import com.imperva.ddc.core.query.RemoveRequest;

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

    RemoveRequestExecutor create(RemoveRequest removeRequest){
        return new RemoveRequestExecutor(removeRequest);
    }

    AddRequestExecutor create(AddRequest addRequest){
        return new AddRequestExecutor(addRequest);
    }
}
