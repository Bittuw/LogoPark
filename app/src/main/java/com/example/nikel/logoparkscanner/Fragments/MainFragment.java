package com.example.nikel.logoparkscanner.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.ExpItemList;
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

public class MainFragment extends Fragment {

    private TextView type, code;
    private Activity mActivity;
    public static AuthFragment.NoticeListener mListener;
    private static final String LOG_TAG = "MainFragment";
    private String user, url, actionString;
    public static ExpandableListView list;
    private JSONObject json;
    public static ProgressBar progressBar;
    public static  ExpItemList adapter;

    private Button more, action;

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
        list = v.findViewById(R.id.fragment_list);
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
        adapter = new ExpItemList(list, mActivity);
        View footer = mActivity.getLayoutInflater().inflate(R.layout.footer_view, null, false);
        View header = mActivity.getLayoutInflater().inflate(R.layout.header_view, null, false);
        list.addHeaderView(header);
        list.addFooterView(footer);

        action = footer.findViewById(R.id.Action);
        more = footer.findViewById(R.id.More);

        action.setOnClickListener(onActionClick);
        more.setOnClickListener(onMoreClick);
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

            if (list.getAdapter() != null) {
                adapter.setList(temp);
                //adapter.notifyDataSetChanged();
            }
            else {
                adapter.setList(temp);
                //list.setAdapter(adapter);
            }

            switch (((SimpleArrayMap<String, String>) temp.get("status")).get("id")) {
                case "5":
                    actionString = "5";
                    action.setText("Пропустить");
                    break;

                case "4":
                    actionString = "4";
                    action.setVisibility(View.INVISIBLE);
                    break;

                case "3":
                    actionString = "3";
                    action.setText("Выпустить");
                    break;

                default:
                    adapter.Stop();
                    Log.e(LOG_TAG, "Невалидный JSON");

                    Toast mToast = Toast.makeText(mActivity, "Ошибки получение данных", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                    break;
            }
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

        private ArrayList<String> getFilterResource() {
            String[] temp =  getResources().getStringArray(R.array.JSONFilter);
            return new ArrayList<>(Arrays.asList(temp));
        }
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            switch (intent.getAction()) {
                case Constants.IntentParams.QR:

                    Log.d(LOG_TAG, intent.getAction());
                    progressBar.setVisibility(View.VISIBLE);
                    list.setVisibility(View.INVISIBLE);
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
                case Constants.IntentParams.isOnlineTimer:
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener onActionClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast mToast;
            switch (actionString) {
                case "Пропустить":
                    mToast = Toast.makeText(mActivity, "Пропустить", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                    break;
                case "Выпустить":
                    mToast = Toast.makeText(mActivity, "Выпустить" + action, Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                default:
                    Log.e(LOG_TAG, "onClick");
                    break;
            }
        }
    };

    View.OnClickListener onMoreClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) code.getText()));
            startActivity(mIntent);
        }
    };

    @Override
    public void onDestroy() {
        mActivity.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
