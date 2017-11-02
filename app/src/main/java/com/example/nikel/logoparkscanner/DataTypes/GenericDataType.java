package com.example.nikel.logoparkscanner.DataTypes;


/**
 * Created by nikel on 31.10.2017.
 */

public abstract class GenericDataType {

    private final String field_name_eng;
    private final String field_type;
    private String field_parent;
    private String field_value;

    protected GenericDataType(String field_name_eng, String field_type) {
        this.field_name_eng = field_name_eng;
        this.field_type = field_type;
    }

    public GenericDataType setParent(final String field_parent) {
        this.field_parent = field_parent;
        return this;
    }

    public GenericDataType setValue(final String field_value) {
        this.field_value = field_value;
        return this;
    }

    public String getField_name_eng() {
        return field_name_eng;
    }

    public String getField_type() {
        return field_type;
    }

    public String getField_parent() {
        return field_parent;
    }

    protected void setField_parent(String field_parent) {
        this.field_parent = field_parent;
    }

    public String getField_value() {
        return field_value;
    }

    protected void setField_value(String field_value) {
        this.field_value = field_value;
    }

}
