package com.example.nikel.logoparkscanner;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nikel on 14.10.2017.
 */

public class ExpItemList extends BaseExpandableListAdapter implements ExpandableListView.OnGroupClickListener {

    private SimpleArrayMap<String, Object> mGroups;
    private SimpleArrayMap<String, String> temp;
    private ArrayList<View> cGroups;
    private Context mContext;
    public int currentGroupExpand;

    public ExpItemList(){

    }

    public ExpItemList (Context context,SimpleArrayMap<String, Object> groups){
        mContext = context;
        mGroups = groups;
    }

    public void setList (SimpleArrayMap<String, Object> temp){
        mGroups = temp;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mGroups.valueAt(groupPosition) instanceof SimpleArrayMap) {
            temp = (SimpleArrayMap<String, String>) (mGroups.valueAt(groupPosition));
            return temp.size();
        }
        else {
            return 1;
        }
    }

    @Override
    public String getGroup(int groupPosition) {
        return mGroups.keyAt(groupPosition);
    }

    @Override
    public Pair<String, String> getChild(int groupPosition, int childPosition) {
        if (mGroups.valueAt(groupPosition) instanceof SimpleArrayMap) {
            SimpleArrayMap<String, String> row =(SimpleArrayMap<String, String>) (mGroups.valueAt(groupPosition));
            Pair<String, String> temp = new Pair<>(row.keyAt(childPosition), row.valueAt(childPosition));
            return temp;
        }
        else {
            Pair<String, String> temp= new Pair<>(mGroups.keyAt(groupPosition),(String) mGroups.valueAt(groupPosition));
            return temp;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            //LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = inflater.inflate(R.layout.group_view, null);
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view, parent, false);
        }
        if (currentGroupExpand != groupPosition) {

        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);

        String temp = getGroup(groupPosition);
        textGroup.setText(temp);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            /*LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);*/
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_view, parent, false);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        TextView textChildID = (TextView) convertView.findViewById(R.id.textChildID);
        Pair<String, String> temp = getChild(groupPosition, childPosition);

        textChildID.setText(temp.first);
        textChild.setText(temp.second);

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        if (currentGroupExpand != i) {
            expandableListView.collapseGroup(i);
        }
        else {
            currentGroupExpand = i;
        }
        return true;
    }
}
