package com.example.nikel.logoparkscanner.DataTypes;

/**
 * Created by nikel on 31.10.2017.
 */

public abstract class GenericDataType {
    private final String field_name_eng;
    private final String field_type;

    protected GenericDataType(String field_name_eng, String field_type) {
        this.field_name_eng = field_name_eng;
        this.field_type = field_type;
    }

    public String getField_name_eng() {
        return field_name_eng;
    }

    public String getField_type() {
        return field_type;
    }
}
