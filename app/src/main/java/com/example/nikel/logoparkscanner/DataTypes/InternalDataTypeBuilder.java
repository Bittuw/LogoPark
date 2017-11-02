package com.example.nikel.logoparkscanner.DataTypes;


import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypes.InternalDataType;

/**
 * Created by nikel on 02.11.2017.
 */

public class InternalDataTypeBuilder extends GenericDataTypeBuilder {

    private String field_name_rus;
    private String field_targer_parent;

    @Override
    public InternalDataType buildDataType() {
        return new InternalDataType(this.getField_name_eng(), this.getField_type(), this.getField_parent(), this.getField_value(), this.getField_name_rus(), this.getField_target_parent());
    }

    public String getField_name_rus() {
        return field_name_rus;
    }
    public String getField_target_parent() {
        return field_targer_parent;
    }

    public InternalDataTypeBuilder setName_eng(final String field_name_eng) {
        this.setField_name_eng(field_name_eng);
        return this;
    }
    public InternalDataTypeBuilder setType(final String field_type) {
        this.setField_type(field_type);
        return this;
    }
    public InternalDataTypeBuilder setParent(final String field_parent) {
        this.setField_parent(field_parent);
        return this;
    }
    public InternalDataTypeBuilder setValue(final String field_value) {
        this.setField_value(field_value);
        return this;
    }
    public InternalDataTypeBuilder setName_rus(final String field_name_rus) {
        this.field_name_rus = field_name_rus;
        return this;
    }
    public InternalDataTypeBuilder setTargetParent(final String field_target_parent) {
        this.field_targer_parent = field_target_parent;
        return this;
    }
}
