package com.example.nikel.logoparkscanner;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.Set;

public class MainActivity extends AppCompatActivity implements AuthFragment.NoticeListener{ // TODO реализация логики в фрагментах


    private WebFragment webFragment; // TODO Фрагмен веб формы

    private AuthFragment authFragment; // TODO авторизация и мануал
    private MainFragment mainFragment;
    private Fragment mCurrentFragment;

    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private boolean isReadInstruct, isAuthorized;

    private DiaFragment manual_dlg;

    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getAppInfo();
        checkReadManual();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart(){ // Запихнуть проверка на isRead и isAuth
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
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        /*if (!mPref.contains(IS_FIRST_LAUNCH)) {

            Intent intent = getIntent();
            Toast.makeText(this, type, Toast.LENGTH_LONG).show();
            Type.setText(intent.getStringExtra("type"));
            Code.setText(intent.getStringExtra("code"));
        }*/

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
        Log.d(LOG_TAG, "onNewIntent");
        setIntent(intent);
        super.onNewIntent(intent);

    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    /*public void onDialogPositiveClick() { // Подтверждение прочтения мануала
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(IS_FIRST_LAUNCH, YES);
        editor.apply();
        *//*Intent intent = new Intent(this, MainService.class);
        intent.putExtra((new IntentParams()).getClass().getName(), IntentParams.Authorizate);
        startService(new Intent(this, MainService.class)); //Настройка первого 1 доступа*//*
    }

    public void onDialogNegativeClick() { //Без подтверждения прочтения мануала
        this.finish();
    }*/


    public View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(manual_dlg.getCheckBoolean()) {
                Toast.makeText(getApplicationContext(), "Мануал принят", Toast.LENGTH_LONG).show();
                manual_dlg.CloseDialog();

                isReadInstruct = true;

                setAppInfo();
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
    public void Authorized() {
        setAppInfo();
        makeMainFragment();
    }

    @Override
    public void NoAuthorized() {
        finish();
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

    private void setAppInfo() {
        SharedPreferences.Editor editor = mPref.edit();
        if (isReadInstruct) {
            editor.putString(Constants.IS_FIRST_LAUNCH, Constants.YES);
        }
        if (isAuthorized) {
            editor.putString(Constants.IS_AUTHARIZED, Constants.YES);
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
                .add(R.id.fragment_container, authFragment, authFragment.getClass().getName());
        manager.commit();
        mCurrentFragment = authFragment;
    }

    private void makeMainFragment() {
        mainFragment = new MainFragment();
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
