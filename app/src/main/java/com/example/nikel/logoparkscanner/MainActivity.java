package com.example.nikel.logoparkscanner;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.Fragments.AuthFragment;
import com.example.nikel.logoparkscanner.Fragments.DiaFragment;
import com.example.nikel.logoparkscanner.Fragments.MainFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AuthFragment.NoticeListener {

    private AuthFragment authFragment; // TODO авторизация и мануал
    private MainFragment mainFragment;

    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private boolean isReadInstruct, isAuthorized, isForegroundService, isRestarting = false;

    private DiaFragment manual_dlg;

    private String user, password;
    private static final String LOG_TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createLogFile();

        Log.d(LOG_TAG, "onCreate " + getIntent().getAction());

        getAppInfo();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent mIntent = new Intent(this, MainService.class);
        mIntent.setAction(Constants.IntentParams.isActivityAlive);
        mIntent.putExtra(Constants.IntentParams.isActivityAlive, true);
        StartServiceTask(mIntent);

        mIntent.setAction(Constants.IntentParams.StartRecCas);
        StartServiceTask(mIntent);

        if (isForegroundService) {
            mIntent = new Intent(this, MainService.class);
            mIntent.setAction(Constants.IntentParams.foregroundService);
            mIntent.putExtra(Constants.IntentParams.foregroundService, isForegroundService);
            StartServiceTask(mIntent);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart(){
        Log.d(LOG_TAG, "onStart " + getIntent().getAction());
        if (!isRestarting)
            switch (getIntent().getAction()) {
                case Intent.ACTION_MAIN:
                    checkReadAndAuth();
                    break;
                case Constants.IntentParams.QR:
                    checkReadAndAuth();
                    break;
                default:
                    Log.e(LOG_TAG, "onStart " + getIntent().getAction());
                    break;
            }

        Intent mIntent = new Intent(this, MainService.class);
        mIntent.setAction(Constants.IntentParams.isActivityStop);
        mIntent.putExtra(Constants.IntentParams.isActivityStop, false);
        StartServiceTask(mIntent);

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "Сервис в фоновом режиме").setCheckable(true);
        menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.findItem(1).setChecked(isForegroundService);
        menu.findItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent mIntent = new Intent(getApplicationContext(), MainService.class);
                mIntent.setAction(Constants.IntentParams.foregroundService);
                mIntent.putExtra(Constants.IntentParams.foregroundService, !item.isChecked());
                StartServiceTask(mIntent);

                item.setChecked(!item.isChecked());

                Bundle mBundle = new Bundle();
                mBundle.putBoolean(Constants.IntentParams.foregroundService, item.isChecked());
                setAppInfo(mBundle);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 1) {
            item.setChecked(true);
            /*Intent mIntent = new Intent(this, MainService.class);
            mIntent.setAction(Constants.IntentParams.foregroundService);
            StartServiceTask(mIntent);*/
            return true;
        }
        //return super.onOptionsItemSelected(item);
        return true;
    }


    @Override
    protected void onResume(){
        Log.d(LOG_TAG, "onResume " + getIntent().getAction() + " " + getIntent().getFlags());

        if (!getIntent().getAction().equals(Intent.ACTION_MAIN)  && !isRestarting)
            switch (getIntent().getAction()) {
                case Constants.IntentParams.QR:
                    Intent mIntent = new Intent(Constants.IntentParams.QR);
                    mIntent.putExtra("type", getIntent().getStringExtra("type"));
                    mIntent.putExtra("code", getIntent().getStringExtra("code"));
                    sendBroadcast(mIntent);
                    break;
                case Constants.IntentParams.Auth:
                    sendBroadcast(getIntent().setFlags(0));
                    break;
                case Constants.IntentParams.RecData:
                    sendBroadcast(getIntent().setFlags(0));
                    break;
                default:
                    Log.e(LOG_TAG, "onResume " + getIntent().getAction());
                    break;
            }
            isRestarting = false;

        super.onResume();
    }

    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");

        Intent mIntent = new Intent(this, MainService.class);
        mIntent.setAction(Constants.IntentParams.isActivityStop);
        mIntent.putExtra(Constants.IntentParams.isActivityStop, true);
        StartServiceTask(mIntent);

        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(LOG_TAG, "onNewIntent " + intent.getAction());
        setIntent(intent);
        super.onNewIntent(intent);

    }

    @Override
    public void setIntent(Intent newIntent) {
        Log.d(LOG_TAG, "setIntent " + newIntent.getAction());
        super.setIntent(newIntent);
    }

    @Override
    protected void onRestart() {
        Log.d(LOG_TAG, "onRestart");
        isRestarting = true;
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");

        Intent mIntent = new Intent(this, MainService.class);
        mIntent.setAction(Constants.IntentParams.isActivityAlive);
        mIntent.putExtra(Constants.IntentParams.isActivityAlive, false);
        StartServiceTask(mIntent);

        super.onDestroy();
    }

    public View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(manual_dlg.getCheckBoolean()) {
                Toast mToast = Toast.makeText(getApplicationContext(), "Инструкция прочтена", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
                manual_dlg.CloseDialog();

                isReadInstruct = true;

                setAppInfo(new Bundle());
                checkReadAndAuth();
            }
            else {
                Toast mToast = Toast.makeText(getApplicationContext(), "Необходимо подтвердить прочтение интрукции", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
        }
    };

    public View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            manual_dlg.CloseDialog();
            finish();
        }
    };

    @Override
    public void Authorized(Bundle mBundle) {
        isAuthorized = true;
        setAppInfo(mBundle);
        checkReadAndAuth();
    }

    @Override
    public void NoAuthorized() {
        setIntent(new Intent(Intent.ACTION_MAIN));
        StopServiceTask();
        finish();
    }

    @Override
    public void StartServiceTask(@Nullable Intent mIntent) {
        try {
            Log.d(LOG_TAG, "startService with: " + mIntent.getAction());
            startService(mIntent);
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "startService with: " + null, e);
            startService(new Intent(this, MainService.class));
        }
    }

    @Override
    public void StopServiceTask() {
        Log.d(LOG_TAG, "StopServiceTask");
        stopService((new Intent()).setAction(Constants.IntentParams.StopService));
    }

    private void getAppInfo() { // TODO инициализация параметров из файла
        Log.d(LOG_TAG, "getAppInfo");
        this.mPref = getSharedPreferences(Constants.MainFileInfo, MODE_PRIVATE);

        if(Constants.DebugMode) {
            SharedPreferences.Editor mEditor = mPref.edit();
            mEditor.clear();
            mEditor.apply();
        }

        if (this.mPref.getString(Constants.IS_FIRST_LAUNCH, Constants.NO).equals(Constants.YES)) {
            this.isReadInstruct = true;
        }
        else {
            this.isReadInstruct = false;
        }

        if(mPref.contains(Constants.User) && mPref.contains(Constants.Password)) {
            user = mPref.getString(Constants.User, Constants.NO);
            password = mPref.getString(Constants.Password, Constants.NO);
            isAuthorized = true;
        }
        else {
            isAuthorized = false;
        }

        if (mPref.contains(Constants.IntentParams.foregroundService)) {
            isForegroundService = mPref.getBoolean(Constants.IntentParams.foregroundService, false);
        }
        else {
            isForegroundService = false;
        }

    }

    private void setAppInfo(Bundle mBundle) {
        Log.d(LOG_TAG, "setAppInfo");
        SharedPreferences.Editor editor = mPref.edit();
        if (isReadInstruct && !mPref.contains(Constants.IS_FIRST_LAUNCH)) {
            editor.putString(Constants.IS_FIRST_LAUNCH, Constants.YES);
        }
        if (mBundle.containsKey(Constants.Password) && mBundle.containsKey(Constants.User)) {
            editor.putString(Constants.Password, mBundle.getString(Constants.Password));
            editor.putString(Constants.User, mBundle.getString(Constants.User));
            editor.putString(Constants.IS_AUTHARIZED, Constants.YES);

            user = mBundle.getString(Constants.User);
            password = mBundle.getString(Constants.Password);
        }
        if (mBundle.containsKey(Constants.IntentParams.foregroundService))
        {
            isForegroundService = mBundle.getBoolean(Constants.IntentParams.foregroundService);
            editor.putBoolean(Constants.IntentParams.foregroundService, isForegroundService);
        }
        editor.apply();
    }

    private void makeAuthFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putBoolean(Constants.isRead, isReadInstruct);
        mBundle.putBoolean(Constants.isAuth, isAuthorized);
        authFragment = new AuthFragment(); // Возможен перенос в функцию для автовхода
        authFragment.setArguments(mBundle);
        manager = getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, authFragment, authFragment.getClass().getName());
        manager.commit();
    }


    private void makeAuthFragment(Bundle mBundle) {
        authFragment = new AuthFragment();
        authFragment.setArguments(mBundle);
        manager = getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, authFragment, authFragment.getClass().getName());
        manager.commit();
    }

    private void makeMainFragment() {
        mainFragment = new MainFragment();
        manager = getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment, mainFragment.getClass().getName());
        manager.commit();
    }

    private void makeMainFragment(Bundle mBundle) {
        mainFragment = new MainFragment();
        mainFragment.setArguments(mBundle);
        manager = getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment, mainFragment.getClass().getName());
        manager.commit();
    }

    private void checkReadAndAuth() {
        if (!isReadInstruct && !isAuthorized && manual_dlg == null ) {
            makeManualDialog();
        }
        if(isReadInstruct && !isAuthorized && authFragment == null) {
            makeAuthFragment();
        }
        if (isReadInstruct && isAuthorized && mainFragment == null) {
            Bundle mBundle = new Bundle();
            mBundle.putString(Constants.User, user);
            mBundle.putString(Constants.Type, getIntent().getStringExtra("type"));
            mBundle.putString(Constants.Code, getIntent().getStringExtra("code"));
            makeMainFragment(mBundle);

            //Intent mIntent = new Intent(this, MainService.class);
            //mIntent.setAction(Constants.IntentParams.StartRecCas);
            /*mIntent.putExtra(Constants.IntentParams.foregroundService, true);*/

            //StartServiceTask(mIntent);
        }
    }

    private void makeManualDialog() {
        manual_dlg = new DiaFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt(Constants.TypeOfDialog, Constants.ManualDialog);
        manual_dlg.setArguments(mBundle);
        manual_dlg.setOnClickListener(positiveListener, negativeListener);
        manual_dlg.show(getFragmentManager(), manual_dlg.toString());
    }

    private void createLogFile() {
        if (isExternalStorageWritable()) {

            String date = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/LogoparkSanner");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, date + ".txt");

            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            if (logDirectory.listFiles().length != 0) {
                for (File file:logDirectory.listFiles()) {
                    if (file.getName().equals(date))
                        file.delete();
                }
            }

            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                Log.e(LOG_TAG, "Error with logcat", e);
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
}
