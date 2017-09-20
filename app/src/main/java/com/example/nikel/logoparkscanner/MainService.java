package com.example.nikel.logoparkscanner;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by nikel on 13.09.2017.
 */

public class MainService extends Service {

    BroadcastReceiver mReceiver;
    private final String MESSAGE_TAG = "urovo.rcv.message";
    private final String MESSAGE_APP ="FromService";
    private HandlerThread mThread;
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
        /*mThread = new HandlerThread("BroadcastThread");
        mThread.start();
        Looper mLooper = mThread.getLooper();
        Handler mHandle = new Handler(mLooper);*/
        Log.d(LOG_TAG, "onCreate");
        /*mReceiver = new MyReceiver();
        registerReceiver(mReceiver, new IntentFilter(MESSAGE_TAG), null, mHandle);*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new HandlerThread("BroadcastThread");
        mThread.start();
        Looper mLooper = mThread.getLooper();
        Handler mHandle = new Handler(mLooper);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, new IntentFilter(MESSAGE_TAG), null, mHandle);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, new IntentFilter(MESSAGE_TAG));
    }*/



    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        mThread.quitSafely();
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

