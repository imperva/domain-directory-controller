package com.imperva.ddc.core.query;

import com.imperva.ddc.core.exceptions.BaseException;

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

    public ChangeRequest(){}

    public ChangeRequest add(String dn, FieldType fieldType, String value) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new AddModificationDetails(dn,field,value));
        return this;
    }

    public ChangeRequest add(String dn, String fieldName, String value) {
        Field field = new Field();
        field.setName(fieldName);
        modificationDetailsList.add(new AddModificationDetails(dn,field,value));
        return this;
    }

    public ChangeRequest add(FieldType fieldType, String value) {
        checkDNExists();
        return add(this.dn, fieldType, value);
    }

    public ChangeRequest add(String fieldName, String value) {
        checkDNExists();
        return add(this.dn, fieldName, value);
    }

    public ChangeRequest remove(String dn, FieldType fieldType) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new RemoveModificationDetails(dn,field));
        return this;
    }

    public ChangeRequest remove(String dn, String fieldName) {
        Field field = new Field();
        field.setName(fieldName);
        modificationDetailsList.add(new RemoveModificationDetails(dn,field));
        return this;
    }

    public ChangeRequest remove(FieldType fieldType) {
        checkDNExists();
        return remove(this.dn, fieldType);
    }

    public ChangeRequest remove(String fieldName) {
        checkDNExists();
        return remove(this.dn, fieldName);
    }

    public ChangeRequest remove(final String dn, final FieldType fieldType, final Object value) {
        modificationDetailsList.add(new RemoveModificationDetails(dn, new Field(fieldType, value)));
        return this;
    }

    public ChangeRequest remove(final FieldType fieldType, final Object value) {
        checkDNExists();
        return remove(dn, fieldType, value);
    }

    public ChangeRequest replace(FieldType fieldType, String value) {
        checkDNExists();
        return replace(this.dn, fieldType, value);
    }

    public ChangeRequest replace(String dn, FieldType fieldType, String value) {
        Field field = new Field();
        field.setType(fieldType);
        modificationDetailsList.add(new ReplaceModificationDetails(dn,field,value));
        return this;
    }

    public ChangeRequest replace(String fieldName, String value) {
        checkDNExists();
        return replace(this.dn, fieldName, value);
    }

    public ChangeRequest replace(String dn, String fieldName, String value) {
        Field field = new Field();
        field.setName(fieldName);
        modificationDetailsList.add(new ReplaceModificationDetails(dn,field,value));
        return this;
    }

    private void checkDNExists() {
        if (this.dn == null || this.dn.isEmpty()) {
            throw new BaseException("DN is required. Either use the ChangeRequest(DN) constructor or specify the desired DN in the method itself.");
        }
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