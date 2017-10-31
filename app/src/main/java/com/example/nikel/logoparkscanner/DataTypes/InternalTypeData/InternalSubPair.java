package com.example.nikel.logoparkscanner.DataTypes.InternalTypeData;

import android.support.annotation.Nullable;

import com.example.nikel.logoparkscanner.DataTypes.GenericDataType;

/**
 * Created by nikel on 31.10.2017.
 */

public class InternalSubPair extends GenericDataType {

    private final String field_name_rus;

    protected InternalSubPair(String field_name_eng, String field_type, @Nullable String field_name_rus) {
        super(field_name_eng, field_type);
        this.field_name_rus = field_name_rus;
    }

    public String getField_name_rus() {
        return field_name_rus;
    }

}
