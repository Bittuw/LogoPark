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
import android.widget.ExpandableListAdapter;
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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * Created by nikel on 29.09.2017.
 */

public class MainFragment extends Fragment implements ExpItemList.onDataChange {

    private TextView /*type, code,*/ Title;
    private Activity mActivity;
    public static AuthFragment.NoticeListener mListener;
    private static final String LOG_TAG = "MainFragment";

    private String user, code, url, actionString;
    public static ExpandableListView list;
    private JSONObject json;
    private ProgressBar progressBar;
    public static ExpItemList adapter;
    private JSONAsync executer;

    private String pattern = "[0-9]+";
    private Button more, action, close;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        setRetainInstance(true);
        mActivity = getActivity();

        mListener = (AuthFragment.NoticeListener) mActivity;

        /*Intent mIntent = new Intent(mActivity, MainService.class);
        mIntent.setAction(Constants.IntentParams.StartRecCas);
        mIntent.putExtra(Constants.IntentParams.foregroundService, true);
        mListener.StartServiceTask(mIntent);*/

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setArguments(Bundle args) {
        Log.d(LOG_TAG, "setArguments");
        user = args.getString(Constants.User);
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        //type = v.findViewById(R.id.TypeID);
        //code = v.findViewById(R.id.CodeID);
        action = v.findViewById(R.id.action);
        more = v.findViewById(R.id.more);
        close = v.findViewById(R.id.close);
        list = v.findViewById(R.id.list);
        progressBar = v.findViewById(R.id.progressBar);

        prepareListView();
        return v;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.IntentParams.RecData);
        mIntentFilter.addAction(Constants.IntentParams.QR);
        mIntentFilter.addAction(Constants.IntentParams.isOnlineTimer);
        mIntentFilter.addAction(Constants.IntentParams.Fail);
        mIntentFilter.addAction(Constants.IntentParams.Success);
        mActivity.registerReceiver(mBroadcastReceiver, mIntentFilter);

        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        //mActivity.unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }



    private void prepareListView() {
        Log.d(LOG_TAG, "prepareListView");
        adapter = new ExpItemList(list, mActivity, this);
        /*View footer = mActivity.getLayoutInflater().inflate(R.layout.footer_view, null, false);*/
        View header = mActivity.getLayoutInflater().inflate(R.layout.header_view, null, false);
        header.setOnClickListener(null);
        Title = header.findViewById(R.id.Title);
        list.addHeaderView(header);
        /*list.addFooterView(footer);

        action = footer.findViewById(R.id.Action);
        more = footer.findViewById(R.id.More);
        close = footer.findViewById(R.id.Close);*/

        action.setOnClickListener(onActionClick);
        more.setOnClickListener(onMoreClick);
        close.setOnClickListener(onCloseClick);
    }

    private String getNumber(final String string) {
        String temp = "";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);

        while(m.find()) {
            temp = temp + string.substring(m.start(), m.end());
        }

        return temp;
    }

    private class JSONAsync extends AsyncTask<JSONObject, Void, SimpleArrayMap<String, Object>> {

        @Override
        protected SimpleArrayMap<String, Object> doInBackground(JSONObject... temp) {
            try {
                JSONArray t = temp[0].getJSONArray("items");
                json = t.getJSONObject(0);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error with retrieval JSONArray items", e);
            }
            return getSimpleArrayMap(json, getFilterResource());
        }

        @Override
        protected void onPreExecute() {
            //action.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(SimpleArrayMap<String, Object> temp) { // TODO загрузить картинку
            Log.d(LOG_TAG, "onPostExecute");
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
                    actionString = "Пропустить";
                    action.setVisibility(View.VISIBLE);
                    action.setText("Пропустить");
                    action.setClickable(true);
                    break;

                case "4":
                    actionString = "4";
                    action.setText("Покинул территорию");
                    action.setClickable(false);
                    break;

                case "3":
                    actionString = "Выпустить";
                    action.setVisibility(View.VISIBLE);
                    action.setText("Выпустить");
                    action.setClickable(true);
                    break;

                default:
                    adapter.Stop();
                    Log.e(LOG_TAG, "Невалидный JSON");

                    Toast mToast = Toast.makeText(mActivity, "Ошибки получение данных: Невалидный JSON", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                    break;
            }
            executer.cancel(true);
            try {
                executer.finalize();
            } catch (Throwable e) {
                Log.e(LOG_TAG, "Error with delete JSONAsync", e);
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
                Log.e(LOG_TAG, "Error with NullPointer JSON",e);
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
            Toast mToast;
            Log.d(LOG_TAG, intent.getAction());
            switch (intent.getAction()) {
                case Constants.IntentParams.QR:
                    if (!AuthFragment.validateCode(intent.getStringExtra("code"))) {

                        setDownloading(View.INVISIBLE, View.VISIBLE);
                        /*progressBar.setVisibility(View.VISIBLE);*/
                        //list.setVisibility(View.INVISIBLE);
                        //type.setText(intent.getStringExtra("type"));

                        Title.setText("Пропуск №" + getNumber(intent.getStringExtra("code")));

                        if (!intent.getStringExtra("code").contains("https")) {
                            code = (intent.getStringExtra("code").replace("http", "https"));
                        }
                        else {
                            code = (intent.getStringExtra("code"));
                        }

                        url = code + "?" + user;

                        Intent mIntent = new Intent(mActivity, MainService.class);
                        mIntent.setAction(Constants.IntentParams.RecData);
                        mIntent.putExtra(Constants.IntentParams.URL, url);
                        mListener.StartServiceTask(mIntent);
                    }
                    else {
                        Log.e(LOG_TAG, "Not valid URL for read");
                        mToast = Toast.makeText(mActivity, "Нечитаемый QR", Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                    break;

                case Constants.IntentParams.RecData:
                    try {
                        json = new JSONObject(intent.getStringExtra(Constants.IntentParams.GetData));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Error with retrieval JSON",e);

                        mToast = Toast.makeText(mActivity, "Ошибка при  парсинге json", Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                    }
                    executer = new JSONAsync();
                    executer.execute(json);

                    break;
                case Constants.IntentParams.isOnlineTimer:
                    break;
                case Constants.IntentParams.Success:
                    Log.d(LOG_TAG, "Success with SendData");
                    adapter.setList(null);
                    adapter.notifyDataSetChanged();
                    //list.setVisibility(View.GONE);
                    setDownloading(View.INVISIBLE, View.INVISIBLE);

                    mToast = Toast.makeText(mActivity, "Успешно отправленно", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                    break;
                case Constants.IntentParams.Fail:
                    Log.e(LOG_TAG, "Fail to SendData");

                    mToast = Toast.makeText(mActivity, "Не успешная отправка", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener onActionClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "onClickAction");
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
                    break;

                default:
                    Log.e(LOG_TAG, "onClick: Uncaught action");
                    break;
            }

            Intent mIntent = new Intent(mActivity, MainService.class);
            mIntent.setAction(Constants.IntentParams.SendData);
            mIntent.putExtra(Constants.IntentParams.URL, makingGetURL(code, actionString));
            mListener.StartServiceTask(mIntent);
        }
    };

    View.OnClickListener onMoreClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(code));
            startActivity(mIntent);
        }
    };

    View.OnClickListener onCloseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*type.setText(null);
            code.setText(null);*/
            adapter.setList(null);
            adapter.notifyDataSetChanged();
            setDownloading(View.INVISIBLE, View.INVISIBLE);
        }
    };

    private String makingGetURL(final String path, final String action) {
        String temp = "";
        switch (action) {
            case "Пропустить":
                temp += path + "?change&status=status_in&key=" + user;
                break;
            case "Выпустить":
                temp += path + "?change&status=status_finished&key=" + user;
                break;
            default:
                Log.e(LOG_TAG, "Uncaught action");
                return "";
        }
        return temp;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        mActivity.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onDataLoaded() {
        list.setAdapter(adapter);
        setDownloading(View.VISIBLE, View.INVISIBLE);
    }

    public void setDownloading(int Visible, int VisibleOfProgress) {

        list.setVisibility(Visible);
        more.setVisibility(Visible);
        action.setVisibility(Visible);
        close.setVisibility(Visible);

        progressBar.setVisibility(VisibleOfProgress);
    }
}
