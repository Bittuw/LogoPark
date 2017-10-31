package com.example.nikel.logoparkscanner.JSONParser;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.DataTypes.InternalTypeData.InternalData.GenericInternalDataType;
import com.example.nikel.logoparkscanner.DataTypes.JSONTypeData.JSONSubPair;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nikel on 29.09.2017.
 */

public class JSONParser {

    private List<JSONSubPair> list;
    private ArrayList<String> mlist;
    private List<GenericInternalDataType> map;
    private SimpleArrayMap<String, Object> targetMap;

    private final JSONObject object;

    public JSONParser(JSONObject object, String[] mapResource) {
        this.object = object;
        this.mlist = new ArrayList<>(Arrays.asList(mapResource));
    }

    private static List<GenericInternalDataType> makeMap(ArrayList<String> list) {
        for (String element:list) {
            String[] temp = element.split("__");
            for (String parametr:) {

            }
        }
        return null;
    }

    @Nullable
    public SimpleArrayMap parseJSONObject() {
        try {
            for (Iterator<String> it = object.keys(); it.hasNext();) {

            }
        } catch (JSONException e) {
           Log.e(this.getClass().getSimpleName(), "Error with JSONParser", e);
           return null;
        }
        return null;
    }

}
