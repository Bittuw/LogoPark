package com.example.nikel.logoparkscanner;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Created by nikel on 11.10.2017.
 */

public class ItemList extends BaseAdapter {
    private SimpleArrayMap<String, Object> mList;

    public ItemList() {    }
    public ItemList(SimpleArrayMap<String, Object> list) {
        mList = list;
    }

    public void setList(SimpleArrayMap<String, Object> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Pair getItem(int i) {
        return new Pair<>(mList.keyAt(i), mList.valueAt(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View result;
        if (view == null) {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.info_row, viewGroup, false);
        }
        else {
            result = view;
        }

        Pair row = getItem(i);

        ((TextView)result.findViewById(R.id.field_id)).setText(row.first.toString());
        ((TextView)result.findViewById(R.id.field)).setText(row.second.toString());
        return result;
    }
}
