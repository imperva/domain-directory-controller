package com.imperva.ddc.core.query;

/**
 * Created by gabi.beyo on 18/06/2015.
 * Represent a data-field
 */
public class Field {
    private String name;
    private Object value;
    private FieldType type;

    /**
     * @return the real concrete Dyrectory implementation field name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the real concrete Directory implementation field name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return type A {@link FieldType} which specifies the common friendly field type
     */
    public FieldType getType() {
        return type;
    }

    /**
     * @param type A {@link FieldType} which specifies the common friendly field type
     */
    public void setType(FieldType type) {
        this.type = type;
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
