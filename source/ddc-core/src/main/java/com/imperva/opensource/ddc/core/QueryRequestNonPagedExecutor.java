package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.commons.Utils;
import com.imperva.opensource.ddc.core.exceptions.BaseException;
import com.imperva.opensource.ddc.core.query.*;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
class QueryRequestNonPagedExecutor extends QueryRequestExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryRequestNonPagedExecutor.class.getName());

    QueryRequestNonPagedExecutor(QueryRequest queryRequest) {
        super(queryRequest);
    }

    //TODO split into small functions
    @Override
    QueryResponse execute() {
        QueryResponse response = new QueryResponse();
        List<PartitionResponse> result = new ArrayList<>();
        for (Endpoint endpoint : queryRequest.getEndpoints()) {
            String host = endpoint.getHost();

            try {
                LOGGER.debug("Executing request for: " + host);

                //* Prepare structures
                PartitionResponse partitionResponse = new PartitionResponse();
                result.add(partitionResponse);
                partitionResponse.setEndpoint(endpoint);

                //* Connect
                LdapConnectionResult ldapConnectionResult = driverGetInstance().connect(endpoint);

                //*Resolve Base Path
                String baseSearchPath = tryResolveBasePath(ldapConnectionResult.getConnection(), endpoint.getBaseSearchPath());
                endpoint.setBaseSearchPath(baseSearchPath);

                //* Fill results
                endpoint.setDestinationType(ldapConnectionResult.getDestinationType());
                endpoint.setBaseSearchPath(baseSearchPath);
                partitionResponse.setStatus(ldapConnectionResult.getStatuses());

                //* Query
                RoundtripResult roundtripResult = query(ldapConnectionResult.getConnection(), baseSearchPath);
                if (roundtripResult != null && roundtripResult.getData() != null)
                    partitionResponse.setData(parserGetInstance().toEntityResponse(roundtripResult.getData(), queryRequest.getRequestedFields()));

            } catch (LdapException | BaseException e) {
                LOGGER.error("Query Execution failed for Endpoint: " + host, e);
                result.get(result.size() - 1).addStatus(host, new Oops(e));
                if (!Utils.isEmpty(endpoint.getSecondaryHost())) {
                    result.get(result.size() - 1).addStatus(endpoint.getSecondaryHost(), new Oops(e));
                }
                endpoint.setDestinationType(DestinationType.NONE);
            }
            LOGGER.debug(result.size() + " results found for: " + host);
        }
        response.addPartitionResponse(result);
        return response;
    }

    RoundtripResult query(LdapConnection ldapConnection, String baseSearchPath) throws LdapException {
        if (ldapConnection != null && ldapConnection.isConnected()) {
            LdapConnection connection = ldapConnection;
            SearchRequest search = parserGetInstance().toSearchRequest(queryRequest, baseSearchPath);
            RoundtripResult roundtripResult = collectData(connection, search);
            return roundtripResult;
        } else {
            return null;
        }
    }

    RoundtripResult collectData(LdapConnection connection, SearchRequest search) {
        RoundtripResult roundtripResult = new RoundtripResult();
        SearchCursor cursor = null;
        List<Entry> result = new ArrayList<>();
        try {
            cursor = connection.search(search);
            result = this.run(cursor);
            roundtripResult.setData(result);
        } catch (LdapException e) {
            LOGGER.error("Can't execute non-paged LDAP query", e.getStackTrace());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (IOException e) {
                    LOGGER.error("Can't close cursor", e.getStackTrace());
                }
            }
        }
        LOGGER.trace("Collect Data. Data size:", result == null ? "NaN" : result.size());
        return roundtripResult;
    }
}
