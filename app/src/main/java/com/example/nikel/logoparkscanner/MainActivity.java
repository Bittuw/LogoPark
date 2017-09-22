package com.example.nikel.logoparkscanner;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements WebFragment.FragmentEvents, DiaFragment.NoticeDialogListener, MainInterface{

    private DialogFragment dlg;

    private WebFragment fragment; // TODO Фрагмен веб формы
    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private TextView Type, Code; //Тип и код штрих-кода
    private ProgressBar Progressbar;

    private String type, code;
    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*mPref = getSharedPreferences(IS_FIRST_LAUNCH, MODE_PRIVATE);
        if (!mPref.contains(IS_FIRST)) {
            dlg = new DiaFragment();
            dlg.show(getFragmentManager(), "dlg");
        }*/

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart(){
        Type = (TextView)findViewById(R.id.Type);
        Code = (TextView)findViewById(R.id.Code);
        Progressbar = (ProgressBar)findViewById(R.id.progressBar);

        fragment = (WebFragment) getFragmentManager().findFragmentById(R.id.contentFragment);
        manager = getFragmentManager().beginTransaction();
        manager.hide(fragment).commit();

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
        /*if (!mPref.contains(IS_FIRST)) {

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
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void downloaded() {
        manager = getFragmentManager().beginTransaction();
        manager.show(fragment).commit();
        Progressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    public void onDialogPositiveClick() { // Подтверждение прочтения мануала
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(IS_FIRST, YES);
        editor.apply();
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra((new IntentParams()).getClass().getName(), IntentParams.Authorizate);
        startService(new Intent(this, MainService.class)); //Настройка первого 1 доступа
    }

    public void onDialogNegativeClick() { //Без подтверждения прочения мануала
        this.finish();
    }

    // События активити
    public interface ActivityEvents {
        public void Started();
    }
}
