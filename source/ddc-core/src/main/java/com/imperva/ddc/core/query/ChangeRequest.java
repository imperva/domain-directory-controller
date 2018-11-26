package com.imperva.ddc.core.query;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gabi.beyo on 18/06/2015.
 */
public class ChangeRequest extends Request{

    private List<ModificationDetails> modificationDetailsList = new ArrayList<>();

    private String dn;


    private Endpoint endpoint;


    public ChangeRequest(String dn){
        this.dn=dn;
    }

    public ChangeRequest add(String dn, FieldType fieldType, String value) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new AddModificationDetails(dn,field,value));
        return this;
    }

    public ChangeRequest remove(String dn, FieldType fieldType) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new RemoveModificationDetails(dn,field));
        return this;
    }

    public ChangeRequest replace(String dn, FieldType fieldType, String value) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new ReplaceModificationDetails(dn,field,value));
        return this;
    }

    public ChangeRequest add(FieldType fieldType, String value) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new AddModificationDetails(dn,field,value));
        return this;
    }

    public ChangeRequest remove(FieldType fieldType) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new RemoveModificationDetails(dn,field));
        return this;
    }

    public ChangeRequest replace(FieldType fieldType, String value) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new ReplaceModificationDetails(dn,field,value));
        return this;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }


    public List<ModificationDetails> getModificationDetailsList() {
        return modificationDetailsList;
    }

    public void setModificationDetailsList(List<ModificationDetails> modificationDetailsList) {
        this.modificationDetailsList = modificationDetailsList;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    @Override
    public void close() {
        if (endpoint!=null) {
            endpoint.close();
            endpoint.setLdapConnection(null);
            endpoint.setDestinationType(DestinationType.NONE);
        }
    }

}