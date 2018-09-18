package com.imperva.opensource.ddc.core.query;

import com.imperva.opensource.ddc.core.commons.Utils;
import com.imperva.opensource.ddc.core.language.*;
import org.apache.directory.ldap.client.api.LdapConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by gabi.beyo on 18/06/2015.
 */
public class QueryRequest extends Request {
    private ObjectType objectType;
    private List<Field> requestedFields = new ArrayList<Field>();
    private Sentence searchSentence = null;
    private String searchText;
    private ReferralsHandling referralsHandling;
    private Integer sizeLimit = 1000;
    private Integer timeLimit = 30;
    private int pageChunkSize;
    private String searchSentenceText;
    private List<Endpoint> endpoints = new ArrayList<Endpoint>();

    /**
     * @return Used in Paged scenarios, specify the max chunk size of each roundtrip
     */
    public int getPageChunkSize() {
        return this.pageChunkSize;
    }

    /**
     * @param pageChunkSize Used in Paged scenarios, specify the max chunk size of each roundtrip
     */
    public void setPageChunkSize(int pageChunkSize) {
        this.pageChunkSize = pageChunkSize;

    }

    /**
     * @param objectType A {@link ObjectType} which indicates the requested object to search
     */
    public void setObjectType(ObjectType objectType) {
        Sentence baseSentence = createBaseSentence(objectType);
        this.addSearchSentence(baseSentence);
        this.objectType = objectType;
    }

    /**
     * @return A {@link ObjectType} which indicates the requested object to search
     */
    public ObjectType getObjectType() {
        return this.objectType;
    }

    /**
     * @param fields A list of {@link Field} used to indicate the requested field per object to retrieve
     *               When not specified the query will return as result all the available attributes
     */
    public void setRequestedFields(List<Field> fields) {
        this.requestedFields = fields;
    }

    /**
     * @return A list of {@link Field} used to indicate the requested field per object to retrieve
     * When not specified the query will return as result all the available attributes
     */
    public List<Field> getRequestedFields() {
        return this.requestedFields;
    }



    /**
     * @param searchSentences A {@link Sentence} which express part of the query
     */
    public void setSearchSentence(Sentence searchSentences) {
        this.searchSentence = searchSentences;
    }

    /**
     * @return A {@link Sentence} which express part of the query
     */
    public Sentence getSearchSentence() {
        return this.searchSentence;
    }

    /**
     * @return Free style search, when exist overrides {@link Sentence} and {@link Phrase} representation
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * @param searchText Free style search, when exist overrides Sentences and Phrases
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @return The final search text translated by DDC from objects representation i.e {@link Sentence} and {@link Phrase}
     */
    public String getSearchSentenceText() {
        return searchSentenceText;
    }

    /**
     * @param searchSentenceText The final search text translated by DDC from objects representation i.e {@link Sentence} and {@link Phrase}
     */
    public void setSearchSentenceText(String searchSentenceText) {
        this.searchSentenceText = searchSentenceText;
    }

    /**
     * @return List of {@link Endpoint}, used in multi forest scenario
     */
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * @param endpoints List of {@link Endpoint}, used in multi forest scenario
     */
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }


    /**
     * @return A {@link ReferralsHandling} which dictates the way to handle Referrals
     */
    public ReferralsHandling getReferralsHandling() {
        return referralsHandling;
    }

    /**
     * @param referralsHandling A {@link ReferralsHandling} which dictates the way to handle Referrals
     */
    public void setReferralsHandling(ReferralsHandling referralsHandling) {
        this.referralsHandling = referralsHandling;
    }

    /**
     * @return Maximum number of entries to retrieve
     */
    public Integer getSizeLimit() {
        return sizeLimit;
    }

    /**
     * @param sizeLimit Maximum number of entries to retrieve
     */
    public void setSizeLimit(Integer sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    /**
     * @return Maximum time to wait for a response from the server (in seconds).
     *  A value of 0 in this field indicates that no client-requested time-limit restrictions are in effect for the search.
     */
    public Integer getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit Maximum time to wait for a response from the server (in seconds).
     *  A value of 0 in this field indicates that no client-requested time-limit restrictions are in effect for the search.
     */
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Helper function used for adding requested fields {@link Field} by field-name in a fluent coding style instead of using setRequestedField
     * @param fieldName A string which indicates the name of the added field
     * @return
     */
    public QueryRequest addRequestedField(String fieldName) {
        Field field = new Field();
        field.setName(fieldName);
        requestedFields.add(field);
        return this;
    }

    /**
     * Helper function used for adding requested fields {@link Field} by field-type in a fluent coding style instead of using setRequestedField
     * @param fieldType A {@link FieldType} which indicates the type of the added field
     * @return
     */
    public QueryRequest addRequestedField(FieldType fieldType) {
        Field field = new Field();
        field.setType(fieldType);
        requestedFields.add(field);
        return this;
    }

    /**
     * @return return true if request isa Paged request, otherwise false
     */
    public boolean isPaged() {
        if (getPageChunkSize() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Helper function used for adding {@link Endpoint} in a fluent coding style instead of using setEndpoints
     *
     * @param endpoint A {@link Endpoint} objects
     * @return
     */
    public QueryRequest addEndpoint(Endpoint endpoint) {
        if (this.endpoints != null) {
            this.endpoints.add(endpoint);
        }
        return this;
    }

    /**
     * @param objectType A {@link ObjectType} which indicates which object type to search.
     *                   If present, system will add the appropriate search objects {@link Sentence} and {@link Phrase}
     * @return
     */
    Sentence createBaseSentence(ObjectType objectType) {
        Sentence baseFilterSentence;
        QueryAssembler queryAssembler = queryAssemblerGetInstance();
        switch (objectType) {
            case USER:
                baseFilterSentence = queryAssembler.addPhrase(FieldType.OBJECT_CLASS, PhraseOperator.EQUAL, "user")
                        .addPhrase(FieldType.OBJECT_CATEGORY, PhraseOperator.EQUAL, "person")
                        .closeSentence(SentenceOperator.AND);
                break;
            case GROUP:
                baseFilterSentence = queryAssembler.addPhrase(FieldType.OBJECT_CLASS, PhraseOperator.EQUAL, "group")
                        .closeSentence(SentenceOperator.EMPTY);
                break;
            case COMPUTER:
                return null;
            case PRINTER:
                return null;
            case ALL:
                return null;
            default:
                return null;
        }
        return baseFilterSentence;
    }

    /**
     * @param searchSentences A {@link Sentence} append new Sentence if other Sentences exist, otherwise set as first Sentence
     */
    public void addSearchSentence(Sentence searchSentences) {
        if (this.searchSentence == null) {
            this.searchSentence = searchSentences;
        } else {
            Sentence fullSentence = queryAssemblerGetInstance().appendSentence(searchSentences, this.getSearchSentence());
            this.searchSentence = fullSentence;
        }
    }

    /**
     * Both {@link Sentence} and {@link Phrase} extends from {@link Word}.
     * This function is used in cases where there is a need to keep track of specific {@link Sentence} or {@link Phrase}
     * @param id Word identifier
     * @return Requested Word object if found, otherwise null
     */
    public Word find(String id){
        Sentence sentence = this.getSearchSentence();
        return find(id,sentence);
    }

    Word find(String id, Word nextWord) {
        if(nextWord != null && nextWord.getId().equals(id)){
            return nextWord;
        } else if(nextWord == null) {
            return null;
        }

        if(nextWord instanceof Sentence) {
            Sentence sentence = (Sentence) nextWord;
            for (Word word : sentence.getSentences()) {
                Word detectedWord =  find(id, word);
                if(detectedWord != null) {
                    return detectedWord;
                }
            }
        }
        return null;
    }


    QueryAssembler queryAssemblerGetInstance() {
        return new QueryAssembler();
    }


    @Override
    public void close() {
        if (!Utils.isEmpty(endpoints)) {
            endpoints.forEach((e) -> {
                if (e != null) {
                    e.close();
                    e.setLdapConnection(null);
                    e.setDestinationType(DestinationType.NONE);
                }
            });
        }
    }
}