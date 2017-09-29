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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.MainService;
import com.example.nikel.logoparkscanner.R;

/**
 * Created by nikel on 27.09.2017.
 */

public class AuthFragment extends Fragment {

    private Button Auth;
    private Activity mActivity;
    private NoticeListener mListener;
    private DiaFragment confirm_dlg;

    private boolean isReadInstruct, isAuthorized;

    @Override
    public void setArguments(Bundle args) {
        isReadInstruct = args.getBoolean(Constants.isRead);
        isAuthorized = args.getBoolean(Constants.isAuth);
        super.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mListener = (NoticeListener) mActivity;

        /*Intent mIntent = new Intent(mActivity, MainService.class); // Если делать регистрацию
        mIntent.setAction(Constants.IntentParams.StartRecCas);
        mActivity.startService(mIntent);

        mIntent.setAction(Constants.IntentParams.Auth);
        mActivity.startService(mIntent);*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auth, container, false);
        Auth = v.findViewById(R.id.Autharization);
        Auth.setOnClickListener(Listener);
        return v;
    }

    public View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            confirm_dlg = new DiaFragment();
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.TypeOfDialog, Constants.ConfirmDialog);
            confirm_dlg.setArguments(mBundle);
            confirm_dlg.setOnClickListener(positiveListener, negativeListener);
            confirm_dlg.show(getFragmentManager(), confirm_dlg.toString());

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.IntentParams.Auth));
        }
    };

    public View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            confirm_dlg.CloseDialog();
            mListener.Authorized();
        }
    };

    public View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            confirm_dlg.CloseDialog();
            mListener.NoAuthorized();
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    public interface NoticeListener {
        public void Authorized();
        public void NoAuthorized();
    }
}
