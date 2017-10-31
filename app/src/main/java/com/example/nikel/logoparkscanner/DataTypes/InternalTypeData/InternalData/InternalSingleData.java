package com.example.nikel.logoparkscanner.DataTypes.InternalTypeData.InternalData;

import android.support.annotation.Nullable;

import com.example.nikel.logoparkscanner.DataTypes.InternalTypeData.InternalSinglePair;

/**
 * Created by nikel on 31.10.2017.
 */

public class InternalSingleData extends InternalSinglePair {

    public InternalSingleData(String field_name_eng, String field_type, String field_name_rus, String field_value, @Nullable String field_parent) {
        super(field_name_eng, field_type, field_name_rus, field_value, field_parent);
    }

}
