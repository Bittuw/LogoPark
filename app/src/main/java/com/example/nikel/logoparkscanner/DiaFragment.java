package com.example.nikel.logoparkscanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Created by nikel on 19.09.2017.
 */

public class DiaFragment extends DialogFragment implements View.OnClickListener {

    private Button OK_Btn, EXIT_Btn;
    private CheckBox CheckM;
    private NoticeDialogListner mListner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog, null);
        OK_Btn = v.findViewById(R.id.OK_BUTTON);
        EXIT_Btn = v.findViewById(R.id.EXIT_Button);
        CheckM = v.findViewById(R.id.CheckManual);

        builder
                .setView(v)
                .setCancelable(false)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.exit, null);

        mListner = (NoticeDialogListner) getActivity();


        return builder.create();
    }

    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.OK_BUTTON:
                if(CheckM.isChecked()) {
                    mListner.onDialogPositiveClick(this);
                    this.dismiss();
                }
                Toast.makeText(getActivity(), "Необходимо подтвердить прочтение интрукции", Toast.LENGTH_LONG).show();
                break;
            case R.id.EXIT_Button:
                mListner.onDialogNegativeClick(this);
                dismiss();
                break;
            default:
                break;
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog)getDialog();
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(CheckM.isChecked()) {
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

    public interface NoticeDialogListner {
        public void onDialogPositiveClick();
        public void onDialogNegativeClick();
    }
}
