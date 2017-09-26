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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements WebFragment.FragmentEvents, DiaFragment.NoticeDialogListener, MainInterface{

    private DialogFragment dlg;

    private WebFragment fragment; // TODO Фрагмен веб формы
    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private boolean isReadInstruct, isAuthorized;

    private TextView Type, Code; //Тип и код штрих-кода
    private ProgressBar Progressbar;

    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAppInfo();
        checkReadInstruct();
        checkIsAuthorized();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        editor.putString(IS_FIRST_LAUNCH, YES);
        editor.apply();
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra((new IntentParams()).getClass().getName(), IntentParams.Authorizate);
        startService(new Intent(this, MainService.class)); //Настройка первого 1 доступа
    }

    public void onDialogNegativeClick() { //Без подтверждения прочтения мануала
        this.finish();
    }

    // События активити
    public interface ActivityEvents {
        public void Started();
    }

    private void checkReadInstruct() {
        if (!isReadInstruct) {
            this.dlg = new DiaFragment();

            Bundle mBundle = new Bundle();
            mBundle.putInt(TypeOfDialog , ManualDialog);

            this.dlg.setArguments(mBundle);
            this.dlg.show(getFragmentManager(), "dlg");
        }
    }

    private void getAppInfo() {

        this.mPref = getSharedPreferences(MainFileInfo, MODE_PRIVATE);

        if (this.mPref.getString(IS_FIRST_LAUNCH, NO) == YES) {
            this.isReadInstruct = true;
        }
        else {
            this.isReadInstruct = false;
        }

        if (this.mPref.getString(IS_AUTHARIZED, NO) == YES) {
            this.isAuthorized = true;
        }
        else {
            this.isAuthorized = false;
        }

    }

    private void findAllView() {
        this.Type = (TextView)findViewById(R.id.Type);
        this.Code = (TextView)findViewById(R.id.Code);
        this.Progressbar = (ProgressBar)findViewById(R.id.progressBar);

        this.fragment = (WebFragment) getFragmentManager().findFragmentById(R.id.contentFragment); // Выбор фрагмента нужно настроить
        this.manager = getFragmentManager().beginTransaction();
        this.manager.hide(fragment).commit();
    }

    private void checkIsAuthorized() {
        if (true) { // this.isAuthorized
            Intent mIntent = new Intent(this, MainService.class);
            mIntent.setAction(IntentParams.StartRecCas);
            startService(mIntent);
        }
        else {

            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(ConfirmAuth));

            Intent mIntent = new Intent(this, MainService.class);
            mIntent.setAction(IntentParams.StartRecCas);
            startService(mIntent);

            Bundle mBundle = new Bundle();
            mBundle.putInt(TypeOfDialog, ConfirmDialog);
            this.dlg.setArguments(mBundle);
            this.dlg = new DiaFragment();
            this.dlg.show(getFragmentManager(), "dlg");

            mIntent = new Intent(this, MainService.class); //вызов авторизации доделать
            mIntent.setAction(IntentParams.StartRecCas);
            startService(mIntent);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isAuthorized = true;
        }
    };
}
