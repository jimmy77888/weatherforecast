package com.example.jimmy.weatherforecast.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.jimmy.weatherforecast.R;

import org.w3c.dom.Text;

public class ZipcodeDialogFragment extends DialogFragment {

    private OnButtonClickListener onButtonClickListener;

    public interface OnButtonClickListener {
        void onOkClick(String zipcode);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = (LayoutInflater)requireActivity().getLayoutInflater();
        View view = li.inflate(R.layout.dialogfragment_zipcode, null);
        EditText etZipcode = (EditText) view.findViewById(R.id.et_zipcode);
        AlertDialog alertDialog =  new AlertDialog.Builder(getActivity())
                .setView(view)                  // R.layout.dialogfragment_zipcode won't work since those are different references
                .setTitle(R.string.dialogfragment_zipcode_title)
                .setPositiveButton(R.string.dialogfragment_zipcode_ok, (v, i)->{
                    onButtonClickListener.onOkClick(etZipcode.getText().toString());
                })
                .setNegativeButton(R.string.dialogfragment_zipcode_cancel, (v, i)->{

                }).create();
        //alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        etZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 5) {    //TODO try .toString().matchs("\\d{5}")
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
                else {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog)getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onButtonClickListener = (OnButtonClickListener) context;
    }
}
