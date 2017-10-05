package com.example.nikel.logoparkscanner.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.MainService;
import com.example.nikel.logoparkscanner.R;

/**
 * Created by nikel on 27.09.2017.
 */

public class AuthFragment extends Fragment{

    private Button Auth;
    private String type, code;
    private Activity mActivity;
    private TextView TEST;
    private NoticeListener mListener;
    private DiaFragment confirm_dlg;

    @Override
    public void setArguments(Bundle args) {

        type = args.getString("type");
        super.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mListener = (NoticeListener) mActivity;

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcasrReceiverQR, new IntentFilter(Constants.IntentParams.QR));
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcasrReceiverAuth, new IntentFilter(Constants.IntentParams.Auth));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auth, container, false);
        TEST = v.findViewById(R.id.TEST);
        Auth = v.findViewById(R.id.Autharization);
        Auth.setOnClickListener(Listener);
        return v;
    }

    public View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Auth.setClickable(false);
            confirm_dlg = new DiaFragment();
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.TypeOfDialog, Constants.ConfirmDialog);
            confirm_dlg.setArguments(mBundle);
            confirm_dlg.setOnClickListener(positiveListener, negativeListener);
            confirm_dlg.show(getFragmentManager(), confirm_dlg.toString());

            Intent mIntent = new Intent(mActivity, MainService.class);
            mIntent.setAction(Constants.IntentParams.StartRecCas);
            mListener.StartServiceTask(mIntent);
        }
    };

    public View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(type == null && code == null) {
                Toast mToast = Toast.makeText(mActivity, "Штрих-код не был просканирван", Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, 0);
                mToast.show();
            }
            else {

                Bundle mBundle = confirm_dlg.getPassword();

                if(!mBundle.isEmpty()) {
                    mBundle.putString(Constants.User, code);
                    confirm_dlg.CloseDialog();
                    mListener.Authorized(mBundle);
                }
                else {
                    Toast mToast = Toast.makeText(mActivity, "Пароли не соответствуют или поля пусты", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.BOTTOM, 0, 0);
                    mToast.show();
                }
            }
        }
    };

    public View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            confirm_dlg.CloseDialog();
            mListener.StopServiceTask();
            mListener.NoAuthorized();
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcasrReceiverQR);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcasrReceiverAuth);
        super.onDestroy();
    }

    private BroadcastReceiver mBroadcasrReceiverQR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast mToast = Toast.makeText(mActivity, "Штрих-код просканирован", Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();

            type = intent.getStringExtra("type");
            code = (intent.getStringExtra("code")).replace(":", "=");

            /* Запросы на загрузку профиля*/
            Intent mIntent = new Intent(mActivity, MainService.class);
            mIntent.setAction(Constants.IntentParams.Auth);
            mIntent.putExtra(Constants.IntentParams.URL, code);
            mListener.StartServiceTask(mIntent);
        }
    };

    private BroadcastReceiver mBroadcasrReceiverAuth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast mToast = Toast.makeText(mActivity, "Подтверждение учетной записи", Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();

            /*TEST.setText(intent.getStringExtra(Constants.IntentParams.GetData));*/
        }
    };

    public interface NoticeListener {
        public void Authorized(Bundle mBundle);
        public void NoAuthorized();
        public void StartServiceTask(Intent mIntent);
        public void StopServiceTask();
    }
}
