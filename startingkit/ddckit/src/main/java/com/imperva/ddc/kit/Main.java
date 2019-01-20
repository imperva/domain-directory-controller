package com.imperva.ddc.kit;


import com.imperva.ddc.core.Connector;
import com.imperva.ddc.core.commons.Utils;
import com.imperva.ddc.core.language.PhraseOperator;
import com.imperva.ddc.core.language.QueryAssembler;
import com.imperva.ddc.core.language.Sentence;
import com.imperva.ddc.core.language.SentenceOperator;
import com.imperva.ddc.core.query.*;
import com.imperva.ddc.service.DirectoryConnectorService;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gabi.beyo on 3/16/2017.
 */
public class Main {

    public static void main(String[] args) {
        useCase4();
    }

    private static void useCase4() {
        Endpoint endpoint = createEndpoint();
        QueryRequest queryRequest = createQueryRequest(endpoint);

        queryRequest.addSearchSentence(new QueryAssembler().addPhrase(FieldType.FIRST_NAME, PhraseOperator.NOTEQUAL, "THIS IS A TEST").closeSentence());

        try(Connector connector = new Connector(queryRequest)) {
            connector.execute();
            printRootDSE(endpoint.getLdapConnection());
        }
    }


    static void printRootDSE(LdapConnection ldapConnection){

        if(ldapConnection != null && ldapConnection.isConnected()){
            try {
                System.out.println(ldapConnection.getRootDse().toString());


            } catch (LdapException e) {
                System.out.println("Failed to resolve Base Path: " + e.toString());
            }
        }


    }





    private static Endpoint createEndpoint() {
        Endpoint endpoint = new Endpoint();
        endpoint.setSecuredConnection(false);
        endpoint.setPort(389);
        endpoint.setHost("");
        endpoint.setPassword("");
        endpoint.setUserAccountName(""); //* You can us the user's DistinguishedName as well
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
        queryRequest.setSizeLimit(1);
        queryRequest.setTimeLimit(1);
        return queryRequest;
    }
}
