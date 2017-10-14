package com.example.nikel.logoparkscanner;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import com.example.nikel.logoparkscanner.Fragments.WebFragment;

public class MainActivity extends AppCompatActivity implements AuthFragment.NoticeListener{ // TODO реализация логики в фрагментах


    private WebFragment webFragment; // TODO Фрагмен веб формы

    private AuthFragment authFragment; // TODO авторизация и мануал
    private MainFragment mainFragment;

    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private boolean isReadInstruct, isAuthorized, isRestarting = false;

    private DiaFragment manual_dlg;

    private String user, password;
    private static final String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate " + getIntent().getAction());
        super.onCreate(savedInstanceState);

        getAppInfo();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart(){ // Под вопросом
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

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        Log.d(LOG_TAG, "onResume " + getIntent().getAction());

        if (!isRestarting)
            switch (getIntent().getAction()) {
                case Constants.IntentParams.QR:
                    sendBroadcast(getIntent());
                    break;
                case Constants.IntentParams.Auth:
                    sendBroadcast(getIntent());
                    break;
                case Constants.IntentParams.RecData:
                    sendBroadcast(getIntent());
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
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) { // TODO Получение новый данных (Если активити запущена)
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
    public void StartServiceTask(Intent mIntent) {
        Log.d(LOG_TAG, "startService with: " + mIntent.getAction());
        startService(mIntent);
    }

    @Override
    public void StopServiceTask() {
        stopService((new Intent()).setAction(Constants.IntentParams.StopService));
    }

    private void getAppInfo() { // TODO инициализация параметров из файла

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

    }

    private void setAppInfo(Bundle mBundle) {
        SharedPreferences.Editor editor = mPref.edit();
        if (isReadInstruct) {
            editor.putString(Constants.IS_FIRST_LAUNCH, Constants.YES);
        }
        if (mBundle.containsKey(Constants.Password) && mBundle.containsKey(Constants.User)) {
            editor.putString(Constants.Password, mBundle.getString(Constants.Password));
            editor.putString(Constants.User, mBundle.getString(Constants.User));
            editor.putString(Constants.IS_AUTHARIZED, Constants.YES);

            user = mBundle.getString(Constants.User);
            password = mBundle.getString(Constants.Password);
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
        if (!isReadInstruct && !isAuthorized) {
            makeManualDialog();
        }
        if(isReadInstruct && !isAuthorized) {
            makeAuthFragment();
        }
        if (isReadInstruct && isAuthorized) {
            Bundle mBundle = new Bundle();
            mBundle.putString(Constants.User, user);
            mBundle.putString(Constants.Type, getIntent().getStringExtra("type"));
            mBundle.putString(Constants.Code, getIntent().getStringExtra("code"));
            makeMainFragment(mBundle);

            Intent mIntent = new Intent(this, MainService.class);
            mIntent.setAction(Constants.IntentParams.StartRecCas);
            /*mIntent.putExtra(Constants.IntentParams.foregroundService, true);*/

            StartServiceTask(mIntent);
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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

}
