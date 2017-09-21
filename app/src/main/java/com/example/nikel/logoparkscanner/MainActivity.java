package com.example.nikel.logoparkscanner;

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

public class MainActivity extends AppCompatActivity implements WebFragment.FragmentEvents, DiaFragment.NoticeDialogListner, MainInterface{

    private DialogFragment dlg;
    private WebFragment fragment; // TODO Фрагмен веб формы
    private FragmentTransaction manager;

    private SharedPreferences mPref;

    private TextView Type, Code; //Тип и код штрих-кода
    private ProgressBar Progressbar;

    private String type, code;s
    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";


    private static class PARAMS {

        public static String NAME() {
            return PARAMS;
        }

        private String name() {
            return this.getClass().getName();
        }
        public static final int Authorizate = 1;
        public static final int ReceiveData = 2;
        public static final int StartReceiveCasts = 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref = getSharedPreferences(IS_FIRST_LAUNCH, MODE_PRIVATE);
        if (!mPref.contains(IS_FIRST)) {
            dlg = new DiaFragment();
            dlg.show(getFragmentManager(), "dlg");
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //startService(new Intent(this, MainService.class));
    }

    @Override
    protected void onStart(){
        /*Intent mIntent = this.getIntent();
        Type.setText(mIntent.getStringExtra("Type"));
        Code.setText(mIntent.getStringExtra("Code"));*/
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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!mPref.contains(IS_FIRST)) {
            Log.d(LOG_TAG, "onResume");
            Intent intent = getIntent();
            Toast.makeText(this, type, Toast.LENGTH_LONG).show();
            Type.setText(intent.getStringExtra("type"));
            Code.setText(intent.getStringExtra("code"));
        }

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
    public void onNewIntent(Intent intent) { // TODO Получение новый данных (Если активити запущена)
        Log.d(LOG_TAG, "onNewIntent");
        super.onNewIntent(intent);
        /*type = intent.getStringExtra("type");
        code = intent.getStringExtra("code");*/
        setIntent(intent);
        Log.d(LOG_TAG, "onNewIntent");
        /*Toast.makeText(this, intent.getStringExtra("Type"), Toast.LENGTH_LONG).show();
        Type.setText(intent.getStringExtra("Type"));
        Code.setText(intent.getStringExtra("Code"));*/
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

    public void onDialogPositiveClick() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(IS_FIRST, YES);
        editor.apply();
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(PARAMS.NAME(), PARAMS.Authorizate );
        startService(new Intent(this, MainService.class)); //Настройка первого 1 доступа
    }

    public void onDialogNegativeClick() {
        this.finish();
    }

    // События активити
    public interface ActivityEvents {
        public void Started();
    }
}
