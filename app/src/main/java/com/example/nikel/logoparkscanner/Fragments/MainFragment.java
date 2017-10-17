package com.example.nikel.logoparkscanner.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.ExpItemList;
import com.example.nikel.logoparkscanner.ItemList;
import com.example.nikel.logoparkscanner.MainService;
import com.example.nikel.logoparkscanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by nikel on 29.09.2017.
 */

public class MainFragment extends Fragment implements ExpandableListView.OnGroupClickListener {

    private TextView type, code, test;
    private Activity mActivity;
    private AuthFragment.NoticeListener mListener;
    private static final String LOG_TAG = "MainFragment";
    private String user, url;
    private ListView list;
    private ExpandableListView elist;
    private JSONObject json;
    private ProgressBar progressBar;
    private ItemList adapter;
    private ExpItemList eadapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        mActivity = getActivity();

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.IntentParams.RecData);
        mIntentFilter.addAction(Constants.IntentParams.QR);
        mIntentFilter.addAction(Constants.IntentParams.isOnlineTimer);
        mActivity.registerReceiver(mBroadcastReceiver, mIntentFilter);

        mListener = (AuthFragment.NoticeListener) mActivity;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setArguments(Bundle args) {
        user = args.getString(Constants.User);
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        type = v.findViewById(R.id.TypeID);
        code = v.findViewById(R.id.CodeID);
        list = v.findViewById(R.id.fragment_info);
        elist = v.findViewById(R.id.fragment_list);
        elist.setOnGroupClickListener(this);
        elist.setAlpha((float)0.5);
        progressBar = v.findViewById(R.id.progressBar);

        prepareListView();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    private void prepareListView() {
        /*adapter = new ItemList();*/
        eadapter = new ExpItemList();
        View footer = mActivity.getLayoutInflater().inflate(R.layout.footer_view, null, false);
        View header = mActivity.getLayoutInflater().inflate(R.layout.header_view, null, false);
        /*list.addHeaderView(header);
        list.addFooterView(footer);*/
        elist.addHeaderView(header);
        elist.addFooterView(footer);
    }

    private class JSONAsync extends AsyncTask<JSONObject, Void, SimpleArrayMap<String, Object>> {

        @Override
        protected SimpleArrayMap<String, Object> doInBackground(JSONObject... temp) {
            try {
                JSONArray t = temp[0].getJSONArray("items");
                json = t.getJSONObject(0);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Ошибка при извлечении JSONArray items");
            }
            return getSimpleArrayMap(json, getFilterResource());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(SimpleArrayMap<String, Object> temp) { // TODO загрузить картинку
            progressBar.setVisibility(View.INVISIBLE);

            if (elist.getAdapter() != null) {
                /*adapter.setList(temp);
                adapter.notifyDataSetChanged();*/
                eadapter.setList(temp);
                eadapter.notifyDataSetChanged();
            }
            else {
                /*adapter.setList(temp);
                list.setAdapter(adapter);*/
                eadapter.setList(temp);
                elist.setAdapter(eadapter);
            }
            elist.setVisibility(View.VISIBLE);
        }

        @Nullable
        private SimpleArrayMap<String, Object> getSimpleArrayMap(JSONObject temp, @Nullable ArrayList<String> jsonFilter) {
            SimpleArrayMap<String, Object> map = new SimpleArrayMap<>();

            try {
                for (Iterator<String> it = temp.keys(); it.hasNext();) {
                    String key = it.next();

                    if (jsonFilter == null) {
                        if (temp.get(key) instanceof JSONObject) {
                            map.put(key, getSimpleArrayMap(temp.getJSONObject(key), null));
                        }
                        else {
                            map.put(key, temp.getString(key));
                        }
                    }
                   else {
                        if (temp.get(key) instanceof JSONObject && jsonFilter.contains(key)) {
                            map.put(key, getSimpleArrayMap(temp.getJSONObject(key), null));
                            continue;
                        }
                        if (temp.get(key) instanceof JSONObject && !jsonFilter.contains(key)) {
                            if (getSimpleArrayMap(temp.getJSONObject(key), jsonFilter) != null)
                                map.put(key, getSimpleArrayMap(temp.getJSONObject(key), jsonFilter));
                            continue;
                        }
                        if(!(temp.get(key) instanceof JSONObject) && jsonFilter.contains(key)) {
                            map.put(key, temp.getString(key));
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
                Toast mToast = Toast.makeText(mActivity, "Ошибка при  парсинге json в Async", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            if (map.isEmpty()) {
                return null;
            }
            return map;
        }

        private ArrayList<String> getFilterResource() { // TODO сделать возврат ресурсов фильтра для json
            String[] temp =  getResources().getStringArray(R.array.JSONFilter);
            return new ArrayList<>(Arrays.asList(temp));
        }
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            switch (intent.getAction()) {
                case Constants.IntentParams.QR:
                    /*Log.d(LOG_TAG, intent.getAction());
                    progressBar.setVisibility(View.VISIBLE);
                    type.setText(intent.getStringExtra("type"));

                    if (!intent.getStringExtra("code").contains("https")) {
                        code.setText(intent.getStringExtra("code").replace("http", "https"));
                    }
                    else {
                        code.setText(intent.getStringExtra("code"));
                    }
                    url = code.getText() + "?" + user;*/


                    Log.d(LOG_TAG, intent.getAction());
                    progressBar.setVisibility(View.VISIBLE);
                    elist.setVisibility(View.INVISIBLE);
                    type.setText(intent.getStringExtra("type"));

                    if (!intent.getStringExtra("code").contains("https")) {
                        code.setText(intent.getStringExtra("code").replace("http", "https"));
                    }
                    else {
                        code.setText(intent.getStringExtra("code"));
                    }

                    url = code.getText() + "?" + user;

                    Intent mIntent = new Intent(mActivity, MainService.class);
                    mIntent.setAction(Constants.IntentParams.RecData);
                    mIntent.putExtra(Constants.IntentParams.URL, url);
                    mListener.StartServiceTask(mIntent);


                    break;

                case Constants.IntentParams.RecData:
                    try {
                        json = new JSONObject(intent.getStringExtra(Constants.IntentParams.GetData));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                        Toast mToast = Toast.makeText(mActivity, "Ошибка при  парсинге json", Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                    new JSONAsync().execute(json);

                    break;
                default:
            }
        }
    };

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        if (eadapter.currentGroupExpand != i) {
            boolean temp = expandableListView.collapseGroup(eadapter.currentGroupExpand);
            expandableListView.expandGroup(i);
            eadapter.currentGroupExpand = i;
        }
        else {
            expandableListView.expandGroup(i);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mActivity.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
