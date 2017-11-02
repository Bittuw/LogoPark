package com.example.nikel.logoparkscanner.DataTypes.InternalDataTypes;

import android.support.annotation.Nullable;

import com.example.nikel.logoparkscanner.DataTypes.GenericDataType;

/**
 * Created by nikel on 02.11.2017.
 */

public class InternalDataType extends GenericDataType {

    private String field_name_rus;
    private String field_target_parent;

    public InternalDataType(String field_name_eng, String field_type, String field_parent, String field_value, String field_name_rus, String field_target_parent) {
        super(field_name_eng, field_type);
        this.setField_parent(field_parent);
        this.setField_value(field_value);
        this.setField_name_rus(field_name_rus);
        this.setField_target_parent(field_target_parent);
    }

    public String getField_name_rus() {
        return field_name_rus;
    }
    public String getField_target_parent() {
        return field_target_parent;
    }

    private void setField_name_rus(String field_name_rus) {
        this.field_name_rus = field_name_rus;
    }
    private void setField_target_parent(String field_target_parent) {
        this.field_target_parent = field_target_parent;
    }


}
