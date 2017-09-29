package com.example.nikel.logoparkscanner;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nikel.logoparkscanner.Fragments.AuthFragment;
import com.example.nikel.logoparkscanner.Fragments.DiaFragment;
import com.example.nikel.logoparkscanner.Fragments.WebFragment;

public class MainActivity extends AppCompatActivity implements AuthFragment.NoticeListener, MainInterface{ // TODO реализация логики в фрагментах


    private WebFragment webFragment; // TODO Фрагмен веб формы

    private AuthFragment authFragment; // TODO авторизация и мануал


    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private boolean isReadInstruct, isAuthorized;


    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAppInfo();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MakeFragment();
    }

    @Override
    protected void onStart(){
        super.onStart();
        findAllView();
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

    @Override
    public void onAuthPositiveClick(int TypeOfDialog) { 
        switch (TypeOfDialog) {
            case ManualDialog:
                SharedPreferences.Editor editor =mPref.edit();
                editor.putString(IS_FIRST_LAUNCH, YES);
                editor.apply();
                break;
            case ConfirmDialog:
                break;
            default:
                break;
        }
    }

    @Override
    public void onAuthNegativeClick(int TypeOfDialog) {
        switch (TypeOfDialog) {
            case ManualDialog:
                break;
            case ConfirmDialog:
                break;
            default:
                break;
        }
    }

    private void getAppInfo() { // TODO инициализация параметров из файла

        this.mPref = getSharedPreferences(MainFileInfo, MODE_PRIVATE);

        if (this.mPref.getString(IS_FIRST_LAUNCH, NO) == YES) {
            this.isReadInstruct = true;
        }
        else {
            this.isReadInstruct = false;
        }

        if (this.mPref.getString(IS_AUTHARIZED, NO) == YES) {
            this.isAuthorized = true; // Ключи и тд.
        }
        else {
            this.isAuthorized = false;
        }

    }

    private void MakeFragment() {
        if (!isReadInstruct || !isAuthorized) {
            Bundle mBundle = new Bundle();
            mBundle.putBoolean(isRead, isReadInstruct);
            mBundle.putBoolean(isAuth, isAuthorized);
            authFragment = new AuthFragment(); // Возможен перенос в функцию для автовхода
            authFragment.setArguments(mBundle);
            manager = getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, authFragment);
            manager.commit();
        }
        else {
            /*
            manager = getFragmentManager().beginTransaction()
                    .replace(R.id.contentFragment, )*/ // Создание основного фрагмента
        }
    }

    private void findAllView() { // TODO поиск всех отображаемых объектов
        /*this.Type = (TextView)findViewById(R.id.Type);
        this.Code = (TextView)findViewById(R.id.Code);
        this.Progressbar = (ProgressBar)findViewById(R.id.progressBar);


        this.fragment = (WebFragment) getFragmentManager().findFragmentById(R.id.contentFragment); // Выбор фрагмента нужно настроить
        this.manager = getFragmentManager().beginTransaction();
        this.manager.hide(fragment).commit();*/
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isAuthorized = true;
        }
    };
}
