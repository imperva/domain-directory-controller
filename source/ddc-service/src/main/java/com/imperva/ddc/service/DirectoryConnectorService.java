package com.imperva.ddc.service;

import com.imperva.ddc.core.Connector;
import com.imperva.ddc.core.commons.Utils;
import com.imperva.ddc.core.exceptions.GroupDoesNotExistException;
import com.imperva.ddc.core.exceptions.InvalidAuthenticationInfoException;
import com.imperva.ddc.core.exceptions.UserDisabledException;
import com.imperva.ddc.core.query.*;
import com.imperva.ddc.core.language.PhraseOperator;
import com.imperva.ddc.core.language.QueryAssembler;
import com.imperva.ddc.core.language.Sentence;
import com.imperva.ddc.core.language.SentenceOperator;
import com.imperva.ddc.core.query.Cursor;
import com.imperva.ddc.core.query.CursorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gabi.beyo on 11/06/2015.
 * <p>
 * Expose DirectoryConnectorService public API
 * Initialize Spring context - once and in a threadsafe mode
 * </p>
 */
public class DirectoryConnectorService {
    private static PagingCallback defaultPagingCallback = (data,context)-> true;
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryConnectorService.class.getName());
    private DirectoryConnectorService() {
    }

    /**
     * Authenticate a user against an AD endpoint
     *
     * @param endpoint A {@link Endpoint} Authentication Endpoint
     * @return {@link ConnectionResponse} An Object representing the Connection result
     */
    public static ConnectionResponse authenticate(Endpoint endpoint) {
        ConnectionResponse connectionResponse = null;
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setIgnoreSSLValidations(true);
        queryRequest.addEndpoint(endpoint);
        try(Connector connector = new Connector(queryRequest)){
            connectionResponse = connector.testConnection();
        }
        return connectionResponse;
    }

    /**
     * Authenticate a user against an AD endpoint
     *
     * @param allowEnabledOnly True false flag indicates whether disabled users should be considered not authenticated
     * @param endpoint         A {@link Endpoint} Authentication Endpoint
     * @return {@link ConnectionResponse} An Object representing the Connection result
     */
    public static ConnectionResponse authenticate(Endpoint endpointForAuth, boolean allowEnabledOnly, Endpoint endpoint) {
        ConnectionResponse response = null;
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setIgnoreSSLValidations(true);
        queryRequest.addEndpoint(endpoint);
        try(Connector connector = new Connector(queryRequest)){
            response = connector.testConnection();
        }

        if (!allowEnabledOnly) {
            return response;
        }

        if (response.isError()) {
            return response;
        }

        String username = endpoint.getOsUserName();
        String userDN = Utils.isDistinguishName(username) ? username : resolveDistinguishedName(username, FieldType.LOGON_NAME, ObjectType.USER, endpointForAuth);
        if (Utils.isEmpty(userDN))
            throw new InvalidAuthenticationInfoException("Ldap connection to " + endpointForAuth.getHost() + " failed");

        boolean isEnabled = isEnabled(endpointForAuth, endpoint.getOsUserName());
        if (!isEnabled) {
            String error = "Ldap Connection to " + endpointForAuth.getHost() + " failed";
            Map<String, Status> statuses = response.getStatuses();
            statuses.putIfAbsent(endpointForAuth.getHost(), new Oops(new UserDisabledException(error)));
            boolean hasSecondary = !Utils.isEmpty(endpointForAuth.getSecondaryHost());
            boolean noSecondaryError = statuses.get(endpointForAuth.getSecondaryHost()) == null;
            if (hasSecondary && noSecondaryError) {
                statuses.put(endpointForAuth.getSecondaryHost(), new Oops(new UserDisabledException(error)));
            }
        }

        return response;
    }

    /**
     * Resolves the DN of the given FieldType {@link FieldType}
     *
     * @param name       The FieldType {@link FieldType} value
     * @param fieldType  The Requested FieldType {@link FieldType}
     * @param objectType The Requested ObjectType {@link ObjectType}
     * @param endpoint   The endpoint {@link Endpoint} to query
     * @return The resolved DN if found otherwise null
     */
    public static String resolveDistinguishedName(String name, FieldType fieldType, ObjectType objectType, Endpoint endpoint) {
        ArrayList arrayList = new ArrayList<String>(1);
        arrayList.add(0, name);
        List<String> resolved =  resolveDistinguishedName(arrayList, fieldType, objectType, endpoint);
        if(!Utils.isEmpty(resolved)) {
            String dn = resolved.get(0).toString();
            return dn;
        }
        return null;
    }
    /**
     * Resolves the DN of the given FieldType {@link FieldType} od the N given values
     *
     * @param names      The FieldType {@link FieldType} values
     * @param fieldType  The Requested FieldType {@link FieldType}
     * @param objectType The Requested ObjectType {@link ObjectType}
     * @param endpoint   The endpoint {@link Endpoint} to query
     * @return The resolved DNs' list if found otherwise null
     */
    public static List<String> resolveDistinguishedName(List<String> names, FieldType fieldType, ObjectType objectType, Endpoint endpoint) {

        int maxItemsPerRequest = 100;
        LOGGER.debug("Try to resolve DN of " + names.size() + " entities");
        List<String> totalMembers = new ArrayList<>();
        for (int i = 0; i <= names.size(); i += maxItemsPerRequest) {
            int maxTo = i + maxItemsPerRequest;
            int to = names.size() < maxTo ? names.size() : maxTo;
            List<String> namesChunk = names.subList(i, to);

            QueryRequest queryRequest = new QueryRequest();
            queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
            List<Endpoint> endpoints = new ArrayList<>();
            endpoints.add(endpoint);
            queryRequest.setEndpoints(endpoints);
            queryRequest.setSizeLimit(1000);
            queryRequest.setTimeLimit(1000);
            queryRequest.setObjectType(objectType);
            queryRequest.setIgnoreSSLValidations(true);

            QueryAssembler queryAssembler = new QueryAssembler();

            LOGGER.debug("Resolving " + namesChunk.size() + " dns");
            for (String name : namesChunk) {
                LOGGER.trace(name);
                queryAssembler.addPhrase(fieldType, PhraseOperator.EQUAL, name);
            }

            Sentence sentence = queryAssembler.closeSentence(SentenceOperator.OR);
            queryRequest.setSearchSentence(sentence);


            QueryResponse queryResponse = null;
            try (Connector connector = new Connector(queryRequest)) {
                queryResponse = connector.execute();
            }

            List<String> result = new ArrayList<>();
            for (EntityResponse q : queryResponse.getAll()) {
                //result.addAll(q.getValue().stream().filter(val -> val.getType() == FieldType.DISTINGUISHED_NAME).collect(Collectors.toList()).stream().map(v -> v.getValue().toString()).collect(Collectors.toList()));
                result.add((String) q.getKey());
            }
            LOGGER.trace("Resolved DNs:");
            for (String res : result) {
                LOGGER.trace("DN: " + res);
            }
            totalMembers.addAll(result);
        }
        LOGGER.debug("TOTAL DNs resolved " + totalMembers.size());
        return totalMembers;
    }

    /**
     *
     * @param dns
     * @param fieldType
     * @param objectType
     * @param endpoint
     * @return
     */
    public static List<EntityResponse> getEntity(List<String> dns, FieldType fieldType, ObjectType objectType, Endpoint endpoint) {
        int maxItemsPerRequest = 100;
        LOGGER.debug("Try to get entities " + dns.size() + " entities");
        List<EntityResponse> totalMembers = new ArrayList<>();
        for (int i = 0; i <= dns.size(); i += maxItemsPerRequest) {
            int maxTo = i + maxItemsPerRequest;
            int to = dns.size() < maxTo ? dns.size() : maxTo;
            List<String> namesChunk = dns.subList(i, to);

            QueryRequest queryRequest = new QueryRequest();
            queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
            List<Endpoint> endpoints = new ArrayList<>();
            endpoints.add(endpoint);
            queryRequest.setEndpoints(endpoints);
            queryRequest.setSizeLimit(1000);
            queryRequest.setTimeLimit(1000);
            queryRequest.setObjectType(objectType);
            queryRequest.setIgnoreSSLValidations(true);

            queryRequest.addRequestedField(FieldType.LOGON_NAME);
            queryRequest.addRequestedField(FieldType.COMMON_NAME);
            QueryAssembler queryAssembler = new QueryAssembler();


            for (String name : namesChunk) {
                queryAssembler.addPhrase(fieldType, PhraseOperator.EQUAL, name);
            }

            Sentence sentence = queryAssembler.closeSentence(SentenceOperator.OR);
            queryRequest.setSearchSentence(sentence);
            QueryResponse queryResponse = null;
            try(Connector connector = new Connector(queryRequest))           {
                queryResponse = connector.execute();
            }

            List<EntityResponse> result = new ArrayList<>();
            totalMembers.addAll(queryResponse.getAll());

            totalMembers.addAll(result);
        }
        return totalMembers;
    }

    /**
     * Finds recursively the groups of the given user based on a pre-defined group list to be checked
     *
     * @param logonName   The user's logonName
     * @param groupsNames The Common Name of the groups to be checked
     * @param endpoint    The endpoint {@link Endpoint} to query
     * @return The Common Name list of the detected groups
     */
    public static List<String> isMemberOf(String logonName, List<String> groupsNames, Endpoint endpoint) {
        List<String> stringList = new ArrayList<>();
        String distinguishedName = Utils.isDistinguishName(logonName) ? logonName : resolveDistinguishedName(logonName,FieldType.LOGON_NAME,ObjectType.USER,endpoint);
        stringList.add(distinguishedName);

        List<EntityResponse> grps = doIsMemberOf(stringList, groupsNames, endpoint, null);

        List<String> memberOf = new ArrayList<>();
        for (EntityResponse grp : grps) {
            String groupCN = grp.getValue().stream().filter(val -> val.getType() == FieldType.COMMON_NAME).collect(Collectors.toList()).get(0).getValue().toString();
            memberOf.add(groupCN);
        }
        return memberOf;
    }

    public static List<EntityResponse> isMemberOf(List<String> logonName, Endpoint endpoint, PagingCallback pagingCallback) {
        List<String> dns = new ArrayList<>();
        List<String> cns = new ArrayList<>();
        for (String g : logonName) {
            if (!Utils.isDistinguishName(g)) {
                cns.add(g);
            } else {
                dns.add(g);
            }
        }
        if (!Utils.isEmpty(cns)) {
            List<String> dnsResolved = resolveDistinguishedName(cns, FieldType.LOGON_NAME, ObjectType.GROUP, endpoint);
            dns.addAll(dnsResolved);
        }

        if (Utils.isEmpty(dns))
            throw new GroupDoesNotExistException("Group {} does not exist in the Active Directory");
        return doIsMemberOf(dns, new ArrayList<>(), endpoint,pagingCallback);
    }

    public static List<EntityResponse> isMemberOf(List<String> logonName, Endpoint endpoint) {
        return isMemberOf(logonName, endpoint, defaultPagingCallback);
    }

    private static List<EntityResponse> doIsMemberOf(List<String> dn, List<String> groupsNames, Endpoint endpoint,PagingCallback pagingCallback) {
        LOGGER.debug("Check " + dn + " is member of one or more groups:");

        int maxItemsPerRequest = 100;
        List<EntityResponse> totalMembers = new ArrayList<>();
        for(int i =0; i <= dn.size(); i += maxItemsPerRequest) {
            int maxTo = i + maxItemsPerRequest;
            int to = dn.size() < maxTo? dn.size() : maxTo ;
            List<String> logonNamesChunk = dn.subList(i,to);

            QueryRequest queryRequest = new QueryRequest();
            queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
            List<Endpoint> endpoints = new ArrayList<>();
            endpoints.add(new Endpoint(endpoint));
            queryRequest.setEndpoints(endpoints);
            queryRequest.setSizeLimit(1000);
            queryRequest.setTimeLimit(1000);
            queryRequest.setPageChunkSize(1000);
            queryRequest.setObjectType(ObjectType.GROUP);
            queryRequest.addRequestedField(FieldType.COMMON_NAME);
            queryRequest.addRequestedField(FieldType.MEMBER);
            queryRequest.setIgnoreSSLValidations(true);

            QueryAssembler queryAssembler = new QueryAssembler();
            for (String l : logonNamesChunk) {
                queryAssembler.addPhrase(FieldType.GROUP_RECURSIVE, PhraseOperator.EQUAL, l);
            }
            Sentence allEntitiesGroups = queryAssembler.closeSentence(SentenceOperator.OR);

            Sentence finalSentence = allEntitiesGroups;
            if (groupsNames != null && groupsNames.size() > 0) {
                for (String g : groupsNames) {
                    LOGGER.debug("Group Name: " + g);
                    queryAssembler.addPhrase(FieldType.COMMON_NAME, PhraseOperator.EQUAL, g);
                }
                Sentence specificGroupsCommonNames = queryAssembler.closeSentence(SentenceOperator.OR);
                finalSentence = queryAssembler.addSentence(allEntitiesGroups).addSentence(specificGroupsCommonNames).closeSentence(SentenceOperator.AND);
            }
            queryRequest.addSearchSentence(finalSentence);
            try(Connector connector = new Connector(queryRequest)) {
                Cursor cursor = connector.getCursor();
                QueryResponse queryResponse = new QueryResponse();

                PagingCallbackContext pagingCallbackContext = new PagingCallbackContext();
                while (cursor.hasNext()) {
                    List<PartitionResponse> pp = cursor.next().get();
                    queryResponse.addPartitionResponse(pp);
                    totalMembers.addAll(queryResponse.getAll());
                    if (pagingCallback != null) {
                        pagingCallbackContext.setTotal(pagingCallbackContext.getTotal() + queryResponse.getAll().size());
                        boolean isContinue = pagingCallback.callback(queryResponse.getAll(), pagingCallbackContext);
                        queryResponse.get().clear();
                        if (!isContinue) {
                            return queryResponse.getAll();
                        }
                    }
                }
            }
        }
        return totalMembers;
    }

    /**
     * Finds recursively all groups' members of the given group list
     *
     * @param groupNames The groups to be checked
     * @param endpoint   The endpoint {@link Endpoint} to query
     * @return The list of the detected group members
     */
    public static List<EntityResponse> getUsersInGroup(List<String> groupNames,List<FieldType> fields, Endpoint endpoint) {
        return getUsersInGroup(0, groupNames, fields, endpoint);
    }

    public static List<EntityResponse> getUsersInGroup(List<String> groupNames, Endpoint endpoint) {
        return getUsersInGroup(0, groupNames, endpoint);
    }

    public static List<EntityResponse> getUsersInGroup(int maxUsersPerGroup,List<String> groupNames, Endpoint endpoint) {
        return getUsersInGroup(maxUsersPerGroup,groupNames, new ArrayList<>(), endpoint);
    }

    /**
     * Finds recursively all groups' members of the given group list
     *
     * @param groupNames The groups to be checked
     * @param fields     The requested information to query about each group member
     * @param endpoint   The endpoint {@link Endpoint} to query
     * @return The list of the detected group members
     */
    public static List<EntityResponse> getUsersInGroup(int maxUsersPerGroup, List<String> groupNames, List<FieldType> fields, Endpoint endpoint) {
        List<String> dns = new ArrayList<>();
        List<String> cns = new ArrayList<>();
        for (String g : groupNames) {
            if (!Utils.isDistinguishName(g)) {
                cns.add(g);
            } else {
                dns.add(g);
            }
        }
        if (!Utils.isEmpty(cns)) {
            List<String> dnsResolved = resolveDistinguishedName(cns, FieldType.COMMON_NAME, ObjectType.GROUP, endpoint);
            dns.addAll(dnsResolved);
        }

        if (Utils.isEmpty(dns))
            throw new GroupDoesNotExistException("Group {} does not exist in the Active Directory");

        return getUsersInGroup(maxUsersPerGroup, new LinkedList<>(dns), fields, endpoint, new HashSet<>());
    }

    /**
     * Finds recursively all groups' members of the given group
     *
     * @param groupName The group to be checked
     * @param endpoint  The endpoint {@link Endpoint} to query
     * @return The list of the detected group members
     */
    public static List<EntityResponse> getUsersInGroup(String groupName, Endpoint endpoint) {
        return getUsersInGroup(0, groupName, endpoint);
    }

    public static List<EntityResponse> getUsersInGroup(int maxUsersPerGroup,String groupName, Endpoint endpoint) {
        ArrayList arrayList = new ArrayList<String>();
        arrayList.add(groupName);
        return getUsersInGroup(maxUsersPerGroup,arrayList, endpoint);
    }

    /**
     * @param guid     User's GUID
     * @param endpoint The endpoint {@link Endpoint} to query
     * @return The detected user
     * @deprecated Get user by GUID
     */
    public static EntityResponse getUserByGuid(byte[] guid, Endpoint endpoint) {
        if (Utils.isEmpty(guid)) {
            return null;
        }

        String guidStr = DirectoryConnectorService.convertToByteString(guid);

        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(endpoint);
        queryRequest.setEndpoints(endpoints);
        queryRequest.setSizeLimit(1000);
        queryRequest.setTimeLimit(1000);
        queryRequest.setObjectType(ObjectType.USER);
        queryRequest.addRequestedField(FieldType.GUID);
        queryRequest.addRequestedField(FieldType.EMAIL);
        queryRequest.addRequestedField(FieldType.COMMON_NAME);
        queryRequest.addRequestedField(FieldType.LOGON_NAME);
        queryRequest.setIgnoreSSLValidations(true);

        Sentence emailByGuid = new QueryAssembler().addPhrase(FieldType.GUID, PhraseOperator.EQUAL, guidStr).closeSentence();
        queryRequest.addSearchSentence(emailByGuid);
        QueryResponse queryResponse = null;
        try(Connector connector = new Connector(queryRequest)){
             queryResponse = connector.execute();
        }

        if (queryResponse.getAll() != null && !queryResponse.getAll().isEmpty()) {
            return queryResponse.getAll().get(0);
        }
        return null;
    }

    /**
     * Performs a small query to test that any user has been found in the given Endpoint {@link Endpoint}
     *
     * @param endpoint The endpoint {@link Endpoint} to query
     * @return The test result {@link TestQueryType}
     */
    public static TestQueryType testQuery(Endpoint endpoint) throws Exception {
        return testQuery(endpoint, ObjectType.USER);
    }

    /**
     * Performs a small query to test that any given objectType has been found in the given Endpoint {@link Endpoint}
     * @param endpoint The endpoint {@link Endpoint} to query
     * @param objectType The Object type to query for
     * @return The test result {@link TestQueryType}
     * @throws Exception
     */
    public static TestQueryType testQuery(Endpoint endpoint, ObjectType objectType) throws Exception {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(endpoint);
        queryRequest.setEndpoints(endpoints);
        queryRequest.setSizeLimit(1);
        queryRequest.setTimeLimit(1000);
        queryRequest.setObjectType(objectType);
        queryRequest.addRequestedField(FieldType.GUID);
        queryRequest.setIgnoreSSLValidations(true);

        QueryResponse queryResponse = null;
        try(Connector connector = new Connector(queryRequest)){
            queryResponse = connector.execute();
        }

        if (queryResponse.hasError()) {
            PartitionResponse partitionResponse = queryResponse.get().get(0);
            Status status = partitionResponse.getStatus(endpoint.getHost());
            Status statusSecondary = partitionResponse.getStatus(endpoint.getHost());
            if (status != null && status.isError()) {
                throw status.getError();
            } else if (statusSecondary != null && statusSecondary.isError()) {
                throw statusSecondary.getError();
            }
        } else if (queryResponse.getAll().size() > 0) {
            return TestQueryType.DATA_FOUND;
        }
        return TestQueryType.NO_DATA_FOUND;
    }

    /**
     * @deprecated
     * Test endpoint's connection
     *
     * @param endpoint The endpoint {@link Endpoint} to query
     * @return The connection result {@link ConnectionResponse}
     */
    public static ConnectionResponse testConnection(Endpoint endpoint) {
        ConnectionResponse connectionResponse;
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setIgnoreSSLValidations(true);
        queryRequest.addEndpoint(endpoint);
        try (Connector connector = new Connector(queryRequest)) {
            connectionResponse = connector.testConnection();
        }
        return connectionResponse;
    }

    private static boolean isEnabled(Endpoint endpointForAuth, String username) {
        String userDN = Utils.isDistinguishName(username) ? username : resolveDistinguishedName(username, FieldType.LOGON_NAME, ObjectType.USER, endpointForAuth);
        if (Utils.isEmpty(userDN))
            return false;
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(endpointForAuth);
        queryRequest.setEndpoints(endpoints);
        queryRequest.setSizeLimit(1);
        queryRequest.setTimeLimit(1000);
        queryRequest.setObjectType(ObjectType.USER);
        queryRequest.setIgnoreSSLValidations(true);

        queryRequest.addRequestedField(FieldType.LOGON_NAME);
        Sentence entity = new QueryAssembler().addPhrase(FieldType.DISTINGUISHED_NAME, PhraseOperator.EQUAL, userDN)
                .addPhrase(FieldType.USER_ACCOUNT_CONTROL, PhraseOperator.EQUAL, "2").closeSentence(); // 2 means account is disabled
        queryRequest.addSearchSentence(entity);
        QueryResponse queryResponse = null;
        try(Connector connector = new Connector(queryRequest)){
             queryResponse = connector.execute();
        }
        return queryResponse.getAll().size() == 0;

    }

    private static String convertToByteString(byte[] objectGUID) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < objectGUID.length; i++) {
            String transformed = prefixZeros((int) objectGUID[i] & 0xFF);
            result.append("\\");
            result.append(transformed);
        }

        return result.toString();
    }

    private static String prefixZeros(int value) {
        if (value <= 0xF) {
            StringBuilder sb = new StringBuilder("0");
            sb.append(Integer.toHexString(value));

            return sb.toString();

        } else {
            return Integer.toHexString(value);
        }
    }

    private static QueryResponse filterUsers(Endpoint endpoint, List<FieldType> fields, Set<String> dns) {

        LOGGER.debug("Filter out users from list of " + dns.size() + " entities");

        QueryResponse allUsers = new QueryResponse();
        List<String> list = new ArrayList<>(dns);
        int maxItemsPerRequest = 100;
        for (int i = 0; i <= list.size(); i += maxItemsPerRequest) {
            int maxTo = i + maxItemsPerRequest;
            int to = list.size() < maxTo ? list.size() : maxTo;
            List<String> dnsChucked = list.subList(i, to);
            if (Utils.isEmpty(dnsChucked))
                return allUsers;

            QueryRequest queryRequest = new QueryRequest();
            queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
            List<Endpoint> endpoints = new ArrayList<>();
            endpoints.add(new Endpoint(endpoint));
            queryRequest.setEndpoints(endpoints);
            queryRequest.setSizeLimit(1000);
            queryRequest.setTimeLimit(1000);
            queryRequest.setPageChunkSize(1000);
            queryRequest.addRequestedField(FieldType.GROUP);
            queryRequest.addRequestedField(FieldType.COMMON_NAME);
            queryRequest.addRequestedField(FieldType.LOGON_NAME);
            queryRequest.setIgnoreSSLValidations(true);

            for (FieldType field : fields) {
                if (field != FieldType.GROUP && field != FieldType.DISTINGUISHED_NAME)
                    queryRequest.addRequestedField(field);
            }
            QueryAssembler queryAssembler = new QueryAssembler();
            Sentence userRestrictionSentence = queryAssembler.addPhrase(FieldType.OBJECT_CLASS, PhraseOperator.EQUAL, "person").addPhrase(FieldType.OBJECT_CLASS, PhraseOperator.EQUAL, "user").closeSentence(SentenceOperator.AND);
            for (String res : dnsChucked) {
                queryAssembler.addPhrase(FieldType.DISTINGUISHED_NAME, PhraseOperator.EQUAL, res);
            }
            Sentence dnQuerySentence = queryAssembler.closeSentence(SentenceOperator.OR);
            queryRequest.addSearchSentence(queryAssembler.addSentence(dnQuerySentence).addSentence(userRestrictionSentence).closeSentence(SentenceOperator.AND));

            try (Connector connector = new Connector(queryRequest)) {
                Cursor cursor = connector.getCursor();
                while (cursor.hasNext()) {
                    QueryResponse response = cursor.next();
                    LOGGER.debug("Users filtered. Users found: " + response.getAll().size());
                    allUsers.addPartitionResponse(response.get());
                }
            }
            for (EntityResponse user : allUsers.getAll()) {
                LOGGER.trace("User: " + user.toString());
            }
        }
        LOGGER.debug("Total filtered users: " + allUsers.getAll().size());
        return allUsers;
    }

    private static List<EntityResponse> getUsersInGroup(int maxUsersPerGroup, Queue<String> groupNames, List<FieldType> fields, Endpoint endpoint, Set<String> totalResults) {
        LOGGER.debug("Get recursively members of " + groupNames.size()  +" entities");
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(endpoint);
        endpoint.hasNext(CursorStatus.STARTING);
        queryRequest.setEndpoints(endpoints);
        queryRequest.setSizeLimit(1000);
        queryRequest.setTimeLimit(1000);
        queryRequest.setPageChunkSize(1000);
        queryRequest.setIgnoreSSLValidations(true);

        int maxItemsPerRequest = 100;

        QueryAssembler queryAssembler = new QueryAssembler();

        //* STOP CONDITION
        boolean totalResultsExceeded = maxUsersPerGroup > 0 && totalResults.size() >= maxUsersPerGroup;
        if (totalResultsExceeded) {
            LOGGER.debug("About to shrinking total result. Total Results Exceeded (maxUsersPerGroup = " + maxUsersPerGroup + " , totalResults.size() " + totalResults.size() + " )");
            List<EntityResponse> users = filterUsers(endpoint, fields, totalResults).getAll();

            LOGGER.debug("Rechecking Total Results Exceeded (" + users.size() + ").");
            if (users.size() >= maxUsersPerGroup) {
                LOGGER.debug("Exceeded! Sublisting users list to " + users.size());
                users = users.subList(0, maxUsersPerGroup);
                return users;
            } else {
                LOGGER.debug("After shrinking total result size is " + users.size() + ". Continuing executing the recursion");
                HashSet<String> usersDNs = new HashSet<>();
                for (EntityResponse res : users) {
                    usersDNs.add((String) res.getKey());
                }
                totalResults = usersDNs;
            }
        }

        //* STOP CONDITION
        boolean isGroupEmpty = groupNames == null || groupNames.isEmpty();
        if (isGroupEmpty) {
            if (Utils.isEmpty(totalResults))
                return new ArrayList<>();
            LOGGER.debug("No more members could be found. Exiting from recursion");
            return filterUsers(endpoint, fields, totalResults).getAll();
        }

        queryRequest.addRequestedField(FieldType.COMMON_NAME);
        queryRequest.addRequestedField(FieldType.MEMBER);
        queryRequest.addRequestedField(FieldType.OBJECT_CLASS);

        Queue<String> partialNamesQueue = new LinkedList<>();
        int to = groupNames.size() < maxItemsPerRequest ? groupNames.size() : maxItemsPerRequest;
        for(int i = 1; i<= to; i ++){
            partialNamesQueue.add(groupNames.poll());
        }

        for (String groupName : partialNamesQueue) {
            LOGGER.trace("Group Name: " + groupName);
            queryAssembler.addPhrase(FieldType.DISTINGUISHED_NAME, PhraseOperator.EQUAL, groupName);
        }
        Sentence dnQuerySentence = queryAssembler.closeSentence(SentenceOperator.OR);

        Sentence groupRestrictionSentence = queryAssembler.addPhrase(FieldType.OBJECT_CLASS, PhraseOperator.EQUAL, "group").closeSentence(SentenceOperator.EMPTY);
        queryRequest.addSearchSentence(queryAssembler.addSentence(dnQuerySentence).addSentence(groupRestrictionSentence).closeSentence(SentenceOperator.AND));

        QueryResponse queryResponse = new QueryResponse();
        try(Connector connector = new Connector(queryRequest)) {
            Cursor cursor = connector.getCursor();
            while (cursor.hasNext()) {
                queryResponse.addPartitionResponse(cursor.next().get());
            }
        }
        for (EntityResponse res : queryResponse.getAll()) {
            for (Field field : res.getValue()) {
                if (field.getType() == FieldType.MEMBER && totalResults.add(field.getValue().toString())) {
                    groupNames.add(field.getValue().toString());
                }
            }
        }

        return getUsersInGroup(maxUsersPerGroup, groupNames, fields, endpoint, totalResults);
    }

}
