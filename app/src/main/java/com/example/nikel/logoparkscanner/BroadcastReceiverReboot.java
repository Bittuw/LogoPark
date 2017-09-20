package com.example.nikel.logoparkscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nikel on 13.09.2017.
 */

public class BroadcastReceiverReboot extends BroadcastReceiver {

    final String LOG_TAG = "Reboot";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive " + intent.getAction());
        context.startService(new Intent(context, MainService.class));
        Toast.makeText(context.getApplicationContext(), "onReceive", Toast.LENGTH_LONG).show();
    }
}
