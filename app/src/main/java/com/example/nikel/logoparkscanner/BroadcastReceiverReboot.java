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
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                Intent in = new Intent(context, MainService.class);
                in.setAction(Constants.IntentParams.StartRecCas);
                context.startService(in);
                in.setAction(Constants.IntentParams.foregroundService);
                context.startService(in);
                break;
            case "onCreateActivity":

                break;
            case "onDestroyActivity":

                break;
            default:
                break;
        }

    }
}
