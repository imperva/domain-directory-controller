package com.imperva.ddc.core.query;

import com.imperva.ddc.core.language.changeCreteria.ChangeCriteria;
import java.util.List;

public class ChangeRequestBuilder extends RequestBuilder {

    private ChangeRequest changeRequest;

    List<ModificationDetails> modificationDetailsList;

    public ChangeRequest getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
    }

    public void translateChangeFields() {
        List<ModificationDetails> modificationDetailsList = getChangeRequest().getModificationDetailsList();
        for (ModificationDetails modificationDetails: modificationDetailsList) {
            translateField(modificationDetails.getAttribute());
        }
    }


    public ChangeCriteria get() {
        ChangeCriteria changeCriteria= new ChangeCriteria();
        changeCriteria.setModificationDetailsList(changeRequest.getModificationDetailsList());
        return changeCriteria;
    }

    @Override
    public void translateFilter() {
        //todo this is never called, not sure this works. Review the entire flow
        //todo Consider: this translation doesn't belong to here. It is not a filter translation
        //todo this method is not meant to change any state of internal objects. Instead it should create a new state and add it to the criteria
        if (modificationDetailsList == null && modificationDetailsList.isEmpty()) {
            return;
        }
        Field dnField= new Field();
        dnField.setType(FieldType.DISTINGUISHED_NAME);
        for(ModificationDetails modificationDetails: modificationDetailsList)
            modificationDetails.setDn(escapeSpecialChars(modificationDetails.getDn(), dnField));
    }



}
