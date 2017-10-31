package com.example.nikel.logoparkscanner.DataTypes.InternalTypeData;

import android.support.annotation.Nullable;

/**
 * Created by nikel on 31.10.2017.
 */

public class InternalSinglePair extends InternalSubPair {

    private String field_value;
    private final String field_parent;

    public InternalSinglePair(String field_name_eng, String field_type, String field_name_rus, String field_value, @Nullable String field_parent) {
        super(field_name_eng, field_type, field_name_rus);
        this.field_value = field_value;
        this.field_parent = field_parent;
    }

    public String getField_value() {
        return field_value;
    }

    public String getField_parent() {
        return field_parent;
    }

    public void setField_value(String field_value) {
        this.field_value = field_value;
    }

}
