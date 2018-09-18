package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.exceptions.*;
import com.imperva.opensource.ddc.core.language.changeCreteria.ChangeCriteria;
import com.imperva.opensource.ddc.core.language.searchcriteria.RequestBridgeBuilderDirector;
import com.imperva.opensource.ddc.core.language.searchcriteria.RequestBridgeBuilderDirectorImpl;
import com.imperva.opensource.ddc.core.query.*;
import com.imperva.opensource.ddc.core.language.searchcriteria.SearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.InvalidParameterException;

/**
 * Created by gabi.beyo on 07/06/2015.
 */
public class Connector implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class.getName());
    private Executor executor = new Executor();
    private QueryRequest queryRequest;
    private ChangeRequest changeRequest;
    private RequestType requestType;

    public Connector(QueryRequest queryRequest) {
        setRequest(queryRequest);
    }

    public Connector(ChangeRequest changeRequest) {
        setRequest(changeRequest);
    }


    public void setRequest(QueryRequest queryRequest) {
        this.queryRequest = queryRequest;
        requestType = RequestType.QUERY;
    }

    public void setRequest(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
        requestType = RequestType.CHANGE;
    }


    /**
     * Cursor is used in Paging scenarios (setPageChunkSize > 0). The cursor is used to iterate through paged responses
     *
     * @return A {@link Cursor} object
     */
    public Cursor getCursor() {
        if (requestType == RequestType.QUERY) {
            if (!queryRequest.isPaged()) {
                throw new InvalidParameterException("Chunk Size in not configured, paging can't be handled");
            }

            Cursor cursor = new Cursor(this);
            for (Endpoint endPoint : queryRequest.getEndpoints()) {
                cursor.addEndpoint(endPoint);
            }
            return cursor;
        } else
            return null;
    }

    /**
     * Execute request
     *
     * @return A {@link QueryResponse} containing the result
     */
    public QueryResponse execute() {

        if (requestType != RequestType.QUERY)
            throw new InvalidExecuteException("The ChangeRequest object in the connector class doesn't match with the execute() method");

        RequestBridgeBuilderDirector builderManager = searchCriteriaBridgeBuilderDirectorImplGetInstance();

        validateRequest(queryRequest);


        builderManager.build(queryRequest);
        SearchCriteria searchCriteria = builderManager.get();

        LOGGER.debug("Ldap Query: " + searchCriteria.toString());

        queryRequest.setRequestedFields(searchCriteria.getRequestedFields());
        queryRequest.setSearchSentenceText(searchCriteria.getSearchFilter());

        return executor.execute(queryRequest);

    }

    public void executeChangeRequest() {
        if (requestType != RequestType.CHANGE)
            throw new InvalidExecuteException("The queryRequest object in the connector class doesn't match with the executeChangeRequest() method");

        RequestBridgeBuilderDirector builderManager = searchCriteriaBridgeBuilderDirectorImplGetInstance();

        validateRequest(changeRequest);
        builderManager.build(changeRequest);
        ChangeCriteria changeCriteria = builderManager.get();
        changeRequest.setModificationDetailsList(changeCriteria.getModificationDetailsList());

        executor.execute(changeRequest);
    }


    /**
     * Test endpoint connectivity
     *
     * @throws AuthenticationException    if credentials doesn't match
     * @throws InvalidConnectionException if server is not reachable
     * @throws ProtocolException          if request is not supported by protocol
     * @throws UnknownException           unknown exception
     */
    public ConnectionResponse testConnection() {
        if (requestType == RequestType.QUERY) {
            validateRequest(queryRequest);
            return executor.testConnection(queryRequest.getEndpoints().get(0));
        } else
            return executor.testConnection(changeRequest.getEndpoint());
    }

    @Override
    public void close() {
        if (requestType == RequestType.QUERY) {
            if (queryRequest != null)
                queryRequest.close();
        } else {
            if (changeRequest != null)
                changeRequest.close();
        }
    }

    RequestBridgeBuilderDirectorImpl searchCriteriaBridgeBuilderDirectorImplGetInstance() {
        return new RequestBridgeBuilderDirectorImpl();
    }

    void validateRequest(QueryRequest queryRequest) {

        if (requestType == RequestType.QUERY) {
            if (queryRequest.getEndpoints() == null || queryRequest.getEndpoints().isEmpty()) {
                throw new InvalidParameterException("Endpoints are required");
            }

            if (queryRequest.getSizeLimit() < queryRequest.getPageChunkSize()) {
                throw new InvalidParameterException("SizeLimit must be grater than or equal to PageChunkSize");
            }

            if (queryRequest.isIgnoreSSLValidations() != null)
                queryRequest.getEndpoints().forEach((end) -> end.setIgnoreSSLValidations(queryRequest.isIgnoreSSLValidations()));


            int howManyFailed = (int) queryRequest.getEndpoints().stream().filter(i -> !i.isValid()).count();
            if (howManyFailed > 0)
                throw new InvalidParameterException("Critical Endpoint info missing");
        }
    }


    void validateRequest(ChangeRequest changeRequest) {

        if (requestType == RequestType.QUERY) {
            if (changeRequest.getEndpoint() == null) {
                throw new InvalidParameterException("Endpoints are required");
            }

            if (changeRequest.isIgnoreSSLValidations() != null)
                changeRequest.getEndpoint().setIgnoreSSLValidations(changeRequest.isIgnoreSSLValidations());
        }
    }

    public RequestType getRequestType() {
        return requestType;
    }
}