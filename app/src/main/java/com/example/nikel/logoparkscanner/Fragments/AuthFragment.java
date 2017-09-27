package com.example.nikel.logoparkscanner.Fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nikel.logoparkscanner.MainInterface;
import com.example.nikel.logoparkscanner.R;

/**
 * Created by nikel on 27.09.2017.
 */

public class AuthFragment extends Fragment implements DiaFragment.NoticeListener, MainInterface{

    private Button Auth;
    private NoticeListener mListener;
    private DialogFragment manual_dlg, confirm_dlg;

    private boolean isReadInstruct, isAuthorized;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener = (NoticeListener) getActivity();

        if(getArguments() != null) {
            isReadInstruct = getArguments().getBoolean(isRead);
            isAuthorized = getArguments().getBoolean(isAuth);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!this.isReadInstruct) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(TypeOfDialog, ManualDialog);
            manual_dlg = new DiaFragment();
            manual_dlg.setArguments(mBundle);
            manual_dlg.show(this.getChildFragmentManager(), manual_dlg.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_auth, container, false);
        Auth = v.findViewById(R.id.Autharization);
        Auth.setOnClickListener(Listener);
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    public View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            confirm_dlg = new DiaFragment();
            Bundle mBundle = new Bundle();
            mBundle.putInt(TypeOfDialog, ConfirmDialog);
            confirm_dlg.setArguments(mBundle);
            confirm_dlg.show(getFragmentManager(), confirm_dlg.toString());
        }
    };

    @Override
    public void onDialogPositiveClick(int Type) {
        switch (Type) {
            case ManualDialog:
                mListener.onAuthPositiveClick(Type);
                break;
            case ConfirmDialog:
                mListener.onAuthPositiveClick(Type);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(int Type) {
        switch (Type) {
            case ManualDialog:
                mListener.onAuthNegativeClick(Type);
                break;
            case ConfirmDialog:
                mListener.onAuthNegativeClick(Type);
                break;
            default:
                break;
        }
    }

    public interface NoticeListener {
        public void onAuthPositiveClick(int TypeOfDialog);
        public void onAuthNegativeClick(int TypeOfDialog);
    }
}
