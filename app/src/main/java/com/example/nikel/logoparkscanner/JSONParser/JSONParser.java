package com.example.nikel.logoparkscanner.JSONParser;


import android.support.annotation.Nullable;
import android.util.Log;

import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypeList;
import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypes.InternalDataType;
import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypeBuilder;
import com.example.nikel.logoparkscanner.DataTypes.InternalDataTypeListWrapper;
import com.example.nikel.logoparkscanner.DataTypes.JSONDataTypes.JSONDataType;
import com.example.nikel.logoparkscanner.DataTypes.JSONDataTypeBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nikel on 29.09.2017.
 */

public class JSONParser {

    private JSONObject json;
    private final String[] mStructResource;
    private final String[] mMarkupResource;

    private android.support.v4.util.ArrayMap<String, String> mMarkupResourceList; // List from MarkupResource, spelling of Struct pseudo lang
    private ArrayList<JSONDataType> mJSONList; // List from JSONObject transformed to InternalType
    @Nullable private ArrayList<String> jsonFilter; // List of filter line in JSON
    private ArrayList<InternalDataType> mInternalList; // List from StructResource, Struct and map of future TargetList
    private ArrayList<InternalDataType> mInternalUnhandledDataList; // List of Unhandled InternalDataType after transformed from JSONDataType to TargetList
    private InternalDataTypeListWrapper<InternalDataTypeList<InternalDataType>> mTargetList; // List which class build after all transformations

    public JSONParser(final JSONObject json, @Nullable final ArrayList<String> jsonFilter, final String[] mStructResource, final String[] mMarkupResource) {
        this.json = json;
        this.jsonFilter = jsonFilter;
        this.mStructResource = mStructResource;
        this.mMarkupResource = mMarkupResource;
        makeInternalList();
        makeMarkupResourceMap();
    }

    private boolean makeInternalList() {
        mInternalList = new ArrayList<>();

        for (String line: mStructResource) {

            String[] temp = line.split("__");
            InternalDataTypeBuilder builder = new InternalDataTypeBuilder();

            switch (mMarkupResourceList.get(temp[0])) {
                case "pk": // Primary_kay
                    mInternalList.add(
                            builder
                                .setType(temp[0])
                                .setName_eng(temp[1])
                                .setName_rus(temp[2])
                                .buildDataType()
                    );
                    break;
                case "sc": // Secondary_Key
                    if(temp.length == 4) {
                        mInternalList.add(
                                builder
                                    .setType(temp[0])
                                    .setName_eng(temp[1])
                                    .setName_rus(temp[2])
                                    .setParent(temp[3])
                                    .buildDataType()
                        );
                    } else if (temp.length == 5) {
                        mInternalList.add(
                                builder
                                    .setType(temp[0])
                                    .setName_eng(temp[1])
                                    .setName_rus(temp[2])
                                    .setParent(temp[3])
                                    .setTargetParent(temp[4])
                                    .buildDataType()
                        );
                    } else if (4 < temp.length && temp.length < 5) { // If line in structResource are not Available
                        Log.e(this.getClass().getSimpleName(), "Unsigned length of line in mStructResource");
                        return false;
                    }
                    break;

                default:
                    Log.e(this.getClass().getSimpleName(), "Find unsupported tag in markup\n Supported tag: pk, sc. See strings." );
                    return false;
            }
        }

        return true;
    }
    private void makeMarkupResourceMap() {
        mMarkupResourceList = new android.support.v4.util.ArrayMap<>();
        for (String element:mMarkupResource) {
            mMarkupResourceList.put(element, element);
        }
    }
    private boolean makeTargetList() { // TODO disable raw data, may be create new WrapperList
        mTargetList = new InternalDataTypeListWrapper<>();
        Iterator iterator = mInternalList.iterator();

        while (iterator.hasNext()) {
            InternalDataType temp = (InternalDataType) iterator.next();
            if (temp.getField_type().equals("pk")) {
                InternalDataTypeList<InternalDataType> element = new InternalDataTypeList<>();
                element.setField_name_rus(temp.getField_name_rus());
                mTargetList.add(element);
            } else if(temp.getField_type().equals("sc")){
                (mTargetList.getElement(temp.getField_target_parent())).add(temp);
            }
        }
        return true;
    }

    private void makeJSONList(JSONObject tempJSON, @Nullable String parent) {
        mJSONList = new ArrayList<>();
        for (Iterator<String> iterator = json.keys(); iterator.hasNext();) {
            String key = iterator.next();

            JSONDataTypeBuilder builderJSON = new JSONDataTypeBuilder();

            try {
                if (tempJSON.get(key) instanceof JSONObject || jsonFilter.contains(key)) {
                    makeJSONList(tempJSON.getJSONObject(key), key);
                } else if (jsonFilter.contains(key) && parent == null){
                    mJSONList.add(
                            builderJSON
                                .setName_eng(key)
                                .setType("sc")
                                .setParent(parent)
                                .setValue(tempJSON.getString(key))
                                .buildDataType()
                    );

                    InternalDataType tempInternal = findInInternalList(tempJSON.getString(key));
                    if (tempInternal != null) {
                        tempInternal.setValue(tempJSON.getString(key));
                    }
                } else if (jsonFilter == null && parent != null) {
                    mJSONList.add(
                            builderJSON
                                    .setName_eng(key)
                                    .setType("sc")
                                    .setParent(parent)
                                    .setValue(tempJSON.getString(key))
                                    .buildDataType()
                    );

                    InternalDataType tempInternal = findInInternalList(tempJSON.getString(key));
                    if (tempInternal != null) {
                        tempInternal.setValue(tempJSON.getString(key));
                    }
                } else if (jsonFilter.contains(key) && parent == null) {
                    mJSONList.add(
                            builderJSON
                                    .setName_eng(key)
                                    .setType("sc")
                                    .setParent(parent)
                                    .setValue(tempJSON.getString(key))
                                    .buildDataType()
                    );

                    InternalDataType tempInternal = findInInternalList(tempJSON.getString(key));
                    if (tempInternal != null) {
                        tempInternal.setValue(tempJSON.getString(key));
                    }
                }
            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), "Parsing error",e);
            } catch (NullPointerException e) {
                Log.e(this.getClass().getSimpleName(), "Parsing error",e);
            }
        }
    }

    private void makeTargetListFrom() {}

    @Nullable
    private InternalDataType findInInternalList(final String field_name_eng) { // TODO here is a little crutch, must find substitutions
        Iterator iterator = mInternalList.iterator();

        while(iterator.hasNext()) {
            InternalDataType temp = (InternalDataType) iterator.next();
            if (temp.getField_name_eng().equals(field_name_eng))
                return temp;
        }
        return null;
    }

    public ArrayList<JSONDataType> getmJSONList() {
        return mJSONList;
    }

    public InternalDataTypeListWrapper<InternalDataTypeList<InternalDataType>> getmTargetList() {
        return mTargetList;
    }
}
