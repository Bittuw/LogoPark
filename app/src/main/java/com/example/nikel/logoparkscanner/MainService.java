package com.example.nikel.logoparkscanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nikel on 13.09.2017.
 */

public class MainService extends Service {

    private final String MESSAGE_TAG = "urovo.rcv.message";
    private Timer timer;
    private boolean isAliveActivity, isActivityStop, isForegroundService;

    private HandlerThread rThread, getThread, postThread;
    private Handler rHanlder, getHandler, postHandler;
    private InternetThread GET, POST;
    private String type, code;

    final String LOG_TAG = "MainService";

    private final static int NOTIFICATION_ID = 42;

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

        Log.d(LOG_TAG, "onCreate");

        getThread = new HandlerThread("getThread");
        getThread.start();
        getHandler = new Handler(getThread.getLooper());

        postThread = new HandlerThread("postThread");
        postThread.start();
        postHandler = new Handler(postThread.getLooper());

        GET = new InternetThread();
        POST = new InternetThread();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            if (intent.getAction() != null) {

                Log.d(LOG_TAG, "onStartCommand " + intent.getAction());

                String action = intent.getAction();

                switch (action) {
                    case Constants.IntentParams.Auth:
                        Log.d(LOG_TAG, this.getClass().getName() + ": " + action);
                        getData(action, intent.getStringExtra(Constants.IntentParams.URL), startId);
                        break;
                    case Constants.IntentParams.RecData:
                        Log.d(LOG_TAG, this.getClass().getName() + ": " + action + " " + intent.getStringExtra(Constants.IntentParams.URL));
                        getData(action, intent.getStringExtra(Constants.IntentParams.URL), startId);
                        break;
                    case Constants.IntentParams.Picture:
                        getPicture(action, intent.getStringExtra(Constants.IntentParams.URL), startId);
                        break;
                    case Constants.IntentParams.SendData:
                        Log.d(LOG_TAG, this.getClass().getName() + ": " + action);
                        sendData(action, intent.getStringExtra(Constants.IntentParams.URL), startId);
                        break;
                    case Constants.IntentParams.StartRecCas:
                        if (rHanlder == null) {
                            rThread = new HandlerThread("ReceiveThread");
                            rThread.start();
                            rHanlder = new Handler(rThread.getLooper());

                            IntentFilter mIntentFilter = new IntentFilter();
                            mIntentFilter.addAction(Constants.IntentParams.UROVO);
                            registerReceiver(mBroadcastReceiver, mIntentFilter, null, rHanlder);
                        } else {
                            Log.e(LOG_TAG, action + ": Service is already working");
                        }
                        break;
                    case Constants.IntentParams.StopService:
                        stopSelf();
                        break;
                    case Constants.IntentParams.foregroundService:
                        foregroundService(intent.getBooleanExtra(Constants.IntentParams.foregroundService, false));
                        break;
                    case Constants.IntentParams.isOnlineTimer:
                        Timer(intent.getBooleanExtra(Constants.IntentParams.isOnlineTimer, true));
                        break;
                    case Constants.IntentParams.isActivityAlive:
                        isAliveActivity = intent.getBooleanExtra(Constants.IntentParams.isActivityAlive, false);
                        break;
                    case Constants.IntentParams.isActivityStop:
                        isActivityStop = intent.getBooleanExtra(Constants.IntentParams.isActivityStop, false);
                        break;
                    default:
                        Log.e(LOG_TAG, "onStartCommand " + intent.getAction());
                        break;
                }
        }
        return START_STICKY;
    }

    private void foregroundService(boolean type) {
        if (isForegroundService != type) {
            if (type) {
                startForeground(NOTIFICATION_ID, new Notification());
                isForegroundService = type;
                Toast mToast = Toast.makeText(getApplicationContext(), "Запуск сервиса в фоновом режиме", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            } else {
                stopForeground(false);
                NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();
                isForegroundService = type;
                Toast mToast = Toast.makeText(getApplicationContext(), "Запуск сервиса как часть приложения", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        }
        else {
            Log.e(LOG_TAG, "Receive current state of \"isForegroundService\": " + isForegroundService);
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

    private void getData(String action, String path, int startID) {
        GET.setPath(path);
        GET.setAction(action);
        GET.setStartID(startID);
        getHandler.post(GET);
    }

    private void sendData(String action, String path, int startID) {
        POST.setAction(action);
        POST.setPath(path);
        POST.setStartID(startID);
        postHandler.post(POST);
    }

    private void getPicture(String action, String path, int startID) {
        GET.setPath(path);
        GET.setAction(action);
        GET.setStartID(startID);
        getHandler.post(GET);
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

            sendBroadcast(mIntent);
        }
    }

    private class InternetThread implements Runnable {
        String path, action;
        int startID;

        public InternetThread() {

        }

        @Override
        public void run() {
            BufferedReader reader = null;
            java.net.URL url;
            StringBuilder buf = new StringBuilder();
            Bitmap mBitmap = null;
            Intent mIntent;
            HttpsURLConnection connection;
            boolean success = false;
            try{
                switch (action) {
                    case Constants.IntentParams.RecData:
                        url = new URL(path);
                        connection = (HttpsURLConnection)url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(10000);
                        connection.connect();
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            buf.append(line + "\n");
                        }
                        break;
                    case Constants.IntentParams.Picture:
                        try {
                            InputStream in = new java.net.URL(path).openStream();
                            mBitmap = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                        break;
                    case Constants.IntentParams.SendData: // TODO сделать POST запрос
                        url = new URL(path);
                        connection = (HttpsURLConnection)url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoInput(false);
                        connection.setDoOutput(false);
                        connection.setReadTimeout(10000);
                        connection.connect();

                        if (connection.getResponseCode() == 200)
                            success = true;
                        else
                            success = false;
                        break;
                    default:
                        Log.e(LOG_TAG, getClass().getName() + " error download");

                        Toast mToast = Toast.makeText(getApplicationContext(), "Ошибка интернет-соединения: дейстиве - " + action, Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.BOTTOM, 0, 0);
                        mToast.show();
                }

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
                    case Constants.IntentParams.Picture:
                        mIntent = new Intent(Constants.IntentParams.Picture);
                        mIntent.putExtra(Constants.IntentParams.Picture, mBitmap);
                        sendBroadcast(mIntent);
                        break;
                    case Constants.IntentParams.SendData:
                        mIntent = new Intent(Constants.IntentParams.Success);
                        mIntent.putExtra(Constants.IntentParams.Success, success);
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

        public void setStartID(int ID) {
            this.startID = ID;
        }

        public int getStartID() {
            return this.startID;
        }
    }


    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");

        if (rThread != null) {
            unregisterReceiver(mBroadcastReceiver);
            rThread.quitSafely();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // TODO сделать запуск активити или передачу по broadcast
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive " + intent.getAction());
            byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            code = new String(barcode,0, barocodelen);
            type = BarcodeTypes.decodeType(temp).name();

            //context.startActivity(mIntent);
            if (isAliveActivity && !isActivityStop) {
                Intent mIntent = new Intent();
                mIntent.setAction(Constants.IntentParams.QR);
                mIntent.putExtra("type", type);
                mIntent.putExtra("code", code);
                //mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //LocalBroadcastManager.getInstance(context).sendBroadcast(message);
                sendBroadcast(mIntent);
            }
            if (isAliveActivity && isActivityStop) {
                Intent mIntent = new Intent();
                Intent mActivity = new Intent(context, MainActivity.class);
                mIntent.setAction(Constants.IntentParams.QR);
                mActivity.setAction(Intent.ACTION_MAIN);
                mIntent.putExtra("type", type);
                mIntent.putExtra("code", code);
                mActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mActivity);
                sendBroadcast(mIntent);
            }
            if(!isAliveActivity && isActivityStop) {
                Intent mIntent = new Intent(context, MainActivity.class);
                mIntent.setAction(Constants.IntentParams.QR);
                mIntent.putExtra("type", type);
                mIntent.putExtra("code", code);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mIntent);
            }
        }
    };
}

