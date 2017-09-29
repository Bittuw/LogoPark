package com.example.nikel.logoparkscanner.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nikel.logoparkscanner.Constants;
import com.example.nikel.logoparkscanner.R;

/**
 * Created by nikel on 19.09.2017.
 */

public class DiaFragment extends DialogFragment {

    private CheckBox mCheck;
    private TextView mTextView;
    private NoticeListener mListner;
    private String LOG_TAG = "DialogFragment";
    private int CurrentDialogType;

    public View.OnClickListener positiveListener, negativeListener;

    @Override
    public void setArguments(Bundle args) {
        CurrentDialogType = args.getInt(Constants.TypeOfDialog);
        super.setArguments(args);
    }

    public void setOnClickListener(View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        this.negativeListener = negativeListener;
        this.positiveListener = positiveListener;
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
        switch (CurrentDialogType) {
            case Constants.ManualDialog:
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

                /*mListner = (NoticeListener) getParentFragment();*/

                break;

            case Constants.ConfirmDialog:

                View cd = inflater.inflate(R.layout.auth_dialog, null);

                builder
                        .setView(cd)
                        .setCancelable(false)
                        .setTitle(R.string.confirm_dialog_title)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.exit, null);


                setCancelable(false);

                /*mListner = (NoticeListener) getParentFragment();*/

                break;
            default:
                Log.e(LOG_TAG, "onCreateDialog");
                return builder.create();
        }
        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog)getDialog();
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        positive.setOnClickListener(this.positiveListener);
        negative.setOnClickListener(this.negativeListener);
    }

    public boolean getCheckBoolean() {
        return mCheck.isChecked();
    }

    public void CloseDialog() {
        this.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface NoticeListener {
        public void onDialogPositiveClick();
        public void onDialogNegativeClick();
    }
}
