package com.example.nikel.logoparkscanner.DataTypes;

/**
 * Created by nikel on 02.11.2017.
 */

public abstract class GenericDataTypeBuilder {
    private String field_name_eng;
    private String field_type;
    private String field_parent;
    private String field_value;

    public GenericDataTypeBuilder() {}



    abstract GenericDataType buildDataType();
    abstract GenericDataTypeBuilder setName_eng(final String field_name_eng);
    abstract GenericDataTypeBuilder setType(final String field_type);
    abstract GenericDataTypeBuilder setParent(final String field_parent);
    abstract GenericDataTypeBuilder setValue(final String field_value);

    protected void setField_name_eng(String field_name_eng) {
        this.field_name_eng = field_name_eng;
    }
    protected void setField_type(String field_type) {
        this.field_type = field_type;
    }
    protected void setField_parent(String field_parent) {
        this.field_parent = field_parent;
    }
    protected void setField_value(String field_value) {
        this.field_value = field_value;
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
    public String getField_value() {
        return field_value;
    }
}
