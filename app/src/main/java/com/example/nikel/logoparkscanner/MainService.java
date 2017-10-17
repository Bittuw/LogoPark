package com.example.nikel.logoparkscanner;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.Fragments.MainFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nikel on 13.09.2017.
 */

public class MainService extends Service {

    MyReceiver mReceiver;
    private final String MESSAGE_TAG = "urovo.rcv.message";
    private Timer timer;
    private boolean isAliveActivity;

    private HandlerThread rThread, getThread, postThread; // TODO создаваемые потоки
    private Handler rHanlder, getHandler, postHandler; // TODO создаваемые хендлеры
    private InternetThread GET, POST; // TODO интерфейсы загрузки
    private String type, code; // TODO тип и код

    final String LOG_TAG = "MainService";

    private final static int NOTIFICATION_ID = 42;

    private static enum BarcodeTypes {// TODO определение типа штрих-кода
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

        getThread = new HandlerThread("getThread");
        getThread.start();
        getHandler = new Handler(getThread.getLooper());

        postThread = new HandlerThread("postThread");
        postThread.start();
        postHandler = new Handler(postThread.getLooper());

        GET = new InternetThread();
        POST = new InternetThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // TODO выбор действия сервиса
        Log.d(LOG_TAG, "onStartCommand " + intent.getAction());

        String action = intent.getAction();

        switch (action) {
            case Constants.IntentParams.Auth:
                Log.d(LOG_TAG, this.getClass().getName() + ": " + action);
                getData(action, intent.getStringExtra(Constants.IntentParams.URL));
                break;
            case Constants.IntentParams.RecData: // TODO get запрос
                Log.d(LOG_TAG, this.getClass().getName() + ": " + action + " " + intent.getStringExtra(Constants.IntentParams.URL));
                getData(action, intent.getStringExtra(Constants.IntentParams.URL));
                break;
            case Constants.IntentParams.Picture:
                getPicture(action, intent.getStringExtra(Constants.IntentParams.URL));
                break;
            case Constants.IntentParams.SendData: // TODO post запрос
                Log.d(LOG_TAG, this.getClass().getName() + ": " + action);
                sendData(action, intent.getStringExtra(Constants.IntentParams.URL));
                break;
            case Constants.IntentParams.StartRecCas: // TODO
                if (rHanlder == null) {
                    rThread = new HandlerThread("ReceiveThread");
                    rThread.start();
                    rHanlder = new Handler(rThread.getLooper());
                    mReceiver = new MyReceiver();
                    registerReceiver(mReceiver, new IntentFilter(MESSAGE_TAG), null, rHanlder);
                }
                else {
                    Log.e(LOG_TAG, action + ": Service is already working");
                }
                break;
            case Constants.IntentParams.StopService:
                stopSelf();
                break;
            case Constants.IntentParams.foregroundService:
                foregroundService(intent.getBooleanExtra(Constants.IntentParams.foregroundService, true));
                break;
            case Constants.IntentParams.isOnlineTimer:
                Timer(intent.getBooleanExtra(Constants.IntentParams.isOnlineTimer, true));
                break;
            default:
                Log.e(LOG_TAG, "onStartCommand " + intent.getAction());
                break;
        }
        return START_REDELIVER_INTENT;
    }

    private void foregroundService(final boolean type) {
        if (type) {
            startForeground(NOTIFICATION_ID, new Notification());
        }
        else {
            stopForeground(false);
        }
    }

    private void Timer(final boolean action) {
        if (action) {
            timer = new Timer();
            TimerTask isOnline = new isOnline();
            timer.schedule(isOnline, Constants.delay, Constants.period);
        }
        else {
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getData(String action, String path) {
        GET.setPath(path);
        GET.setAction(action);
        getHandler.post(GET);
    }

    private void sendData(String action, String path) {
        POST.setAction(action);
        POST.setPath(path);
        postHandler.post(POST);
    }

    private void getPicture(String action, String path) {

    }

    private class isOnline extends TimerTask {

        @Override
        public void run() {
            boolean temp;
            ConnectivityManager cm =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting())
            {
                temp = true;
            }
            else {
                temp = false;
            }

            Intent mIntent = new Intent();
            mIntent.setAction(Constants.IntentParams.isOnlineTimer);
            mIntent.putExtra(Constants.IntentParams.isOnlineTimer, temp);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mIntent);
        }
    }

    private class InternetThread implements Runnable {
        String path, action;

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

                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }

                Intent mIntent;

                switch (action) {
                    case Constants.IntentParams.Auth:
                        mIntent = new Intent(Constants.IntentParams.Auth);
                        mIntent.putExtra(Constants.IntentParams.GetData, buf.toString());
                        sendBroadcast(mIntent);
                        break;
                    case Constants.IntentParams.RecData:
                        mIntent = new Intent(Constants.IntentParams.RecData);
                        mIntent.putExtra(Constants.IntentParams.GetData, buf.toString());
                        sendBroadcast(mIntent);
                        break;
                    default:
                        Log.e(LOG_TAG, this.getClass().getName());
                        break;
                }
            }
            catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage());
                Toast mToast = Toast.makeText(getApplicationContext(), "Проверте интернет соединение или сам штрих-код", Toast.LENGTH_LONG);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return this.path;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return this.action;
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

            Intent mIntent = new Intent();
            mIntent.setAction(Constants.IntentParams.QR);
            mIntent.putExtra("type", type);
            mIntent.putExtra("code", code);
            //mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //LocalBroadcastManager.getInstance(context).sendBroadcast(message);
            sendBroadcast(mIntent);
            //context.startActivity(mIntent);
            if (isAliveActivity) {
                sendBroadcast(mIntent);
            }
            else {
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mIntent);
            }
        }

        public MyReceiver() {

        }
    }
}

