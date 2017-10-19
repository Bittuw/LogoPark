package com.example.nikel.logoparkscanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikel.logoparkscanner.Fragments.MainFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nikel on 14.10.2017.
 */

public class ExpItemList extends BaseExpandableListAdapter implements ExpandableListView.OnGroupClickListener {

    private String LOG_TAG = "ExpItemList";
    private SimpleArrayMap<String, Object> mGroups;
    private SimpleArrayMap<String, Object> mFilteredGroups;
    private SimpleArrayMap<String, String> temp, translate;
    private Activity mActivity;
    private ViewImageHolder viewImageHolder;
    private ArrayList<String> toShowOnList, doNotToShowOnList;
    private FilterMap executer;

    public int currentGroupExpand;

    public ExpItemList(ExpandableListView v, Activity mmActivity){
        Log.d(LOG_TAG, "ExpItemList");
        mActivity = mmActivity;
        v.setOnGroupClickListener(this);
        getResources(mActivity);
    }

    public void setList (SimpleArrayMap<String, Object> temp){
        Log.d(LOG_TAG, "setList");
        if (temp != null) {
            mGroups = temp;
            executer = new FilterMap();
            executer.execute(mGroups);
        }
        else {
            mGroups.clear();
            mFilteredGroups.clear();
        }
    }

    public void Stop() {
        executer.cancel(true);
    }

    private void getResources(Activity mActivity) {

        String[] temp =  mActivity.getResources().getStringArray(R.array.toShowOnList);
        toShowOnList = new ArrayList<>(Arrays.asList(temp));

        temp =  mActivity.getResources().getStringArray(R.array.doNotToShowOnList);
        doNotToShowOnList = new ArrayList<>(Arrays.asList(temp));

        String[] keys = mActivity.getResources().getStringArray(R.array.translate_key);
        String[] values = mActivity.getResources().getStringArray(R.array.translate_value);

        translate = makeTranslateMap(keys, values);
    }

    private class FilterMap extends AsyncTask<SimpleArrayMap<String, Object>, Void, SimpleArrayMap<String, Object>> {
        @Override
        protected void onPreExecute() {
            MainFragment.progressBar.setVisibility(View.VISIBLE);
            MainFragment.list.setVisibility(View.INVISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            MainFragment.progressBar.setVisibility(View.INVISIBLE);
            super.onCancelled();
        }

        @Override
        protected SimpleArrayMap<String, Object> doInBackground(SimpleArrayMap<String, Object>... map) {
            return FilterArray(map[0]);
        }

        private SimpleArrayMap<String, Object> FilterArray(SimpleArrayMap<String, Object> map) {
            SimpleArrayMap<String, Object> temp = new SimpleArrayMap<>();

            for (int loop = 0; loop < map.size(); loop++) {
                String key = map.keyAt(loop);
                Object mObject = map.get(key);

                if (mObject instanceof SimpleArrayMap && toShowOnList.contains(key) && !doNotToShowOnList.contains(key)) {
                    if (translate.containsKey(key)) {
                        temp.put(translate.get(key), FilterArray((SimpleArrayMap<String, Object>) mObject));
                        continue;
                    }
                    else {
                        temp.put(key, FilterArray((SimpleArrayMap<String, Object>) mObject));
                        continue;
                    }
                }

                if(mObject instanceof String && toShowOnList.contains(key) && !doNotToShowOnList.contains(key)) {
                    if (translate.containsKey(key))
                        temp.put(translate.get(key), mObject);
                    else {
                        temp.put(key, mObject);
                    }
                }
            }
            return temp;
        }


        @Override
        protected void onPostExecute(SimpleArrayMap<String, Object> map) {
            mFilteredGroups = map;
            notifyDataSetChanged();
            MainFragment.list.setAdapter(MainFragment.adapter);
        }
    }
   /* private SimpleArrayMap<String, Object> FilterArray(SimpleArrayMap<String, Object> temp) {
        SimpleArrayMap<String, Object> map = new SimpleArrayMap<>();

        for (int loop = 0; loop < temp.size(); loop++) {
            String key = temp.keyAt(loop);
            Object mObject = temp.get(key);

            if (mObject instanceof SimpleArrayMap && toShowOnList.contains(key) && !doNotToShowOnList.contains(key)) {
                if (translate.containsKey(key)) {
                    map.put(translate.get(key), FilterArray((SimpleArrayMap<String, Object>) mObject));
                    continue;
                }
                else {
                    map.put(key, FilterArray((SimpleArrayMap<String, Object>) mObject));
                    continue;
                }
            }

            if(mObject instanceof String && toShowOnList.contains(key) && !doNotToShowOnList.contains(key)) {
                if (translate.containsKey(key))
                    map.put(translate.get(key), mObject);
                else {
                    map.put(key, mObject);
                }
            }
        }
        return map;
    }*/

    private SimpleArrayMap<String, String> makeTranslateMap(String[] keys, String[] values) {
        SimpleArrayMap<String, String> map = new SimpleArrayMap<>();
        for (int x = 0; x < keys.length; x++) {
            map.put(keys[x], values[x]);
        }
        return map;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        MainFragment.list.setVisibility(View.VISIBLE);
        MainFragment.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getGroupCount() {
        return mFilteredGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mFilteredGroups.valueAt(groupPosition) instanceof SimpleArrayMap) {
            temp = (SimpleArrayMap<String, String>) (mFilteredGroups.valueAt(groupPosition));
            return temp.size();
        }
        else {
            return 1;
        }
    }

    @Override
    public String getGroup(int groupPosition) {
        return mFilteredGroups.keyAt(groupPosition);
    }

    @Override
    public Pair<String, String> getChild(int groupPosition, int childPosition) {
        if (mFilteredGroups.valueAt(groupPosition) instanceof SimpleArrayMap) {
            SimpleArrayMap<String, String> row =(SimpleArrayMap<String, String>) (mFilteredGroups.valueAt(groupPosition));
            Pair<String, String> temp = new Pair<>(row.keyAt(childPosition), row.valueAt(childPosition));
            return temp;
        }
        else {
            Pair<String, String> temp= new Pair<>(mFilteredGroups.keyAt(groupPosition),(String) mFilteredGroups.valueAt(groupPosition));
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

        Pair<String, String> temp = getChild(groupPosition, childPosition);

        if (temp.first.equals("Номер")) {
            if (viewImageHolder != null ) {
                if (viewImageHolder.murl.equals(temp.second)) {
                    return viewImageHolder.mview;
                }
                else {
                    Intent mIntent = new Intent(mActivity, MainService.class);
                    mIntent.setAction(Constants.IntentParams.Picture);
                    mIntent.putExtra(Constants.IntentParams.URL, "https://lgprk.ru" + temp.second);
                    MainFragment.mListener.StartServiceTask(new Intent(mIntent));

                    viewImageHolder.image.setImageResource(0);
                    viewImageHolder.murl = temp.second;
                    return viewImageHolder.mview;
                }
            }
            else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_row_image, parent, false);
                viewImageHolder = new ViewImageHolder(convertView, temp.second);
                convertView.setTag(viewImageHolder);

                viewImageHolder.textChildID.setText(temp.first);

                Intent mIntent = new Intent(mActivity, MainService.class);
                mIntent.setAction(Constants.IntentParams.Picture);
                mIntent.putExtra(Constants.IntentParams.URL, "https://lgprk.ru" + temp.second);
                MainFragment.mListener.StartServiceTask(new Intent(mIntent));

                return convertView;
            }
        }
        else {
            ViewHolder viewHolder;

            /*LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);*/
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);


            viewHolder.textChildID.setText(temp.first);
            viewHolder.textChild.setText(temp.second);

            return convertView;
        }
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        if (currentGroupExpand != i) {
            expandableListView.collapseGroup(currentGroupExpand);
            expandableListView.expandGroup(i);
            currentGroupExpand = i;
        }
        else {
            if (expandableListView.isGroupExpanded(i))
                expandableListView.collapseGroup(i);
            else
                expandableListView.expandGroup(i);
        }
        return true;
    }

    private class ViewImageHolder {
        final View mview;
        final ImageView image;
        final TextView textChildID;
        String murl;
        ViewImageHolder(View view, String url) {
            mview = view;
            murl = url;
            image = view.findViewById(R.id.imageView);
            textChildID = view.findViewById(R.id.textChildID);
            try {
                mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.IntentParams.Picture));
            }  catch (Exception e)
            {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }


        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                image.setImageBitmap((Bitmap) intent.getParcelableExtra(Constants.IntentParams.Picture));
            }
        };

        @Override
        protected void finalize() throws Throwable {
            mActivity.unregisterReceiver(mBroadcastReceiver);
            super.finalize();
        }
    }

    private class ViewHolder {
        final TextView textChildID;
        final TextView textChild;
        ViewHolder(View view) {
            textChildID = view.findViewById(R.id.textChildID);
            textChild = view.findViewById(R.id.textChild);
        }
    }
    @Override
    protected void finalize() throws Throwable {

        super.finalize();
    }
}
