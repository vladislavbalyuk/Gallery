package com.status.gallery;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DeleteDialog extends DialogFragment implements View.OnClickListener {

    private Settable settable;
    private String fileName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getDialog().setTitle(getActivity().getResources().getString(R.string.delete));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_delete, null);
        TextView textView = (TextView) v.findViewById(R.id.textDelete);
        Button btnYes = (Button) v.findViewById(R.id.btnYes);
        Button btnNo = (Button) v.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);

        textView.setText(getResources().getString(R.string.message_delete) + " " + fileName + "?");

        return v;
    }


    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.btnYes:
                settable.setValue(new Boolean(true));
                break;

            case R.id.btnNo:
                break;
        }
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void setSettable(Settable settable){
        this.settable = settable;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }
}