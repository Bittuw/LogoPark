package com.example.nikel.logoparkscanner.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikel.logoparkscanner.MainInterface;
import com.example.nikel.logoparkscanner.R;

/**
 * Created by nikel on 19.09.2017.
 */

public class DiaFragment extends DialogFragment implements View.OnClickListener, MainInterface {

    private CheckBox mCheck;
    private TextView mTextView;
    private NoticeListener mListner;
    private String LOG_TAG = "DialogFragment";
    private int CurrentDialogType;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        try {
            mListner = (NoticeListener) getParentFragment();
        } catch (ClassCastException e) {
            throw  new ClassCastException("Calling");
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        CurrentDialogType = getArguments().getInt(TypeOfDialog);
        switch (CurrentDialogType) {
            case ManualDialog:
                View md = inflater.inflate(R.layout.manual_dialog, null);

                mTextView = md.findViewById(R.id.Text);
                mCheck = md.findViewById(R.id.CheckManual);
                mTextView.setText(R.string.manual_dialog_text);

                builder
                        .setView(md)
                        .setCancelable(false)
                        .setTitle(R.string.manual_dialog_title)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.exit, null);


                setCancelable(false);

                mListner = (NoticeListener) getParentFragment();

                break;

            case ConfirmDialog:

                View cd = inflater.inflate(R.layout.auth_dialog, null);

                builder
                        .setView(cd)
                        .setCancelable(false)
                        .setTitle(R.string.confirm_dialog_title)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.exit, null);


                setCancelable(false);

                mListner = (NoticeListener) getParentFragment();

                break;
            default:
                Log.e(LOG_TAG, "onCreateDialog");
                return builder.create();
        }
        return builder.create();
    }



    public void onClick(View v) {

    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog)getDialog();
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (CurrentDialogType) {
                    case ManualDialog:
                        if(mCheck.isChecked()) {
                            Toast.makeText(getActivity(), "Мануал принят", Toast.LENGTH_LONG).show();
                            mListner.onDialogPositiveClick(CurrentDialogType);
                            dismiss();
                        }
                        Toast.makeText(getActivity(), "Необходимо подтвердить прочтение интрукции", Toast.LENGTH_LONG).show();
                        break;
                    case ConfirmDialog:
                        break;
                    default:
                        Log.e(LOG_TAG, "onStart 1");
                }

            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (CurrentDialogType) {
                    case ManualDialog:
                        mListner.onDialogNegativeClick(CurrentDialogType);
                        dismiss();
                        break;
                    case ConfirmDialog:
                        mListner.onDialogNegativeClick(CurrentDialogType);
                        dismiss();
                        break;
                    default:
                        Log.e(LOG_TAG, "onStart 2");
                        break;
                }

            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface NoticeListener { // отправка типа диалога
        public void onDialogPositiveClick(int TypeOfDialog);
        public void onDialogNegativeClick(int TypeOfDialog);
    }
}
