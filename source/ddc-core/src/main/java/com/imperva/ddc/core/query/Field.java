package com.imperva.ddc.core.query;

/**
 * Created by gabi.beyo on 18/06/2015.
 * Represent a data-field
 */
public class Field extends FieldInfo {

    private Object value;
 
    public Field() {
    	super();
    }

    public  Field(FieldType fieldType, Object fieldValue){
        super(fieldType);
        this.value = fieldValue;
    }

    public  Field(String fieldName, Object fieldValue){
        super(fieldName);
        this.value = fieldValue;
    }

    /**
     * @return field value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value field value
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
