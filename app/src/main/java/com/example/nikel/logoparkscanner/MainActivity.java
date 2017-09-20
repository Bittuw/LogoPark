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

public class MainActivity extends AppCompatActivity implements WebFragment.FragmentEvents {

    private DialogFragment dlg;
    private WebFragment fragment; // TODO Фрагмен веб формы
    private FragmentTransaction manager;

    private SharedPreferences mPref;
    private static final String IS_FIRST_LAUNCH = "IsFirstLaunch";

    private TextView Type, Code; //Тип и код штрих-кода
    private ProgressBar Progressbar;

    private String type, code;
    private String url = "https://lgprk.ru/api/v1/scan";
    private String LOG_TAG = "MainActivity";

    /*private static enum BarcodeTypes { // TODO
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
    }*/

   /* private BroadcastReceiver CORE_EVENT_RECEIVER = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            *//*byte[] barcode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            code = new String(barcode,0, barocodelen);
            type = BarcodeTypes.decodeType(temp).name();*//*
            type = intent.getStringExtra("Type");
            code = intent.getStringExtra("Code");

            Code.setText(code);
            Type.setText(type);
            Progressbar.setVisibility(View.VISIBLE);
            WebFragment fragment = (WebFragment) getFragmentManager().findFragmentById(R.id.contentFragment);
            manager = getFragmentManager().beginTransaction();
            manager.show(fragment).commit();
            fragment.DownloadInfo(url, code);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getPreferences(MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        startService(new Intent(this, MainService.class));
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
        Log.d(LOG_TAG, "onResume");
        Intent intent = getIntent();
        Toast.makeText(this, type, Toast.LENGTH_LONG).show();
        Type.setText(intent.getStringExtra("type"));
        Code.setText(intent.getStringExtra("code"));

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

    // События активити
    public interface ActivityEvents {
        public void Started();
    }
}
