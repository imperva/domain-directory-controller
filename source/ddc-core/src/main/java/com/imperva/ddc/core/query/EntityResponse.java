package com.imperva.ddc.core.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gabi.beyo on 30/06/2015.
 * Query response, contains all requested objects and their attributes
 */
public class EntityResponse implements Map.Entry {
    private String key;
    private List<Field> value = new ArrayList<Field>();


    /**
     * @param key Unique Directory object key
     */
    public void setKey(Object key){
        this.key = (String) key;
    }

    /**
     * @return Unique Directory object key
     */
    public Object getKey() {
        return key;
    }

    /**
     * @return Directory object value
     */
    public List<Field> getValue() {
        return value;
    }

    /**
     * @param value Directory object value
     * @return Old Directory object value
     */
    public Object setValue(Object value) {
        List<Field> oldValue = this.value;
        this.value = (List<Field>)value;
        return oldValue;
    }

    /**
     * Add Field to response list
     * @param value A {@link Field} containing an attribute result
     */
    public void addValue(Field value) {
        this.value.add(value);
    }


    /**
     * Add Field to response list
     * @param value Directory object value
     * @param name Directory object name
     * @param fieldType A {@link FieldType} Directory object type
     */
    public void addValue(Object value, String name, FieldType fieldType) {
        Field field = new Field();
        field.setValue(value);
        field.setName(name);
        field.setType(fieldType);
        this.value.add(field);
    }

    /**
     * Iterates over the returned values and searches the one with the matching FieldType
     * @param fieldType {@link FieldType}
     * @return The correspondent field {@link Field}
     */
    public Field find(FieldType fieldType){
        for(Field field :value){
            if(field.getType() == fieldType){
                return field;
            }
        }
        return null;
    }

    /**
     * Iterates over the returned values and searches the one with the matching field name
     * @param name
     * @return The correspondent field {@link Field}
     */
    public Field find(String name){
        for(Field field :value){
            if(field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return key;
    }

}
