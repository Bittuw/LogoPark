package com.example.nikel.logoparkscanner.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.ItemList;
import com.example.nikel.logoparkscanner.JSONParser.JSONParser;
import com.example.nikel.logoparkscanner.MainService;
import com.example.nikel.logoparkscanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nikel on 29.09.2017.
 */

public class MainFragment extends Fragment {

    private TextView type, code, test;
    private Activity mActivity;
    private AuthFragment.NoticeListener mListener;
    private static final String LOG_TAG = "MainFragment";
    private String user, url;
    private ListView list;
    private JSONObject json;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverQR, new IntentFilter(Constants.IntentParams.QR));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverIsOnline, new IntentFilter(Constants.IntentParams.isOnlineTimer));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverData, new IntentFilter(Constants.IntentParams.RecData));

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
        test = v.findViewById(R.id.TEST);
        list = v.findViewById(R.id.fragment_info);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private class JSONAsync extends AsyncTask<JSONObject, Void, ArrayMap<String, Object>> {

        @Override
        protected ArrayMap<String, Object> doInBackground(JSONObject... temp) {
            try {
                JSONArray t = temp[0].getJSONArray("items");
                json = t.getJSONObject(0);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Ошибка при items");
            }

            return getArrayMap(json);
        }

        @Override
        protected void onPostExecute(ArrayMap<String, Object> temp) { //загрузить картинку
            //test.setText(Arrays.toString(v));
            ItemList adapter = new ItemList(temp);
            list.setAdapter(adapter);
            list.setVisibility(View.VISIBLE);

            //ArrayMap map = (ArrayMap)temp.valueAt(2);

            /*String ToText = "";
            try {
                for (int i = 0; i < temp.size(); i++) {
                    ToText = ToText + temp.valueAt(i);
                }

                test.setText(ToText);
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, e.getMessage());
                test.setText("Неудача");
            }*/
        }

        private ArrayMap<String, Object> getArrayMap(JSONObject temp) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            try {

                for (Iterator<String> it = temp.keys(); it.hasNext();) {
                    String key = it.next();
                    if (temp.get(key) instanceof JSONObject) {
                        map.put(key, getArrayMap(temp.getJSONObject(key)));
                    }
                    else {
                        map.put(key, temp.getString(key));
                    }
                }

                return map;

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
                Toast mToast = Toast.makeText(mActivity, "Ошибка при  парсинге json в Async", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
                return null;
            }
        }
    }

    BroadcastReceiver mBroadcastReceiverQR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, intent.getAction());
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
        }
    };

    BroadcastReceiver mBroadcastReceiverIsOnline = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    BroadcastReceiver mBroadcastReceiverData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*container.setVisibility(View.VISIBLE);*/
            /*test.setText(intent.getStringExtra(Constants.IntentParams.GetData));
            test.setVisibility(View.VISIBLE);*/

            try {
                json = new JSONObject(intent.getStringExtra(Constants.IntentParams.GetData));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
                Toast mToast = Toast.makeText(mActivity, "Ошибка при  парсинге json", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
            new JSONAsync().execute(json);
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiverQR);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiverData);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiverIsOnline);
        super.onDestroy();
    }
}
