package com.example.nikel.logoparkscanner;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private Fragment mCurrentFragment;

    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private boolean isReadInstruct, isAuthorized, isRestarting = false;

    private DiaFragment manual_dlg;

    private String user, password;
    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";


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
                /*case Constants.IntentParams.QR:
                    Bundle mBundle = new Bundle();
                    mBundle.putString("type", getIntent().getStringExtra("type"));
                    mBundle.putString("code", getIntent().getStringExtra("code"));
                    makeMainFragment(mBundle);
                    break;*/
                case Intent.ACTION_MAIN:
                    checkReadManual();
                    break;
                default:
                    Log.e(LOG_TAG, "onStart" + getIntent().getAction());
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
                    LocalBroadcastManager.getInstance(this).sendBroadcast(getIntent());
                    break;
                case Constants.IntentParams.Auth:
                    LocalBroadcastManager.getInstance(this).sendBroadcast(getIntent());
                    break;
                case Constants.IntentParams.RecData:
                    LocalBroadcastManager.getInstance(this).sendBroadcast(getIntent());
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
                Toast.makeText(getApplicationContext(), "Мануал принят", Toast.LENGTH_LONG).show();
                manual_dlg.CloseDialog();

                isReadInstruct = true;

                setAppInfo(new Bundle());
                makeAuthFragment();
            }
            else {
                Toast.makeText(getApplicationContext(), "Необходимо подтвердить прочтение интрукции", Toast.LENGTH_LONG).show();
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
        setAppInfo(mBundle);
        /*Intent mIntent = new Intent(this, MainService.class); //вызов авторизации доделать
        mIntent.setAction(Constants.IntentParams.StartRecCas);
        startService(mIntent);*/
        //makeMainFragment();
    }

    @Override
    public void NoAuthorized() {
        stopService((new Intent()).setAction(Constants.IntentParams.StopService));
        finish();
    }

    @Override
    public void StartServiceTask(Intent mIntent) {
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

        if (this.mPref.getString(Constants.IS_AUTHARIZED, Constants.NO) == Constants.YES) {
            this.isAuthorized = true; // Ключи и тд.
        }
        else {
            this.isAuthorized = false;
        }

    }

    private void setAppInfo(Bundle mBundle) {
        SharedPreferences.Editor editor = mPref.edit();
        if (isReadInstruct) {
            editor.putString(Constants.IS_FIRST_LAUNCH, Constants.YES);
        }
        if (isAuthorized) {
            editor.putString(Constants.IS_AUTHARIZED, Constants.YES);
        }
        if (mBundle.containsKey(Constants.Password)) {
            editor.putString(Constants.Password, mBundle.getString(Constants.Password));
        }
        if(mBundle.containsKey(Constants.Password_rep)) {
            editor.putString(Constants.Password, mBundle.getString(Constants.Password_rep));
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
        mCurrentFragment = mainFragment;
    }

    private void makeMainFragment(Bundle mBundle) {
        mainFragment = new MainFragment();
        mainFragment.setArguments(mBundle);
        manager = getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment, mainFragment.getClass().getName());
        manager.commit();
        mCurrentFragment = mainFragment;
    }

   /* private boolean checkIsAuthorized() {
        if (true) { // this.isAuthorized
            Intent mIntent = new Intent(this, MainService.class);
            mIntent.setAction(IntentParams.StartRecCas);
            startService(mIntent);
            return true;
        }
        else {

            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(ConfirmAuth));

            Intent mIntent = new Intent(this, MainService.class);
            mIntent.setAction(IntentParams.StartRecCas);
            startService(mIntent);

            *//*mIntent = new Intent(this, MainService.class); //вызов авторизации доделать
            mIntent.setAction(IntentParams.Auth);
            startService(mIntent);*//*
            return false;
        }
    }*/

    private void checkReadManual() {
        if (!isReadInstruct && !isAuthorized) {
            makeManualDialog();
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

}
