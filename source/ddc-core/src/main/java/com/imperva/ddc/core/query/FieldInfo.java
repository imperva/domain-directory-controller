package com.imperva.ddc.core.query;

public abstract class FieldInfo {

    private String name;
    private FieldType type;

    public FieldInfo(){}

    public  FieldInfo(String fieldName){
        this.name = fieldName;
    }

    public  FieldInfo(FieldType fieldType){
        this.type = fieldType;
    }

    /**
     * @return the real concrete Directory implementation field name
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
}
