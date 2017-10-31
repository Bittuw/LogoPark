package com.example.nikel.logoparkscanner.DataTypes.JSONTypeData;

/**
 * Created by nikel on 31.10.2017.
 */

public abstract class JSONSinglePair extends JSONSubPair {

    private final String field_value;
    private final String fielf_parent;

    public JSONSinglePair(String field_name_eng, String field_type, String field_value, String field_parent) {
        super(field_name_eng, field_type);
        this.field_value = field_value;
        this.fielf_parent = field_parent;
    }

    public String getField_value() {
        return field_value;
    }

    public String getFielf_parent() {
        return fielf_parent;
    }
}
