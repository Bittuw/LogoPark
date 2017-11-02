package com.example.nikel.logoparkscanner.DataTypes;

import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypes.InternalDataType;

import java.util.ArrayList;

/**
 * Created by nikel on 02.11.2017.
 */

public class InternalDataTypeList<T> extends ArrayList<T>{

    private String field_name_rus;

    public String getField_name_rus() {
        return field_name_rus;
    }
    public void setField_name_rus(String field_name_rus) {
        this.field_name_rus = field_name_rus;
    }

}
