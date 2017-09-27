package com.example.nikel.logoparkscanner;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nikel on 13.09.2017.
 */

public class MainService extends Service implements MainInterface{

    private SharedPreferences mPref;
    BroadcastReceiver mReceiver;
    private final String MESSAGE_TAG = "urovo.rcv.message";

    private HandlerThread rThread, mThread;
    private Handler rHanlder, mHandler;
    private InternetThread GET;
    private String type, code;

    final String LOG_TAG = "MainService";

    private static enum BarcodeTypes {
        unknown(0),all(-1),ean13(11),ean8(10),code39(1),code93(7),code128(3),qr(28),pdf417(17),interleaved2of5(6),upca(-117),upce(9);

        private final int code;
        private BarcodeTypes(int code) {
            this.code = code;
        }
        public static BarcodeTypes decodeType(int value) {
            for(BarcodeTypes t : values())
                if(t.code == value)
                    return t;
            return unknown;
        }
    }

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        mThread = new HandlerThread("InternetThread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        GET = new InternetThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand " + intent.getAction());

        String action = intent.getAction();

        switch (action) {
            case IntentParams.Auth:
                Log.d(LOG_TAG, this.getClass().getName() + ": " + action);
                getData(action, intent.getStringExtra("URL"));
                break;
            case IntentParams.RecD:
                Log.d(LOG_TAG, this.getClass().getName() + ": " + action);
                getData(action, intent.getStringExtra("URL"));
                break;
            case IntentParams.StartRecCas:
                if (rHanlder == null) {
                    rThread = new HandlerThread("ReceiveThread");
                    rThread.start();
                    rHanlder = new Handler(rThread.getLooper());
                    registerReceiver(mReceiver, new IntentFilter(MESSAGE_TAG), null, rHanlder);
                }
                else {
                    this.stopSelf(startId);
                }
                break;
            default:
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getData(String action, String path) {
        GET.setPath(path);
        mHandler.post(GET);
    }

    private class InternetThread implements Runnable {
        String path;

        public InternetThread() {

        }

        @Override
        public void run() {
            BufferedReader reader = null;
            try{
                java.net.URL url=new URL(path);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line;

                while ((line=reader.readLine()) != null) {
                    buf.append(line + "\n");
                }

                Intent mIntent = new Intent(ConfirmAuth);
                mIntent.putExtra("Data", buf.toString());
                LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(mIntent);
                //return(buf.toString());
            }
            catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return this.path;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");

        if (rThread != null) {
            unregisterReceiver(mReceiver);
            rThread.quitSafely();
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive " + intent.getAction());
            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            code = new String(barcode,0, barocodelen);
            type = BarcodeTypes.decodeType(temp).name();

            Intent message = new Intent(context, MainActivity.class);
            message.putExtra("type", type);
            message.putExtra("code", code);
            message.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//          context.sendBroadcast(message);
            context.startActivity(message);
        }

        public MyReceiver() {

        }
    }

}

