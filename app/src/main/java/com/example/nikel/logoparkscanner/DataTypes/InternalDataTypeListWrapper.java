package com.example.nikel.logoparkscanner.DataTypes;

import android.support.annotation.Nullable;

import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypes.InternalDataType;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nikel on 02.11.2017.
 */

public class InternalDataTypeListWrapper<T> extends ArrayList<T> {

    public T getElement(final String field_name_rus) {
        int index = findElement(field_name_rus);
        if(index < 0)
            return null;
        return this.get(index);
    }

    private int findElement(final String field_name_rus) {
        Iterator<T> iterator = this.iterator();
        while(iterator.hasNext()) {
            InternalDataTypeList<InternalDataType> temp = (InternalDataTypeList<InternalDataType>) iterator.next();

            if (field_name_rus.equals(temp.getField_name_rus()))
                return this.indexOf(temp);
        }
        return -1;
    }
}
