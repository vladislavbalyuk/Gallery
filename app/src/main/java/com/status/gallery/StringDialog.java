package com.status.gallery;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class StringDialog extends DialogFragment implements View.OnClickListener {

    private Settable settable;
    private EditText editText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_string, null);

        editText = (EditText) v.findViewById(R.id.editString);

        Button btnOK = (Button) v.findViewById(R.id.btnOK);
        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);

        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return v;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnOK:
                String s = editText.getText().toString();
                settable.setValue(s.trim());
                break;

            case R.id.btnCancel:
                settable.setValue("");
                break;
        }
        dismiss();
    }

    public void setSettable(Settable settable) {
        this.settable = settable;
    }

}
