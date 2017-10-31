package com.example.nikel.logoparkscanner.DataTypes.JSONTypeData;

import android.support.annotation.Nullable;

import com.example.nikel.logoparkscanner.DataTypes.GenericDataType;

/**
 * Created by nikel on 31.10.2017.
 */

public abstract class JSONSubPair extends GenericDataType {

    protected JSONSubPair(String field_name_eng, String field_type) {
        super(field_name_eng, field_type);
    }

}
