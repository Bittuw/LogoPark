package com.example.nikel.logoparkscanner.DataTypes.InternalTypeData.InternalData;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

import com.example.nikel.logoparkscanner.DataTypes.InternalTypeData.InternalSubPair;
import com.example.nikel.logoparkscanner.DataTypes.JSONTypeData.JSONSubPair;

import java.util.Iterator;

/**
 * Created by nikel on 31.10.2017.
 */

public class InternalSubData extends InternalSubPair{

    private InternalSingleData[] field_fields;

    protected InternalSubData(String field_name_eng, String field_type, String field_name_rus, InternalSingleData[] array) {
        super(field_name_eng, field_type, field_name_rus);
    }

    public InternalSingleData[] getField_fields() {
        return field_fields;
    }

    @Nullable
    public InternalSingleData findByName(String find_by_name) {
        for (InternalSingleData field:field_fields) {
            if (field.getField_name_rus().equals(find_by_name))
                return field;
        }
        return null;
    }

    public Iterator getIterator() {
        return new DataIterator();
    }

    private class DataIterator implements Iterator<InternalSingleData> {

        private int index = field_fields.length;

        @Override
        public boolean hasNext() {
            return index < field_fields.length;
        }

        @Override
        public InternalSingleData next() {
            return field_fields[index++];
        }
    }
}
