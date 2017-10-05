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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.JSONParser.JSONParser;
import com.example.nikel.logoparkscanner.R;

/**
 * Created by nikel on 29.09.2017.
 */

public class MainFragment extends Fragment {

    private TextView type, code;
    private JSONParser parser;
    private Activity mActivity;
    private static final String LOG_TAG = "MainFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverQR, new IntentFilter(Constants.IntentParams.QR));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiverData, new IntentFilter(Constants.IntentParams.RecData));
        super.onCreate(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        type = v.findViewById(R.id.TypeID);
        code = v.findViewById(R.id.CodeID);
        return v;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    private class JSONAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    BroadcastReceiver mBroadcastReceiverQR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, intent.getAction());
            type.setText(intent.getStringExtra("type"));
            code.setText(intent.getStringExtra("code"));
        }
    };

    BroadcastReceiver mBroadcastReceiverData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiverQR);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiverQR);
        super.onDestroy();
    }
}
