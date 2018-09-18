package com.imperva.opensource.ddc.core;

import com.imperva.opensource.ddc.core.commons.Utils;
import com.imperva.opensource.ddc.core.query.*;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchResultReference;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabi.beyo on 05/07/2015.
 */
abstract class QueryRequestExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryRequestExecutor.class.getName());
    QueryRequest queryRequest;

    QueryRequestExecutor(QueryRequest queryRequest) {
        this.queryRequest = queryRequest;
    }

     List<Entry> run(SearchCursor cursor) {
         List<Entry> results = new ArrayList<>();
         try {
             while (cursor.next()) {
                 Response response = cursor.get();
                 if (response instanceof SearchResultEntry) {
                     Entry entry = ((SearchResultEntry) response).getEntry();
                     results.add(entry);
                 } else if (response instanceof SearchResultReference) {
                     //* DO NOTHING
                 } else {
                     //* DO NOTHING
                 }
             }
         } catch (LdapException e) {
             String error = "Can't execute LDAP request";
             LOGGER.error(error, e.getStackTrace());
             throw new RuntimeException(error);
         } catch (CursorException e) {
             String error = "LDAP Cursor failed";
             LOGGER.error(error, e.getStackTrace());
             throw new RuntimeException(error);
         }
         return results;
     }

    ApacheAPIConverter parserGetInstance() {
        return new ApacheAPIConverter();
    }

    String tryResolveBasePath(LdapConnection ldapConnection, String baseSearchPath){
        if(!Utils.isEmpty(baseSearchPath)) {
            return baseSearchPath;
        }

        if(ldapConnection != null && ldapConnection.isConnected()){
            Entry rootDSEInfo;
            try {
                rootDSEInfo = ldapConnection.getRootDse("rootDomainNamingContext");
                return rootDSEInfo.get("rootDomainNamingContext").getString();
            } catch (LdapException e) {
                LOGGER.error("Failed to resolve Base Path", e);
            }
        }
        return null;
    }

    DriverBase driverGetInstance() {
        return new DriverHostResolverDecorator(new DriverRobustDecorator(new Driver()));
    }


    abstract QueryResponse execute();

}
