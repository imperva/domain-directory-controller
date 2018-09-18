package com.imperva.opensource.ddc.core.language.changeCreteria;

import com.imperva.opensource.ddc.core.query.Field;
import com.imperva.opensource.ddc.core.query.ModificationDetails;

import java.util.List;

/**
 * Created by gabi.beyo on 28/06/2015.
 */
public class ChangeCriteria {
    public List<ModificationDetails> getModificationDetailsList() {
        return modificationDetailsList;
    }

    public void setModificationDetailsList(List<ModificationDetails> modificationDetailsList) {
        this.modificationDetailsList = modificationDetailsList;
    }

    private List<ModificationDetails> modificationDetailsList;


}
