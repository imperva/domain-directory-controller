package com.imperva.ddc.kit;


import com.imperva.ddc.core.*;
import com.imperva.ddc.core.language.*;

import com.imperva.ddc.core.query.*;
import com.imperva.ddc.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gabi.beyo on 3/16/2017.
 */
public class Main {

    public static void main(String[] args) {

        useCase1();

        useCase2();

        useCase3();

        useCase4();

        useCase5();

        useCase6();

        useCase7();

        useCase8();

        useCase9();

        useCase10();

        isEnabled();
    }

    private static void useCase1() {
        Endpoint endpoint = createEndpoint();
        ConnectionResponse connectionResponse = DirectoryConnectorService.authenticate(endpoint);
        boolean succeeded = !connectionResponse.isError();
        System.out.println("Use Case 1 - Authentication: " + succeeded);

    }

    private static void useCase2() {
        Endpoint endpoint = createEndpoint();
        endpoint.setIgnoreSSLValidations(true);
        QueryRequest queryRequest = createQueryRequest(endpoint);
        queryRequest.setObjectType(ObjectType.USER);
        //* Shortcut. Internally will add the relevant LDAP script to filter out any non human Entry (printers, machines etc.)

        queryRequest.addRequestedField(FieldType.EMAIL);
        queryRequest.addRequestedField(FieldType.CITY);
        queryRequest.addRequestedField(FieldType.DISTINGUISHED_NAME);

        QueryAssembler queryAssembler;
        queryAssembler = new QueryAssembler();
        Sentence firstNameSentence = queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "Donald").closeSentence();

        queryRequest.addSearchSentence(firstNameSentence);

        QueryResponse queryResponse;
        try(Connector connector = new Connector(queryRequest)) {
            queryResponse = connector.execute();
        }

        List fields = queryResponse.getAll().stream().map(res -> res.getValue()).collect(Collectors.toList());
        System.out.println("Use Case 2 - Query all users' phone number and city of users that their first name is 'Donald': " + fields.size() + "\n------------------\n");
    }


    private static void useCase3() {
        Endpoint endpoint = createEndpoint();
        QueryRequest queryRequest = createQueryRequest(endpoint);
        queryRequest.setObjectType(ObjectType.USER);
        //* Shortcut. Internally will add the relevant LDAP script to filter out any non human Entry (printers, machines etc.)

        queryRequest.addRequestedField(FieldType.EMAIL);
        queryRequest.addRequestedField(FieldType.CITY);

        QueryAssembler queryAssembler;
        queryAssembler = new QueryAssembler();
        Sentence firstNameSentence = queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "Gabriel").closeSentence();

        queryRequest.addSearchSentence(firstNameSentence);

        QueryResponse queryResponse;
        try(Connector connector = new Connector(queryRequest)) {
            queryResponse = connector.execute();
        }


        System.out.println("Use Case 3 - QueryResponse structure and data manipulation:");
        for (EntityResponse entityResponse : queryResponse.getAll()) {
            for (Field field : entityResponse.getValue()) {
                System.out.println("Val: " + field.getValue());
            }
        }
    }


    private static void useCase4() {
        Endpoint endpoint = createEndpoint();
        QueryRequest queryRequest = createQueryRequest(endpoint);

        queryRequest.setPageChunkSize(100); //* PAGING

        queryRequest.addSearchSentence(new QueryAssembler().addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "Gabriel").closeSentence());

        QueryResponse result = new QueryResponse();
        try(Connector connector = new Connector(queryRequest)) {
            Cursor cursor = connector.getCursor();
            while (cursor.hasNext()) {
                result.addPartitionResponse(cursor.next().get());
            }
        }

        System.out.println("Use Case 4 - Paging:");
        for (EntityResponse entityResponse : result.getAll()) {
            for (Field field : entityResponse.getValue()) {
                System.out.println("Val: " + field.getValue());
            }
        }
    }

    private static void useCase5() {

        Endpoint endpoint = createEndpoint();
        QueryRequest queryRequest = createQueryRequest(endpoint);

        queryRequest.addRequestedField(FieldType.EMAIL);
        queryRequest.addRequestedField(FieldType.CITY);
        queryRequest.addRequestedField(FieldType.PHONE_NUMBER);
        queryRequest.addRequestedField(FieldType.DISTINGUISHED_NAME);
        queryRequest.setPageChunkSize(100);

        QueryAssembler queryAssembler = new QueryAssembler();

        //* Create first sentence: users which their name results to be "Gabriel" AND thet are part of the IT department
        Sentence nameAndDepSentence = queryAssembler
                .addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "Gabriel")
                .addPhrase(FieldType.DEPARTMENT, PhraseOperator.EQUAL, "IT")
                .closeSentence(SentenceOperator.AND);

        //* Create the second sentence: users that their country results to be "Italy"
        Sentence countrySentence = queryAssembler
                .addPhrase(FieldType.COUNTRY, PhraseOperator.EQUAL, "Italy")
                .closeSentence();

        //* Glue the sentences with the OR operator
        Sentence finalSentence = queryAssembler
                .addSentence(nameAndDepSentence)
                .addSentence(countrySentence)
                .closeSentence(SentenceOperator.OR);

        queryRequest.addSearchSentence(finalSentence);

        QueryResponse result = new QueryResponse();
        try(Connector connector = new Connector(queryRequest)) {
            Cursor cursor = connector.getCursor();
            while (cursor.hasNext()) {
                result.addPartitionResponse(cursor.next().get());
            }
        }

        System.out.println("Use Case 5 - Are you ready?:");
        for (EntityResponse entityResponse : result.getAll()) {
            for (Field field : entityResponse.getValue()) {
                System.out.println("Val: " + field.getValue());
            }
        }
    }

    private static void useCase6() {
        //* Given as a parameter at runtime
        List<String> usersList = new ArrayList<String>() {{
            add("Gabriel");
            add("Noam");
            add("Shahar");
            add("Mor");
            add("Assaf");
            add("Viatly");
            add("Dror");
            add("Rina");
            add("Simcha");
        }};

        Endpoint endpoint = createEndpoint();
        QueryRequest queryRequest = createQueryRequest(endpoint);

        queryRequest.addRequestedField(FieldType.EMAIL);
        queryRequest.addRequestedField(FieldType.CITY);
        queryRequest.addRequestedField(FieldType.PHONE_NUMBER);
        queryRequest.addRequestedField(FieldType.DISTINGUISHED_NAME);
        queryRequest.setPageChunkSize(100);
        QueryAssembler queryAssembler = new QueryAssembler();

        //* Create the dynamic sentence: users which their name results to be values of the list
        for (String user : usersList) {
            queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, user);
        }

        Sentence usersSentence = queryAssembler.closeSentence(SentenceOperator.OR);
        Sentence depSentence = queryAssembler.addPhrase(FieldType.DEPARTMENT, PhraseOperator.EQUAL, "IT").closeSentence();
        Sentence nameAndDepSentence = queryAssembler.addSentence(usersSentence).addSentence(depSentence).closeSentence(SentenceOperator.AND);

        //* Create the second sentence: users that their country results to be "Italy"
        Sentence countrySentence = queryAssembler
                .addPhrase(FieldType.COUNTRY, PhraseOperator.EQUAL, "Italy")
                .closeSentence();

        //* Glue the sentences with the OR operator
        Sentence finalSentence = queryAssembler
                .addSentence(nameAndDepSentence)
                .addSentence(countrySentence)
                .closeSentence(SentenceOperator.OR);

        queryRequest.addSearchSentence(finalSentence);

        QueryResponse result = new QueryResponse();

        try(Connector connector = new Connector(queryRequest)) {
            Cursor cursor = connector.getCursor();
            while (cursor.hasNext()) {
                result.addPartitionResponse(cursor.next().get());
            }
        }

        System.out.println("Use Case 6 - Dynamic Queries:");
        for (EntityResponse entityResponse : result.getAll()) {
            for (Field field : entityResponse.getValue()) {
                System.out.println("Val: " + field.getValue());
            }
        }

    }

    private static void useCase7() {
        Endpoint endpoint = createEndpoint();
        QueryRequest queryRequest = createQueryRequest(endpoint);
        queryRequest.addRequestedField(FieldType.EMAIL);
        queryRequest.addRequestedField(FieldType.CITY);

        QueryAssembler queryAssembler = new QueryAssembler();
        Sentence firstNameSentence = queryAssembler.addPhrase(FieldType.FIRST_NAME, PhraseOperator.EQUAL, "Gabriel").closeSentence();

        queryRequest.addSearchSentence(firstNameSentence);

        QueryResponse queryResponse = null;
        try(Connector connector = new Connector(queryRequest)) {
            queryResponse = connector.execute();
        }


        //* Exceptions are stored in a key-value structure where the key is the endpoint's IP address
        System.out.println("Use Case 7 - Error Handling:");
        for (PartitionResponse partitionResponse : queryResponse.get()) {
            Status status = partitionResponse.getStatus().get("10.100.10.100");
            System.out.println("Exception: " + status.getError());
        }
    }


    private static void useCase8() {
        Endpoint endpoint = createEndpoint();
        endpoint.setIgnoreSSLValidations(true);
        ChangeRequest changeRequest = new ChangeRequest("<The Distinguished Name of the AD object to change>");
        changeRequest.add(FieldType.CITY, "<value>");//* Add new field with value
        changeRequest.remove(FieldType.EMAIL);//* Remove field
        changeRequest.replace(FieldType.COUNTRY, "<value>");//* Replace field's value
        changeRequest.remove("sn");

        changeRequest.setEndpoint(endpoint);

        try (Connector connector = new Connector(changeRequest)) {
            connector.executeChangeRequest();
        }
    }


    private static void useCase9() {

        Endpoint endpoint = createEndpoint();
        endpoint.setIgnoreSSLValidations(true);

        RemoveRequest removeRequest = new RemoveRequest("<The Distinguished Name of the AD object to remove>");
        removeRequest.setEndpoint(endpoint);

        try (Connector connector = new Connector(removeRequest)) {
            connector.executeRemoveRequest();
        }
    }

    private static void useCase10() {

        Endpoint endpoint = createEndpoint();
        endpoint.setIgnoreSSLValidations(true);

        String dn = "<The Distinguished Name of the AD object to add>";

        AddRequest addRequest = new AddRequest(dn);
        addRequest.setEndpoint(endpoint);

        addRequest.
                addField(new Field(FieldType.OBJECT_CLASS,"top")).
                addField(new Field(FieldType.OBJECT_CLASS,"person")).
                addField(new Field(FieldType.OBJECT_CLASS,"user")).
                addField(new Field("cn","<last name>")).
                /* NOTE: The CN MUST BE IDENTICAL TO THE CN SPECIFIED IN YOUR DN
                        If your DN is: 'CN=Gabi,OU=Users', then the CN should be 'Gabi'
                */
                addField(new Field("givenName","<first name>")).
                addField(new Field("sn", "<last name>"));


        try (Connector connector = new Connector(addRequest)) {
            connector.executeAddequest();
        }
    }



    private static Endpoint createEndpoint() {
        return createEndpoint("<YOUR IP>",389,"<DOMAIN>\\<NAME>","<YOUR PASS>",false);
    }

    private static Endpoint createEndpoint(String host, int port, String userName, String pass, boolean isSecured) {
        Endpoint endpoint = new Endpoint();
        endpoint.setSecuredConnection(isSecured);
        endpoint.setPort(port);
        endpoint.setHost(host);
        endpoint.setPassword(pass);
        endpoint.setUserAccountName(userName); //* You can us the user's DistinguishedName as well
        //*endpoint.setSecondaryPort(389);
        //*endpoint.setSecondaryHost("10.100.10.100");
        //*endpoint.setSecuredConnectionSecondary(false);
        return endpoint;
    }

    private static QueryRequest createQueryRequest(final Endpoint endpoint) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDirectoryType(DirectoryType.MS_ACTIVE_DIRECTORY);
        queryRequest.setEndpoints(new ArrayList<Endpoint>() {{
            add(endpoint);
        }});
        queryRequest.setSizeLimit(1000);
        queryRequest.setTimeLimit(1000);
        return queryRequest;
    }

    private static void isEnabled() {
        Endpoint endpointAdmin = createEndpoint("<10.10.10.10>",389,"<domain\\administrator>","<pass>",false);
        Endpoint endpointToCheckIfEnabled = createEndpoint("<10.10.10.10>",389,"domain\\userToCheckIfEnabled","<pass>",false);
        ConnectionResponse connectionResponse = DirectoryConnectorService.authenticate(endpointAdmin,true, endpointToCheckIfEnabled);
        boolean succeeded = !connectionResponse.isError();
        System.out.println("Is Enabled - User is Authenticated and Enabled: " + succeeded);
    }
}
