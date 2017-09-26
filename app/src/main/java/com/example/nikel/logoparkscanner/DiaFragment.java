package com.example.nikel.logoparkscanner;

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
import android.widget.Toast;

/**
 * Created by nikel on 19.09.2017.
 */

public class DiaFragment extends DialogFragment implements View.OnClickListener, MainInterface{

    private CheckBox mCheck;
    private TextView mTextView;
    private NoticeDialogListener mListner;
    private String LOG_TAG = "DialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        int tDialog = getArguments().getInt(TypeOfDialog);
        switch (tDialog) {
            case ManualDialog:
                View md = inflater.inflate(R.layout.manual_dialog, null);

                mTextView = md.findViewById(R.id.Text);
                mCheck = md.findViewById(R.id.CheckManual);
                mTextView.setText(R.string.manual_dialog_text);

                builder
                        .setView(md)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.exit, null);

                mListner = (NoticeDialogListener) getActivity();

                setCancelable(false);

                break;

            case ConfirmDialog:

                View cd = inflater.inflate(R.layout.auth_dialog, null);

                mTextView = cd.findViewById(R.id.Text);
                mCheck = cd.findViewById(R.id.CheckManual);
                mTextView.setText(R.string.confirm_dialog_text);
                mCheck.setVisibility(View.INVISIBLE);

                builder
                        .setView(cd)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.exit, null);

                mListner = (NoticeDialogListener) getActivity();

                setCancelable(false);

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

        AlertDialog dialog = (AlertDialog)getDialog();
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mCheck.isChecked()) {
                    Toast.makeText(getActivity(), "Мануал принят", Toast.LENGTH_LONG).show();
                    mListner.onDialogPositiveClick();
                    dismiss();
                }
                Toast.makeText(getActivity(), "Необходимо подтвердить прочтение интрукции", Toast.LENGTH_LONG).show();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListner.onDialogNegativeClick();
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface NoticeDialogListener { //отправка типа диалога
        public void onDialogPositiveClick();
        public void onDialogNegativeClick();
    }
}
