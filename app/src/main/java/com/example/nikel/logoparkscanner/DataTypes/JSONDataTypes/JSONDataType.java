package com.example.nikel.logoparkscanner.DataTypes.JSONDataTypes;

import com.example.nikel.logoparkscanner.DataTypes.GenericDataType;

/**
 * Created by nikel on 02.11.2017.
 */

public class JSONDataType extends GenericDataType {

    public JSONDataType(String field_name_eng, String field_type, String field_parent, String field_value) {
        super(field_name_eng, field_type);
        this.setField_parent(field_parent);
        this.setField_value(field_value);
    }

}
