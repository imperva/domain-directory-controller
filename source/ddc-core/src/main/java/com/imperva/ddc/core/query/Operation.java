package com.imperva.ddc.core.query;

/**
 * Created by gabi.beyo on 18/06/2015.
 * Specify the requested object
 * It's a shortcut which enables consumers to specify the object need, internally the specific criteria is added to the Search Request
 */
public enum Operation {
    ADD,
    REMOVE,
    REPLACE
}
