package com.imperva.ddc.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.message.controls.PagedResults;
import org.apache.directory.api.ldap.model.message.controls.PagedResultsImpl;
import org.apache.directory.api.ldap.model.message.controls.SortRequestImpl;
import org.apache.directory.api.ldap.model.name.Dn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imperva.ddc.core.exceptions.ProtocolException;
import com.imperva.ddc.core.query.EntityResponse;
import com.imperva.ddc.core.query.Field;
import com.imperva.ddc.core.query.ModificationDetails;
import com.imperva.ddc.core.query.Operation;
import com.imperva.ddc.core.query.QueryRequest;
import com.imperva.ddc.core.query.ReferralsHandling;
import com.imperva.ddc.core.query.SortKey;

/**
 * Created by gabi.beyo on 02/07/2015.
 * Convert DirectoryConnector objects to org.apache.directory.api and vice versa
 */
class ApacheAPIConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheAPIConverter.class.getName());

    String[] toStringArray(List<Field> entities) {
        if (entities == null || entities.isEmpty()) {
            String[] result = new String[1];
            result[0] = "*";
            return result;
        }
        String[] result = new String[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            result[i] = entities.get(i).getName();
        }
        return result;
    }

    public Modification toModification(ModificationDetails modificationDetails) {
        final Object value = null == modificationDetails.getValue()
                ? null
                : modificationDetails.getValue();
        Operation operation = modificationDetails.getOperation();
        String strAttribute = modificationDetails.getAttribute().getName();

        switch (operation) {
            case ADD:
                return new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, strAttribute, convertToValue(value));
            case REMOVE:
                return null == value
                        ? new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, strAttribute)
                        : new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, strAttribute, convertToValue(value));
            case REPLACE:
                return new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, strAttribute, convertToValue(value));
            default:
                return null;
        }
    }

    Value convertToValue(Object value) {
    	if (value instanceof String) {
    		return new Value((String) value);
    	}
    	if (value instanceof Number) {
    		return new Value(value.toString());
    	}
    	if (value instanceof byte[]) {
    		return new Value((byte[]) value);
    	}
    	return null;
    }

    List<EntityResponse> toEntityResponse(List<Entry> entries, List<Field> requestedFields) {
        List<EntityResponse> entResponses = new ArrayList<>();
        for (Entry ent : entries) {
            EntityResponse entResponse = new EntityResponse();
            entResponse.setKey(ent.getDn().getName());
            for (Attribute att : ent.getAttributes()) {
                for (Field field : requestedFields) {
                    if (field.getName().equals("*") || field.getName().equalsIgnoreCase(att.getId())) {
                        att.iterator().forEachRemaining(value -> {
                            entResponse.addValue(value, att.getId(), field.getType());
                        });
                    }
                }
            }
            entResponses.add(entResponse);
        }
        return entResponses;
    }

    SearchRequest toSearchRequest(QueryRequest queryRequest, String baseSearchPath) throws LdapException {
        SearchRequest search = new SearchRequestImpl();

        search.setScope(SearchScope.SUBTREE);
        String[] translatedRequestedFieldsArray = toStringArray(queryRequest.getRequestedFields());
        search.addAttributes(translatedRequestedFieldsArray);
        search.setTimeLimit(queryRequest.getTimeLimit());
        search.setSizeLimit(queryRequest.getSizeLimit());
        search.setBase(new Dn(baseSearchPath));
        if (queryRequest.getReferralsHandling() == ReferralsHandling.FOLLOW) {
            search.followReferrals();
        } else if (queryRequest.getReferralsHandling() == ReferralsHandling.IGNORE) {
            search.ignoreReferrals();
        }
        String searchText = queryRequest.getSearchText() == null ? queryRequest.getSearchSentenceText() : queryRequest.getSearchText();
        if (searchText != null && !searchText.trim().isEmpty())
            search.setFilter(searchText);

        List<SortKey> sortKeys = queryRequest.getSortKeys();
        if (!Objects.isNull(sortKeys) && !sortKeys.isEmpty()) {
            SortRequestImpl sortRequest = applySort(sortKeys);
            search.addControl(sortRequest);
        }
        return search;
    }

    SearchRequest toSearchRequest(QueryRequest queryRequest, String baseSearchPath, Object cookie) throws LdapException {
        SearchRequest search = toSearchRequest(queryRequest, baseSearchPath);
        if (queryRequest.isPaged()) {
            PagedResults pagedSearchControl = new PagedResultsImpl();
            int size = queryRequest.getPageChunkSize();
            pagedSearchControl.setSize(size);
            pagedSearchControl.setCookie((byte[]) cookie);
            pagedSearchControl.setCritical(true);
            search.addControl(pagedSearchControl);
        }
        return search;
    }

//    void applySort(QueryRequest queryRequest, SearchRequest searchRequest) {
//        if (!Objects.isNull(queryRequest.getSortKeys()) && !queryRequest.getSortKeys().isEmpty()) {
//            SortRequest sortRequest = new SortRequestControlImpl();
//            for (SortKey sortKey : queryRequest.getSortKeys()) {
//                sortRequest.addSortKey(new org.apache.directory.api.ldap.model.message.controls.SortKey(sortKey.getName(), sortKey.getMatchingRuleId(), sortKey.isReverseOrder()));
//            }
//            searchRequest.addControl(sortRequest);
//        }
//    }


    SortRequestImpl applySort(List<SortKey> sortKeys) {
        if (Objects.isNull(sortKeys) && sortKeys.isEmpty()) {
            throw new ProtocolException("Sorting keys can't be empty");
        }
        SortRequestImpl sortRequest = new SortRequestImpl();
        sortRequest.setCritical(false);

        for (SortKey sortKey : sortKeys) {
            sortRequest.addSortKey(new org.apache.directory.api.ldap.model.message.controls.SortKey(sortKey.getName(), sortKey.getMatchingRuleId(), sortKey.isReverseOrder()));
        }
        return sortRequest;
    }
}
