package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.commons.Utils;
import com.imperva.opensource.ddc.core.exceptions.BaseException;
import com.imperva.opensource.ddc.core.exceptions.QueryFailedException;
import com.imperva.opensource.ddc.core.query.*;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.message.controls.PagedResults;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
class QueryRequestPagedExecutor extends QueryRequestExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryRequestPagedExecutor.class.getName());

    QueryRequestPagedExecutor(QueryRequest queryRequest) {
        super(queryRequest);
    }

    //TODO split into small functions
    @Override
    QueryResponse execute() {
        QueryResponse response = new QueryResponse();
        for (Endpoint endpoint : this.queryRequest.getEndpoints()) {
            //* Prepare result obj
            PartitionResponse partitionResponse = new PartitionResponse();
            response.addPartitionResponse(partitionResponse);
            partitionResponse.setEndpoint(endpoint);
            try {
                //* Pre Check
                LOGGER.debug("Executing request for: " + endpoint.getHost());
                if (endpoint.hasNext() == CursorStatus.EOF) {
                    LOGGER.debug("Host " + endpoint.getHost() + " skipped: EOF");
                    continue;
                }

                //* Get Connection
                LdapConnectionResult ldapConnectionResult = driverGetInstance().connect(endpoint);

                String basePath = tryResolveBasePath(ldapConnectionResult.getConnection(), endpoint.getBaseSearchPath());
                endpoint.setBaseSearchPath(basePath);

                //* Update Connection Details
                endpoint.setDestinationType(ldapConnectionResult.getDestinationType());
                partitionResponse.setStatus(ldapConnectionResult.getStatuses());

                LdapConnection connection = ldapConnectionResult.getConnection();
                if (ldapConnectionResult.connectionSucceeded()) {
                    SearchRequest search = parserGetInstance().toSearchRequest(queryRequest, endpoint.getBaseSearchPath(), endpoint.getCookie());
                    RoundtripResult roundtripResult = collectData(connection, search);
                    Object cookie = retrieveCookie(roundtripResult.getSearchCursor());
                    endpoint.setCookie(cookie);

                    if (cookie == null) {
                        endpoint.hasNext(CursorStatus.EOF);
                    } else {
                        endpoint.hasNext(CursorStatus.PAGING);
                    }
                    partitionResponse.setData(new ApacheAPIConverter().toEntityResponse(roundtripResult.getData(), queryRequest.getRequestedFields()));
                    return response;
                } else {
                    LOGGER.error(String.format("Could not execute query against AD {}. Connection failure!", endpoint.getHost()));
                    endpoint.hasNext(CursorStatus.EOF);
                    endpoint.setDestinationType(DestinationType.NONE);
                }
            } catch (LdapException | BaseException e) {
                LOGGER.error("Query Execution failed for Endpoint:" + endpoint.getHost(), e);
                partitionResponse.addStatus(endpoint.getHost(), new Oops(e));
                if (!Utils.isEmpty(endpoint.getSecondaryHost())) {
                    partitionResponse.addStatus(endpoint.getSecondaryHost(), new Oops(e));
                }
                endpoint.hasNext(CursorStatus.EOF);
                endpoint.setDestinationType(DestinationType.NONE);
            } catch (Exception e) {
                endpoint.hasNext(CursorStatus.EOF);
                endpoint.setDestinationType(DestinationType.NONE);
                LOGGER.error("Some unhandled exception occurred at Endpoint: " + endpoint.getHost(), e);
                throw e;
            }
        }
        return response;
    }

    Object retrieveCookie(SearchCursor cursor) {
        SearchResultDone result = cursor.getSearchResultDone();

        PagedResults pagedSearchControl = (PagedResults) result.getControl(PagedResults.OID);
        LOGGER.trace("Retrieving cookie. Page control: {}", pagedSearchControl);

        ResultCodeEnum resultCodeEnum = result.getLdapResult().getResultCode();
        LOGGER.trace("Retrieving cookie. Result Code: {}", resultCodeEnum.getMessage());

        if (resultCodeEnum == ResultCodeEnum.UNWILLING_TO_PERFORM || resultCodeEnum == ResultCodeEnum.UNAVAILABLE_CRITICAL_EXTENSION || pagedSearchControl == null) {
            throw new QueryFailedException("AD can't handle paging. pagedSearchControl null?: " + (pagedSearchControl == null) + ", result.getLdapResult().getResultCode(): " + result.getLdapResult().getResultCode().name());
        }
        Object cookie = pagedSearchControl.getCookie();
        LOGGER.trace("Retrieving cookie. Cookie value: {}", cookie);
        return cookie;
    }

    RoundtripResult collectData(LdapConnection connection, SearchRequest searchRequest) {
        RoundtripResult roundtripResult = new RoundtripResult();
        List<Entry> results = new ArrayList<>();
        SearchCursor cursor = null;
        try {
            cursor = connection.search(searchRequest);
            results = this.run(cursor);
        } catch (LdapException e) {
            LOGGER.error("Search failed", e.getStackTrace());
        }finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (IOException e) {
                    LOGGER.error("Can't close cursor", e.getStackTrace());
                }
            }
        }
        LOGGER.trace("Retrieving cookie. Data size: {}",  results ==null ? "NaN" : results.size());
        roundtripResult.setData(results);
        roundtripResult.setSearchCursor(cursor);
        return roundtripResult;
    }
}
