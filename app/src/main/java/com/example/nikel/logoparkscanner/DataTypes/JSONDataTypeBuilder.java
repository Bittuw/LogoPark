package com.example.nikel.logoparkscanner.DataTypes;

import com.example.nikel.logoparkscanner.DataTypes.GenericDataTypeBuilder;
import com.example.nikel.logoparkscanner.DataTypes.JSONDataTypes.JSONDataType;

/**
 * Created by nikel on 02.11.2017.
 */

public class JSONDataTypeBuilder extends GenericDataTypeBuilder {

    @Override
    public JSONDataType buildDataType() {
        return new JSONDataType(this.getField_name_eng(), this.getField_type(), this.getField_parent(), this.getField_value());
    }

    @Override
    public JSONDataTypeBuilder setName_eng(String field_name_eng) {
        this.setField_name_eng(field_name_eng);
        return this;
    }

    @Override
    public JSONDataTypeBuilder setType(String field_type) {
        this.setField_type(field_type);
        return this;
    }

    @Override
    public JSONDataTypeBuilder setParent(String field_parent) {
        this.setField_parent(field_parent);
        return this;
    }

    @Override
    public JSONDataTypeBuilder setValue(String field_value) {
        this.setField_value(field_value);
        return this;
    }
}

